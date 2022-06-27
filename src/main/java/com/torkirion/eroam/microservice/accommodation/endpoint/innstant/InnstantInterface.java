package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRQ;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.*;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationChannelService;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.datadomain.SystemProperty;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class InnstantInterface
{
	private DestinationSearchRQDataRepo destinationSearchRQDataRepo;

	public static final String API_VERSION = "1.0";

	public static final int LIMITED_ID_SEARCH = 250;

	private SystemPropertiesDAO properties;

	private InnstantRCAPIProperties innstantRCAPIProperties;

	public InnstantRCAPIProperties getInnstantRCAPIProperties()
	{
		return innstantRCAPIProperties;
	}

	public InnstantInterface(SystemPropertiesDAO properties, String site, String channel, DestinationSearchRQDataRepo destinationSearchRQDataRepo) throws Exception
	{
		this.properties = properties;
		this.destinationSearchRQDataRepo = destinationSearchRQDataRepo;
		init(site, channel);

		// System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		// System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		// System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
		// System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
	}

	private void init(String site, String channel) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("init::entering with site " + site + ", channel " + channel + ", properties=" + properties);

		innstantRCAPIProperties = new InnstantRCAPIProperties(properties, site);
	}

	private static final BigDecimal HB_MARKUP = new BigDecimal("1.1363636");

	public static BigDecimal applyInventoryMarkup(BigDecimal nett, BigDecimal gross) throws Exception
	{
		return nett.multiply(HB_MARKUP).setScale(0, RoundingMode.UP);
	}

	public InnstantPreBookRS preBook(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("preBook::enter for " + rateCheckRQDTO);
		InnstantAvailabilityRQ innstantAvailabilityRQ = makeAvailabilityRQ(rateCheckRQDTO);

		List<InnstantPreBookRQ> innstantPreBookRQList = new ArrayList<>();
		// searchRequest
		InnstantPreBookRQ innstantPreBookRQ = new InnstantPreBookRQ();
		innstantPreBookRQ.setSearchRequest(innstantAvailabilityRQ);
		// searchCodes
		List<SearchCodes> searchCodesList = new ArrayList<>();
		for (String bookingCode : rateCheckRQDTO.getBookingCodes())
		{
			if (log.isDebugEnabled())
				log.debug("preBook::bookingCode=" + bookingCode);
			String[] bookCodeSplit = bookingCode.split("_");
			if (bookCodeSplit.length < 2)
				throw new Exception("Rate-check::Invalid booking code!");
			SearchCodes searchCodes = new SearchCodes();
			searchCodes.setCode(bookCodeSplit[1]);
			searchCodes.setPax(innstantAvailabilityRQ.getPax());
			searchCodesList.add(searchCodes);
			innstantAvailabilityRQ.setDestinations(getDestinations(bookCodeSplit[0]));
		}
		innstantPreBookRQ.setSearchCodes(searchCodesList);
		innstantPreBookRQList.add(innstantPreBookRQ);
		ServicesPreBookDTO servicesPreBookDTO = new ServicesPreBookDTO();
		servicesPreBookDTO.setServices(innstantPreBookRQList);
		HttpService httpService = new InnstantRCHttpBook(innstantRCAPIProperties);
		String response = httpService.doCallPost("pre-book", servicesPreBookDTO);
		InnstantPreBookRS innstantPreBookRS = getObjectMapper().readValue(response, InnstantPreBookRS.class);
		innstantPreBookRS.getContent().setBookingCode(rateCheckRQDTO.getBookingCodes().get(0));
		return innstantPreBookRS;
	}

	private List<InnstantAvailabilityRQ.Destination> getDestinations(String destinationSearchId) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("getDestinations::enter with " + destinationSearchId);
		Optional<DestinationSearchRQData> optDesSearch = destinationSearchRQDataRepo.findById(Long.valueOf(destinationSearchId));

		if (!optDesSearch.isPresent())
		{
			throw new Exception("Rate-check::Not found data search. Please create a new search!");
		}
		InnstantAvailabilityRQ.Destination[] destinations = new ObjectMapper().readValue(optDesSearch.get().getValue(), InnstantAvailabilityRQ.Destination[].class);

		return Arrays.asList(destinations);
	}

	public Map<Integer, List<ResultDTO>> startSearchHotelById(AvailSearchByHotelIdRQDTO availSearchRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("startSearchHotels::entering for availSearchRQ=" + availSearchRQ);

		InnstantAvailabilityRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);

		availabilityRQ.setDestinations(new ArrayList<>());
		InnstantAvailabilityRQ.Destination destination = new InnstantAvailabilityRQ.Destination();

		for (String hotelId : availSearchRQ.getHotelIds())
		{
			// Example: "IN824846" => sub Id = "824846"
			destination.setId(Integer.valueOf(hotelId.substring(2)));
			destination.setType("hotel");
			availabilityRQ.getDestinations().add(destination);
		}

		Map<Integer, List<ResultDTO>> hmResult = new HashMap<>();
		InnstantAvailabilityRS searchRS = searchHotels(availabilityRQ);

		for (ResultDTO results : searchRS.getResults())
		{
			Integer key = results.getItems().get(0).getHotelId();
			results.setDestinationSearchRQId(getDestinationSearchId(availabilityRQ.getDestinations()));

			if (hmResult.get(key) != null)
			{
				hmResult.get(key).add(results);
			}
			else
			{
				List<ResultDTO> value = new ArrayList<>();
				value.add(results);
				hmResult.put(key, value);
			}
		}
		return hmResult;
	}

	private Long getDestinationSearchId(List<InnstantAvailabilityRQ.Destination> destinations) throws JsonProcessingException
	{
		DestinationSearchRQData destinationSearchRQData = new DestinationSearchRQData(destinations);
		destinationSearchRQDataRepo.save(destinationSearchRQData);
		return destinationSearchRQData.getId();
	}

	public Map<Integer, List<ResultDTO>> startSearchHotels(AvailSearchByGeocordBoxRQDTO availSearchRQ, List<String> hotelIds) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("startSearchHotels::entering for availSearchRQ=" + availSearchRQ);

		InnstantAvailabilityRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);
		Map<Integer, List<ResultDTO>> hmResult = new HashMap<>();
		if (hotelIds.size() > 0)
		{
			int batch = 1;
			int count = 0;
			int page = LIMITED_ID_SEARCH;
			List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
			destinationSearchRQDataRepo.deleteAll();

			while (count < hotelIds.size())
			{
				int startIndex = (batch - 1) * page;
				int lastIndex = batch * page;
				if (lastIndex >= hotelIds.size())
				{
					lastIndex = hotelIds.size();
				}
				log.info("start {}", startIndex);
				log.info("lastIndex {}", lastIndex);
				availabilityRQ.setDestinations(new ArrayList<>());
				List<String> dataSub = hotelIds.subList(startIndex, lastIndex);

				CompletableFuture<Void> requestCompletableFuture = CompletableFuture.runAsync(() -> {
					for (String hotelId : dataSub)
					{
						InnstantAvailabilityRQ.Destination destination = new InnstantAvailabilityRQ.Destination();
						destination.setId(Integer.valueOf(hotelId));
						destination.setType("hotel");
						availabilityRQ.getDestinations().add(destination);
					}

					try
					{
						Long desSearchId = getDestinationSearchId(availabilityRQ.getDestinations());
						for (ResultDTO results : searchHotels(availabilityRQ).getResults())
						{
							Integer key = results.getItems().get(0).getHotelId();
							results.setDestinationSearchRQId(desSearchId);

							if (hmResult.get(key) != null)
							{
								hmResult.get(key).add(results);
							}
							else
							{
								List<ResultDTO> value = new ArrayList<>();
								value.add(results);
								hmResult.put(key, value);
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				});
				count = lastIndex;
				batch++;
				completableFutures.add(requestCompletableFuture);
			}
			CompletableFuture[] futureResultArray = completableFutures.toArray(new CompletableFuture[completableFutures.size()]);
			CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futureResultArray);
			combinedFuture.get();
		}
//		List<String> destinationIdList = destinationsStaticDataRepo.findDestinationIdByLatLon(latNorthwest, lonNorthwest, latSoutheast, lonSoutheast);
//		if(destinationIdList.size()>0){
//			Map<Integer, List<ResultDTO>> hmResult = new HashMap<>();
//			for (String destinationId : destinationIdList){
//				InnstantAvailabilityRQ.Destination destination = new InnstantAvailabilityRQ.Destination();
//				destination.setId(Integer.valueOf(destinationId));
//				destination.setType("location");
//				availabilityRQ.setDestinations(new ArrayList<>());
//				availabilityRQ.getDestinations().add(destination);
//
//				for (ResultDTO results : searchHotels(availabilityRQ).getResults()) {
//					Integer key = results.getItems().get(0).getHotelId();
//					results.setDestinationId(destination.getId());
//					results.setDestinationType(destination.getType());
//
//					if(hmResult.get(key)!=null){
//						hmResult.get(key).add(results);
//					}
//					else {
//						List<ResultDTO> value = new ArrayList<>();
//						value.add(results);
//						hmResult.put(key, value);
//					}
//				}
//			}
//			return hmResult;
//		}
		return hmResult;
	}

	private InnstantAvailabilityRQ makeAvailabilityRQ(AvailSearchRQDTO availSearchRQ)
	{
		if (log.isDebugEnabled())
			log.debug("makeAvailabilityRQ::entering for availSearchRQ=" + availSearchRQ);

		InnstantAvailabilityRQ availabilityRQ = new InnstantAvailabilityRQ();

		availabilityRQ.setDates(new InnstantAvailabilityRQ.Dates());
		availabilityRQ.getDates().setFrom(df2YYYYMMDD.format(availSearchRQ.getCheckin()));
		availabilityRQ.getDates().setTo(df2YYYYMMDD.format(availSearchRQ.getCheckout()));
		availabilityRQ.setCustomerCountry(availSearchRQ.getCountryCodeOfOrigin() == null ? innstantRCAPIProperties.country : availSearchRQ.getCountryCodeOfOrigin() );
		String[] currencies = { "USD" };
		availabilityRQ.setCurrencies(Arrays.asList(currencies));
		availabilityRQ.setPax(new ArrayList<>());
		for (TravellerMix travellerSet : availSearchRQ.getTravellers())
		{
			PaxDTO pax = new PaxDTO();
			pax.setAdults(travellerSet.getAdultCount());
			if (travellerSet.getChildAges() == null || travellerSet.getChildAges().size() < 1)
				pax.setChildren(null);
			else
			{
				pax.setChildren(travellerSet.getChildAges());
			}
			availabilityRQ.getPax().add(pax);
		}
		return availabilityRQ;
	}

	private InnstantAvailabilityRS searchHotels(InnstantAvailabilityRQ availabilityRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("searchHotels::entering for availabilityRQ=" + availabilityRQ);
		long searchStartTime = System.currentTimeMillis();

		try
		{
			HttpService httpService = new InnstantRCHttpSearchService(innstantRCAPIProperties);
	
			String rpString = httpService.doCallPost("hotels/search", availabilityRQ);
			log.debug("searchHotels::search responseString = " + rpString);
			InnstantAvailabilityRS availRS = getObjectMapper().readValue(rpString, InnstantAvailabilityRS.class);
			if(availRS == null) {
				log.debug("searchHotels::avail response is empty");
				return null;
			}
			if(availRS.getError() != null) {
				throw new Exception("Innstant:: " + availRS.getError().getMessage());
			}
			if(availRS.getResults() == null) {
				availRS.setResults(new ArrayList<>());
			}
			if (log.isDebugEnabled())
				log.debug("searchHotels::first call returned " + availRS.getResults().size() + " results");
	
			boolean complete = availRS.getStatus().equals("done");
			int loopCount = 0;
			Long lastTimestamp = availRS.getTimestamp();
			while ( !complete )
			{
				String responseString = httpService.doCallPost("hotels/poll/"+lastTimestamp.toString(), availabilityRQ);
				if (log.isDebugEnabled())
					log.debug("searchHotels::poll responseString = " + responseString);
				InnstantAvailabilityRS pollRS = getObjectMapper().readValue(responseString, InnstantAvailabilityRS.class);
				if ( pollRS.getResults() != null )
				{
					availRS.getResults().addAll(pollRS.getResults());
					if (log.isDebugEnabled())
						log.debug("searchHotels::poll returned " + pollRS.getResults().size() + " results");
				}
				complete = pollRS.getStatus().equals("done");
				lastTimestamp = pollRS.getTimestamp();
				if ( loopCount++ > 5)
					break;
				Thread.sleep(1000);
			}
			if (log.isDebugEnabled())
				log.debug("searchHotels::returning " + availRS.getResults().size() + " results");
			if (log.isDebugEnabled())
				log.debug("searchHotels::time taken = " + (System.currentTimeMillis() - searchStartTime));
			return availRS;
		}
		catch (Exception e)
		{
			log.error("searchHotels::caught exception " + e.toString(), e);
			return null;
		}
	}

	public InnstantBookRS book(AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::entering");

		InnstantBookRQ innstantBookRQ = new InnstantBookRQ();
		// CUSTOMER
		Customer customer = new Customer();

		NameDTO nameDTO = new NameDTO();
		nameDTO.setFirst(bookRQ.getBooker().getGivenName());
		nameDTO.setLast(bookRQ.getBooker().getSurname());
		;
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setPhone(bookRQ.getBooker().getTelephone());
		contactDTO.setEmail(innstantRCAPIProperties.email);
		contactDTO.setCity(innstantRCAPIProperties.city);
		contactDTO.setCountry(innstantRCAPIProperties.country);
		contactDTO.setZip(innstantRCAPIProperties.zip);
		contactDTO.setAddress(innstantRCAPIProperties.address);
		customer.setContact(contactDTO);
		customer.setName(nameDTO);
		customer.setTitle(bookRQ.getBooker().getTitle().toUpperCase());
		innstantBookRQ.setCustomer(customer);
		// PAYMENT METHOD
		PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO();
		paymentMethodDTO.setMethodName("account_credit");
		innstantBookRQ.setPaymentMethod(paymentMethodDTO);
		// SERVICES
		LocalDate checkin = null;
		LocalDate checkout = null;
		String hotelId = null;
		String currency = "";

		List<InnstantBookRQ.BookingRequest> bookingRequestList = new ArrayList<>();

		InnstantAvailabilityRQ innstantAvailabilityRQ = new InnstantAvailabilityRQ();

		for (AccommodationBookRQ.AccommodationRequestItem item : bookRQ.getItems())
		{
			// eRoam sometimes forget to put dates in multirooms!
			if (checkin == null && item.getCheckin() != null)
				checkin = item.getCheckin();
			if (checkout == null && item.getCheckout() != null)
				checkout = item.getCheckout();
			if (hotelId == null)
				hotelId = item.getHotelID();
			if (checkin != null && checkout != null && item.getCheckin() != null && item.getCheckout() != null
					&& (!checkin.equals(item.getCheckin()) || !checkout.equals(item.getCheckout()) || !hotelId.equals(item.getHotelID())))
			{
				throw new Exception("Multi-room bookings allowed, multi-date not allowed");
			}
			// BOOKING REQUEST
			InnstantBookRQ.BookingRequest bookingRequest = new InnstantBookRQ.BookingRequest();
			String[] bookCodeSplit = item.getBookingCode().split("_");

			if (bookCodeSplit.length < 2)
				throw new Exception("Booking::Invalid booking code!");
			bookingRequest.setCode(bookCodeSplit[1]);
			bookingRequest.setToken("3B3CDD9D");
			// pax detail
			List<InnstantBookRQ.PaxDetail> paxDetailList = new ArrayList<>();
			List<InnstantBookRQ.Adult> adultList = new ArrayList<>();
			for (Integer index : item.getTravellerIndex())
			{
				Traveller traveller = bookRQ.getTravellers().get(index);
				ContactDTO contactTraveller = new ContactDTO();
				NameDTO nameTraveller = new NameDTO();
				contactTraveller.setEmail(traveller.getEmail());
				contactTraveller.setPhone(traveller.getTelephone());
				contactTraveller.setCity(innstantRCAPIProperties.city);
				contactTraveller.setCountry(innstantRCAPIProperties.country);
				contactTraveller.setZip(innstantRCAPIProperties.zip);
				contactTraveller.setAddress(innstantRCAPIProperties.address);

				nameTraveller.setFirst(traveller.getGivenName());
				nameTraveller.setLast(traveller.getSurname());

				InnstantBookRQ.Adult adult = new InnstantBookRQ.Adult();
				adult.setTitle(traveller.getTitle().toUpperCase());
				adult.setContact(contactTraveller);
				adult.setName(nameTraveller);
//				adult.setBirthDate(dateFormat.format(traveller.getBirthDate()));
				adultList.add(adult);
			}
			InnstantBookRQ.PaxDetail paxDetail = new InnstantBookRQ.PaxDetail();
			adultList.get(0).setLead(true);
			paxDetail.setAdults(adultList);
			paxDetailList.add(paxDetail);

			bookingRequest.setPax(paxDetailList);
			bookingRequestList.add(bookingRequest);

			currency = item.getSupplyRate().getCurrencyId();
			innstantAvailabilityRQ.setDestinations(getDestinations(bookCodeSplit[0]));
		}
		// SEARCH REQUEST
		List<String> currencies = new ArrayList<>();
		currencies.add(currency);
		PaxDTO paxDTO = new PaxDTO();
		paxDTO.setAdults(bookRQ.getTravellers().size());
		List<PaxDTO> paxDTOList = new ArrayList<>();
		paxDTOList.add(paxDTO);

		innstantAvailabilityRQ.setCustomerCountry(bookRQ.getCountryCodeOfOrigin() == null ? innstantRCAPIProperties.country : bookRQ.getCountryCodeOfOrigin());
		innstantAvailabilityRQ.setCurrencies(currencies);
		innstantAvailabilityRQ.setDates(new InnstantAvailabilityRQ.Dates());
		innstantAvailabilityRQ.getDates().setFrom(df2YYYYMMDD.format(checkin));
		innstantAvailabilityRQ.getDates().setTo(df2YYYYMMDD.format(checkout));
		innstantAvailabilityRQ.setPax(paxDTOList);
		InnstantBookRQ.BookingService bookingService = new InnstantBookRQ.BookingService();
		bookingService.setBookingRequest(bookingRequestList);
		bookingService.setSearchRequest(innstantAvailabilityRQ);

		innstantBookRQ.setServices(new ArrayList<>());
		innstantBookRQ.getServices().add(bookingService);

		HttpService httpService = new InnstantRCHttpBook(innstantRCAPIProperties);
		String response = httpService.doCallPost("book", innstantBookRQ);
		InnstantBookRS innstantBookRS = getObjectMapper().readValue(response, InnstantBookRS.class);

		return innstantBookRS;
	}

	private InnstantAvailabilityRQ.Destination getDestination(String desId, String type) throws Exception
	{
		if (desId == null || type == null)
			throw new Exception("Destination isn't providered!");

		InnstantAvailabilityRQ.Destination destination = new InnstantAvailabilityRQ.Destination();

		destination.setId(Integer.valueOf(desId));
		destination.setType(type);

		if (!destination.getType().equals("location") && !destination.getType().equals("hotel"))
		{
			throw new Exception("Destination type must be 'location' or 'hotel'.");
		}

		return destination;
	}

	public InnstantCancelBookRS cancel(AccommodationCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::entering");
		InnstantCancelBookRQ cancelBookRQ = new InnstantCancelBookRQ();
		cancelBookRQ.setBookingID(Long.valueOf(cancelRQ.getBookingReference()));

		long startTime = System.currentTimeMillis();

		HttpService httpService = new InnstantRCHttpBook(innstantRCAPIProperties);
		String responseString = httpService.doCallPost("booking-cancel", cancelBookRQ);

		if (log.isDebugEnabled())
			log.debug("cancel::responseString = " + responseString);

		if (log.isDebugEnabled())
			log.debug("cancel::time taken = " + (System.currentTimeMillis() - startTime));
		try
		{
			InnstantCancelBookRS cancelBookRS = getObjectMapper().readValue(responseString, InnstantCancelBookRS.class);
			return cancelBookRS;
		}
		catch (Exception e)
		{
			log.error("cancel::caught exception " + e, e);
			return null;
		}
	}

	private static final String cancellationFlag = "CANCELLATION";

	public String doCallPost(String application, String callType, String requestData)
	{
		if (log.isDebugEnabled())
			log.debug("doCallPost::entering");
		HttpService httpService = new InnstantRCHttpService(innstantRCAPIProperties);
		String response = httpService.doCallPost("/" + application + "/" + API_VERSION + "/" + callType, requestData);
		return response;
	}

	public String doCallGet(String application, String callType, Map<String, String> params)
	{
		if (log.isDebugEnabled())
			log.debug("doCallGet::entering");
		HttpService httpService = new InnstantRCHttpService(innstantRCAPIProperties);
		String response = httpService.doCallGet("/" + application + "/" + API_VERSION + "/" + callType, params);
		return response;
	}

	public String doCallDelete(String application, String callType, Map<String, String> parameters)
	{
		if (log.isDebugEnabled())
			log.debug("doCallDelete::entering");
		HttpService httpService = new InnstantRCHttpService(innstantRCAPIProperties);
		String response = httpService.doCallDelete("/" + application + "/" + API_VERSION + "/" + callType, parameters);
		return response;
	}

	public boolean allowZeroCommissionProduct()
	{
		return innstantRCAPIProperties.allowZeroCommissionProduct;
	}

	private static JAXBContext jaxbContext;

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

	private static DateTimeFormatter df2YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final String HB_VOUCHER_INFO = "Payable through {0}, acting as agent for the service operating company, details of which can be provided upon request. VAT: {1} Reference: {2}";
}
