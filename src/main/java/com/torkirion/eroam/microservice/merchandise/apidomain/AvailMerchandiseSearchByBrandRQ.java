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
public class AvailMerchandiseSearchByBrandRQ extends AvailMerchandiseSearchRQ implements Serializable
{
	@ApiModelProperty(notes = "The brand to search. Null means ALL (branded AND unbranded).  Blank \"\" means no brand", required = false, example = "World Cup")
	private String brand;
}
