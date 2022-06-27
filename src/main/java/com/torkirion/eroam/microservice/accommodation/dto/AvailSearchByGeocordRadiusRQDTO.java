package com.torkirion.eroam.microservice.accommodation.dto;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper=true)
public class AvailSearchByGeocordRadiusRQDTO extends AvailSearchRQDTO 
{
	private LatitudeLongitude geocoordinates;

	// radius in kilometers
	private Double radius;
}
