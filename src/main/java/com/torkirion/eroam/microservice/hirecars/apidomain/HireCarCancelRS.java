package com.torkirion.eroam.microservice.hirecars.apidomain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HireCarCancelRS {

    @ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
    private String internalBookingReference;
    private HireCarBookRS.BookingStatus status;
    private List<CancellationFree> cancellationFrees = new ArrayList<>();
    private String notes;

    @JsonIgnore
    private List<ResponseExtraInformation> errors = new ArrayList<>();

    @Data
    public static class CancellationFree {
        private CurrencyValue fee;
        private String description;
        private boolean taxInclusive;
        private boolean includedInRate;
    }
}
