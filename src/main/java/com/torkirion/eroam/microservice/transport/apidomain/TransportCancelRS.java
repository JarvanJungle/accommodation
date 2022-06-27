package com.torkirion.eroam.microservice.transport.apidomain;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.Traveller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class TransportCancelRS extends AbstractRQ implements Serializable
{
	public TransportCancelRS(String confirmationCode, CurrencyValue cancellationCharge)
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
