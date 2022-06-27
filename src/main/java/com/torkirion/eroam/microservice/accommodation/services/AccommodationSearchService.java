package com.torkirion.eroam.microservice.accommodation.services;

import com.torkirion.eroam.microservice.accommodation.endpoint.youtravel.YoutravelService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import javax.sql.DataSource;

import org.ehcache.PersistentCacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.core.statistics.DefaultStatisticsService;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationResult;
import com.torkirion.eroam.microservice.accommodation.apidomain.AvailSearchByHotelIdRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.LookupTopRCByGeocoordBoxRQ;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationProperty;
import com.torkirion.eroam.microservice.accommodation.apidomain.OleryAccommodationData;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomResult;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRateCheckRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRetrieveRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRetrieveRS;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo.CodeOnly;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ.AccommodationRequestItem;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.accommodation.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.InnstantService;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryService;
import com.torkirion.eroam.microservice.accommodation.endpoint.sabrecsl.SabreCSLService;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoService;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.cache.FileCache;
import com.torkirion.eroam.microservice.config.TenantContext;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationSearchService
{
	private static final int SERVICE_TIMEOUT = 600;

	@NonNull
	private SystemPropertiesDAO propertiesDAO;

	@NonNull
	private AccommodationChannelService channelService;

	@NonNull
	private AccommodationRCService accommodationRCService;

	@NonNull
	private OleryService oleryService;

	public static class CacheableAccommodationResults implements Serializable
	{
		private static final long serialVersionUID = 1L;

		List<AccommodationResult> result;
	}

	public void clearSearchCache()
	{
		try
		{
			getCache().clear();
			channelService.clearChannelCaches();
		}
		catch (Exception e)
		{

		}
	}

	public List<AccommodationResult> searchHotels(AvailSearchRQDTO availSearchRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("searchHotels::enter");

		long timer1 = System.currentTimeMillis();

		if (log.isDebugEnabled())
			log.debug("searchHotels::checking cache for " + availSearchRQ);
		CacheableAccommodationResults cacheableAccommodationResults = getCache().get(availSearchRQ.toString());
		if (cacheableAccommodationResults != null)
		{
			if (log.isDebugEnabled())
				log.debug("searchHotels::returning cached result");
			return cacheableAccommodationResults.result;
		}

		// if we have asked for hotels, but NOT a channel, then find ALL deduped hotels
		if (availSearchRQ instanceof AvailSearchByHotelIdRQDTO)
		{
			availSearchRQ = expandDuppedHotelIds((AvailSearchByHotelIdRQDTO) availSearchRQ);
		}

		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(availSearchRQ.getClient(), propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		Set<Future<List<AccommodationResult>>> futures = new HashSet<Future<List<AccommodationResult>>>();
		if (useYoutravel(availSearchRQ))
		{
			AccommodationServiceIF youtravelService = channelService.getAccommodationService(YoutravelService.CHANNEL);
			AccommodationCallableService callableService = new AccommodationCallableService(youtravelService, availSearchRQ);
			Future<List<AccommodationResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			if (log.isDebugEnabled())
				log.debug("searchHotels::adding yalagoService future " + f);
		}
		if (useYalago(availSearchRQ))
		{
			AccommodationServiceIF yalagoService = channelService.getAccommodationService(YalagoService.CHANNEL);
			AccommodationCallableService callableService = new AccommodationCallableService(yalagoService, availSearchRQ);
			Future<List<AccommodationResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			if (log.isDebugEnabled())
				log.debug("searchHotels::adding yalagoService future " + f);
		}
		if (useHotelbeds(availSearchRQ))
		{
			AccommodationServiceIF hotelbedsService = channelService.getAccommodationService(HotelbedsService.CHANNEL);
			AccommodationCallableService callableService = new AccommodationCallableService(hotelbedsService, availSearchRQ);
			Future<List<AccommodationResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			if (log.isDebugEnabled())
				log.debug("searchHotels::adding hotelbedsService future " + f);
		}
		if (useInnstant(availSearchRQ))
		{
			AccommodationServiceIF innstantService = channelService.getAccommodationService(InnstantService.CHANNEL);
			AccommodationCallableService callableService = new AccommodationCallableService(innstantService, availSearchRQ);
			Future<List<AccommodationResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			if (log.isDebugEnabled())
				log.debug("searchHotels::adding innstantService future " + f);
		}
		if (useLocalIMS(availSearchRQ))
		{
			AccommodationServiceIF imsService = channelService.getAccommodationService(IMSService.CHANNEL);
			AccommodationCallableService callableService = new AccommodationCallableService(imsService, availSearchRQ);
			Future<List<AccommodationResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			if (log.isDebugEnabled())
				log.debug("searchHotels::adding imsService future " + f);
		}
		if (useSabreCSL(availSearchRQ))
		{
			AccommodationServiceIF sabreCSLService = channelService.getAccommodationService(SabreCSLService.CHANNEL);
			AccommodationCallableService callableService = new AccommodationCallableService(sabreCSLService, availSearchRQ);
			Future<List<AccommodationResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			if (log.isDebugEnabled())
				log.debug("searchHotels::adding sabreCSLService future " + f);
		}

		List<AccommodationResult> allResults = new ArrayList<>();
		if (log.isDebugEnabled())
			log.debug("searchHotels::waiting on " + futures.size() + " futures");
		int futureCount = 0;
		for (Future<List<AccommodationResult>> f : futures)
		{
			try
			{
				if (log.isDebugEnabled())
					log.debug("searchHotels::waiting on future " + f);
				List<AccommodationResult> results = f.get(SERVICE_TIMEOUT, TimeUnit.SECONDS);
				if (results != null)
					allResults.addAll(results);
				if (log.isDebugEnabled())
					log.debug("searchHotels::loaded " + futureCount++ + "th future with " +  (results == null ? 0 : results.size()) + " results");
			}
			catch (Exception e)
			{
				log.warn("searchHotels::search call threw exception " + e.toString(), e);
			}
		}
		threadPoolExecutor.shutdown();

		// add master accommodation details
		markupAccommodationProperty(allResults, availSearchRQ.getCountryCodeOfOrigin());

		// merge results
		List<AccommodationResult> mergedResults = mergeResults(allResults, false, availSearchRQ);

		if (propertiesDAO.getProperty(null, null, "limitHotelResults", 100000) < 10000 && mergedResults.size() > propertiesDAO.getProperty(null, null, "limitHotelResults", 100000))
		{
			if (log.isDebugEnabled())
				log.debug("searchHotels::trimming results from " + mergedResults.size() + " to " + propertiesDAO.getProperty(null, null, "limitHotelResults", 100000));
			mergedResults = new ArrayList<AccommodationResult>(mergedResults.subList(0, propertiesDAO.getProperty(null, null, "limitHotelResults", 100000)));
		}

		// sort by distance from centrepoint
		if ( availSearchRQ instanceof AvailSearchByGeocordBoxRQDTO)
		{
			long timerSort = System.currentTimeMillis();
			mergedResults = sortByHasIMSAndDistance(mergedResults, availSearchRQ);
			log.info("SORT::" + (System.currentTimeMillis() - timerSort));
			log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));
		}

		cacheableAccommodationResults = new CacheableAccommodationResults();
		cacheableAccommodationResults.result = mergedResults;
		if (log.isDebugEnabled())
			log.debug("searchHotels::putting cache for " + availSearchRQ);
		getCache().put(availSearchRQ.toString(), cacheableAccommodationResults);
		CacheableAccommodationResults test = getCache().get(availSearchRQ.toString());
		if (test == null)
		{
			if (log.isDebugEnabled())
				log.debug("searchHotels::could not save cache!!");
		}
		else
		{
			if (log.isDebugEnabled())
				log.debug("searchHotels::cache OK!!");
		}

		if (log.isDebugEnabled())
			log.debug("searchHotels::returning " + mergedResults.size() + " results");
		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));
		return mergedResults;
	}

	public AccommodationRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		log.debug("rateCheck::enter");

		AccommodationServiceIF accommodationService = null;
		accommodationService = channelService.getAccommodationService(rateCheckRQDTO.getChannel());
		AccommodationRateCheckRS accommodationRateCheckRS = accommodationService.rateCheck(rateCheckRQDTO);  
		if ( rateCheckRQDTO.getBookingCodes().size() != accommodationRateCheckRS.getRooms().size())
		{
			log.warn("rateCheck::requested number of rooms " + rateCheckRQDTO.getBookingCodes().size() + " does not equal returned number of rooms " + accommodationRateCheckRS.getRooms().size());
			throw new Exception("Returned room count not equal requested room count");
		}
		return accommodationRateCheckRS;
	}

	public AccommodationBookRS book(String client, AccommodationBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");

		validateBook(client, bookRQ);

		String channel = null;
		for (AccommodationBookRQ.AccommodationRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
		}
		AccommodationServiceIF accommodationService = null;
		accommodationService = channelService.getAccommodationService(channel);
		AccommodationBookRS bookRS = accommodationService.book(client, bookRQ);
		getCache().clear();
		return bookRS;
	}

	private void validateBook(String site, AccommodationBookRQ bookRQ) throws Exception
	{
		log.debug("validateBook::enter");
		// ensure all items are for the same channel!
		String channel = null;
		for (AccommodationBookRQ.AccommodationRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
		}

		for (AccommodationRequestItem item : bookRQ.getItems())
		{
			if (item.getTravellerIndex().size() > bookRQ.getTravellers().size())
			{
				throw new Exception("travellerIndex size must equal travellers size");
			}
		}
		for (Traveller t : bookRQ.getTravellers())
		{
			if (t.getGivenName() == null && t.getGivenName().length() == 0 || t.getSurname() == null || t.getSurname().length() == 0)
			{
				throw new Exception("Traveller first names and surnames must all be complete");
			}
		}
	}

	public AccommodationCancelRS cancel(String site, AccommodationCancelRQ cancelRQ) throws Exception
	{
		log.debug("cancel::enter");

		AccommodationServiceIF accommodationService = null;
		accommodationService = channelService.getAccommodationService(cancelRQ.getChannel());
		return accommodationService.cancel(site, cancelRQ);
	}

	public AccommodationRetrieveRS retrieve(String site, AccommodationRetrieveRQ retrieveRQ) throws Exception
	{
		log.debug("retrieve::enter");

		AccommodationServiceIF accommodationService = null;
		accommodationService = channelService.getAccommodationService(retrieveRQ.getChannel());
		return accommodationService.retrieve(site, retrieveRQ);
	}

	public static final class OlerySorter implements Comparator<AccommodationResult>
	{
		@Override
		public int compare(AccommodationResult o1, AccommodationResult o2)
		{
			if (o1 == null)
				return -1;
			if (o2 == null)
				return +1;
			OleryAccommodationData ol1 = o1.getProperty().getOleryData();
			OleryAccommodationData ol2 = o2.getProperty().getOleryData();
			int h1 = o1.getProperty().getCode().hashCode();
			int h2 = o2.getProperty().getCode().hashCode();
			BigDecimal rating1 = (h1 > 0 ? BigDecimal.valueOf(h1 * -1) : BigDecimal.valueOf(h1));
			BigDecimal rating2 = (h2 > 0 ? BigDecimal.valueOf(h2 * -1) : BigDecimal.valueOf(h2));
			if (ol1 != null && ol1.getRating() != null)
			{
				rating1 = ol1.getRating();
			}
			if (ol2 != null && ol2.getRating() != null)
			{
				rating2 = ol2.getRating();
			}
			if (rating1.compareTo(rating2) == 0)
				return o1.getProperty().getCode().compareTo(o2.getProperty().getCode());
			else
				return rating2.compareTo(rating1);
		}
	}

	public List<AccommodationResult> getTopRichContentForGeoBox(LookupTopRCByGeocoordBoxRQ lookupTopRCByGrocoordBoxRQ, String client)
	{
		log.debug("getTopRichContentForGeoBox::enter for lookupTopRCByGrocoordBoxRQ " + lookupTopRCByGrocoordBoxRQ);
		List<AccommodationRCData> accommodationDataList = accommodationRCService.findByGeobox(lookupTopRCByGrocoordBoxRQ.getNorthwest().getLatitude(),
				lookupTopRCByGrocoordBoxRQ.getNorthwest().getLongitude(), lookupTopRCByGrocoordBoxRQ.getSoutheast().getLatitude(), lookupTopRCByGrocoordBoxRQ.getSoutheast().getLongitude());

		List<AccommodationResult> allResults = new ArrayList<>();

		log.debug("getTopRichContentForGeoBox::mapping " + accommodationDataList.size() + " entries");
		Set<String> availableChannels = propertiesDAO.getSiteChannelList(client, ProductType.ACCOMMODATION);
		for (AccommodationRCData accommodationRCData : accommodationDataList)
		{
			try
			{
				if (!availableChannels.contains(accommodationRCData.getChannel()))
				{
					log.debug("getTopRichContentForGeoBox::channel  + " + accommodationRCData.getChannel() + " not suitable for " + client);
					continue;
				}
				AccommodationResult accommodationResult = new AccommodationResult();
				accommodationResult.setProperty(new AccommodationProperty());
				accommodationResult.getProperty().setCode(accommodationRCData.getCode());
				accommodationResult.getProperty().setChannelCode(accommodationRCData.getChannelCode());
				AccommodationRC accommodationRC = accommodationRCService.map(accommodationRCData);
				accommodationResult.getProperty().setAccommodationName(accommodationRC.getAccommodationName());
				accommodationResult.getProperty().setAddress(accommodationRC.getAddress());
				if (accommodationRC.getImageThumbnail() == null)
				{
					log.debug("getTopRichContentForGeoBox::property " + accommodationRCData.getCode() + " has no image");
					continue;
				}
				accommodationResult.getProperty().setImageThumbnailUrl(accommodationRC.getImageThumbnail().getImageURL());
				accommodationResult.getProperty().setIntroduction(accommodationRC.getIntroduction());
				accommodationResult.getProperty().setRating(accommodationRC.getRating());
				accommodationResult.getProperty().setRatingText(accommodationRC.getRatingText());
				accommodationResult.getProperty().setChannel(accommodationRC.getChannel());
				accommodationResult.getProperty().setOleryCompanyCode(accommodationRC.getOleryCompanyCode());

				if (accommodationRC.getOleryCompanyCode() != null && accommodationRC.getOleryCompanyCode() > 0)
				{
					OleryAccommodationData oleryAccommodationData = oleryService.getAccommodationOleryData(accommodationRC.getOleryCompanyCode(), "GB");
					accommodationResult.getProperty().setOleryData(oleryAccommodationData);
					log.debug("getTopRichContentForGeoBox::adding olery hotel " + accommodationResult.getProperty().getCode() + " " + accommodationResult.getProperty().getAccommodationName() + " rating " + accommodationResult.getProperty().getOleryData().getRating());
					allResults.add(accommodationResult);
				}
				else
				{
					// TODO until we redo Olery!
					log.info("getTopRichContentForGeoBox::adding in null Olery data for now, just to test results");
					allResults.add(accommodationResult);
				}
			}
			catch (Exception e)
			{
				log.warn("getAccommodationRCByCountryCode::caught " + e.toString(), e);
			}
		}
		log.debug("getTopRichContentForGeoBox::loaded " + allResults.size() + " entries before merge");
		allResults = mergeResults(allResults, true, null);
		log.debug("getTopRichContentForGeoBox::loaded " + allResults.size() + " entries after merge");
		SortedSet<AccommodationResult> sortedResults = new TreeSet<>(new OlerySorter());
		sortedResults.addAll(allResults);
		allResults = new ArrayList<>(sortedResults);
		if (lookupTopRCByGrocoordBoxRQ.getResultsLimit().intValue() < allResults.size())
		{
			log.debug("getTopRichContentForGeoBox::truncating results");
			return allResults.subList(0, lookupTopRCByGrocoordBoxRQ.getResultsLimit());
		}
		else
		{
			log.debug("getTopRichContentForGeoBox::returning all results");
			return allResults;
		}
	}

	protected void markupAccommodationProperty(Collection<AccommodationResult> accommodationResults, String countryCodeOfOrigin)
	{
		log.debug("markupAccommodationProperty::enter");

		for (AccommodationResult accommodationResult : accommodationResults)
		{
			Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(accommodationResult.getProperty().getCode());
			if (accommodationRCOpt.isPresent())
			{
				AccommodationRC accommodationRC = accommodationRCOpt.get();
				accommodationResult.getProperty().setAccommodationName(accommodationRC.getAccommodationName());
				accommodationResult.getProperty().setAddress(accommodationRC.getAddress());
				if (accommodationRC.getImageThumbnail() != null)
					accommodationResult.getProperty().setImageThumbnailUrl(accommodationRC.getImageThumbnail().getImageURL());
				accommodationResult.getProperty().setIntroduction(accommodationRC.getIntroduction());
				accommodationResult.getProperty().setRating(accommodationRC.getRating());
				accommodationResult.getProperty().setRatingText(accommodationRC.getRatingText());
				accommodationResult.getProperty().setOleryCompanyCode(accommodationRC.getOleryCompanyCode());

				if (accommodationRC.getOleryCompanyCode() != null && accommodationRC.getOleryCompanyCode() != 0)
				{
					OleryAccommodationData oleryAccommodationData = oleryService.getAccommodationOleryData(accommodationRC.getOleryCompanyCode(), countryCodeOfOrigin);
					accommodationResult.getProperty().setOleryData(oleryAccommodationData);
				}
			}
		}
	}

	/**
	 * Merge properties based on Same olery mapping code
	 * 
	 * @param accommodationResults
	 */
	private List<AccommodationResult> mergeResults(List<AccommodationResult> accommodationResults, boolean allowEmptyRooms, AvailSearchRQDTO availSearchRQ)
	{
		log.debug("mergeResults::enter");

		List<AccommodationResult> mergedResults = new ArrayList<>();
		Map<Long, AccommodationResult> mappedResults = new HashMap<>();
		int mergeCount = 0;
		for (AccommodationResult accommodationResult : accommodationResults)
		{
			if ( availSearchRQ != null && availSearchRQ instanceof AvailSearchByGeocordBoxRQDTO)
			{
				AvailSearchByGeocordBoxRQDTO availSearchByGeocordBoxRQDTO = (AvailSearchByGeocordBoxRQDTO)availSearchRQ;
				if ( !withinBounds(availSearchByGeocordBoxRQDTO, accommodationResult.getProperty()))
				{
					if (log.isDebugEnabled())
						log.debug("mergeResults::hotel " + accommodationResult.getProperty().getCode() + " not added, outside bounds");
					continue;
				}
			}
			log.debug("mergeResults::read " + accommodationResult.getProperty().getCode() + " Olery:" + accommodationResult.getProperty().getOleryCompanyCode());
			if (accommodationResult.getProperty().getOleryCompanyCode() == null || accommodationResult.getProperty().getOleryCompanyCode() < 1)
			{
				// no Olery mapping
				log.debug("mergeResults::no Olery code");
				mergedResults.add(accommodationResult);
			}
			else
			{
				AccommodationResult testMapped = mappedResults.get(accommodationResult.getProperty().getOleryCompanyCode());
				if (testMapped == null)
				{
					log.debug("mergeResults::first code");
					mappedResults.put(accommodationResult.getProperty().getOleryCompanyCode(), accommodationResult);
				}
				else
				{
					mergeCount++;
					// merge the rooms
					log.debug("mergeResults::copying rooms " + accommodationResult.getProperty().getCode() + "(" + accommodationResult.getRooms().size() + " rooms) to "
							+ testMapped.getProperty().getCode() + " (" + testMapped.getRooms().size() + " rooms)");
					testMapped.getRooms().addAll(accommodationResult.getRooms());
					// replace the RC ?
					if (channelService.getChannelRCRank(accommodationResult.getProperty().getChannel()) > channelService.getChannelRCRank(testMapped.getProperty().getChannel()))
					{
						log.debug("mergeResults::replacing RC as well");
						testMapped.setProperty(accommodationResult.getProperty());
					}
				}
			}
		}
		log.debug("mergeResults::merging " + mappedResults.size() + " merged hotels into " + mergedResults.size() + " unmapped hotels with " + mergeCount + " real merges");
		mergedResults.addAll(mappedResults.values());
		List<AccommodationResult> finalResults = new ArrayList<>();
		for (AccommodationResult accommodationResult : mergedResults) {
			if (!allowEmptyRooms && (accommodationResult.getRooms() == null || accommodationResult.getRooms().size() == 0)){
				log.debug("mergeResults::property " + accommodationResult.getProperty().getCode() + " " + accommodationResult.getProperty().getAccommodationName() + " has no rooms, bypassing");
				continue;
			}
			finalResults.add(accommodationResult);
		}
		return finalResults;
	}

	private boolean useYalago(AvailSearchRQDTO availSearchRQ)
	{
		log.debug("useYalago::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), YalagoService.CHANNEL, "enabled", false);
		log.debug("useYalago::enabled=" + enabled);
		if (!enabled)
			return false;
		if (availSearchRQ.getChannel() != null && !availSearchRQ.getChannel().equals(YalagoService.CHANNEL))
			return false;
		if (availSearchRQ.getChannelExceptions() != null && availSearchRQ.getChannelExceptions().contains(YalagoService.CHANNEL))
			return false;
		if (availSearchRQ instanceof AvailSearchByHotelIdRQDTO)
		{
			AvailSearchByHotelIdRQDTO availSearchByHotelIdRQDTO = (AvailSearchByHotelIdRQDTO) availSearchRQ;
			boolean hasThisChannel = false;
			for (String hotelId : availSearchByHotelIdRQDTO.getHotelIds())
			{
				if (YalagoService.CHANNEL.equals(AccommodationChannelService.getChannelForHotelId(hotelId)))
					hasThisChannel = true;
			}
			if (!hasThisChannel)
				return false;
		}
		return true;
	}

	private boolean useHotelbeds(AvailSearchRQDTO availSearchRQ)
	{
		log.debug("useHotelbeds::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), HotelbedsService.CHANNEL, "enabled", false);
		log.debug("useHotelbeds::enabled=" + enabled);
		if (!enabled)
			return false;
		if (availSearchRQ.getChannel() != null && !availSearchRQ.getChannel().equals(HotelbedsService.CHANNEL))
			return false;
		if (availSearchRQ.getChannelExceptions() != null && availSearchRQ.getChannelExceptions().contains(HotelbedsService.CHANNEL))
			return false;
		if (availSearchRQ instanceof AvailSearchByHotelIdRQDTO)
		{
			AvailSearchByHotelIdRQDTO availSearchByHotelIdRQDTO = (AvailSearchByHotelIdRQDTO) availSearchRQ;
			boolean hasThisChannel = false;
			for (String hotelId : availSearchByHotelIdRQDTO.getHotelIds())
			{
				log.debug("useHotelbeds::check " + hotelId + " prefixChannel=" + AccommodationChannelService.getChannelForHotelId(hotelId));
				if (HotelbedsService.CHANNEL.equals(AccommodationChannelService.getChannelForHotelId(hotelId)))
					hasThisChannel = true;
			}
			if (!hasThisChannel)
				return false;
		}
		return true;
	}
	private boolean useYoutravel(AvailSearchRQDTO availSearchRQ)
	{
		log.debug("useHotelbeds::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), YoutravelService.CHANNEL, "enabled", false);
		log.debug("useHotelbeds::enabled=" + enabled);
		if (!enabled)
			return false;
		if (availSearchRQ.getChannel() != null && !availSearchRQ.getChannel().equals(YoutravelService.CHANNEL))
			return false;
		if (availSearchRQ.getChannelExceptions() != null && availSearchRQ.getChannelExceptions().contains(YoutravelService.CHANNEL))
			return false;
		if (availSearchRQ instanceof AvailSearchByHotelIdRQDTO)
		{
			AvailSearchByHotelIdRQDTO availSearchByHotelIdRQDTO = (AvailSearchByHotelIdRQDTO) availSearchRQ;
			boolean hasThisChannel = false;
			for (String hotelId : availSearchByHotelIdRQDTO.getHotelIds())
			{
				log.debug("useYoutravel::check " + hotelId + " prefixChannel=" + AccommodationChannelService.getChannelForHotelId(hotelId));
				if (YoutravelService.CHANNEL.equals(AccommodationChannelService.getChannelForHotelId(hotelId)))
					hasThisChannel = true;
			}
			if (!hasThisChannel)
				return false;
		}
		return true;
	}


	private boolean useInnstant(AvailSearchRQDTO availSearchRQ)
	{
		log.debug("useInnstant::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), InnstantService.CHANNEL, "enabled", false);
		log.debug("useInnstant::enabled=" + enabled);
		if (!enabled)
			return false;
		if (availSearchRQ.getChannel() != null && !availSearchRQ.getChannel().equals(InnstantService.CHANNEL))
			return false;
		if (availSearchRQ.getChannelExceptions() != null && availSearchRQ.getChannelExceptions().contains(InnstantService.CHANNEL))
			return false;
		if (availSearchRQ instanceof AvailSearchByHotelIdRQDTO)
		{
			AvailSearchByHotelIdRQDTO availSearchByHotelIdRQDTO = (AvailSearchByHotelIdRQDTO) availSearchRQ;
			boolean hasThisChannel = false;
			for (String hotelId : availSearchByHotelIdRQDTO.getHotelIds())
			{
				log.debug("useInnstant::check " + hotelId + " prefixChannel=" + AccommodationChannelService.getChannelForHotelId(hotelId));
				if (InnstantService.CHANNEL.equals(AccommodationChannelService.getChannelForHotelId(hotelId)))
					hasThisChannel = true;
			}
			if (!hasThisChannel)
				return false;
		}
		return true;
	}

	private boolean useLocalIMS(AvailSearchRQDTO availSearchRQ)
	{
		log.debug("useLocalIMS::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), IMSService.CHANNEL, "enabled", false);
		log.debug("useLocalIMS::enabled=" + enabled);
		if (!enabled)
			return false;
		if (availSearchRQ.getChannel() != null && !availSearchRQ.getChannel().equals(IMSService.CHANNEL))
			return false;
		if (availSearchRQ.getChannelExceptions() != null && availSearchRQ.getChannelExceptions().contains(IMSService.CHANNEL))
			return false;
		if (availSearchRQ instanceof AvailSearchByHotelIdRQDTO)
		{
			AvailSearchByHotelIdRQDTO availSearchByHotelIdRQDTO = (AvailSearchByHotelIdRQDTO) availSearchRQ;
			boolean hasThisChannel = false;
			for (String hotelId : availSearchByHotelIdRQDTO.getHotelIds())
			{
				log.debug("useLocalIMS::check " + hotelId + " prefixChannel=" + AccommodationChannelService.getChannelForHotelId(hotelId));
				if (IMSService.CHANNEL.equals(AccommodationChannelService.getChannelForHotelId(hotelId)))
					hasThisChannel = true;
			}
			if (!hasThisChannel)
				return false;
		}
		return true;
	}

	private boolean useSabreCSL(AvailSearchRQDTO availSearchRQ)
	{
		log.debug("useSabreCSL::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), SabreCSLService.CHANNEL, "enabled", false);
		log.debug("useSabreCSL::enabled=" + enabled);
		if (!enabled)
			return false;
		if (availSearchRQ.getChannel() != null && !availSearchRQ.getChannel().equals(SabreCSLService.CHANNEL))
			return false;
		if (availSearchRQ.getChannelExceptions() != null && availSearchRQ.getChannelExceptions().contains(SabreCSLService.CHANNEL))
			return false;
//		if (availSearchRQ instanceof AvailSearchByHotelIdRQDTO)
//		{
//			AvailSearchByHotelIdRQDTO availSearchByHotelIdRQDTO = (AvailSearchByHotelIdRQDTO) availSearchRQ;
//			boolean hasThisChannel = false;
//			for (String hotelId : availSearchByHotelIdRQDTO.getHotelIds())
//			{
//				log.debug("useSabreCSL::check " + hotelId + " prefixChannel=" + channelService.getChannelForHotelId(hotelId));
//				if (SabreCSLService.CHANNEL.equals(channelService.getChannelForHotelId(hotelId)))
//					hasThisChannel = true;
//			}
//			if (!hasThisChannel)
//				return false;
//		}
		return true;
	}

	private AvailSearchByHotelIdRQDTO expandDuppedHotelIds(AvailSearchByHotelIdRQDTO availSearchByHotelIdRQDTO)
	{
		log.debug("expandDuppedHotelIds::enter for " + availSearchByHotelIdRQDTO.getHotelIds());
		Set<String> allHotelIDs = new HashSet<>();
		for (String hotelId : availSearchByHotelIdRQDTO.getHotelIds())
		{
			allHotelIDs.add(hotelId);
			List<CodeOnly> codes = accommodationRCService.findDedupedCodesFromCode(hotelId);
			for (CodeOnly c : codes)
			{
				log.debug("expandDuppedHotelIds::transformed hotelId into " + c.getCode());
				allHotelIDs.add(c.getCode());
			}
		}
		availSearchByHotelIdRQDTO.setHotelIds(allHotelIDs);
		return availSearchByHotelIdRQDTO;
	}

	private static class SortResultByHasIMSAndDistance implements Comparator<AccommodationResult>
	{
		@Override
		public int compare(AccommodationResult r1, AccommodationResult r2)
		{
			if ( r1.getHasIMS().equals(r2.getHasIMS()))
			{
				if ( r1.getDistancefromCentrepoint().compareTo(r2.getDistancefromCentrepoint()) == 0)
					if ( r1.getProperty().getOleryData() != null && r1.getProperty().getOleryData().getRating() != null && r2.getProperty().getOleryData() != null && r2.getProperty().getOleryData().getRating() != null && r1.getProperty().getOleryData().getRating().compareTo(r2.getProperty().getOleryData().getRating()) != 0)
						return r1.getProperty().getOleryData().getRating().compareTo(r2.getProperty().getOleryData().getRating());
					else
						return r1.getProperty().getAccommodationName().compareTo(r2.getProperty().getAccommodationName());
				else
					return r1.getDistancefromCentrepoint().compareTo(r2.getDistancefromCentrepoint());
			}
			else
			{
				return r1.getHasIMS() ? -1 : 1;
			}
		}
	}
	private List<AccommodationResult> sortByHasIMSAndDistance(List<AccommodationResult> unsorted, AvailSearchRQDTO availSearchRQ)
	{
		log.debug("sortByHasIMSAndDistance::enter");
		AvailSearchByGeocordBoxRQDTO availSearchByGeocordBoxRQDTO = (AvailSearchByGeocordBoxRQDTO)availSearchRQ;
		LatitudeLongitude centre = availSearchByGeocordBoxRQDTO.getDistanceCentrepoint();
		if ( centre == null || (centre.getLatitude().compareTo(BigDecimal.ZERO) == 0 && centre.getLongitude().compareTo(BigDecimal.ZERO) == 0))
		{
			centre = Functions.center(availSearchByGeocordBoxRQDTO.getNorthwest(), availSearchByGeocordBoxRQDTO.getSoutheast());
		}
		SortedSet<AccommodationResult> sorted = new TreeSet<>(new SortResultByHasIMSAndDistance());
		try
		{
		for ( AccommodationResult result : unsorted )
		{
			if ( result.getProperty().getAccommodationName() == null )
			{
				log.debug("sortByHasIMSAndDistance::bypassing " + result.getProperty().getCode() + " as name is null");
				continue;
			}
			result.setHasIMS(false);
			for ( RoomResult room : result.getRooms())
			{
				if ( room.getChannel().equals(IMSService.CHANNEL))
				{
					result.setHasIMS(true);
					break;
				}
			}
			if ( result.getProperty().getAddress() == null || result.getProperty().getAddress().getGeoCoordinates() == null || result.getProperty().getAddress().getGeoCoordinates().getLatitude() == null || result.getProperty().getAddress().getGeoCoordinates().getLongitude() == null ||
					(result.getProperty().getAddress().getGeoCoordinates().getLatitude().compareTo(BigDecimal.ZERO) == 0 && result.getProperty().getAddress().getGeoCoordinates().getLongitude().compareTo(BigDecimal.ZERO) == 0))
			{
				result.setDistancefromCentrepoint(BigDecimal.valueOf(10000));
				log.debug("sortByHasIMSAndDistance::bad co-ordinates for " + result.getProperty().getAccommodationName());
			}
			else
			{
				result.setDistancefromCentrepoint(Functions.distance2(centre, result.getProperty().getAddress().getGeoCoordinates()));
			}
			log.debug("sortByHasIMSAndDistance::adding " + result.getProperty().getAccommodationName() + " {" + result.getProperty().getCode() + "} distance " + result.getDistancefromCentrepoint() + " between " + result.getProperty().getAddress().getGeoCoordinates() + " and centre " + centre + " and hasIMS " + result.getHasIMS());
			if ( availSearchByGeocordBoxRQDTO.getKilometerFilter() != null && availSearchByGeocordBoxRQDTO.getKilometerFilter().compareTo(BigInteger.ZERO) != 0)
			{
				if ( result.getDistancefromCentrepoint().intValue() > availSearchByGeocordBoxRQDTO.getKilometerFilter().intValue())
				{
					log.debug("sortByHasIMSAndDistance::bypassing " + result.getProperty().getCode() + " as distance " + result.getDistancefromCentrepoint() +  " > limit " + availSearchByGeocordBoxRQDTO.getKilometerFilter());
					continue;
				}
			}
			sorted.add(result);
		}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return List.copyOf(sorted);
	}

	private boolean withinBounds(AvailSearchByGeocordBoxRQDTO availSearchByGeocordBoxRQDTO, AccommodationProperty accommodationProperty)
	{
		if ( accommodationProperty.getAddress() == null || accommodationProperty.getAddress().getGeoCoordinates() == null || accommodationProperty.getAddress().getGeoCoordinates().getLatitude() == null ||accommodationProperty.getAddress().getGeoCoordinates().getLongitude() == null )
		{
			if (log.isDebugEnabled())
				log.debug("withinBounds::rejecting " + accommodationProperty.getCode() + " as latlong is null");
			return false;
		}
		if ( accommodationProperty.getAddress().getGeoCoordinates().getLatitude().doubleValue() < availSearchByGeocordBoxRQDTO.getSoutheast().getLatitude().doubleValue())
		{
			if (log.isDebugEnabled())
				log.debug("withinBounds::rejecting " + accommodationProperty.getCode() + " as latlong is too south");
			return false;
		}
		if ( accommodationProperty.getAddress().getGeoCoordinates().getLatitude().doubleValue() > availSearchByGeocordBoxRQDTO.getNorthwest().getLatitude().doubleValue())
		{
			if (log.isDebugEnabled())
				log.debug("withinBounds::rejecting " + accommodationProperty.getCode() + " as latlong is too north");
			return false;
		}
		if ( accommodationProperty.getAddress().getGeoCoordinates().getLongitude().doubleValue() < availSearchByGeocordBoxRQDTO.getNorthwest().getLongitude().doubleValue())
		{
			if (log.isDebugEnabled())
				log.debug("withinBounds::rejecting " + accommodationProperty.getCode() + " as latlong is too west");
			return false;
		}
		if ( accommodationProperty.getAddress().getGeoCoordinates().getLongitude().doubleValue() > availSearchByGeocordBoxRQDTO.getSoutheast().getLongitude().doubleValue())
		{
			if (log.isDebugEnabled())
				log.debug("withinBounds::rejecting " + accommodationProperty.getCode() + " as latlong is too east");
			return false;
		}
		return true;
	}

	/**
	 * We could use the actual AvailSearchRQDTO to be the key, but we have troubles hashing and 'equals' down through all the
	 * structures. Easier to use the String representation as the key
	 * 
	 * @return
	 */
	private final org.ehcache.Cache<String, CacheableAccommodationResults> getCache() throws Exception
	{
		if (_cache == null)
		{
			Path tempDirWithPrefix = Files.createTempDirectory("accommodationSearch");
			log.debug("getCache::tempDirWithPrefix=" + tempDirWithPrefix);

			_statisticsService = new DefaultStatisticsService();
			PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(CacheManagerBuilder.persistence(tempDirWithPrefix.toFile())).using(_statisticsService)
					.build(true);

			org.ehcache.Cache<String, CacheableAccommodationResults> ehCache = persistentCacheManager.createCache("accommodationSearch",
					CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, CacheableAccommodationResults.class, ResourcePoolsBuilder.heap(5).disk(1000, MemoryUnit.MB)));
			_cache = ehCache;
		}

		return _cache;
	}

	public void logStats()
	{
		if (_statisticsService != null)
		{
			try
			{ // just to be sure the cache is loaded on first run!
				getCache();
			}
			catch (Exception e)
			{

			}
			CacheStatistics ehCacheStat = _statisticsService.getCacheStatistics("accommodationSearch");
			log.info("logStats::accommodationSearch hit%=" + ehCacheStat.getCacheHitPercentage() + ", gets=" + ehCacheStat.getCacheGets() + ", evictions/expirations/removals="
					+ ehCacheStat.getCacheEvictions() + "/" + ehCacheStat.getCacheExpirations() + "/" + ehCacheStat.getCacheRemovals() + ", heap count="
					+ ehCacheStat.getTierStatistics().get("OnHeap").getMappings());
		}
	}

	private StatisticsService _statisticsService = null;

	private static org.ehcache.Cache<String, CacheableAccommodationResults> _cache = null;

}
