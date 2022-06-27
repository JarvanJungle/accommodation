package com.torkirion.eroam.microservice.hirecars.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.hirecars.apidomain.HireCarSearchRQ.BoundingBox;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class DetailRQDTO implements Serializable
{
	private String client;

	private String channel;
	
	private String vehicleId;
}
