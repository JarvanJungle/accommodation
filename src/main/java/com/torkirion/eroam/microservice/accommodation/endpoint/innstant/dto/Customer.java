package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Customer
{
    private ContactDTO contact;
    private String birthDate = "1980-01-01";
    private NameDTO name;
    /**
     * M for male, F for female or children, MR / MRS / MS
     */
    private String title;
}
