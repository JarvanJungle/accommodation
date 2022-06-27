package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class SaveATrainStartChooseRSDTO {
    SaveATrainBookApiSearchRSDTO.Result chosenResult;
    private SaveATrainBookApiSubRouteRSDTO subRouteRS;
}
