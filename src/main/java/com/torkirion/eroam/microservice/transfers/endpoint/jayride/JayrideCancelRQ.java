package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class JayrideCancelRQ
{
	@JsonProperty("message")
	private String message;
}
