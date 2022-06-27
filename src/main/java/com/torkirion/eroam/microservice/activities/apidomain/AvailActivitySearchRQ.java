package com.torkirion.eroam.microservice.activities.apidomain;

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
@ToString
public class AvailActivitySearchRQ extends AbstractRQ implements Serializable
{
	private static final long serialVersionUID = 6427209212711367007L;

	@ApiModelProperty(notes = "Optoinal filtering by name", required = false)
	private String nameFilter;

	@ApiModelProperty(notes = "The number of adults, and the child ages (of children < 18")
	private TravellerMix travellers;

	@ApiModelProperty(notes = "Allows limiting the results to be only from a given channel")
	private String channel;

	@ApiModelProperty(notes = "The country Code of origin.", required = false, example = "AU")
	private String countryCodeOfOrigin;
}
