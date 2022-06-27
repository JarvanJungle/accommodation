package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.InnstantBookRQ;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookContentDTO
{
    @Data
    public static class Supplier {
        private String reference;
    }
    @Data
    public static class Payment {
        private String method;
        private String paymentStatus;
        private Object clearingState = null;
    }
    @Data
    public static class Remark{
        private String general;
    }
    @Data
    public static class Pax{
        List<InnstantBookRQ.Adult> adults;
        List<InnstantBookRQ.Adult> children = new ArrayList<>();
    }

    @Data
    public static class Service {
        private CancellationDTO cancellation;
        private Object clearingState = null;
        private Integer itemId;
        private Integer quantity;
        private String code;
        private Remark remarks;
        private String service;
        private String status;
        private AmountAndCurrencyDTO netPrice;
        private AmountAndCurrencyDTO price;
        private Supplier supplier;
        private List<Pax> pax;
    }
    private String bookingID;
    private String status;


    private Customer customer;
    private Payment payment;

    private AmountAndCurrencyDTO netPrice;
    private AmountAndCurrencyDTO price;

    private List<Service> services;

}
