package com.torkirion.eroam.microservice.activities.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AvailSearchByActivityIdRQDTO extends AvailSearchRQDTO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Set<String> activityIds;
}
