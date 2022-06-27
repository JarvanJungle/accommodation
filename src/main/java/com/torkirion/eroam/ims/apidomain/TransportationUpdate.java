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
public class TransportationUpdate
{
	private Long id;

	private String currency;

	private String rrpCurrency = "AUD";

	private String flight;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate scheduleFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate scheduleTo;

	private DaysOfTheWeek daysOfTheWeek;

	private String searchIataFrom;

	private String searchIataTo;

	private Boolean requiresPassport = false;

	private Boolean onRequest = false;
	
	private String supplier;
	
	private String bookingConditions;
}
