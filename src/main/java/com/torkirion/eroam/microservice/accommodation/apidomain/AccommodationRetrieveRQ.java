package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AccommodationRetrieveRQ extends AbstractRQ implements Serializable
{
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The source channel of this item, from the Room=>Source field in search results", example = "YALAGO", required = true)
	private String channel;
	
	@ApiModelProperty(notes = "The booking reference from the called system for this booking", required = true)
	private String bookingReference;
}
