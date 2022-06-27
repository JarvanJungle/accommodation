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
@Table(name = "imseventsale")
@Data
public class EventSale
{
	public static enum ItemStatus
	{
		BOOKED, CANCELLED, FAILED;
	}

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
    @JoinColumn(name="event_id", nullable=false)
	private Event event;

	@Column(length = 200)
	private LocalDateTime bookingDateTime;

	// save event details
	@Column(length = 200)
	private String name;

	@Column
	private LocalDate eventDate;

	@Column(length = 3)
	private String currency;

	@Column(length = 3)
	private String rrpCurrency;

	@Column
	private BigDecimal nettPrice;

	@Column
	private BigDecimal rrpPrice;

	@Column(length = 1000)
	private String ticketingDescription;

	@Enumerated(EnumType.STRING)
	private ItemStatus itemStatus;
	
	@Column
	private Integer allotmentId;

	@Column(length = 200)
	private String allotmentName;
	
	@Column
	private Integer classificationId;

	@Column(length = 200)
	private String classificationName;
	
	@Column
	private Integer count;

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

}
