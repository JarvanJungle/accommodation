package com.torkirion.eroam.microservice.accommodation.datadomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentData;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "accommodationrc", indexes = {
			@Index(name = "accommodationrc_countryCode", columnList="countryCode", unique = false), 
			@Index(name = "accommodationrc_latlong", columnList="latitude, longitude", unique = false),
			@Index(name = "accommodationrc_oleryCompanyCode", columnList="oleryCompanyCode", unique = false)
			})
@Data
public class AccommodationRCData
{
	@Data
	@ToString
	@Embeddable
	public static class GeoCoordinates
	{
		@Column(scale = 5, precision = 8)
		private BigDecimal latitude;

		@Column(scale = 5, precision = 8)
		private BigDecimal longitude;

		@Column
		private BigDecimal geoAccuracy;
	}

	@Data
	@ToString
	@Embeddable
	public static class Address
	{
		@Column(length = 1000)
		private String fullFormAddress;

		@Column(length = 1000)
		private String street;

		@Column(length = 100)
		private String postcode;

		@Column(length = 100)
		private String city;

		@Column(length = 100)
		private String state;

		@Column(length = 2)
		private String countryCode;

		@Embedded
		private GeoCoordinates geoCoordinates;
	}

	@Id
	@Column(length = 100)
	private String code;

	@Column(length = 100)
	private String channelCode;

	@Column(length = 20)
	private String channel;

	@Column(length = 1000)
	private String accommodationName;

	@Enumerated(EnumType.STRING)
	private AccommodationTypeTag productType;

	@Column(columnDefinition = "TEXT")
	private String introduction;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 100)
	private String chain;

	@Column(length = 100)
	private String category;

	@Embedded
	private Address address;

	@Column(length = 100)
	private String phone;

	@Column(length = 100)
	private String email;

	@Column
	private BigDecimal rating;

	@Column(length = 100)
	private String ratingText;

	@Column(length = 100)
	private String internalDestinationCode;

	@Column
	private LocalDate lastUpdate;

	@Column(length = 20)
	private String checkinTime;

	@Column(length = 20)
	private String checkoutTime;

	//private List<FacilityGroup> facilityGroups;
	@Column(columnDefinition = "TEXT")
	private String facilityGroupsJson;

	//private List<String> errata;
	@Column(columnDefinition = "TEXT")
	private String errataJson;

	@Column(columnDefinition = "TEXT")
	private String imageThumbnail;

	@Column(columnDefinition = "TEXT")
	private String imagesJson;

	@Column
	private Long oleryCompanyCode;
}
