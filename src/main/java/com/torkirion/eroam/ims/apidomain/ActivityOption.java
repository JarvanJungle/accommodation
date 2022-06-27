package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.Data;

@Data
public class ActivityOption
{
	@Data
	public static class ActivityOptionPriceBand
	{
		private Integer id;

		private String ageBandName;

		private String currency;

		private String rrpCurrency = "AUD";

		private BigDecimal nettPrice;

		private BigDecimal rrpPrice;
	}

	@Data
	public static class ActivityOptionBlock
	{
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")		
		@JsonDeserialize(using = LocalDateDeserializer.class)
		@JsonSerialize(using = LocalDateSerializer.class)
		private LocalDate fromDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")		
		@JsonDeserialize(using = LocalDateDeserializer.class)
		@JsonSerialize(using = LocalDateSerializer.class)
		private LocalDate toDate;

		private DaysOfTheWeek daysOfTheWeek;

		private Map<Integer, ActivityOptionPriceBand> priceBands;
	}

	private Integer id;

	private String externalCode;

	private String name;

	private List<ActivityOptionBlock> priceBlocks;
	
	private Boolean bundlesOnly;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
