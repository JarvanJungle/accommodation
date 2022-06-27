package com.torkirion.eroam.microservice.datadomain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "iata_airport")
public class IataAirportV2 {

    @Id
    private Integer openflightsIndex;

    private String airportName;

    private String cityname;

    private String country;

    private String dst;

    private String iataCode;

    private String icao;
}
