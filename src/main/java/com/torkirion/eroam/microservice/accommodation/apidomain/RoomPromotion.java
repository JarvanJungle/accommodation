package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoomPromotion implements Serializable
{
    public static enum PromotionType
    {
    	MEAL, VALUEADD, DISCOUNT_OFFER;
    }

	@ApiModelProperty(notes = "The type of promotion")
    private PromotionType promoType;
    
	@ApiModelProperty(notes = "Short description of this promotion")
	private String shortMarketingText;
    
	@ApiModelProperty(notes = "Any terms and conditions around this promotion")
    private String termsAndConditions;
    
	@ApiModelProperty(notes = "Any special actions customer must take to fulfil/obtain this promotion")
    private String customerFulfillmentRequirements;
}
