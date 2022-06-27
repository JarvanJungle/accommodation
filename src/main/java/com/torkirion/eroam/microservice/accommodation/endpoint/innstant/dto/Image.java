package com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Image {

    private Integer id;
    private Integer width;
    private Integer height;
    private String title;
    private String url;
}
