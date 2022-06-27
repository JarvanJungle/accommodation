package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AvailSearchRQ extends AbstractRQ implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6427209212711367007L;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate checkin;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate checkout;

	@ApiModelProperty(notes = "The number of adults, and the child ages (of children < 18")
	private List<TravellerMix> travellers;

	@ApiModelProperty(notes = "Allows limiting the results to be only from a given channel")
	private String channel;

	@ApiModelProperty(notes = "Channels that should NOT be used", required = false)
	private List<String> channelExceptions;
	
	@ApiModelProperty(notes = "The country Code of origin.  Where possible, reviews and rankings will be based on country of origin", required = false, example = "AU")
	private String countryCodeOfOrigin;
}
