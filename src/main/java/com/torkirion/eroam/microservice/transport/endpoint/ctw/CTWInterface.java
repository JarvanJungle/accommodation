package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.cache.AirlineCacheUtil;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.apidomain.*;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO.Route;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.SearchRQ.LocationStruct;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.SearchRQ.Passenger;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.SearchRQ.Segment;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.SearchRQ.Seller;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.mapping.CTWItineraryPriceRqBuilder;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.mapping.SearchRsMapping;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CTWInterface
{
	@Data
	public static class CTWAuthToken
	{
		private String site;

		private LocalDateTime lastUsed = LocalDateTime.now();

		private String token;
	}

	private String email = null;

	private String password = null;

	private String url = null;
	private String pos = null; // DCA
	private String channel = null; // 1A
	private String country = null; // US
	private String currency = null; // USD
	private String travelAgencyCode = null; // 21S19P
	private String iataNumber = null; // 49881134

	private static Map<String, CTWAuthToken> tokenCache = new HashMap<>();

	private AirlineCacheUtil airlineCacheUtil;

	public CTWInterface(SystemPropertiesDAO properties, String site, AirlineCacheUtil airlineCacheUtil) throws Exception
	{
		email = properties.getProperty(site, CTWService.CHANNEL, "email");
		password = properties.getProperty(site, CTWService.CHANNEL, "password");
		url = properties.getProperty(site, CTWService.CHANNEL, "url");
		pos = properties.getProperty(site, CTWService.CHANNEL, "pos");
		channel = properties.getProperty(site, CTWService.CHANNEL, "channel");
		country = properties.getProperty(site, CTWService.CHANNEL, "country");
		currency = properties.getProperty(site, CTWService.CHANNEL, "currency");
		travelAgencyCode = properties.getProperty(site, CTWService.CHANNEL, "travelAgencyCode");
		iataNumber = properties.getProperty(site, CTWService.CHANNEL, "iataNumber");
		this.airlineCacheUtil = airlineCacheUtil;
	}

	public List<AvailTransportSearchRS> search(AvailTransportSearchRQDTO availTransportSearchRQDTO)
	{
		if (log.isDebugEnabled())
			log.debug("search::enter");

		SearchRQ ctwSearchRQ = new SearchRQ();
		
		ctwSearchRQ.getPreferences().getFare().getFareTypes().add("PUBLIC");

		String requestCurrency;
		if (Objects.nonNull(availTransportSearchRQDTO.getCurrency()))
			requestCurrency = availTransportSearchRQDTO.getCurrency();
		else
			requestCurrency = currency;
		ctwSearchRQ.getPreferences().getResponse().setDisplayCurrency(requestCurrency);

		for ( Route route : availTransportSearchRQDTO.getRoute() )
		{
			Segment segment = new Segment();
			ctwSearchRQ.getSegments().add(segment);
			segment.getDepartureDate().setDay(route.getTravelDate().getDayOfMonth());
			segment.getDepartureDate().setMonth(route.getTravelDate().getMonthValue());
			segment.getDepartureDate().setYear(route.getTravelDate().getYear());
			LocationStruct origin = new LocationStruct();
			segment.getOrigin().add(origin);
			origin.setName(route.getDepartureIata());
			origin.setType("CITY");
			LocationStruct destination = new LocationStruct();
			segment.getDestination().add(destination);
			destination.setName(route.getArrivalIata());
			destination.setType("CITY");
			if ( route.getTransportClass() != null )
			{
				switch (route.getTransportClass())
				{
					case "F" : segment.getPreferences().getCabins().getCabin().add("FIRST"); break;
					case "C" : segment.getPreferences().getCabins().getCabin().add("BUSINESS"); break;
					case "S" : segment.getPreferences().getCabins().getCabin().add("PREMIUM_ECONOMY"); break;
					case "Y" : segment.getPreferences().getCabins().getCabin().add("ECONOMY"); break;
					case "FIRST" : segment.getPreferences().getCabins().getCabin().add("FIRST"); break;
					case "BUSINESS" : segment.getPreferences().getCabins().getCabin().add("BUSINESS"); break;
					case "PREMIUM_ECONOMY" : segment.getPreferences().getCabins().getCabin().add("PREMIUM_ECONOMY"); break;
					case "ECONOMY" : segment.getPreferences().getCabins().getCabin().add("ECONOMY"); break;
					default: 
						if (log.isDebugEnabled())
							log.warn("search::unknown cabin class " + route.getTransportClass());
				}
			}
		}

		int numberOfAdult = availTransportSearchRQDTO.getTravellers().getAdultCount();
		int id = 0;
		for ( Integer childAge : availTransportSearchRQDTO.getTravellers().getChildAges())
		{
			String ptcs;
			if (childAge <= 1) {
				ptcs = "INF";
			} else if (childAge >= 18) {
				numberOfAdult++;
				continue;
			} else {
				ptcs = "CHD";
			}
			Passenger passenger = new Passenger();
			ctwSearchRQ.getPassengers().add(passenger);
			passenger.getPtcs().add(ptcs);
			passenger.setId(id++);
			LocalDate fakeChild = LocalDate.now().minusYears(childAge).minusDays(1);
			passenger.getDateOfBirth().setDay(fakeChild.getDayOfMonth());
			passenger.getDateOfBirth().setMonth(fakeChild.getMonthValue());
			passenger.getDateOfBirth().setYear(fakeChild.getYear());
			passenger.setCurrencyOfPayment(requestCurrency);
		}

		for ( int i = 0 ; i < numberOfAdult; i++)
		{
			Passenger passenger = new Passenger();
			ctwSearchRQ.getPassengers().add(passenger);
			passenger.setId(id++);
			passenger.getPtcs().add("ADT");
			LocalDate fakeAdult = LocalDate.now().minusYears(50).minusDays(1);
			passenger.getDateOfBirth().setDay(fakeAdult.getDayOfMonth());
			passenger.getDateOfBirth().setMonth(fakeAdult.getMonthValue());
			passenger.getDateOfBirth().setYear(fakeAdult.getYear());
			passenger.setCurrencyOfPayment(requestCurrency);
		}

		Seller seller = new Seller();
		ctwSearchRQ.getSellers().add(seller);
		seller.setId(0);
		seller.setPos(pos);
		seller.setChannel(channel);
		seller.setCountry(country);
		seller.setCurrency(currency);
		seller.setTravelAgencyCode(travelAgencyCode);
		seller.setIataNumber(iataNumber);
		CTWHttpService httpService = new CTWHttpService(url, getToken(availTransportSearchRQDTO.getClient()), "ITINERARY_SHOP");
		String responseString = httpService.doCallPost("itinerary_shop", ctwSearchRQ);
		if (log.isDebugEnabled())
			log.debug("search::response is " + responseString);
		CTWItineraryShopRS itineraryShopRs = JsonUtil.parse(responseString, CTWItineraryShopRS.class);
		return SearchRsMapping.getInstance(airlineCacheUtil).map(itineraryShopRs, availTransportSearchRQDTO.getTransportCallType());
	}

	public TransportRateCheckRS rateCheck(String client, TransportRateCheckRQ rateCheckRQ) throws Exception {
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter");

		CTWItinenaryPriceRQ ctwItinenaryPrice = new CTWItineraryPriceRqBuilder().builder()
				.segments(rateCheckRQ.getSegments())
				.passengers(rateCheckRQ.getTravellers(), rateCheckRQ.getRate())
				.ticketingSeller(pos, channel, country, currency, travelAgencyCode, iataNumber)
				.build();

		CTWHttpService httpService = new CTWHttpService(url, getToken(client), "itinerary_price");
		String responseString = httpService.doCallPost("itinerary_price", ctwItinenaryPrice);
		CTWItineraryShopRS itineraryGetPrice = JsonUtil.parse(responseString, CTWItineraryShopRS.class);
		if(log.isDebugEnabled()) {
			log.debug("ctwbook::rateCheck: itineraryGetPrice: {}", itineraryGetPrice);
		}

		List<CTWCommon.ItineraryOffer> itineraryOffers = itineraryGetPrice.getItineraryOffers();

		TransportRateCheckRS rateCheckRS = new TransportRateCheckRS();
		if(itineraryOffers.isEmpty()) {
			if(log.isDebugEnabled()) 
				log.debug("rateCheck::itineraryOffers.isEmpty()");
			
				rateCheckRS.getErrors().add(new ResponseExtraInformation("501", "Cannot find an offer that matches the provided rate"));
				return rateCheckRS;
		}

		itineraryOffers = itineraryOffers.stream().sorted((a, b) -> Double.compare(Double.parseDouble(a.getTotal().getAmount()),
						Double.parseDouble(b.getTotal().getAmount())))
				.collect(Collectors.toList());

		List<CTWCommon.ItineraryOffer> itineraryOffersChosen = new ArrayList<>();
//		for(CTWCommon.ItineraryOffer offer : itineraryOffers) {
//			if(!offer.getTotal().getCurrency().equals(rateCheckRQ.getRate().getCurrencyId())) {
//				if(log.isDebugEnabled())
//					log.debug("rateCheck::offer currency " + offer.getTotal().getCurrency() + " differs from requested " + rateCheckRQ.getRate().getCurrencyId());
//				rateCheckRS.getErrors().add(new ResponseExtraInformation("501", "currency differs"));
//				return rateCheckRS;
//			}
//
//			if(rateCheckRQ.getAllowFarebasisUpgrades() == true) {
//				if(new BigDecimal(offer.getTotal().getAmount()).compareTo(rateCheckRQ.getRate().getAmount()) >= 0) {
//					itineraryOffersChosen.add(offer);
//					break;
//				}
//			}
//			if(new BigDecimal(offer.getTotal().getAmount()).compareTo(rateCheckRQ.getRate().getAmount()) == 0) {
//				itineraryOffersChosen.add(offer);
//				break;
//			}
//		}
//		if(itineraryOffersChosen.isEmpty()) {
//			if(log.isDebugEnabled())
//				log.debug("rateCheck::itineraryOffersChosen.isEmpty()");
//			rateCheckRS.getErrors().add(new ResponseExtraInformation("501", "Cannot find an offer that matches the provided rate"));
//			return rateCheckRS;
//		}


		double minCost = 0;
		for(CTWCommon.ItineraryOffer offer : itineraryOffers) {
			if(log.isDebugEnabled()) {
				log.debug("currency: {}, amount: {}", offer.getTotal().getCurrency(), offer.getTotal().getAmount());
			}
			if(!rateCheckRQ.getRate().getCurrencyId().equals(offer.getTotal().getCurrency())) {
				rateCheckRS.getErrors().add(new ResponseExtraInformation("502", "the currency required is: " + offer.getTotal().getCurrency()));
				return rateCheckRS;
			}
			if((rateCheckRQ.getRate().getAmount().compareTo(new BigDecimal(offer.getTotal().getAmount())) == 0 || rateCheckRQ.getRate().getAmount().compareTo(new BigDecimal(offer.getTotal().getAmount())) == 1)) {
				itineraryOffersChosen.add(offer);
				break;
			} else {
				if(minCost == 0) {
					minCost = Double.parseDouble(offer.getTotal().getAmount());
				} else {
					minCost = Math.min(minCost, Double.parseDouble(offer.getTotal().getAmount()));
				}
			}
		}
		if(itineraryOffersChosen.size() == 0) {
			if(log.isDebugEnabled())
				log.debug("book::itineraryOffer is null");
			rateCheckRS.getErrors().add(new ResponseExtraInformation("501", "Please change the rate value you. the smallest rate that allows is: " + minCost));
			return rateCheckRS;
		}

		itineraryGetPrice.setItineraryOffers(itineraryOffersChosen);
		List<AvailTransportSearchRS> availTransportSearchRSs = SearchRsMapping.getInstance(airlineCacheUtil).map(itineraryGetPrice, rateCheckRQ.getTransport_call_type());
		//TODO - will update after make confirmation for check rate
		return new TransportRateCheckRS(availTransportSearchRSs.get(0));
	}

	public TransportBookRS book(String client, TransportBookRQ bookRQ) throws Exception {
		if (log.isDebugEnabled())
			log.debug("book::enter:  {}", bookRQ);
		TransportBookRS bookRS = new TransportBookRS();
		bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
		//Step 1 call itinenary_price to get info for booking
		CTWItinenaryPriceRQ ctwItinenaryPrice = new CTWItineraryPriceRqBuilder().builder()
				.segments(bookRQ.getSegments())
				.passengersByDoc(bookRQ.getTravellers(), bookRQ.getRate())
				.ticketingSeller(pos, channel, country, currency, travelAgencyCode, iataNumber)
				.build();

		CTWHttpService httpService = new CTWHttpService(url, getToken(client), "itinerary_price");

		if(log.isDebugEnabled()) {
			log.debug("book::ctwItinenaryPrice::\n {}", JsonUtil.convertToPrettyJson(ctwItinenaryPrice));
		}

		String itineraryPriceText = httpService.doCallPost("itinerary_price", ctwItinenaryPrice);
		if(log.isDebugEnabled()) {
			log.debug("rateCheck: itineraryPriceText: {}", itineraryPriceText);
		}

		CTWItineraryShopRS itineraryGetPrice = JsonUtil.parse(itineraryPriceText, CTWItineraryShopRS.class);
		if(log.isDebugEnabled()) {
			log.debug("rateCheck: itineraryGetPrice: {}", itineraryGetPrice);
		}

		//Step 2 get offer and dataLibrary
		CTWCommon.ItineraryOffer itineraryOffer = null;
		CTWCommon.DataLibrary dataLibrary = itineraryGetPrice.getDataLibrary();

		List<CTWCommon.ItineraryOffer> itineraryOffers = itineraryGetPrice.getItineraryOffers();
		double minCost = 0;
		for(CTWCommon.ItineraryOffer offer : itineraryOffers) {
			if(log.isDebugEnabled()) {
				log.debug("currency: {}, amount: {}", offer.getTotal().getCurrency(), offer.getTotal().getAmount());
			}
			if(!bookRQ.getRate().getCurrencyId().equals(offer.getTotal().getCurrency())) {
				bookRS.getErrors().add(new ResponseExtraInformation("502", "the currency required is: " + offer.getTotal().getCurrency()));
				return bookRS;
			}
			if((bookRQ.getRate().getAmount().compareTo(new BigDecimal(offer.getTotal().getAmount())) == 0 || bookRQ.getRate().getAmount().compareTo(new BigDecimal(offer.getTotal().getAmount())) == 1)) {
				itineraryOffer = offer;
			} else {
				if(minCost == 0) {
					minCost = Double.parseDouble(offer.getTotal().getAmount());
				} else {
					minCost = Math.min(minCost, Double.parseDouble(offer.getTotal().getAmount()));
				}
			}
		}
		if(itineraryOffer == null) {
			if(log.isDebugEnabled()) 
				log.debug("book::itineraryOffer is null");
			bookRS.getErrors().add(new ResponseExtraInformation("501", "Please change the rate value you. the smallest rate that allows is: " + minCost));
			return bookRS;
		}

		CTWBookRQ ctwBookRQ = new CTWBookRQ();
		ctwBookRQ.setItineraryOffer(itineraryOffer);
		ctwBookRQ.setDataLibrary(dataLibrary);

		//------------------------------------------------------------------------------------------------------------//
		//Step 3: call book
		httpService = new CTWHttpService(url, getToken(client), "order_booking_create");
		if(log.isDebugEnabled()) {
			log.debug("book::start_book:\n {}", JsonUtil.convertToPrettyJson(ctwBookRQ));
		}
		String orderBookingCreateText = httpService.doCallPost("order_booking_create", ctwBookRQ);
		if(log.isDebugEnabled()) {
			log.debug("book::orderBookingCreateText: {}", orderBookingCreateText);
		}

		CTWBookRS ctwbookRS = JsonUtil.parse(orderBookingCreateText, CTWBookRS.class);
		if(!"SUCCESS".equals(ctwbookRS.getResponseStatus().getStatusCode())) {
			bookRS.getErrors().add(new ResponseExtraInformation("502", "booking failed"));
			return bookRS;
		}

		CTWItineraryShopRS itineraryShopRS = new CTWItineraryShopRS();
		itineraryShopRS.setItineraryOffers(List.of(itineraryOffer));
		itineraryShopRS.setDataLibrary(dataLibrary);

		AvailTransportSearchRS availTransportSearchRS = SearchRsMapping.getInstance(airlineCacheUtil).map(itineraryShopRS, 1).get(0);

		makeBookRs(bookRS, availTransportSearchRS);
		bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
		bookRS.setBookingReference(itineraryOffer.getUuid());

		return bookRS;
	}

	private void makeBookRs(TransportBookRS bookRS, AvailTransportSearchRS availTransportSearchRS) {
		bookRS.setId(availTransportSearchRS.getId());
		bookRS.setType(availTransportSearchRS.getType());
		bookRS.setProvider(availTransportSearchRS.getProvider());
		bookRS.setItineraryPricingInfo(availTransportSearchRS.getItineraryPricingInfo());
		bookRS.setSegments(availTransportSearchRS.getSegments());
		bookRS.setCommonDatas(availTransportSearchRS.getCommonDatas());
		bookRS.setDuration(availTransportSearchRS.getDuration());
		bookRS.setDuration_time(availTransportSearchRS.getDuration_time());
	}

	protected String getToken(String site)
	{
		if (log.isDebugEnabled())
			log.debug("getToken::enter for " + site);
		CTWAuthToken ctwAuthToken = tokenCache.get(site);
		LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
		if (ctwAuthToken != null && ctwAuthToken.getLastUsed().isAfter(oneHourAgo))
		{
			ctwAuthToken.setLastUsed(LocalDateTime.now());
			return ctwAuthToken.getToken();
		}
		String token = login(site);
		if (token != null)
		{
			ctwAuthToken = new CTWAuthToken();
			ctwAuthToken.setSite(site);
			ctwAuthToken.setToken(token);
			tokenCache.put(site, ctwAuthToken);
		}
		return token;
	}

	protected String login(String site)
	{
		if (log.isDebugEnabled())
			log.debug("login::enter for " + site);

		CTWHttpService httpService = new CTWHttpService(url);

		try
		{
			LoginRQ loginRQ = new LoginRQ();
			loginRQ.setEmail(email);
			loginRQ.setPassword(password);

			String responseString = httpService.doCallPost("login", loginRQ);
			if (log.isDebugEnabled())
				log.debug("login::responseString = " + responseString);
			LoginRS loginRS = getObjectMapper().readValue(responseString, LoginRS.class);
			return loginRS.getToken();
		}
		catch (Exception e)
		{
			log.error("login::caught exception " + e.toString(), e);
			return null;
		}
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
