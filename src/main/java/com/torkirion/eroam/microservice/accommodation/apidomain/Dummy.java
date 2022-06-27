package com.torkirion.eroam.microservice.accommodation.apidomain;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Dummy
{
	@ApiModelProperty(required = false)
	private Integer dummyInt;

}
