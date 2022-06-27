package com.torkirion.eroam.ims.datadomain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "imsactivity")
@Data
public class Activity
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 20)
	private String externalActivityId;

	@Column(length = 200)
	private String name;

	@ManyToOne
    @JoinColumn(name="activitysupplier_id", nullable=false)
	private ActivitySupplier activitySupplier;

	@Column(columnDefinition = "TEXT")
	private String overview;

	@Column(length = 500)
	private String categoriesJson;

	@Column(length = 100)
	private String operator;

	@Column(length = 500)
	private String duration;

	@Column(length = 100)
	private String city;

	@Column(length = 100)
	private String state;

	@Column(length = 2)
	private String countryCode;
	
	@Embedded
	private GeoCoordinates geoCoordinates;

	@Column
	private Boolean hotelPickup;

	@Column(length = 500)
	private String hotelPickupsJson;

	@Column(length = 500)
	private String departurePoint;

	@Column(columnDefinition = "TEXT")
	private String imagesJson;

	@Column(columnDefinition = "TEXT")
	private String inclusionsJson;

	@Column(columnDefinition = "TEXT")
	private String exclusionsJson;

	@Column(columnDefinition = "TEXT")
	private String additionalInformationJson;

	@Column(columnDefinition = "TEXT")
	private String voucherInformation;

	@Column(columnDefinition = "TEXT")
	private String localOperatorInformation;

	@Column(columnDefinition = "TEXT")
	private String scheduleAndPricing;

	@Column(columnDefinition = "TEXT")
	private String termsAndConditions;

	@Column
	private Boolean allotmentByDepartureAndOption = true;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="activity", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ActivityOption> allotments;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="activity", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ActivityDepartureTime> departureTimes;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="activity", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ActivityOption> options;

	@OneToMany(fetch = FetchType.LAZY, mappedBy="activity", cascade = CascadeType.DETACH, orphanRemoval = false)
	private Set<ActivitySale> sales;
	
	@Column
	private LocalDateTime lastUpdated;
}
