package com.torkirion.eroam.microservice.transport.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
public class TransportRS extends AbstractRQ implements Serializable 
{
	private static final long serialVersionUID = 6427209212711367007L;

	private String id;

	private String type;

	private String provider;

	private List<ItineraryPricingInfo> itineraryPricingInfo = new ArrayList<>();

	@ApiModelProperty(notes = "use bookingCode to book rail")
	private List<Segment> segments = new ArrayList<>();

	@ApiModelProperty(notes = "CommonData for the segments - there must be one CommonData for each Segment int he segments array")
	private List<CommonData> commonDatas = new ArrayList<>();
	
	@Deprecated
	private CommonData commonData = new CommonData();

	private Duration duration = new Duration();

	private String duration_time;

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class ItineraryPricingInfo
	{
		private String channel;

		private String ref;

		@ApiModelProperty(notes = "use bookingCode to book rail")
		private String bookingCode = "";

		private ItineraryPricingInfoPricing pricing = new ItineraryPricingInfoPricing();

		private Cabin cabin = new Cabin();

		private SeatsRemaining seatsRemaining = new SeatsRemaining();

		private String fareSourceCode;

		private BaggageDetails baggageDetails = new BaggageDetails();

		private List<AddOn> addOns = new ArrayList<>();

		private Data data = new Data();

		private Extra extra = new Extra();

		private List<ItineraryPricingInfo> additionalFares = new ArrayList<>();
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class ItineraryPricingInfoPricing
	{
		private PricePerPax pricePerPax = new PricePerPax();

		private ItineraryPrice itineraryPrice = new ItineraryPrice();

		private Taxes taxes = new Taxes();

		private List<Taxes> listOfTaxes = new ArrayList<>();

		private List<BrandName> brandNames = new ArrayList<>();
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class PricePerPax
	{
		private AgeGroupings ageGroupings = new AgeGroupings();

		private Integer decimalPlaces;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class AgeGroupings
	{
		private AdultPriceGroup adult = new AdultPriceGroup();

		private ChildPriceGroup child = new ChildPriceGroup();

		private InfantPriceGroup infant = new InfantPriceGroup();
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CurrencyAmount
	{
		private String currency;

		private BigDecimal amount;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class ItineraryPrice
	{
		private BigDecimal amount;

		private PriceGroup baseGroup = new PriceGroup();

		private PriceGroup totalGroup = new PriceGroup();

		private PriceGroup taxGroup = new PriceGroup();

		private Integer decimalPlaces;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class PriceGroup
	{
		private CurrencyAmount supplyPrice;

		private CurrencyAmount netPrice;

		private CurrencyAmount retailPrice;
	}
	
	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class AdultPriceGroup extends PriceGroup
	{
		private Integer totalAdult = 0;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class ChildPriceGroup extends PriceGroup
	{
		private Integer totalChild = 0;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class InfantPriceGroup extends PriceGroup
	{
		private Integer totalInfant = 0;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class Taxes
	{
		private String code = "TOTALTAX";

		private BigDecimal amount = BigDecimal.ZERO;

		private BigDecimal netAmount = BigDecimal.ZERO;

		private BigDecimal retailAmount = BigDecimal.ZERO;

		private String currency;

		private String description = "";
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class BrandName
	{
		private String passengerType;

		private BigDecimal brandID;

		private BigDecimal brandName;

		private List<BrandSegment> segments;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class BrandSegment
	{
		private Integer LegIndex;

		private Integer FlightIndex;
	}

	public static enum CabinClassBucketPax
	{
		adult, child, infant;
	}
	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class CabinClassBucket
	{
		private Integer segment;

		private CabinClassBucketPax passenger;

		private String fareBucket;

		private String mealCode;

		private String cabinClass;

		private String cabinClassText;

		private Integer seatsRemaining;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class Cabin
	{
		private String cabinClassCode;

		private List<CabinClassBucket> cabinClassBucket = new ArrayList<>();

		@ApiModelProperty(notes = "A quick list of either the adult or child segment fareBucket codes, comma seperated", example = "X,X")
		private String list;
		
		private String cabinClassText;

		private CabinPenalties cabinPenalties;

		private String cabinFareMessages;

		private List<String> cabinBrandName = new ArrayList<>();
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class CabinPenalties
	{
		private String types;

		private List<CabinPenalty> penalties;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class CabinPenalty
	{
		private String passenger;

		private String type;

		private String applicability;

		private Boolean refundable;

		private Boolean changeable;

		private BigDecimal amount;

		private String currencyCode;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class SeatsRemaining
	{
		private Boolean belowMinimum = false;

		private Integer number = 0;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class BaggageDetails
	{
		private String types;

		private List<CheckInBaggage> checkInBaggage; // for each freeBaggageAllowance and paidBaggage with type A

		private List<CabinBaggage> cabinBaggage; // for each freeBaggageAllowance and paidBaggage with type B
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class CheckInBaggage
	{
		private String types;

		private List<Integer> segments;

		private List<Bag> bags;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class CabinBaggage
	{
		private String types;

		private List<Integer> segments;

		private List<Bag> bags;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class Bag
	{
		private Integer pieces;

		private Integer weight;

		private String unit;

		private String description;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class AddOn
	{
		private String name;

		private BigDecimal price;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class Data
	{
		private Boolean isPassportMandatory = false;

		private String ticketType = "eTicket";
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class Extra
	{
		private Boolean isRefundable;

		private Boolean isRefundableTag;

		private Boolean isSoldOut;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class Segment
	{
		private List<Leg> legs = new ArrayList<>();
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class Leg
	{
		private String arrivalAirportLocationCode;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime arrivalDateTime;

		private String cabinClassCode;

		private String cabinClassText;

		private String departureAirportLocationCode;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime departureDateTime;

		private Boolean eticket;

		private String flightNumber;

		private Integer journeyDuration;

		private Integer legIndicator;

		private String marketingAirlineCode;

		private String marriageGroup;

		private String mealCode;

		private OperatingAirline operatingAirline;

		private String resBookDesigCode;

		private String resBookDesigText;

		private SeatsRemaining seatsRemaining;

		private Integer stopQuantity;

		private StopQuantityInfo stopQuantityInfo;

		private String marketingAirlineName;

		private String arrivalData;

		private String departureData;

		private Integer layOverTime;

		private String layOverTimeText;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
		private LocalTime arrivalTime;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
		private LocalTime departureTime;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
		private LocalDate arrivalDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
		private LocalDate departureDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate checkInDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate checkOutDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEEE")
		private LocalDate departDay;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEEE")
		private LocalDate arrivalDay;

		private CityInfo cityInfo;

		private String departureLocation;

		private String arrivalLocation;

		private String departureTerminal;

		private String arrivalTerminal;

		private String flightDuration;

		private String totalDuration;

		private String flightStop;

		private String stopDetail;

		private Integer stopCount;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class CityInfo
	{
		private String departureCity;

		private String arrivalCity;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class OperatingAirline
	{
		private String Code;

		private String Equipment;

		private String FlightNumber;

		private String Name;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class StopQuantityInfo
	{
		private String arrivalDateTime;

		private String departureDateTime;

		private Integer duration;

		private String locationCode;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class CommonData
	{
		private String fareSourceCode;

		private String fareType;

		private String isRefundable;

		private String sequenceNumber;

		private String directionInd;

		private String isPassportMandatory;

		private String ticketType;

		private String validatingAirlineCode;

		private String provider;

		private String transportTypeName;

		private String carrier;

		private String slug;

		private String CabinClassCode;

		private String cabinClassText;

		private String operatingAirlineCode;

		private String flightNumber;

		private String OperatingAirlineName;

		private String MarketingAirlineName;

		private String arrivalText;

		private String departureText;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime eta;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime etd;

		private Integer layOverTime;

		private Integer flightDuration;

		private Integer total_duration;

		private String layOverTimeText;

		private String totalDurationText;

		private String durationTime;

		private Boolean isOne;

		private Integer stopCount;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
		private LocalTime arrivalTime;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
		private LocalTime departureTime;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
		private LocalDate arrivalDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
		private LocalDate departureDate;

		private String arrivalCity;

		private String departureCity;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate checkInDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate checkOutDate;

		@JsonProperty("DepartureDateTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime departureDateTime;

		@JsonProperty("ArrivalDateTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime arrivalDateTime;

		private String departureAirportLocationCode;

		private String arrivalAirportLocationCode;

		private BigDecimal totalNetPrice;

		private BigDecimal totalRetailPrice;

		private BigDecimal taxAmount;

		private String bookingConditions;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	public static class Duration
	{
		private Integer comboDuration;

		private String comboDurationText;

		private String airlineTitle;
	}
}
