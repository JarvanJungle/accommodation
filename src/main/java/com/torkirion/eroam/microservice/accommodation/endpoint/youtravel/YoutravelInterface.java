package com.torkirion.eroam.microservice.accommodation.endpoint.youtravel;

import com.hotelbeds.schemas.messages.*;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ.AccommodationRequestItem;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ.SpecialRequest;
import com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationChannelService;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.youtravel.schemas.messages.*;
import com.youtravel.schemas.messages.RateHotelResponse;
import com.youtravel.schemas.messages.RatePromotion;
import com.youtravel.schemas.messages.RatePromotions;
import com.youtravel.schemas.messages.RatesHotelResponse;
import com.youtravel.schemas.messages.RoomHotelResponse;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class YoutravelInterface
{
	public static final String API_VERSION = "1.0";

	private SystemPropertiesDAO properties;

	private YoutravelAPIProperties youtravelAPIProperties;

	public YoutravelAPIProperties getHotelbedsPropertiesx()
	{
		return youtravelAPIProperties;
	}

	public YoutravelInterface(SystemPropertiesDAO properties, String site, String channel) throws Exception
	{
		this.properties = properties;
		init(site, channel);
	}

	private void init(String site, String channel) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("init::entering with site " + site + ", channel " + channel + ", properties=" + properties);

		youtravelAPIProperties = new YoutravelAPIProperties(properties, site);

		jaxbContext = JAXBContext.newInstance("com.hotelbeds.schemas.messages");
	}

	private static final BigDecimal HB_MARKUP = new BigDecimal("1.1363636");

	public static BigDecimal applyInventoryMarkup(BigDecimal nett, BigDecimal gross) throws Exception
	{
		return nett.multiply(HB_MARKUP).setScale(0, RoundingMode.UP);
	}

	public AvailabilityRS startSearchHotels(AvailSearchByHotelIdRQDTO availSearchRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("startSearchHotels::entering for availSearchRQ=" + availSearchRQ);

		AvailabilityRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);

		availabilityRQ.setHotels(new HotelSelector());
		for (String hotelId : availSearchRQ.getHotelIds())
		{
			if (!YoutravelService.CHANNEL.equals(AccommodationChannelService.getChannelForHotelId(hotelId)))
			{
				continue;
			}
			if (hotelId.startsWith(YoutravelService.CHANNEL_PREFIX))
				hotelId = hotelId.substring(YoutravelService.CHANNEL_PREFIX.length());
			availabilityRQ.getHotels().getHotel().add(hotelId);
		}

		return searchHotels(availabilityRQ);
	}

	public AvailabilityRS startSearchHotels(AvailSearchByGeocordBoxRQDTO availSearchRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("startSearchHotels::entering for availSearchRQ=" + availSearchRQ);

		AvailabilityRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);

		availabilityRQ.setGeolocation(new GeolocationSelector());
		availabilityRQ.getGeolocation().setLatitude(availSearchRQ.getNorthwest().getLatitude().floatValue());
		availabilityRQ.getGeolocation().setLongitude(availSearchRQ.getNorthwest().getLongitude().floatValue());
		availabilityRQ.getGeolocation().setSecondaryLatitude(availSearchRQ.getSoutheast().getLatitude().floatValue());
		availabilityRQ.getGeolocation().setSecondaryLongitude(availSearchRQ.getSoutheast().getLongitude().floatValue());

		return searchHotels(availabilityRQ);
	}

	private AvailabilityRQ makeAvailabilityRQ(AvailSearchRQDTO availSearchRQ)
	{
		if (log.isDebugEnabled())
			log.debug("makeAvailabilityRQ::entering for availSearchRQ=" + availSearchRQ);

		AvailabilityRQ availabilityRQ = new AvailabilityRQ();

		availabilityRQ.setStay(new StaySelector());
		availabilityRQ.getStay().setCheckIn(df2YYYYMMDD.format(availSearchRQ.getCheckin()));
		availabilityRQ.getStay().setCheckOut(df2YYYYMMDD.format(availSearchRQ.getCheckout()));

		availabilityRQ.setOccupancies(new OccupanciesSelector());
		availabilityRQ.setSourceMarket(youtravelAPIProperties.sourceMarket);
		int roomNumber = 1;
		for (TravellerMix travellerSet : availSearchRQ.getTravellers())
		{
			OccupancySelector occupancySelector = new OccupancySelector();
			availabilityRQ.getOccupancies().getOccupancy().add(occupancySelector);
			occupancySelector.setRooms(BigInteger.ONE);
			occupancySelector.setAdults(BigInteger.valueOf(travellerSet.getAdultCount()));
			if (travellerSet.getChildAges() != null)
				occupancySelector.setChildren(BigInteger.valueOf(travellerSet.getChildAges().size()));
			else
				occupancySelector.setChildren(BigInteger.ZERO);
			PaxesSelector paxesSelector = new PaxesSelector();
			occupancySelector.setPaxes(paxesSelector);
			for (int i = 0; i < travellerSet.getAdultCount(); i++)
			{
				PaxSelector pax = new PaxSelector();
				paxesSelector.getPax().add(pax);
				pax.setType("AD");
			}
			for (Integer childAge : travellerSet.getChildAges())
			{
				PaxSelector pax = new PaxSelector();
				paxesSelector.getPax().add(pax);
				pax.setType("CH");
				pax.setAge(BigInteger.valueOf(childAge));
			}
		}

		return availabilityRQ;
	}

	private AvailabilityRS searchHotels(AvailabilityRQ availabilityRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("searchHotels::entering for availabilityRQ=" + availabilityRQ);
		JAXBContext jax = JAXBContext.newInstance("com.hotelbeds.schemas.messages");
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		StringWriter sw = new StringWriter();
		marshaller.marshal(availabilityRQ, sw);
		String requestString = sw.toString();

		if (log.isDebugEnabled())
			log.debug("searchHotels::requestString = " + requestString);

		long searchStartTime = System.currentTimeMillis();

		String responseString = doCallPost("hotel-api", "hotels", requestString);
		if (log.isDebugEnabled())
			log.debug("searchHotels::responseString = " + responseString);

		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
			Object responseObject = unmarshaller.unmarshal(bin);
			if (log.isDebugEnabled())
				log.debug("searchHotels::responseObject = " + responseObject);

			if (log.isDebugEnabled())
				log.debug("searchHotels::time taken = " + (System.currentTimeMillis() - searchStartTime));
			if (responseObject instanceof AvailabilityRS)
			{
				return (AvailabilityRS) responseObject;
			}
			else
			{
				log.error("searchHotels::bad responseString : " + responseString + " for " + requestString);
				return null;
			}
		}
		catch (Exception e)
		{
			log.error("searchHotels::caught exception " + e.toString(), e);
			return null;
		}
	}
	private Object unMarshal(String response, Unmarshaller unm) throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(response.getBytes());
		Object responseObject = unm.unmarshal(bin);
		return responseObject;
	}
	public CheckRateRS checkRates(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		int adultCount = rateCheckRQDTO.getTravellers().stream().mapToInt(TravellerMix::getAdultCount).sum();
		jaxbContext = JAXBContext.newInstance(RateCheck.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Map<String, String> parameters = new HashMap<>();
		long daysBetween = Duration.between(rateCheckRQDTO.getCheckout(), rateCheckRQDTO.getCheckin()).toDays();
		parameters.put("Username", properties.getProperty("eroam", "YOUTRAVEL", "apiKey"));
		parameters.put("Password", properties.getProperty("eroam", "YOUTRAVEL", "secret"));
		parameters.put("Checkin_Date", rateCheckRQDTO.getCheckin().toString());
		parameters.put("Nights", String.valueOf(daysBetween));
		parameters.put("Currency", "USD");
		parameters.put("LangID", "EN");
		parameters.put("Rooms", "1");
		parameters.put("CanxPol", "1");
		parameters.put("SBT", "1");
		parameters.put("SRR", "1");
		parameters.put("SBC", "1");
		parameters.put("HID", rateCheckRQDTO.getHotelId());
		parameters.put("ADLTS_1", String.valueOf(adultCount));
		HttpService httpService = new YoutravelHttpService(youtravelAPIProperties);
		String response = httpService.doCallGet("index.asp", parameters);
		RateCheck rateCheck = (RateCheck) unMarshal(response, unmarshaller);
//		rateCheck.getSession().getHotelRateCheck().get
		String language = rateCheck.getSession().getHotelRateCheck().ge
		CheckRateRQ checkRatesRQ = new CheckRateRQ();
		checkRatesRQ.setLanguage("ENG");
		checkRatesRQ.setUpselling(false);
		CheckRateRS checkRateRS = new CheckRateRS();
		HotelResponse hotelResponse = new HotelResponse();
		hotelResponse.setCheckIn(rateCheckRQDTO.getCheckin().toString());
		hotelResponse.setCheckOut(rateCheckRQDTO.getCheckout().toString());
		hotelResponse.setCode(rateCheckRQDTO.getHotelId());
		hotelResponse.setCurrency(rateCheck.getSession().getCurrency());
		hotelResponse.setName(rateCheck.getSession().getHotelRateCheck().getName());
		HotelRateCheck hotelRateCheck = rateCheck.getSession().getHotelRateCheck();
		List<RoomElement> roomElementList = rateCheck.getSession().getHotelRateCheck().getRoomRateCheck1().getRoomElement();
		roomElementList.forEach(e->{
			RoomHotelResponse roomHotelResponse = new RoomHotelResponse();
			RatesHotelResponse ratesHotelResponse = new RatesHotelResponse();
			RateHotelResponse rateHotelResponse = new RateHotelResponse();
			rateHotelResponse.setAdults(BigInteger.valueOf(adultCount));
//			rateHotelResponse.setChildren();
			rateHotelResponse.setBoardCode(hotelRateCheck.getBoardType());
			rateHotelResponse.setChildrenAges(hotelRateCheck.getChildAge());
			rateHotelResponse.setNet(new BigDecimal( e.getRate().getFinalRate()));
			RatePromotion freeStay = new RatePromotion();
			freeStay.setCode("freeStay");
			freeStay.setName("freeStay");
			freeStay.setRemark("freeStay");
			RatePromotion freeTransfer = new RatePromotion();
			freeStay.setCode("freeTransfer");
			freeStay.setName("freeTransfer");
			freeStay.setRemark("freeTransfer");
			List<RatePromotion> ratePromotionList = new ArrayList<>();
			ratePromotionList.add(freeStay);
			ratePromotionList.add(freeStay);
			RatePromotions ratePromotions = new RatePromotions(ratePromotionList);
			rateHotelResponse.setPromotions(ratePromotions);
			roomHotelResponse.setRates(ratesHotelResponse);
		});

		List<RoomsHotelResponse> roomsHotelResponseList = new ArrayList<>();
		RoomsHotelResponse roomsHotelResponse = new RoomsHotelResponse();

		List<RoomHotelResponse> roomHotelResponseList = new ArrayList<>();
		RoomHotelResponse roomHotelResponse = new RoomHotelResponse();
		roomHotelResponse.getRates().get
		roomHotelResponseList.ge

		RoomsHotelResponse roomsHotelResponse = new RoomsHotelResponse();
		roomsHotelResponse.getRoom().get(0).getRates()
		RatesHotelResponse ratesHotelResponse = new RatesHotelResponse();
		ratesHotelResponse.getRate().get(0).g
		roomsHotelResponse.getRoom().get(0).set
		hotelResponse.setRooms(rateCheckRQDTO.getHotelId());
		checkRateRS.setHotel();
		checkRatesRQ.get().







		if (log.isDebugEnabled())
			log.debug("checkRates::entering for " + ratekeys.size() + " ratekeys:" + ratekeys);

		CheckRateRQ checkRatesRQ = new CheckRateRQ();
		checkRatesRQ.
		checkRatesRQ.setUpselling(false);
		checkRatesRQ.setLanguage("ENG");
		checkRatesRQ.setUpselling(false);
		//checkRatesRQ.setRooms(new RoomsCheckrateRequest());
		for (String ratekey : ratekeys)
		{
			RoomsCheckrateRequest roomsCheckrateRequest = new RoomsCheckrateRequest();
			roomsCheckrateRequest.setRoom(new RoomCheckrateRequest());
			roomsCheckrateRequest.getRoom().setRateKey(ratekey);;
			checkRatesRQ.getRooms().add(roomsCheckrateRequest);
		}

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		String responseString = "";
		Object responseObject = null;
		if (ratekeys.size() >= 1)
		{


			StringWriter sw = new StringWriter();
			marshaller.marshal(checkRatesRQ, sw);
			String requestString = sw.toString();


			if (log.isDebugEnabled())
				log.debug("checkRates::requestString = " + requestString);
			responseString = doCallPost("hotel-api", "checkrates", requestString);

			if (responseString != null && responseString.length() > 0)
			{
				if (log.isDebugEnabled())
					log.debug("checkRates::responseString = " + responseString);
				ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
				try
				{
					responseObject = unmarshaller.unmarshal(bin);
					if (log.isDebugEnabled())
						log.debug("checkRates::responseObject = " + responseObject);
				}
				catch (Exception e)
				{
					log.warn("checkRates::caught POST error " + e.toString());
				}
			}
		}

		if (responseObject instanceof CheckRateRS)
		{
			return (CheckRateRS) responseObject;
		}
		else
		{
			log.error("checkRates::bad responseString : " + responseString + " for " + ratekeys);
			return null;
		}
	}

	private static final boolean fakeItForHotelBeds = false;
	public AccommodationBookRS book(AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::entering");
		String productNameForError = null;
		LocalDate productDateForError = null;

		if (bookRQ.getItems().size() == 1)
		{
			// SINGLE ROOM
			AccommodationBookRS accommodationBookRS = bookSingle(bookRQ);
			return accommodationBookRS;
		}

		if (bookRQ.getItems().size() > 1 && fakeItForHotelBeds)
		{
			if (log.isDebugEnabled())
				log.debug("book::multi-room!  Do it 'properly' for Hotelbeds, even through multi-room is broken for them");
			// MULTI ROOM
			AccommodationBookRS accommodationBookRS = bookSingle(bookRQ);
			return accommodationBookRS;
		}

		if (log.isDebugEnabled())
			log.debug("book::multi-room!  Break the calls into individuals!");

		List<Traveller> travellers = new ArrayList<Traveller>();
		travellers.addAll(bookRQ.getTravellers());

		Set<AccommodationRequestItem> items = new HashSet<AccommodationRequestItem>();
		items.addAll(bookRQ.getItems());

		AccommodationBookRS bookRS = null;

		int passCount = 0;
		for (AccommodationRequestItem item : items)
		{
			bookRQ.getTravellers().clear();
			bookRQ.getItems().clear();

			bookRQ.getItems().add(item);
			Set<Integer> travellerIndex = new HashSet<>(item.getTravellerIndex());
			item.getTravellerIndex().clear();
			int addCount = 0;
			for (Integer tIndex : travellerIndex)
			{
				bookRQ.getTravellers().add(travellers.get(tIndex));
				item.getTravellerIndex().add(addCount++);
			}
			try
			{
				if (log.isDebugEnabled())
					log.debug("book::calling multiBook for item " + passCount++ + " : " + item.toString());
				AccommodationBookRS brs = bookSingle(bookRQ);
				if (bookRS == null)
					bookRS = brs;
				else
				{
					bookRS.getItems().addAll(brs.getItems());
					bookRS.getWarnings().addAll(brs.getWarnings());
					bookRS.getErrors().addAll(brs.getErrors());
					bookRS.setBookingReference(bookRS.getBookingReference() + ";" + brs.getBookingReference());
				}
			}
			catch (Exception e)
			{
				if (log.isDebugEnabled())
					log.debug("book::multi-room!  Caught exception " + e.toString(), e);
				if (bookRS == null)
					bookRS = new AccommodationBookRS();
				AccommodationBookRS.ResponseItem brs = new AccommodationBookRS.ResponseItem();
				brs.setChannel(YoutravelService.CHANNEL);
				brs.setItemStatus(ItemStatus.FAILED);
				brs.setInternalItemReference(item.getInternalItemReference());
				bookRS.getItems().add(brs);
				bookRS.getErrors().add(new ResponseExtraInformation("500", "System error"));
			}
		}

		if (log.isDebugEnabled())
			log.debug("book::returning multiBook " + bookRS);
		return bookRS;
	}

	protected AccommodationBookRS bookSingle(AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("bookSingle::entering with " + bookRQ);
		String productNameForError = null;
		LocalDate productDateForError = null;

		AccommodationBookRS response = new AccommodationBookRS();
		response.setInternalBookingReference(bookRQ.getInternalBookingReference());

		Date today = Functions.normaliseDate(new Date());

		try
		{
			BookingRQ hbBookingRQ = new BookingRQ();

			hbBookingRQ.setHolder(new Holder());
			hbBookingRQ.getHolder().setName(bookRQ.getBooker().getGivenName());
			hbBookingRQ.getHolder().setSurname(bookRQ.getBooker().getSurname());

			hbBookingRQ.setRooms(new RoomsBookingRequest());

			BigInteger roomNumber = BigInteger.ZERO;
			for (AccommodationRequestItem item : bookRQ.getItems())
			{
				roomNumber = roomNumber.add(BigInteger.ONE);
				if (log.isDebugEnabled())
					log.debug("book::processing roomNumber " + roomNumber);
				AccommodationBookRS.ResponseItem itemRS = new AccommodationBookRS.ResponseItem();
				itemRS.setInternalItemReference(item.getInternalItemReference());
				response.getItems().add(itemRS);
				hbBookingRQ.setClientReference(bookRQ.getInternalBookingReference());

				productDateForError = item.getCheckin();
				RoomBookingRequest roomBookingRequest = new RoomBookingRequest();
				hbBookingRQ.getRooms().getRoom().add(roomBookingRequest);

				if (item.getSpecialRequests() != null)
				{
					for (SpecialRequest specialRequest : item.getSpecialRequests())
					{
						hbBookingRQ.setRemark(specialRequest.getValue());
					}
				}

				roomBookingRequest.setRateKey(item.getBookingCode());
				TravellerMix travellers = new TravellerMix();
				if (log.isDebugEnabled())
					log.debug("book::processing room pax travellers=" + travellers.toString());
				if (log.isDebugEnabled())
					log.debug("book::processing room pax travellers.adults=" + travellers.getAdultCount());
				roomBookingRequest.setPaxes(new PaxesBooking());

				if (bookRQ.getItems().size() == 1)
				{
					// HB barfs on multiroom pax
					for (Integer travellerIndex : item.getTravellerIndex())
					{
						Traveller traveller = bookRQ.getTravellers().get(travellerIndex);
						PaxBooking pax = new PaxBooking();
						roomBookingRequest.getPaxes().getPax().add(pax);
						pax.setRoomId(roomNumber);
						pax.setName(traveller.getGivenName());
						pax.setSurname(traveller.getSurname());
						int age = traveller.getAge(today);
						if (age >= 18)
						{
							pax.setType("AD");
						}
						else
						{
							pax.setType("CH");
							pax.setAge(BigInteger.valueOf(age));
						}
					}
				}
			}

			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			StringWriter sw = new StringWriter();
			marshaller.marshal(hbBookingRQ, sw);
			String requestString = sw.toString();
			if (log.isDebugEnabled())
				log.debug("bookHotels::requestString = " + requestString);

			if (youtravelAPIProperties.bypassBooking)
			{
				log.warn("book::bypassBooking is true");
				log.warn("book::requestString=" + requestString);
				int randomInt = (int) (Math.random() * 10000.0);
				String randomHotelValue = Integer.toString(randomInt);
				int roomValue = 1;
				response.setBookingReference("HB" + randomHotelValue);
				for (AccommodationBookRS.ResponseItem item : response.getItems())
				{
					randomInt = (int) (Math.random() * 10000.0);
					item.setBookingItemReference("HBH" + randomHotelValue + "_" + roomValue++);
					item.setChannel(YoutravelService.CHANNEL);
					item.setItemStatus(ItemStatus.BOOKED);
					String hbVoucherInfo = MessageFormat.format(HB_VOUCHER_INFO, "TRAVELCUBE PACIFIC PTY. LTD", "52099122577", item.getBookingItemReference());
					item.setItemRemark(hbVoucherInfo);
					if (log.isDebugEnabled())
						log.debug("bookHotels::eRoam does not show itemRemarks - copy to main as well");
					if (!response.getRemarks().contains(hbVoucherInfo))
						response.getRemarks().add(hbVoucherInfo);
				}
				return response;
			}

			String responseString = doCallPost("hotel-api", "bookings", requestString);

			if (responseString == null || responseString.length() == 0)
			{
				log.error("book::empty responseString : " + responseString + " for " + requestString);
				String text = "An error has occurred and we are unable to process your request, please do not try again, call us for assistance";
				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1001");
				response.getErrors().add(responseExtraInformation);
				return response;
			}

			ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
			Object responseObject = unmarshaller.unmarshal(bin);
			if (log.isDebugEnabled())
				log.debug("book::responseObject = " + responseObject);

			if (!(responseObject instanceof BookingRS))
			{
				log.error("book::bad response object");
				String text = "A system error has occurred and your booking has not been created.";
				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1002");
				response.getErrors().add(responseExtraInformation);
				return response;
			}

			BookingRS hbResponse = (BookingRS) responseObject;

			if (hbResponse.getError() != null)
			{
				if (hbResponse.getError().getMessage() != null && hbResponse.getError().getMessage().contains("Invalid data. rateKey does not exist or expired"))
				{
					String text = "While trying to finalise your booking , this rate has expired.   You may try the room type again, but the rate will only remain valid for 30 minutes.";
					ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1003");
					response.getErrors().add(responseExtraInformation);
					return response;
				}
				if (hbResponse.getError().getMessage() != null && hbResponse.getError().getMessage().contains("Booking confirmation error: 164"))
				{
					// hideHotel(productIDForError);
					String text = "While trying to finalise your booking, this room type has become unavailable.   Please try a different room type to complete your booking.";
					ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1004");
					response.getErrors().add(responseExtraInformation);
					return response;
				}
				if (hbResponse.getError().getMessage() != null && hbResponse.getError().getMessage().contains("There are stop sales on the dates indicated"))
				{
					// hideHotel(productIDForError, conn);
					String text = "While trying to finalise your booking, this room type has become unavailable due to a stop sell being applied by the hotel.   Please try a different room type to complete your booking.";
					ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1005");
					response.getErrors().add(responseExtraInformation);
					return response;
				}
				String text = "A hotelbeds error has occurred and your booking has not been created: '" + hbResponse.getError().getMessage() + "'";
				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1006");
				response.getErrors().add(responseExtraInformation);
				return response;
			}
			if (hbResponse.getBooking() == null || hbResponse.getBooking().getStatus() == null || !hbResponse.getBooking().getStatus().equals("CONFIRMED") || hbResponse.getBooking().getHotel() == null
					|| hbResponse.getBooking().getHotel().getRooms() == null || hbResponse.getBooking().getHotel().getRooms().getRoom() == null)
			{
				String text = "A system error has occurred and your booking has not been created. ";
				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1007");
				response.getErrors().add(responseExtraInformation);
				return response;
			}

			for (RoomBookingResponse roomBookingResponse : hbResponse.getBooking().getHotel().getRooms().getRoom())
			{
				if (!roomBookingResponse.getStatus().equals("CONFIRMED"))
				{
					log.error("book::bad response value '" + roomBookingResponse.getStatus() + "' in booking " + roomBookingResponse + " in request " + bookRQ.toString());
					try
					{
						AccommodationCancelRQ cancelRQ = new AccommodationCancelRQ();
						cancelRQ.setBookingReference(hbResponse.getBooking().getReference());
						AccommodationCancelRS cancelRS = cancel(cancelRQ);
						log.error("book::cancelRS : " + cancelRS);
					}
					catch (Exception e)
					{
						log.error("book::error on hard rollback cancel");
					}
					String text = MessageFormat.format("A system error has occurred and your booking for {0} on {1, date, medium} has not been created. ", productNameForError, productDateForError);
					ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
					response.getErrors().add(responseExtraInformation);
					return response;
				}
			}
			// TODO multi-room
			response.setBookingReference(hbResponse.getBooking().getReference());
			if ( response.getItems().size() != hbResponse.getBooking().getHotel().getRooms().getRoom().size())
			{
				log.error("book::incorrect number of rooms returned " + hbResponse + " in request " + bookRQ.toString());
				try
				{
					AccommodationCancelRQ cancelRQ = new AccommodationCancelRQ();
					cancelRQ.setBookingReference(hbResponse.getBooking().getReference());
					AccommodationCancelRS cancelRS = cancel(cancelRQ);
					log.error("book::cancelRS : " + cancelRS);
				}
				catch (Exception e)
				{
					log.error("book::error on hard rollback cancel");
				}
				String text = MessageFormat.format("A system error has occurred and your booking for {0} on {1, date, medium} has not been created. ", productNameForError, productDateForError);
				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
				response.getErrors().add(responseExtraInformation);
				return response;
			}
			for ( int i = 0; i < response.getItems().size(); i ++)
			{
				if (log.isDebugEnabled())
					log.debug("book::processing response room " + i);
				AccommodationBookRS.ResponseItem item = response.getItems().get(i);
				RoomBookingResponse roomBookingResponse = hbResponse.getBooking().getHotel().getRooms().getRoom().get(i);
				for (RateHotelResponse rateHotelResponse : roomBookingResponse.getRates().getRate())
				{
					if (log.isDebugEnabled())
						log.debug("book::processing roomBookingResponse " + roomBookingResponse.toString() + " rateHotelResponse " + rateHotelResponse.toString());
					String hbVoucherInfo = MessageFormat.format(HB_VOUCHER_INFO, hbResponse.getBooking().getInvoiceCompany().getName(),
							hbResponse.getBooking().getInvoiceCompany().getRegistrationNumber(), hbResponse.getBooking().getReference());
					BigDecimal linePrice = rateHotelResponse.getNet();
					String confirmationRef = hbResponse.getBooking().getReference();
					item.setBookingItemReference(confirmationRef);
					item.setItemStatus(ItemStatus.BOOKED);
					item.setChannel(YoutravelService.CHANNEL);
					item.setItemRemark(roomBookingResponse.getName() + ": " + hbVoucherInfo);
					if (log.isDebugEnabled())
						log.debug("bookHotels::eRoam does not show itemRemarks - copy to main as well");
					if (!response.getRemarks().contains(hbVoucherInfo))
						response.getRemarks().add(hbVoucherInfo);
					if (rateHotelResponse.getRateComments() != null && rateHotelResponse.getRateComments().length() > 0)
						response.getRemarks().add(roomBookingResponse.getName() + ": " + rateHotelResponse.getRateComments());
					if (log.isDebugEnabled())
						log.debug("book::setting itinItem " + item.getBookingItemReference() + " to " + confirmationRef + ", price was " + linePrice);
					break;
				}
			}
			return response;
		}
		catch (Exception e)
		{
			log.error("book::caught " + e.toString(), e);
			String text = MessageFormat.format("A system error has occurred and your booking for {0} on {1, date, medium} has not been created. ", productNameForError, productDateForError);
			ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
			response.getErrors().add(responseExtraInformation);
			return response;
		}
	}

	private static final String cancellationFlag = "CANCELLATION";

	// private static final String cancellationFlag = "SIMULATION";
	public AccommodationCancelRS cancel(AccommodationCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::entering");

		CurrencyValue cancellationCharge = new CurrencyValue("AUD", BigDecimal.ZERO);

		if (youtravelAPIProperties.bypassBooking)
		{
			log.warn("cancel::bypassBooking is true");
			AccommodationCancelRS cancellation = new AccommodationCancelRS("HB123", cancellationCharge);
			return cancellation;
		}

		Map<String, String> deleteParameters = new HashMap<>();
		deleteParameters.put("cancellationFlag", cancellationFlag);
		String responseString = doCallDelete("hotel-api", "bookings/" + cancelRQ.getBookingReference(), deleteParameters);
		if (log.isDebugEnabled())
			log.debug("cancel::responseString = " + responseString);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
		Object responseObject = unmarshaller.unmarshal(bin);
		if (log.isDebugEnabled())
			log.debug("cancel::responseObject = " + responseObject);

		if (!(responseObject instanceof BookingCancellationRS))
		{
			String text = "A system error has occurred and the cancellation failed";
			ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
			AccommodationCancelRS cancellation = new AccommodationCancelRS("", cancellationCharge);
			cancellation.getErrors().add(responseExtraInformation);
			return cancellation;
		}

		BookingCancellationRS hbResponse = (BookingCancellationRS) responseObject;
		if (hbResponse.getBooking() == null || hbResponse.getError() != null)
		{
			String text = "A system error has occurred and the cancellation failed";
			ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
			AccommodationCancelRS cancellation = new AccommodationCancelRS("", cancellationCharge);
			cancellation.getErrors().add(responseExtraInformation);
			return cancellation;
		}
		if (hbResponse.getBooking().getHotel().getCancellationAmount() != null)
			cancellationCharge = new CurrencyValue(hbResponse.getBooking().getHotel().getCurrency(), hbResponse.getBooking().getHotel().getCancellationAmount());
		AccommodationCancelRS cancellation = new AccommodationCancelRS(hbResponse.getBooking().getCancellationReference(), cancellationCharge);
		if (log.isDebugEnabled())
			log.debug("cancel::returning = " + cancellation);
		return cancellation;
	}

	public AccommodationRetrieveRS retrieve(AccommodationRetrieveRQ retrieveRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("retrieve::entering");

		if (youtravelAPIProperties.bypassBooking)
		{
			log.warn("retrieve::bypassBooking is true");
			AccommodationRetrieveRS retrieveRS = new AccommodationRetrieveRS();
			retrieveRS.setItemStatus(ItemStatus.BOOKED);
			return retrieveRS;
		}

		String responseString = doCallGet("hotel-api", "bookings/" + retrieveRQ.getBookingReference(), null);

		if (log.isDebugEnabled())
			log.debug("retrieve::responseString = " + responseString);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
		Object responseObject = unmarshaller.unmarshal(bin);
		if (log.isDebugEnabled())
			log.debug("retrieve::responseObject = " + responseObject);

		if (!(responseObject instanceof BookingDetailRS))
		{
			String text = "A system error has occurred and the cancellation failed";
			ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
			AccommodationRetrieveRS retrieval = new AccommodationRetrieveRS();
			retrieval.getErrors().add(responseExtraInformation);
			return retrieval;
		}

		BookingDetailRS hbResponse = (BookingDetailRS) responseObject;
		if (hbResponse.getBooking() == null || hbResponse.getError() != null)
		{
			String text = "A system error has occurred and the cancellation failed";
			ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
			AccommodationRetrieveRS retrieval = new AccommodationRetrieveRS();
			retrieval.getErrors().add(responseExtraInformation);
			return retrieval;
		}
		AccommodationRetrieveRS retrieveRS = new AccommodationRetrieveRS();
		switch (hbResponse.getBooking().getStatus())
		{
			case "CONFIRMED":
				retrieveRS.setItemStatus(ItemStatus.BOOKED);
				break;
			case "CANCELLED":
				retrieveRS.setItemStatus(ItemStatus.CANCELLED);
				break;
			default:
				retrieveRS.setItemStatus(ItemStatus.FAILED);
				break;
		}

		if (log.isDebugEnabled())
			log.debug("retrieval::returning = " + retrieveRS);
		return retrieveRS;
	}

	public String doCallPost(String application, String callType, String requestData)
	{
		if (log.isDebugEnabled())
			log.debug("doCallPost::entering");
		HttpService httpService = new YoutravelHttpService(youtravelAPIProperties);
		String fixedRequestData = fixRequestNamespaces(requestData);
		String response = httpService.doCallPost("/" + application + "/" + API_VERSION + "/" + callType, fixedRequestData);
		return fixResponseNamespaces(response);
	}

	public String doCallGet(String application, String callType, Map<String, String> params)
	{
		if (log.isDebugEnabled())
			log.debug("doCallGet::entering");
		HttpService httpService = new YoutravelHttpService(youtravelAPIProperties);
		String response = httpService.doCallGet("/" + application + "/" + API_VERSION + "/" + callType, params);
		return fixResponseNamespaces(response);
	}

	public String doCallDelete(String application, String callType, Map<String, String> parameters)
	{
		if (log.isDebugEnabled())
			log.debug("doCallDelete::entering");
		HttpService httpService = new YoutravelHttpService(youtravelAPIProperties);
		String response = httpService.doCallDelete("/" + application + "/" + API_VERSION + "/" + callType, parameters);
		return fixResponseNamespaces(response);
	}

	public static String fixRequestNamespaces(String s)
	{
		if (s == null)
			return "";
		s = s.replaceAll("<ns2:bookingRQ xmlns:ns2=\"http://www.hotelbeds.com/schemas/messages\">",
				"<bookingRQ xmlns=\"http://www.hotelbeds.com/schemas/messages\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" platform=\"167\" >");
		s = s.replaceAll("</ns2:bookingRQ>", "</bookingRQ>");
		s = s.replaceAll("<ns2:checkRateRQ upselling=\"false\" language=\"ENG\" xmlns:ns2=\"http://www.hotelbeds.com/schemas/messages\">",
				"<checkRateRQ upselling=\"false\" language=\"ENG\" xmlns=\"http://www.hotelbeds.com/schemas/messages\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" platform=\"167\" >");
		s = s.replaceAll("</ns2:checkRateRQ>", "</checkRateRQ>");
		s = s.replaceAll("xmlns:ns2=\"http://www.hotelbeds.com/schemas/messages\">",
				"xmlns=\"http://www.hotelbeds.com/schemas/messages\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" platform=\"167\" >");
		s = s.replaceAll("<ns2:availabilityRQ ", "<availabilityRQ ");
		s = s.replaceAll("</ns2:availabilityRQ>", "</availabilityRQ>");
		return s;
	}

	public static String fixResponseNamespaces(String s)
	{
		if (s == null)
			return "";
		s = s.replaceAll("xmlns=\"http://www.hotelbeds.com", "xmlns:ns2=\"http://www.hotelbeds.com");
		s = s.replaceAll("countriesRS", "ns2:countriesRS");
		s = s.replaceAll("destinationsRS", "ns2:destinationsRS");
		s = s.replaceAll("accommodationsRS", "ns2:accommodationsRS");
		s = s.replaceAll("boardsRS", "ns2:boardsRS");
		s = s.replaceAll("categoriesRS", "ns2:categoriesRS");
		s = s.replaceAll("chainsRS", "ns2:chainsRS");
		s = s.replaceAll("facilitiesRS", "ns2:facilitiesRS");
		s = s.replaceAll("facilityGroupsRS", "ns2:facilityGroupsRS");
		s = s.replaceAll("facilityTypologiesRS", "ns2:facilityTypologiesRS");
		s = s.replaceAll("issuesRS", "ns2:issuesRS");
		s = s.replaceAll("promotionsRS", "ns2:promotionsRS");
		s = s.replaceAll("segmentsRS", "ns2:segmentsRS");
		s = s.replaceAll("roomsRS", "ns2:roomsRS");
		s = s.replaceAll("terminalsRS", "ns2:terminalsRS");
		s = s.replaceAll("imageTypesRS", "ns2:imageTypesRS");
		s = s.replaceAll("groupCategoriesRS", "ns2:groupCategoriesRS");
		s = s.replaceAll("rateCommentsRS", "ns2:rateCommentsRS");
		s = s.replaceAll("hotelsRS", "ns2:hotelsRS");
		s = s.replaceAll("availabilityRS", "ns2:availabilityRS");
		s = s.replaceAll("checkRateRS", "ns2:checkRateRS");
		s = s.replaceAll("bookingRS", "ns2:bookingRS");
		s = s.replaceAll("bookingCancellationRS", "ns2:bookingCancellationRS");
		s = s.replaceAll("bookingDetailRS", "ns2:bookingDetailRS");
		s = s.replaceAll("<ActivitySearchResponse>",
				"<ns2:ActivitySearchResponse xmlns:ns2=\"http://www.hotelbeds.com/schemas/activitymessages\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" schemaLocation=\"http://www.hotelbeds.com/schemas/activitymessages\">");
		s = s.replaceAll("</ActivitySearchResponse>", "</ns2:ActivitySearchResponse>");
		return s;
	}

	public boolean allowZeroCommissionProduct()
	{
		return youtravelAPIProperties.allowZeroCommissionProduct;
	}
	/*
	 * public static BigDecimal getAgentCommission(Money cost, Money price, ConsumerQuery consumerQuery) throws
	 * DataAccessException { if ( l4jStaticLogger.isDebugEnabled()) l4jStaticLogger.debug("getAgentCommission::enter for cost " +
	 * cost + " price " + price);
	 * 
	 * SortedSet<HotelbedsCommissions> hotelbedsCommissions; if ( consumerQuery != null ) hotelbedsCommissions =
	 * consumerQuery.getHotelbedsCommissions(); else { com.viator.loreto.datastorecache.DAO helper = new
	 * com.viator.loreto.datastorecache.DAO(); hotelbedsCommissions = helper.getHotelbedsCommissions(); }
	 * 
	 * BigDecimal baseMarkup = new BigDecimal((price.getFloatValue() - cost.getFloatValue()) / cost.getFloatValue()); baseMarkup =
	 * baseMarkup.multiply(Functions.BD_100).add(BD_100_05); BigDecimal agentCommission = BigDecimal.ZERO; for (
	 * HotelbedsCommissions commission : hotelbedsCommissions ) { if ( l4jStaticLogger.isDebugEnabled())
	 * l4jStaticLogger.debug("getAgentCommission::comparing base " + baseMarkup + " against " + commission.getTotalMarkup()); if (
	 * baseMarkup.compareTo(commission.getTotalMarkup()) > 0 ) { agentCommission = commission.getAgentCommission(); if (
	 * l4jStaticLogger.isDebugEnabled()) l4jStaticLogger.debug("getAgentCommission::set commission to " + agentCommission); } else
	 * { if ( l4jStaticLogger.isDebugEnabled()) l4jStaticLogger.debug("getAgentCommission::break"); break; } } if (
	 * l4jStaticLogger.isDebugEnabled()) l4jStaticLogger.debug("getAgentCommission::returning " + agentCommission); return
	 * agentCommission; }
	 */

	/*
	 * private final Cache<String, String> getRequestResponseCache() { if (_cache == null) { CachingProvider provider =
	 * Caching.getCachingProvider(); CacheManager cacheManager = provider.getCacheManager(); MutableConfiguration<String, String>
	 * configuration = new MutableConfiguration<String, String>().setTypes(String.class, String.class)
	 * .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf((javax.cache.expiry.Duration.FIVE_MINUTES))); _cache =
	 * cacheManager.createCache("hotelbedsInterfaceRQRS", configuration); } return _cache; }
	 */
	// private static Cache<String, String> _cache = null;

	private static JAXBContext jaxbContext;

	private static DateTimeFormatter df2YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final String HB_VOUCHER_INFO = "Payable through {0}, acting as agent for the service operating company, details of which can be provided upon request. VAT: {1} Reference: {2}";
}
