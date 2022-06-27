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
public class BookingRS extends CommonRQRS
{
	@Data
	public static class BookedRoom
	{
		@JsonProperty("BookRoomId")
		private Integer bookRoomId;

		@JsonProperty("AffiliateRoomRef")
		private String affiliateRoomRef;

		@JsonProperty("Description")
		private String description;

		@JsonProperty("Board")
		private String board;

		@JsonProperty("RoomCode")
		private String roomCode;

		@JsonProperty("BoardCode")
		private String boardCode;

		@JsonProperty("ProviderRef")
		private String providerRef;

		@JsonProperty("ProviderName")
		private String providerName;

		@JsonProperty("Guests")
		private List<Guest> guests;

		@JsonProperty("GrossCost")
		private Money grossCost;

		@JsonProperty("NetCost")
		private Money netCost;

		@JsonProperty("SpecialRequests")
		private String specialRequests;

		@JsonProperty("NonRefundable")
		private Boolean nonRefundable;

		@JsonProperty("IsBindingPrice")
		private Boolean isBindingPrice;

		@JsonProperty("LocalCharges")
		private List<LocalCharge> localCharges;
		
		@JsonProperty("BoardBasis")
		private BoardBasis boardBasis;
	}

	@JsonProperty("BookingRef")
	private String bookingRef;

	@JsonProperty("Status")
	private Integer status;
	// 2 = good, 3 = fail

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

	@JsonProperty("ErrorCode")
	private String errorCode;

	@JsonProperty("Message")
	private String message;
}
