package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.util.concurrent.CycleDetectingLockFactory.Policies;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class Rates
{
	public static class RateComparator implements Comparator<Rate>
	{

		@Override
		public int compare(Rate o1, Rate o2)
		{
			if (o1.getDescription().equals(o2.getDescription()))
			{
				return o1.getRateId() - o2.getRateId();
			}
			else
			{
				return o1.getDescription().compareTo(o2.getDescription());
			}
		}

	}

	@Data
	public static class PaxmixRate
	{
		private Integer numberOfAdults;

		public Integer getNumberOfAdults()
		{
			return numberOfAdults == null ? 0 : numberOfAdults;
		}

		private Integer numberOfChildren;

		public Integer getNumberOfChildren()
		{
			return numberOfChildren == null ? 0 : numberOfChildren;
		}

		private CurrencyValue nett;

		@ApiModelProperty(notes = "optional")
		private CurrencyValue rrp;
	}

	@Data
	public static class DOTWRate
	{
		// below can repeat for different DaysOfTheWeek
		private DaysOfTheWeek daysOfTheWeek;

		private CurrencyValue nett;

		@ApiModelProperty(notes = "optional")
		private CurrencyValue rrp;

		@ApiModelProperty(notes = "If paxmixPricing is true, paxmixRates must be provided, otherwise JUST nett and (optional) rrp")
		private Boolean paxmixPricing;

		private List<PaxmixRate> paxmixRates;
	}

	@Data
	public static class Rate
	{
		// the rateId is the same as the hash of roomTypeId, seasonId, policyId and boardCode
		private Integer rateId;

		private String hotelId;

		private Integer roomtypeId;

		private String rateGroup;

		@JsonIgnore
		private Roomtypes.Roomtype roomType;

		private Integer seasonId;

		@JsonIgnore
		private Seasons.Season season;

		private Integer policyId;

		@JsonIgnore
		private CancellationPolicies.CancellationPolicy policy;

		private String boardCode;

		@JsonIgnore
		private Boards.Board board;

		private String description;

		private Integer minimumNights = 0;

		private Integer allocationId;

		private Boolean bundlesOnly;

		private Allocation allocation;

		private List<DOTWRate> dotwRates = new ArrayList<>();
		
		private BigDecimal perInfantSurcharge;
	}

	@ApiModelProperty(notes = "The unique code of this property within the service", example = "YL123456")
	private String hotelId;

	private List<Rate> rates = new ArrayList<>();
}
