package com.torkirion.eroam.microservice.activities.endpoint.hotelbeds;

import com.hotelbeds.activities.api.ApiException;
import com.hotelbeds.activities.model.AvailabilityByHotelResponse;
import com.hotelbeds.activities.model.AvailabilitybyhotelcodeRequest;
import com.hotelbeds.activities.model.BookingConfirmRequest;
import com.hotelbeds.activities.model.BookingResponse;
import com.hotelbeds.activities.model.DetailSimpleRequest;
import com.hotelbeds.activities.model.DetailSimpleResponse;
import com.torkirion.eroam.microservice.util.HotelBedsUtil;
import org.springframework.stereotype.Service;

import com.hotelbeds.activities.api.ActivitiesApi;
import com.hotelbeds.activities.api.ApiClient;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotelBedsClient
{
	private ActivityHBAPIProperties hbapiProperties;
	private static ApiClient apiClient = new ApiClient();

	public HotelBedsClient(ActivityHBAPIProperties hbapiProperties)
	{
		if ( log.isDebugEnabled())
			log.debug("HotelBedsClient::hbapiProperties=" + hbapiProperties);
		this.hbapiProperties = hbapiProperties;
	}

	public ActivitiesApi getActivitiesApi(String client)
	{
		ActivitiesApi activitiesApi = new ActivitiesApi(getApiClient(hbapiProperties.basePathActivityBooking, client));
		return activitiesApi;
	}

	public AvailabilityByHotelResponse availabilitybyhotelcode(String client, AvailabilitybyhotelcodeRequest availabilityRQ) throws ApiException {
		return getActivitiesApi(client).availabilitybyhotelcode (
				hbapiProperties.apikey, getXSignature(), hbapiProperties.accept, hbapiProperties.acceptEncoding, availabilityRQ);
	}

	public DetailSimpleResponse detail(String client, DetailSimpleRequest detailRQ) throws ApiException {
		return getActivitiesApi(client).detailSimple(
				hbapiProperties.apikey, getXSignature(), hbapiProperties.accept, hbapiProperties.acceptEncoding, detailRQ);
	}

	public BookingResponse bookingConfirm(String client, BookingConfirmRequest bookingConfirmRequest) throws ApiException {
		return getActivitiesApi(client).bookingConfirm(
				hbapiProperties.apikey, getXSignature(), hbapiProperties.accept, hbapiProperties.acceptEncoding, bookingConfirmRequest);
	}

	public BookingResponse bookingCancel(String client, String language, String bookingReference) throws ApiException {
		return getActivitiesApi(client).bookingCancel(hbapiProperties.apikey, getXSignature(),
				hbapiProperties.accept, hbapiProperties.acceptEncoding, language, bookingReference, hbapiProperties.cancellationFlag);
	}

	public ApiClient getApiClient(String basePath, String client)
	{
		String url = hbapiProperties.url;
		if (url != null)
		{
			
			// when the api Client is okhttp-gson 
			if ( log.isDebugEnabled())
				log.debug("getApiClient::basePath=" + basePath + ", url=" + url);
			apiClient.setBasePath(hbapiProperties.url + basePath);
			apiClient.setDebugging(true);
			apiClient.setConnectTimeout(60000);
			apiClient.setReadTimeout(60000);
			apiClient.setWriteTimeout(60000);
			
			// when api Client is java.net.http.HttpClient 
			//int index = url.indexOf("://");
			//url = url.substring(index + 3);
			//apiClient.setScheme("https");
			//apiClient.setHost(url);
			//apiClient.setBasePath(basePath);
		}
		return apiClient;
	}


	private String getXSignature() {
		String xSignature = HotelBedsUtil.getXSignature(hbapiProperties.apikey, hbapiProperties.secret); 
		if ( log.isDebugEnabled())
			log.debug("getXSignature::xSignature=" + xSignature);
		return  xSignature;
	}
}
