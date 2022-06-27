package com.torkirion.eroam.microservice.apidomain;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class   LatitudeLongitude implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6603710254492778917L;

	public LatitudeLongitude()
	{
	}

	public LatitudeLongitude(BigDecimal latitude, BigDecimal longitude)
	{
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	private BigDecimal latitude;

	private BigDecimal longitude;

}
