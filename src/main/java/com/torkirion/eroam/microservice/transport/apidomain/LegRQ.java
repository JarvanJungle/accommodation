package com.torkirion.eroam.microservice.transport.apidomain;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.TravellerSummary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class LegRQ
{
	@ApiModelProperty(notes = "An optional code which may be required by the channel provider.", required = false)
	private String bookingCode;

	@ApiModelProperty(notes = "The departure IATA airport code", example = "MEL", required = true)
	private String departureIata;

	@ApiModelProperty(notes = "The arrival IATA airport code", example = "SYD", required = true)
	private String arrivalIata;

	@ApiModelProperty(notes = "The cabin class", example = "ECONOMY", required = true)
	private CabinClass transportClass;

	@ApiModelProperty(notes = "The type of transportation", example = "flight", required = true)
	private TransportType transportType;

	@ApiModelProperty(notes = "The date and time of departure", required = true, example="2025-06-20T13:50")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime travelDateTime;

	@ApiModelProperty(notes = "The marketing airline code", example = "QF", required = true)
	private String airlineCode;

	@ApiModelProperty(notes = "The marketing airline number", example = "12", required = true)
	private String flightNumber;

	@ApiModelProperty(notes = "The fare basis code", example = "Y", required = true)
	private String fareBasis;

	@ApiModelProperty(notes = "The fare source, such as 'webfare', 'public' or 'private'.  If missing or empty, assumes public", example = "private", required = false)
	private String fareType;

	@ApiModelProperty(notes = "Only used for RateChecks.  Whether the channel can return a higher priced option in the same cabin class if the requested fareBasis is unavailable", example = "Y", required = false)
	private Boolean allowFarebasisUpgrades;

	@ApiModelProperty(notes = "If allowFarebasisUpgrades=true, then if the required fareBasis is unavailable, rateCheck will return the next cheapest fare higher than this rate.  This ensures that we do NOT offer a more restrictive fare.  This field is ONLY used for rateCheck calls (not booking).")
	private CurrencyValue rate;

}
