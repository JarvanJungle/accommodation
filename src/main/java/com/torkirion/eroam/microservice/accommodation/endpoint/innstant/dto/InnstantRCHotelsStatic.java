package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InnstantRCHotelsStatic {

    private Integer id;
    private String name;
    private String address;
    private Integer status;
    private String zip;
    private String phone;
    private Integer stars;
    private String fax;
    private BigDecimal lat;
    private BigDecimal lon;
    private String seoname;
    private String description;
    private Integer mainImageId;
    private List<Destination> destinations = null;
    private List<Surrounding> surroundings = null;
    private Facilities facilities;
    private List<Image> images = null;
}
