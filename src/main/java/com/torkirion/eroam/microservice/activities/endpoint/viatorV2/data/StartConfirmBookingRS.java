package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartConfirmBookingRS {

    private String status;
    private String bookingRef;
    private String partnerBookingRef;
    private String currency;
    private List<LineItem> lineItems;
    private TotalPrice totalPrice;
    private CancellationPolicy cancellationPolicy;
    private VoucherInfo voucherInfo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price{
        private double recommendedRetailPrice;
        private double partnerNetPrice;
        private double bookingFee;
        private double partnerTotalPrice;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubtotalPrice{
        private Price price;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LineItem{
        private String ageBand;
        private int numberOfTravelers;
        private SubtotalPrice subtotalPrice;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TotalPrice{
        private Price price;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RefundEligibility{
        private int dayRangeMin;
        private int percentageRefundable;
        private String startTimestamp;
        private String endTimestamp;
        private int dayRangeMax;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CancellationPolicy{
        private String type;
        private String description;
        private boolean cancelIfBadWeather;
        private boolean cancelIfInsufficientTravelers;
        private List<RefundEligibility> refundEligibility;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VoucherInfo{
        private String url;
        private String format;
    }
}
