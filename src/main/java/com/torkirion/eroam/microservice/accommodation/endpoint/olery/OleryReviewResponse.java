package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
public class OleryReviewResponse
{
	@Data
	public static class Opinion
	{
		@JsonProperty("topic")
		private String topic;

		@JsonProperty("title")
		private String title;

		@JsonProperty("label")
		private String label;

		@JsonProperty("positive_opinions")
		private Integer positiveOpinions;

		@JsonProperty("negative_opinions")
		private Integer negativeOpinions;

		@JsonProperty("neutral_opinions")
		private Integer neutralOpinions;

		@JsonProperty("review_count")
		private Integer reviewCount;

		@JsonProperty("opinions_count")
		private Integer opinionsCount;

		@JsonProperty("sentiment_score")
		private BigDecimal sentimentScore;

		@JsonProperty("positive_percentage")
		private BigDecimal positivePercentage;
	}

	@Data
	public static class Rating
	{
		@JsonProperty("value")
		private BigDecimal value;

		@JsonProperty("topic")
		private String topic;

		@JsonProperty("title")
		private String title;

		@JsonProperty("count")
		private Integer count;

		@JsonProperty("review_count")
		private Integer reviewCount;

		@JsonProperty("subratings")
		private Map<String, Rating> subratings;
	}

	@Data
	public static class CountryRating
	{
		@JsonProperty("label")
		private String label;

		@JsonProperty("overall")
		private Rating overall;

		@JsonProperty("service")
		private Rating service;

		@JsonProperty("cleanliness")
		private Rating cleanliness;

		@JsonProperty("facilities")
		private Rating facilities;

		@JsonProperty("location")
		private Rating location;

		@JsonProperty("room")
		private Rating room;

		@JsonProperty("value")
		private Rating value;

		@JsonProperty("ambiance")
		private Rating ambiance;

		@JsonProperty("food")
		private Rating food;

		@JsonProperty("friendliness")
		private Rating friendliness;
	}

	@Data
	public static class CompositionRating
	{

	}

	@Data
	public static class ReviewData
	{
		@JsonProperty("company_id")
		private Long companyId;

		@JsonProperty("name")
		private String name;

		@JsonProperty("gei")
		private BigDecimal gei;

		@JsonProperty("review_count")
		private Integer reviewCount;

		@JsonProperty("opinions")
		private Map<String, Opinion> opinions;

		@JsonProperty("ratings")
		private Map<String, Rating> ratings;

		@JsonProperty("country_ratings")
		private Map<String, CountryRating> countryRatings;

		@JsonProperty("composition_ratings")
		private Map<String, CompositionRating> compositionRatings;
	}

	@JsonProperty("data")
	private ReviewData data;
}
