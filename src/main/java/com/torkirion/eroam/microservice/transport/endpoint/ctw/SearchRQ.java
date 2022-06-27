package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
public class SearchRQ
{
	@Data
	public static class Fare
	{
		private List<String> fareTypes = new ArrayList<>();

		private String refundable = "any";

		private String changeable = "any";

		private Boolean useCustomBrands = false;
	}

	@Data
	public static class Cabin
	{
		private String cabinCombo = "CHEAPEST";

		private Boolean downsell = false;

		private Boolean upsell = false;

		private Boolean processVirtualCabinCombos = false;
	}

	@Data
	public static class SegmentCabin
	{
		private List<String> cabin = new ArrayList<>();

		private Boolean downsell = false;

		private Boolean upsell = false;

		private Boolean allowLowerCabinOnFeederFlights = false;
	}

	@Data
	public static class Debug
	{
		private Boolean includeDebugInformation = true;
	}

	@Data
	public static class Carrier
	{
		//private List<String> faresAllowedCarriers = new ArrayList<>();

		//private List<String> flightsAllowedMarketingCarriers = new ArrayList<>();

		private Boolean noCodeshare = false;

		private Boolean noInterline = false;
	}

	@Data
	public static class Schedule
	{
		private String allowedStops = "2+";

		private Boolean noOvernightStay = false;
	}

	@Data
	public static class Baggage
	{
		private Integer minimumCheckedBags;

		private Boolean collectBaggageOnArrival;
	}

	@Data
	public static class Response
	{
		private String availability = "NO";
		private Boolean includeBaggage = true;
		private Boolean includeCommission = true;
		private Boolean includeIataTax = true;
		private Boolean includePenalties = true;
		private Boolean includeSurcharge = true;
		private Boolean includeTicketMask = true;
		private Boolean includeYqyr = true;
		private Integer maxResults = 1000;
		private Boolean returnResultsInBatches = true;
		private Boolean includeAllBaggageCharges = true;
		private Boolean includeAncillariesShopOffers = true;
		private String displayCurrency = "USD";
	}

	@Data
	public static class AdvancedShop
	{
		private Integer searchSpeed = 2; // trid 3, takes 100 seconds for NYC to HAN ?
		private String multipleTicket = "NO";
		private Boolean ticketingChecks = false;
	}

	@Data
	public static class Preferences
	{
		private Fare fare = new Fare();

		private Cabin cabin = new Cabin();

		private Debug debug = new Debug();

		private Carrier carrier = new Carrier();

		private Schedule schedule = new  Schedule();

		private Response response = new Response();

		private AdvancedShop advancedShop = new AdvancedShop();
	}

	@Data
	public static class SegmentPreferences
	{
		private Fare fare;

		private SegmentCabin cabins = new SegmentCabin();

		private Carrier carrier = new Carrier();

		private Schedule schedule = new  Schedule();

		private Baggage baggage;
	}

	@Data
	public static class DateStruct
	{
		private Integer day;
		private Integer month;
		private Integer year;
	}
	@Data
	public static class LocationStruct
	{
		private String name;
		private String type;
	}

	@Data
	public static class Segment
	{
		private DateStruct departureDate = new DateStruct();
		private List<LocationStruct> origin = new ArrayList<>();
		private List<LocationStruct> destination = new ArrayList<>();
		private SegmentPreferences preferences = new SegmentPreferences();
	}

	@Data
	public static class FormOfPayment
	{
		private String type = "CASH";
	}

	@Data
	public static class Passenger
	{
		private Integer id;
		private List<String> ptcs = new ArrayList<>();
		private DateStruct dateOfBirth = new DateStruct();
		private String nationality;
		private String residency;
		private String currencyOfPayment;
		private FormOfPayment formOfPayment = new FormOfPayment();
	}

	@Data
	public static class Seller
	{
		private Integer id;
		private String pos;
		private String channel;
		private String country;
		private String currency;
		private String travelAgencyCode;
		private String iataNumber;
	}

	private Preferences preferences = new Preferences();

	private List<Segment> segments = new ArrayList<>();

	private List<Passenger> passengers = new ArrayList<>();

	private List<Seller> sellers = new ArrayList<>();
	/*
	 * { "preferences": { "fare": { "fareTypes": [ "PUBLIC", "PRIVATE" ], "refundable": "any", "changeable": "any",
	 * "useCustomBrands": false }, "cabin": { "cabinCombo": "CHEAPEST", "downsell": false, "upsell": false,
	 * "processVirtualCabinCombos": false }, "debug": { "diagnosticPricing": false, "includeDebugInformation": true,
	 * "diagnosticPricingIfFail": false, "includeTraceInfo": { "performance": false } }, "carrier": { "faresAllowedCarriers": [],
	 * "flightsAllowedMarketingCarriers": [], "noCodeshare": false, "noInterline": false }, "schedule": { "allowedStops": "2+",
	 * "noOvernightStay": false }, "response": { "availability": "NO", "includeBaggage": true, "includeCommission": false,
	 * "includeIataTax": true, "includePenalties": true, "includeSurcharge": true, "includeTicketMask": true, "includeYqyr": true,
	 * "maxResults": 1000, "returnResultsInBatches": true, "includeAllBaggageCharges": true, "includeAncillariesShopOffers": true
	 * }, "advancedShop": { "searchSpeed": 3, "multipleTicket": "NO", "ticketingChecks": false } }, "segments": [ {
	 * "departureDate": { "day": 7, "month": 1, "year": 2022 }, "preferences": { "schedule": { "allowedStops": "2+" } }, "origin":
	 * [ { "name": "NYC", "type": "CITY" } ], "destination": [ { "name": "HAN", "type": "CITY" } ] } ], "passengers": [ { "id": 0,
	 * "ptcs": [ "ADT" ], "dateOfBirth": { "day": 6, "month": 1, "year": 1982 }, "nationality": "US", "residency": "US",
	 * "currencyOfPayment": "USD", "formOfPayment": { "type": "CASH" } } ], "sellers": [ { "id": 0, "pos": "DCA", "channel": "1A",
	 * "country": "US", "currency": "USD", "travelAgencyCode": "21S19P", "iataNumber": "49881134" } ] }
	 */
}
