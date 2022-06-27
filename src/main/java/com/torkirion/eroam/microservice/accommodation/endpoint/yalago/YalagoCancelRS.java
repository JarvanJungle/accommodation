package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.BookingRQ.Guest;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.Board;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.BoardBasis;

import lombok.Data;
import lombok.ToString;

@Data
public class YalagoCancelRS extends CommonRQRS
{
	@JsonProperty("BookingRef")
	private String bookingRef;

	@JsonProperty("Status")
	private Integer status;

	@JsonProperty("Messages")
	private String messages;
}
