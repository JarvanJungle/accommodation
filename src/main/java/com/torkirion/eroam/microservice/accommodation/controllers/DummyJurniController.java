package com.torkirion.eroam.microservice.accommodation.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.cache.Cache;
import javax.transaction.Transactional;

import org.apache.http.HttpMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Address;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.FacilityGroup;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.GeoCoordinates;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelBaseRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelBaseRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelDefaultRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelDefaultRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelDetailRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelDetailRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelListRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelListRS;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.AvailabilityRS;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoAPIProperties;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoHttpService;
import com.torkirion.eroam.microservice.accommodation.services.*;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.datadomain.Airline;
import com.torkirion.eroam.microservice.datadomain.AirlineRepo;

@RestController
@RequestMapping("/jurni/dummyFetch")
@Api(value = "Mock Jurni API")
@Slf4j
@AllArgsConstructor
public class DummyJurniController
{
	@Autowired
	private AirlineRepo airlineRepo;

	public class JurniHttpService extends HttpService
	{
		@Override
		protected void addHeaders(HttpMessage httpMessage)
		{
		}

		@Override
		protected String getUrl()
		{
			return "https://e2.cms.eroam.com/api";
		}
	}

	@Data
	public static class RemoteItinerary
	{
		private String message;

		// private DataObj data;
		private List<RemoteItinerary.Itinerary> data;

		@Data
		public static class DataObj
		{
			private List<RemoteItinerary.Itinerary> data;
		}

		@Data
		public static class Itinerary
		{
			private String itineraryId;

			private RemoteItinerary.Agent agent = new Agent();

			private RemoteItinerary.Customer customer = new Customer();

			private List<RemoteItinerary.Traveller> travellers = new ArrayList<>();

			private RemoteItinerary.Items items = new Items();

			private String itineraryTitle;

			private String itineraryOverview;

			private String startDate;

			private String endDate;

			private CurrencyValue totalPrice;

			private CurrencyValue totalPaid;

			private CurrencyValue totalOutstanding;

			private String leadImageUrl;
		}

		@Data
		public static class Agent
		{
			private String name;

			private String logoUrl;

			private String conciergeContact;
		}

		@Data
		public static class Customer
		{
			private String firstName;

			private String surname;

			private String email;

			private String customerId;
		}

		@Data
		public static class Traveller
		{
			private String firstName;

			private String surname;

			private Boolean adult;

			private Integer childAge;
		}

		@Data
		public static class Items
		{
			private List<RemoteItinerary.AccommodationItem> accommodation = new ArrayList<>();

			private List<RemoteItinerary.ActivityItem> activities = new ArrayList<>();

			private List<RemoteItinerary.TransportItem> transport = new ArrayList<>();

			private List<RemoteItinerary.GitItem> git = new ArrayList<>();
		}

		@Data
		public static class AccommodationItem
		{
			private String hotelName;

			private LocalDate checkinDate;

			private LocalDate checkoutDate;

			private String roomName;

			private String cityName;

			private BigDecimal starRating;

			private List<String> images = new ArrayList<>();

			private String overview;

			private Address address = new Address();

			private String telephone;

			private List<String> amenities = new ArrayList<>();

			private String voucherUrl;
		}

		@Data
		public static class Image
		{
			private String caption;

			private String photoURL;

			private String path;

			private String supplier;
		}

		@Data
		public static class Time
		{
			private String hour;

			private String minute;

			private String second;

			private String nano;
		}

		@Data
		public static class ActivityItem
		{
			private String activityName;

			private String activitDescription;

			private String optionDescription;

			private LocalDate date;

			private Time pickupTime;

			private String operator;

			private List<Image> images = new ArrayList<>();

			private String cityName;

			private String category;

			private String duration;

			private String departurePoint;

			private GeoCoordinates departureGeoCoordinates;

			private List<String> inclusions = new ArrayList<>();

