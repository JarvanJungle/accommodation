package com.torkirion.eroam.microservice.cruise.dto;

import java.time.LocalDate;
import java.util.*;

import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class SearchRQDTO 
{
	private String client;
	
	private LocalDate departureMonth;

	private Integer durationNights;

//	private Integer durationNightsTo;

	private String cruiseLine;

	private String destination;

	private List<TravellerMix> travellers;

	private String channel;

	private List<String> channelExceptions;
	
	private String countryCodeOfOrigin;

	private String locationId;

}
