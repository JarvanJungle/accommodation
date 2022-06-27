package com.torkirion.eroam.microservice.events.apidomain;


import java.util.SortedSet;

import com.torkirion.eroam.ims.apidomain.Address;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationProperty;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomResult;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Venue
{
	private Integer id;

	private String externalVenueId;

	private String name;

	private Address address;

	private String overview;

	private String imageUrl;

	private String defaultSeatmapImageUrl;
}
