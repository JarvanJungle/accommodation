package com.torkirion.eroam.microservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static  <T> T parse(String json, Class<T> clazz) {
        if(StringUtils.isEmpty(json)) return null;
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException ex) {
            log.error("Convert JSON ERROR: {}", ex.getMessage());
            return null;
        }
    }

    public static String convertToJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (IOException ex) {
            return null;
        }
    }

    public static String convertToPrettyJson(Object someObject) {
        String jsonOutput = gson.toJson(someObject);
        return jsonOutput;
    }
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
}
