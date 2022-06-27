package com.torkirion.eroam.microservice.merchandise.endpoint;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.merchandise.apidomain.*;
import com.torkirion.eroam.microservice.merchandise.dto.*;

public interface MerchandiseServiceIF
{
	public Collection<MerchandiseResult> search(AvailSearchRQDTO availSearchRQ);

	public MerchandiseResult rateCheck(RateCheckRQDTO rateCheckRQDT) throws Exception;

	public MerchandiseBookRS book(String client, MerchandiseBookRQ bookRQ) throws Exception;

	/*
	public CancelRS cancel(String site, CancelRQ cancelRQ) throws Exception;

	public abstract RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) throws Exception;
	*/
}
