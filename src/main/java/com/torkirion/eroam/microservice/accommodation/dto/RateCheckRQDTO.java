package com.torkirion.eroam.microservice.accommodation.dto;

import java.util.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class RateCheckRQDTO extends AvailSearchRQDTO 
{
	private String hotelId;
	private List<String> bookingCodes;
}
