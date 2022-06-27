package com.torkirion.eroam.microservice.cruise.apidomain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ItineraryItem implements Serializable
{
	private String location;

	private Integer dayNumber;

	private Integer durationNights;

	private String name;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate departDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH-mm")
	private LocalTime departTime;
}
