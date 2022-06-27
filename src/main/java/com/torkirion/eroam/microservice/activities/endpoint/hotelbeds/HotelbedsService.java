package com.torkirion.eroam.microservice.activities.endpoint.hotelbeds;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelbeds.schemas.messages.AvailabilityRS;
import com.hotelbeds.schemas.messages.CancellationPolicy;
import com.hotelbeds.schemas.messages.CheckRateRS;
import com.hotelbeds.schemas.messages.HotelResponse;
import com.hotelbeds.schemas.messages.Offer;
import com.hotelbeds.schemas.messages.RateHotelResponse;
import com.hotelbeds.schemas.messages.RatePromotion;
import com.hotelbeds.schemas.messages.RoomHotelResponse;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityResult;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRS;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityCancelRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityCancelRS;
import com.torkirion.eroam.microservice.activities.apidomain.*;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByActivityIdRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.activities.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class HotelbedsService implements ActivityServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	//private StaticRepo staticRepo;

	//private static HotelbedsCache _hotelbedsCache = null;

	public static final String CHANNEL = "HBACTIVITIES";

	public static final String CHANNEL_PREFIX = "HB";

	public static final Boolean TEST_SAVE = true;

	/*
	public Set<AccommodationResult> searchByHotelId(AvailSearchByHotelIdRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByHotelIdRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		AvailabilityRS availabilityRS = null;
		Set<AccommodationResult> results = new HashSet<>();

		try
		{
			HotelbedsInterface hotelBedsInterface = new HotelbedsInterface(propertiesDAO, availSearchRQ.getClient(), CHANNEL);
			long timer2 = System.currentTimeMillis();
			availabilityRS = hotelBedsInterface.startSearchHotels(availSearchRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("search::time in hotelbeds search was " + totalTime2 + " millis");
			if (availabilityRS == null || availabilityRS.getHotels() == null || availabilityRS.getHotels().getHotel() == null || availabilityRS.getHotels().getHotel().size() == 0)
			{
				log.debug("search::availabilityRS returned no lists");
			}
			else
			{
				int listNo = 0;
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned " + availabilityRS.getHotels().getHotel().size() + " hotels");
				for (HotelResponse hotelResponse : availabilityRS.getHotels().getHotel())
				{
					if (log.isDebugEnabled())
						log.debug("search::processing item " + listNo);
					AccommodationResult accommodationResult = new AccommodationResult();
					String property_id = new String(CHANNEL_PREFIX + hotelResponse.getCode());
					AccommodationProperty property = new AccommodationProperty();
					property.setCode(property_id);
					property.setChannel(HotelbedsService.CHANNEL);
					property.setChannelCode(hotelResponse.getCode());
					// rest of the details are set in the AccommodationSearchService
					accommodationResult.setProperty(property);

					accommodationResult.setRooms(loadRooms(hotelBedsInterface, hotelResponse.getRooms().getRoom(), "AUD", availSearchRQ, property_id));

					if (accommodationResult.getRooms() == null || accommodationResult.getRooms().size() == 0)
					{
						if (log.isDebugEnabled())
							log.debug("search::hotel " + property_id + " not added, has no rooms");
					}
					else
					{
						results.add(accommodationResult);
					}
				}
			}
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	} */

	public Set<ActivityResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("searchByGeocordBox::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);
		long timer1 = System.currentTimeMillis();
		Set<ActivityResult> results = new HashSet<>();
		try
		{
			HotelbedsInterface hotelBedsInterface = new HotelbedsInterface(propertiesDAO, availSearchRQ.getClient());
			long timer2 = System.currentTimeMillis();
			results = hotelBedsInterface.startSearchActivities(availSearchRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("searchByGeocordBox::time in hotelbeds search was " + totalTime2 + " millis");
			log.info("searchByGeocordBox::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
			
			if ( TEST_SAVE )
			{
				if ( results.size() == 0 || results == null)
				{
					ObjectMapper _objectMapper = new ObjectMapper();
					String content = Files.readString(Path.of("activity_save.txt"));
					results = _objectMapper.readValue(content, new TypeReference<Set<ActivityResult>>(){});
					log.warn("searchByGeocordBox::returning dummy saved results");
				}
				else
				{
					StringWriter writer = new StringWriter();
					ObjectMapper _objectMapper = new ObjectMapper();
					_objectMapper.writeValue(writer, results);
					Path savePath = Path.of("activity_save.txt");
					String toSave = writer.toString();
					log.info("searchByGeocordBox::saving to path " + savePath + " : " + toSave);
					Files.writeString(savePath, toSave);
				}
			}
		}
		catch (Exception e)
		{
			log.error("searchByGeocordBox::threw exception " + e.toString(), e);
		}
		return results;
	}

	@Override
	public Collection<ActivityResult> searchByActivityId(AvailSearchByActivityIdRQDTO availSearchRQ)
	{
		log.debug("searchByActivityId::search(searchByActivityId)= {}", availSearchRQ);
		long timer1 = System.currentTimeMillis();
		Set<ActivityResult> results = new HashSet<>();
		Set<String> activityIds = availSearchRQ.getActivityIds();
		Set<String> activityIdsCreated = new HashSet<>();
		for(String activityId : activityIds) {
			activityIdsCreated.add(correctActivityId(activityId));
		}
		availSearchRQ.setActivityIds(activityIdsCreated);
		try
		{
			HotelbedsInterface hotelBedsInterface = new HotelbedsInterface(propertiesDAO, availSearchRQ.getClient());
			long timer2 = System.currentTimeMillis();
			Object activity = hotelBedsInterface.startSearchActivities(availSearchRQ);
			if(activity != null) {
				results = (Set<ActivityResult>) activity;
			}
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("searchByActivityId::time in activities search was " + totalTime2 + " millis");
			log.info("searchByActivityId::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		}
		catch (Exception e)
		{
			log.error("searchByActivityId::threw exception " + e.toString(), e);
		}
		return results;
	}

	@Override
	public ActivityResult rateCheck(RateCheckRQDTO rateCheckRQDT) throws Exception
	{
		log.debug("rateCheck::search(rateCheckRQDT)= {}", rateCheckRQDT);
		rateCheckRQDT.setActivityId(correctActivityId(rateCheckRQDT.getActivityId()));
		HotelbedsInterface hotelBedsInterface = new HotelbedsInterface(propertiesDAO, rateCheckRQDT.getClient());
        if ( rateCheckRQDT.getOptionId().contains("#"))
        	rateCheckRQDT.setOptionId( rateCheckRQDT.getOptionId().substring(rateCheckRQDT.getOptionId().indexOf("#")));

		return hotelBedsInterface.startCheckRate(rateCheckRQDT);
	}

	@Override
	public ActivityBookRS book(String client, ActivityBookRQ bookRQ) throws Exception
	{
		log.info("book::enter (client = {}, bookRQ= {})", client, bookRQ);
		if(bookRQ.getItems() == null || bookRQ.getItems().isEmpty()) {
			throw new Exception("item is empty");
		}
		for(ActivityBookRQ.ActivityRequestItem item : bookRQ.getItems()) {
			item.setActivityId(correctActivityId(item.getActivityId()));
		}
		long timer1 = System.currentTimeMillis();
		ActivityBookRS result = BOOK_RS_DEFAULT;
		HotelbedsInterface hotelBedsInterface = new HotelbedsInterface(propertiesDAO, client);
		long timer2 = System.currentTimeMillis();
		result = hotelBedsInterface.startBookActivities(client, bookRQ);
		long totalTime2 = (System.currentTimeMillis() - timer2);
		log.info("book::time in activities booking was " + totalTime2 + " millis");
		if(log.isDebugEnabled()) {
			log.debug("book::(result = {})", result);
		}
		return result;
	}

	@Override
	public ActivityCancelRS cancel(String client, ActivityCancelRQ cancelRQ) throws Exception {
		log.info("cancel:: (client = {}, cancelRQ= {})", client, cancelRQ);
		long timer1 = System.currentTimeMillis();
		HotelbedsInterface hotelBedsInterface = new HotelbedsInterface(propertiesDAO, client);
		long timer2 = System.currentTimeMillis();
		ActivityCancelRS result = hotelBedsInterface.startCancelActivities(client, cancelRQ);
		long totalTime2 = (System.currentTimeMillis() - timer2);
		log.info("cancel::time in activities canceling was " + totalTime2 + " millis");
		if(log.isDebugEnabled()) {
			log.debug("cancel:: (result = {})", result);
		}
		return result;
	}

	private String correctActivityId(String activityId) {
		if(activityId == null || !activityId.startsWith(CHANNEL_PREFIX)) {
			return activityId;
		}
		return activityId.substring(CHANNEL_PREFIX.length());
	}

	private static final ActivityBookRS BOOK_RS_DEFAULT = new ActivityBookRS();

	@Override
	public void initiateRCLoad(String code)
	{
		log.debug("initiateRCLoad::none required");
	}
	
	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("URL endpoint of Hotelbeds API", "hotelbedsURL", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The Hotelbeds APIKey", "apikey", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The Hotelbeds secret value", "secret", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("If bookings should be 'faked' and NOT sent to the server, just return a dummy confirmation", "bypassBooking", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}
}
