package com.torkirion.eroam.microservice.transport.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class AvailTransportSearchRQ extends AbstractRQ implements Serializable
{
	@Data
	public static class LatitudeLongitudeBoundingBox
	{
		@ApiModelProperty(position = 1, notes = "The northwest (upper left) corner of a bounding box for a search")
		private LatitudeLongitude northwest;

		@ApiModelProperty(notes = "The southeast (lower right) corner of a bounding box for a search")
		private LatitudeLongitude southeast;
	}
	
	@Data
	public static class TotalPassenger
	{
		private Integer totalAdult;
		
		private Integer totalChild;
		
		private List<List<Integer>> child;
		
		@ApiModelProperty(required = false)
		private String childsAge;
		
	}
	@Data
	public static class Route
	{
		@ApiModelProperty(notes = "flight, ferry, rail", required = false)
		private String transportType;

		@ApiModelProperty(notes = "ENUM (P,F,J,C,S,Y)", required = false)
		private String transportClass;

		private String departureIata;

		@ApiModelProperty(notes = "either departureIata or departureLatitudeLongitude should be specified.  For flights, use departureIata, for rail, use departureLatitudeLongitude", required = false)
		private LatitudeLongitudeBoundingBox departureLatitudeLongitude;

		private String arrivalIata;

		@ApiModelProperty(notes = "either arrivalIata or arrivalLatitudeLongitude should be specified.  For flights, use arrivalIata, for rail, use arrivalLatitudeLongitude", required = false)
		private LatitudeLongitudeBoundingBox arrivalLatitudeLongitude;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate travelDate;
	}

	private String currency;

	private List<Route> route;

	@ApiModelProperty(notes = "unused", required = false)
	private String searchType;

	@ApiModelProperty(notes = "unused", required = false)
	private Integer store_id;

	@ApiModelProperty(notes = "1 = one-way, 2 = return", required = false)
	private Integer transport_call_type;
	
	private TotalPassenger totalPassenger;
}
