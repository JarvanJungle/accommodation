package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
public class AvailSearchSetRQ extends AvailSearchRQ implements Serializable
{
	@ApiModelProperty(notes = "A filter for accommodationName")
	private String nameFilter;

	private Integer resultsLimit = 0;

	@ApiModelProperty(notes = "A filter for star rating")
	private List<BigDecimal> starsFilter;

	@ApiModelProperty(notes = "An override point to find the 'distance' of a hotel, if not specified will use the centre of the box/circle.", required = false)
	private LatitudeLongitude distanceCentrepoint;
	
	private BigInteger kilometerFilter;

}
