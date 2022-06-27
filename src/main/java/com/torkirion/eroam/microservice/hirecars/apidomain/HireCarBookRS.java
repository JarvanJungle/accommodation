package com.torkirion.eroam.microservice.hirecars.apidomain;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HireCarBookRS implements Serializable
{
	@JsonIgnore
	private List<ResponseExtraInformation> errors = new ArrayList<>();

	@JsonIgnore
	private List<ResponseExtraInformation> warnings = new ArrayList<>();

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	private String bookingReference;

	private String endpointReference;
	
	private String insuranceReference;
	
	private BookingStatus status;
	
	private HireCarBookingLocation pickupLocation;

	private HireCarBookingLocation dropoffLocation;
	
	private CarSearchEntry carSearchResult;

	private List<Extra> extrasSelected = new ArrayList<>();

	private SortedSet<CarSearchEntryDetailed.CancellationPolicy> cancellationPolicies;

	public enum BookingStatus
	{
		BOOKED, CANCELLED, FAILED;
	}

	@Data
	public static class Extra extends HireCarEntryExtra
	{
		private Integer extrasCount = null;

		public Extra(HireCarEntryExtra copy)
		{
			this.setAmount(copy.getAmount());
			this.setCode(copy.getCode());
			this.setDescription(copy.getDescription());
			this.setIncludedInRate(copy.getIncludedInRate());
			this.setTaxInclusive(copy.getTaxInclusive());
		}
	}
}
