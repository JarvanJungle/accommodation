package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EstablishmentText implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("EstablishmentId")
	private Integer establishmentId;

	@JsonProperty("Summary")
	private String summary;

	@JsonProperty("Description")
	private String description;
}
