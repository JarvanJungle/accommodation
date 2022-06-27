package com.torkirion.eroam.microservice.events.dto;

import java.util.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class RateCheckRQDTO extends AvailSearchRQDTO 
{
	private String eventId;

	private String classificationId;
	
	@ApiModelProperty(notes = "The number of tickets to buy")
	private Integer numberOfTickets;
}
