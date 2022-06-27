package com.torkirion.eroam.microservice.accommodation.endpoint.sabrecsl;

import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

public class SabreCSLAPIProperties
{
	public SabreCSLAPIProperties(SystemPropertiesDAO properties, String site)
	{
		sabreURL = properties.getProperty(site, SabreCSLService.CHANNEL, "sabreURL");
		username = properties.getProperty(site, SabreCSLService.CHANNEL, "username");
		password = properties.getProperty(site, SabreCSLService.CHANNEL, "password");
		pcc = properties.getProperty(site, SabreCSLService.CHANNEL, "pcc");
		//token = properties.getProperty(site, SabreCSLService.CHANNEL, "token");
		//actionGetAvail = properties.getProperty(site, SabreCSLService.CHANNEL, "actionGetAvail");
		//requestToken = properties.getProperty(site, SabreCSLService.CHANNEL, "requestToken");
		addressLine = properties.getProperty(site, SabreCSLService.CHANNEL, "addressLine");
		cityName = properties.getProperty(site, SabreCSLService.CHANNEL, "cityName");
		countryCode = properties.getProperty(site, SabreCSLService.CHANNEL, "countryCode");
		postalCode = properties.getProperty(site, SabreCSLService.CHANNEL, "postalCode");
		streetNmbr = properties.getProperty(site, SabreCSLService.CHANNEL, "streetNmbr");

		siteInit = site;
	}

	String sabreURL;

	String username;

	String pcc;

	String password;

	//String token;

	//String actionGetAvail;

	//String requestToken;

	String siteInit;

	String addressLine;

	String cityName;

	String countryCode;

	String postalCode;

	String streetNmbr;

	boolean bypassProxy = false;

	boolean bypassBooking = false;

	boolean testBooking = false;

	boolean markupCNXValues = false;

	int connectionTimeout = 60; // seconds

	boolean allowZeroCommissionProduct = true;
}