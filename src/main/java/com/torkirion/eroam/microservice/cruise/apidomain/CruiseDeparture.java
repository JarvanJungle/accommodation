package com.torkirion.eroam.microservice.cruise.apidomain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CruiseDeparture implements Serializable
{
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@ApiModelProperty(notes = "The departuredate of this particular cruise from this port")
	private LocalDate departureDate;

	@ApiModelProperty(notes = "The departure port")
	private String departurePort;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@ApiModelProperty(notes = "The finishDate of this particular cruise")
	private LocalDate finishDate;

	private List<Room> rooms;
}
