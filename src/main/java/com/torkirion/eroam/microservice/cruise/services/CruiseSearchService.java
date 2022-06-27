package com.torkirion.eroam.microservice.cruise.services;

import com.torkirion.eroam.microservice.cruise.dto.DetailRQDTO;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
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

import com.torkirion.eroam.microservice.cruise.apidomain.*;
import com.torkirion.eroam.microservice.cruise.dto.SearchRQDTO;
import com.torkirion.eroam.microservice.cruise.endpoint.CruiseServiceIF;
import com.torkirion.eroam.microservice.cruise.endpoint.traveltek.TravelTekService;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.cache.FileCache;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CruiseSearchService
{
	private static final int SERVICE_TIMEOUT = 30;

	@NonNull
	private SystemPropertiesDAO propertiesDAO;

	@NonNull
	private CruiseChannelService channelService;

	public static class CacheableCruiseResults implements Serializable
	{
		private static final long serialVersionUID = 1L;

		List<CruiseResult> result;
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

	public List<CruiseLine> availCruiseLines(String client)
	{
		if (log.isDebugEnabled())
			log.debug("availCruiseLines::enter");
		
		SearchRQDTO searchRQDTO = new SearchRQDTO();
		searchRQDTO.setClient(client);
		List<CruiseLine> cruiseLines = new ArrayList<>();
		if (useTravelTek(searchRQDTO))
		{
			CruiseServiceIF travelTekService = channelService.getCruiseService(TravelTekService.CHANNEL);
			cruiseLines.addAll(travelTekService.availCruiseLines(client));
		}
		return cruiseLines;
	}
	
	public List<String> availDestinations(String client)
	{
		if (log.isDebugEnabled())
			log.debug("availDestinations::enter");
		
		SearchRQDTO searchRQDTO = new SearchRQDTO();
		searchRQDTO.setClient(client);
		List<String> destinations = new ArrayList<>();
		if (useTravelTek(searchRQDTO))
		{
			CruiseServiceIF travelTekService = channelService.getCruiseService(TravelTekService.CHANNEL);
			destinations.addAll(travelTekService.availDestinations(client));
		}
		return destinations;
	}
	
	public List<Location> availLocations(String client)
	{
		if (log.isDebugEnabled())
			log.debug("availLocations::enter");
		
		SearchRQDTO searchRQDTO = new SearchRQDTO();
		searchRQDTO.setClient(client);
		List<Location> locations = new ArrayList<>();
		if (useTravelTek(searchRQDTO))
		{
			CruiseServiceIF travelTekService = channelService.getCruiseService(TravelTekService.CHANNEL);
			locations.addAll(travelTekService.availLocations(client));
		}
		return locations;
	}
	
	public List<CruiseResult> searchCruises(SearchRQDTO availSearchRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("searchCruises::enter");

		long timer1 = System.currentTimeMillis();

		if (log.isDebugEnabled())
			log.debug("searchCruises::checking cache for " + availSearchRQDTO);
		CacheableCruiseResults cacheableCruiseResults = getCache().get(availSearchRQDTO.toString());
		if (cacheableCruiseResults != null && !cacheableCruiseResults.result.isEmpty())
		{
			if (log.isDebugEnabled())
				log.debug("searchCruises::returning cached result");
			return cacheableCruiseResults.result;
		}

		ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		Set<Future<List<CruiseResult>>> futures = new HashSet<Future<List<CruiseResult>>>();
		if (useTravelTek(availSearchRQDTO))
		{
			CruiseServiceIF travelTekService = channelService.getCruiseService(TravelTekService.CHANNEL);
			CruiseCallableService callableService = new CruiseCallableService(travelTekService, availSearchRQDTO);
			Future<List<CruiseResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			if (log.isDebugEnabled())
				log.debug("searchCruises::adding travelTekService future " + f);
		}

		List<CruiseResult> allResults = new ArrayList<>();
		for (Future<List<CruiseResult>> f : futures)
		{
			try
			{
				if (log.isDebugEnabled())
					log.debug("searchCruises::waiting on future " + f);
				List<CruiseResult> results = f.get(SERVICE_TIMEOUT, TimeUnit.SECONDS);
				if (results != null)
					allResults.addAll(results);
			}
			catch (Exception e)
			{
				log.warn("searchCruises::search call threw exception " + e.toString(), e);
			}
		}

		// add master cruise details
		//markupCruiseProperty(allResults, availSearchRQ.getCountryCodeOfOrigin());

		// merge results
		//List<CruiseResult> mergedResults = mergeResults(allResults);
		List<CruiseResult> mergedResults = allResults;

		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));

		cacheableCruiseResults = new CacheableCruiseResults();
		cacheableCruiseResults.result = mergedResults;
		if (log.isDebugEnabled())
			log.debug("searchCruises::putting cache for " + availSearchRQDTO);
		getCache().put(availSearchRQDTO.toString(), cacheableCruiseResults);
		CacheableCruiseResults test = getCache().get(availSearchRQDTO.toString());
		if (test == null)
		{
			if (log.isDebugEnabled())
				log.debug("searchCruises::could not save cache!!");
		}
		else
		{
			if (log.isDebugEnabled())
				log.debug("searchCruises::cache OK!!");
		}

		if (log.isDebugEnabled())
			log.debug("searchCruises::returning " + mergedResults.size() + " results");
		return mergedResults;
	}

	public CruiseResult detailCruise(DetailRQDTO detailRQDTO) throws Exception
	{
		log.debug("detail::enter");

		CruiseServiceIF cruiseService = channelService.getCruiseService(detailRQDTO.getChannel());
		return cruiseService.detailCruise(detailRQDTO);
	}

	/*
	public CruiseRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		log.debug("rateCheck::enter");

		CruiseServiceIF cruiseService = null;
		cruiseService = channelService.getCruiseService(rateCheckRQDTO.getChannel());
		return cruiseService.rateCheck(rateCheckRQDTO);
	}

	public CruiseBookRS book(String client, CruiseBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");

		validateBook(client, bookRQ);

		String channel = null;
		for (CruiseBookRQ.CruiseRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
		}
		CruiseServiceIF cruiseService = null;
		cruiseService = channelService.getCruiseService(channel);
		CruiseBookRS bookRS = cruiseService.book(client, bookRQ);
		getCache().clear();
		return bookRS;
	}

	private void validateBook(String site, CruiseBookRQ bookRQ) throws Exception
	{
		log.debug("validateBook::enter");
		// ensure all items are for the same channel!
		String channel = null;
		for (CruiseBookRQ.CruiseRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
		}

		for (CruiseRequestItem item : bookRQ.getItems())
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

	public CruiseCancelRS cancel(String site, CruiseCancelRQ cancelRQ) throws Exception
	{
		log.debug("cancel::enter");

		CruiseServiceIF cruiseService = null;
		cruiseService = channelService.getCruiseService(cancelRQ.getChannel());
		return cruiseService.cancel(site, cancelRQ);
	}

	public CruiseRetrieveRS retrieve(String site, CruiseRetrieveRQ retrieveRQ) throws Exception
	{
		log.debug("retrieve::enter");

		CruiseServiceIF cruiseService = null;
		cruiseService = channelService.getCruiseService(retrieveRQ.getChannel());
		return cruiseService.retrieve(site, retrieveRQ);
	}
*/
	private boolean useTravelTek(SearchRQDTO availSearchRQ)
	{
		log.debug("useTravelTek::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), TravelTekService.CHANNEL, "enabled", false);
		log.debug("useYalago::enabled=" + enabled);
		if (!enabled)
			return false;
		if (availSearchRQ.getChannel() != null && !availSearchRQ.getChannel().equals(TravelTekService.CHANNEL))
			return false;
		if (availSearchRQ.getChannelExceptions() != null && availSearchRQ.getChannelExceptions().contains(TravelTekService.CHANNEL))
			return false;
		return true;
	}

	/**
	 * We could use the actual AvailSearchRQDTO to be the key, but we have troubles hashing and 'equals' down through all the
	 * structures. Easier to use the String representation as the key
	 * 
	 * @return
	 */
	private final org.ehcache.Cache<String, CacheableCruiseResults> getCache() throws Exception
	{
		if (_cache == null)
		{
			Path tempDirWithPrefix = Files.createTempDirectory("cruiseSearch");
			log.debug("getCache::tempDirWithPrefix=" + tempDirWithPrefix);

			_statisticsService = new DefaultStatisticsService();
			PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(CacheManagerBuilder.persistence(tempDirWithPrefix.toFile())).using(_statisticsService)
					.build(true);

			org.ehcache.Cache<String, CacheableCruiseResults> ehCache = persistentCacheManager.createCache("cruiseSearch",
					CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, CacheableCruiseResults.class, ResourcePoolsBuilder.heap(5).disk(1000, MemoryUnit.MB)));
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
			CacheStatistics ehCacheStat = _statisticsService.getCacheStatistics("cruiseSearch");
			log.info("logStats::cruiseSearch hit%=" + ehCacheStat.getCacheHitPercentage() + ", gets=" + ehCacheStat.getCacheGets() + ", evictions/expirations/removals="
					+ ehCacheStat.getCacheEvictions() + "/" + ehCacheStat.getCacheExpirations() + "/" + ehCacheStat.getCacheRemovals() + ", heap count="
					+ ehCacheStat.getTierStatistics().get("OnHeap").getMappings());
		}
	}

	private StatisticsService _statisticsService = null;

	private static org.ehcache.Cache<String, CacheableCruiseResults> _cache = null;

}
