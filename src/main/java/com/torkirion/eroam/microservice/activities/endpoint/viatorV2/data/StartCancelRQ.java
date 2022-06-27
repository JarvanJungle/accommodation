package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartCancelRQ {
    private String bookingItemReference;
}
