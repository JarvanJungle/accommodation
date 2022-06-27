package com.torkirion.eroam.microservice.accommodation.apidomain.eroam;

import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class HotelListRS extends HotelBaseRS
{
	@JsonProperty("hotel-list")
	private List<Hotel> hotel_list = new ArrayList<>();
}
