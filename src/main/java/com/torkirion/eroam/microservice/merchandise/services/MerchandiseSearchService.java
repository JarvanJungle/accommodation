package com.torkirion.eroam.microservice.merchandise.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseBookRQ;
import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseBookRS;
import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseResult;
import com.torkirion.eroam.microservice.merchandise.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.merchandise.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.merchandise.endpoint.MerchandiseServiceIF;
import com.torkirion.eroam.microservice.merchandise.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchandiseSearchService
{
	private static final int SERVICE_TIMEOUT = 30;

	@NonNull
	private SystemPropertiesDAO propertiesDAO;

	@NonNull
	private MerchandiseChannelService channelService;

	public static class CacheableMerchandiseResults implements Serializable
	{
		private static final long serialVersionUID = 1L;

		Collection<MerchandiseResult> result;
	}

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

	public Collection<MerchandiseResult> searchMerchandise(AvailSearchRQDTO availSearchRQ) throws Exception
	{
		log.debug("searchMerchandise::enter");

		long timer1 = System.currentTimeMillis();

		log.debug("searchMerchandise::checking cache for " + availSearchRQ);
		CacheableMerchandiseResults cacheableMerchandiseResults = getCache().get(availSearchRQ);
		if (cacheableMerchandiseResults != null)
		{
			log.debug("searchMerchandise::returning cached result");
			return cacheableMerchandiseResults.result;
		}

		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(availSearchRQ.getClient(), propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		Collection<Future<Collection<MerchandiseResult>>> futures = new ArrayList<Future<Collection<MerchandiseResult>>>();
		if (useLocalIMS(availSearchRQ))
		{
			MerchandiseServiceIF imsService = channelService.getMerchandiseServiceIF(IMSService.CHANNEL);
			MerchandiseCallableService callableService = new MerchandiseCallableService(imsService, availSearchRQ);
			Future<Collection<MerchandiseResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("searchMerchandise::adding ims future " + f);
		}

		Collection<MerchandiseResult> allResults = new ArrayList<>();
		for (Future<Collection<MerchandiseResult>> f : futures)
		{
			try
			{
				log.debug("searchMerchandise::waiting on future " + f);
				Collection<MerchandiseResult> results = f.get(SERVICE_TIMEOUT, TimeUnit.SECONDS);
				if (results != null)
					allResults.addAll(results);
			}
			catch (Exception e)
			{
				log.warn("searchMerchandise::search call threw exception " + e.toString(), e);
			}
		}
		threadPoolExecutor.shutdown();
		
		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));

		cacheableMerchandiseResults = new CacheableMerchandiseResults();
		cacheableMerchandiseResults.result = allResults;
		log.debug("searchMerchandise::putting cache for " + availSearchRQ);
		getCache().put(availSearchRQ, cacheableMerchandiseResults);

		log.debug("searchMerchandise::returning " + allResults.size() + " results");
		return allResults;
	}

	public MerchandiseResult rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		log.debug("rateCheck::enter");

		MerchandiseServiceIF merchandisesService = null;
		merchandisesService = channelService.getMerchandiseServiceIF(rateCheckRQDTO.getChannel());
		return merchandisesService.rateCheck(rateCheckRQDTO);
	}
	
	public MerchandiseBookRS book(String client, MerchandiseBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");

		validateBook(client, bookRQ);

		String channel = null;
		
		for (MerchandiseBookRQ.MerchandiseRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
		}
		
		MerchandiseServiceIF merchandisesService = null;
		merchandisesService = channelService.getMerchandiseServiceIF(channel);
		log.debug("book::merchandisesService=" + merchandisesService);
		MerchandiseBookRS merchandiseBookRS = merchandisesService.book(client, bookRQ);
		getCache().clear();
		return merchandiseBookRS;
	}

	
	private void validateBook(String site, MerchandiseBookRQ bookRQ) throws Exception
	{
		log.debug("validateBook::enter");
		// ensure all items are for the same channel!
		String channel = null;
		
		for (MerchandiseBookRQ.MerchandiseRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
		} 
	}

	/*
	public CancelRS cancel(String site, CancelRQ cancelRQ) throws Exception
	{
		log.debug("cancel::enter");

		TransferServiceIF transferService = null;
		transferService = channelService.getTransferService(site);
		return transferService.cancel(site, cancelRQ);
	}
*/
	/*
	public RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) throws Exception
	{
		log.debug("retrieve::enter");

		TransferServiceIF transferService = null;
		transferService = channelService.getTransferService(retrieveRQ.getChannel());
		return transferService.retrieve(site, retrieveRQ);
	}*/

	
	private boolean useLocalIMS(AvailSearchRQDTO searchRQ)
	{
		log.debug("useLocalIMS::enter for client " + searchRQ.getClient());
		
		Boolean enabled = propertiesDAO.getProperty(searchRQ.getClient(), IMSService.CHANNEL, "enabled", false);
		log.debug("useLocalIMS::enabled=" + enabled);
		if (!enabled)
			return false;

		return true;
	}
	

	private final Cache<AvailSearchRQDTO, CacheableMerchandiseResults> getCache()
	{
		if (_cache == null)
		{
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			MutableConfiguration<AvailSearchRQDTO, CacheableMerchandiseResults> configuration = new MutableConfiguration<AvailSearchRQDTO, CacheableMerchandiseResults>()
					.setTypes(AvailSearchRQDTO.class, CacheableMerchandiseResults.class).setStoreByValue(false).setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(javax.cache.expiry.Duration.FIVE_MINUTES));
			// TODO keep this cache on disk!
			_cache = cacheManager.createCache("merchandiseSearch", configuration);
		}
		return _cache;
	}

	private static Cache<AvailSearchRQDTO, CacheableMerchandiseResults> _cache = null;

}
