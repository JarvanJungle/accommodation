package com.torkirion.eroam.microservice.apidomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@ApiModel
@ToString
public class CurrencyValue implements Serializable
{
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((currencyId == null) ? 0 : currencyId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurrencyValue other = (CurrencyValue) obj;
		if (amount == null)
		{
			if (other.amount != null)
				return false;
		}
		else if (amount.compareTo(other.amount) != 0)
			return false;
		if (currencyId == null)
		{
			if (other.currencyId != null)
				return false;
		}
		else if (!currencyId.equals(other.currencyId))
			return false;
		return true;
	}

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

	public CurrencyValue add(CurrencyValue other) throws Exception
	{
		if (!this.currencyId.equals(other.currencyId))
			throw new Exception("Cannot add differing currencies");
		else
			return new CurrencyValue(this.currencyId, this.amount.add(other.amount));
	}
}
