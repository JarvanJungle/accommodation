package com.torkirion.eroam.microservice.activities.datadomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.torkirion.eroam.microservice.datadomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
 * Have assigned seemingly random varchar lengths, simply to help debugging postgres when overflow errors.  SInce it reports the attempted length of the insert, we can see which field is overflowing.
 */
@Entity
@Table(name = "activityrc", indexes = { @Index(name = "activityrc_countryCode", columnList = "countryCode", unique = false),
		@Index(name = "activityrc_latlong", columnList = "latitude, longitude", unique = false), @Index(name = "activityrc_channelchannelcode", columnList = "channel, channelcode", unique = false)  })
@Data
public class ActivityRCData
{
	@Data
	@ToString
	@Embeddable
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GeoCoordinates
	{
		@Column(scale = 5, precision = 8)
		private BigDecimal latitude;

		@Column(scale = 5, precision = 8)
		private BigDecimal longitude;

		@Column
		private BigDecimal geoAccuracy;
	}

	@Id
	@Column(length = 90)
	private String code;

	@Column(length = 80)
	private String channelCode;

	@Column(length = 20)
	private String channel;

	@Column(length = 1050)
	private String activityName;

	@Embedded
	private GeoCoordinates geoCoordinates;

	@Column(length = 130)
	private String productType;

	@Column(length = 120)
	private String durationText;

	@Column(length = 10)
	private String duration;

	@Embedded
	private CurrencyValue fromPerPerson;

	// @Column(length = 3)
	// private String fromPerPersonCurrencyIdx;

	// @Column
	// private BigDecimal fromPerPersonAmountx;

	@Column(length = 110)
	private String operator;

	@Column(length = 1040)
	private String departurePoint;

	@Column
	private Boolean hotelPickupAvailable;

	@Column(columnDefinition = "TEXT")
	private String overview;

	@Column(columnDefinition = "TEXT")
	private String itineraryHighlights;

	@Column(columnDefinition = "TEXT")
	private String inclusions;

	@Column(columnDefinition = "TEXT")
	private String exclusions;

	@Column(columnDefinition = "TEXT")
	private String additionalInformation;

	@Column(length = 2020)
	private String voucherInformation;

	@Column(length = 1020)
	private String localOperatorInformation;

	@Column(length = 1030)
	private String cancellationPolicyOverview;

	@Column(length = 2)
	private String countryCode;

	// ArrayList<String>()
	@Column(columnDefinition = "TEXT")
	private String imagesJson;

	@Column
	private LocalDateTime lastUpdate;
}
