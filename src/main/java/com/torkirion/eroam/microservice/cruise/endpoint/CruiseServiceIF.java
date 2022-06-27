package com.torkirion.eroam.microservice.cruise.endpoint;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.cruise.apidomain.*;
import com.torkirion.eroam.microservice.cruise.dto.*;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;

public interface CruiseServiceIF
{
	public List<CruiseResult> searchByDestination(SearchRQDTO availSearchRQ);

	public CruiseResult detailCruise(DetailRQDTO detailRQDTO) throws Exception;

	public List<CruiseLine> availCruiseLines(String client);

	public List<String> availDestinations(String client);

	public List<Location> availLocations(String client);

	public void initiateRCLoad(String code);
	
	/*
	public AccommodationRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDT) throws Exception;

	public AccommodationBookRS book(String client, AccommodationBookRQ bookRQ) throws Exception;

	public AccommodationCancelRS cancel(String site, AccommodationCancelRQ cancelRQ) throws Exception;

	public abstract AccommodationRetrieveRS retrieve(String site, AccommodationRetrieveRQ retrieveRQ) throws Exception;
	*/
}
