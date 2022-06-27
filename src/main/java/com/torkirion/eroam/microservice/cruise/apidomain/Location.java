package com.torkirion.eroam.microservice.cruise.apidomain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Location implements Serializable
{
	private Integer locationId;

	private String locationName;
}
