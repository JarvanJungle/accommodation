package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class LookupTopRCByGeocoordBoxRQ extends AbstractRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2631748914483772350L;

	@ApiModelProperty(notes = "The northwest (upper left) corner of a bounding box for a search")
	private LatitudeLongitude northwest;

	@ApiModelProperty(notes = "The southeast (lower right) corner of a bounding box for a search")
	private LatitudeLongitude southeast;

	@ApiModelProperty(notes = "The number of results to return")
	private Integer resultsLimit = 0;
	
	@ApiModelProperty(notes = "The country Code of origin.  Where possible, reviews and rankings will be based on country of origin", required = false, example = "AU")
	private String countryCodeOfOrigin;
}
