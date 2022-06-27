package com.torkirion.eroam.microservice.accommodation.endpoint.youtravel;

import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

public class YoutravelAPIProperties
{
	public YoutravelAPIProperties(SystemPropertiesDAO properties, String site)
	{
		youtravelURL = properties.getProperty(site, YoutravelService.CHANNEL, "youtravelURL");
		apikey = properties.getProperty(site, YoutravelService.CHANNEL, "apikey");
		secret = properties.getProperty(site, YoutravelService.CHANNEL, "secret");
		bypassBooking = properties.getProperty(site, YoutravelService.CHANNEL, "bypassBooking", false);
		testBooking = properties.getProperty(site, YoutravelService.CHANNEL, "testBooking", false);
		markupCNXValues = properties.getProperty(site, YoutravelService.CHANNEL, "markupCNXValues", Boolean.FALSE);
		allowZeroCommissionProduct = properties.getProperty(site, YoutravelService.CHANNEL, "allowZeroCommissionProduct", true);
		sourceMarket = properties.getProperty(site, YoutravelService.CHANNEL, "sourceMarket", "GB");
	}

	String youtravelURL;

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