package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class AccommodationContentWithoutId
{
	@Data
	public static class HotelImage
	{
		private String url;
		private String imageDescription;
	}

	private String hotelName;

	private String hotelOverview;

	private Address address;

	private BigDecimal hotelRating; 

	private String currency;

	private String rrpCurrency = "AUD";

	private Integer childAge = 18;

	private Integer infantAge = 0;

	private String supplier;

	private String hotel_category; // e.g. "highsuite"

	private Long oleryCompanyCode;

	private List<String> facilities;

	private String phone;

	private List<HotelImage> hotel_images = new ArrayList<>();
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
