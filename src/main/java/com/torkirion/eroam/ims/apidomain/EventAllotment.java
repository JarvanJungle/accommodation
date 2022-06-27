	package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventAllotment
{
	private Integer id;

	private Integer eventId;

	private String name;

	private Integer allotment;

	private Integer minimumSale;

	private Integer maximumSale;

	private List<Integer> multiplePattern;

	private Boolean onRequest;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
