package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class SaveATrainStartBookRSDTO {
    private String remark;
    private Map<String, String> remarkDetail = new HashMap<>();
    private boolean isSuccess;
    private String internalItemReference;
    private SaveATrainConfirmSelectionRSDTO confirmSelectionRs;
    private SaveATrainMakeBookingRSDTO makeBookingRs;
    private SaveATrainBookingsConfirmRSDTO bookingsConfirmRs;
    private SaveATrainGetTicketRSDTO ticketRs;
}
