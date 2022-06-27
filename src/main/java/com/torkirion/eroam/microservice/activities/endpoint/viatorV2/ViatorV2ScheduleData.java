package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "viatorv2_schedule", indexes = { @Index(name = "viatorv2_schedule_productCode", columnList = "productCode", unique = false) })
@Data
@ToString
public class ViatorV2ScheduleData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 50)
	private String productCode;

	@Column(length = 100)
	private String productOptionCode;

	@Column
	private LocalTime time;

	@Column
	private LocalDate startDate;

	@Column
	private LocalDate endDate;

	@Column(scale = 5, precision = 8)
	private BigDecimal latitude;

	@Column(scale = 5, precision = 8)
	private BigDecimal longitude;

	@Column(length = 20)
	private String ageBand;

	@Column
	private Boolean sunday = false;

	@Column
	private Boolean monday = false;

	@Column
	private Boolean tuesday = false;

	@Column
	private Boolean wednesday = false;

	@Column
	private Boolean thursday = false;

	@Column
	private Boolean friday = false;

	@Column
	private Boolean saturday = false;

	@Column
	private Integer minTravelers = 1;

	@Column
	private Integer maxTravelers = 99;

	@Column
	private BigDecimal priceNet;

	@Column
	private BigDecimal priceRrp;

	@Column(length = 3)
	private String currencyId;

	@Column
	private Boolean special = false;
}
