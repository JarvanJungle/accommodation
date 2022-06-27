package com.torkirion.eroam.microservice.activities.apidomain;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.Traveller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ActivityBookRQ extends AbstractRQ implements Serializable
{
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The country Code of origin.", required = false, example = "AU")
	private String countryCodeOfOrigin;

	@ApiModelProperty(notes = "The contact details of the person making the booking", required = true)
	private ActivityBooker booker = new ActivityBooker();;

	@ApiModelProperty(notes = "The items to be booked.", required = true)
	private Set<ActivityBookRQ.ActivityRequestItem> items = new HashSet<ActivityBookRQ.ActivityRequestItem>();

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "A list of details of the customers on this booking.  Note that the order is important, as this array is accessed with an index during booking", required = true)
	private List<Traveller> travellers = new ArrayList<Traveller>();

	@ToString
	@Data
	public static class ActivityBooker
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
	public static class ActivityRequestItem
	{
		@ApiModelProperty(notes = "A reference from the calling system for this item", required = true)
		private String internalItemReference;

		@ApiModelProperty(notes = "The source channel of this item", example = "VIATOR", required = true)
		private String channel;
		
		@ApiModelProperty(notes = "The activity id", example = "IM123456", required = true)
		private String activityId;
		
		@ApiModelProperty(notes = "The departure id", required = true)
		private String departureId;
		
		@ApiModelProperty(notes = "The option id", required = true)
		private String optionId;

		@ApiModelProperty(notes = "The activity date")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		@JsonProperty("date")
		private LocalDate date;

		@ApiModelProperty(notes = "Answers to the questions specified by the Activity Option", required = false)
		private List<BookingAnswers> bookingQuestionAnswers = new ArrayList<>();

		@ApiModelProperty(notes = "The net rate.  Used to verify that the rate has not changed between the time of search, and booking")
		private CurrencyValue supplyRate;

		@ApiModelProperty(notes = "The index into the array of travellers against this booking, to indicate which travellers are on/in this item (/room)")
		private Set<Integer> travellerIndex = new HashSet<Integer>();
	}
}
