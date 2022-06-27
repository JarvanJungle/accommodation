package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import org.apache.http.HttpMessage;

import com.torkirion.eroam.HttpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YalagoHttpService extends HttpService
{
	private YalagoAPIProperties yalagoAPIProperties;

	public YalagoHttpService(YalagoAPIProperties yalagoAPIProperties)
	{
		this.yalagoAPIProperties = yalagoAPIProperties;
	}

	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		httpMessage.setHeader("Accept", "application/json");
		httpMessage.setHeader("Content-Type", "application/json; charset=UTF-8");
		httpMessage.setHeader("X-Api-Key", yalagoAPIProperties.getApikey());
	}

	@Override
	protected String getUrl()
	{
		return yalagoAPIProperties.getUrl();
	}
}
