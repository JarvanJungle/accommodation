package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignInRSDTO {
    //private String name;
    private String email;
    @JsonProperty("access_token")
    private String token;
}
