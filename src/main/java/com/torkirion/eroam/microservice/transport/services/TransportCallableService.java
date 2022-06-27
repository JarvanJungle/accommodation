package com.torkirion.eroam.microservice.transport.services;

import java.util.Collection;
import java.util.concurrent.Callable;

import com.torkirion.eroam.microservice.transport.apidomain.AvailTransportSearchRS;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO;
import com.torkirion.eroam.microservice.transport.endpoint.TransportServiceIF;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TransportCallableService implements Callable<Collection<AvailTransportSearchRS>>
{

	public TransportCallableService(TransportServiceIF service, AvailTransportSearchRQDTO searchRQ)
	{
		super();
		log.debug("CallableService::service=" + service);
		this.service = service;
		this.searchRQ = searchRQ;
	}

	public Collection<AvailTransportSearchRS> call() throws Exception
	{
		log.debug("call::enter");
		try
		{
			if (searchRQ != null)
			{
				return service.search(searchRQ);
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			log.warn("call::call failed with " + e.toString(), e);
			return null;
		}
	}

	private TransportServiceIF service;

	private AvailTransportSearchRQDTO searchRQ;
}