package com.torkirion.eroam.microservice.cruise.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class DetailRQDTO
{
	private String client;
	private String channel;
	private String countryCodeOfOrigin;
	private Integer cruiseId;
	private Integer shipId;
}
