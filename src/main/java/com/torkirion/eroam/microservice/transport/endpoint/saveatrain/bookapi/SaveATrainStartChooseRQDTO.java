package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SaveATrainStartChooseRQDTO {
    @ApiModelProperty(notes = "format yyyy-MM-dd HH:mm")
    private String departureDatetime;
    private String originStationUid;
    private String destinationStationUid;
    private TravellerMix travellers;
}
