package com.torkirion.eroam.microservice.events.apidomain;

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
public class EventRateCheckRQ extends AvailEventSearchRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8121974676256854733L;

	@ApiModelProperty(notes = "The channel field from the Event Room structure", required = true)
	private String channel;

	@ApiModelProperty(notes = "The unique code for the event", required = true)
	private String eventId;

	@ApiModelProperty(notes = "The ticket classifiction", required = true)
	private String classificationId;
	
	@ApiModelProperty(notes = "The number of tickets to buy")
	private Integer numberOfTickets;
}
