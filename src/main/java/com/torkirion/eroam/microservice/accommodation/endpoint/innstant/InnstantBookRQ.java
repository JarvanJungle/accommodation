package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InnstantBookRQ
{
	@Data
	public static class Adult extends Customer {
		private Boolean lead;
	}
	@Data
	public static class PaxDetail {
		private List<Adult> adults;
//		private List<Children> children;
	}
	@Data
	public static class BookingRequest {
		private String code;
		private String token = "B6B3AF3A";
		private List<PaxDetail> pax;

	}
	@Data
	public static class BookingService {
		private List<BookingRequest> bookingRequest;
		private InnstantAvailabilityRQ searchRequest;
	}

	private Customer customer;
	private PaymentMethodDTO paymentMethod;
	private List<BookingService> services;
}
