package com.torkirion.eroam.microservice.accommodation.apidomain.eroam;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HotelBaseRQ implements Serializable
{
	private static final long serialVersionUID = 6427209212711367007L;

	@ApiModelProperty(notes = "client, determines channel parameters (api keys etc), default = 'eroam'")
	private String client = "eroam";
	
	@ApiModelProperty(notes = "The required channel", required = false)
	private String channel;
	
	@ApiModelProperty(notes = "Channels that should NOT be used", required = false)
	private List<String> channelExceptions;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate checkin;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate checkout;

	@ApiModelProperty(notes = "The northwest (upper left) corner of a bounding box for a search")
	private LatitudeLongitude northwest;

	@ApiModelProperty(notes = "The southeast (lower right) corner of a bounding box for a search")
	private LatitudeLongitude southeast;

	@ApiModelProperty(notes = "An override point to find the 'distance' of a hotel, if not specified will use the centre of the box/circle.", required = false)
	private LatitudeLongitude distanceCentrepoint;

	@ApiModelProperty(notes = "The maximum distance from the centre point a hotel can be to appear in the results", required = false)
	private BigInteger kilometerFilter = BigInteger.valueOf(200);

	@ApiModelProperty(notes = "The number of adults, and the child ages (of children < 18")
	private List<TravellerMix> travellers;

	@ApiModelProperty(notes = "The country Code of origin.  Where possible, reviews and rankings will be based on country of origin", required = false, example = "AU")
	private String countryCodeOfOrigin;
	
	@ApiModelProperty(notes = "Star rating of the hotel, e.g. 4.0. If there are no 4.0 hotels, then everythign beneath that is shown", required = false, example = "3.5")
	private BigDecimal accommodation_rating;
	
}
