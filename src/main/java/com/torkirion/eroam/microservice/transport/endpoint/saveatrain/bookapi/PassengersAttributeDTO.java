package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

public class PassengersAttributeDTO {
    private String title;
    private String fname;
    private String lname;
    private String birthdate;
    private String country;
    private String gender;

    public static class PassengerTypeAttribute {
        private String type;
    }

    private static enum PassengerType {
        Adult, Youth, Senior
    }
}
