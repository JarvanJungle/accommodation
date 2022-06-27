package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi;

import lombok.Data;

@Data
public class UserSignInRSDTO {

    private User user;

    @Data
    public static class User {
        private String email;
        private String token;
    }
}
