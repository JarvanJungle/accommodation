package com.torkirion.eroam.microservice.hirecars.apidomain;

import lombok.Data;

@Data
public class LocationResult
{
	private String code;

	private String name;

	private String displayName;

	private LocationType type;

	private String parentCityCode;

	private String parentCityName;

	private String IATA;

	private String countryCode;

	private Boolean hasCars;

	private String state;

	private Double lat;

	private Double lng;

}
