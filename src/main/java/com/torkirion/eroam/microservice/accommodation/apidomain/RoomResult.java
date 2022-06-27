package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoomResult implements Comparable<RoomResult>, Serializable
{
	@ApiModelProperty(notes = "The source channel of this item", example = "YALAGO", required = true)
	private String channel;

	@ApiModelProperty(notes = "The unique code of this property for the source channel", example = "YL123456")
	private String channelPropertyCode;

	@ApiModelProperty(notes = "The full name of this room/rate", example = "Superior room, Lunch and Dinner", required = true)
	private String roomName;

	@ApiModelProperty(notes = "For multi-room requests, the index into the traveller set, starting at '1'", example = "1", required = true)
	private Integer roomNumber;

	@ApiModelProperty(notes = "For multi-room requests, if this room is chosen, the other rooms chosen must have the same multiRoomMatchCode", required = true)
	private String multiRoomMatchCode = "0";

	@ApiModelProperty(notes = "The unique code for this room.  May be an index into static room content in 'loadRichContent'")
	private String roomCode;

	@ApiModelProperty(notes = "The unique rate/board code for this offer. Generally not useful by itself")
	private String rateCode;

	@ApiModelProperty(notes = "Calls at different times may return different codes for the same room/rate (see Hotelbeds).  This code will allow 'matching' of rooms/rates across calls seperated in time. Useful to be able to store an quote and re-check prices later")
	private String matchCode;

	@ApiModelProperty(notes = "The full code that should be passed back at time of booking")
	private String bookingCode;

	@ApiModelProperty(notes = "The board or meal code - FB/HB/BB")
	private String boardCode;

	@ApiModelProperty(notes = "The board or meal description based on the code")
	private String boardDescription;

	@ApiModelProperty(notes = "The bedding, e.g. double, twin")
	private String bedding;

	@ApiModelProperty(notes = "The room standard: e.g. dormitory, standard, double")
	private String roomStandard;

	@ApiModelProperty(notes = "Any additional information about this room.  e.g. For Yalago, it may be lengthy descriptions of included extras")
	private String roomExtraInformation = "";

	@ApiModelProperty(notes = "The gross price a consumer pays for this offer")
	private CurrencyValue totalRate;

	@ApiModelProperty(notes = "The nett price that will be charged for this offer")
	private CurrencyValue supplyRate;

	@ApiModelProperty(notes = "Sometimes totalRate may NOT be discounted / marked up for B2C")
	private Boolean rrpIsMandatory = false;

	@ApiModelProperty(notes = "If available, will show the number of rooms left for this offer", required = false)
	private BigInteger inventory;

	@ApiModelProperty(notes = "If a 'rateCheck' call must be made prior to booking this room")
	private Boolean requiresRecheck;

	@ApiModelProperty(notes = "Any extra fees associated with this offer")
	private Set<RoomExtraFee> extraFees = new HashSet<>();

	@ApiModelProperty(notes = "Any included special promotions attached to the offer")
	private Set<RoomPromotion> promotions = new HashSet<>();

	@ApiModelProperty(notes = "Machine readable cancellation policy")
	private SortedSet<RoomCancellationPolicyLine> cancellationPolicy;

	@ApiModelProperty(notes = "Human readable cancellation policy")
	private String cancellationPolicyText;

	@ApiModelProperty(notes = "Extra conditions to do with the booking, often extra to the cancellation policy")
	private String bookingConditions;

	@ApiModelProperty(notes = "If this room rate should ONLY be used for packaging, never shown directly to a customer or agent")
	private Boolean bundlesOnly = false;

	@Override
	public int compareTo(RoomResult other)
	{
		if (supplyRate.getAmount().compareTo(other.getSupplyRate().getAmount()) == 0)
		{
			return (roomNumber + bookingCode).compareTo(other.getRoomNumber() + other.getBookingCode());
		}
		else
			return supplyRate.getAmount().compareTo(other.getSupplyRate().getAmount());
	}
}
