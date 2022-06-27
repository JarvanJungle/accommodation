package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartCheckingCancelRS {
    private String bookingId;
    private RefundDetails refundDetails;
    private String status;
    private String message;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RefundDetails{
        private double itemPrice;
        private double refundAmount;
        private double refundPercentage;
        private String currencyCode;
    }
}
