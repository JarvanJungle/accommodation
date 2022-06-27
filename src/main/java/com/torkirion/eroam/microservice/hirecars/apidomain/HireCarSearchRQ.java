package com.torkirion.eroam.microservice.hirecars.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class HireCarSearchRQ extends AbstractRQ implements Serializable
{
	private static final long serialVersionUID = 6427209212711367007L;
	
	@Data
	public static class BoundingBox
	{
		@ApiModelProperty(notes = "The northwest (upper left) corner of a bounding box for a search")
		private LatitudeLongitude northwest;

		@ApiModelProperty(notes = "The southeast (lower right) corner of a bounding box for a search")
		private LatitudeLongitude southeast;
	}
	
	@ApiModelProperty(notes = "Pickup location", required = true)
	private BoundingBox pickupLocation;

	@ApiModelProperty(notes = "Dropoff location, if different to pickup location", required = false)
	private BoundingBox dropoffLocation;

	@ApiModelProperty(notes = "Pickup date and time", required = true)
	private LocalDateTime pickupDateTime;

	@ApiModelProperty(notes = "Dropoff date and time", required = true)
	private LocalDateTime dropoffDateTime;

	@ApiModelProperty(notes = "Age of the driver", required = true)
	private Integer age;

	@ApiModelProperty(notes = "The country Code of origin.", required = false, example = "AU")
	private String customerCountryID = null;
}
