package com.torkirion.eroam.microservice.hirecars.services;

import com.torkirion.eroam.microservice.hirecars.apidomain.*;
import com.torkirion.eroam.microservice.hirecars.datadomain.CarSearchEntryRCRepo;
import com.torkirion.eroam.microservice.hirecars.dto.DetailRQDTO;
import com.torkirion.eroam.microservice.hirecars.dto.HireCarSearchRQDTO;
import com.torkirion.eroam.microservice.hirecars.endpoint.HireCarServiceIF;
import com.torkirion.eroam.microservice.hirecars.endpoint.carnect.CarNectService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class HireCarSearchService
{
	private static final int SERVICE_TIMEOUT = 30;

	@NonNull
	private SystemPropertiesDAO propertiesDAO;

	@NonNull
	private HireCarChannelService channelService;


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

	public static class CacheableHireCarResults implements Serializable
	{
		private static final long serialVersionUID = 1L;

		Collection<HireCarResult> result;
	}

	public Collection<HireCarResult> search(HireCarSearchRQDTO availSearchRQ) throws Exception
	{
		log.debug("search::enter");

		long timer1 = System.currentTimeMillis();

		log.debug("search::checking cache for " + availSearchRQ);
		CacheableHireCarResults cacheableResults = getCache().get(availSearchRQ);
		if (cacheableResults != null)
		{
			log.debug("search::returning cached result");
			return cacheableResults.result;
		}
		ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		Collection<Future<Collection<HireCarResult>>> futures = new ArrayList<Future<Collection<HireCarResult>>>();
		if (useCarNect(availSearchRQ))
		{
			HireCarServiceIF carNectService = channelService.getHireCarServiceIF(CarNectService.CHANNEL);
			HireCarCallableService callableService = new HireCarCallableService(carNectService, availSearchRQ);
			Future<Collection<HireCarResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("search::adding carnect future " + f);
		}

		Collection<HireCarResult> allResults = new ArrayList<>();
		for (Future<Collection<HireCarResult>> f : futures)
		{
			try
			{
				log.debug("search::waiting on future " + f);
				Collection<HireCarResult> results = f.get(SERVICE_TIMEOUT, TimeUnit.SECONDS);
				if (results != null)
					allResults.addAll(results);
			}
			catch (Exception e)
			{
				log.warn("search::search call threw exception " + e.toString(), e);
			}
		}

		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));

		cacheableResults = new CacheableHireCarResults();
		cacheableResults.result = allResults;
		log.debug("searchActivities::putting cache for " + availSearchRQ);
		 getCache().put(availSearchRQ, cacheableResults);

		log.debug("search::returning " + allResults.size() + " results");
		return allResults;
	}

	public HireCarDetailResult getDetail(DetailRQDTO detailRQDTO) throws Exception
	{
		log.debug("getDetail::enter");

		HireCarServiceIF hireCarService = null;
		hireCarService = channelService.getHireCarServiceIF(detailRQDTO.getChannel());
		return hireCarService.getDetail(detailRQDTO);
	}

	public HireCarBookRS book(String client, HireCarBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");
		validateBook(client, bookRQ);
		HireCarServiceIF activityService = channelService.getHireCarServiceIF(bookRQ.getChannel());
		return activityService.book(client, bookRQ);
	}


	public HireCarCancelRS cancel(String client, HireCarCancelRQ cancelRQ) throws Exception{
		validateCancel(cancelRQ);
		HireCarServiceIF activityService = channelService.getHireCarServiceIF(cancelRQ.getChannel());
		return activityService.cancel(client, cancelRQ);
	}

	private void validateCancel(HireCarCancelRQ cancelRQ) throws Exception{
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
		if(cancelRQ.getBooker() == null || "".equals(cancelRQ.getBooker().getSurname())) {
			throw new Exception("Missing input surname");
		}
	}

	private void validateBook(String site, HireCarBookRQ bookRQ) throws Exception
	{
		log.debug("validateBook::enter");
		// ensure all items are for the same channel!
		if(StringUtils.isBlank(site)) {
			throw new Exception("Missing input client");
		}
		if(bookRQ == null) {
			throw new Exception("Missing input data");
		}
		return;
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

	private boolean useCarNect(HireCarSearchRQDTO searchRQ)
	{
		log.debug("useCarNect::enter for client " + searchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(searchRQ.getClient(), CarNectService.CHANNEL, "enabled", false);
		log.debug("useCarNect::enabled=" + enabled);
		if (!enabled)
			return false;
		if (searchRQ.getChannel() != null && !searchRQ.getChannel().equals(CarNectService.CHANNEL))
			return false;
		return true;
	}

	private final Cache<HireCarSearchRQDTO, CacheableHireCarResults> getCache()
	{
		if (_cache == null)
		{
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			MutableConfiguration<HireCarSearchRQDTO, CacheableHireCarResults> configuration = new MutableConfiguration<HireCarSearchRQDTO, CacheableHireCarResults>()
					.setTypes(HireCarSearchRQDTO.class, CacheableHireCarResults.class).setStoreByValue(false)
					.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(javax.cache.expiry.Duration.FIVE_MINUTES));
			// TODO keep this cache on disk!
			_cache = cacheManager.createCache("hirecarSearch", configuration);
		}
		return _cache;
	}

	private static Cache<HireCarSearchRQDTO, CacheableHireCarResults> _cache = null;
	private static final String LANGUAGE_DEFAULT = "en";

}
