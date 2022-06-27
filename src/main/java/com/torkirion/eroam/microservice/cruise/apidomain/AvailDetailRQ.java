package com.torkirion.eroam.microservice.cruise.apidomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class AvailDetailRQ implements Serializable {
    @ApiModelProperty(required = true)
    private Integer cruiseId;
    @ApiModelProperty(notes = "Allows limiting the results to be only from a given channel", required = true)
    private String channel;
    @ApiModelProperty(notes = "Channels that should NOT be used", required = false)
    private List<String> channelExceptions;
    @ApiModelProperty(notes = "The country Code of origin", required = true, example = "au")
    private String countryCodeOfOrigin;
}
