package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
public class OleryReviewSummaryResponse
{
	@Data
	public static class ReviewMap
	{
		private Map<String,Object> summaries;
	}
	
	@Data
	public static class ReviewSummary
	{
		@JsonProperty("rating")
		private BigDecimal rating;

		@JsonProperty("review_count")
		private Integer review_count;

		@JsonProperty("language")
		private String language;

		@JsonProperty("sentiment")
		private String sentiment;

		@JsonProperty("topic")
		private List<String> topic;

		@JsonProperty("count_delta")
		private BigDecimal count_delta;

		@JsonProperty("text")
		private String text;

		@JsonProperty("slug")
		private String slug;
	}
	
	@JsonProperty("property_id")
	private Long property_id;
	
	@JsonProperty("template_name")
	private String templateName;
	
	@JsonProperty("data")
	private List<Map<String,Object>> data;
}
