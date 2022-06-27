package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class AccommodationRC implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8446577586244257235L;

	@ToString
	public static enum ImageTag
	{
		GENERAL, HOTEL, ROOM
	}

	@ToString
	public static enum AccommodationTypeTag
	{
		HOTEL, APARTMENT, HOSTEL, RESORT, CAMPING, HOME, RURAL
	}

	@Getter
	@Setter
	@ToString
	public static class Image implements Serializable, Comparable<Image>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1129972655156447536L;

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((channelCode == null) ? 0 : channelCode.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Image other = (Image) obj;
			if (channelCode == null)
			{
				if (other.channelCode != null)
					return false;
			}
			else if (!channelCode.equals(other.channelCode))
				return false;
			return true;
		}

		@ApiModelProperty(value = "Internal code, often set by channel, non-unique")
		private String channelCode;

		private Integer imageOrder;

		private String imageURL;

		private ImageTag imageTag;

		@ApiModelProperty(value = "for ROOM images, refers to the roomcode of this room, if available")
		private String tagCode;

		private String imageDescription;

		@Override
		public int compareTo(Image other)
		{
			return imageOrder.compareTo(other.imageOrder);
		}
	}

	@Data
	@ToString
	public static class Distance implements Serializable
	{
		@ApiModelProperty(value = "Nearby landmark")
		private String landmark;

		@ApiModelProperty(value = "Distance to landmark in kilometers")
		private BigDecimal kilometers;
	}
	
	@Data
	@ToString
	public static class FacilityGroup implements Serializable
	{
		@ApiModelProperty(value = "Groupings such as Hotel Facililties, Recreational Facilties. Various from channel to channel.")
		private String groupName;

		private List<String> facilities = new ArrayList<String>();;
	}

	@Data
	@ToString(callSuper = true)
	public static class GeoCoordinates extends LatitudeLongitude
	{
		@ApiModelProperty(notes = "Co-ordinate accuracy, in meters (https://developers.google.com/maps/documentation/geolocation/overview)", required = false)
		private BigDecimal geoAccuracy;
	}

	@Data
	@ToString
	public static class Address implements Serializable
	{
		@ApiModelProperty(notes = "The full address of this property. May be provided if a street/postcoide etc breakdown is not available", example = "YL123456")
		private String fullFormAddress;

		@ApiModelProperty(notes = "The street part of the address", required = false)
		private String street;

		@ApiModelProperty(notes = "The postcode part of the address", required = false)
		private String postcode;

		@ApiModelProperty(notes = "The city part of the address", required = false)
		private String city;

		@ApiModelProperty(notes = "The state part of the address", required = false)
		private String state;

		@ApiModelProperty(notes = "The countryId part of the address", required = true)
		private String countryCode;

		@ApiModelProperty(notes = "The country part of the address", required = true)
		private String countryName;

		@ApiModelProperty(notes = "The geocordinates of the property", required = false)
		private GeoCoordinates geoCoordinates;

	}

	@ApiModelProperty(notes = "The unique code of this property within the service", example = "YL123456")
	private String code;

	@ApiModelProperty(notes = "The unique code of this property within the channel from which it originated", example = "123456")
	private String channelCode;

	@ApiModelProperty(notes = "The channel from which this property record originated", example = "YALAGO")
	private String channel;

	@ApiModelProperty(notes = "The title of this property", example = "Los Angeles hilton")
	private String accommodationName;

	@ApiModelProperty(notes = "The style of property", example = "APARTMENT")
	private AccommodationTypeTag productType;

	@ApiModelProperty(notes = "A 'short' introduction to the hotel", required = true)
	private String introduction;

	@ApiModelProperty(notes = "A longer description of the hotel, if available", required = false)
	private String description;

	@ApiModelProperty(notes = "The chain this property belongs to, if applicable", required  = false)
	private String chain;

	@ApiModelProperty(notes = "The hotel category, only used for IMS hotels currently", required  = false)
	private String category;

	@ApiModelProperty(notes = "The address of the property")
	private Address address;

	@ApiModelProperty(notes = "The rating of the property")
	private BigDecimal rating;

	@ApiModelProperty(notes = "A description of the rating.")
	private String ratingText;

	@ApiModelProperty(notes = "The destination code of the property, as relevant to the channel, if applicable", required = false)
	private String internalDestinationCode;

	@ApiModelProperty(notes = "The telephone number of this property", required = false)
	private String phone;

	@ApiModelProperty(notes = "The general enquiry email address of this property", required = false)
	private String email;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@ApiModelProperty(notes = "the date and time this record was last updated", required = true)
	private LocalDate lastUpdate;

	@ApiModelProperty(notes = "General static information about renovations, or other important information relevant to the customer. Dynamic errata also appear against search results", required = false)
	private List<String> errata = new ArrayList<>();

	@ApiModelProperty(notes = "Images for this property", required = false)
	private SortedSet<Image> images = new TreeSet<>();

	@ApiModelProperty(notes = "The thumbnail image for this property", required = false)
	private Image imageThumbnail;

	@ApiModelProperty(notes = "Distances to nearby landmarks (airports etc)", required = false)
	private List<Distance> landmarkDistances = new ArrayList<>();

	@ApiModelProperty(value = "Normal checkin time hh:mm", example = "14:00", required = false)
	private String checkinTime;

	@ApiModelProperty(value = "Normal checkout time hh:mm", example = "11:00", required = false)
	private String checkoutTime;

	@ApiModelProperty(notes = "Bullet list of facilties at this property", required = false)
	private List<FacilityGroup> facilityGroups = new ArrayList<>();

	@ApiModelProperty(notes = "The Olery company code for this property", required = false)
	private Long oleryCompanyCode;
}
