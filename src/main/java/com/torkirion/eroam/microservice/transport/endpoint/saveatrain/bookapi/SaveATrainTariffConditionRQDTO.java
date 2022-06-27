package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SaveATrainTariffConditionRQDTO {
    private String identifier;
    private int resultId;
    private int fareId;
}
