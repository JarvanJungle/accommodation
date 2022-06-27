package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivityDepartureTime
{

	private Integer id;
	
	@JsonFormat(pattern="HH:mm")
	@ApiModelProperty(notes = "Format HH:mm. Enter 00:00 for a 'null' departure time", required = true)
	private String departureTime;

	private String name;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
