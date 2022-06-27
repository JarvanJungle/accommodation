package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductRC implements Serializable
{
	@Data
	public static class TicketInfo
	{
		private List<String> ticketTypes;
		private String ticketTypeDescription;
		private String ticketsPerBooking;
		private String ticketsPerBookingDescription;
	}
	
	@Data
	public static class AgeBand
	{
		private String ageBand;
		private Integer startAge;
		private Integer endAge;
		private Integer minTravelersPerBooking;
		private Integer maxTravelersPerBooking;
	}
	
	@Data
	public static class PricingInfo
	{
		private String type;
		private List<AgeBand> ageBands;
	}
	
	@Data
	public static class ImageVariant
	{
		private Integer height;
		private Integer width;
		private String url;
	}
	
	@Data
	public static class Image
	{
		private String imageSource;
		private String caption;
		private Boolean isCover;
		private List<ImageVariant> variants;
	}
	
	@Data
	public static class Redemption
	{
		private String redemptionType;
		private String specialInstructions;
	}
	
	@Data
	public static class LocationRef
	{
		private String ref;
	}
	
	@Data
	public static class TravelerPickupLocation
	{
		private LocationRef location;
		private String pickupType;
	}
	
	@Data
	public static class TravelerPickup
	{
		private String pickupOptionType;
		private Boolean allowCustomTravelerPickup;
		private List<TravelerPickupLocation> locations;
		private Integer minutesBeforeDepartureTimeForPickup;
		private String additionalInfo;
	}
	
	@Data
	public static class Logistics
	{
		private Redemption redemption;
		private TravelerPickup travelerPickup;
	}
	
	@Data
	public static class InclusionExclusion
	{
		private String category;
		private String categoryDescription;
		private String type;
		private String typeDescription;
		private String description;
		private String otherDescription;
	}
	
	@Data
	public static class AdditionalInfo
	{
		private String type;
		private String description;
	}
	
	@Data
	public static class RefundEligibility
	{
		private Integer dayRangeMin;
		private Integer dayRangeMax;
		private Integer percentageRefundable;
	}
	
	@Data
	public static class CancellationPolicy
	{
		private String type;
		private String description;
		private Boolean cancelIfBadWeather;
		private Boolean cancelIfInsufficientTravelers;
		private List<RefundEligibility> refundEligibility;
	}
	
	@Data
	public static class BookingConfirmationSettings
	{
		private String bookingCutoffType;
		private Integer bookingCutoffInMinutes;
		private String confirmationType;
	}
	
	@Data
	public static class BookingRequirements
	{
		private Integer minTravelersPerBooking;
		private Integer maxTravelersPerBooking;
		private Boolean requiresAdultForBooking;
	}
	
	@Data
	public static class LanguageGuide
	{
		private String type;
		private String language;
		private String legacyGuide;
	}
	
	@Data
	public static class Destination
	{
		private String ref;
		private Boolean primary;
	}
	
	@Data
	public static class ItineraryDuration
	{
		private Integer fixedDurationInMinutes;
		private Integer variableDurationFromMinutes;
		private Integer variableDurationToMinutes;
	}
	
	@Data
	public static class PointOfInterestLocation
	{
		private LocationRef location;
		private Integer attractionId;
	}
	
	@Data
	public static class ItineraryItem
	{
		private PointOfInterestLocation pointOfInterestLocation;
		private ItineraryDuration duration;
		private Boolean passByWithoutStopping;
		private String admissionIncluded;
		private String description;
	}
	
	@Data
	public static class Itinerary
	{
		private String itineraryType;
		private Boolean skipTheLine;
		private Boolean privateTour;
		private ItineraryDuration duration;
		private List<ItineraryItem> itineraryItems;
	}
	
	@Data
	public static class ProductOption
	{
		private String productOptionCode;
		private String description;
		private String title;
		private List<LanguageGuide> languageGuides;
	}
	
	@Data
	public static class TranslationInfo
	{
		private Boolean containsMachineTranslatedText;
	}
	
	@Data
	public static class Supplier
	{
		private String name;
	}
	
	@Data
	public static class Product
	{
		private String status;
		private String productCode;
		private String language;
		private String createdAt;
		private String lastUpdatedAt;
		private String title;
		private TicketInfo ticketInfo;
		private PricingInfo pricingInfo;
		private List<Image> images;
		private Logistics logistics;
		private String timeZone;
		private String description;
		private List<InclusionExclusion> inclusions;
		private List<InclusionExclusion> exclusions;
		private List<AdditionalInfo> additionalInfo;
		private CancellationPolicy cancellationPolicy;
		private BookingConfirmationSettings bookingConfirmationSettings;
		private BookingRequirements bookingRequirements;
		private List<LanguageGuide> languageGuides;
		private List<String> bookingQuestions;
		private List<Integer> tags;
		private List<Destination> destinations;
		private Itinerary itinerary;
		private List<ProductOption> productOptions;
		private TranslationInfo translationInfo;
		private Supplier supplier;
	}
	
	@JsonProperty("products")
	private List<Product> products;
	
	@JsonProperty("nextCursor")
	private String nextCursor;

}
