package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SupplierBoardType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("SupplierBoardTypeId")
	private Integer supplierBoardTypeId;
	
	@JsonProperty("Title")
	private String title;
}
