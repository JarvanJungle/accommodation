package com.torkirion.eroam.microservice.cruise.apidomain;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CruiseLine implements Serializable {

    private Integer cruiseLineId;

    private String cruiselineCode;

    private String cruiselineName;

    @ApiModelProperty(notes = "Images for this cruiseLIne", required = false)
    private SortedSet<Image> images = new TreeSet<>();

    @ApiModelProperty(notes = "The thumbnail image for this cruiseLine", required = false)
    private Image imageThumbnail;
}