			private List<String> exclusions = new ArrayList<>();

			private List<String> additionalInformation;

			private String joiningInstructions;

			private String voucherUrl;
		}

		@Data
		public static class TransportItem
		{
			private String transportType;

			private String transportNumber;

			private String carrierDescription;

			private String carrierImageUrl;

			private LocalDateTime departure;

			private LocalDateTime arrival;

			private BigDecimal layoverHours;

			private TerminalDetails departureDetails = new TerminalDetails();

			private TerminalDetails arrivalDetails = new TerminalDetails();
		}
		
		@Data
		public static class GitItem
		{
			 // Tour Name, Start Date, Supplier, Operator, Finish Date, Duration, description and image ? (and vouchers?)
			private String tourName;

			private String supplier;

			private String operator;

			private Integer duration;

			private LocalDate startDate;

			private LocalDate endDate;

			private String description;
			
			private String voucherUrl;
			
			private List<String> images = new ArrayList<>();
		}

		public static enum TransportType
		{
			FLIGHT, TRANSFER, FERRY, RAIL;
		}

		@Data
		public static class TerminalDetails
		{
			private String iataAirportCode;

			private String terminalName;

			private String description;
		}

	}

	@Data
	public static class Itinerary
	{
		private String itineraryId;

		private Agent agent = new Agent();

		private Customer customer = new Customer();

		private List<Traveller> travellers = new ArrayList<>();

		private Items items = new Items();

		private String itineraryTitle;

		private String itineraryOverview;

		private LocalDate startDate;

		private LocalDate endDate;

		private CurrencyValue totalPrice;

		private CurrencyValue totalPaid;

		private CurrencyValue totalOutstanding;

		private String leadImageUrl;
	}

	@Data
	public static class Agent
	{
		private String name;

		private String logoUrl;

		private String conciergeContact;
	}

	@Data
	public static class Customer
	{
		private String firstName;

		private String surname;

		private String email;

		private String customerId;
	}

	@Data
	public static class Traveller
	{
		private String firstName;

		private String surname;

		private Boolean adult;

		private Integer childAge;
	}

	@Data
	public static class Items
	{
		private List<AccommodationItem> accommodation = new ArrayList<>();

		private List<ActivityItem> activities = new ArrayList<>();

		private List<TransportItem> transport = new ArrayList<>();

		private List<GitItem> git = new ArrayList<>();
	}

	@Data
	public static class AccommodationItem
	{
		private String hotelName;

		private LocalDate checkinDate;

		private LocalDate checkoutDate;

		private String roomName;

		private String cityName;

		private BigDecimal starRating;

		private List<String> images = new ArrayList<>();

		private String overview;

		private Address address = new Address();

		private String telephone;

		private List<String> amenities = new ArrayList<>();
		
		private String voucherUrl;
	}

	@Data
	public static class ActivityItem
	{
		private String activityName;

		private String activityDescription;

		private String activitDescription;

		private String optionDescription;

		private LocalDate date;

		private LocalTime pickupTime;

		private String operator;

		private List<String> images = new ArrayList<>();

		private String cityName;

		private String category;

		private String duration;

		private String departurePoint;

		private GeoCoordinates departureGeoCoordinates;

		private List<String> inclusions = new ArrayList<>();

		private List<String> exclusions = new ArrayList<>();

		private String additionalInformation;

		private String joiningInstructions;

		private String voucherUrl;
	}

	@Data
	public static class TransportItem
	{
		private TransportType transportType;

		private String transportNumber;

		private String carrierDescription;

		private String carrierImageUrl;

		private LocalDateTime departure;

		private LocalDateTime arrival;

		private BigDecimal layoverHours;

		private TerminalDetails departureDetails = new TerminalDetails();

		private TerminalDetails arrivalDetails = new TerminalDetails();
		
		private String voucherUrl;
	}

	public static enum TransportType
	{
		FLIGHT, TRANSFER, FERRY, RAIL;
	}

