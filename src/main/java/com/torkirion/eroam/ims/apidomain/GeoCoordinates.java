package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GeoCoordinates
{
	private BigDecimal latitude;

	private BigDecimal longitude;

	private BigDecimal geoAccuracy;
}