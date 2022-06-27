package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpMessage;

@Slf4j
public class InnstantRCHttpSearchService extends HttpService
{
	private InnstantRCAPIProperties innstantRCAPIProperties;

	public InnstantRCHttpSearchService(InnstantRCAPIProperties innstantRCAPIProperties)
	{
		this.innstantRCAPIProperties = innstantRCAPIProperties;
	}
	
	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		httpMessage.setHeader("aether-access-token", "$2y$10$4LN64jA4dkQyblmbFQileeeMBJHIDxD.2yEzCKf1SJOVd.uF/xA3m");
		httpMessage.setHeader("Aether-application-key", "$2y$10$HA.1xpmZGRA6sb6koYmQ4eUhlh0gIH1nMYQBPpafw3nBqlDhAf26C");
		httpMessage.setHeader("Content-Type", "application/json");
	}

	@Override
	protected String getUrl()
	{
		return innstantRCAPIProperties.innstantRCSearch;
	}
}