	@Data
	public static class TerminalDetails
	{
		private String iataAirportCode;

		private String terminalName;

		private String description;
	}

	@Data
	public static class GitItem
	{
		 // Tour Name, Start Date, Supplier, Operator, Finish Date, Duration, description and image ? (and vouchers?)
		private String tourName;

		private String supplier;

		private String operator;

		private Integer duration;

		private LocalDateTime startDate;

		private LocalDateTime endDate;

		private String description;
		
		private String voucherUrl;
		
		private List<String> images = new ArrayList<>();
	}


	@ApiOperation(value = "Get Itineraries")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/getItineraries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public List<Itinerary> getItineraries(@RequestHeader("X-API-KEY") String apiKey, @RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "customerId", required = true) String customerId)
	{
		log.info("getItineraries::enter for customerId " + customerId + " email " + email);

		Map<String, String> parameters = new HashMap<>();
		if (email.equalsIgnoreCase("xxx"))
		{
			parameters.put("emailId", "nilay@eyepaste.com");
			parameters.put("customer_id", "937");
		}
		else if ( email.equalsIgnoreCase("jjj"))
		{
			return makeDummy(email, customerId);
		}
		else
		{
			parameters.put("emailId", email);
			parameters.put("customer_id", customerId);
		}
		
		HttpService httpService = new JurniHttpService();
		String responseString = httpService.doCallGet("get-itinerary-jurni-app", parameters);
		responseString = responseString.replace("\"geoAccuracy\":\"TODO\"", "\"geoAccuracy\": 1");
		responseString = responseString.replace("\"geoAccuracy\": \"TODO\"", "\"geoAccuracy\": 1");
		responseString = responseString.replace("\"departure\":\"TODO\"", "\"departure\":\"2021-05-11T00:00:00\"");
		responseString = responseString.replace("\"layoverHours\":\"TODO\"", "\"layoverHours\":1");
		if (log.isDebugEnabled())
			log.debug("getItineraries::responseString=" + responseString);
		log.info("getItineraries::responseString.length=" + (responseString == null ? -1 : responseString.length()));
		
		List<Itinerary> itineraries = new ArrayList<>();

		try
		{
			RemoteItinerary remoteItineraryRS = getObjectMapper().readValue(responseString, RemoteItinerary.class);
			for (RemoteItinerary.Itinerary remoteItin : remoteItineraryRS.getData())
			{
				if (remoteItin.getItineraryId().length() == 0)
					continue;
				Itinerary itinerary = new Itinerary();
				BeanUtils.copyProperties(remoteItin, itinerary, "agent", "customer", "travellers", "items", "startDate", "endDate");
				BeanUtils.copyProperties(remoteItin.getAgent(), itinerary.getAgent());
				BeanUtils.copyProperties(remoteItin.getCustomer(), itinerary.getCustomer());
				for (RemoteItinerary.Traveller t : remoteItin.getTravellers())
				{
					Traveller traveller = new Traveller();
					BeanUtils.copyProperties(t, traveller);
					itinerary.getTravellers().add(traveller);
				}
				if (remoteItin.getItems().getAccommodation() != null)
				{
					for (RemoteItinerary.AccommodationItem a : remoteItin.getItems().getAccommodation())
					{
						AccommodationItem accommodationItem = new AccommodationItem();
						BeanUtils.copyProperties(a, accommodationItem);
						itinerary.getItems().getAccommodation().add(accommodationItem);
						if (log.isDebugEnabled())
							log.debug("getItineraries::from voucherUrl=" + a);

					}
				}
				if (remoteItin.getItems().getTransport() != null)
				{
					for (RemoteItinerary.TransportItem t : remoteItin.getItems().getTransport())
					{
						TransportItem transportItem = new TransportItem();
						BeanUtils.copyProperties(t, transportItem);
						if (t.getTransportType().equals("flight") )
							transportItem.setTransportType(TransportType.FLIGHT);
						if ( transportItem.getTransportType() != null )
						{
							itinerary.getItems().getTransport().add(transportItem);
							BeanUtils.copyProperties(t.getDepartureDetails(), transportItem.getDepartureDetails());
							BeanUtils.copyProperties(t.getArrivalDetails(), transportItem.getArrivalDetails());
						}
					}
				}
				if (remoteItin.getItems().getActivities() != null)
				{
					for (RemoteItinerary.ActivityItem a : remoteItin.getItems().getActivities())
					{
						if (a.getActivityName().length() > 0)
						{
							ActivityItem activityItem = new ActivityItem();
							BeanUtils.copyProperties(a, activityItem, "additionalInformation", "pickupTime", "images");
							activityItem.setActivityDescription(a.getActivitDescription());
							StringBuffer buf = new StringBuffer();
							for (String addi : a.getAdditionalInformation())
							{
								if (buf.length() > 0)
									buf.append(", ");
								buf.append(addi);
							}
							activityItem.setAdditionalInformation(buf.toString());
							for (RemoteItinerary.Image i : a.getImages())
							{
								activityItem.getImages().add(i.getPhotoURL());
							}
							itinerary.getItems().getActivities().add(activityItem);
						}
					}
				}
				if (remoteItin.getItems().getGit() != null)
				{
					for (RemoteItinerary.GitItem g : remoteItin.getItems().getGit())
					{
						if (g.getTourName().length() > 0)
						{
							GitItem gitItem = new GitItem();
							BeanUtils.copyProperties(g, gitItem, "images");
							for (String i : g.getImages())
							{
								gitItem.getImages().add(i);
							}
							gitItem.setStartDate(g.getStartDate().atTime(10, 0));
							gitItem.setEndDate(g.getEndDate().atTime(16, 0));
							itinerary.getItems().getGit().add(gitItem);
						}
					}
				}
				itineraries.add(fixupItinerary(itinerary));
			}
			log.info("getItineraries::returning " + itineraries.size() + " itineraries");

			return itineraries;
		}
		catch (Exception e)
		{
			log.error("getItineraries::caught exception " + e.toString(), e);
			return null;
		}
	}

	private static final boolean INSERT_GIT = false; 
	private Itinerary fixupItinerary(Itinerary itinerary)
	{
		if (itinerary.getAgent().getLogoUrl() == null || itinerary.getAgent().getLogoUrl().equals("") || itinerary.getAgent().getLogoUrl().equals("TODO"))
			itinerary.getAgent().setLogoUrl("");
			//itinerary.getAgent().setLogoUrl("https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_960_720.png");
		// TESTING
		//if (itinerary.getAgent().getName().length() == 0 )
		//{
		//	itinerary.getAgent().setName("Graeme Smith");
		//	itinerary.getAgent().setConciergeContact("+61 452 421 966");
		//}
		if ( INSERT_GIT && itinerary.getItems().getGit().size() == 0)
		{
			GitItem item = new GitItem();
			item.setTourName("Vietnam Adventure");
			item.setSupplier("Intrepid");
			item.setOperator("Hanoi Tours");
			item.setStartDate(LocalDateTime.of(2020, 3, 6, 16, 30, 0));
			item.setEndDate(LocalDateTime.of(2020, 3, 10, 8, 0, 0));
			item.setDuration(4);
			item.setDescription("Long description goes here");
			item.getImages().add("https://www.intrepidtravel.com/sites/intrepid/themes/intrepid_theme/images/tailor-made-card-image.jpg");
			item.setVoucherUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
			itinerary.getItems().getGit().add(item);
		}
		// END TESTING

		LocalDate earliestDate = null;
		LocalDate latestDate = null;
		if (itinerary.getLeadImageUrl() == null || itinerary.getLeadImageUrl().length() == 0 || itinerary.getLeadImageUrl().equals("TODO"))
		{
			for (ActivityItem activity : itinerary.getItems().getActivities())
			{
				if (activity.getImages().size() > 0)
				{
					itinerary.setLeadImageUrl(activity.getImages().get(0));
					break;
				}
			}
		}
		if (itinerary.getLeadImageUrl() == null || itinerary.getLeadImageUrl().length() == 0 || itinerary.getLeadImageUrl().equals("TODO"))
		{
			for (AccommodationItem accommodation : itinerary.getItems().getAccommodation())
			{
				if (accommodation.getImages().size() > 0)
				{
					itinerary.setLeadImageUrl(accommodation.getImages().get(0));
					break;
				}
			}
		}
		if (itinerary.getItineraryTitle().equals("TODO"))
			itinerary.setItineraryTitle(makeHolidayName(itinerary));
		if (itinerary.getItineraryOverview().equals("TODO"))
			itinerary.setItineraryOverview("");
		for (TransportItem transport : itinerary.getItems().getTransport())
		{
			if (transport.getDepartureDetails().getTerminalName().equals("TODO"))
				transport.getDepartureDetails().setTerminalName("");
			if (transport.getArrivalDetails().getTerminalName().equals("TODO"))
				transport.getArrivalDetails().setTerminalName("");
			if (transport.getCarrierImageUrl() == null || transport.getCarrierImageUrl().equals("TODO"))
				transport.setCarrierImageUrl("");
			if (log.isDebugEnabled())
				log.debug("fixupTtinerary::carrierDescription=" + transport.getCarrierDescription() + ", transportNumber=" + transport.getTransportNumber());
			if ( transport.getTransportType() != null && transport.getCarrierDescription() != null && transport.getTransportType().equals(TransportType.FLIGHT) && (transport.getCarrierDescription() == null || transport.getCarrierDescription().length() == 0))
			{
				if ( transport.getTransportNumber().equals("100"))
					transport.setCarrierDescription("QF");
				if ( transport.getTransportNumber().equals("207"))
					transport.setCarrierDescription("CX");
				if ( transport.getTransportNumber().equals("2"))
					transport.setCarrierDescription("BA");
				if ( transport.getTransportNumber().equals("331"))
					transport.setCarrierDescription("AA");
				if ( transport.getTransportNumber().equals("73"))
					transport.setCarrierDescription("QF");
			}
			if ( transport.getTransportType() != null && transport.getCarrierDescription() != null && transport.getTransportType().equals(TransportType.FLIGHT) && transport.getCarrierDescription().length() == 2)
			{
				String airlineCode = transport.getCarrierDescription();
				if ( !transport.getTransportNumber().startsWith(airlineCode))
				{
					String fullFlight = airlineCode + transport.getTransportNumber();
					transport.setTransportNumber(fullFlight);
				}
				Optional<Airline> airlineOpt = airlineRepo.findById(airlineCode);
				if (log.isDebugEnabled())
					log.debug("fixupTtinerary::found airlineOpt " + airlineOpt + " for airlineCode " + airlineCode);
				if (airlineOpt.isPresent() )
				{
					Airline airline = airlineOpt.get();
					transport.setCarrierDescription(airline.getAirline());
					transport.setCarrierImageUrl("https://pics.avs.io/200/200/" + airline.getIataCode() +  ".png");
					//transport.setCarrierImageUrl("https://flightaware.com/images/airline_logos/90p/" + airline.getIcaoCode() + ".png");
				}
			}
			if (transport.getDeparture() != null)
			{
				LocalDate d = transport.getDeparture().toLocalDate();
				if (earliestDate == null || d.isBefore(earliestDate))
					earliestDate = d;
			}
			if (transport.getArrival() != null)
			{
				LocalDate d = transport.getArrival().toLocalDate();
				if (latestDate == null || d.isAfter(latestDate))
					latestDate = d;
			}
		}
		for (ActivityItem activity : itinerary.getItems().getActivities())
		{
			if (activity.getVoucherUrl().equals("TODO"))
				activity.setVoucherUrl("");
			if (activity.getJoiningInstructions().equals("TODO"))
				activity.setJoiningInstructions("");
			if (activity.getDate() != null)
			{
				LocalDate d = activity.getDate();
				if (earliestDate == null || d.isBefore(earliestDate))
					earliestDate = d;
				if (latestDate == null || d.isAfter(latestDate))
					latestDate = d;
			}

		}
		for (AccommodationItem accommodation : itinerary.getItems().getAccommodation())
		{
			if (accommodation.getOverview() == null || accommodation.getOverview().equals("TODO"))
				accommodation.setOverview("");
			if (accommodation.getAddress().getStreet() == null || accommodation.getAddress().getStreet().equals("TODO"))
				accommodation.getAddress().setStreet("");
			if (accommodation.getAddress().getPostcode() == null || accommodation.getAddress().getPostcode().equals("TODO"))
				accommodation.getAddress().setPostcode("");
			if (accommodation.getAddress().getState() == null || accommodation.getAddress().getState().equals("TODO"))
				accommodation.getAddress().setState("");
			if (accommodation.getAddress().getCountryName().equals("TODO"))
				accommodation.getAddress().setCountryName("");
			if (accommodation.getCheckinDate() != null)
			{
				LocalDate d = accommodation.getCheckinDate();
				if (earliestDate == null || d.isBefore(earliestDate))
					earliestDate = d;
			}
			if (accommodation.getCheckoutDate() != null)
			{
				LocalDate d = accommodation.getCheckoutDate();
				if (latestDate == null || d.isAfter(latestDate))
					latestDate = d;
			}
		}
		if ( itinerary.getStartDate() == null )
			itinerary.setStartDate(earliestDate);
		if ( itinerary.getEndDate() == null )
			itinerary.setEndDate(latestDate);
		return itinerary;
	}

	private String makeHolidayName(Itinerary itinerary)
	{
		Set<String> cities = new HashSet<>();
		for (AccommodationItem accommodation : itinerary.getItems().getAccommodation())
		{
			if ( accommodation.getCityName().length() > 1)
				cities.add(accommodation.getCityName());
		}
		/*
		for (ActivityItem activity : itinerary.getItems().getActivities())
		{
			if ( activity.getCityName().length() > 1)
				cities.add(activity.getCityName());
		} */
		StringBuffer buf = new StringBuffer();
		int index = 1;
		for ( String city : cities )
		{
			if (log.isDebugEnabled())
				log.debug("makeHolidayName::index=" + index + ", city='" + city + "', cities.size()=" + cities.size());
			if ( buf.length() > 0)
			{
				if ( index++ < cities.size() - 1)
					buf.append(", ");
				else
					buf.append(" and ");
			}
			buf.append(city);
		}
		if (log.isDebugEnabled())
			log.debug("makeHolidayName::string=" + buf.toString());
		return "Your holiday to " + buf.toString();
	}
	
	private List<Itinerary> makeDummy(String email, String customerId)
	{
		List<Itinerary> itineraries = new ArrayList<>();
		Itinerary itinerary = new Itinerary();
		itinerary.setItineraryId("12346589");
		itinerary.setItineraryTitle("Your holiday to London");
		itinerary.setItineraryOverview("");
		itinerary.setItineraryId("12346589");
		itinerary.setLeadImageUrl("TODO");
		itinerary.setStartDate(LocalDate.of(2021, 3, 4));
		itinerary.setEndDate(LocalDate.of(2021, 3, 8));
		itinerary.getAgent().setName("Jane Smith");
		itinerary.getAgent().setConciergeContact("+61 412 555 666");
		itinerary.getAgent().setLogoUrl("TODO");
		itinerary.getCustomer().setEmail(email);
		itinerary.getCustomer().setFirstName("John");
		itinerary.getCustomer().setSurname("Brown");
		itinerary.getCustomer().setCustomerId(customerId);
		itinerary.setTotalPrice(new CurrencyValue("AUD", new BigDecimal("2000")));
		itinerary.setTotalPaid(new CurrencyValue("AUD", new BigDecimal("500")));
		itinerary.setTotalOutstanding(new CurrencyValue("AUD", new BigDecimal("1500")));
		{
			Traveller t = new Traveller();
			t.setAdult(true);
			t.setFirstName("John");
			t.setSurname("Brown");
			itinerary.getTravellers().add(t);
		}
		{
			Traveller t = new Traveller();
			t.setAdult(true);
			t.setFirstName("Mary");
			t.setSurname("Brown");
			itinerary.getTravellers().add(t);
		}
		{
			Traveller t = new Traveller();
			t.setAdult(false);
			t.setFirstName("Billy");
			t.setSurname("Brown");
			t.setChildAge(10);
			itinerary.getTravellers().add(t);
		}
		{
			AccommodationItem item = new AccommodationItem();
			item.setHotelName("London Hilton");
			item.setCheckinDate(LocalDate.of(2021, 3, 5));
			item.setCheckoutDate(LocalDate.of(2021, 3, 7));
			item.setRoomName("Superior King Bed");
			item.setCityName("London");
			item.setStarRating(new BigDecimal("4.5"));
			item.setOverview("Upscale lodging with sophisticated dining and a lively tropical-inspired bar, plus a spa and a gym");
			item.getAddress().setStreet("22 Park Lane");
			item.getAddress().setCity("Mayfair");
			item.getAddress().setPostcode("W1K 1BE");
			item.getAddress().setCountryCode("GB");
			item.getAddress().setCountryName("Great Britain");
			item.getAddress().setGeoCoordinates(new GeoCoordinates());
			item.getAddress().getGeoCoordinates().setLatitude(new BigDecimal(51.505));
			item.getAddress().getGeoCoordinates().setLongitude(new BigDecimal(-0.15));
			item.getAddress().getGeoCoordinates().setGeoAccuracy(new BigDecimal("1"));
			item.setTelephone("+44 2345 7890");
			item.getAmenities().add("Pool");
			item.getAmenities().add("Gym");
			item.getImages().add("https://www.hilton.com/im/en/LONHITW/8153431/18-london-hilton-on-park-lane-balmoral-suite-lounge.jpg?impolicy=crop&cw=6720&ch=2821&gravity=NorthWest&xposition=40&yposition=90&rw=1920&rh=806");
			item.setVoucherUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
			itinerary.getItems().getAccommodation().add(item);
		}
		{
			ActivityItem item = new ActivityItem();
			item.setActivityName("Big London Ticket: London Eye, Big Bus & Thames River Cruise");
			item.setActivityDescription("Experience the best views of London with The Big London Ticket. Explore the city at your own pace with a hop-on hop-off bus tour. Enjoy 360-degree views of the skyline from the London Eye, then take a 40-minute cruise on the River Thames ");
			item.setOptionDescription("Tour");
			item.setDate(LocalDate.of(2021, 3, 6));
			item.setPickupTime(LocalTime.of(9, 30));
			item.setOperator("Greyline Tours");
			//item.setDepartureGeoCoordinates(new GeoCoordinates());
			//item.getDepartureGeoCoordinates().setLatitude(new BigDecimal(50.1));
			//item.getDepartureGeoCoordinates().setLongitude(new BigDecimal(0.1));
			//item.getDepartureGeoCoordinates().setGeoAccuracy(new BigDecimal("1"));
			item.getImages().add("https://cdn.getyourguide.com/img/tour/5f3406a6c39c8.jpeg/99.jpg");
			item.setCityName("London");
			item.setCategory("Bus Tour");
			item.setDuration("2.5 hours");
			item.setDeparturePoint("Park Lane");
			item.getInclusions().add("Lunch");
			item.getInclusions().add("Drinking water");
			item.getExclusions().add("Admission to nearby attractions");
			item.setAdditionalInformation("Please wear a hat");
			item.setJoiningInstructions("");
			item.setVoucherUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
			itinerary.getItems().getActivities().add(item);
		}
		{
			GitItem item = new GitItem();
			item.setTourName("Vietnam Adventure");
			item.setSupplier("Intrepid");
			item.setOperator("Hanoi Tours");
			item.setStartDate(LocalDateTime.of(2020, 3, 6, 16, 30, 0));
			item.setEndDate(LocalDateTime.of(2020, 3, 10, 8, 0, 0));
			item.setDuration(4);
			item.setDescription("Long description goes here");
			item.getImages().add("TODO");
			item.setVoucherUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
			itinerary.getItems().getGit().add(item);
		}
		{
			TransportItem item = new TransportItem();
			item.setTransportType(TransportType.FLIGHT);
			item.setTransportNumber("TG955");
			item.setCarrierDescription("TG");
			item.setDeparture(LocalDateTime.of(2021, 3, 4, 23, 50));
			item.setArrival(LocalDateTime.of(2021, 3, 5, 1, 30));
			item.getDepartureDetails().setIataAirportCode("BKK");
			item.getDepartureDetails().setTerminalName("Main terminal");
			item.getDepartureDetails().setDescription("Suwannaphum Airport, Bangkok");
			item.getArrivalDetails().setIataAirportCode("FRA");
			item.getArrivalDetails().setTerminalName("Terminal 1");
			item.getArrivalDetails().setDescription("Frankfurt International Airport");
			item.setLayoverHours(new BigDecimal("1.5"));
			item.setVoucherUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
			itinerary.getItems().getTransport().add(item);
		}
		{
			TransportItem item = new TransportItem();
			item.setTransportType(TransportType.FLIGHT);
			item.setTransportNumber("LH77");
			item.setCarrierDescription("LH");
			item.setDeparture(LocalDateTime.of(2021, 3, 5, 3, 00));
			item.setArrival(LocalDateTime.of(2021, 3, 5, 7, 30));
			item.getDepartureDetails().setIataAirportCode("FRA");
			item.getDepartureDetails().setTerminalName("Terminal 1");
			item.getDepartureDetails().setDescription("Frankfurt International Airport");
			item.getArrivalDetails().setIataAirportCode("LHR");
			item.getArrivalDetails().setTerminalName("Terminal 4");
			item.getArrivalDetails().setDescription("Heathrow London Airport");
			item.setVoucherUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
			itinerary.getItems().getTransport().add(item);
		}
		{
			TransportItem item = new TransportItem();
			item.setTransportType(TransportType.FLIGHT);
			item.setTransportNumber("TG956");
			item.setCarrierDescription("TG");
			item.setDeparture(LocalDateTime.of(2021, 3, 7, 18, 00));
			item.setArrival(LocalDateTime.of(2021, 3, 8, 6, 00));
			item.getDepartureDetails().setIataAirportCode("LHR");
			item.getDepartureDetails().setTerminalName("Terminal 4");
			item.getDepartureDetails().setDescription("Heathrow London Airport");
			item.getArrivalDetails().setIataAirportCode("BKK");
			item.getArrivalDetails().setTerminalName("Main terminal");
			item.getArrivalDetails().setDescription("Suwannaphum Airport, Bangkok");
			item.setVoucherUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
			itinerary.getItems().getTransport().add(item);
		}
		itineraries.add(fixupItinerary(itinerary));
		return itineraries;
	}

	private final ObjectMapper getObjectMapper()
	{
		if (_objectMapper == null)
		{
			_objectMapper = new ObjectMapper();
			_objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return _objectMapper;
	}

	private ObjectMapper _objectMapper = null;
}
