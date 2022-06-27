package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveATrainBookingsConfirmRQDTO {

    private Booking booking;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class Booking {
        @JsonProperty("search_identifier")
        private String searchIdentifier;
    }
}
