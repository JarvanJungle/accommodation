package com.torkirion.eroam.microservice.activities.apidomain;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivityBookRS extends ActivityBooking implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Data
	public static class ActivityResponseItem
	{
		@ApiModelProperty(notes = "The booking reference from the called system for this item", required = true)
		private String bookingItemReference;

		@ApiModelProperty(notes = "A reference from the calling system for this item", required = true)
		private String internalItemReference;
		
		@ApiModelProperty(notes = "The source channel of this item, from the Room=>Source field in search results", example = "VIATOR", required = true)
		private String channel;

		@ApiModelProperty(notes = "Any remark returned by the booking system particularly for this item", required = false)
		private String itemRemark;

		@ApiModelProperty(notes = "An optional URL of a voucher that MUST be presented to the agent/customer, and they MUST open this URL, print the voucher and give to the operator", required = false)
		private String itemVoucherURL;

		@ApiModelProperty(notes = "The booking status of this iterm", required = true)
		private ItemStatus itemStatus;
		
		@ApiModelProperty(notes = "Answers to the questions specified by the Activity Option", required = true)
		private List<BookingAnswers> bookingQuestionAnswers = new ArrayList<>();
	}

	@JsonIgnore
	private List<ResponseExtraInformation> errors = new ArrayList<>();

	@JsonIgnore
	private List<ResponseExtraInformation> warnings = new ArrayList<>();

	@ApiModelProperty(notes = "The list of items (activities) booked", required = true)
	private List<ActivityResponseItem> items = new ArrayList<>();

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "The booking reference from the called system for this booking", required = true)
	private String bookingReference;

	@ApiModelProperty(notes = "Any remarks for this booking returned by the supplier", required = true)
	private List<String> remarks = new ArrayList<>();
}
