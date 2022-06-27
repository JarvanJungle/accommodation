package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Airports 
{
	@Data
	public static class Terminal
	{
		@JsonProperty("id")
		private String id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("lat")
		private BigDecimal latitude;

		@JsonProperty("lng")
		private BigDecimal longitude;
	}

	@Data
	public static class Airport
	{
		@JsonProperty("iata")
		private String iata;

		@JsonProperty("name")
		private String name;

		@JsonProperty("name_alias")
		private List<String> name_alias;

		@JsonProperty("country_code")
		private String country_code;

		@JsonProperty("terminals")
		private List<Terminal> terminals;
	}

	@JsonProperty("status")
	private String status;

	@JsonProperty("airports")
	private List<Airport> airports;
}
