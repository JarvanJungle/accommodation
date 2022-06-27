package com.torkirion.eroam.microservice.events.services;

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

import com.torkirion.eroam.microservice.events.apidomain.EventsBookRQ;
import com.torkirion.eroam.microservice.events.apidomain.EventsBookRS;
import com.torkirion.eroam.microservice.events.apidomain.EventResult;
import com.torkirion.eroam.microservice.events.apidomain.EventSeries;
import com.torkirion.eroam.microservice.events.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.events.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.events.endpoint.EventsServiceIF;
import com.torkirion.eroam.microservice.events.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.events.apidomain.EventResult;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsSearchService
{
	private static final int SERVICE_TIMEOUT = 30;

	@NonNull
	private SystemPropertiesDAO propertiesDAO;

	@NonNull
	private EventsChannelService channelService;

	public static class CacheableEventsResults implements Serializable
	{
		private static final long serialVersionUID = 1L;

		Collection<EventResult> result;
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

	public Collection<EventResult> searchEvents(AvailSearchRQDTO availSearchRQ) throws Exception
	{
		log.debug("searchEvents::enter");

		long timer1 = System.currentTimeMillis();

		log.debug("searchEvents::checking cache for " + availSearchRQ);
		CacheableEventsResults cacheableEventsResults = getCache().get(availSearchRQ);
		if (cacheableEventsResults != null)
		{
			log.debug("searchEvents::returning cached result");
			return cacheableEventsResults.result;
		}

		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(availSearchRQ.getClient(), propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		Collection<Future<Collection<EventResult>>> futures = new ArrayList<Future<Collection<EventResult>>>();
		if (useLocalIMS(availSearchRQ))
		{
			EventsServiceIF imsService = channelService.getEventsServiceIF(IMSService.CHANNEL);
			EventsCallableService callableService = new EventsCallableService(imsService, availSearchRQ);
			Future<Collection<EventResult>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("searchEvents::adding ims future " + f);
		}

		Collection<EventResult> allResults = new ArrayList<>();
		for (Future<Collection<EventResult>> f : futures)
		{
			try
			{
				log.debug("searchEvents::waiting on future " + f);
				Collection<EventResult> results = f.get(SERVICE_TIMEOUT, TimeUnit.SECONDS);
				if (results != null)
					allResults.addAll(results);
			}
			catch (Exception e)
			{
				log.warn("searchEvents::search call threw exception " + e.toString(), e);
			}
		}
		threadPoolExecutor.shutdown();
		
		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));

		cacheableEventsResults = new CacheableEventsResults();
		cacheableEventsResults.result = allResults;
		log.debug("searchEvents::putting cache for " + availSearchRQ);
		getCache().put(availSearchRQ, cacheableEventsResults);

		log.debug("searchEvents::returning " + allResults.size() + " results");
		return allResults;
	}

	public EventResult rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		log.debug("rateCheck::enter");

		EventsServiceIF eventsService = null;
		eventsService = channelService.getEventsServiceIF(rateCheckRQDTO.getChannel());
		return eventsService.rateCheck(rateCheckRQDTO);
	}
	
	public EventResult readEvent(String client, String eventId) throws Exception
	{
		log.debug("readEvent::enter");

		EventsServiceIF eventsService = null;
		eventsService = channelService.getEventsServiceIF(channelService.getChannelForEventId(eventId));
		return eventsService.readEvent(client, eventId);
	}
	
	public List<EventSeries> listSeries(String client) throws Exception
	{
		log.debug("listSeries::enter");

		List<EventSeries> series = new ArrayList<>();
		for ( EventsServiceIF eventsService : channelService.getAllEventsServiceIF() )
		{
			series.addAll(eventsService.listSeries(client));
		}
		return series;
	}
	
	public EventsBookRS book(String client, EventsBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");

		validateBook(client, bookRQ);

		String channel = null;
		for (EventsBookRQ.EventRequestItem item : bookRQ.getItems())
		{
			if (channel == null)
				channel = item.getChannel();
			if (!item.getChannel().equals(channel))
			{
				throw new Exception("All items must be of the same channel");
			}
			if ( item.getNumberOfTickets().intValue() == 0 )
			{
				throw new Exception("Must specify number of tickets greater than zero");
			}
		}
		EventsServiceIF eventsService = null;
		eventsService = channelService.getEventsServiceIF(channel);
		log.debug("book::eventsService=" + eventsService);
		EventsBookRS eventsBookRS = eventsService.book(client, bookRQ);
		getCache().clear();
		return eventsBookRS;
	}

	
	private void validateBook(String site, EventsBookRQ bookRQ) throws Exception
	{
		log.debug("validateBook::enter");
		// ensure all items are for the same channel!
		String channel = null;
		for (EventsBookRQ.EventRequestItem item : bookRQ.getItems())
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
	

	private final Cache<AvailSearchRQDTO, CacheableEventsResults> getCache()
	{
		if (_cache == null)
		{
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			MutableConfiguration<AvailSearchRQDTO, CacheableEventsResults> configuration = new MutableConfiguration<AvailSearchRQDTO, CacheableEventsResults>()
					.setTypes(AvailSearchRQDTO.class, CacheableEventsResults.class).setStoreByValue(false).setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(javax.cache.expiry.Duration.FIVE_MINUTES));
			// TODO keep this cache on disk!
			_cache = cacheManager.createCache("eventSearch", configuration);
		}
		return _cache;
	}

	private static Cache<AvailSearchRQDTO, CacheableEventsResults> _cache = null;

}
