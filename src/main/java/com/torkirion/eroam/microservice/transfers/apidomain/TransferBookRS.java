package com.torkirion.eroam.microservice.transfers.apidomain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransferBookRS extends Booking implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Data
	public static class ResponseItem
	{
		@ApiModelProperty(notes = "The booking reference from the called system for this item", required = true)
		private String bookingItemReference;

		@ApiModelProperty(notes = "A reference from the calling system for this item", required = true)
		private String internalItemReference;
		
		@ApiModelProperty(notes = "The source channel of this item, from the Room=>Source field in search results", example = "YALAGO", required = true)
		private String channel;

		@ApiModelProperty(notes = "Any comments returned by the booking system", required = false)
		private String comments;

		@ApiModelProperty(notes = "The booking status of this iterm", required = true)
		private ItemStatus itemStatus;

		//@ApiModelProperty(notes = "The date of the transfer", required = true)
		//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		//private LocalDate transferDate;

		//@ApiModelProperty(notes = "The description of the transfer", required = true)
		//private String itemName;
	}

	@JsonIgnore
	private List<ResponseExtraInformation> errors = new ArrayList<>();

	@JsonIgnore
	private List<ResponseExtraInformation> warnings = new ArrayList<>();

	@ApiModelProperty(notes = "The list of items (rooms) booked", required = true)
	private Set<ResponseItem> items = new HashSet<ResponseItem>();

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "The booking reference from the called system for this booking", required = true)
	private String bookingReference;
}
