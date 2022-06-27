package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SearchApiSearchRSDTO {
    private int passengers;
    private Result result;
    private String errors;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private List<Route> inbound;
        private List<Route> outbound;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {

        @JsonProperty("origin_station")
        private String originStation;

        @JsonProperty("destin_station")
        private String destinStation;

        @JsonProperty("departure_date")
        private String departureDate;

        @JsonProperty("departure_time")
        private String departureTime;

        private String duration;

        @JsonProperty("arrival_date")
        private String arrivalDate;

        @JsonProperty("arrival_time")
        private String arrivalTime;

        private String changes;

        private String operator;

        private String connections;

        private Price price;

        @JsonProperty("train_numbers")
        private String trainNumbers;

        @JsonProperty("departure_connections")
        private String departureConnections;

        @JsonProperty("arrival_connections")
        private String arrivalConnections;

        @JsonProperty("layover_connections")
        private String layoverConnections;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price {
        @JsonProperty("first_class")
        private BigDecimal firstClass;

        @JsonProperty("second_class")
        private BigDecimal secondClass;

        @JsonProperty("business_class")
        private BigDecimal businessClass;
    }
}
