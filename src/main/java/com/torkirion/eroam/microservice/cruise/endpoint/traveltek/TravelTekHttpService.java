package com.torkirion.eroam.microservice.cruise.endpoint.traveltek;

import com.torkirion.eroam.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpMessage;

@Slf4j
public class TravelTekHttpService extends HttpService
{
	private TravelTekProperties travelTekProperties;

	public TravelTekHttpService(TravelTekProperties travelTekProperties)
	{
		this.travelTekProperties = travelTekProperties;
	}

	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		httpMessage.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	}

	@Override
	protected String getUrl()
	{
		return travelTekProperties.getApiUrl();
	}

	@Override
	public String doCallPost(String callType, Object requestData) {
		return super.doCallPost(callType, String.format("xml=%s", requestData));
	}

}
