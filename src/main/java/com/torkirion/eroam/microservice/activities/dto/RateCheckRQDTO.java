package com.torkirion.eroam.microservice.activities.dto;

import java.time.LocalDate;
import java.util.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class RateCheckRQDTO extends AvailSearchRQDTO 
{
	private String activityId;
	
	private String departureId;
	
	private String optionId;

}
