package com.torkirion.eroam.microservice.cruise.apidomain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class CabinType implements Serializable {
    protected Integer id;
    protected String cabinCode;
    protected String type;
    protected String caption;
    protected String colourCode;
    protected String description;
    protected String imageUrl;
    protected String isDefault;
    protected String name;
    protected String originalImageUrl;
    protected String smallImageUrl;
    protected Float sortWeight;
}
