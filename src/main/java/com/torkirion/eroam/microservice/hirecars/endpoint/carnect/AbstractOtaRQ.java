package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import lombok.Data;

@Data
public class AbstractOtaRQ<D> {

    @Data
    public static class Body<D> {
        private D d;
    }
}
