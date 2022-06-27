package com.torkirion.eroam.microservice.accommodation.endpoint.olery;


import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
public class OleryMappingResponse 
{
	@Data
	public static class Match
	{
		@JsonProperty("id")
		private Long id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("latitude")
		private BigDecimal latitude;

		@JsonProperty("longitude")
		private BigDecimal longitude;

		@JsonProperty("city")
		private String city;

		@JsonProperty("country_code")
		private String countryCode;

		@JsonProperty("trip_advisor_id")
		private String tripAdvisorId;

		@JsonProperty("google_place_id")
		private String googlePlaceId;

		@JsonProperty("expedia_id")
		private String expediaId;

		@JsonProperty("ean_id")
		private String eanId;
	}

	@Data
	public static class MappingData
	{
		@JsonProperty("best_match")
		private Match bestMatch;
	}

	@JsonProperty("data")
	private MappingData data;
}
