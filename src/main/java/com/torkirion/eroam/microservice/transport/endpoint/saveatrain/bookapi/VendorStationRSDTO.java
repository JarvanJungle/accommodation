package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class VendorStationRSDTO {
    private String uid;
    private String name;
    private Boolean searchable;
    private String location;

    public BigDecimal getLatitude() {
        if(location == null || StringUtils.isBlank(location)) {
            return null;
        }
        String[] split = location.split(",");
        try {
            String longitudeStr = split[0].trim();
            if(longitudeStr.length() > 7) {
                longitudeStr = longitudeStr.substring(0, 7);
            }
            return new BigDecimal(longitudeStr).setScale(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getLongitude() {
        if(location == null || StringUtils.isBlank(location)) {
            return null;
        }
        String[] split = location.split(",");
        if(split.length < 2) {
            return null;
        }
        try {
            String longitudeStr = split[1].trim();
            if(longitudeStr.length() > 7) {
                longitudeStr = longitudeStr.substring(0, 7);
            }
            return new BigDecimal(longitudeStr).setScale(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
