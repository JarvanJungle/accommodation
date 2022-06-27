package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YalagoCancelRQ extends CommonRQRS
{	@Data
	public static class ExpectedCharge
	{
		@JsonProperty("Charge")
		private Money charge;
	}

	@JsonProperty("BookingRef")
	private String bookingRef;

	@JsonProperty("ExpectedCharge")
	private ExpectedCharge expectedCharge;
}
