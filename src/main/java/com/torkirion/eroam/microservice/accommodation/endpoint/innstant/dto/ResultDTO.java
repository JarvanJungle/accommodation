package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResultDTO {
    private List<RoomDTO> items;

    private AmountAndCurrencyDTO price;
    private AmountAndCurrencyDTO netPrice;
    private AmountAndCurrencyDTO barRate;

    private CancellationDTO cancellation;

    private String code;
    private String confirmation;
    private String paymentType;

    private List<ProviderDTO> providers;

    private Boolean commissionable;
    private List<Object> specialOffers;
    private Boolean packageRate;

    private Long destinationSearchRQId;
}
