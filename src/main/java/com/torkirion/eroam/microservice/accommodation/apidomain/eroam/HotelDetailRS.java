package com.torkirion.eroam.microservice.accommodation.apidomain.eroam;

import java.math.BigDecimal;
import java.util.*;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class HotelDetailRS extends HotelBaseRS
{
	private BasicHotelDetail basic_detail;
}
