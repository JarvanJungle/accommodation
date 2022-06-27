package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveATrainBookingsConfirmRSDTO extends AbstractSaveATrainRSDTO {

    @JsonProperty("search_identifier")
    private String searchIdentifier;

    @JsonProperty("issuing_info")
    private String issuingInfo;

    @Override
    public boolean isSuccess() {
        return searchIdentifier != null;
    }
}
