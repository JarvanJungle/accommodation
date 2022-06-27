package com.torkirion.eroam.microservice.hirecars.apidomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CarSearchLocationDetail {

    private String supplierAddress;
    //private String supplierAccess;
    private String supplierLocationCode;
    private Boolean atAirport;
    //private Integer supplierAccessCode;

    private List<String> phones = new ArrayList<>();

    @ApiModelProperty(notes = "")
    private List<OperationSchedule> operationSchedules = new ArrayList<>();

    @Data
    public static class OperationSchedule {
        private String dayOfTheWeek;

        @ApiModelProperty("Opening time of the location. Format: HH:MM")
        private String OpeningTime;

        @ApiModelProperty("Closing time of the location. Format: HH:MM")
        private String closingTime;
    }
}
