package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData.GeoCoordinates;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "olery_property_country", indexes = {@Index(name = "olery_property_country_companycountry", columnList="company_id, countryCode", unique = false)})
@Data
@ToString
public class OleryCountryProperty implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;

	@Column(name="company_id")
	private Long companyId;

	@Column(length = 2)
	private String countryCode;

	@Column
	private BigDecimal rating;

	@Column
	private BigInteger reviewCount;

	@Column(columnDefinition = "TEXT")
	private String categoryRating;
}
