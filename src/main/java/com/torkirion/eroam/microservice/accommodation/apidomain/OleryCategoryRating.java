package com.torkirion.eroam.microservice.accommodation.apidomain;

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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class OleryCategoryRating implements Serializable 
{
	@ApiModelProperty(notes = "The Olery rating for this property, but only if there are enough reviews!", required = false)
	private BigDecimal rating;

	@ApiModelProperty(notes = "The number of Olery ratings for this property", required = false)
	private BigInteger reviewCount;
}
