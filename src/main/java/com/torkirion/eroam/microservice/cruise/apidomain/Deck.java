package com.torkirion.eroam.microservice.cruise.apidomain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Deck implements Serializable {
    protected Integer id;
    protected String caption;
    protected String description;
    protected String imageId;
    protected String imageUrl;
    protected String name;
}
