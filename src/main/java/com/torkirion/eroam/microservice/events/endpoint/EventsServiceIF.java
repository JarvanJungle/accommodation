package com.torkirion.eroam.microservice.events.endpoint;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.events.apidomain.*;
import com.torkirion.eroam.microservice.events.dto.*;

public interface EventsServiceIF
{
	public Collection<EventResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ);

	public EventResult rateCheck(RateCheckRQDTO rateCheckRQDT) throws Exception;

	public EventResult readEvent(String client, String eventId) throws Exception;

	public List<EventSeries> listSeries(String client) throws Exception;
	
	public EventsBookRS book(String client, EventsBookRQ bookRQ) throws Exception;

	/*
	public CancelRS cancel(String site, CancelRQ cancelRQ) throws Exception;

	public abstract RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) throws Exception;
	*/
}
