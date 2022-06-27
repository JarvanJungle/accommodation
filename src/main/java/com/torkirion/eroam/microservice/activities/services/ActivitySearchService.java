package com.torkirion.eroam.microservice.activities.services;

import com.torkirion.eroam.microservice.activities.apidomain.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.activities.dto.AvailSearchByActivityIdRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.activities.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;
import com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.activities.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.ViatorV2Service;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivitySearchService
{
	private static final int SERVICE_TIMEOUT = 30;

	@NonNull
	private SystemPropertiesDAO propertiesDAO;

	@NonNull
	private ActivityChannelService channelService;

	public void clearSearchCache()
	{
		try
		{
			getCache().clear();
		}
		catch (Exception e)
		{

		}
	}

	public static class CacheableActivityResults implements Serializable
	{
		private static final long serialVersionUID = 1L;

		Collection<ActivityResult> result;
	}

	public Collection<ActivityResult> searchActivities(AvailSearchRQDTO availSearchRQ) throws Exception
	{
		log.debug("searchActivities::enter");

		long timer1 = System.currentTimeMillis();

		log.debug("searchActivities::checking cache for " + availSearchRQ);
		CacheableActivityResults cacheableActivityResults = getCache().get(availSearchRQ);
		if (cacheableActivityResults != null)
		{
			log.debug("searchTransfers::returning cached result");
			return cacheableActivityResults.result;
		}

		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(availSearchRQ.getClient(), propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		Collection<Future<Collection<ActivityResult>>> futures = new ArrayList<Future<Collection<ActivityResult>>>();
		if (useHotelbeds(availSearchRQ))
		{
			ActivityServiceIF hotelbedsService = channelService.getActivityServiceIF(HotelbedsService.CHANNEL);
			ActivityCallableService callableService = new ActivityCallableService(hotelbedsService, availSearchRQ);
			Future<Collection<ActivityResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("searchActivities::adding hotelbedsService future " + f);
		}
		if (useLocalIMS(availSearchRQ))
		{
			ActivityServiceIF imsService = channelService.getActivityServiceIF(IMSService.CHANNEL);
			ActivityCallableService callableService = new ActivityCallableService(imsService, availSearchRQ);
			Future<Collection<ActivityResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("searchActivities::adding ims future " + f);
		}
		if (useViatorV2(availSearchRQ))
		{
			ActivityServiceIF viatorV2Service = channelService.getActivityServiceIF(ViatorV2Service.CHANNEL);
			ActivityCallableService callableService = new ActivityCallableService(viatorV2Service, availSearchRQ);
			Future<Collection<ActivityResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("searchActivities::adding viatorv2 future " + f);
		}

		Collection<ActivityResult> allResults = new ArrayList<>();
		for (Future<Collection<ActivityResult>> f : futures)
		{
			try
			{
				log.debug("searchActivities::waiting on future " + f);
				Collection<ActivityResult> results = f.get(SERVICE_TIMEOUT, TimeUnit.SECONDS);
				if (results != null)
					allResults.addAll(results);
			}
			catch (Exception e)
			{
				log.warn("searchActivities::search call threw exception " + e.toString(), e);
			}
		}
		threadPoolExecutor.shutdown();

		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));

		cacheableActivityResults = new CacheableActivityResults();
		cacheableActivityResults.result = allResults;
		log.debug("searchActivities::putting cache for " + availSearchRQ);
		getCache().put(availSearchRQ, cacheableActivityResults);

		log.debug("searchActivities::returning " + allResults.size() + " results");
		return allResults;
	}

	public ActivityResult rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		log.debug("rateCheck::enter");
		validateRateCheck(rateCheckRQDTO);
		ActivityServiceIF activityService = null;
		activityService = channelService.getActivityServiceIF(rateCheckRQDTO.getChannel());
		ActivityResult activityResult = activityService.rateCheck(rateCheckRQDTO); 
		log.debug("rateCheck::return " + activityResult);
		return activityResult;
	}

	private void validateRateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception {
		if(rateCheckRQDTO == null) {
			throw new Exception("Missing input");
		}
		if("".equals(rateCheckRQDTO.getChannel())) {
			throw new Exception("Missing input channel");
		}
		if(rateCheckRQDTO.getActivityDate() == null || "".equals(rateCheckRQDTO.getActivityDate())) {
			throw new Exception("Missing input activityDate");
		}
		if(rateCheckRQDTO.getActivityId() == null || "".equals(rateCheckRQDTO.getActivityId())) {
			throw new Exception("Missing input activityId");
		}
		if(rateCheckRQDTO.getDepartureId() == null || "".equals(rateCheckRQDTO.getDepartureId())) {
			throw new Exception("Missing input departureId");
		}
		if(rateCheckRQDTO.getOptionId() == null || "".equals(rateCheckRQDTO.getOptionId())) {
			throw new Exception("Missing input optionId");
		}

	}

	public ActivityBookRS book(String client, ActivityBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");

		validateBook(client, bookRQ);

		String channel = null;
		for (ActivityBookRQ.ActivityRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
			// the front end often duplicates answers if the passenger is not specified.  Remove duplicates
			if ( item.getBookingQuestionAnswers() != null )
			{
				item.setBookingQuestionAnswers(new ArrayList<>(new HashSet<>(item.getBookingQuestionAnswers())));
			}
		}

		ActivityServiceIF activityService = channelService.getActivityServiceIF(channel);
		return activityService.book(client, bookRQ);
	}

	public ActivityCancelRS cancel(String client, ActivityCancelRQ cancelRQ) throws Exception{
		validateCancel(cancelRQ);
		ActivityServiceIF activityService = channelService.getActivityServiceIF(cancelRQ.getChannel());
		return activityService.cancel(client, cancelRQ);
	}

	private void validateCancel(ActivityCancelRQ cancelRQ) throws Exception{
		if(cancelRQ == null) {
			throw new Exception("Missing input");
		}
		if("".equals(cancelRQ.getChannel())) {
			throw new Exception("Missing input channel");
		}
		if("".equals(cancelRQ.getBookingReference())) {
			throw new Exception("Missing input bookingReference");
		}
		if("".equals(cancelRQ.getInternalBookingReference())) {
			throw new Exception("Missing input internalBookingReference");
		}
	}

	private void validateBook(String site, ActivityBookRQ bookRQ) throws Exception
	{
		log.debug("validateBook::enter");
		// ensure all items are for the same channel!
		if(StringUtils.isBlank(site)) {
			throw new Exception("Missing input client");
		}
		if(bookRQ == null) {
			throw new Exception("Missing input data");
		}
		ActivityBookRQ.ActivityBooker booker = bookRQ.getBooker();
		if(booker == null || StringUtils.isBlank(booker.getGivenName()) || StringUtils.isBlank(booker.getSurname())
				|| StringUtils.isBlank(booker.getTitle())) {
			throw new Exception("Missing input booker");
		}
		if(CollectionUtils.isEmpty(bookRQ.getItems())) {
			throw new Exception("Missing input items");
		}
		if(StringUtils.isBlank(bookRQ.getInternalBookingReference())) {
			throw new Exception("Missing input internalBookingReference");
		}
		if(CollectionUtils.isEmpty(bookRQ.getTravellers())) {
			throw new Exception("Missing input travellers");
		}
		String channel = null;
		for (ActivityBookRQ.ActivityRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
			if(StringUtils.isBlank(item.getInternalItemReference())) {
				throw new Exception("all internalItemReference must not empty");
			}
			if(StringUtils.isBlank(item.getActivityId())) {
				throw new Exception("all activityId must not empty");
			}
			if(StringUtils.isBlank(item.getOptionId())) {
				throw new Exception("all optionId must not empty");
			}
			if(item.getDate() == null ) {
				throw new Exception("all date must not empty");
			}

		}

		for (Traveller t : bookRQ.getTravellers())
		{
			if (t.getGivenName() == null && t.getGivenName().length() == 0 || t.getSurname() == null || t.getSurname().length() == 0)
			{
				throw new Exception("Traveller first names and surnames must all be complete");
			}
			if(t.getBirthDate() == null) {
				throw new Exception("BirthDate must be complete");
			}
		}
	}

	/*
	 * public CancelRS cancel(String site, CancelRQ cancelRQ) throws Exception { log.debug("cancel::enter");
	 * 
	 * TransferServiceIF transferService = null; transferService = channelService.getTransferService(site); return
	 * transferService.cancel(site, cancelRQ); }
	 */
	/*
	 * public RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) throws Exception { log.debug("retrieve::enter");
	 * 
	 * TransferServiceIF transferService = null; transferService = channelService.getTransferService(retrieveRQ.getChannel());
	 * return transferService.retrieve(site, retrieveRQ); }
	 */

	private boolean useHotelbeds(AvailSearchRQDTO searchRQ)
	{
		log.debug("useHotelbeds::enter for client " + searchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(searchRQ.getClient(), HotelbedsService.CHANNEL, "enabled", false);
		log.debug("useHotelbeds::enabled=" + enabled);
		if (!enabled)
			return false;
		if (searchRQ.getChannel() != null && !searchRQ.getChannel().equals(HotelbedsService.CHANNEL))
			return false;
		return true;
	}

	private boolean useLocalIMS(AvailSearchRQDTO searchRQ)
	{
		log.debug("useLocalIMS::enter for client " + searchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(searchRQ.getClient(), IMSService.CHANNEL, "enabled", false);
		log.debug("useLocalIMS::enabled=" + enabled);
		if (!enabled)
			return false;

		if (searchRQ.getChannel() != null && !searchRQ.getChannel().equals(IMSService.CHANNEL))
			return false;
		if (searchRQ instanceof AvailSearchByActivityIdRQDTO)
		{
			AvailSearchByActivityIdRQDTO availSearchByActivityIdRQDTO = (AvailSearchByActivityIdRQDTO) searchRQ;
			boolean hasThisChannel = false;
			if (availSearchByActivityIdRQDTO.getActivityIds() != null)
			{
				for (String activityId : availSearchByActivityIdRQDTO.getActivityIds())
				{
					log.debug("useLocalIMS::check " + activityId);
					if (activityId.startsWith(IMSService.CHANNEL_PREFIX))
						hasThisChannel = true;
				}
			}
			if (!hasThisChannel)
				return false;
		}

		return true;
	}

	private boolean useViatorV2(AvailSearchRQDTO searchRQ)
	{
		log.debug("useViatorV2::enter for client " + searchRQ.getClient());

		if (searchRQ.getChannel() != null && !searchRQ.getChannel().equals(ViatorV2Service.CHANNEL))
			return false;
		Boolean enabled = propertiesDAO.getProperty(searchRQ.getClient(), ViatorV2Service.CHANNEL, "enabled", false);
		log.debug("useViatorV2::enabled=" + enabled);
		if (!enabled)
			return false;
		if (searchRQ instanceof AvailSearchByActivityIdRQDTO)
		{
			AvailSearchByActivityIdRQDTO availSearchByActivityIdRQDTO = (AvailSearchByActivityIdRQDTO) searchRQ;
			boolean hasThisChannel = false;
			for (String activityId : availSearchByActivityIdRQDTO.getActivityIds())
			{
				log.debug("useViatorV2::check " + activityId);
				if (activityId.startsWith(ViatorV2Service.CHANNEL_PREFIX))
					hasThisChannel = true;
			}
			if (!hasThisChannel)
				return false;
		}
		return true;
	}

	private final Cache<AvailSearchRQDTO, CacheableActivityResults> getCache()
	{
		if (_cache == null)
		{
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			MutableConfiguration<AvailSearchRQDTO, CacheableActivityResults> configuration = new MutableConfiguration<AvailSearchRQDTO, CacheableActivityResults>()
					.setTypes(AvailSearchRQDTO.class, CacheableActivityResults.class).setStoreByValue(false)
					.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(javax.cache.expiry.Duration.FIVE_MINUTES));
			// TODO keep this cache on disk!
			_cache = cacheManager.createCache("activitySearch", configuration);
		}
		return _cache;
	}

	private static Cache<AvailSearchRQDTO, CacheableActivityResults> _cache = null;
	private static final String LANGUAGE_DEFAULT = "en";

}
