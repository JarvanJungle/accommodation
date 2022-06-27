package com.torkirion.eroam.microservice.hirecars.apidomain;

import lombok.Data;

import java.util.List;

@Data
public class HireCarBookingLocation
{
	private String address;
	
	private Boolean atAirport;
	
	private List<String> phoneNumber;

	private Double locationLatitude;
	
	private Double locationLongitude;
	
	//private String supplierAccess;
	
	private String supplierLocationCode;

	public HireCarBookingLocation() {
	}

	public HireCarBookingLocation(CarSearchLocationDetail detail, CarSearchLocationAndDate base) {
		this.setAddress(detail.getSupplierAddress());
		this.setAtAirport(detail.getAtAirport());
		this.setPhoneNumber(detail.getPhones());
		this.setLocationLatitude(base.getLocation().getLatitude().doubleValue());
		this.setLocationLongitude(base.getLocation().getLongitude().doubleValue());
		this.setSupplierLocationCode(detail.getSupplierLocationCode());
	}

}
