package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Destinations implements Serializable
{
	@Data
	public static class Destination
	{
		@JsonProperty("sortOrder")
		private Integer sortOrder;

		@JsonProperty("selectable")
		private Boolean selectable;
		
		@JsonProperty("destinationUrlName")
		private String destinationUrlName;
		
		@JsonProperty("defaultCurrencyCode")
		private String defaultCurrencyCode;
		
		@JsonProperty("lookupId")
		private String lookupId;
		
		@JsonProperty("parentId")
		private Integer parentId;
		
		@JsonProperty("timeZone")
		private String timeZone;
		
		@JsonProperty("iataCode")
		private String iataCode;
		
		@JsonProperty("destinationType")
		private String destinationType;
		
		@JsonProperty("destinationId")
		private Integer destinationId;
		
		@JsonProperty("longitude")
		private BigDecimal longitude;
		
		@JsonProperty("latitude")
		private BigDecimal latitude;
		
		@JsonProperty("destinationName")
		private String destinationName;
		
	}
	
	@JsonProperty("errorReference")
	private String errorReference;

	@JsonProperty("data")
	private List<Destination> data;
}
