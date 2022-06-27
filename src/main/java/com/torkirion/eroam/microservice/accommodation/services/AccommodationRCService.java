package com.torkirion.eroam.microservice.accommodation.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import javax.sql.DataSource;

import org.ehcache.PersistentCacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.core.statistics.DefaultStatisticsService;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.ims.apidomain.AccommodationContent;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.controllers.AccommodationIMSRCController;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRateCheckRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.LookupTopRCByGeocoordBoxRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.OleryAccommodationData;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.FacilityGroup;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo.CodeOnly;
import com.torkirion.eroam.microservice.accommodation.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryService;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationSearchService.CacheableAccommodationResults;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.datadomain.Country;
import com.torkirion.eroam.microservice.datadomain.CountryRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationRCService
{
	@NonNull
	private AccommodationRCRepo accommodationRCRepo;

	@NonNull
	private CountryRepo countryRepo;

	@NonNull
	private OleryService oleryService;
	
	@NonNull
	private AccommodationIMSRCController accommodationIMSRCController;

	public Optional<AccommodationRC> getAccommodationRC(String code)
	{
		return getAccommodationRC("eroam", code);
	}
		
	public Optional<AccommodationRC> getAccommodationRC(String client, String code)
	{
		if ( log.isDebugEnabled())
			log.debug("getAccommodationRC::enter for code " + code);

		try
		{
			if ( code.startsWith(IMSService.CHANNEL_PREFIX))
			{
				// load directly from the client's schema
				return getIMSAccommodationRC(client, code);
			}
		}
		catch (Exception e)
		{
			log.warn("getAccommodationRC::caught exception in IMS:" + e.toString(), e);
		}
		try
		{
			AccommodationRC cachedAccommodationRC = getCache().get(code);
			if (cachedAccommodationRC != null)
				return Optional.of(cachedAccommodationRC);
		}
		catch (Exception e)
		{
			log.warn("getAccommodationRC::caught exception in cache:" + e.toString(), e);
		}

		if ( log.isDebugEnabled())
			log.debug("getAccommodationRC::not found in cache, fetching");
		Optional<AccommodationRCData> accommodationDataOpt = accommodationRCRepo.findById(code);
		if (accommodationDataOpt.isPresent())
		{
			try
			{
				AccommodationRC accommodationRC = map(accommodationDataOpt.get());
				getCache().put(accommodationRC.getCode(), accommodationRC);
				return Optional.of(accommodationRC);
			}
			catch (Exception e)
			{
				log.warn("getAccommodationRC::caught " + e.toString(), e);
				e.printStackTrace();
				return Optional.empty();
			}
		}
		else
		{
			return Optional.empty();
		}
	}

	public List<AccommodationRC> getAccommodationRCByCountryCode(String countryCode)
	{
		log.debug("getAccommodationRCByCountryCode::enter for code " + countryCode);
		List<AccommodationRCData> accommodationDataList = accommodationRCRepo.findByAddressCountryCode(countryCode);

		List<AccommodationRC> accommodationList = new ArrayList<>();
		log.debug("getAccommodationRCByCountryCode::mapping " + accommodationDataList.size() + " entries");
		for (AccommodationRCData accommodationRCData : accommodationDataList)
		{
			try
			{
				AccommodationRC accommodationRC = map(accommodationRCData);
				accommodationList.add(accommodationRC);
			}
			catch (Exception e)
			{
				log.warn("getAccommodationRCByCountryCode::caught " + e.toString(), e);
			}
		}
		return accommodationList;
	}

	public void saveAccommodationRC(AccommodationRC accommodationRC) throws Exception
	{
		getCache().put(accommodationRC.getCode(), accommodationRC);
		AccommodationRCData accommodationRCData = map(accommodationRC);
		log.debug("saveAccommodationRC::saving " + accommodationRCData);
		accommodationRCRepo.save(accommodationRCData);
	}

	public void deleteAccommodationRC(String code) throws Exception
	{
		getCache().remove(code);
		Optional<AccommodationRCData> opt = accommodationRCRepo.findById(code);
		if ( opt.isPresent())
		{
			accommodationRCRepo.deleteById(code);
		}
	}

	public List<CodeOnly> findDedupedCodesFromCode(String code)
	{
		return accommodationRCRepo.findDedupedCodesFromCode(code);
	}

	public List<AccommodationRCData> findByGeobox(BigDecimal latitudeNW, BigDecimal longitudeNW, BigDecimal latitudeSE, BigDecimal longitudeSE)
	{
		return accommodationRCRepo.findByGeobox(latitudeNW, longitudeNW, latitudeSE, longitudeSE);
	}

	public AccommodationRC map(AccommodationRCData accommodationRCData) throws Exception
	{
		AccommodationRC accommodationRC = new AccommodationRC();
		BeanUtils.copyProperties(accommodationRCData, accommodationRC, "images");
		AccommodationRC.Address address = new AccommodationRC.Address();
		accommodationRC.setAddress(address);
		if (accommodationRCData.getAddress() != null)
		{
			BeanUtils.copyProperties(accommodationRCData.getAddress(), address, "geoCoordinates");
			AccommodationRC.GeoCoordinates geoCoordinates = new AccommodationRC.GeoCoordinates();
			if (accommodationRCData.getAddress().getGeoCoordinates() != null)
				BeanUtils.copyProperties(accommodationRCData.getAddress().getGeoCoordinates(), geoCoordinates);
			Optional<Country> countryOpt = countryRepo.findById(accommodationRCData.getAddress().getCountryCode());
			if (countryOpt.isPresent())
			{
				address.setCountryName(countryOpt.get().getCountryName());
			}
			address.setGeoCoordinates(geoCoordinates);
		}
		List<String> errata = getObjectMapper().readValue(accommodationRCData.getErrataJson(), new TypeReference<List<String>>()
		{});
		accommodationRC.setErrata(errata);
		List<FacilityGroup> facilityGroups = getObjectMapper().readValue(accommodationRCData.getFacilityGroupsJson(), new TypeReference<List<FacilityGroup>>()
		{});
		accommodationRC.setFacilityGroups(facilityGroups);
		List<Image> images = getObjectMapper().readValue(accommodationRCData.getImagesJson(), new TypeReference<List<Image>>()
		{});
		accommodationRC.getImages().addAll(images);
		if (accommodationRCData.getImageThumbnail() != null)
		{
			Image imageThumbnail = getObjectMapper().readValue(accommodationRCData.getImageThumbnail(), Image.class);
			// temporary fix to make Hotelbeds thumbnails better quality
			if (imageThumbnail != null)
			{
				if (imageThumbnail.getImageURL().contains("/giata/small/"))
					imageThumbnail.setImageURL(imageThumbnail.getImageURL().replace("/giata/small/", "/giata/"));
				accommodationRC.setImageThumbnail(imageThumbnail);
			}
		}
		return accommodationRC;
	}

	private AccommodationRCData map(AccommodationRC accommodationRC) throws Exception
	{
		AccommodationRCData accommodationRCData = new AccommodationRCData();
		BeanUtils.copyProperties(accommodationRC, accommodationRCData, "images", "address");
		AccommodationRCData.Address addressData = new AccommodationRCData.Address();
		if (accommodationRC.getAddress() != null)
			BeanUtils.copyProperties(accommodationRC.getAddress(), addressData, "geoCoordinates");
		accommodationRCData.setAddress(addressData);
		AccommodationRCData.GeoCoordinates geoCoordinates = new AccommodationRCData.GeoCoordinates();
		if (accommodationRC.getAddress() != null && accommodationRC.getAddress().getGeoCoordinates() != null)
			BeanUtils.copyProperties(accommodationRC.getAddress().getGeoCoordinates(), geoCoordinates);
		addressData.setGeoCoordinates(geoCoordinates);
		String facilityGroupsJson = getObjectMapper().writeValueAsString(accommodationRC.getFacilityGroups());
		accommodationRCData.setFacilityGroupsJson(facilityGroupsJson);
		String errataJson = getObjectMapper().writeValueAsString(accommodationRC.getErrata());
		accommodationRCData.setErrataJson(errataJson);
		String imagesJson = getObjectMapper().writeValueAsString(accommodationRC.getImages());
		accommodationRCData.setImagesJson(imagesJson);
		String imageThumbnail = getObjectMapper().writeValueAsString(accommodationRC.getImageThumbnail());
		accommodationRCData.setImageThumbnail(imageThumbnail);
		log.debug("map::mapped " + accommodationRC + " to " + accommodationRCData);
		return accommodationRCData;
	}

	protected Optional<AccommodationRC> getIMSAccommodationRC(String client, String code) throws Exception
	{
		log.debug("getIMSAccommodationRC::loading " + code+ " for " + client);
		// we do this to make sure we have the right client schema
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<AccommodationRC> callableTask = () -> {
		    return getIMSAccommodationRCThreaded(client, code);
		};
		Future<AccommodationRC> future = threadPoolExecutor.submit(callableTask);
		AccommodationRC result = future.get(30, TimeUnit.SECONDS);
		return Optional.of(result);
	}

	protected AccommodationRC getIMSAccommodationRCThreaded(String client, String code)
	{
		log.debug("getIMSAccommodationRCThreaded::loading " + code);
		
		AccommodationRC imsRCData = accommodationIMSRCController.getRC(client, code.substring(IMSService.CHANNEL_PREFIX.length()));
		if ( imsRCData != null && imsRCData != null  )
		{
			return imsRCData;
		}
		log.debug("getIMSAccommodationRCThreaded::returning null");
		return null;
	}

	private final ObjectMapper getObjectMapper()
	{
		if (_objectMapper == null)
		{
			_objectMapper = new ObjectMapper();
			_objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return _objectMapper;
	}

	private final org.ehcache.Cache<String, AccommodationRC> getCache() throws Exception
	{
		if (_cache == null)
		{
			Path tempDirWithPrefix = Files.createTempDirectory("accommodationRC");
			log.debug("getCache::tempDirWithPrefix=" + tempDirWithPrefix);

			_statisticsService = new DefaultStatisticsService();
			PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(CacheManagerBuilder.persistence(tempDirWithPrefix.toFile())).using(_statisticsService)
					.build(true);
			org.ehcache.Cache<String, AccommodationRC> ehCache = persistentCacheManager.createCache("accommodationRC",
					CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, AccommodationRC.class, ResourcePoolsBuilder.heap(1000)));
			_cache = ehCache;

		}
		return _cache;
	}

	public void logStats()
	{
		if (_statisticsService != null)
		{
			CacheStatistics ehCacheStat = _statisticsService.getCacheStatistics("accommodationRC");
			log.info("logStats::accommodationRC hit%=" + ehCacheStat.getCacheHitPercentage() + ", gets=" + ehCacheStat.getCacheGets() + ", evictions/expirations/removals=" + ehCacheStat.getCacheEvictions() + "/" + ehCacheStat.getCacheExpirations() + "/" + ehCacheStat.getCacheRemovals() + ", heap count=" + ehCacheStat.getTierStatistics().get("OnHeap").getMappings());
		}
	}

	private StatisticsService _statisticsService = null;

	private org.ehcache.Cache<String, AccommodationRC> _cache = null;

	private ObjectMapper _objectMapper = null;

}
