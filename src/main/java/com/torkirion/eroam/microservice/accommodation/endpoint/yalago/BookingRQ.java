package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BookingRQ extends CommonRQRS
{
	@Data
	public static class Guest
	{
		@JsonProperty("Title")
		private String title;

		@JsonProperty("FirstName")
		private String firstName;

		@JsonProperty("LastName")
		private String lastName;

		@JsonProperty("Age")
		private Integer age;
	}

	@Data
	public static class PaymentMethod
	{
		@JsonProperty("CardNumber")
		private String cardNumber;

		@JsonProperty("CardType")
		private String cardType;

		@JsonProperty("SecurityCode")
		private String securityCode;

		@JsonProperty("IssueNumber")
		private String issueNumber;

		@JsonProperty("StartMonth")
		private Integer startMonth;

		@JsonProperty("StartYear")
		private Integer startYear;

		@JsonProperty("EndMonth")
		private Integer endMonth;

		@JsonProperty("EndYear")
		private Integer endYear;
	}

	@Data
	public static class ContactDetails
	{
		@JsonProperty("Title")
		private String title;

		@JsonProperty("FirstName")
		private String firstName;

		@JsonProperty("LastName")
		private String lastName;

		@JsonProperty("DateOfBirth") // yyyy-mm-dd
		private String dateOfBirth;

		@JsonProperty("Address1")
		private String address1;

		@JsonProperty("Address2")
		private String address2;

		@JsonProperty("Address3")
		private String address3;

		@JsonProperty("Town")
		private String town;

		@JsonProperty("County")
		private String county;

		@JsonProperty("PostCode")
		private String postCode;

		@JsonProperty("Country")
		private String country;

		@JsonProperty("EmailAddress")
		private String emailAddress;

		@JsonProperty("HomeTel")
		private String homeTel;

		@JsonProperty("MobileTel")
		private String mobileTel;

		@JsonProperty("WorkTel")
		private String workTel;

	}

	@Data
	public static class BookedRoomExtra
	{
		@JsonProperty("ExtraId")
		private Integer extraId;

		@JsonProperty("OptionId")
		private Integer OptionId;

		@JsonProperty("ExpectedNetCost")
		private Money ExpectedNetCost;

		@JsonProperty("ExpectedCost")
		private Money ExpectedCost;
	}

	@Data
	public static class Room
	{
		@JsonProperty("AffiliateRoomRef")
		private String affiliateRoomRef;

		@JsonProperty("AffiliateRef")
		private String affiliateRef;

		@JsonProperty("RoomCode")
		private String roomCode;

		@JsonProperty("BoardCode")
		private String boardCode;

		@JsonProperty("ExpectedCost")
		private Money expectedCost;

		@JsonProperty("ExpectedNetCost")
		private Money expectedNetCost;

		@JsonProperty("Guests")
		private List<Guest> Guests = new ArrayList<>();

		@JsonProperty("SpecialRequests")
		private String specialRequests;

		@JsonProperty("Extras")
		private List<BookedRoomExtra> extras = new ArrayList<>();
	}

	@JsonProperty("AffiliateRef")
	private String affiliateRef;

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

	@JsonProperty("ContactDetails")
	private ContactDetails contactDetails;

	@JsonProperty("PaymentMethod")
	private PaymentMethod paymentMethod;

	@JsonProperty("GetPackagePrice")
	private Boolean getPackagePrice = true;

	@JsonProperty("IsFlightPlus")
	private Boolean isFlightPlus = false;

	@JsonProperty("IsMobile")
	private Boolean isMobile = false;

	@JsonProperty("SourceMarket")
	private String sourceMarket;

	@JsonProperty("GetTaxBreakdown")
	private Boolean getTaxBreakdown = true;

	@JsonProperty("GetLocalCharges")
	private Boolean getLocalCharges = true;

	@JsonProperty("GetErrataCategory")
	private Boolean getErrataCategory = true;

	@JsonProperty("GetBoardBasis")
	private Boolean getBoardBasis = true;

	@JsonProperty("AllowOnRequest")
	private Boolean allowOnRequest = false;
}
