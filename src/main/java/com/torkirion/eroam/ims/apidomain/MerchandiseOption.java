package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MerchandiseOption
{
	private Integer id;

	private Integer merchandiseId;

	private String name;

	private String currency;

	private String rrpCurrency = "AUD";

	private BigDecimal nettPrice;

	private BigDecimal rrpPrice;

	private Integer allotment;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
