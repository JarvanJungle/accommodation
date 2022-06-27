package com.torkirion.eroam.microservice.transport.endpoint;

import java.util.Collection;

import com.torkirion.eroam.microservice.transport.apidomain.*;
import com.torkirion.eroam.microservice.transport.dto.TransportChosenRQDTO;

import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO;

public interface TransportServiceIF
{
	public Collection<AvailTransportSearchRS> search(AvailTransportSearchRQDTO availTransportSearchRQDTO) throws Exception;

	public TransportRateCheckRS rateCheck(String client, TransportRateCheckRQ rateCheckRQ) throws Exception;

	public TransportBookRS book(String client, TransportBookRQ bookRQ) throws Exception;

	public TransportCancelRS cancel(String site, TransportCancelRQ cancelRQ) throws Exception;

	default Collection<AvailTransportSearchRS> choose(String client, TransportChosenRQDTO chooseRQ) throws Exception {
		throw new Exception("not supported");
	}

	//public abstract RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) throws Exception;
}
