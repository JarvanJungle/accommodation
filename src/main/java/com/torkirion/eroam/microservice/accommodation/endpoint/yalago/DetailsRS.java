package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
public class DetailsRS extends CommonRQRS
{
	@JsonProperty("Establishment")
	private Establishment establishment;

	@JsonProperty("InfoItems")
	private List<InfoItem> infoItems;
}
