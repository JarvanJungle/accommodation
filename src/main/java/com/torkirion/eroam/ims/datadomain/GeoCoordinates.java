package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Embeddable
public class GeoCoordinates
{
	@Column(scale = 5, precision = 8)
	private BigDecimal latitude;

	@Column(scale = 5, precision = 8)
	private BigDecimal longitude;
}