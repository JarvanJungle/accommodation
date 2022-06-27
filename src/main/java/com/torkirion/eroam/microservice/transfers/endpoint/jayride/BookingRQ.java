package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BookingRQ
{
	@Data
	public static class Communication
	{
		@JsonProperty("primary_contact")
		private String primary_contact = "agent";
	}

	@Data
	public static class Luggage
	{
		@JsonProperty("extra_items")
		private String extra_items;
	}

	@Data
	public static class Agent
	{
		@JsonProperty("name")
		private String name;

		@JsonProperty("phone")
		private String phone;

		@JsonProperty("email")
		private String email;
	}

	@Data
	public static class Passenger
	{
		@JsonProperty("name")
		private String name;

		@JsonProperty("mobile")
		private String mobile;

		@JsonProperty("email")
		private String email;
	}

	@Data
	public static class Flight
	{
		@JsonProperty("departure_flight_number")
		private String departure_flight_number;

		@JsonProperty("landing_flight_number")
		private String landing_flight_number;
	}

	@JsonProperty("quote_id")
	private String quote_id;

	@JsonProperty("quote_request_id")
	private String quote_request_id;

	@JsonProperty("flight")
	private Flight flight;

	@JsonProperty("passenger")
	private Passenger passenger;

	@JsonProperty("agent")
	private Agent agent;
	
	@JsonProperty("communication")
	private Communication communication = new Communication();
	
	@JsonProperty("luggage")
	private Luggage luggage;
	
	@JsonProperty("additional_notes")
	private String additional_notes;
}
