package com.torkirion.eroam.ims.datadomain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Embeddable
public class Address
{
	@Column(length = 1000)
	private String fullFormAddress;

	@Column(length = 100)
	private String street;

	@Column(length = 100)
	private String postcode;

	@Column(length = 100)
	private String city;

	@Column(length = 100)
	private String state;

	@Column(length = 2)
	private String countryCode;

	@Column(length = 50)
	private String countryName;

	@Embedded
	private GeoCoordinates geoCoordinates;
}