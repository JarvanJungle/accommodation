package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YalagoGetBookingRQ extends CommonRQRS
{
	@JsonProperty("BookingRef")
	private String bookingRef;
}
