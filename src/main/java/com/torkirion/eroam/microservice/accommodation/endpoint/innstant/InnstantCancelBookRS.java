package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.Customer;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.PaymentMethodDTO;
import lombok.Data;

import java.util.List;

@Data
public class InnstantCancelBookRS
{
    @Data
    public static class Content
    {
        @JsonProperty(value = "Warning")
        private List<String> warning;
        @JsonProperty(value = "Status")
        private String status;
        @JsonProperty(value = "Message")
        private String message;
        @JsonProperty(value = "ProfileVersion")
        private String profileVersion;
        @JsonProperty(value = "Data")
        private String data;
    }
    private Content content;
    private String status;
    private String errorCode;
    private String errorMessage;

}
