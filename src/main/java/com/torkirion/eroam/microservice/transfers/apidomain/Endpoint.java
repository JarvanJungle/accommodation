package com.torkirion.eroam.microservice.transfers.apidomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Endpoint
{
	@ApiModelProperty(notes = "The type of this endpoint")
	private EndpointType endpointType;

	@ApiModelProperty(notes = "For AIRPORT and HOTEL, the endpoint code returned from lookupEndpoint.  For FLIGHT, the full flight number (e.g. TG475)")
	private String endpointCode;
}