package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartCheckingCancelRQ {
    private String bookingItemReference;
}
