package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Countries 
{
	@Data
	public static class Country
	{
		@JsonProperty("code")
		private String code;

		@JsonProperty("name_short")
		private String name_short;

		@JsonProperty("name_full")
		private String name_full;
	}

	@JsonProperty("status")
	private String status;

	@JsonProperty("countries")
	private List<Country> countries;
}
