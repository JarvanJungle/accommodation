package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchApiStartSearchRQDTO {
    private String origin;
    private String destination;
    private Integer tripType;
    private String departureDate;
    private String returnDate;
    private String passengers;
}
