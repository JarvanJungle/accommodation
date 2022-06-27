package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Facility implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("FacilityId")
	private Integer facilityId;

	@JsonProperty("FacilityGroup")
	private String facilityGroup;

	@JsonProperty("FacilityType")
	private String facilityType;

	@JsonProperty("Title")
	private String title;
}
