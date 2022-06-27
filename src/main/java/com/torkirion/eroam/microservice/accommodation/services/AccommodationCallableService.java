package com.torkirion.eroam.microservice.accommodation.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationResult;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AccommodationCallableService implements Callable<List<AccommodationResult>>
{

	public AccommodationCallableService(AccommodationServiceIF service, AvailSearchRQDTO availSearchRQ)
	{
		super();
		log.debug("CallableService::service=" + service);
		this.service = service;
		this.availSearchRQ = availSearchRQ;
	}

	public List<AccommodationResult> call() throws Exception
	{
		log.debug("call::enter");
		try
		{
			if (availSearchRQ != null)
			{
				log.debug("call::calling city search, hotelID is null for " + service);
				if ( availSearchRQ instanceof AvailSearchByGeocordBoxRQDTO)
					return service.searchByGeocordBox((AvailSearchByGeocordBoxRQDTO)availSearchRQ);
				else if ( availSearchRQ instanceof AvailSearchByHotelIdRQDTO)
					return service.searchByHotelId((AvailSearchByHotelIdRQDTO)availSearchRQ);
				else
					return null;
			}
			else
			{
				// call for individual hotel
				List<AccommodationResult> l = new ArrayList<AccommodationResult>();
				return l;
			}
		}
		catch (Exception e)
		{
			log.warn("call::call failed with " + e.toString(), e);
			return null;
		}
	}

	private AccommodationServiceIF service;

	private AvailSearchRQDTO availSearchRQ;
}