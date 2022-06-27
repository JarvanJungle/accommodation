package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import lombok.Data;

@Data
public class CTWBookRQ {
    private CTWCommon.ItineraryOffer itineraryOffer;
    private CTWCommon.DataLibrary dataLibrary;
}
