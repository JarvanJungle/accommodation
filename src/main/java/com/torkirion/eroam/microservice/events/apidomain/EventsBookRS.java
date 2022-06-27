package com.torkirion.eroam.microservice.events.apidomain;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EventsBookRS extends Booking implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Data
	public static class ResponseItem
	{
		@ApiModelProperty(notes = "The booking reference from the called system for this item", required = true)
		private String bookingItemReference;

		@ApiModelProperty(notes = "A reference from the calling system for this item", required = true)
		private String internalItemReference;
		
		@ApiModelProperty(notes = "The source channel of this item, from the Room=>Source field in search results", example = "IMS", required = true)
		private String channel;

		@ApiModelProperty(notes = "Any remark returned by the booking system particularly for this item", required = false)
		private String itemRemark;

		@ApiModelProperty(notes = "The booking status of this iterm", required = true)
		private ItemStatus itemStatus;
	}

	@JsonIgnore
	private List<ResponseExtraInformation> errors = new ArrayList<>();

	@JsonIgnore
	private List<ResponseExtraInformation> warnings = new ArrayList<>();

	@ApiModelProperty(notes = "The list of items (rooms) booked", required = true)
	private List<ResponseItem> items = new ArrayList<ResponseItem>();

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "The booking reference from the called system for this booking", required = true)
	private String bookingReference;

	@ApiModelProperty(notes = "Any remarks for this booking returned by the supplier", required = true)
	private List<String> remarks = new ArrayList<>();
}
