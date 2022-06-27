package com.torkirion.eroam.microservice.cruise.apidomain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
public class Price implements Serializable {
    private String marker;
    private String name;
    private BigDecimal value;
}
