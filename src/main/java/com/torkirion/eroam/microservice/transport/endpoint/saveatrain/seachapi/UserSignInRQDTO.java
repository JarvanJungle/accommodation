package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSignInRQDTO {
    private String email;
    private String password;
}
