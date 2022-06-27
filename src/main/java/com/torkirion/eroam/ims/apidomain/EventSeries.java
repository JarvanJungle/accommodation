package com.torkirion.eroam.ims.apidomain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventSeries
{
	private Integer id;

	private String externalSeriesId;

	private String name;

	private String defaultCurrency;

	private String type;

	private List<String> countries;

	private List<String> marketingCountries;
	
	private List<String> excludedMarketingCountries;

	private String overview;

	private Boolean active = true;

	private String imageUrl;
	
	private List<EventMerchandiseAPILink> eventMerchandiseLinks;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
