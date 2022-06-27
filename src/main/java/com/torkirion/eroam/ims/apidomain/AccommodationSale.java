package com.torkirion.eroam.ims.apidomain;

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

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccommodationSale
{
	public static enum ItemStatus
	{
		BOOKED, CANCELLED, FAILED;
	}

	private Integer id;

	@JsonFormat(pattern = "yyyy-MM-dd'T'hh:MM:ss")
	private LocalDateTime bookingDateTime;

	private String hotelId;

	private String accommodationName;

	private String roomName;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate checkin;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate checkout;

	private String rateName;

	private Integer roomNumber;

	private String board;

	private String cnxPolicy;

	private String currency;

	private BigDecimal nettPrice;

	private BigDecimal rrpPrice;

	private ItemStatus itemStatus;
	
	private String countryCodeOfOrigin;
	
	String title;

	String givenName;

	String surname;

	String telephone;
	
	private String internalBookingReference;

	private String internalItemReference;

	String guestInformation;
}
