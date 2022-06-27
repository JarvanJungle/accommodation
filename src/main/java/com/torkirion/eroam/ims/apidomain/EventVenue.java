package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventVenue
{
	private Integer id;

	private String externalVenueId;

	private String name;

	private Address address;

	private String overview;

	private String imageUrl;

	private String defaultSeatmapImageUrl;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
