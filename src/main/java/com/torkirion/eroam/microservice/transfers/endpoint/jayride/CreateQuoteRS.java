package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreateQuoteRS
{
	@Data
	public static class Luggage
	{
		@JsonProperty("inclusive_allowance")
		private String inclusive_allowance;
	}
	
	@Data
	public static class RefundPolicy
	{
		@JsonProperty("minute_prior")
		private Integer minute_prior;

		@JsonProperty("percent")
		private BigDecimal percent;

		@JsonProperty("method")
		private String method;
	}

	@Data
	public static class Fare
	{
		@JsonProperty("price")
		private BigDecimal price;

		@JsonProperty("currency_code")
		private String currency_code;

		@JsonProperty("type")
		private String type;

		@JsonProperty("refund_cancellation_policy")
		private String refund_cancellation_policy;

		@JsonProperty("refund_policies")
		private List<RefundPolicy> refund_policies;
	}

	@Data
	public static class Supplier
	{
		@JsonProperty("id")
		private String id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("photo_url")
		private String photo_url;

		@JsonProperty("description")
		private String description;
	}

	@Data
	public static class Reviews
	{
		@JsonProperty("count")
		private Integer count;

		@JsonProperty("average_rating")
		private BigDecimal average_rating;
	}

	@Data
	public static class ServiceInfo
	{
		@JsonProperty("type")
		private String type;

		@JsonProperty("photo_url")
		private String photo_url;

		@JsonProperty("photo_urls")
		private List<String> photo_urls;

		@JsonProperty("description")
		private String description;

		@JsonProperty("supplier")
		private Supplier supplier;

		@JsonProperty("passenger_reviews")
		private Reviews passenger_reviews;
	}

	@Data
	public static class Quote
	{
		@JsonProperty("quote_id")
		private String quote_id;

		@JsonProperty("status")
		private String status;

		@JsonProperty("service_info")
		private ServiceInfo service_info;

		@JsonProperty("fare")
		private Fare fare;

		@JsonProperty("luggage")
		private Luggage luggage;

		@JsonProperty("expire_datetime_utc")
		private String expire_datetime_utc;

		@JsonProperty("quote_url")
		private String quote_url;

		@JsonProperty("book_url")
		private String book_url;
	}

	@Data
	public static class Results
	{

		@JsonProperty("quotes")
		private List<Quote> quotes;
	}

	@JsonProperty("quote_request")
	private CreateQuoteRQ quote_request;

	@JsonProperty("quote_request_datetime_utc")
	private String quote_request_datetime_utc;

	@JsonProperty("quote_request_id")
	private String quote_request_id;

	@JsonProperty("quote_request_url")
	private String quote_request_url;

	@JsonProperty("results")
	private Results results;

	@JsonProperty("message")
	private String message;

	@JsonProperty("status")
	private String status;
}
