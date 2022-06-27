package com.torkirion.eroam.microservice.activities.endpoint.hotelbeds;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AvailabilityRQ
{
	@Data
	public static class FilterItem
	{
		@JsonProperty("type")
		private String type;

		@JsonProperty("value")
		private String value;
	}

	@Data
	public static class Filter
	{
		@JsonProperty("searchFilterItems")
		private List<FilterItem> searchFilterItems;
	}

	@Data
	public static class Pagination
	{
		@JsonProperty("itemsPerPage")
		private Integer itemsPerPage;

		@JsonProperty("page")
		private Integer page;
	}

	@Data
	public static class Pax
	{
		@JsonProperty("age")
		private Integer age;

		@JsonProperty("page")
		private Integer page;
	}

	@JsonProperty("filters")
	private List<Filter> filters;

	@JsonProperty("from")
	private String from;

	@JsonProperty("to")
	private String to;

	@JsonProperty("paxes")
	private List<Pax> paxes;

	@JsonProperty("language")
	private String language = "en";

	@JsonProperty("pagination")
	private Pagination pagination;

	@JsonProperty("order")
	private String order;
}
