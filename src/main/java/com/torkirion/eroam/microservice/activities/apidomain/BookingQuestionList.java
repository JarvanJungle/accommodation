package com.torkirion.eroam.microservice.activities.apidomain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookingQuestionList extends BookingQuestion
{
	private List<String> validValues = new ArrayList<>();

	private Boolean freeFormatAllowed;

	private String freeFormatValue;
}
