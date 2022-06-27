package com.torkirion.eroam.microservice.transfers.services;

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
import java.util.stream.Collectors;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRS;
import com.torkirion.eroam.microservice.transfers.apidomain.RetrieveTransferRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.RetrieveTransferRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO;
import com.torkirion.eroam.microservice.transfers.endpoint.TransferServiceIF;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.JayrideService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferSearchService
{
	private static final int SERVICE_TIMEOUT = 30;

	@NonNull
	private SystemPropertiesDAO propertiesDAO;

	@NonNull
	private TransferChannelService channelService;

	public static class CacheableTransferResults implements Serializable
	{
		private static final long serialVersionUID = 1L;

		List<TransferResult> result;
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

	public List<TransferResult> searchTransfers(SearchRQDTO searchRQ) throws Exception
	{
		log.debug("searchTransfers::enter");

		long timer1 = System.currentTimeMillis();

		log.debug("searchTransfers::checking cache for " + searchRQ);
		CacheableTransferResults cacheableTransferResults = getCache().get(searchRQ);
		if (cacheableTransferResults != null)
		{
			log.debug("searchTransfers::returning cached result");
			return cacheableTransferResults.result;
		}

		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(searchRQ.getClient(), propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		Set<Future<List<TransferResult>>> futures = new HashSet<Future<List<TransferResult>>>();
		if (useJayride(searchRQ))
		{
			TransferServiceIF jayrideService = channelService.getTransferService(JayrideService.CHANNEL);
			TransferCallableService callableService = new TransferCallableService(jayrideService, searchRQ);
			Future<List<TransferResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("searchTransfers::adding Jayride future " + f);
		}

		List<TransferResult> allResults = new ArrayList<>();
		for (Future<List<TransferResult>> f : futures)
		{
			try
			{
				log.debug("searchTransfers::waiting on future " + f);
				Collection<TransferResult> results = f.get(SERVICE_TIMEOUT, TimeUnit.SECONDS);
				if (results != null)
					allResults.addAll(results);
			}
			catch (Exception e)
			{
				log.warn("searchTransfers::search call threw exception " + e.toString(), e);
			}
		}
		threadPoolExecutor.shutdown();
		
		if ( searchRQ.getSupplierName() != null && searchRQ.getSupplierName().length() > 0 )
		{
			log.debug("searchTransfers::filtering by supplier " + searchRQ.getSupplierName());
			allResults = allResults.stream().filter(t -> t.getSupplier().getSupplierName().equalsIgnoreCase(searchRQ.getSupplierName())).collect(Collectors.toList());
		}
		
		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));

		cacheableTransferResults = new CacheableTransferResults();
		cacheableTransferResults.result = allResults;
		log.debug("searchHotels::putting cache for " + searchRQ);
		getCache().put(searchRQ, cacheableTransferResults);
		
		log.debug("searchTransfers::returning " + allResults.size() + " results");
		return allResults;
	}
/*
	public RateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		log.debug("rateCheck::enter");

		TransferServiceIF accommodationService = null;
		accommodationService = channelService.getAccommodationService(rateCheckRQDTO.getChannel());
		return accommodationService.rateCheck(rateCheckRQDTO);
	}
*/
	
	public TransferBookRS book(String client, String subclient, TransferBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");

		validateBook(client, subclient, bookRQ);

		String channel = null;
		for (TransferBookRQ.TransferRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
		}
		TransferServiceIF transferService = null;
		transferService = channelService.getTransferService(channel);
		return transferService.book(client, subclient, bookRQ);
	}

	private void validateBook(String client, String subclient, TransferBookRQ bookRQ) throws Exception
	{
		log.debug("validateBook::enter");
		// ensure all items are for the same channel!
		String channel = null;
		for (TransferBookRQ.TransferRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
		}
		if (channel == null)
		{
			throw new Exception("Channel must be specified");
		}

		/*
		for (RequestItem item : bookRQ.getItems())
		{
			if (item.getTravellerIndex().size() > bookRQ.getTravellers().size())
			{
				throw new Exception("travellerIndex size must equal travellers size");
			}
		} */
		for (Traveller t : bookRQ.getTravellers())
		{
			if (t.getGivenName() == null && t.getGivenName().length() == 0 || t.getSurname() == null || t.getSurname().length() == 0)
			{
				throw new Exception("Traveller first names and surnames must all be complete");
			}
		}
	}

	
	public TransferCancelRS cancel(String client, String subclient, TransferCancelRQ cancelRQ) throws Exception
	{
		log.debug("cancel::enter");

		TransferServiceIF transferService = null;
		transferService = channelService.getTransferService(cancelRQ.getChannel());
		return transferService.cancel(client, subclient, cancelRQ);
	}

	public RetrieveTransferRS retrieve(String client, String subclient, RetrieveTransferRQ retrieveRQ) throws Exception
	{
		log.debug("retrieve::enter");

		TransferServiceIF transferService = null;
		transferService = channelService.getTransferService(retrieveRQ.getChannel());
		return transferService.retrieve(client, subclient, retrieveRQ);
	}

	private boolean useJayride(SearchRQDTO searchRQ)
	{
		log.debug("useJayride::enter for client " + searchRQ.getClient());
		
		Boolean enabled = propertiesDAO.getProperty(searchRQ.getClient(), JayrideService.CHANNEL, "enabled", false);
		log.debug("useJayride::enabled=" + enabled);
		if ( !enabled )
			return false;
		if ( searchRQ.getChannel() != null && !searchRQ.getChannel().equals(JayrideService.CHANNEL) )
			return false;
		return true;
	}
	

	private final Cache<SearchRQDTO, CacheableTransferResults> getCache()
	{
		if (_cache == null)
		{
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			MutableConfiguration<SearchRQDTO, CacheableTransferResults> configuration = new MutableConfiguration<SearchRQDTO, CacheableTransferResults>()
					.setTypes(SearchRQDTO.class, CacheableTransferResults.class).setStoreByValue(false).setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(javax.cache.expiry.Duration.FIVE_MINUTES));
			// TODO keep this cache on disk!
			_cache = cacheManager.createCache("transferSearch", configuration);
		}
		return _cache;
	}

	private Cache<SearchRQDTO, CacheableTransferResults> _cache = null;

}
