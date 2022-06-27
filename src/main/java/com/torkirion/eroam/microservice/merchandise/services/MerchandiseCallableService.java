package com.torkirion.eroam.microservice.merchandise.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseResult;
import com.torkirion.eroam.microservice.merchandise.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.merchandise.endpoint.MerchandiseServiceIF;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class MerchandiseCallableService implements Callable<Collection<MerchandiseResult>>
{

	public MerchandiseCallableService(MerchandiseServiceIF service, AvailSearchRQDTO searchRQ)
	{
		super();
		log.debug("CallableService::service=" + service);
		this.service = service;
		this.searchRQ = searchRQ;
	}

	public Collection<MerchandiseResult> call() throws Exception
	{
		log.debug("call::enter");
		try
		{
			if (searchRQ != null)
			{
				if ( searchRQ instanceof AvailSearchRQDTO)
					return service.search((AvailSearchRQDTO)searchRQ);
				else
					return null;
			}
			else
			{
				List<MerchandiseResult> r = new ArrayList<MerchandiseResult>();
				return r;
			}
		}
		catch (Exception e)
		{
			log.warn("call::call failed with " + e.toString(), e);
			return null;
		}
	}

	private MerchandiseServiceIF service;

	private AvailSearchRQDTO searchRQ;
}