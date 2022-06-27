package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.torkirion.eroam.microservice.accommodation.apidomain.OleryCategoryRating;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class OleryCategoryRatings
{
	private Map<String, OleryCategoryRating> categoryRatings = new HashMap<>();
}
