package com.torkirion.eroam.microservice.activities.apidomain;

import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ActivityCancelRQ extends AbstractRQ {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "The source channel of this item, from the Room=>Source field in search results", example = "YALAGO", required = true)
    private String channel;

    @ApiModelProperty(notes = "A reference from the calling system for this item", required = true)
    private String internalBookingReference;

    @ApiModelProperty(notes = "The booking reference from the called system for this booking", required = true)
    private String bookingReference;
}
