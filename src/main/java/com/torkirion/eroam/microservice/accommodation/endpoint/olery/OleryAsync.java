package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import org.springframework.scheduling.annotation.Async;

public class OleryAsync
{
	public OleryAsync(OleryService oleryService)
	{
		this.oleryService = oleryService;
	}
	private OleryService oleryService;

	@Async
	public void mapOleryCountryCodeAsync(String countryCode, String channel)
	{
		int passNumber = 0;
		boolean loop = true;
		while (loop)
		{
			loop = oleryService.mapAccommodationByCountry(countryCode, passNumber, channel);
			passNumber++;
		}
	}

	@Async
	public void loadReviews(String countryCode, Integer passNumber)
	{
		if ( passNumber == null )
			passNumber = 0;
		boolean loop = true;
		while (loop)
		{
			loop = oleryService.loadReviews(countryCode, passNumber);
			passNumber++;
		}
	}
}

