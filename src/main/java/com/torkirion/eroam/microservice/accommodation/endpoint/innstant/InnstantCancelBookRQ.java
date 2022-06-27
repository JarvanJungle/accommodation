package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.Customer;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.PaymentMethodDTO;
import lombok.Data;

import java.util.List;

@Data
public class InnstantCancelBookRQ
{
	@JsonProperty(value = "BookingID")
	private Long bookingID;
	@JsonProperty(value = "CancelReason")
	private String cancelReason = "Sick";
	@JsonProperty(value = "Force")
	private Boolean force = false;
	@JsonProperty(value = "IsManual")
	private Boolean isManual = false;
}
