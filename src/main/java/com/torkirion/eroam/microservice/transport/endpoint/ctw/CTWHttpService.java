package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import org.apache.http.HttpMessage;

import com.torkirion.eroam.HttpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CTWHttpService extends HttpService
{
	private String url;
	private String token = null;
	private String callType = null;
	
	protected int getConnectionTimeout()
	{
		return 120;
	}

	public CTWHttpService(String url)
	{
		this.url= url;
	}
	
	public CTWHttpService(String url, String token, String callType)
	{
		this.url= url;
		this.token= token;
		this.callType= callType;
	}
	
	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		if ( token != null )
		{
			httpMessage.setHeader("token", token);
			httpMessage.setHeader("callType", callType);
			if (log.isDebugEnabled())
				log.debug("addHeaders::token = " + token + ", callType = " + callType);
		}
		httpMessage.setHeader("Accept", "application/json");
		httpMessage.setHeader("Content-Type", "application/json; charset=UTF-8");
	}

	@Override
	protected String getUrl()
	{
		return url;
	}
}
