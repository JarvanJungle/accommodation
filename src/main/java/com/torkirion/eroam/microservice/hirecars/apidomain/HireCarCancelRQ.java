package com.torkirion.eroam.microservice.hirecars.apidomain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@ToString
@Data
public class HireCarCancelRQ extends AbstractRQ implements Serializable {
    @ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
    private String internalBookingReference;

    @ApiModelProperty(notes = "The channel field from the Search structure", required = true, example = "CARNECT")
    private String channel;

    @ApiModelProperty(notes = "get the bookingReference property of HireCarBookRS", required = true, example = "Test_abcd")
    private String bookingReference;

    @ApiModelProperty(notes = "The contact details of the person making the booking", required = true)
    private Booker booker;

    @Data
    public static class Booker {
        @ApiModelProperty(notes = "The title of the booking contact", example = "Mr", required = false)
        private String title;

        @ApiModelProperty(notes = "The given (or first) name of the booking contact", example = "John", required = false)
        private String givenName;

        @ApiModelProperty(notes = "The family name (or surname) name of the booking contact", example = "Smith", required = true)
        private String surname = "";

        @ApiModelProperty(notes = "The telephone numnber of the booking contact", example = "0414555666", required = false)
        private String telephone;

        @ApiModelProperty(notes = "The email of the booking contact", example = "test@gmail.com", required = false)
        private String email;

        @ApiModelProperty(notes = "The birthDate of the booking contact", example = "1991-01-01", required = false)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate birthDate;

        @ApiModelProperty(notes = "The address of the booking contact", required = false)
        private HireCarBookRQ.Address address;
    }
}
