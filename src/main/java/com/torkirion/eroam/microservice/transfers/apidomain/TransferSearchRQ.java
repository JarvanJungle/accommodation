package com.torkirion.eroam.microservice.transfers.apidomain;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.ToString;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;

@Data
@ToString
public class TransferSearchRQ extends AbstractRQ implements Serializable
{
	@ApiModelProperty(notes = "The start point of the transfer")
	private Endpoint startPoint;

	@ApiModelProperty(notes = "The end point of the transfer")
	private Endpoint endPoint;

	@ApiModelProperty(notes = "If two-way (retuen) transfers are requested")
	private Boolean includeReturn;

	@ApiModelProperty(notes = "If startPoint is a FLIGHT or AIRPORT, or return is specified and endPoint is FLIGHT or AIRPORT, the time the flight lands")
	private LocalDateTime flightArrivalTime;

	@ApiModelProperty(notes = "If endPoint is a FLIGHT or AIRPORT, or return is specified and startPoint is FLIGHT or AIRPORT, the time the flight departs")
	private LocalDateTime flightDepartureTime;

	@ApiModelProperty(notes = "If start end end points are NOT AIRPORT or FLIGHT, the date and time of the requested pickup")
	private LocalDateTime pickupTime;

	@ApiModelProperty(notes = "If start end end points are NOT AIRPORT or FLIGHT, and includeReturn is true, the date and time of the requested return pickup")
	private LocalDateTime returnPickupTime;
	
	@ApiModelProperty(notes = "The number of adults, and the child ages (of children < 18")
	private TravellerMix travellers;
	
	@ApiModelProperty(notes = "Allows limiting the results to be only from a given channel")
	private String channel;

	@ApiModelProperty(notes = "Allows filtering by Suppplier Name")
	private String supplierName;

}
