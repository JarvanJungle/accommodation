package com.torkirion.eroam.microservice.hirecars.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import com.torkirion.eroam.microservice.hirecars.apidomain.*;
import com.torkirion.eroam.microservice.hirecars.dto.*;
import com.torkirion.eroam.microservice.hirecars.endpoint.HireCarServiceIF;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class HireCarCallableService implements Callable<Collection<HireCarResult>>
{

	public HireCarCallableService(HireCarServiceIF service, HireCarSearchRQDTO searchRQ)
	{
		super();
		log.debug("CallableService::service=" + service);
		this.service = service;
		this.searchRQ = searchRQ;
	}

	public Collection<HireCarResult> call() throws Exception
	{
		log.debug("call::enter");
		try
		{
			if (searchRQ != null)
				return service.search(searchRQ);
			else
			{
				List<HireCarResult> r = new ArrayList<HireCarResult>();
				return r;
			}
		}
		catch (Exception e)
		{
			log.warn("call::call failed with " + e.toString(), e);
			return null;
		}
	}

	private HireCarServiceIF service;

	private HireCarSearchRQDTO searchRQ;
}