package com.torkirion.eroam.microservice.merchandise.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class AvailSearchRQDTO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String countryCodeOfOrigin;
	
	private String nameFilter;

	private String merchandiseId;

	private String client;

	private String channel;
	
	private String brand;
}
