package com.torkirion.eroam.microservice.activities.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AvailSearchByGeocordBoxRQDTO extends AvailSearchRQDTO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private LatitudeLongitude northwest;

	private LatitudeLongitude southeast;
}
