package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
public class CommonRQRS
{
	@Data
	public static class InfoItem
	{
		@JsonProperty("ErrataCategory")
		private Integer errataCategory;

		@JsonProperty("Title")
		private String title;

		@JsonProperty("Description")
		private String description;
	}
	
	@Data
	public static class TaxBreakdown
	{
		@JsonProperty("Amount")
		private BigDecimal amount;

		@JsonProperty("VAT")
		private BigDecimal vat;
	}

	@Data
	@ToString
	public static class Money
	{
		@JsonProperty("Amount")
		private BigDecimal amount;

		@JsonProperty("Currency")
		private String currency;

		@JsonProperty("TaxBreakdown")
		private TaxBreakdown TaxBreakdown;
	}

	@Data
	public static class LocalChargesAmount
	{
		@JsonProperty("Amount")
		private BigDecimal amount;

		@JsonProperty("Currency")
		private String currency;
	}

	@Data
	public static class LocalCharge
	{
		@JsonProperty("Amount")
		private Money amount;

		@JsonProperty("Title")
		private String title;

		@JsonProperty("Description")
		private String description;

		@JsonProperty("IsPerBooking")
		private Boolean isPerBooking;
	}

	@Data
	public static class Inclusion
	{
		@JsonProperty("InclusionId")
		private Integer inclusionId;

		@JsonProperty("ReplacementText")
		private String replacementText;
	}

	@Data
	public static class CancellationCharge
	{
		@JsonProperty("Charge")
		private Money charge;

		@JsonProperty("ExpiryDate") // 2019-12-25T23:59:59
		private String ExpiryDate;

		@JsonProperty("ExpiryDateUTC") // 2019-12-25T23:59:59Z
		private String ExpiryDateUTC;
	}

	@Data
	public static class CancellationPolicy
	{
		@JsonProperty("CancellationCharges")
		private List<CancellationCharge> cancellationCharges;
	}

	@Data
	@ToString
	public static class ExtraOption
	{
		@JsonProperty("OptionId")
		private String optionId;

		@JsonProperty("Title")
		private String title;

		@JsonProperty("CustomerText")
		private String customerText;

		@JsonProperty("NetCost")
		private Money netCost;

		@JsonProperty("GrossCost")
		private Money grossCost;
	}

	@Data
	public static class Extra
	{
		@JsonProperty("ExtraId")
		private String extraId;

		@JsonProperty("Title")
		private String title;

		@JsonProperty("ExtraTypeId")
		private String extraTypeId;

		@JsonProperty("RequiresFlightDetails")
		private Boolean requiresFlightDetails;

		@JsonProperty("IsMandatory")
		private Boolean isMandatory;

		@JsonProperty("IsOpaque")
		private Boolean isOpaque;

		@JsonProperty("Options")
		private List<ExtraOption> options;

		@JsonProperty("IsBindingPrice")
		private Boolean isBindingPrice;
	}

	@Data
	public static class BoardBasis
	{
		@JsonProperty("Inclusions")
		private List<Inclusion> inclusions;

		@JsonProperty("Exclusions")
		private List<Inclusion> exclusions;

		@JsonProperty("SupplierBoardTypeId")
		private Integer supplierBoardTypeId;
	}

	@Data
	public static class Board
	{
		@JsonProperty("Type")
		private Integer type;

		@JsonProperty("Code")
		private String code;

		@JsonProperty("Description")
		private String description;

		@JsonProperty("GrossCost")
		private Money grossCost;

		@JsonProperty("NetCost")
		private Money netCost;

		@JsonProperty("NonRefundable")
		private Boolean nonRefundable;

		@JsonProperty("IsPayAtHotel")
		private Boolean isPayAtHotel;

		@JsonProperty("IsBindingPrice")
		private Boolean isBindingPrice;

		@JsonProperty("IsOnRequest")
		private Boolean isOnRequest;

		@JsonProperty("RequestedRoomIndex")
		private Integer requestedRoomIndex;

		@JsonProperty("CancellationPolicy")
		private CancellationPolicy cancellationPolicy;

		@JsonProperty("BookingConditions")
		private String bookingConditions;

		@JsonProperty("SpecialOfferText")
		private String specialOfferText;

		// Detail only
		@JsonProperty("LongSpecialOfferText")
		private String longSpecialOfferText;

		// Detail only
		@JsonProperty("GrossDiscount")
		private String grossDiscount;

		// Detail only
		@JsonProperty("PreOpaqueCost")
		private Money preOpaqueCost;

		@JsonProperty("Extras")
		private List<Extra> extras;

		@JsonProperty("IsPackagePrice")
		private Boolean isPackagePrice;

		// Detail only
		@JsonProperty("LocalCharges")
		private List<LocalCharge> localCharges;

		@JsonProperty("PresaleOfferBookableDate")
		private Boolean presaleOfferBookableDate;

		@JsonProperty("IsEstimatedContract")
		private Boolean isEstimatedContract;

		@JsonProperty("LocalChargesAmount")
		private LocalChargesAmount localChargesAmount;

		@JsonProperty("BoardBasis")
		private BoardBasis boardBasis;

		@JsonProperty("SupplierBoardTypeId")
		private Integer supplierBoardTypeId;
	}

	@Data
	public static class Room
	{
		@JsonProperty("Code")
		private String code;

		@JsonProperty("Description")
		private String description;

		@JsonProperty("QuantityAvailable")
		private Integer quantityAvailable;

		// for Details only
		@JsonProperty("SupplierId")
		private Integer supplierId;

		// for Details only
		@JsonProperty("SupplierName")
		private String supplierName;

		// for Details only
		@JsonProperty("IsDirectContract")
		private Boolean isDirectContract;

		@JsonProperty("Boards")
		private List<Board> boards;
	}

	@Data
	public static class Establishment
	{
		@JsonProperty("EstablishmentId")
		private Integer establishmentId;

		@JsonProperty("AcceptedCardTypes")
		private List<String> acceptedCardTypes;

		@JsonProperty("Rooms")
		private List<Room> rooms;
	}

	@JsonProperty("ErrorCode")
	private String errorCode;

	@JsonProperty("Message")
	private String message;

	@JsonProperty("ErrorMessage")
	private String errorMessage;

	@JsonProperty("ErrorId")
	private String errorId;
}
