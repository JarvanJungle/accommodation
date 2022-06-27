package com.torkirion.eroam.microservice.activities.apidomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class BookingAnswers
{
	@ApiModelProperty(notes = "The question Id", required = true)
	private String questionId;

	@ApiModelProperty(notes = "The answer", required = true)
	private String answer;

	@ApiModelProperty(notes = "If the question is 'perTraveller', then the traeller index", required = false)
	private Integer travelerNum;
}