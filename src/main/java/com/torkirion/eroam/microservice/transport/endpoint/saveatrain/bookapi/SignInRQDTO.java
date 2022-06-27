package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInRQDTO {
    private String email;
    private String password;
}
