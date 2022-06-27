package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HotelServicesDTO {

    @Data
    public static class Surcharge {
        private AmountAndCurrencyDTO price;
        private String payment;
        private String description = "";
        private String title;
        private Boolean mandatory;
    }
    @Data
    public static class Quantity
    {
        private Integer max;
        private Integer min;
    }
    @Data
    public static class Remark
    {
        private String general;
    }
    @Data
    public static class Item {
        private String name;
        private String category;
        private String bedding;
        private String board;
        private String boardName;
        private HotelInfoDTO hotel;
        private PaxDTO pax;
        private Quantity quantity;
        private Remark remark;
    }
    private List<Item> items;
    private List<Surcharge> surcharges;
    private List<ProviderDTO> providers;

    private AmountAndCurrencyDTO price;
    private AmountAndCurrencyDTO priceWithoutTax;
    private AmountAndCurrencyDTO netPrice;
    private AmountAndCurrencyDTO taxAmount;
    private AmountAndCurrencyDTO transactionFee;
    private AmountAndCurrencyDTO netPriceInClientCurrency;
    private AmountAndCurrencyDTO barRate;

    private CancellationDTO cancellation;

    private String confirmation;
    private String paymentMethod;
    private String code;
    private String token;
    private String requestCode;
}
