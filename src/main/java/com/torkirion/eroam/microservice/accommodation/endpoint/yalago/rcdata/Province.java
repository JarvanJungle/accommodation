package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Province implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("ProvinceId")
	private Integer provinceId;

	@JsonProperty("Title")
	private String title;

	@JsonProperty("Locations")
	private List<Location> locations;
}
