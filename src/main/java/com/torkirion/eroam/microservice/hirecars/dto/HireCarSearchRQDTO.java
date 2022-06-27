package com.torkirion.eroam.microservice.hirecars.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.hirecars.apidomain.HireCarSearchRQ.BoundingBox;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class HireCarSearchRQDTO implements Serializable
{
	private String client;

	private String channel;
	
	@ApiModelProperty(notes = "Pickup location", required = true)
	private BoundingBox pickupLocation;

	@ApiModelProperty(notes = "Dropoff location, if different to pickup location", required = false)
	private BoundingBox dropoffLocation;

	@ApiModelProperty(notes = "Pickup date and time", required = true)
	private LocalDateTime pickupDateTime;

	@ApiModelProperty(notes = "Dropoff date and time", required = true)
	private LocalDateTime dropoffDateTime;

	@ApiModelProperty(notes = "Age of the driver", required = true)
	private Integer age;

	@ApiModelProperty(notes = "The country Code of origin.", required = false, example = "AU")
	private String customerCountryID = null;
}
