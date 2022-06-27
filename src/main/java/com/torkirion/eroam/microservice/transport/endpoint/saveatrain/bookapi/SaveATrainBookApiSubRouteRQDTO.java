package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SaveATrainBookApiSubRouteRQDTO {
    private String identifier;
    private String id;

    @Override
    public String toString() {
        return "SaveATrainBookApiSubRouteRQDTO{" +
                "identifier='" + identifier + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
