package com.torkirion.eroam.microservice.activities.endpoint.hotelbeds;

import java.math.BigDecimal;

import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

public class HotelbedsAPIProperties
{
	public HotelbedsAPIProperties(SystemPropertiesDAO properties, String site)
	{
		hotelbedsURL = properties.getProperty(site, HotelbedsService.CHANNEL, "hotelbedsURL");
		apikey = properties.getProperty(site, HotelbedsService.CHANNEL, "apikey");
		secret = properties.getProperty(site, HotelbedsService.CHANNEL, "secret");
		bypassBooking = properties.getProperty(site, HotelbedsService.CHANNEL, "bypassBooking", false);
		testBooking = properties.getProperty(site, HotelbedsService.CHANNEL, "testBooking", false);
		markupCNXValues = properties.getProperty(site, HotelbedsService.CHANNEL, "markupCNXValues", Boolean.FALSE);
		allowZeroCommissionProduct = properties.getProperty(site, HotelbedsService.CHANNEL, "allowZeroCommissionProduct", true);
		if ( properties.getProperty(site, HotelbedsService.CHANNEL, "tolerance") != null )
		{
			tolerance = new BigDecimal(properties.getProperty(site, HotelbedsService.CHANNEL, "tolerance"));
		}
	}

	String hotelbedsURL;

	String apikey;

	String secret;

	boolean useGZip;

	String proxyHost;

	Integer proxyPort;

	BigDecimal tolerance = BigDecimal.ZERO;

	boolean bypassProxy = false;

	boolean bypassBooking = false;

	boolean testBooking = false;

	boolean markupCNXValues = false;

	int connectionTimeout = 60; // seconds

	boolean allowZeroCommissionProduct = true;
}