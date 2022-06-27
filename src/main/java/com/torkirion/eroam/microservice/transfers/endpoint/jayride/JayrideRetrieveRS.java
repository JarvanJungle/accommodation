package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class JayrideRetrieveRS
{
	@JsonProperty("booking_id")
	private String booking_id;

	@JsonProperty("booking_status")
	private String booking_status;
}
