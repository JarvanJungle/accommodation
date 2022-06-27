package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.math.BigDecimal;

import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

public class ViatorV2APIProperties
{
	public ViatorV2APIProperties(SystemPropertiesDAO properties, String site)
	{
		viatorV1URL = properties.getProperty(site, ViatorV2Service.CHANNEL, "viatorV1URL");
		viatorV2URL = properties.getProperty(site, ViatorV2Service.CHANNEL, "viatorV2URL");
		apikey = properties.getProperty(site, ViatorV2Service.CHANNEL, "apikey");
		bypassBooking = properties.getProperty(site, ViatorV2Service.CHANNEL, "bypassBooking", false);
	}

	String viatorV1URL;

	String 	viatorV2URL;

	String apikey;

	boolean bypassBooking = false;
}