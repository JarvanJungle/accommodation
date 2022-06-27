package com.torkirion.eroam.microservice.transport.apidomain;

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
import com.torkirion.eroam.microservice.apidomain.DocumentedTraveller;
import com.torkirion.eroam.microservice.apidomain.Traveller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class TransportBookRQ extends AbstractRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The channel field from the Search structure", required = true, example = "CTW")
	private String channel;
	
	@ApiModelProperty(notes = "The contact details of the person making the booking", required = true)
	private Booker booker = new Booker();;

	@ApiModelProperty(notes = "The items to be booked. Note that multiple items should be for the same 'set' of one-way and optionally a return", required = true)
	private Set<SegmentRQ> segments;

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "A list of details of the customers on this booking.  Note that the order is important, as this array is accessed with an index during booking", required = true)
	private List<DocumentedTraveller> travellers = new ArrayList<DocumentedTraveller>();

	@ApiModelProperty(notes = "The country Code of origin.  Where possible, reviews and rankings will be based on country of origin", required = false, example = "AU")
	private String countryCodeOfOrigin;

	@ApiModelProperty(notes = "The total barPrice for the booking.  A price check will occur before booking, and if the total barPrice has changed an error will be returned.")
	private CurrencyValue rate;

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
}
