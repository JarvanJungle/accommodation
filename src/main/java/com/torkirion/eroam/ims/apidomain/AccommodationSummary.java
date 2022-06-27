package com.torkirion.eroam.ims.apidomain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class AccommodationSummary
{
	private String hotelId;

	private String city;

	private String state;

	private String country;

	private String accommodationName;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
