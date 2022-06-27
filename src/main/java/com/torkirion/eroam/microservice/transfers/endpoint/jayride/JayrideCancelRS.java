package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class JayrideCancelRS
{
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("message")
	private String message;
}
