package com.torkirion.eroam.microservice.transport.services;

import com.torkirion.eroam.microservice.transport.apidomain.*;
import com.torkirion.eroam.microservice.transport.dto.TransportChosenRQDTO;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.apidomain.DocumentedTraveller;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO.Route;
import com.torkirion.eroam.microservice.transport.endpoint.TransportServiceIF;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.CTWService;
import com.torkirion.eroam.microservice.transport.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransportSearchService
{
	private static final int SERVICE_TIMEOUT = 70;

	@NonNull
	private SystemPropertiesDAO propertiesDAO;

	@NonNull
	private TransportChannelService channelService;

	public Collection<AvailTransportSearchRS> searchBasic(AvailTransportSearchRQDTO availSearchRQ) throws Exception
	{
		log.debug("searchBasic::enter::availSearchRQ");
		log.debug(JsonUtil.convertToPrettyJson(availSearchRQ));

		long timer1 = System.currentTimeMillis();

		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(availSearchRQ.getClient(), propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		Collection<Future<Collection<AvailTransportSearchRS>>> futures = new ArrayList<Future<Collection<AvailTransportSearchRS>>>();
//		if (useLocalIMS(availSearchRQ))
//		{
//
//			TransportServiceIF imsService = channelService.getTransportServiceIF(IMSService.CHANNEL);
//			TransportCallableService callableService = new TransportCallableService(imsService, availSearchRQ);
//			Future<Collection<AvailTransportSearchRS>> f = threadPoolExecutor.submit(callableService);
//			futures.add(f);
//			log.debug("searchBasic::adding ims future " + f);
//		}
		if (useSaveATrain(availSearchRQ))
		{

			TransportServiceIF saveATrainService = channelService.getTransportServiceIF(SaveATrainService.CHANNEL);
			TransportCallableService callableService = new TransportCallableService(saveATrainService, availSearchRQ);
			Future<Collection<AvailTransportSearchRS>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("searchBasic::adding saveATrainService future " + f);
		}
		if (useCTW(availSearchRQ))
		{

			TransportServiceIF ctwService = channelService.getTransportServiceIF(CTWService.CHANNEL);
			TransportCallableService callableService = new TransportCallableService(ctwService, availSearchRQ);
			Future<Collection<AvailTransportSearchRS>> f = threadPoolExecutor.submit(callableService);
			futures.add(f);
			log.debug("searchBasic::adding ctwService future " + f);
		}

		Collection<AvailTransportSearchRS> allResults = new ArrayList<>();
		for (Future<Collection<AvailTransportSearchRS>> f : futures)
		{
			try
			{
				log.debug("searchBasic::waiting on future " + f);
				Collection<AvailTransportSearchRS> results = f.get(SERVICE_TIMEOUT, TimeUnit.SECONDS);
				if (results != null)
					allResults.addAll(results);
			}
			catch (Exception e)
			{
				log.warn("searchBasic::search call threw exception " + e.toString(), e);
			}
		}
		threadPoolExecutor.shutdown();
		log.info("EXECUTE::" + (System.currentTimeMillis() - timer1));

		log.debug("searchBasic::returning " + allResults.size() + " results");
		return allResults;
	}

	public Collection<AvailTransportSearchRS> chooseBasic(String client, TransportChooseRQ chooseRQ) throws Exception
	{
		log.debug("chooseBasic::enter::chooseRQ: ");
		if (chooseRQ == null || !SaveATrainService.CHANNEL.equals(chooseRQ.getChannel()))
		{
			throw new Exception("Just support the SAVEATRAIN channel");
		}
		if (chooseRQ.getChosenRouteId() == null || !chooseRQ.getChosenRouteId().startsWith(SaveATrainService.CHANNEL_PREFIX))
		{
			throw new Exception("data is empty or choosenRouteId has to start with ST_");
		}
		TransportServiceIF transportService = channelService.getTransportServiceIF(chooseRQ.getChannel());
		TransportChosenRQDTO transportChosenRQDTO = TransportChosenRQDTO.makeTransportChosenRQDTO(chooseRQ);
		return transportService.choose(client, transportChosenRQDTO);
	}

	public TransportRateCheckRS rateCheck(String client, TransportRateCheckRQ rateCheckRQ) throws Exception
	{
		log.debug("rateCheck::enter");

		TransportServiceIF transportService = null;
		transportService = channelService.getTransportServiceIF(rateCheckRQ.getChannel());
		return transportService.rateCheck(client, rateCheckRQ);
	}

	public TransportBookRS book(String client, TransportBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");

		validateBook(client, bookRQ);

		TransportServiceIF transportService = channelService.getTransportServiceIF(bookRQ.getChannel());
		return transportService.book(client, bookRQ);
	}

	private void validateBook(String site, TransportBookRQ bookRQ) throws Exception
	{
		log.debug("validateBook::enter");
		// ensure all items are for the same channel!
		/*
		 * for (RequestItem item : bookRQ.getItems()) { if (item.getTravellerIndex().size() > bookRQ.getTravellers().size()) {
		 * throw new Exception("travellerIndex size must equal travellers size"); } }
		 */
		for (DocumentedTraveller t : bookRQ.getTravellers())
		{
			if (t.getGivenName() == null && t.getGivenName().length() == 0 || t.getSurname() == null || t.getSurname().length() == 0)
			{
				throw new Exception("Traveller first names and surnames must all be complete");
			}
		}
	}

	public TransportCancelRS cancel(String site, TransportCancelRQ cancelRQ) throws Exception
	{
		log.debug("cancel::enter");

		TransportServiceIF transportService = null;
		transportService = channelService.getTransportServiceIF(site);
		return transportService.cancel(site, cancelRQ);
	}

	private boolean useLocalIMS(AvailTransportSearchRQDTO availSearchRQ)
	{
		log.debug("useLocalIMS::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), IMSService.CHANNEL, "enabled", false);
		log.debug("useLocalIMS::enabled=" + enabled);
		if (!enabled)
			return false;
		for (Route r : availSearchRQ.getRoute())
		{
			if (r.getDepartureIata() == null || r.getArrivalIata() == null)
			{
				log.debug("useLocalIMS::blank departure or arrival iata");
				return false;
			}
			if (!r.getTransportType().equals(TransportType.flight))
			{
				log.debug("useLocalIMS::unsupported transporttype:" + r.getTransportType());
				return false;
			}
		}
		return true;
	}

	private boolean useSaveATrain(AvailTransportSearchRQDTO availSearchRQ)
	{
		log.debug("useSaveATrain::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), SaveATrainService.CHANNEL, "enabled", false);
		log.debug("useSaveATrain::enabled=" + enabled);
		if (!enabled)
			return false;
		for (Route r : availSearchRQ.getRoute())
		{
			if (r.getDepartureNorthwest() == null || r.getDepartureSoutheast() == null || r.getArrivalNorthwest() == null || r.getArrivalSoutheast() == null)
			{
				log.debug("useSaveATrain::blank departure or arrival lat longs");
				return false;
			}
			if (!r.getTransportType().equals(TransportType.rail))
			{
				log.debug("useSaveATrain::unsupported transporttype:" + r.getTransportType());
				return false;
			}
		}
		return true;
	}

	private boolean useCTW(AvailTransportSearchRQDTO availSearchRQ)
	{
		log.debug("useCTW::enter for client " + availSearchRQ.getClient());

		Boolean enabled = propertiesDAO.getProperty(availSearchRQ.getClient(), CTWService.CHANNEL, "enabled", false);
		log.debug("useCTW::enabled=" + enabled);
		if (!enabled)
			return false;
		for (Route r : availSearchRQ.getRoute())
		{
			if (r.getDepartureIata() == null || r.getArrivalIata() == null)
			{
				log.debug("useCTW::blank departure or arrival iata");
				return false;
			}
			if (!r.getTransportType().equals(TransportType.flight))
			{
				log.debug("useCTW::unsupported transporttype:" + r.getTransportType());
				return false;
			}
		}
		return true;
	}
}
