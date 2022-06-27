package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreateQuoteRQ
{
	@Data
	public static class Location
	{
		@JsonProperty("type")
		private String type;

		@JsonProperty("description")
		private String description;

		@JsonProperty("lat")
		private BigDecimal latitude;

		@JsonProperty("lng")
		private BigDecimal longitide;
	}

	@Data
	public static class Flight
	{
		@JsonProperty("landing_datetime_local")
		private String landing_datetime_local;

		@JsonProperty("departure_datetime_local")
		private String departure_datetime_local;

		@JsonProperty("lat")
		private BigDecimal latitude;

		@JsonProperty("lng")
		private BigDecimal longitide;
	}
	
	@Data
	public static class Passenger
	{

		@JsonProperty("count")
		private Integer count;
	}

	@JsonProperty("from_location")
	private Location from_location;

	@JsonProperty("to_location")
	private Location to_location;

	@JsonProperty("include_return_trip")
	private Boolean include_return_trip;

	@JsonProperty("flight")
	private Flight flight;

	@JsonProperty("passenger")
	private Passenger passenger;
}
