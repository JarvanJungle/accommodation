package com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds;

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
		sourceMarket = properties.getProperty(site, HotelbedsService.CHANNEL, "sourceMarket", "GB");
	}

	String hotelbedsURL;

	String apikey;

	String secret;

	String sourceMarket;

	boolean useGZip;

	String proxyHost;

	Integer proxyPort;

	boolean bypassProxy = false;

	boolean bypassBooking = false;

	boolean testBooking = false;

	boolean markupCNXValues = false;

	int connectionTimeout = 60; // seconds

	boolean allowZeroCommissionProduct = true;
}