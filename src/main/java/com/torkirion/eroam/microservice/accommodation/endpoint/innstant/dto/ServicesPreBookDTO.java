package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.InnstantPreBookRQ;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ServicesPreBookDTO {
    private List<InnstantPreBookRQ> services;
}
