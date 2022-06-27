package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Address;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccommodationProperty implements Serializable
{
	@ApiModelProperty(notes = "The unique Code of this property. Full static data can be fetched through 'loadRichContent'", example = "YL12345")
	private String code;

	@ApiModelProperty(notes = "The unique code of this property within the channel from which it originated", example = "123456")
	private String channelCode;

	@ApiModelProperty(notes = "The channel from which this property record originated", example = "YALAGO")
	private String channel;

	@ApiModelProperty(notes = "The name of the property", example = "Hilton Sydney")
	private String accommodationName;

	@ApiModelProperty(notes = "Additional information regarding the hotel", example = "'Renovations occuring in December, pool will be closed.'")
	private List<String> errata = new ArrayList<>();

	@ApiModelProperty(notes = "A thumbnail image of the property")
	private String imageThumbnailUrl;

	@ApiModelProperty(notes = "The rating of the property")
	private BigDecimal rating;

	@ApiModelProperty(notes = "A description of the rating.")
	private String ratingText;

	@ApiModelProperty(notes = "A 'short' introduction to the hotel", required = true)
	private String introduction;

	@ApiModelProperty(notes = "The address of the property")
	private Address address;
	
	@ApiModelProperty(notes = "The Olery company code for this property.  May be null if this property is not mapped by Olery", required = false)
	private Long oleryCompanyCode;
	
	@ApiModelProperty(notes = "The Olery review data for this property, if any exists", required = false)
	private OleryAccommodationData oleryData;
}
