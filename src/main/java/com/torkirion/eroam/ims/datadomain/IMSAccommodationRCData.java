package com.torkirion.eroam.ims.datadomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
//@IdClass(IMSAccommodationRCData.RCDataKey.class)
@Table(name = "imsaccommodationrc", indexes = { @Index(name = "imsaccommodationrc_countryCode", columnList = "countryCode", unique = false),
		@Index(name = "imsaccommodationrc_latlong", columnList = "latitude, longitude", unique = false),
		@Index(name = "imsaccommodationrc_oleryCompanyCode", columnList = "oleryCompanyCode", unique = false) })
@Data
public class IMSAccommodationRCData
{
	public static class RCDataKey implements Serializable
	{
		private int client;

		private int hotelId;
	}

	//@Id
	//@Column(length =20)
	//private String client;

	@Id
	@Column(length = 100)
	private String hotelId;

	@Column(length = 1000)
	private String accommodationName;

	@Enumerated(EnumType.STRING)
	private AccommodationTypeTag productType;

	@Column(columnDefinition = "TEXT")
	private String introduction;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 100)
	private String supplier;

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

	@Column
	private LocalDate lastUpdate;

	@Column(length = 20)
	private String checkinTime;

	@Column(length = 3)
	private String currency;

	@Column(length = 3)
	private String rrpCurrency;

	@Column(length = 20)
	private String checkoutTime;

	@Column
	private Integer childAge;
	
	public Integer getChildAge()
	{
		return childAge == null ? 19 : childAge;
	}

	@Column
	private Integer infantAge;

	public Integer getInfantAge()
	{
		return infantAge == null ? 0 : infantAge;
	}

	// private List<FacilityGroup> facilityGroups;
	@Column(columnDefinition = "TEXT")
	private String facilityGroupsJson;

	// private List<String> errata;
	@Column(columnDefinition = "TEXT")
	private String errataJson;

	@Column(columnDefinition = "TEXT")
	private String imageThumbnail;

	@Column(columnDefinition = "TEXT")
	private String imagesJson;

	@Column
	private Long oleryCompanyCode;

	@Column
	private LocalDateTime lastUpdated;
}
