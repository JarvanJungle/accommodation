package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveATrainStartCancelRQDTO {
    private String confirmationCode;
}
