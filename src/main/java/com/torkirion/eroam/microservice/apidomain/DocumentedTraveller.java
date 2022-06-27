package com.torkirion.eroam.microservice.apidomain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.transport.apidomain.TransportBookRQ;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DocumentedTraveller extends TravellerSummary implements Serializable
{
	public static enum DocumentType
	{
		PASSPORT, MILITARY_ID, DIPLOMATIC_PASSPORT, MISSIONARY_PASSPORT, SPECIAL_PASSPORT, OTHERS, TRAVEL_VISA
	}

	@Data
	public static class TravellerDocumentation
	{
		@ApiModelProperty(notes = "The travellers documetn (e.g. passport) number", required = true)
		private String documentNumber;

		@ApiModelProperty(notes = "The document (e.g. passport) expiry, in format MM/YY", example = "09/27", required = true)
		private String documentExpiry;

		private DocumentType documentType;
	}

	private static final long serialVersionUID = 1L;

	private String title;

	private String givenName;

	private String surname;

	@ApiModelProperty(notes = "The telephone numnber of the person.  Currently only used for transfers.", example = "0414555666", required = false)
	private String telephone;

	@ApiModelProperty(notes = "The email of the traveller. Currently only used for transfers.", example = "fred@gmail.com", required = false)
	private String email;

	@ApiModelProperty(notes = "The 2 letter country code indicating the traveller's nationality", example = "US", required = true)
	private String nationality;

	@ApiModelProperty(notes = "The 2 letter country code indicating the traveller's country of residency", example = "US", required = true)
	private String residency;

	@ApiModelProperty(notes = "The traveller's official documentation", required = false)
	private TravellerDocumentation travellerDocumentation;
}
