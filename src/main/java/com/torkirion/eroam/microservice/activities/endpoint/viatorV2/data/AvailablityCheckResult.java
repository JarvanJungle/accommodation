package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailablityCheckResult extends ViatorResult
{
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BookableItem
	{
		@JsonProperty("startTime")
		private String startTime;

		@JsonProperty("available")
		private Boolean available;

		@JsonProperty("lineItems")
		private List<LineItem> lineItems = new ArrayList<>();

		@JsonProperty("totalPrice")
		private PriceBlock totalPrice;
	}

	private String currency;

	private String productCode;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonProperty("travelDate")
	private String travelDate;

	private String productOptionCode;

	private List<BookableItem> bookableItems = new ArrayList<>();
}
