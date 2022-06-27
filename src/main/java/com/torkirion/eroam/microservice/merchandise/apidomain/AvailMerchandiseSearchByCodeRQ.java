package com.torkirion.eroam.microservice.merchandise.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class AvailMerchandiseSearchByCodeRQ extends AvailMerchandiseSearchRQ implements Serializable
{
	@ApiModelProperty(notes = "The unique merchandise ID", required = false, example = "IM123456")
	private String merchandiseId;
}
