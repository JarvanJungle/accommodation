package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartCancelRS {
    private String bookingId;
    private String status;
    private String reason;
//    {
//        "bookingId": "BR-584379986",
//            "status": "ACCEPTED"
//    }
}
