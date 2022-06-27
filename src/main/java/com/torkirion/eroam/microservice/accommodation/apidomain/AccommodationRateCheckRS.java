package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccommodationRateCheckRS 
{
	@ApiModelProperty(notes = "The property summary details")
	private AccommodationProperty property;

	@ApiModelProperty(notes = "All rooms available")
	private List<RoomResult> rooms = new ArrayList<>();
}
