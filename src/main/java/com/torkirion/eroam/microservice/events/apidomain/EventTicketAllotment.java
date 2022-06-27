package com.torkirion.eroam.microservice.events.apidomain;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationProperty;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomResult;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EventTicketAllotment
{
	private Integer allotmentId;

	private String name;

	private Integer allotment;
	
	private Boolean onRequest;
	
	private List<EventTicketClassification> eventTicketClassifications = new ArrayList<>();
}
