package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EstablishmentExtra implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("MasterExtraId")
	private Integer masterExtraId;

	@JsonProperty("EstablishmentId")
	private Integer establishmentId;

	@JsonProperty("Title")
	private String title;

	@JsonProperty("Type")
	private String type;
}
