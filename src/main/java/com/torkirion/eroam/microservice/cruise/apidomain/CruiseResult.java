package com.torkirion.eroam.microservice.cruise.apidomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CruiseResult implements Serializable
{
	private Integer cruiseId;
	@ApiModelProperty(notes = "The cruise line")
	private CruiseLine cruiseLine;

	@ApiModelProperty(notes = "The ship", example="MS Maud")
	private Ship ship;

	@ApiModelProperty(example = "P&O Cruises")
	private String operator;

	@ApiModelProperty(notes = "The cruise name", example = "4 night Caribbean Cruise")
	private String cruiseTitle;

	@ApiModelProperty(notes = "The cruise itinerary as a map")
	private Image itineraryMap;

	@ApiModelProperty(notes = "The cruise iinerary as a map")
	private String itineraryDescription;

	private List<String> inclusions;

	private List<CruisePort> portsOfCall;

	private Integer durationNights;

	@ApiModelProperty(notes = "The ports this departs from")
	private List<String> exclusions;
	
	@ApiModelProperty(notes = "The departures (optionally with availability and pricing)")
	private List<CruiseDeparture> departures;

	private List<ItineraryItem> itineraries;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	private BigDecimal price;
}
