package com.torkirion.eroam.microservice.hirecars.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import lombok.Data;

@Data
public class CarSearchLocationAndDate
{
	private LatitudeLongitude location = new LatitudeLongitude();

	private String locationCode;

	@JsonFormat(pattern="yyyy-MM-dd")
	private Date dateTime;

}
