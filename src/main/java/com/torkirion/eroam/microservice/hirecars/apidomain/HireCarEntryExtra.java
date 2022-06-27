package com.torkirion.eroam.microservice.hirecars.apidomain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HireCarEntryExtra
{
	private String code;

	private String description;

	private BigDecimal amount;

	private Boolean taxInclusive;

	private Boolean includedInRate;

	private Boolean requestOnly = Boolean.FALSE;
}