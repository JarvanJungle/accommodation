package com.torkirion.eroam.microservice.merchandise.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MerchandiseRateCheckRQ extends AvailMerchandiseSearchRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8121974676256854733L;

	@ApiModelProperty(notes = "The channel field from the Merchandise structure", required = true)
	private String channel;

	@ApiModelProperty(notes = "The unique code for the merchandise", required = true)
	private String merchandiseId;

	@ApiModelProperty(notes = "The option", required = true)
	private String optionId;
	
	@ApiModelProperty(notes = "The number of items to buy")
	private Integer count;
}
