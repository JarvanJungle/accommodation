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
public class TransportRateCheckRQ extends AbstractRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The source channel to check", example = "SABRE", required = true)
	private String channel;

	@ApiModelProperty(notes = "The items to be checked. Note that multiple items should be for the same 'set' of one-way and optionally a return", required = true)
	private Set<SegmentRQ> segments;

	@ApiModelProperty(notes = "A list of details of the customers on this booking.  Note that the order is important, as this array is accessed with an index during booking", required = true)
	private List<TravellerSummary> travellers;

	@ApiModelProperty(notes = "Only used for RateChecks.  Whether the channel can return a higher priced option in the same cabin class if the requested fareBasis is unavailable", example = "Y", required = false)
	private Boolean allowFarebasisUpgrades;

	@ApiModelProperty(notes = "If allowFarebasisUpgrades=true, then if the required fareBasis is unavailable, rateCheck will return the next cheapest fare higher than this rate.  This ensures that we do NOT offer a more restrictive fare.  This field is ONLY used for rateCheck calls (not booking).")
	private CurrencyValue rate;

	@ApiModelProperty(notes = "1 = one-way, 2 = return", required = false)
	private Integer transport_call_type;
}
