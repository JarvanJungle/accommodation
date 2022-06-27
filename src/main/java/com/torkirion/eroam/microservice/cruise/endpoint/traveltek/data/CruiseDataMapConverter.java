package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traveltek.schemas.messages.Cruise;

import javax.annotation.Resource;
import javax.persistence.*;
import java.io.IOException;

@Converter
public class CruiseDataMapConverter implements AttributeConverter<Cruise, byte[]> {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public byte[] convertToDatabaseColumn(Cruise cruise) {
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsBytes(cruise);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert to Json", e);
        }
    }

    @Override
    public Cruise convertToEntityAttribute(byte[] json) {
        try {
            return objectMapper.readValue(json, Cruise.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert from Json", e);
        }
    }
}

