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
import com.torkirion.eroam.ims.datadomain.Activity;
import com.torkirion.eroam.ims.datadomain.ActivitySale.ItemStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivitySale
{
	private Integer id;

	@JsonFormat(pattern = "yyyy-MM-dd'T'hh:MM:ss")
	private LocalDateTime bookingDateTime;

	private String name;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate activityDate;

	private String nettCurrency;

	private BigDecimal nettPrice;

	private String rrpCurrency;

	private BigDecimal rrpPrice;

	private ItemStatus itemStatus;

	private Integer optionId;

	private String optionName;

	private Integer departureTimeId;

	private String departureTimeName;

	private Integer count;

	private String ageList;

	private String countryCodeOfOrigin;

	private String title;

	private String givenName;

	private String surname;

	private String telephone;

	private String internalBookingReference;

	private String internalItemReference;
	
	private String bookingQuestionAnswers;
}
