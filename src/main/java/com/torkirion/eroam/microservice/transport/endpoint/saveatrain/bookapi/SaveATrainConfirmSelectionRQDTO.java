package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class SaveATrainConfirmSelectionRQDTO {

    @JsonProperty("select_results_attributes")
    private SelectResultsAttributes selectResultsAttributes;

    @Builder
    @Getter
    public static class SelectResultsAttributes {

        @JsonProperty("search_identifier")
        private String searchIdentifier;

        @JsonProperty("result_id")
        private int resultId;

        @JsonProperty("transfers_attributes")
        private Map<Integer, TransfersAttribute> transfersAttributes;


    }

    @Builder
    @Getter
    public static class TransfersAttribute {
        private int id;

        @JsonProperty("fare_id")
        private int fareId;
    }
}
