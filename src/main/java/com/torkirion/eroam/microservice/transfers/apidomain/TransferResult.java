package com.torkirion.eroam.microservice.transfers.apidomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.SortedSet;

import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.ImageTag;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
public class TransferResult
{
	@Data
	@ToString
	public static class Review
	{
		@ApiModelProperty(notes = "The number of reviews")
		private Integer count;

		@ApiModelProperty(notes = "Average review value")
		private BigDecimal rating;
	}

	@Data
	@ToString
	public static class Luggage
	{
		private String inclusiveAllowance;
	}
	
	public static enum ImageTag
	{
		TRANSFER, SUPPLIER
	}

	@Data
	@ToString
	public static class Image 
	{
		private String imageURL;

		private ImageTag imageTag;

	}

	@ApiModelProperty(notes = "The start point of the transfer")
	private EndpointExtended startPoint;

	@ApiModelProperty(notes = "Optional pickup time", required = false)
	private LocalTime pickupTime;

	@ApiModelProperty(notes = "Optional pickup information", required = false)
	private String pickupInformation;

	@ApiModelProperty(notes = "The end point of the transfer")
	private EndpointExtended endPoint;

	@ApiModelProperty(notes = "The unique Code of this transfer service.")
	private String bookingCode;

	@ApiModelProperty(notes = "The unique code of this transfer service within the channel from which it originated", example = "123456")
	private String channelCode;

	@ApiModelProperty(notes = "The channel from which this transfer originated", example = "JAYRIDE")
	private String channel;

	//@ApiModelProperty(notes = "direcion of transfer for multi", example = "JAYRIDE")
	//private TransferDirection transferDirection;

	@ApiModelProperty(notes = "Whether the transfer is shared or private")
	private TransferType transferType;

	@ApiModelProperty(notes = "Optional vehicle description", required = false)
	private String vehicleDescription;

	@ApiModelProperty(notes = "A list of images for this transfer")
	private List<Image> imageUrls;

	@ApiModelProperty(notes = "A description of this transfer")
	private String transferDescription;

	@ApiModelProperty(notes = "The supplier of this transfer", required = false)
	private TransferSupplier supplier;

	@ApiModelProperty(notes = "The gross price a consumer pays for this offer")
	private CurrencyValue totalRate;

	@ApiModelProperty(notes = "The nett price that will be charged for this offer")
	private CurrencyValue supplyRate;

	@ApiModelProperty(notes = "If the product is refundable at time of booking")
	private Boolean refundableStatus;

	@ApiModelProperty(notes = "Machine readable cancellation policy")
	private SortedSet<TransferCancellationPolicyLine> cancellationPolicy;

	@ApiModelProperty(notes = "Human readable cancellation policy")
	private String cancellationPolicyText;

	@ApiModelProperty(notes = "General booking conditions")
	private String bookingConditions;

	@ApiModelProperty(notes = "Details on any loggauge policies")
	private Luggage luggageDetails;

	@ApiModelProperty(notes = "Maximum time the operator will wait, in ISO-8601 format", example = "PT45M")
	private String maxOperatorWaitTime;

	@ApiModelProperty(notes = "Service review values")
	private Review reviews;
}
