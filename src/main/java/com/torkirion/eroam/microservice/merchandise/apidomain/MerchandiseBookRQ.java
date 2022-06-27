package com.torkirion.eroam.microservice.merchandise.apidomain;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.Traveller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class MerchandiseBookRQ extends AbstractRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The country Code of origin.  Where possible, reviews and rankings will be based on country of origin", required = false, example = "AU")
	private String countryCodeOfOrigin;

	@ApiModelProperty(notes = "The contact details of the person making the booking", required = true)
	private Booker booker = new Booker();

	@ApiModelProperty(notes = "The items to be booked. Note that multiple items should be for the same hotel, for the same checkin and checkout dates", required = true)
	private Set<MerchandiseBookRQ.MerchandiseRequestItem> items = new HashSet<MerchandiseBookRQ.MerchandiseRequestItem>();

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ToString
	@Data
	public static class Booker
	{
		@ApiModelProperty(notes = "The title of the booking contact", example = "Mr", required = true)
		String title;

		@ApiModelProperty(notes = "The given (or first) name of the booking contact", example = "John", required = true)
		String givenName;

		@ApiModelProperty(notes = "The family name (or surname) name of the booking contact", example = "Smith", required = true)
		String surname;

		@ApiModelProperty(notes = "The telephone numnber of the booking contact", example = "0414555666", required = false)
		String telephone;
	}

	@ToString
	@Data
	public static class MerchandiseRequestItem
	{
		@ApiModelProperty(notes = "A reference from the calling system for this item", required = true)
		private String internalItemReference;

		@ApiModelProperty(notes = "The source channel of this item", example = "LOCALIMS", required = true)
		private String channel;
		
		@ApiModelProperty(notes = "The merchandise id", example = "IM123456", required = true)
		private String merchandiseId;

		@ApiModelProperty(notes = "The option id", example = "318764", required = true)
		private String optionId;

		@ApiModelProperty(notes = "The number of items to buy")
		private Integer count;

		@ApiModelProperty(notes = "The net rate.  Used to verify that the rate has not changed between the time of search, and booking")
		private CurrencyValue supplyRate;
	}
}
