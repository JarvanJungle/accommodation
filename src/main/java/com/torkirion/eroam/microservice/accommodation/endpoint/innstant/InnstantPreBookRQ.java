package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.SearchCodes;
import lombok.Data;

import java.util.List;

@Data
public class InnstantPreBookRQ
{
	private List<SearchCodes> searchCodes;
	private InnstantAvailabilityRQ searchRequest;
}
