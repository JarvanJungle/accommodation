package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AccommodationRateCheckRQ extends AvailSearchRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8121974676256854733L;

	@ApiModelProperty(notes = "The channel field from the AccommodationResult Room structure", required = true)
	private String channel;

	@ApiModelProperty(notes = "The unique code for the hotel", required = true)
	private String hotelId;

	@ApiModelProperty(notes = "The bookingCode field from the AccommodationResult Room structure", required = true)
	private List<String> bookingCodes;

}
