package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CTWBookRS {

    private DataLibrary dataLibrary;
    private String type;
    private Order order;
    private ResponseStatus responseStatus;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Seller{
        private Integer id;
        private String channel;
        private String pos;
        private String travelAgencyCode;
        private String iataNumber;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateOfBirth{
        private Integer year;
        private Integer month;
        private Integer day;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpirationDate{
        private String month;
        private String year;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardHolder{
        private String firstName;
        private String lastName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BillingAddress{
        private String streetAddressLine1;
        private String streetAddressLine2;
        private String city;
        private String stateProvinceCode;
        private String countryCode;
        private String phoneNumber;
        private String eMailAddress;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FormOfPayment{
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
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Passenger{
        private Integer id;
        private String firstName;
        private String lastName;
        private String title;
        private String gender;
        private List<String> ptcs;
        private DateOfBirth dateOfBirth;
        private FormOfPayment formOfPayment;
        private String currencyOfPayment;
        private List<Object> frequentFlierStatuses;
        private String discountCode;
        private String discountCodeQualifier;
        private String nationality;
        private String residency;
        private boolean virtual;
        private boolean idProvided;
        private Integer passengerId;
        private ItineraryPricing itineraryPricing;
        private Baggage baggage;
        private List<CouponStatus> couponStatuses;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Date{
        private Integer year;
        private Integer month;
        private Integer day;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Time{
        private Integer hour;
        private Integer minutes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DepartureDateTime{
        private Date date;
        private Time time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArrivalDateTime{
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
    public static class BookedOnDateTime{
        private Date date;
        private Time time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Flight{
        private String operatingFlightNumber;
        private boolean aircraftChange;
        private Integer distance;
        private Integer duration;
        private String displayCarrier;
        private String co2Info;
        private String onTimePerformanceIndicator;
        private String upgradeCabin;
        private String fareCabin;
        private String fareTier;
        private String fareBrandName;
        private Integer id;
        private String marketingCarrier;
        private String marketingFlightNumber;
        private String operatingCarrier;
        private String departureAirport;
        private String departureTerminal;
        private String arrivalAirport;
        private String arrivalTerminal;
        private DepartureDateTime departureDateTime;
        private ArrivalDateTime arrivalDateTime;
        private List<AircraftType> aircraftTypes;
        private List<String> inFlightService;
        private String cabin;
        private String compartment;
        private String rbd;
        private Integer availableSeats;
        private List<String> meal;
        private BookedOnDateTime bookedOnDateTime;
        private Integer bookedBySeller;
        private String bookingStatus;
        private String bookingStatusCode;
        private String fareBreakpoInteger;
        private String poIntegerOfTurnaround;
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
        private String accountCode;
        private String tourCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AllowedBaggage{
        private Integer quantity;
        private String unit;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coupon{
        private Integer id;
        private Integer flightId;
        private String depAirport;
        private String arrAirport;
        private String status;
        private String stopover;
        private String carrier;
        private String flightNo;
        private String rbd;
        private String depDate;
        private String depTime;
        private Integer fareId;
        private String fbcPassengerCoupon;
        private String fbcAuditCoupon;
        private String ticketDesignator1PassengerCoupon;
        private String ticketDesignator2PassengerCoupon;
        private String ticketDesignator1AuditCoupon;
        private String ticketDesignator2AuditCoupon;
        private String tourCode;
        private String nvb;
        private String nva;
        private AllowedBaggage allowedBaggage;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FareConstructionChecks{
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketMask{
        private Integer id;
        private IssueInformation issueInformation;
        private String platingCarrier;
        private String ticketType;
        private String origDest;
        private List<Coupon> coupons;
        private String endorsementBox;
        private String formOfPaymentBox;
        private Integer numberOfPreviousReissues;
        private FareConstructionChecks fareConstructionChecks;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataLibrary{
        private List<Seller> sellers;
        private List<Passenger> passengers;
        private List<Flight> flights;
        private List<TicketMask> ticketMasks;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecordLocator{
        private String host;
        private String locator;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItineraryFlight{
        private Integer flightId;
        private String hostReservationReference;
        private List<RecordLocator> recordLocators;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RequestSegmentsMapping{
        private String origin;
        private String destination;
        private List<Integer> flights;
        private Integer totalFlightTime;
        private Integer numberOfOvernights;
        private boolean longLayover;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaresToFlightsMapping{
        private Integer fareId;
        private List<Integer> coveredFlights;
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
        private String code;
        private TicketBy ticketBy;
        private DateTime dateTime;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketingInfo{
        private Status status;
        private Integer minimumNumberOfAccompanying;
        private Integer ticketMaskId;
        private List<Integer> coveredFlights;
        private List<Object> ticketingFees;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItineraryPricing{
        private List<FaresToFlightsMapping> faresToFlightsMapping;
        private List<TicketingInfo> ticketingInfos;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FreeBaggageAllowance{
        private Integer freeBaggageAllowanceId;
        private List<Integer> coveredFlights;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Baggage{
        private List<FreeBaggageAllowance> freeBaggageAllowance;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CouponStatus{
        private String type;
        private Integer ticketMaskId;
        private List<Object> coupons;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Order{
        private String id;
        private Integer sellerId;
        private String checksum;
        private String ora;
        private List<ItineraryFlight> itineraryFlights;
        private List<RequestSegmentsMapping> requestSegmentsMapping;
        private List<Passenger> passengers;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Versions{
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
        private List<Object> messages;
        private String uuid;
        private DebugInformation debugInformation;
    }
}
