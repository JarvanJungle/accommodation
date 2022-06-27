package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Establishment implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("EstablishmentId")
	private Integer establishmentId;

	@JsonProperty("EstablishmentTitle")
	private String establishmentTitle;

	@JsonProperty("AcommodationTypeId")
	private Integer acommodationTypeId;

	@JsonProperty("Address")
	private String address;

	@JsonProperty("PostalCode")
	private String postalCode;

	@JsonProperty("Email")
	private String email;

	@JsonProperty("FaxNumber")
	private String faxNumber;

	@JsonProperty("GeocodeAccuracy")
	private Integer geocodeAccuracy;

	@JsonProperty("Latitude")
	private BigDecimal latitude;

	@JsonProperty("Longitude")
	private BigDecimal Longitude;
	
	@JsonProperty("LocationId")
	private Integer locationId;
	
	@JsonProperty("PhoneNumber")
	private String phoneNumber;
	
	@JsonProperty("Rating")
	private Integer rating;
	
	@JsonProperty("RatingTypeId")
	private Integer ratingTypeId;
}
