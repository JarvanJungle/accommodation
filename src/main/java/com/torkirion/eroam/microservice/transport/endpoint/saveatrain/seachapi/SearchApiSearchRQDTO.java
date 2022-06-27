package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//Paris/London?triptype=2&passengers=1&ddate=2021-10-28&rdate=2021-10-30&email={{search_email}}&password={{search_password}}
//[origin]/[destination]?triptype=&passengers=&ddate=&rdate=&token=Token XXXX
@Getter
@Builder
public class SearchApiSearchRQDTO {
    private String origin;
    private String destination;

    /*-
     triptype = 1: one way
     triptype = 2: round trip
     -*/
    @JsonProperty("triptype")
    private Integer tripType;

    private String departureDate;

    private String returnDate;

    private String passengers;
}
