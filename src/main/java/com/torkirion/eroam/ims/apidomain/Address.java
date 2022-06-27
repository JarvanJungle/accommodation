package com.torkirion.eroam.ims.apidomain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Address implements Serializable
{
	@ApiModelProperty(notes = "The full address of this place. May be provided if a street/postcoide etc breakdown is not available")
	private String fullFormAddress;

	@ApiModelProperty(notes = "The street part of the address", required = false)
	private String street;

	@ApiModelProperty(notes = "The postcode part of the address", required = false)
	private String postcode;

	@ApiModelProperty(notes = "The city part of the address", required = false)
	private String city;

	@ApiModelProperty(notes = "The state part of the address", required = false)
	private String state;

	@ApiModelProperty(notes = "The 2 character countryId part of the address", required = false)
	private String countryCode;

	// only need countryCode - the external calling system can look up the name 
	@Deprecated
	@ApiModelProperty(notes = "The countryName part of the address", required = false)
	private String countryName;

	@ApiModelProperty(notes = "The Geocoordinates of the address", required = false)
	private GeoCoordinates geoCoordinates;
}