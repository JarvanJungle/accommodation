package com.torkirion.eroam.microservice.transfers.services;

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
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO;
import com.torkirion.eroam.microservice.transfers.endpoint.TransferServiceIF;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TransferCallableService implements Callable<List<TransferResult>>
{

	public TransferCallableService(TransferServiceIF service, SearchRQDTO searchRQ)
	{
		super();
		log.debug("CallableService::service=" + service);
		this.service = service;
		this.searchRQ = searchRQ;
	}

	public List<TransferResult> call() throws Exception
	{
		log.debug("call::enter");
		try
		{
			if (searchRQ != null)
			{
				return service.searchByCodes(searchRQ);
			}
			else
			{
				List<TransferResult> r = new ArrayList<TransferResult>();
				return r;
			}
		}
		catch (Exception e)
		{
			log.warn("call::call failed with " + e.toString(), e);
			return null;
		}
	}

	private TransferServiceIF service;

	private SearchRQDTO searchRQ;
}