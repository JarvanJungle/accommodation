package com.torkirion.eroam.microservice.accommodation.endpoint;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationResult;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRateCheckRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRetrieveRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRetrieveRS;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;

public interface AccommodationServiceIF
{
	public List<AccommodationResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ);

	public List<AccommodationResult> searchByHotelId(AvailSearchByHotelIdRQDTO availSearchRQ);

	public AccommodationRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDT) throws Exception;

	public AccommodationBookRS book(String client, AccommodationBookRQ bookRQ) throws Exception;

	public AccommodationCancelRS cancel(String site, AccommodationCancelRQ cancelRQ) throws Exception;

	public abstract AccommodationRetrieveRS retrieve(String site, AccommodationRetrieveRQ retrieveRQ) throws Exception;
}
