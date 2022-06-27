package com.torkirion.eroam.microservice.activities.apidomain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class ActivityCancelRS implements Serializable  {
    public ActivityCancelRS(String confirmationCode, CurrencyValue cancellationCharge)
    {
        this.confirmationCode = confirmationCode;
        this.cancellationCharge = cancellationCharge;
    }

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "The cancellation confirmation code", required = true)
    private String confirmationCode;

    @ApiModelProperty(notes = "The charge (fee) for this cancellation", required = true)
    private CurrencyValue cancellationCharge;

    @JsonIgnore
    private List<ResponseExtraInformation> errors = new ArrayList<>();
}
