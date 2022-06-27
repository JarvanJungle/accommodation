package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.hotelbeds.schemas.messages.AvailabilityRS;
import com.hotelbeds.schemas.messages.CancellationPolicy;
import com.hotelbeds.schemas.messages.CheckRateRS;
import com.hotelbeds.schemas.messages.HotelResponse;
import com.hotelbeds.schemas.messages.Offer;
import com.hotelbeds.schemas.messages.RateHotelResponse;
import com.hotelbeds.schemas.messages.RatePromotion;
import com.hotelbeds.schemas.messages.RoomHotelResponse;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRQ.TransferRequestItem;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRQ.SpecialRequest;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRS.ResponseItem;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRS;
import com.torkirion.eroam.microservice.transfers.apidomain.EndpointType;
import com.torkirion.eroam.microservice.transfers.apidomain.RetrieveTransferRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.RetrieveTransferRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO.Endpoint;
import com.torkirion.eroam.microservice.transfers.endpoint.TransferServiceIF;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.BookingRQ.Flight;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.BookingRQ.Luggage;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.BookingRS.Booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class JayrideService implements TransferServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private AccommodationRCService accommodationRCService;

	private AirportRepo airportRepo;

	private AirportTerminalRepo airportTerminalRepo;

	private JayrideStaticLoader loader;

	public static final String CHANNEL = "JAYRIDE";

	public static final String CHANNEL_PREFIX = "JR";

	@Override
	public List<TransferResult> searchByCodes(SearchRQDTO searchRQ)
	{
		log.debug("searchByCodes::enter for " + searchRQ);

		CreateQuoteRQ createQuoteRQ = new CreateQuoteRQ();
		createQuoteRQ.setFrom_location(makeLocation(searchRQ.getStartPoint(), searchRQ.getClient()));
		createQuoteRQ.setTo_location(makeLocation(searchRQ.getEndPoint(), searchRQ.getClient()));
		createQuoteRQ.setFlight(new CreateQuoteRQ.Flight());
		if (searchRQ.getStartPoint().getEndpointType().equals(EndpointType.AIRPORT) || searchRQ.getIncludeReturn())
		{
			if (searchRQ.getFlightArrivalTime() != null)
			{
				createQuoteRQ.getFlight().setLanding_datetime_local(df_YYYmmddhhmm.format(searchRQ.getFlightArrivalTime()));
			}
		}
		if (searchRQ.getEndPoint().getEndpointType().equals(EndpointType.AIRPORT) || searchRQ.getIncludeReturn())
		{
			if (searchRQ.getFlightDepartureTime() != null)
			{
				createQuoteRQ.getFlight().setDeparture_datetime_local(df_YYYmmddhhmm.format(searchRQ.getFlightDepartureTime()));
			}
		}
		createQuoteRQ.setInclude_return_trip(searchRQ.getIncludeReturn());
		createQuoteRQ.setPassenger(new CreateQuoteRQ.Passenger());
		createQuoteRQ.getPassenger().setCount(searchRQ.getTravellers().getAdultCount() + (searchRQ.getTravellers().getChildAges() == null ? 0 : searchRQ.getTravellers().getChildAges().size()));

		try
		{
			JayrideInterface jayrideInterface = new JayrideInterface(propertiesDAO, searchRQ.getClient(), CHANNEL);
			long timer1 = System.currentTimeMillis();
			List<TransferResult> results = jayrideInterface.createQuote(createQuoteRQ, searchRQ);
			long totalTime1 = (System.currentTimeMillis() - timer1);
			log.info("search::time in jayride search was " + totalTime1 + " millis");
			return results;
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		return new ArrayList<>();
	}

	@Override
	public TransferBookRS book(String client, String subclient, TransferBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter");

		JayrideInterface jayrideInterface = new JayrideInterface(propertiesDAO, client, CHANNEL);

		BookingRQ bookingRQ = new BookingRQ();
		bookingRQ.setAgent(new BookingRQ.Agent());
		bookingRQ.getAgent().setName(jayrideInterface.getJayrideAPIProperties().getAgentName());
		bookingRQ.getAgent().setEmail(jayrideInterface.getJayrideAPIProperties().getAgentSupportEmail());
		bookingRQ.getAgent().setPhone(jayrideInterface.getJayrideAPIProperties().getAgentSupportPhone());
		Traveller traveller = bookRQ.getTravellers().get(0);
		bookingRQ.setPassenger(new BookingRQ.Passenger());
		bookingRQ.getPassenger().setName(traveller.getGivenName() + " " + traveller.getSurname());
		if ( subclient != null && subclient.length() > 0 )
			bookingRQ.getPassenger().setName(bookingRQ.getPassenger().getName() + " #" + subclient);
		if (traveller.getEmail() == null || traveller.getEmail().length() == 0)
			bookingRQ.getPassenger().setEmail(bookRQ.getBooker().getEmail());
		else
			bookingRQ.getPassenger().setEmail(traveller.getEmail());
		if (traveller.getTelephone() == null || traveller.getTelephone().length() == 0)
			bookingRQ.getPassenger().setMobile(bookRQ.getBooker().getTelephone());
		else
			bookingRQ.getPassenger().setMobile(traveller.getTelephone());
		if (bookRQ.getItems().size() > 1)
		{
			throw new Exception("Jayride only supports single item bookings");
		}
		for (TransferRequestItem requestItem : bookRQ.getItems())
		{
			String bookingCode = requestItem.getBookingCode();
			if (bookingCode.startsWith(JayrideService.CHANNEL_PREFIX))
				bookingCode = bookingCode.substring(JayrideService.CHANNEL_PREFIX.length());
			String[] quoteIDs = bookingCode.split("/");
			bookingRQ.setQuote_id(quoteIDs[0]);
			bookingRQ.setQuote_request_id(quoteIDs[1]);
			if (requestItem.getArrivalFlight() != null && requestItem.getArrivalFlight().getFlightNumber() != null)
			{
				if (bookingRQ.getFlight() == null)
					bookingRQ.setFlight(new Flight());
				bookingRQ.getFlight().setLanding_flight_number(requestItem.getArrivalFlight().getFlightNumber());
			}
			if (requestItem.getDepartureFlight() != null && requestItem.getDepartureFlight().getFlightNumber() != null)
			{
				if (bookingRQ.getFlight() == null)
					bookingRQ.setFlight(new Flight());
				bookingRQ.getFlight().setDeparture_flight_number(requestItem.getDepartureFlight().getFlightNumber());
			}
			if (requestItem.getLuggageSpecialRequests() != null)
			{
				StringBuffer buf = new StringBuffer();
				for (SpecialRequest s : requestItem.getLuggageSpecialRequests())
				{
					if (buf.length() > 0)
						buf.append(", ");
					buf.append(s.getValue());
				}
				bookingRQ.setLuggage(new Luggage());
				bookingRQ.getLuggage().setExtra_items(buf.toString());
			}
			if (requestItem.getSpecialRequests() != null)
			{
				StringBuffer buf = new StringBuffer();
				for (SpecialRequest s : requestItem.getSpecialRequests())
				{
					if (buf.length() > 0)
						buf.append(", ");
					buf.append(s.getValue());
				}
				bookingRQ.setAdditional_notes(buf.toString());
			}
		}
		try
		{
			long timer1 = System.currentTimeMillis();
			BookingRS bookingRS = jayrideInterface.book(bookingRQ);
			long totalTime1 = (System.currentTimeMillis() - timer1);
			log.info("book::time in jayride booking was " + totalTime1 + " millis");
			TransferBookRS bookRS = mapBooking(bookingRS, bookRQ);
			return bookRS;
		}
		catch (Exception e)
		{
			log.error("book::threw exception " + e.toString(), e);
			if (e.getMessage().contains("Error in Jayride Service"))
				throw new Exception(e.getMessage());
			else
				throw new Exception("Error in Jayride Service:" + e.getMessage());
		}
	}

	@Override
	public TransferCancelRS cancel(String client, String subclient, TransferCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::received " + cancelRQ);

		try
		{
			JayrideInterface jayrideInterface = new JayrideInterface(propertiesDAO, client, CHANNEL);

			long timer = System.currentTimeMillis();
			JayrideCancelRS jayrideCancelRS = jayrideInterface.cancel(cancelRQ.getBookingReference());
			log.info("cancel::time taken = " + (System.currentTimeMillis() - timer));

			CurrencyValue cancellationCharge = new CurrencyValue("USD", BigDecimal.ZERO);
			TransferCancelRS cancelRS = new TransferCancelRS(cancelRQ.getBookingReference(), cancellationCharge);
			return cancelRS;

		}
		catch (Exception e)
		{
			log.error("cancel::threw exception " + e.toString(), e);
			throw new Exception("Error in Jayride Service:" + e.getMessage());
		}
	}

	@Override
	public RetrieveTransferRS retrieve(String client, String subclient, RetrieveTransferRQ retrieveRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("retrieve::received " + retrieveRQ);

		try
		{
			JayrideInterface jayrideInterface = new JayrideInterface(propertiesDAO, client, CHANNEL);

			long timer = System.currentTimeMillis();
			JayrideRetrieveRS JayrideRetrieveRS = jayrideInterface.retrieve(retrieveRQ.getBookingReference());
			log.info("cancel::time taken = " + (System.currentTimeMillis() - timer));

			RetrieveTransferRS retrieveRS = new RetrieveTransferRS();
			switch (JayrideRetrieveRS.getBooking_status())
			{
				case "confirmed":
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.transfers.apidomain.Booking.ItemStatus.BOOKED);
					break;
				case "cancelled-by-passenger":
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.transfers.apidomain.Booking.ItemStatus.CANCELLED);
					break;
				case "cancelled-by-supplier":
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.transfers.apidomain.Booking.ItemStatus.CANCELLED);
					break;
				case "cancelled-by-jayride":
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.transfers.apidomain.Booking.ItemStatus.CANCELLED);
					break;
				default: // UNKNOWN
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.transfers.apidomain.Booking.ItemStatus.FAILED);
					break;
			}
			return retrieveRS;
		}
		catch (Exception e)
		{
			log.error("cancel::threw exception " + e.toString(), e);
			throw new Exception("Error in Jayride Service:" + e.getMessage());
		}
	}

	@Override
	public void initiateRCLoad(String code)
	{
		log.debug("initiateRCLoad::enter");
		try {
			loader.fetchAirports();
		}
		catch (Exception e) {
			log.warn("initiateRCLoad::caught " + e.toString(), e);
		}
	}

	protected CreateQuoteRQ.Location makeLocation(SearchRQDTO.Endpoint endpoint, String client)
	{
		log.debug("makeLocation::enter");
		CreateQuoteRQ.Location location = new CreateQuoteRQ.Location();
		switch (endpoint.getEndpointType())
		{
			case AIRPORT:
				location.setType("airport-terminal");
				String iataCode = getAirportEndpoint(endpoint.getEndpointCode());
				log.debug("makeLocation::endpoint is airport, iata=" + iataCode);
				Optional<AirportData> airportDataOpt = airportRepo.findById(iataCode);
				if (airportDataOpt.isPresent())
				{
					AirportData airportData = airportDataOpt.get();
					String terminal = "Terminal";
					List<AirportTerminalData> terminals = airportTerminalRepo.findByIata(iataCode);
					if (terminals.size() > 1)
					{
						if (endpoint.getEndpointCode().length() > 3)
						{
							String terminalName = endpoint.getEndpointCode().substring(3);
							terminals = airportTerminalRepo.findByIataAndTerminalNameContaining(iataCode, terminalName);
							log.debug("makeLocation::found " + terminals.size() + " airport terminals for " + iataCode + " and terminal " + terminal);
						}
					}
					for (AirportTerminalData airportTerminalData : terminals)
					{
						location.setDescription(airportTerminalData.getFullAirportTerminalName());
						location.setLatitude(airportTerminalData.getLatitude());
						location.setLongitide(airportTerminalData.getLongitude());
						break;
					}
				}
				if (location.getDescription() == null)
				{
					log.warn("makeLocation::no airport terminal found for " + endpoint.getEndpointCode());
				}
				return location;
			case FLIGHT:
				log.warn("makeLocation::have not coded for flight yet");
				return location;
			case HOTEL:
				location.setType("hotel");
				Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(client, endpoint.getEndpointCode());
				if (accommodationRCOpt.isPresent())
				{
					AccommodationRC accommodationRC = accommodationRCOpt.get();
					location.setDescription(accommodationRC.getAccommodationName());
					location.setLatitude(accommodationRC.getAddress().getGeoCoordinates().getLatitude());
					location.setLongitide(accommodationRC.getAddress().getGeoCoordinates().getLongitude());
				}
				if (location.getDescription() == null)
				{
					log.warn("makeLocation::no hotel found for " + endpoint.getEndpointCode());
				}
				return location;
			default:
				return null;
		}
	}

	protected String getAirportEndpoint(String endpoint)
	{
		String iataCode = endpoint.substring(0, 3);
		//if ( iataCode.equals("XNB"))
		//	return "DXB";
		//else
			return endpoint;
	}
	
	protected TransferBookRS mapBooking(BookingRS bookingRS, TransferBookRQ bookRQ)
	{
		TransferBookRS bookRS = new TransferBookRS();
		if (bookingRS.getStatus() != null && bookingRS.getStatus().equals("error"))
		{
			ResponseExtraInformation info = new ResponseExtraInformation("error", bookingRS.getMessage());
			bookRS.getErrors().add(info);
		}
		if (bookingRS.getBookings() != null)
		{
			for (Booking bookingResult : bookingRS.getBookings())
			{
				ResponseItem responseItem = new ResponseItem();
				responseItem.setChannel(CHANNEL);
				for (TransferRequestItem sourceItem : bookRQ.getItems())
				{
					responseItem.setInternalItemReference(sourceItem.getInternalItemReference());
				}
				responseItem.setBookingItemReference(bookingResult.getBooking_id());
				StringBuffer comment = new StringBuffer();
				if (bookingResult.getService_info() != null && bookingResult.getService_info().getSupplier() != null)
					comment.append("By: " + bookingResult.getService_info().getSupplier().getName() + ", telephone " + bookingResult.getService_info().getSupplier().getPhone() + ", email:"
							+ bookingResult.getService_info().getSupplier().getEmail() + ".  ");
				if ( bookingResult.getInstructions() != null && bookingResult.getInstructions().getMeetingInstructions() != null && bookingResult.getInstructions().getMeetingInstructions().length() > 0 )
				{
					comment.append("Meeting Instructions:" + bookingResult.getInstructions().getMeetingInstructions());
				}
				for (TransferRequestItem requestItem : bookRQ.getItems())
				{
					if (requestItem.getDepartureFlight() != null && requestItem.getDepartureFlight().getFlightNumber() != null)
					{
						comment.append(bookingResult.getService_info().getSupplier().getName() + " will confirm the best pick-up time for you based on your flight details, you may contact them directly to request a time based on your specific requirements. ");
					}
					comment.append("Reservation is for " + requestItem.getTravellerIndex().size() + " passengers.  Passenger contact information: Email: " + bookRQ.getBooker().getEmail() + ", Tel: " + bookRQ.getBooker().getTelephone());
				}

				responseItem.setComments(comment.toString());
				switch (bookingResult.getBooking_status())
				{
					case "confirmed":
						responseItem.setItemStatus(com.torkirion.eroam.microservice.transfers.apidomain.Booking.ItemStatus.BOOKED);
						break;
					default: // UNKNOWN
						responseItem.setItemStatus(com.torkirion.eroam.microservice.transfers.apidomain.Booking.ItemStatus.FAILED);
						break;
				}
				bookRS.getItems().add(responseItem);
				bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
				bookRS.setBookingReference(bookingResult.getBooking_id());
			}
		}

		return bookRS;
	}

	private static DateTimeFormatter df_YYYmmddhhmm = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("URL endpoint of Jayride API", "jayrideURL", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The Jayride APIKey", "apikey", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("If bookings should be 'faked' and NOT sent to the server, just return a dummy confirmation", "bypassBooking", FieldType.STRING, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The agency name, to appear on the voucher", "agentName", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The support email for this agency", "agentSupportEmail", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The support telephone number for this agency", "agentSupportPhone", FieldType.STRING, true, " "));
		return channelType;
	}
}
