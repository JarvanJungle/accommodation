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
@Table(name = "imsaccommodationsale")
@Data
public class IMSAccommodationSale
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

	@Column(length = 100)
	private String hotelId;

	@Column(length = 1000)
	private String accommodationName;

	@Column(length = 1000)
	private String roomName;

	@Column
	private LocalDate checkin;

	@Column
	private LocalDate checkout;

	@Column(length = 100)
	private String rateName;

	@Column
	private Integer roomNumber;

	@Column(length = 100)
	private String board;

	@Column(length = 250)
	private String cnxPolicy;

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

	// comma separated list of guests surname/firstname/title(age), etc
	@Column(length = 1000)
	String guestInformation;

	@Column
	Integer allocationId;
}
