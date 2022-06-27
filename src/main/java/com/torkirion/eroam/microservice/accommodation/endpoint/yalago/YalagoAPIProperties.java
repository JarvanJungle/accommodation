package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import lombok.Data;

@Data
public class YalagoAPIProperties
{
	String url;

	String apikey;

	boolean bypassBooking = false;

	int connectionTimeout = 60; // seconds
}