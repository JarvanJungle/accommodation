package com.torkirion.eroam.microservice.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class TimeZoneUtil {

    private static final BigDecimal BIG_DECIMAL_24 = new BigDecimal(24);
    private static final BigDecimal BIG_DECIMAL_360 = new BigDecimal(360);

    public static BigDecimal getTimeZoneOffsetByLongitude(BigDecimal longitude) {
        try {
            return longitude.multiply(BIG_DECIMAL_24).divide(BIG_DECIMAL_360,0, RoundingMode.DOWN);
        } catch (Exception e) {
            log.error("getTimeZoneOffsetByLongitude::(longitude: {}, error: {})", longitude, e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
