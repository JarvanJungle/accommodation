package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Address;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OleryAccommodationData implements Serializable
{
	@Data
	@ToString
	public static class CountryScores implements Comparable<CountryScores>, Serializable
	{
		private String country;

		private BigInteger percent;

		@Override
		public int compareTo(CountryScores o)
		{
			if (percent.compareTo(o.percent) == 0)
				return country.compareTo(o.country);
			else
				return percent.compareTo(o.percent);
		}
	}

	@ApiModelProperty(notes = "The Olery rating for this property, but onh if there are enough reviews!", required = false)
	private BigDecimal rating;

	@ApiModelProperty(notes = "The number of Olery ratings for this property", required = false)
	private BigInteger reviewCount;

	@ApiModelProperty(notes = "The Olery rating for this property for the country of origin, but only if there are enough reviews!", required = false)
	private BigDecimal originCountryRating;

	@ApiModelProperty(notes = "The number of Olery ratings for this property for the country of origin", required = false)
	private BigInteger originCountryReviewCount;

	@ApiModelProperty(notes = "if this rating is specifically for the country of origin specificed in the reauest", required = false)
	private Boolean ratingIsCountrySpecific;

	@ApiModelProperty(notes = "The Olery category ratings for this property", required = false)
	private Map<String, OleryCategoryRating> categoryRatings;

	@ApiModelProperty(notes = "Review snippets from Olery", required = false)
	private List<String> reviewText;

	@ApiModelProperty(notes = "Most popular nationalities staying here", required = false)
	private SortedSet<CountryScores> countryScores;

	@JsonIgnore
	// used to indicate an null data object, useful for caching!
	private Boolean isNull = false;
}
