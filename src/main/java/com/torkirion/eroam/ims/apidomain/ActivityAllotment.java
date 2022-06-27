package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.torkirion.eroam.ims.apidomain.Allocation.AccommodationAllocationDateData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ActivityAllotment
{
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DateData
	{
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")		
		@JsonDeserialize(using = LocalDateDeserializer.class)
		@JsonSerialize(using = LocalDateSerializer.class)
		private LocalDate date;

		private Integer allotment;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class AllotmentSummary
	{
		private Integer activityId;

		private Integer optionId;

		private Integer departureTimeId;
	}

	private AllotmentSummary allotmentSummary;

	private List<DateData> allotmentDates = new ArrayList<>();
}
