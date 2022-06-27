package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccommodationRetrieveRS extends Booking implements Serializable
{
	public AccommodationRetrieveRS()
	{
	}
	

	@ApiModelProperty(notes = "The booking status", required = true)
	private ItemStatus itemStatus;

	private static final long serialVersionUID = 1L;

	//@ApiModelProperty(notes = "The cancellation confirmation code", required = true)
	//private String confirmationCode;

	//@ApiModelProperty(notes = "The charge (fee) for this cancellation", required = true)
	//private CurrencyValue cancellationCharge;

	@JsonIgnore
	private List<ResponseExtraInformation> errors = new ArrayList<>();

}
