package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.BookingRQ.Guest;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.Board;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.BoardBasis;

import lombok.Data;
import lombok.ToString;

@Data
public class YalagoCancelChargeRS extends CommonRQRS
{
	@Data
	public static class Charge
	{
		@JsonProperty("Charge")
		private Money charge;
	}

	@JsonProperty("IsCancellable")
	private Boolean IsCancellable;

	@JsonProperty("Charge")
	private Charge Charge;

	@JsonProperty("ExpiryDate")
	private String expiryDate;

	@JsonProperty("ExpiryDateUTC")
	private String expiryDateUTC;
}
