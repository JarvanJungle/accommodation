package com.torkirion.eroam.microservice.accommodation.dto;

import java.io.Serializable;
import java.util.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper=true)
@NoArgsConstructor
public class AvailSearchByHotelIdRQDTO extends AvailSearchRQDTO implements Serializable
{
	private Collection<String> hotelIds;
}
