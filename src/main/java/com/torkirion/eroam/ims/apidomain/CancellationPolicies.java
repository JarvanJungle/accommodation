package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class CancellationPolicies
{
	public static enum BeforeCheckinAfterBooking
	{
		AFTER_BOOKING, BEFORE_CHECKIN;
	}

	public static enum PenaltyType
	{
		NUMBER_OF_NIGHTS, DOLLAR_VALUE, PERCENTAGE;
	}

	@Data
	public static class CancellationPolicyLine
	{
		private Integer lineId;

		private Integer numberOfDays;

		private BeforeCheckinAfterBooking beforeCheckinAfterBooking;

		private PenaltyType penaltyType;

		private BigDecimal penalty;
	}

	@Data
	public static class CancellationPolicy
	{
		private Integer policyId;

		private String policyName;

		private String bookingConditions;

		private List<CancellationPolicyLine> lines = new ArrayList<>();
	}

	@ApiModelProperty(notes = "The unique code of this property within the service", example = "YL123456")
	private String hotelId;

	private List<CancellationPolicy> policies = new ArrayList<>();
}
