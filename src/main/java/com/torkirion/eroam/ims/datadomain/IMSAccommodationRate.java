package com.torkirion.eroam.ims.datadomain;

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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.apidomain.CurrencyValue;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.BeforeCheckinAfterBooking;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.PenaltyType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "imsaccommodationrate", indexes = {
			@Index(name = "imsaccommodationrate_hotelid", columnList="hotelId", unique = false) 
			})
@Data
public class IMSAccommodationRate
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// OK, we've denormalised these :-)
	@Column(length = 20)
	private String hotelId;

	@Column
	private Integer rateId;

	@Column(length = 1000)
	private String description;

	@Column(length = 20)
	private String rateGroup;

	@Column
	private Integer roomtypeId;

	@Column
	private Integer seasonId;

	@Column
	private Integer policyId;

	@Column(length = 10)
	private String boardCode;

	@Column
	private Integer allocationId;

	@Embedded
	private DaysOfTheWeek daysOfTheWeek;

	@Column
	private Boolean paxmixPricing;

	@Column
	private Integer numberOfAdults;

	@Column
	private Integer numberOfChildren;

	@Column
	private Boolean bundlesOnly = false;

	@Column
	private Integer minimumNights = 0;

	@Column
	private BigDecimal nett;

	@Column
	private BigDecimal rrp;
	
	@Column
	private BigDecimal perInfantSurcharge;
}
