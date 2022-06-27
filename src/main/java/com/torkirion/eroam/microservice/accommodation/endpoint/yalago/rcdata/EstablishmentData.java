package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "yalago_Establishment")
@Data
public class EstablishmentData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer establishmentId;

	@Column(length = 1000)
	private String establishmentTitle;

	@Column
	private Integer acommodationTypeId;

	@Column(length = 1000)
	private String address;

	@Column(length = 255)
	private String postalCode;

	@Column(length = 255)
	private String email;

	@Column(length = 255)
	private String faxNumber;

	@Column
	private Integer geocodeAccuracy;

	@Column
	private BigDecimal latitude;

	@Column
	private BigDecimal Longitude;

	@Column
	private Integer LocationId;

	@Column(length = 255)
	private String PhoneNumber;

	@Column
	private Integer Rating;

	@Column
	private Integer RatingTypeId;
	
	@Column
	private LocalDate lastUpdate;
}
