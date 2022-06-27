package com.torkirion.eroam.microservice.events.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import com.torkirion.eroam.microservice.events.apidomain.EventResult;
import com.torkirion.eroam.microservice.events.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.events.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.events.endpoint.EventsServiceIF;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class EventsCallableService implements Callable<Collection<EventResult>>
{

	public EventsCallableService(EventsServiceIF service, AvailSearchRQDTO searchRQ)
	{
		super();
		log.debug("CallableService::service=" + service);
		this.service = service;
		this.searchRQ = searchRQ;
	}

	public Collection<EventResult> call() throws Exception
	{
		log.debug("call::enter");
		try
		{
			if (searchRQ != null)
			{
				if ( searchRQ instanceof AvailSearchByGeocordBoxRQDTO)
					return service.searchByGeocordBox((AvailSearchByGeocordBoxRQDTO)searchRQ);
				else
					return null;
			}
			else
			{
				List<EventResult> r = new ArrayList<EventResult>();
				return r;
			}
		}
		catch (Exception e)
		{
			log.warn("call::call failed with " + e.toString(), e);
			return null;
		}
	}

	private EventsServiceIF service;

	private AvailSearchRQDTO searchRQ;
}