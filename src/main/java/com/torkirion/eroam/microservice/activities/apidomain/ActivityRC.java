package com.torkirion.eroam.microservice.activities.apidomain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.GeoCoordinates;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivityRC
{
	@Data
	public static class Image
	{
		private String imageURL;

		private String imageDescription;
	}

	@ApiModelProperty(notes = "The unique code of this property within the service", example = "VI1234ABC")
	private String code;

	@ApiModelProperty(notes = "The unique code of this activity within the channel from which it originated", example = "123456")
	private String channelCode;

	@ApiModelProperty(notes = "The channel from which this activity record originated", example = "VIATOR")
	private String channel;

	@ApiModelProperty(notes = "The name of the channel", example = "Viator")
	private String supplierName;

	@ApiModelProperty(notes = "The title for this activity")
	private String activityName;

	@ApiModelProperty(notes = "The categories for this activity.  Channel specific.")
	private List<String> categories;

	@ApiModelProperty(notes = "The geocordinates of the property", required = false)
	private LatitudeLongitude geoCoordinates;

	@ApiModelProperty(notes = "The general type of this activity")
	private String productType;

	@ApiModelProperty(notes = "The description of the duration")
	private String durationText;
	
	@ApiModelProperty(notes = "The duration in ISO form")
	private String duration;
	
	@ApiModelProperty(notes = "The from / introductory price per adult")
	private CurrencyValue fromPerPerson;

	@ApiModelProperty(notes = "The operator of this service")
	private String operator;

	@ApiModelProperty(notes = "The departure point for this service")
	private String departurePoint;

	@ApiModelProperty(notes = "Are hotel pickups available?")
	private Boolean hotelPickupAvailable;

	@ApiModelProperty(notes = "Hotels available for pickup")
	private List<String> hotelPickupList;

	@ApiModelProperty(notes = "Long text overview")
	private String overview;

	//@ApiModelProperty(notes = "Long text overview")
	//private String introduction; // does this merge with overview?

	@ApiModelProperty(notes = "Long text for itinerary highlights")
	private String itineraryHighlights;

	@ApiModelProperty(notes = "Activity inclusions")
	private String inclusions;

	@ApiModelProperty(notes = "Activity exclusions")
	private String exclusions;

	@ApiModelProperty(notes = "Additional information")
	private String additionalInformation;

	@ApiModelProperty(notes = "General information about schedules and pricing")
	private String scheduleAndPricingInformation;

	@ApiModelProperty(notes = "Voucher information")
	private String voucherInformation;

	@ApiModelProperty(notes = "Extra information about the on the ground operator")
	private String localOperatorInformation;

	@ApiModelProperty(notes = "General cancellqtion policy information")
	private String cancellationPolicyOverview;
	
	@ApiModelProperty(notes = "The countryId", required = true)
	private String countryCode;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@ApiModelProperty(notes = "the date and time this record was last updated", required = true)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate lastUpdate;

	@ApiModelProperty(notes = "Images for this activity", required = false)
	private List<Image> images;
}
