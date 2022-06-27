package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ViatorResult.LineItem;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ViatorResult.PriceBlock;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoldBookingResponse extends ViatorResult
{
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Availability
	{
		@JsonProperty("status")
		private String status;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Pricing
	{
		@JsonProperty("status")
		private String status;

		@JsonProperty("validUntil")
		private String validUntil;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BookingHoldInfo
	{
		@JsonProperty("availability")
		private Availability availability;

		@JsonProperty("pricing")
		private Pricing pricing;

		@JsonProperty("numberOfTravelers")
		private Integer numberOfTravelers = 0;
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonProperty("travelDate")
	private String travelDate;

	private String bookingRef;

	private BookingHoldInfo bookingHoldInfo;

	@JsonProperty("lineItems")
	private List<LineItem> lineItems = new ArrayList<>();

	@JsonProperty("totalPrice")
	private PriceBlock totalPrice;
}
