package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ViatorResult
{
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	public static class Price
	{
		@JsonProperty("recommendedRetailPrice")
		private BigDecimal recommendedRetailPrice;

		@JsonProperty("partnerNetPrice")
		private BigDecimal partnerNetPrice;

		@JsonProperty("bookingFee")
		private BigDecimal bookingFee;

		@JsonProperty("partnerTotalPrice")
		private BigDecimal partnerTotalPrice;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	public static class PriceBlock
	{
		@JsonProperty("price")
		private Price price;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	public static class LineItem
	{
		@JsonProperty("ageBand")
		private String ageBand;

		@JsonProperty("numberOfTravelers")
		private Integer numberOfTravelers;

		@JsonProperty("subtotalPrice")
		private PriceBlock subtotalPrice;
	}

}
