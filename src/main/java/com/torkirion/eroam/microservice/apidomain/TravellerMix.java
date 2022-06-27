package com.torkirion.eroam.microservice.apidomain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.swagger.annotations.ApiModelProperty;

@Data
@Slf4j
public class TravellerMix implements Serializable
{
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The number of adults over (and including) the age of 18", required = true)
	private Integer adultCount = 0;

	@ApiModelProperty(notes = "The ages of the children (the number is given by the number of ages provided", required = true)
	private List<Integer> childAges = new ArrayList<>();
}
