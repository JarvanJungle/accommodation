package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ConfirmHoldBooking
{
	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonProperty("travelDate")
	private String travelDate;

	private String productCode;

	private String productOptionCode;

	private String startTime;

	private String currency;

	private List<PaxMix> paxMix = new ArrayList<>();
}
