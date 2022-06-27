package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactDTO
{
    private String address;
    private String city;
    private String country;
    private String email;
    private String phone;
    private String state = "N/A";
    private String zip;
}
