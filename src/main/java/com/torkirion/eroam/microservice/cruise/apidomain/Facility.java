package com.torkirion.eroam.microservice.cruise.apidomain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class Facility implements Serializable {
    private String categoryName;
    private List<String> facilityList;
}
