package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import lombok.Data;
import lombok.Getter;

@Data
public class SaveATrainCancelRSDTO extends AbstractSaveATrainRSDTO {

    private String confirmationCode;
    private float cancelFee;

    @Override
    public boolean isSuccess() {
        return true;
    }
}
