package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.BookContentDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.PreBookResultDTO;
import lombok.Data;

@Data
public class InnstantBookRS
{
	private BookContentDTO content;
	private String status;
	private String errorCode;
	private String errorMessage;
}
