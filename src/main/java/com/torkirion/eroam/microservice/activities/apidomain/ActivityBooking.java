package com.torkirion.eroam.microservice.activities.apidomain;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivityBooking implements Serializable
{
	public static enum ItemStatus
	{
		BOOKED, CANCELLED, FAILED;
	}
}
