package com.torkirion.eroam.microservice.activities.endpoint;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.activities.apidomain.*;
import com.torkirion.eroam.microservice.activities.dto.*;

public interface ActivityServiceIF
{
//	public Collection<ActivityResult> search(AvailSearchByGeocordBoxRQDTO availSearchByGeocordBoxRQDTO);
	Collection<ActivityResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ);

	Collection<ActivityResult> searchByActivityId(AvailSearchByActivityIdRQDTO availSearchRQ);

	ActivityResult rateCheck(RateCheckRQDTO rateCheckRQDT) throws Exception;

	ActivityBookRS book(String client, ActivityBookRQ bookRQ) throws Exception;

	void initiateRCLoad(String code);
	
	public ActivityCancelRS cancel(String site, ActivityCancelRQ cancelRQ) throws Exception;
	/*
	 * public abstract RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) throws Exception;
	 */
}
