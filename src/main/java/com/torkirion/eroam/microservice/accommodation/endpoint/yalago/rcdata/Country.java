package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Country implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("CountryId")
	private Integer countryId;

	@JsonProperty("CountryCode")
	private String countryCode;

	@JsonProperty("Title")
	private String title;

	@JsonProperty("Provinces")
	private List<Province> provinces;
}
