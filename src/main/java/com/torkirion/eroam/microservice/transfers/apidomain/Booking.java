package com.torkirion.eroam.microservice.transfers.apidomain;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Booking implements Serializable
{
	public static enum ItemStatus
	{
		BOOKED, CANCELLED, FAILED;
	}
}
