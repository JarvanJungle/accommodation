package com.torkirion.eroam.ims.apidomain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Allocation
{
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class AccommodationAllocationDateData implements Comparable<AccommodationAllocationDateData>
	{
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")		
		private LocalDate date;
		private Integer allocation;
		@Override
		public int compareTo(AccommodationAllocationDateData o)
		{
			return this.date.compareTo(o.getDate());
		}
	}

	@Data
	public static class AllocationSummary
	{
		private String hotelId;

		private Integer allocationId;

		private String allocationDescription;
		
		private Integer handbackDays;
	}

	private AllocationSummary allocationSummary;

	private SortedSet<AccommodationAllocationDateData> allocationDates = new TreeSet<>();

	@JsonIgnore
	private Map<LocalDate, Integer> allocationMap = new HashMap<>();
}
