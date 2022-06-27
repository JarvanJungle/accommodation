package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.activities.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.ViatorV2Activity;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.ViatorV2Service;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Slf4j
public class AvailablityCheck
{
	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonProperty("travelDate")
	private String travelDate;

	private String productCode;

	private String productOptionCode;

	private String startTime;

	private String currency;

	private List<PaxMix> paxMix = new ArrayList<>();

	public AvailablityCheck() {
	}

	public AvailablityCheck(String productCode, RateCheckRQDTO rateCheckRQDTO, ViatorV2Activity viatorSpecificData, String currency) {
		this.setProductCode(productCode);
		if ( rateCheckRQDTO.getOptionId() != null && rateCheckRQDTO.getOptionId().length() > 0 && !rateCheckRQDTO.getOptionId().equals("DEFAULT"))
			this.setProductOptionCode(rateCheckRQDTO.getOptionId());
		this.setTravelDate(ViatorV2Service.dateFormatter.format(rateCheckRQDTO.getActivityDate()));
		setPaxMix(PaxMix.listOf(rateCheckRQDTO.getTravellers(), viatorSpecificData));
		this.setCurrency(currency);
	}

}
