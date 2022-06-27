package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import lombok.Data;
import lombok.Getter;

@Getter
public class InnstantRCAPIProperties
{
	public InnstantRCAPIProperties(SystemPropertiesDAO properties, String site)
	{
		//innstantRCURL = properties.getProperty(site, InnstantService.CHANNEL, "innstantRCURL");
		innstantRCBook = properties.getProperty(site, InnstantService.CHANNEL, "innstantRCBook");
		innstantRCStatic = properties.getProperty(site, InnstantService.CHANNEL, "innstantRCStatic");
		innstantRCSearch = properties.getProperty(site, InnstantService.CHANNEL, "innstantRCSearch");
		aether_access_token = properties.getProperty(site, InnstantService.CHANNEL, "aether-access-token");
		aether_application_key = properties.getProperty(site, InnstantService.CHANNEL, "aether-application-key");
		mishor_application_key = properties.getProperty(site, InnstantService.CHANNEL, "mishor-application-key");
		bypassBooking = properties.getProperty(site, InnstantService.CHANNEL, "bypassBooking", false);
		testBooking = properties.getProperty(site, InnstantService.CHANNEL, "testBooking", false);
		markupCNXValues = properties.getProperty(site, InnstantService.CHANNEL, "markupCNXValues", Boolean.FALSE);
		allowZeroCommissionProduct = properties.getProperty(site, InnstantService.CHANNEL, "allowZeroCommissionProduct", true);
		sourceMarket = properties.getProperty(site, InnstantService.CHANNEL, "sourceMarket", "GB");
		address = properties.getProperty(site, InnstantService.CHANNEL, "address");
		city = properties.getProperty(site, InnstantService.CHANNEL, "city");
		country = properties.getProperty(site, InnstantService.CHANNEL, "country");
		email = properties.getProperty(site, InnstantService.CHANNEL, "email");
		zip = properties.getProperty(site, InnstantService.CHANNEL, "zip");
	}

	String innstantRCSearch; //	search api

	String innstantRCBook; //	book api

	String innstantRCStatic; // data static

	//String innstantRCURL;

	String aether_access_token;

	String aether_application_key;

	String mishor_application_key;

	String sourceMarket;

	String address;

	String email;

	String country;

	String city;

	String zip;

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