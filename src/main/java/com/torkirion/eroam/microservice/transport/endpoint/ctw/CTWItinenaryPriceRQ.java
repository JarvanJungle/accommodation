package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;



@Data
public class CTWItinenaryPriceRQ {

    private List<Flight> flights = new ArrayList<>();
    private List<Segment> segments = new ArrayList<>();
    private List<CTWCommon.Passenger> passengers = new ArrayList<>();
    private Preferences preferences = new Preferences();
    private TicketingSeller ticketingSeller = new TicketingSeller();

    @Data
    public static class Date{
        private int day;
        private int month;
        private int year;
    }

    @Data
    public static class Time{
        private int hour;
        private int minutes;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class DateTime {
        private Date date;
        private Time time;
    }

    @Data
    public static class Flight{
        //private int id;
        private String departureAirport;
        private String arrivalAirport;
        private String marketingCarrier;
        private String marketingFlightNumber;
        private DateTime departureDateTime;
        private int requestSegment;
    }

    @AllArgsConstructor
    @Data
    public static class Segment{
        private int id;
        private String cabinCombo;
    }

    @Data
    public static class DateOfBirth{
        private int day;
        private int month;
        private int year;
    }

    @Data
    public static class ExpirationDate{
        private String month = "";
        private String year = "";
    }

    @Data
    public static class CardHolder{
        private String firstName = "";
        private String lastName = "";
    }

    @Data
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
    public static class FormOfPayment{
        private String type = "CASH";
        private String name = "";
        private String number = "           ";
        private String bankCode = "";
        private String cvv = "";
        private String cardToken = "";
        private String transactionId = "";
        private ExpirationDate expirationDate = new ExpirationDate();
        private CardHolder cardHolder = new CardHolder();
        private BillingAddress billingAddress = new BillingAddress();
    }

    @Data
    public static class Passenger{
        private int id;
        private String currencyOfPayment;
        private DateOfBirth dateOfBirth = new DateOfBirth();
        private ArrayList<String> ptcs = new ArrayList<>();
        private FormOfPayment formOfPayment = new FormOfPayment();
        private String nationality;
        private String residency;
        private String discountCode = "  ";
        private String discountCodeQualifier = "  ";
    }

    @Data
    public static class Fare{
        private List<String> fareTypes = List.of("PUBLIC", "PRIVATE");
        private String refundable;
        private String changeable;
        private boolean useCustomBrands;
    }

    @AllArgsConstructor
    @Data
    public static class CabinDetail{
        private String cabin;
    }

//    "cabin": {
//        "cabinCombo": "CHEAPEST_CROSS_CABIN",//để CHEAPEST_CROSS_CABIN
//                "cabins": [//list các cabin class trong segments/legs/transportClass
//        {
//            "cabin": "ECONOMY"
//        },
//        {
//            "cabin": "BUSINESS"
//        },
//        {
//            "cabin": "FIRST"
//        }
//        ],
//        "downsell": false,//Để như default trong sample
//                "processVirtualCabinCombos": true,//Để như default trong sample
//                "upsell": false//Để như default trong sample
//    },
    @Data
    public static class Cabin{
        private String cabinCombo = "CHEAPEST_CROSS_CABIN";
        private List<CabinDetail> cabins = new ArrayList<>();
        private boolean downsell = false;
        private boolean processVirtualCabinCombos = true;
        private boolean upsell = false;
    }


//    "response": {//Để như default trong sample
//        "availability": "NO",
//                "includeBaggage": true,
//                "includeCommission": false,
//                "includeIataTax": true,
//                "includePenalties": true,
//                "includeSurcharge": true,
//                "includeTicketMask": true,
//                "includeYqyr": true,
//                "maxResults": 1000,//để 100
//                "returnResultsInBatches": false,
//                "includeEmdMask": true
//    },
    @Data
    public static class Response{
        private String availability = "NO";
        private boolean includeBaggage = true;
        private boolean includeCommission = false;
        private boolean includeIataTax = true;
        private boolean includePenalties = true;
        private boolean includeSurcharge = true;
        private boolean includeTicketMask = true;
        private boolean includeYqyr = true;
        private int maxResults = 100;
        private boolean returnResultsInBatches = false;
        private boolean includeAllBaggageCharges = true;
        private boolean includeAncillariesShopOffers = true;
        private boolean includeEmdMask = true;
    }

    @Data
    public static class IncludeTraceInfo{
        private boolean performance;
    }

    @Data
    public static class RemoteProcessing{
        private String hostname;
        private int port;
        private int parallelRequests;
    }

    @Data
    public static class Debug{
//        private boolean diagnosticPricing;
        private boolean includeDebugInformation = true;
        private boolean diagnosticPricingIfFail = false;
//        private IncludeTraceInfo includeTraceInfo;
//        private RemoteProcessing remoteProcessing;
    }

    @Data
    public static class AdvancedPrice{
        private String multipleTicket;
    }

    @Data
    public static class Preferences{
        private Fare fare = new Fare();
        private Cabin cabin = new Cabin();
        private boolean ticketingChecks;
        private Response response = new Response();
        private Debug debug = new Debug();
        private AdvancedPrice advancedPrice = new AdvancedPrice();
    }

//     "ticketingSeller": {//không map
//        "id": 1,
//                "pos": "LGA",
//                "channel": "1A",
//                "country": "US",
//                "currency": "USD",
//                "travelAgencyCode": "21S118",
//                "iataNumber": "33651586"
//    }
    //TODO - fake first
    @Data
    public static class TicketingSeller{
        private int id = 1;
        private String pos = "LGA";
        private String channel = "1A";
        private String country = "US";
        private String currency = "USA";
        private String travelAgencyCode = "21S118";
        private String iataNumber = "33651586";
    }
}
