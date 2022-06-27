package com.torkirion.eroam.microservice.transport.apidomain;

import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransportChooseRQ extends AbstractRQ {

    private static final long serialVersionUID = 6427209212711367007L;

    @ApiModelProperty(notes = "The source channel of this item", example = "SAVEATRAIN", required = true)
    private String channel = SaveATrainService.CHANNEL;

    @ApiModelProperty(notes = "item id of search response", required = true)
    private String chosenRouteId;

    private AvailTransportSearchRQ.TotalPassenger totalPassenger;
}
