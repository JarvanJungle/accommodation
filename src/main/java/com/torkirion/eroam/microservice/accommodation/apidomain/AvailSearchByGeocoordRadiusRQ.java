package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class AvailSearchByGeocoordRadiusRQ extends AvailSearchSetRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2631748914483772350L;

	@ApiModelProperty(notes = "The center of a bounding circle for a search")
	private LatitudeLongitude geocoordinates;

	@ApiModelProperty(notes = "The radius in kilometers")
	private BigDecimal radius;
}
