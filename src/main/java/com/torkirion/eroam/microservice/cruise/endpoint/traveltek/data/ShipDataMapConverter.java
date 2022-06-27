package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traveltek.schemas.messages.Ship;

import javax.annotation.Resource;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class ShipDataMapConverter implements AttributeConverter<Ship, byte[]> {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public byte[] convertToDatabaseColumn(Ship ship) {
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsBytes(ship);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert to Json", e);
        }
    }

    @Override
    public Ship convertToEntityAttribute(byte[] json) {
        try {
            return objectMapper.readValue(json, Ship.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert from Json", e);
        }
    }
}

