package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
public class AvailabilityRS extends CommonRQRS
{
	@JsonProperty("Establishments")
	private List<Establishment> establishments;
}
