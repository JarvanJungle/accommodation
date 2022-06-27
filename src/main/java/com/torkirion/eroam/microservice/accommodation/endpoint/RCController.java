package com.torkirion.eroam.microservice.accommodation.endpoint;

import java.util.Set;

import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationResult;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRateCheckRS;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class RCController
{
	/**
	 * 
	 * @param code an optional code being ONE hotel code to reprocess
	 * @throws Exception
	 */
	public abstract void process(String code) throws Exception;
}
