package com.torkirion.eroam.microservice.accommodation.apidomain;


import java.io.Serializable;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoomExtraFee implements Serializable
{
    public static enum FeeType
    {
    	CheckinFees, CheckoutFees, BookingFees, FeeComments;
    }
    
	@ApiModelProperty(notes = "The fee for this extra")
    private CurrencyValue fee;
    
	@ApiModelProperty(notes = "Description of this extra fee")
    private String description;

	@ApiModelProperty(notes = "When this fee needs to be paid")
    private FeeType feeType = FeeType.CheckoutFees;
}
