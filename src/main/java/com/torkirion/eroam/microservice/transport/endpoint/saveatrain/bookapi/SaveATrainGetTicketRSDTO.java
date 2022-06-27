package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveATrainGetTicketRSDTO extends AbstractSaveATrainRSDTO {

    private String url;

    @JsonProperty("confirmation_code")
    private String confirmationCode;

    private String status;

    @Override
    public boolean isSuccess() {
        return "completed".equals(status);
    }
}
