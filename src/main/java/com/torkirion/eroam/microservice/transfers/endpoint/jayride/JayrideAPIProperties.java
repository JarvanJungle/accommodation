package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import lombok.Getter;
@Getter
public class JayrideAPIProperties
{
	public JayrideAPIProperties(SystemPropertiesDAO properties, String site)
	{
		jayrideURL = properties.getProperty(site, JayrideService.CHANNEL, "jayrideURL");
		apikey = properties.getProperty(site, JayrideService.CHANNEL, "apikey");
		bypassBooking = properties.getProperty(site, JayrideService.CHANNEL, "bypassBooking", false);
		agentName = properties.getProperty(site, JayrideService.CHANNEL, "agentName");
		agentSupportEmail = properties.getProperty(site, JayrideService.CHANNEL, "agentSupportEmail");
		agentSupportPhone = properties.getProperty(site, JayrideService.CHANNEL, "agentSupportPhone");
	}

	String jayrideURL;

	String agentName;

	String agentSupportEmail;

	String agentSupportPhone;

	String apikey;

	boolean bypassBooking = false;
}