package com.torkirion.eroam.microservice.activities.apidomain;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class ActivityRateCheckRQ extends AvailActivitySearchRQ 
{
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate activityDate;
	
	private String channel;

	@ApiModelProperty(notes = "the channelCode, which was responded by call availSearchByGeoBox ")
	private String activityId;
	
	private String departureId;
	
	private String optionId;
}
