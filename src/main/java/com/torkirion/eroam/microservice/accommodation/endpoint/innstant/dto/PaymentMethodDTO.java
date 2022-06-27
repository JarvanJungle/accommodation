package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PaymentMethodDTO
{
    @Data
    public class PaymentSettings {
        private Boolean creditCardForm;
        private List<String> requiredFields;
    }
    private String methodName;
    private String methodDescription;
    private PaymentSettings paymentSettings;
    private String status;
    private String title;
}
