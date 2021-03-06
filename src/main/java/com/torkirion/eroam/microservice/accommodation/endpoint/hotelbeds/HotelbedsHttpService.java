package com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds;

import org.apache.http.HttpMessage;

import com.torkirion.eroam.HttpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotelbedsHttpService extends HttpService
{
	private HotelbedsAPIProperties hotelbedsProperties;
	
	public HotelbedsHttpService(HotelbedsAPIProperties hotelbedsProperties)
	{
		this.hotelbedsProperties = hotelbedsProperties;
	}
	
	@Override
	protected void addHeaders(HttpMessage httpMessage)
	{
		// Signature is generated by SHA256 (Api-Key + Secret + Timestamp (in seconds))
		String hexkey = hotelbedsProperties.apikey + hotelbedsProperties.secret + System.currentTimeMillis() / 1000;
		String signature = org.apache.commons.codec.digest.DigestUtils.sha256Hex(hexkey);
		log.debug("addHeaders::submitting signature '" + signature + "' from hexKey '" + hexkey + "' and apikey/secret " + hotelbedsProperties.apikey + "/" + hotelbedsProperties.secret);

		httpMessage.setHeader("X-Signature", signature);
		httpMessage.setHeader("Api-Key", hotelbedsProperties.apikey);
		httpMessage.setHeader("Accept", "application/xml");
		httpMessage.setHeader("Accept-Encoding", "gzip, deflate");
		httpMessage.setHeader("Content-Type", "application/xml; charset=UTF-8");
	}

	@Override
	protected String getUrl()
	{
		return hotelbedsProperties.hotelbedsURL;
	}
}
