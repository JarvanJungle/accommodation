package com.torkirion.eroam.microservice.events.apidomain;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationProperty;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomResult;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EventTicketClassification
{
	private Integer classificationId;

	private String name;

	@Deprecated
	private String currency;

	@Deprecated
	private BigDecimal nettPrice;

	@Deprecated
	private BigDecimal rrpPrice;

	private CurrencyValue totalRetailPrice;

	private CurrencyValue totalNetPrice;
	
	private String ticketingDescription;
	
	private Integer allowInfantIfUnder;

	private Boolean bundlesOnly;

	@ApiModelProperty(example = "{'2021-08-11'}")
	private List<String> dates = new ArrayList<>();

	private List<Integer> allowedTicketSales = new ArrayList<>();
}
