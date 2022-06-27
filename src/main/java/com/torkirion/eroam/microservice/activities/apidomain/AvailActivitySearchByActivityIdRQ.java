package com.torkirion.eroam.microservice.activities.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class AvailActivitySearchByActivityIdRQ extends AvailActivityRangeSearchRQ implements Serializable
{
	private static final long serialVersionUID = -2631748914483772350L;

	@ApiModelProperty(notes = "The activity id, the channelCode value, which was responded by call availSearchByGeoBox ")
	private Set<String> activityIds;
}
