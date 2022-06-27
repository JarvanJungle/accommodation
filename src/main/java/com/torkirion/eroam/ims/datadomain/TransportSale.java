package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "imstransportsale")
@Data
public class TransportSale
{
	public static enum ItemStatus
	{
		BOOKED, CANCELLED, FAILED;
	}

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 200)
	private LocalDateTime bookingDateTime;

	// "the leg flight number with airline, e.g. QF400")
	@Column(length = 100)
	private String transportCode;

	// "The departure Date"
	@Column
	private LocalDate transportDate;

	// "flight, ferry")
	@Column(length = 10)
	private String transportType;

	// "ENUM (F,J,C,S,Y)")
	@Column(length = 10)
	private String transportClass;

	@Column(length = 3)
	private String currency;

	@Column(length = 3)
	private String rrpCurrency;

	@Column
	private BigDecimal nettPrice;

	@Column
	private BigDecimal rrpPrice;

	@Enumerated(EnumType.STRING)
	private ItemStatus itemStatus;
	
	// booker information
	@Column(length = 2)
	private String countryCodeOfOrigin;
	
	@Column(length = 20)
	String title;

	@Column(length = 100)
	String givenName;

	@Column(length = 100)
	String surname;

	@Column(length = 100)
	String telephone;
	
	@Column(length = 100)
	private String internalBookingReference;

	@Column(length = 100)
	private String internalItemReference;

	// column separated list of travellers surname/firstname/title(age), etc
	@Column(length = 1000)
	String travellerInformation;

}
