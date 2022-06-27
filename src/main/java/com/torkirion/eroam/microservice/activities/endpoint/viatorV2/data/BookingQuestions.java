package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookingQuestions implements Serializable
{
	@Data
	public static class BookingQuestion
	{
		@JsonProperty("legacyBookingQuestionId")
		private Integer legacyBookingQuestionId;

		@JsonProperty("id")
		private String id;
		
		@JsonProperty("type")
		private String type;
		
		@JsonProperty("group")
		private String group;
		
		@JsonProperty("label")
		private String label;
		
		@JsonProperty("hint")
		private String hint;
		
		@JsonProperty("units")
		private List<String> units;
		
		@JsonProperty("allowedAnswers")
		private List<String> allowedAnswers;
		
		@JsonProperty("required")
		private String required;
		
		@JsonProperty("maxLength")
		private Integer maxLength;
	}
	
	@JsonProperty("bookingQuestions")
	private List<BookingQuestion> bookingQuestions;
}
