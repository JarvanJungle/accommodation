package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class CountryStaticDTO {

    private String id;

    private String continent;

    private String name;

    private String region;

    private List<Region> regions = null;

    @Data
    @ToString
    public static class Region implements Serializable
    {
        private String code;

        private String name;
    }
}
