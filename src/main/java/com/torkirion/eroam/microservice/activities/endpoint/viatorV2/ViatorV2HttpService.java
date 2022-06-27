package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import org.apache.http.HttpMessage;

import com.torkirion.eroam.HttpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViatorV2HttpService extends HttpService
{
	private ViatorV2APIProperties viatorV2APIProperties;
	
	private boolean useV1;
	
	public ViatorV2HttpService(ViatorV2APIProperties viatorV2APIProperties)
	{
		useV1 = false;
		this.viatorV2APIProperties = viatorV2APIProperties;
	}
	
	public ViatorV2HttpService(ViatorV2APIProperties viatorV2APIProperties, Boolean useV1)
	{
		this.viatorV2APIProperties = viatorV2APIProperties;
	}
	
	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		httpMessage.setHeader("exp-api-key", viatorV2APIProperties.apikey);
		httpMessage.setHeader("Accept", "application/json;version=2.0");
		httpMessage.setHeader("Accept-Encoding", "gzip, deflate");
		httpMessage.setHeader("Accept-Language", "en");
		httpMessage.setHeader("Content-Type", "application/json");
	}

	@Override
	protected String getUrl()
	{
		if ( useV1)
			return viatorV2APIProperties.viatorV1URL;
		else
			return viatorV2APIProperties.viatorV2URL;
	}
}
