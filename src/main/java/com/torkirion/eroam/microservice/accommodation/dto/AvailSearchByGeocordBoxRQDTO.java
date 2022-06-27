package com.torkirion.eroam.microservice.accommodation.dto;

import java.io.Serializable;
import java.math.BigInteger;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AvailSearchByGeocordBoxRQDTO extends AvailSearchRQDTO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private LatitudeLongitude northwest;

	private LatitudeLongitude southeast;
	
	private LatitudeLongitude distanceCentrepoint;
	
	private BigInteger kilometerFilter;
}
