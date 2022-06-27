package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HotelInfoDTO {

    private String id;
    private String name;
    private String city;
    private String address;
    private String zip;
    private String phone;
    private String fax;
    private String rating;
    private String latitude;
    private String longitude;
    
}
