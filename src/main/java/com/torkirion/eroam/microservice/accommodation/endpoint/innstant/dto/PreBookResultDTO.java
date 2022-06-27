package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.InnstantAvailabilityRQ;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PreBookResultDTO {
    @JsonProperty("Services")
    private ServicesDTO services;

    @JsonProperty("ProfileVersion")
    private String profileVersion;

    @JsonProperty("PaymentMethods")
    private List<PaymentMethodDTO> paymentMethods;

    @JsonProperty("PaymentDueDate")
    private String paymentDueDate;

    @JsonProperty("LoyaltyPoints")
    private List<Integer> poyaltyPoints;

    @JsonProperty("ImmediateCharge")
    private Boolean immediateCharge;

    @JsonProperty("AvailablePoints")
    private List<Integer> availablePoints;

    @JsonProperty("AutoCancellation")
    private Boolean autoCancellation;

    private String bookingCode;
}
