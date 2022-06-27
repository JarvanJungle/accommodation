package com.torkirion.eroam.microservice.activities.apidomain;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ActivityOption implements Comparable<ActivityOption>
{
	@Data
	public static class ActivityOptionPriceBand
	{
		private String ageBandName;

		private CurrencyValue nettPrice;

		private CurrencyValue rrpPrice;
	}
	
	private String optionId;
	
	private String optionName;

	private CurrencyValue nettPrice;

	private CurrencyValue rrpPrice;

	private Boolean bundlesOnly = true;

	private List<ActivityOptionPriceBand> pricePer = new ArrayList<>();

	private List<BookingQuestion> bookingQuestions = new ArrayList<>();

	//TODO need recheck, there is one cancellationPolicy only
	@ApiModelProperty(notes = "Machine readable cancellation policy")
	private SortedSet<ActivityCancellationPolicyLine> cancellationPolicy;

	@ApiModelProperty(notes = "Human readable cancellation policy")
	private String cancellationPolicyText;

//	private ActivityRate rate;
//
//	private String rateKey;

	@Override
	public int compareTo(ActivityOption o)
	{
		if ( nettPrice.getAmount().compareTo(o.nettPrice.getAmount()) == 0)
			return optionName.compareTo(o.optionName);
		else
			return nettPrice.getAmount().compareTo(o.nettPrice.getAmount());
	}
}
