package com.torkirion.eroam.microservice.transport.endpoint;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class AvailTransportSearchRQ extends AbstractRQ implements Serializable
{
	public static class Passenger
	{
		@ApiModelProperty(notes = "Total number of adults", required = true)
		private Integer totalAdult;
		//@ApiModelProperty(notes = "Total number of children", required = true)
		//private Integer totalChild;
		@ApiModelProperty(notes = "Ages, e.g.  [[7]] - why is this a list of lists? Need clarification", required = true)
		private List<Integer> child;
		//@ApiModelProperty(notes = "Ages again?  Need clarification", required = true)
		//private String childsAge;
	}
	
	public static class Route
	{
		@ApiModelProperty(notes = "Start date of the transport request", required = false)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate check_in_date;
		
		@ApiModelProperty(required = false)
		private Integer city_id;
		
		@ApiModelProperty(required = false)
		private String city_name;
		
		@ApiModelProperty(required = false)
		private String country_code;
		
		@ApiModelProperty(required = false)
		private String country_name;
		
		@ApiModelProperty(required = false)
		private String default_nights;
		
		@ApiModelProperty(required = false)
		private String destination_city;
		
		@ApiModelProperty(required = false)
		private String destination_lat;
		
		@ApiModelProperty(required = false)
		private String destination_lng;
		
		@ApiModelProperty(required = false)
		private String source_lat;
		
		@ApiModelProperty(required = false)
		private String source_lng;
		
		@ApiModelProperty(notes = "The class of transport", required = true)
		private String transport_class;
		
		@ApiModelProperty(notes = "The type of transport", required = true)
		private String transport_type;
		
		@ApiModelProperty(notes = "The flight airline codes to filter", required = false)
		private List<String> flight_airlines;
		
		@ApiModelProperty(notes = "The start date of the transportation", required = true)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private String travel_date;
	}
	
	private static final long serialVersionUID = 6427209212711367007L;

	@ApiModelProperty(notes = "Allows limiting the results to be only from a given channel")
	private String channel;

	@ApiModelProperty(notes = "The country Code of origin.", required = false, example = "AU")
	private String countryCodeOfOrigin;

	@ApiModelProperty(notes = "The routes to select", required = true)
	private List<Route> route;

	@ApiModelProperty(notes = "The search engine to use", required = false)
	private String searchType;

	@ApiModelProperty(notes = "passengers", required = true)
	private List<Passenger> totalPassenger;

	@ApiModelProperty(notes = "Unknown", required = false)
	private Integer transport_call_type;
}
