package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import lombok.Data;

@Data
public class SaveATrainStartCancelRSDTO {
    private boolean isSuccess;
    private String errors;
    private float cancelFee;
    private String confirmationCode;
}
