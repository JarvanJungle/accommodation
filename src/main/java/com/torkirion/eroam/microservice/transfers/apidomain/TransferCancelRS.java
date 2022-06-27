package com.torkirion.eroam.microservice.transfers.apidomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransferCancelRS implements Serializable
{
	public TransferCancelRS(String confirmationCode, CurrencyValue cancellationCharge)
	{
		this.confirmationCode = confirmationCode;
		this.cancellationCharge = cancellationCharge;
	}

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The cancellation confirmation code", required = true)
	private String confirmationCode;

	@ApiModelProperty(notes = "The charge (fee) for this cancellation", required = true)
	private CurrencyValue cancellationCharge;

	@JsonIgnore
	private List<ResponseExtraInformation> errors = new ArrayList<>();

}
