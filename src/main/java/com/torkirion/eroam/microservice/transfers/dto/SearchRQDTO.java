package com.torkirion.eroam.microservice.transfers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.transfers.apidomain.EndpointType;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferSearchRQ;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class SearchRQDTO 
{
	@Data
	public static class Endpoint
	{
		private EndpointType endpointType;

		private String endpointCode;
	}

	private String client;

	private String subclient;

	private Endpoint startPoint;

	private Endpoint endPoint;

	private Boolean includeReturn;

	private LocalDateTime flightArrivalTime;

	private LocalDateTime flightDepartureTime;

	private LocalDateTime pickupTime;

	private LocalDateTime returnPickupTime;
	
	private TravellerMix travellers;
	
	private String channel;
	
	private String supplierName;
}
