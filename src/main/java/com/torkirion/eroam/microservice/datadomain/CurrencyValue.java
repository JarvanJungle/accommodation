package com.torkirion.eroam.microservice.datadomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCData;

@Getter
@Setter
@Embeddable
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

	@Column(length = 3)
	private String currencyId;

	@Column
	private BigDecimal amount;

	public CurrencyValue()
	{
	}
}
