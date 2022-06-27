package com.torkirion.eroam.microservice.cruise.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import com.torkirion.eroam.microservice.cruise.apidomain.*;
import com.torkirion.eroam.microservice.cruise.dto.*;
import com.torkirion.eroam.microservice.cruise.endpoint.CruiseServiceIF;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class CruiseCallableService implements Callable<List<CruiseResult>>
{

	public CruiseCallableService(CruiseServiceIF service, SearchRQDTO availSearchRQ)
	{
		super();
		log.debug("CallableService::service=" + service);
		this.service = service;
		this.availSearchRQ = availSearchRQ;
	}

	public List<CruiseResult> call() throws Exception
	{
		log.debug("call::enter");
		try
		{
			if (availSearchRQ != null)
			{
				log.debug("call::calling city search, hotelID is null for " + service);
				return service.searchByDestination(availSearchRQ);
			}
			else
			{
				List<CruiseResult> l = new ArrayList<CruiseResult>();
				return l;
			}
		}
		catch (Exception e)
		{
			log.warn("call::call failed with " + e.toString(), e);
			return null;
		}
	}

	private CruiseServiceIF service;

	private SearchRQDTO availSearchRQ;
}