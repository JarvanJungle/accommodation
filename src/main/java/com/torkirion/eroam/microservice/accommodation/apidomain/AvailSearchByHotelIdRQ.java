package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class AvailSearchByHotelIdRQ extends AvailSearchRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8869014468164847110L;

	@ApiModelProperty(notes = "The unique codes for the hotels", required = true)
	private Set<String> hotelIds;
}
