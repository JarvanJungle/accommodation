package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.PreBookResultDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.SearchCodes;
import lombok.Data;

import java.util.List;

@Data
public class InnstantPreBookRS
{
	private PreBookResultDTO content;
	private String status;
	private String errorCode;
	private String errorMessage;

}
