package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.BookingRQ.Guest;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.BookingRS.BookedRoom;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.Board;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.BoardBasis;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.Establishment;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.InfoItem;

import lombok.Data;
import lombok.ToString;

@Data
public class YalagoGetBookingRS extends CommonRQRS
{
	@JsonProperty("BookingRef")
	private String bookingRef;

	@JsonProperty("Status")
	private Integer status;
	// 2 = good, 3 = fail, 4 = OK?

	@JsonProperty("Establishment")
	private Establishment establishment;

	@JsonProperty("Rooms")
	private List<BookedRoom> rooms;

	@JsonProperty("InfoItems")
	private List<InfoItem> infoItems;

	@JsonProperty("AffilateRef") // DELIBERATE TYPO!
	private String affiliateRef;

	@JsonProperty("CheckInDate")
	private String checkInDate;

	@JsonProperty("CheckOutDate")
	private String checkOutDate;
}
