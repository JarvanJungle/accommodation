package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AvailabilityRQ
{
	@Data
	public static class Room
	{
		@JsonProperty("Adults")
		private Integer Adults;

		@JsonProperty("ChildAges")
		private List<Integer> childAges;
	}

	@JsonProperty("CheckInDate")
	private String checkInDate;

	@JsonProperty("CheckOutDate")
	private String checkOutDate;

	@JsonProperty("ProvinceId")
	private Integer provinceId;

	@JsonProperty("LocationId")
	private Integer locationId;

	@JsonProperty("EstablishmentId")
	private Integer establishmentId;

	@JsonProperty("EstablishmentIds")
	private List<String> establishmentIds = new ArrayList<>();;

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

	@JsonProperty("GetTaxBreakdown")
	private Boolean getTaxBreakdown = true;

	@JsonProperty("IsMobile")
	private Boolean isMobile = false;

	@JsonProperty("AllowOnRequest")
	private Boolean allowOnRequest = false;

	@JsonProperty("GetLocalCharges")
	private Boolean getLocalCharges = true;

	@JsonProperty("GetBoardBasis")
	private Boolean getBoardBasis = true;
}
