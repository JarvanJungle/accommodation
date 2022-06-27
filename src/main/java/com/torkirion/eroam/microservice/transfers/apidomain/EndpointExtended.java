package com.torkirion.eroam.microservice.transfers.apidomain;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EndpointExtended extends Endpoint
{
	@ApiModelProperty(notes = "The latlong of this place", required = true)
	private LatitudeLongitude geoCoordinates;
	
	@ApiModelProperty(notes = "The description of the endpoint (from the transfer supplier)", required = true)
	private String description;
	
	@ApiModelProperty(notes = "For a hotel, the address of the hotel.", required = false)
	private String hotelAddress;
}