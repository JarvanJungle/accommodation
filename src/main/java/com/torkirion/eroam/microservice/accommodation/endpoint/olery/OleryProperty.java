package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
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
@Table(name = "olery_property")
@Data
@ToString
public class OleryProperty implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Data
	@ToString
	@Embeddable
	public static class GeoCoordinates
	{
		@Column
		private BigDecimal latitude;

		@Column
		private BigDecimal longitude;
	}

	@Id
	@Column(length = 20, name="company_id")
	private Long companyId;

	@Column(length = 100)
	private String name;

	@Column
	private LocalDate lastUpdated;

	@Column
	private BigDecimal rating; // AKA gei

	@Column
	private BigInteger reviewCount;

	@Embedded
	private GeoCoordinates geoCoordinates;

	@Column(length = 100)
	private String city;

	@Column(length = 2)
	private String countryCode;
	
	@Column(columnDefinition = "TEXT")
	private String reviewSummaries;
	
	@Column(columnDefinition = "TEXT")
	private String categoryRating;
	
	@Column
	private String tripAdvisorId;
	
	@Column
	private String googlePlaceId;
	
	@Column
	private String expediaId;
	
	@Column
	private String eanId;

}
