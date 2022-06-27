package com.torkirion.eroam.ims.apidomain;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Transportation extends TransportationUpdate
{
	@Data
	public static class Segment
	{
		private Long id;
		
		private Integer segmentNumber;

		private Boolean passportRequired = false;

		private String departureAirportLocationCode;

		private String departureTerminal = "";

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
		private LocalTime departureTime;

		private String arrivalAirportLocationCode;

		private String arrivalTerminal = "";

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
		private LocalTime arrivalTime;

		private Integer arrivalDayExtra = 0;

		private Integer flightDurationMinutes;

		private String marketingAirlineCode;

		private String marketingAirlineFlightNumber;

		private String operatingAirlineCode;

		private String operatingAirlineFlightNumber;
		
		@JsonFormat(pattern="yyyy-MM-dd HH:mm")
		private LocalDateTime lastUpdated;
	}

	@Data
	public static class TransportationClass
	{
		private Long id;
		
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
		
		@JsonFormat(pattern="yyyy-MM-dd HH:mm")
		private LocalDateTime lastUpdated;
	}

	private String fromIata;

	private String toIata;

	private List<TransportationClass> classes = new ArrayList<>();

	private List<Segment> segments = new ArrayList<>();
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
