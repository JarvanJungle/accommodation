package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class SaveATrainStartBookRQDTO {
    @Setter
    private String internalBookingReference;

    @Setter
    private String internalItemReference;

    private String searchIdentifier;

    private int searchResultId;

    private Map<Integer, SaveATrainConfirmSelectionRQDTO.TransfersAttribute> transfersAttributes;

    @Setter
    private SaveATrainMakeBookingRQDTO.Booking booking;

    public void serializeTransportCode(String transportCode) {
        //transportCode format:  searchIdentifier|searchResultId|transferId|fareId
        if(log.isDebugEnabled()) {
            log.debug("serializeTransportCode::transportCode: {}", transportCode);
        }
        if(transportCode == null || "".equals(transportCode)) {
            return;
        }
        String[] values = transportCode.split("\\|");
        if(log.isDebugEnabled()) {
            log.debug("serializeTransportCode::values: {}", values);
        }
        if(values.length != 4) {
            return;
        }
        this.searchIdentifier = values[0];
        this.searchResultId = Integer.parseInt(values[1]);
        String transferId = values[2];
        String fareId = values[3];
        this.transfersAttributes = new HashMap<>();
        this.transfersAttributes.put(0, SaveATrainConfirmSelectionRQDTO.TransfersAttribute.builder()
                .id(Integer.parseInt(transferId))
                .fareId(Integer.parseInt(fareId))
                .build());
        if(this.booking != null) {
            this.booking.setSearchIdentifier(values[0]);
        }
    }
}
