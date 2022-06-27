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
public class HireCarDetailRQ extends AbstractRQ implements Serializable
{
	private static final long serialVersionUID = 6427209212711367017L;

	@ApiModelProperty(notes = "The channel field from the Search structure", required = true)
	private String channel;
	
	@ApiModelProperty(notes = "The vehicle Id from the search", required = true)
	private String vehicleId;
}
