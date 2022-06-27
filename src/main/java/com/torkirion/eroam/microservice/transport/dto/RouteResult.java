package com.torkirion.eroam.microservice.transport.dto;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.apidomain.DaysOfTheWeek;
import com.torkirion.eroam.ims.apidomain.Transportation;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.transport.apidomain.AvailTransportSearchRQ.Route;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
public class RouteResult
{
	@Data
	public static class Segment
	{
		private Integer segmentNumber;

		private String departureAirportLocationCode;

		private String departureTerminal;

		private LocalDateTime departureDateTime;

		private String arrivalAirportLocationCode;

		private String arrivalTerminal;

		private LocalDateTime arrivalDateTime;

		private Integer arrivalDayExtra;

		private Integer flightDurationMinutes;

		private Boolean passportRequired = false;

		private String marketingAirlineCode;

		private String marketingAirlineFlightNumber;

		private String operatingAirlineCode;

		private String operatingAirlineFlightNumber;
	}

	@Data
	public static class TransportationClass
	{
		private String reference;

		private String classCode;

		private String classDescription;

		private Integer baggageMaxWeight;

		private Integer baggageMaxPieces;

		private Boolean refundable;

		private BigDecimal adultNett;

		private BigDecimal adultRrp;

		private BigDecimal childNett;

		private BigDecimal childRrp;

		private String bookingCode;
	}

	private String currency;

	private String rrpCurrency;

	private String flight;

	private LocalDateTime departureDateTime;

	private LocalDateTime arrivalDateTime;

	private String searchIataFrom;

	private String searchIataTo;
	
	private String fromIata;

	private String toIata;

	private String bookingConditions;

	private List<TransportationClass> classes = new ArrayList<>();

	private List<Segment> segments = new ArrayList<>();
}
