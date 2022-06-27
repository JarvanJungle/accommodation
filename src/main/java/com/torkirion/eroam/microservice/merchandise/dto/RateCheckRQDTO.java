package com.torkirion.eroam.microservice.merchandise.dto;

import java.util.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class RateCheckRQDTO extends AvailSearchRQDTO 
{
	private String merchandiseId;

	private String optionId;
	
	private Integer count;

}
