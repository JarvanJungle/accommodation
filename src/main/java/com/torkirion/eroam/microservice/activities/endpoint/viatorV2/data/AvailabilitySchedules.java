package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvailabilitySchedules implements Serializable
{
	@Data
	public static class PriceList
	{
		private BigDecimal recommendedRetailPrice;

		private BigDecimal partnerNetPrice;

		private BigDecimal bookingFee;

		private BigDecimal partnerTotalPrice;

		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate offerStartDate;

		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate offerEndDate;
	}

	@Data
	public static class PriceBlock
	{
		private PriceList original;

		private PriceList special;
	}

	@Data
	public static class UnavailableDate
	{
		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate date;

		private String reason;
	}
	
	@Data
	public static class PricingDetail
	{
		private String pricingPackageType;

		private Integer minTravelers;

		private Integer maxTravelers;

		private String ageBand;

		private PriceBlock price;
	}

	@Data
	public static class TimedEntry
	{
		private String startTime;
		private List<UnavailableDate> unavailableDates;
	}

	@Data
	public static class PricingRecord
	{
		private List<String> daysOfWeek;

		private List<TimedEntry> timedEntries;

		private List<PricingDetail> pricingDetails;
		
		private List<UnavailableDate> unavailableDates;
	}

	@Data
	public static class Season
	{
		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate startDate;

		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate endDate;

		private List<PricingRecord> pricingRecords;
	}

	@Data
	public static class BookableItem
	{
		private String productOptionCode;

		private List<Season> seasons;
	}

	@Data
	public static class AvailabilitySchedule
	{
		private String productCode;

		private List<BookableItem> bookableItems;

		private String currency;
	}

	private List<AvailabilitySchedule> availabilitySchedules;

	@JsonProperty("nextCursor")
	private String nextCursor;

}
