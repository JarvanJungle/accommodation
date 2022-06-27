package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CancelVerificationValue {
    private List<String> errors = new ArrayList<>();
    private boolean isCancelable = true;
    private CurrencyValue cancelationFee;
}
