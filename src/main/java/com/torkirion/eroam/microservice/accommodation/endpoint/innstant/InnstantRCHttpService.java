package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpMessage;

@Slf4j
public class InnstantRCHttpService extends HttpService
{
	private InnstantRCAPIProperties innstantRCAPIProperties;

	public InnstantRCHttpService(InnstantRCAPIProperties innstantRCAPIProperties)
	{
		this.innstantRCAPIProperties = innstantRCAPIProperties;
	}
	
	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		httpMessage.setHeader("aether-access-token", innstantRCAPIProperties.getAether_access_token());
		httpMessage.setHeader("Aether-application-key", innstantRCAPIProperties.getAether_application_key());
		httpMessage.setHeader("mishor-application-key", innstantRCAPIProperties.getMishor_application_key());
		httpMessage.setHeader("Content-Type", "application/json");
	}

	@Override
	protected String getUrl()
	{
		return innstantRCAPIProperties.innstantRCStatic;
	}
}
