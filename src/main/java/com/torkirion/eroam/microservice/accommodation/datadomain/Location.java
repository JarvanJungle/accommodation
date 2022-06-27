package com.torkirion.eroam.microservice.accommodation.datadomain;

import java.io.Serializable;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.datadomain.Country;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Location implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String locationid;

	private String locationName;

	private String searchName;

	private LatitudeLongitude northWest;

	private LatitudeLongitude southEast;

	private Country country;
}
