package com.torkirion.eroam.microservice.activities.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityRC;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityRC.Image;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCData;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityRCService
{
	@NonNull
	private ActivityRCRepo activityRCRepo;

	//@NonNull
	//private CountryRepo countryRepo;

	public Optional<ActivityRC> getActivityRC(String code)
	{
		log.debug("getActivityRC::enter for code " + code);

		ActivityRC cachedActivityRC = getCache().get(code);
		if (cachedActivityRC != null)
			return Optional.of(cachedActivityRC);

		log.debug("getActivityRC::not found in cache, fetching");
		Optional<ActivityRCData> activityRCDataOpt = activityRCRepo.findById(code);
		if (activityRCDataOpt.isPresent())
		{
			try
			{
				ActivityRC activityRC = map(activityRCDataOpt.get());
				getCache().put(activityRC.getCode(), activityRC);
				return Optional.of(activityRC);
			}
			catch (Exception e)
			{
				log.warn("getActivityRC::caught " + e.toString(), e);
				e.printStackTrace();
				return Optional.empty();
			}
		}
		else
		{
			return Optional.empty();
		}
	}

	public void saveActivityRC(ActivityRC activityRC) throws Exception
	{
		getCache().put(activityRC.getCode(), activityRC);
		ActivityRCData activityRCData = map(activityRC);
		log.debug("saveactivityRC::saving " + activityRCData);
		activityRCRepo.save(activityRCData);
	}

	private ActivityRC map(ActivityRCData activityRCData) throws Exception
	{
		ActivityRC activityRC = new ActivityRC();
		BeanUtils.copyProperties(activityRCData, activityRC, "images");
		List<Image> images = getObjectMapper().readValue(activityRCData.getImagesJson(), new TypeReference<List<Image>>()
		{});
		activityRC.getImages().addAll(images);

		return activityRC;
	}

	private ActivityRCData map(ActivityRC activityRC) throws Exception
	{
		ActivityRCData activityRCData = new ActivityRCData();
		BeanUtils.copyProperties(activityRC, activityRCData, "images");
		String imagesJson = getObjectMapper().writeValueAsString(activityRC.getImages());
		activityRCData.setImagesJson(imagesJson);

		return activityRCData;
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

	private final Cache<String, ActivityRC> getCache()
	{
		if (_cache == null)
		{
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			MutableConfiguration<String, ActivityRC> configuration = new MutableConfiguration<String, ActivityRC>().setTypes(String.class, ActivityRC.class);
			_cache = cacheManager.createCache("activityRC", configuration);
		}
		return _cache;
	}

	private ObjectMapper _objectMapper = null;

	private Cache<String, ActivityRC> _cache = null;
}
