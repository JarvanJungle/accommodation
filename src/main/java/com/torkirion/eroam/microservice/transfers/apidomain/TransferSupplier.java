package com.torkirion.eroam.microservice.transfers.apidomain;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;

import com.torkirion.eroam.microservice.accommodation.apidomain.RoomCancellationPolicyLine;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransferSupplier
{

	@ApiModelProperty(notes = "Transfer Supplier Name")
	private String supplierName;

	@ApiModelProperty(notes = "An image of this supplier")
	private String imageUrl;

	@ApiModelProperty(notes = "Transfer Supplier Description")
	private String supplierDescription;
}
