package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SaveATrainCancelRQDTO {
    private String confirmationCode;
}
