package com.torkirion.eroam.microservice.merchandise.apidomain;


import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.datadomain.Merchandise;
import com.torkirion.eroam.ims.datadomain.MerchandiseCategory;
import com.torkirion.eroam.ims.datadomain.MerchandiseSupplier;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MerchandiseOption implements Comparable<MerchandiseOption>
{
	private Integer id;

	private String name;

	@Deprecated
	private String currency;

	@Deprecated
	private BigDecimal nettPrice;

	@Deprecated
	private BigDecimal rrpPrice;

	private CurrencyValue totalRetailPrice;

	private CurrencyValue totalNetPrice;

	private Integer allotment;

	@Override
	public int compareTo(MerchandiseOption o)
	{
		return name.compareTo(o.getName());
	}
}
