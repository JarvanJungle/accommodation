package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import org.apache.http.HttpMessage;

import com.torkirion.eroam.HttpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JayrideHttpService extends HttpService
{
	private JayrideAPIProperties jayrideProperties;
	
	public JayrideHttpService(JayrideAPIProperties jayrideProperties)
	{
		this.jayrideProperties = jayrideProperties;
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
		return jayrideProperties.jayrideURL;
	}
}
