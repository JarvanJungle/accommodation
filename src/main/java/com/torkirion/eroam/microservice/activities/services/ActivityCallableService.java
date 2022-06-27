package com.torkirion.eroam.microservice.activities.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import com.torkirion.eroam.microservice.activities.apidomain.ActivityResult;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByActivityIdRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ActivityCallableService implements Callable<Collection<ActivityResult>>
{

	public ActivityCallableService(ActivityServiceIF service, AvailSearchRQDTO searchRQ)
	{
		super();
		log.debug("CallableService::service=" + service);
		this.service = service;
		this.searchRQ = searchRQ;
	}

	public Collection<ActivityResult> call() throws Exception
	{
		log.debug("call::enter");
		try
		{
			if (searchRQ != null)
			{
				if (searchRQ instanceof AvailSearchByGeocordBoxRQDTO)
					return service.searchByGeocordBox((AvailSearchByGeocordBoxRQDTO) searchRQ);
				else if (searchRQ instanceof AvailSearchByActivityIdRQDTO)
					return service.searchByActivityId((AvailSearchByActivityIdRQDTO) searchRQ);
				else
					return null;
			}
			else
			{
				List<ActivityResult> r = new ArrayList<ActivityResult>();
				return r;
			}
		}
		catch (Exception e)
		{
			log.warn("call::call failed with " + e.toString(), e);
			return null;
		}
	}

	private ActivityServiceIF service;

	private AvailSearchRQDTO searchRQ;
}