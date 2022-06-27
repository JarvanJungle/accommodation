package com.torkirion.eroam.ims.apidomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@ApiModel
@ToString
public class CurrencyValue
{
	public CurrencyValue(String currencyId, BigDecimal amount)
	{
		super();
		this.currencyId = currencyId;
		this.amount = amount.setScale(2, RoundingMode.HALF_UP);
	}

	public CurrencyValue(CurrencyValue other)
	{
		super();
		this.currencyId = other.currencyId;
		this.amount = other.amount.setScale(2, RoundingMode.HALF_UP);
	}

	@ApiModelProperty(required = true, example = "AUD")
	private String currencyId;

	@ApiModelProperty(required = true, example = "100.00")
	private BigDecimal amount;

	public CurrencyValue()
	{
	}
}
