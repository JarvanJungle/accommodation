package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccommodationResult implements Serializable
{
	@ApiModelProperty(notes = "The property summary details")
	private AccommodationProperty property;

	@ApiModelProperty(notes = "The distance from the centre of the search")
	private BigDecimal distancefromCentrepoint;

	@ApiModelProperty(notes = "If this hotel has rooms from the IMS")
	private Boolean hasIMS;

	@ApiModelProperty(notes = "All rooms available")
	private SortedSet<RoomResult> rooms = new TreeSet<>();
}
