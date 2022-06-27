package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CTWItineraryShopRS {

    private CTWCommon.DataLibrary dataLibrary;
    private String type;
    private List<CTWCommon.ItineraryOffer> itineraryOffers;
    private CTWCommon.ResponseStatus responseStatus;
}
