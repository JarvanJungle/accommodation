package com.torkirion.eroam.microservice.hirecars.apidomain;

import java.math.*;
import java.util.ArrayList;
import java.util.List;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import lombok.Data;

@Data
public class CarSearchEntry 
{
	private CurrencyValue price;

	private BigInteger limitedDistance;

	private String limitedDistanceUnits;

	private String limitedDistancePer;

	private Integer seats;

	private String categoryCode;

	private String category;

	private String size;

	private Integer doors;

	private String supplierName;
	
	private String supplierImage;
	
	private CarSearchLocationAndDate pickup;
	
	private CarSearchLocationAndDate dropoff;
	
	private String carName;

	private String image;

	private String endPoint;

	private String id;
	
	private CurrencyValue amountToBeCharged;

	private String paymentTimingStr;

	private String paymentMessage;

	private PaymentTiming paymentTiming;
	
	private Boolean creditCardRequired;
	
	private List<String> messages = new ArrayList<>();

}
