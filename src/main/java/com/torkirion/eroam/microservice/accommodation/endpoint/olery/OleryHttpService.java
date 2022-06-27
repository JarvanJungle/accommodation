package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import org.apache.http.HttpMessage;

import com.torkirion.eroam.HttpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OleryHttpService extends HttpService
{
	private String url;
	
	public OleryHttpService(String url)
	{
		this.url = url;
	}

	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		httpMessage.setHeader("Accept", "application/json");
		httpMessage.setHeader("Content-Type", "application/json; charset=UTF-8");
	}

	@Override
	protected String getUrl()
	{
		return url;
	}
}
