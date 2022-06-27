package com.torkirion.eroam.microservice.events.apidomain;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.apidomain.EventMerchandiseAPILink;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EventSeries 
{
	private Integer id;

	private String externalSeriesId;

	private String name;

	private String defaultCurrency;

	private String type;

	private List<String> countries = new ArrayList<>();

	private List<String> marketingCountries = new ArrayList<>();
	
	private List<String> excludedMarketingCountries = new ArrayList<>();

	private String overview;

	private String imageUrl;
	
	private List<EventResult> events = new ArrayList<>();
	
	private List<EventMerchandiseAPILink> eventMerchandiseLinks = new ArrayList<>();
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
