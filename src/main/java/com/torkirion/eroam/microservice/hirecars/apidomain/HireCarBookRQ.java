package com.torkirion.eroam.microservice.hirecars.apidomain;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ.Booker;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.Traveller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class HireCarBookRQ extends AbstractRQ implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(notes = "The country Code of origin.", required = false, example = "AU")
	private String countryCodeOfOrigin;

	@ApiModelProperty(notes = "The contact details of the person making the booking", required = true)
	private Booker booker;

	@ApiModelProperty(notes = "The details of rhe vehicle to be booked", required = true)
	private VehicleData vehicleData;

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "The channel field from the Search structure", required = true, example = "CARNECT")
	private String channel;

	@ToString
	@Data
	public static class Booker
	{
		@ApiModelProperty(notes = "The title of the booking contact", example = "Mr", required = false)
		private String title;

		@ApiModelProperty(notes = "The given (or first) name of the booking contact", example = "John", required = true)
		private String givenName;

		@ApiModelProperty(notes = "The family name (or surname) name of the booking contact", example = "Smith", required = true)
		private String surname;

		@ApiModelProperty(notes = "The telephone numnber of the booking contact", example = "0414555666", required = true)
		private String telephone;

		@ApiModelProperty(notes = "The email of the booking contact", example = "test@gmail.com", required = true)
		private String email;

		@ApiModelProperty(notes = "The birthDate of the booking contact", example = "1991-01-01", required = true)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate birthDate;

		@ApiModelProperty(notes = "The address of the booking contact", required = true)
		private Address address;
	}

	@Data
	public static class Address {

		@ApiModelProperty(notes = "Street number of the customer (please avoid special characters like: !”§$%&/()=?`;:_>)", example = "Buceriusstr. 2", required = true)
		private String streetNmbr;

		@ApiModelProperty(notes = "City of the customer (please avoid special characters like: !”§$%&/()=?`;:_>)", example = "Hamburg", required = true)
		private String cityName;

		@ApiModelProperty(notes = "Zip code of the customer (please avoid special characters like: !”§$%&/()=?`;:_>)", example = "20095", required = true)
		private String postalCode;
	}

	/*
	private String email = null;

	private String addressStreet = null;

	private String addressCity = null;

	private String addressPostcode = null;

	private String addressCountry = null;

	private String ccNumber = null;

	private String ccExpiration = null;

	private String ccName = null;

	private String ccCCV = null;
	*/

	@ToString
	@Data
	public static class VehicleData
	{
		@ApiModelProperty(notes = "The vehicle Id from th search and detail call", required = true)
		private String vehicleID = null;
		
		@ApiModelProperty(notes = "The vehicle Id from th search and detail call", required = true)
		private String insuranceID = null;
		
//		@ApiModelProperty(notes = "The vehicle Id from th search and detail call", required = true)
//		private CurrencyAmount totalAmount = null;

		private String flightNumber = null;
		
		@ApiModelProperty(notes = "Extras, such as GD, Child Seats etc", required = false)
		private List<VehicleExtra> extras = null;
	}

	@ToString
	@Data
	public static class VehicleExtra
	{
		private String extrasID = null;

		private int extrasCount;
	}
}
