package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DetailsRQ
{
	@Data
	public static class Room
	{
		@JsonProperty("Adults")
		private Integer Adults;

		@JsonProperty("ChildAges")
		private List<Integer> childAges;

		@JsonProperty("RoomCode")
		private String roomCode;

		@JsonProperty("BoardCode")
		private String boardCode;
	}

	@JsonProperty("CheckInDate")
	private String checkInDate;

	@JsonProperty("CheckOutDate")
	private String checkOutDate;

	@JsonProperty("EstablishmentId")
	private Integer establishmentId;

	@JsonProperty("Culture")
	private String culture = "en-GB";

	@JsonProperty("Rooms")
	private List<Room> rooms = new ArrayList<>();

	@JsonProperty("GetPackagePrice")
	private Boolean getPackagePrice = true;

	@JsonProperty("IsFlightPlus")
	private Boolean isFlightPlus = false;

	@JsonProperty("SourceMarket")
	private String sourceMarket;

	@JsonProperty("GetLocalCharges")
	private Boolean getLocalCharges = true;

	@JsonProperty("GetErrataCategory")
	private Boolean getErrataCategory = true;

	@JsonProperty("GetBoardBasis")
	private Boolean getBoardBasis = true;

	@JsonProperty("GetTaxBreakdown")
	private Boolean getTaxBreakdown = true;

	@JsonProperty("AllowOnRequest")
	private Boolean allowOnRequest = false;
}
