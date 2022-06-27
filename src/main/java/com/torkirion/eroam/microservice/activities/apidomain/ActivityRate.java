package com.torkirion.eroam.microservice.activities.apidomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ActivityRate {

    private String rateCode;
    private String rateClass;
    private String name;

    @ApiModelProperty(notes = "key to book activity")
    private String rateKey;
    private boolean freeCancellation = false;
}
