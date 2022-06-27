package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BookingRS
{
	@Data
	public static class Supplier
	{
		@JsonProperty("name")
		private String name;

		@JsonProperty("description")
		private String description;

		@JsonProperty("email")
		private String email;

		@JsonProperty("phone")
		private String phone;

		@JsonProperty("id")
		private String id;
	}

	@Data
	public static class ServiceInfo
	{
		@JsonProperty("supplier")
		private Supplier supplier;

	}

	@Data
	public static class Instructions
	{
		@JsonProperty("meeting_instructions")
		private String meetingInstructions;

	}

	@Data
	public static class Booking
	{
		@JsonProperty("booking_id")
		private String booking_id;

		@JsonProperty("booking_status")
		private String booking_status;

		@JsonProperty("instructions")
		private Instructions instructions;

		@JsonProperty("service_info")
		private ServiceInfo service_info;
	}

	@JsonProperty("quote_id")
	private String quote_id;

	@JsonProperty("bookings")
	private List<Booking> bookings;

	@JsonProperty("status")
	private String status;

	@JsonProperty("message")
	private String message;

}
