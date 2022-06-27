package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public abstract class AbstractSaveATrainRSDTO {
    protected String errors;
    public abstract boolean isSuccess();
}
