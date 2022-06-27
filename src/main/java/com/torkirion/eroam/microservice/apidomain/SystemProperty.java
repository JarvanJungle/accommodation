package com.torkirion.eroam.microservice.apidomain;

import com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SystemProperty
{
	@ApiModelProperty(notes = "The client these parameters refer to")
	private String client;

	@ApiModelProperty(notes = "The channel (product source")
	private String channel;

	@ApiModelProperty(notes = "The system parameter")
	private String parameter;

	@ApiModelProperty(notes = "The system value")
	private String value;

	@ApiModelProperty(notes = "The system value")
	private ProductType productType;
}
