package com.torkirion.eroam.microservice.merchandise.apidomain;


import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.datadomain.MerchandiseCategory;
import com.torkirion.eroam.ims.datadomain.MerchandiseSupplier;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MerchandiseResult implements Comparable<MerchandiseResult>
{
	@ApiModelProperty(notes = "The unique code of this property within the channel from which it originated", example = "123456")
	private String channelId;

	@ApiModelProperty(notes = "The channel from which this property record originated", example = "IMS")
	private String channel;

	private String id;

	private String name;

	private String externalMerchandiseId;

	private String supplierName;

	private String merchandiseCategory;

	private String overview;

	private List<String> images;

	private List<String> brands;

	private Boolean bundlesOnly;

	private List<MerchandiseOption> options;

	@Override
	public int compareTo(MerchandiseResult o)
	{
		return name.compareTo(o.getName());
	}
}
