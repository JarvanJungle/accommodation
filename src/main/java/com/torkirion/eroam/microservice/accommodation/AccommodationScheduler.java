package com.torkirion.eroam.microservice.accommodation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsRCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryService;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoRCController;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationNameSearcher;
import com.torkirion.eroam.microservice.config.ApplicationConfig;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AccommodationScheduler
{
	@Autowired
	private AccommodationNameSearcher accommodationNameSearcher;

	@Autowired
	ApplicationConfig applicationConfig;

	@Scheduled(fixedDelay = (1000 * 60 * 60 * 24 * 7), initialDelay = 1) // once per week
	public void scheduleAccommodationNameSearcher_prime() throws Exception
	{
		if ( applicationConfig.getProduct().isAll() || applicationConfig.getProduct().isAccommodation())
			accommodationNameSearcher.prime();
	}
}
