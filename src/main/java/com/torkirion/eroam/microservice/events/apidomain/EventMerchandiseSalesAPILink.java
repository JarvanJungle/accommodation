package com.torkirion.eroam.microservice.events.apidomain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class EventMerchandiseSalesAPILink
{
	private String merchandiseId;

	private Boolean mandatoryInclusion;
}
