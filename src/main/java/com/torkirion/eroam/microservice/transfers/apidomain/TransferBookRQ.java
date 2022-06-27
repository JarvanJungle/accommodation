package com.torkirion.eroam.microservice.transfers.apidomain;

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
public class TransferBookRQ extends AbstractRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The contact details of the person making the booking", required = true)
	private Booker booker = new Booker();;

	@ApiModelProperty(notes = "The items to be booked. Note that multiple items should be for the same 'set' of one-way and optionally a return", required = true)
	private Set<TransferBookRQ.TransferRequestItem> items = new HashSet<TransferBookRQ.TransferRequestItem>();

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "A list of details of the customers on this booking.  Note that the order is important, as this array is accessed with an index during booking", required = true)
	private List<Traveller> travellers = new ArrayList<Traveller>();

	@ToString
	@Data
	public static class SpecialRequest
	{
		@ApiModelProperty(notes = "Some channels support coded special requests.  Not currently used", required = false)
		String code;

		@ApiModelProperty(notes = "A text value for this special request", required = true)
		String value;
	}

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

		@ApiModelProperty(notes = "The email of the booking contact", example = "fred@gmail.com", required = false)
		String email;
	}

	@ToString
	@Data
	public static class FlightDetail
	{
		@ApiModelProperty(notes = "The flight number", required = false)
		String flightNumber;
	}

	@ToString
	@Data
	public static class TransferRequestItem
	{
		@ApiModelProperty(notes = "A reference from the calling system for this item", required = true)
		private String internalItemReference;

		@ApiModelProperty(notes = "The source channel of this item, from the Room=>Channel field in search results", example = "YALAGO", required = true)
		private String channel;
		
		@ApiModelProperty(notes = "The booking code, from the bookingCode field in transfer search results")
		private String bookingCode;

		@ApiModelProperty(notes = "The arrival flight details, if picking up from an airport", required = false)
		private FlightDetail arrivalFlight;

		@ApiModelProperty(notes = "The departure flight details, if dropping off to an airport", required = false)
		private FlightDetail departureFlight;

		@ApiModelProperty(notes = "The net rate.  Used to verify that the rate has not changed between the time of search, and booking")
		private CurrencyValue supplyRate;

		@ApiModelProperty(notes = "A list of luggage requests for this item, e.g. 'A 6 ft surfboard and 1 extra check-in bag.'",  required = false)
		private Set<SpecialRequest> luggageSpecialRequests = new HashSet<SpecialRequest>();

		@ApiModelProperty(notes = "A list of general special requests for this item, e.g. 'I need a child booster seat and want to be at the airport 2 hours earlier for my flight.'", required = false)
		private Set<SpecialRequest> specialRequests = new HashSet<SpecialRequest>();

		@ApiModelProperty(notes = "The index into the array of travellers against this booking, to indicate which travellers are on/in this item (/room)")
		private Set<Integer> travellerIndex = new HashSet<Integer>();;
	}
}
