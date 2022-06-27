package com.torkirion.eroam.microservice.transport.dto;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.transport.apidomain.AvailTransportSearchRQ;
import com.torkirion.eroam.microservice.transport.apidomain.AvailTransportSearchRQ.Route;
import com.torkirion.eroam.microservice.transport.apidomain.TransportType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
public class AvailTransportSearchRQDTO
{
	@Data
	public static class Route
	{
		@ApiModelProperty(notes = "flight, ferry, rail", required = false)
		private TransportType transportType;

		@ApiModelProperty(notes = "ENUM (F,J,C,S,Y)", required = false)
		private String transportClass;

		private String departureIata;

		private LatitudeLongitude departureNorthwest;

		private LatitudeLongitude departureSoutheast;

		private String arrivalIata;

		private LatitudeLongitude arrivalNorthwest;

		private LatitudeLongitude arrivalSoutheast;
		
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate travelDate;

		private String flight;
	}

	private String client = "eroam";
	
	private List<Route> route = new ArrayList<>();

	@ApiModelProperty(notes = "The number of adults, and the child ages (of children < 12")
	private TravellerMix travellers;

	private Integer transportCallType;

	private String currency;
}
