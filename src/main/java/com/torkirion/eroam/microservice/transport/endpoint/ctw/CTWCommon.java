package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class CTWCommon {
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Seller{
        private Integer id;
        private String channel;
        private String pos;
        private String serviceDeliveryPoInteger;
        private String iataNumber;
        private String travelAgencyCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateOfBirth{
        private Integer day;
        private Integer month;
        private Integer year;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpirationDate{
        private String month = "";
        private String year = "";
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardHolder{
        private String firstName = "";
        private String lastName = "";
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BillingAddress{
        private String streetAddressLine1 = "";
        private String streetAddressLine2= "";
        private String city = "";
        private String stateProvinceCode = "";
        private String countryCode = "";
        private String phoneNumber = "";
        private String emailAddress = "";
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FormOfPayment{
        private String type = "CASH";
        private String name = "";
        private String number = "           ";
        private String bankCode = "";
        private String cvv = "";
        private String cardToken = "";
        private String transactionId = "";
        private CTWItinenaryPriceRQ.ExpirationDate expirationDate = new CTWItinenaryPriceRQ.ExpirationDate();
        private CTWItinenaryPriceRQ.CardHolder cardHolder = new CTWItinenaryPriceRQ.CardHolder();
        private CTWItinenaryPriceRQ.BillingAddress billingAddress = new CTWItinenaryPriceRQ.BillingAddress();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Passenger{
        private Integer id;
        private ArrayList<String> ptcs = new ArrayList<>();
        private DateOfBirth dateOfBirth = new DateOfBirth();
        private FormOfPayment formOfPayment = new FormOfPayment();
        private String currencyOfPayment;
        private String discountCode = "  ";
        private String discountCodeQualifier = "  ";
        private String nationality;
        private String residency;
        private boolean idProvided;
        private boolean virtual;
        private Integer passengerId;
        private Baggage baggage;
        private ArrayList<Object> seats;
        private ArrayList<RuleBuster> ruleBusters;
        private ArrayList<Object> upgrades;
        private ArrayList<Object> mealServices;
        private ArrayList<Object> optionalServices;
        private ItineraryPricing itineraryPricing;
        public String title;
        public String gender;
        public ArrayList<Object> frequentFlierStatuses;
        public String firstName;
        public String lastName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BrandDefinition{
        private ArrayList<Object> offeredForFree;
        private ArrayList<Object> offeredForCharge;
        private ArrayList<Object> displayAsNotOffered;
        private ArrayList<Object> notOffered;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BeforeDeparture{
        private boolean allowed;
        private Price price;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AfterDeparture{
        private boolean allowed;
        private Price price;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Change{
        private BeforeDeparture beforeDeparture;
        private AfterDeparture afterDeparture;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Refund{
        private BeforeDeparture beforeDeparture;
        private AfterDeparture afterDeparture;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NoShow{
        private boolean allowed;
        private Price price;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Penalty{
        private Change change;
        private Refund refund;
        private NoShow noShow;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PublishedAmountOriginalCurrency{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PublishedAmountEquivalentCurrency{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FcAmountEquivalentCurrency{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EffectiveDate{
        private Integer day;
        private Integer month;
        private Integer year;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fare{
        private Integer id;
        private String origin;
        private String destination;
        private String carrier;
        private String fbc;
        private String ruleTariff;
        private String rule;
        private String routing;
        private String ftnt1;
        private String ftnt2;
        private String owrt;
        private String rawFareOwrt;
        @JsonProperty("private")
        private boolean myprivate;
        private String originAddonTrf;
        private String originAddonFtnt1;
        private String originAddonFtnt2;
        private String destinationAddonTrf;
        private String destinationAddonFtnt1;
        private String destinationAddonFtnt2;
        private String originAddonRtgNo;
        private String destinationAddonRtgNo;
        private ArrayList<String> primeRbds;
        private String fbcOverride;
        private String ticketDesignator1;
        private String ticketDesignator2;
        private String cabin;
        private String tier;
        private String brandName;
        private BrandDefinition brandDefinition;
        private Penalty penalty;
        private String fareDataForReprice;
        private String fareDataForDiagnostics;
        private PublishedAmountOriginalCurrency publishedAmountOriginalCurrency;
        private PublishedAmountEquivalentCurrency publishedAmountEquivalentCurrency;
        private String publishedAmountNuc;
        private FcAmountEquivalentCurrency fcAmountEquivalentCurrency;
        private String fcAmountNuc;
        private boolean halfRoundTripAmounts;
        private boolean upgradable;
        private boolean fareSelectionReversed;
        private EffectiveDate effectiveDate;

        private List<String> fareTypes = List.of("PUBLIC", "PRIVATE");
        private String refundable;
        private String changeable;
        private boolean useCustomBrands;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Date{
        private Integer day;
        private Integer month;
        private Integer year;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Time{
        private Integer hour;
        private Integer minutes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookedOnDateTime{
        private Date date;
        private Time time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AircraftType{
        private String aircraftGroupCode;
        private String aircraftTypeCode;
        private String serviceType;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amenities{
        private Integer cabinId;
        private Integer aircraftId;
        private Integer powerId;
        private Integer seatId;
        private Integer wifiId;
        private Integer entertainmentId;
        private Integer freshFoodId;
        private Integer layoutId;
        private Integer beverageId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Flight{
        private BookedOnDateTime bookedOnDateTime;
        private Integer bookedBySeller;
        private String fareBreakpoInteger;
        private String poIntegerOfTurnaround;
        private String departureAirport;
        private String arrivalAirport;
        private DateTime departureDateTime;
        private DateTime arrivalDateTime;
        private String marketingCarrier;
        private String marketingFlightNumber;
        private String rbd;
        private Integer id;
        private ArrayList<AircraftType> aircraftTypes;
        private String departureTerminal;
        private String arrivalTerminal;
        private String operatingCarrier;
        private String operatingFlightNumber;
        private String displayCarrier;
        private String compartment;
        private String cabin;
        private Integer availableSeats;
        private String fareCabin;
        private String fareTier;
        private String fareBrandName;
        private ArrayList<Object> meal;
        private String upgradeCabin;
        private ArrayList<String> inFlightService;
        private String co2Info;
        private String onTimePerformanceIndicator;
        private boolean aircraftChange;
        private Integer distance;
        private Integer duration;
        private Amenities amenities;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceInOriginalCurrency{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sequence{
        private PriceInOriginalCurrency priceInOriginalCurrency;
        private String pricePercent;
        private String sequenceNo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tax{
        private Integer id;
        private String category;
        private Price price;
        private String name;
        private String carrier;
        private String type;
        private String code;
        private ArrayList<Sequence> sequence;
        private ArrayList<Object> airports;
        private ArrayList<Integer> coveredFlights;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AllowedBaggage{
        private String unit;
        private Integer quantity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coupon{
        private Integer id;
        private Integer flightId;
        private String depAirport;
        private String arrAirport;
        private String depDate;
        private String depTime;
        private AllowedBaggage allowedBaggage;
        private String carrier;
        private String status;
        private String stopover;
        private Integer fareId;
        private String fbcAuditCoupon;
        private String ticketDesignator1AuditCoupon;
        private String ticketDesignator2AuditCoupon;
        private String fbcPassengerCoupon;
        private String ticketDesignator1PassengerCoupon;
        private String ticketDesignator2PassengerCoupon;
        private String flightNo;
        private String nva;
        private String nvb;
        private String rbd;
        private String tourCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Endorsement{
        private String carrier;
        private String placement;
        private Integer priority;
        private Integer fareId;
        private Integer fcId;
        private String text;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentAmount{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FormOfPayment2{
        private String type;
        private String name;
        private String number;
        private String bankCode;
        private String cvv;
        private String cardToken;
        private String transactionId;
        private ExpirationDate expirationDate;
        private CardHolder cardHolder;
        private BillingAddress billingAddress;
        private String referenceCode;
        private PaymentAmount paymentAmount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GrossBaseFareCoc{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GrossBaseFare{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GrossTotal{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetBaseFareCoc{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetBaseFare{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetTotal{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetRemitBase{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetRemitTotal{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SellingBaseFare{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SellingBaseFareCoc{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SellingTotal{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StandardCommission{
        private String percent;
        private Amount amount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SupplementaryCommission{
        private String percent;
        private Amount amount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TotalTax{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BspInformation{
        private GrossBaseFareCoc grossBaseFareCoc;
        private GrossBaseFare grossBaseFare;
        private GrossTotal grossTotal;
        private NetBaseFareCoc netBaseFareCoc;
        private NetBaseFare netBaseFare;
        private NetTotal netTotal;
        private NetRemitBase netRemitBase;
        private NetRemitTotal netRemitTotal;
        private SellingBaseFare sellingBaseFare;
        private SellingBaseFareCoc sellingBaseFareCoc;
        private SellingTotal sellingTotal;
        private StandardCommission standardCommission;
        private SupplementaryCommission supplementaryCommission;
        private TotalTax totalTax;
        private String bspCalculationMethod;
        private String fareCalculationMode;
        private String tourCode;
        private String tourCodeBox;
        private String tourType;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bsr{
        private String rate;
        private String fromCurrency;
        private String toCurrency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscontinueDate{
        private Integer day;
        private Integer month;
        private Integer year;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Iroe{
        private EffectiveDate effectiveDate;
        private DiscontinueDate discontinueDate;
        private String rate;
        private String toCurrency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeRates{
        private Bsr bsr;
        private Iroe iroe;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceEquivalentCurrency{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceFilingCurrency{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Surcharge{
        private String carrier;
        private ArrayList<Integer> flightIds;
        private PriceEquivalentCurrency priceEquivalentCurrency;
        private PriceFilingCurrency priceFilingCurrency;
        private String priceNuc;
        private String scope;
        private String from;

        @JsonProperty("to")
        private String myto;

        private String type;
        private Integer fareId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BaseFare{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ETicketTaxes{
        private String tax1;
        private String tax2;
        private String tax3;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EquivalentFare{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TotalFare{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TotalTaxes{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FareInformation{
        private ArrayList<Surcharge> surcharges;
        private ArrayList<Integer> iataTaxes;
        private ArrayList<Integer> yqyr;
        private BaseFare baseFare;
        private ETicketTaxes eTicketTaxes;
        private EquivalentFare equivalentFare;
        private TotalFare totalFare;
        private TotalTaxes totalTaxes;
        private String fareline;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateTime{
        private Date date;
        private Time time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EarliestTimeToTicket{
        private Date date;
        private Time time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LatestTimeToTicket{
        private Date date;
        private Time time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IssueInformation{
        private Integer sellerId;
        private String country;
        private String date;
        private DateTime dateTime;
        private EarliestTimeToTicket earliestTimeToTicket;
        private LatestTimeToTicket latestTimeToTicket;
        private String tourCode;
        private String accountCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PassengerFareInformationOverrides{
        private boolean mustUseFareBoxInformation;
        private String baseFareBox;
        private String equivalentFareBox;
        private String fareline;
        private String totalFareBox;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FareConstructionChecks{
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketMask{
        private ArrayList<Coupon> coupons;
        private ArrayList<Endorsement> endorsements;
        private ArrayList<FormOfPayment> formOfPayments;
        private BspInformation bspInformation;
        private ExchangeRates exchangeRates;
        private FareInformation fareInformation;
        private IssueInformation issueInformation;
        private PassengerFareInformationOverrides passengerFareInformationOverrides;
        private String platingCarrier;
        private Integer id;
        private FareConstructionChecks fareConstructionChecks;
        private Integer numberOfPreviousReissues;
        private String endorsementBox;
        private String formOfPaymentBox;
        private String origDest;
        private String ticketType;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServiceElement{
        private String serviceType;
        private String code;
        private String subCode;
        private String group;
        private String subGroup;
        private String description1;
        private String description2;
        private String commercialName;
        private String ssrCode;
        private String additionalInformation;
        private String mediaId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BaggageTypesAllowed{
        private Integer allowedPieces;
        private ServiceElement serviceElement;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FreeBaggageAllowance{
        private Integer id;
        private String carrier;
        private String serviceType;
        private String code;
        private String subCode;
        private String commercialName;
        private Integer allowedPieces;
        private ArrayList<BaggageTypesAllowed> baggageTypesAllowed;
        private String rs5SequenceNo;
        private String rs7SequenceNo;
        private String rs7T196;
        private String rs7T196Text;
        private Integer allowedWeight;
        private String allowedWeightUnit;
        private Integer freeBaggageAllowanceId;
        private ArrayList<Integer> coveredFlights;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Charge{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaidBaggage{
        private Integer id;
        private String serviceType;
        private String carrier;
        private String prepaidCheckIn;
        private ServiceElement serviceElement;
        private Integer allowedPieces;
        private String chargePerUnit;
        private Charge charge;
        private Integer chargeMiles;
        private String bookingMethod;
        private Integer occurrenceFirst;
        private Integer occurrenceLast;
        private String rs5SequenceNo;
        private String rs7SequenceNo;
        private String rs7T196;
        private ArrayList<Integer> coveredFlights;
        private Integer paidBaggageId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PenaltyDetails{
        private BeforeDeparture beforeDeparture;
        private AfterDeparture afterDeparture;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewProvision{
        private String category;
        private String tariff;
        private String rule;
        private PenaltyDetails penaltyDetails;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RuleBuster{
        private Integer id;
        private String serviceType;
        private String carrier;
        private String prepaidCheckIn;
        private String overrideType;
        private ArrayList<NewProvision> newProvisions;
        private ServiceElement serviceElement;
        private String chargePerUnit;
        private Charge charge;
        private Integer chargeMiles;
        private String bookingMethod;
        private String rs5SequenceNo;
        private String rs7SequenceNo;
        private String rs7T196;
        private ArrayList<Integer> coveredFlights;
        private Integer ruleBusterId;
        private ArrayList<Integer> relatedFares;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pet{
        private ArrayList<Integer> coveredFlights;
        private Integer paidBaggageId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Baggage{
        private ArrayList<PaidBaggage> paidBaggage;
        private ArrayList<Pet> pets;
        private ArrayList<FreeBaggageAllowance> freeBaggageAllowance;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AncillariesShopOffer{
        private Integer id;
        private Integer sellerId;
        private String ora;
        private ArrayList<Passenger> passengers;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataLibrary{
        private ArrayList<Seller> sellers;
        private ArrayList<Passenger> passengers;
        private ArrayList<Fare> fares;
        private ArrayList<Flight> flights;
        private ArrayList<Tax> taxes;
        private ArrayList<TicketMask> ticketMasks;
        private ArrayList<Object> emdMasks;
        private ArrayList<Object> ticketingFees;
        private ArrayList<FreeBaggageAllowance> freeBaggageAllowance;
        private ArrayList<Object> baggageEmbargoes;
        private ArrayList<PaidBaggage> paidBaggage;
        private ArrayList<Object> paidSeats;
        private ArrayList<RuleBuster> ruleBusters;
        private ArrayList<Object> upgrades;
        private ArrayList<Object> mealServices;
        private ArrayList<Object> optionalServices;
        private ArrayList<Object> ssrs;
        private ArrayList<AncillariesShopOffer> ancillariesShopOffers;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Expiration{
        private Date date;
        private Time time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GuaranteedBy{
        private Expiration expiration;
        private String token;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItineraryFlight{
        private Integer flightId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RequestSegmentsMapping{
        private String origin;
        private String destination;
        private ArrayList<Integer> flights;
        private Integer totalFlightTime;
        private Integer numberOfOvernights;
        private boolean longLayover;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Total{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TotalBase{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TotalYqyr{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TotalIata{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TotalTicketingFees{
        private String amount;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaresToFlightsMapping{
        private Integer fareId;
        private ArrayList<Integer> coveredFlights;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketBy{
        private Date date;
        private Time time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status{
        private DateTime dateTime;
        private TicketBy ticketBy;
        private String code;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketingInfo{
        private String ticketNumber;
        private Integer ticketMaskId;
        private ArrayList<Integer> coveredFlights;
        private Total total;
        private TotalBase totalBase;
        private TotalYqyr totalYqyr;
        private TotalIata totalIata;
        private Integer totalPoIntegers;
        private ArrayList<Object> repriceSolutionInformation;
        private ArrayList<Object> ticketingFees;
        private Status status;
        private Integer originalNumberOfPassengers;
        private Integer minimumNumberOfAccompanying;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItineraryPricing{
        private ArrayList<FaresToFlightsMapping> faresToFlightsMapping;
        private ArrayList<TicketingInfo> ticketingInfos;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItineraryOffer{
        private Integer sellerId;
        private GuaranteedBy guaranteedBy;
        private String ora;
        private ArrayList<ItineraryFlight> itineraryFlights;
        private ArrayList<RequestSegmentsMapping> requestSegmentsMapping;
        private Total total;
        private TotalBase totalBase;
        private TotalYqyr totalYqyr;
        private TotalIata totalIata;
        private TotalTicketingFees totalTicketingFees;
        private Integer totalPoIntegers;
        private Integer totalFlightTime;
        private Integer numberOfOvernights;
        private boolean longLayover;
        private Integer sortScore;
        private ArrayList<Passenger> passengers;
        private Integer ancillariesShopOfferId;
        private String itineraryMarketingGroup;
        private String itineraryOperatingGroup;
        private String uuid;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Versions{
        private String aircrafttypeDbVersion;
        private String activeairportsDbVersion;
        private String distancesDbVersion;
        private String carriersDbVersion;
        private String timezonesDbVersion;
        private String locationsDbVersion;
        private String currenciesDbVersion;
        private String flightlinesDbVersion;
        private String atpcoDbVersion;
        private String sitaservicerecordsDbVersion;
        private String sitaroutingsDbVersion;
        private String sitameDbVersion;
        private String sitafbrDbVersion;
        private String sitaDbVersion;
        private String servicerecordsDbVersion;
        private String routingsDbVersion;
        private String minconxDbVersion;
        private String meDbVersion;
        private String mctDbVersion;
        private String iatataxDbVersion;
        private String flightsDbVersion;
        private String fbrDbVersion;
        private String ditroutingsDbVersion;
        private String ditmeDbVersion;
        private String ditfbrDbVersion;
        private String ditDbVersion;
        private String compressedroutingDbVersion;
        private String sitacompressedroutingDbVersion;
        private String miscdataDbVersion;
        private String minpricesDbVersion;
        private String splitsDbVersion;
        private String connectionsDbVersion;
        private String histSitacompressedroutingDbVersion;
        private String histCompressedroutingDbVersion;
        private String histSitameDbVersion;
        private String histSitaservicerecordsDbVersion;
        private String histSitaroutingsDbVersion;
        private String histSitafbrDbVersion;
        private String histSitaDbVersion;
        private String histServicerecordsDbVersion;
        private String histRoutingsDbVersion;
        private String histMeDbVersion;
        private String histIatataxDbVersion;
        private String histFbrDbVersion;
        private String histDitroutingsDbVersion;
        private String histDitmeDbVersion;
        private String histDitfbrDbVersion;
        private String histDitDbVersion;
        private String histAtpcoDbVersion;
        private String canceldataDbVersion;
        private String amenitiesDbVersion;
        private String fakeflightlinesDbVersion;
        private String codeVersion;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DebugInformation{
        private double elapsedTime;
        private Versions versions;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseStatus{
        private String statusCode;
        private ArrayList<Object> diagnostics;
        private String uuid;
        private DebugInformation debugInformation;
        private ArrayList<Object> messages;
        private String userData;
    }
}
