package com.torkirion.eroam.microservice.accommodation.endpoint.sabrecsl;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsAPIProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpMessage;

@Slf4j
public class SabreHttpService extends HttpService
{
	private SabreCSLAPIProperties sabreCSLAPIProperties;

	public SabreHttpService(SabreCSLAPIProperties sabreCSLAPIProperties)
	{
		this.sabreCSLAPIProperties = sabreCSLAPIProperties;
	}
	
	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		httpMessage.setHeader("Content-Type", "text/xml; charset=UTF-8");
	}

	@Override
	protected String getUrl()
	{
		return sabreCSLAPIProperties.sabreURL;
	}
}
