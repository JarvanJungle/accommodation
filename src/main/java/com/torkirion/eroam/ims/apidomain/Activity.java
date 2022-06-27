package com.torkirion.eroam.ims.apidomain;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Activity
{
	@Data
	public static class HotelPickup
	{
		private String hotelId;
		private String hotelName;
	}
	
	private Integer id;

	private String externalActivityId;

	private String name;

	private Integer supplierId;

	@ApiModelProperty(notes = "Only for when reading", required = false)
	private ActivitySupplier supplier;

	private String overview;

	private List<String> categories;

	private String operator;

	private String duration;

	private String city;

	private String state;

	private String countryCode;

	@Embedded
	private GeoCoordinates geoCoordinates;

	private Boolean hotelPickup;
	
	private List<HotelPickup> hotelPickups;

	private String departurePoint;

	private List<String> images;

	private List<String> inclusions;

	private List<String> exclusions;

	private List<String> additionalInformation;

	private String voucherInformation;

	private String localOperatorInformation;

	private String scheduleAndPricing;
	
	private String termsAndConditions;
	
	private Boolean allotmentByDepartureAndOption = true;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
