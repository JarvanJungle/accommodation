package com.torkirion.eroam.microservice.activities.apidomain;

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
public class AvailActivitySearchByGeocoordBoxRQ extends AvailActivityRangeSearchRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2631748914483772350L;

	@ApiModelProperty(notes = "The northwest (upper left) corner of a bounding box for a search")
	private LatitudeLongitude northwest;

	@ApiModelProperty(notes = "The southeast (lower right) corner of a bounding box for a search")
	private LatitudeLongitude southeast;
}
