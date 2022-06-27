package com.torkirion.eroam.microservice.activities.apidomain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookingQuestion
{
	public static enum QuestionType
	{
		LIST, STRING, DATE, TIME, NUMBER
	}

	private String questionId;
	
	private String questionText;
	
	private QuestionType questionType;
	
	private Boolean perTraveller = false;
	
	private List<String> list;
}
