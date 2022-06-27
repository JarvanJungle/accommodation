package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.spi.CachingProvider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.BookingRS.BookedRoom;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YalagoInterface
{
	private SystemPropertiesDAO properties;

	private YalagoAPIProperties yalagoAPIProperties;

	public YalagoAPIProperties getYalagoProperties()
	{
		return yalagoAPIProperties;
	}

	public YalagoInterface(SystemPropertiesDAO properties, AccommodationRCRepo accommodationRCRepo, String site, String channel) throws Exception
	{
		this.properties = properties;
		init(site, channel);
	}

	private void init(String site, String channel) throws Exception
	{
		if (log.isDebugEnabled())
		log.debug("init::entering with site " + site + ", channel " + channel);

		yalagoAPIProperties = new YalagoAPIProperties();
		yalagoAPIProperties.url = properties.getProperty(site, channel, "url");
		yalagoAPIProperties.apikey = properties.getProperty(site, channel, "apikey");
		if (log.isDebugEnabled())
		log.debug("init::yalagoAPIProperties=" + yalagoAPIProperties);
	}

	public AvailabilityRS searchHotels(AvailabilityRQ availabilityRQ) throws Exception
	{
		if (log.isDebugEnabled())
		log.debug("searchHotels::entering for availabilityRQ=" + availabilityRQ);

		long searchStartTime = System.currentTimeMillis();

		HttpService httpService = new YalagoHttpService(yalagoAPIProperties);

		StringWriter writer = new StringWriter();
		getObjectMapper().writeValue(writer, availabilityRQ);
		String requestString = writer.toString();

		String responseString = httpService.doCallPost("hotels/availability/get", availabilityRQ);
		if (log.isDebugEnabled())
		log.debug("searchHotels::responseString = " + responseString);

		if (log.isDebugEnabled())
		log.debug("searchHotels::time taken = " + (System.currentTimeMillis() - searchStartTime));
		try
		{
			AvailabilityRS availabilityRS = getObjectMapper().readValue(responseString, AvailabilityRS.class);
			return availabilityRS;
		}
		catch (Exception e)
		{
			log.error("searchHotels::caught exception " + e.toString(), e);
			return null;
		}
	}

	public DetailsRS directRateCheck(DetailsRQ detailsRQ) throws Exception
	{
		if (log.isDebugEnabled())
		log.debug("directRateCheck::entering for detailsRQ=" + detailsRQ);

		long searchStartTime = System.currentTimeMillis();

		HttpService httpService = new YalagoHttpService(yalagoAPIProperties);

		String responseString = httpService.doCallPost("hotels/details/get", detailsRQ);
		if (log.isDebugEnabled())
		log.debug("directRateCheck::responseString = " + responseString);

		if (log.isDebugEnabled())
		log.debug("directRateCheck::time taken = " + (System.currentTimeMillis() - searchStartTime));
		try
		{
			DetailsRS detailsRS = getObjectMapper().readValue(responseString, DetailsRS.class);
			return detailsRS;
		}
		catch (Exception e)
		{
			log.error("directRateCheck::caught exception " + e.toString(), e);
			return null;
		}
	}

	public BookingRS book(BookingRQ bookingRQ) throws Exception
	{
		if (log.isDebugEnabled())
		log.debug("book::entering");
		long searchStartTime = System.currentTimeMillis();

		HttpService httpService = new YalagoHttpService(yalagoAPIProperties);

		if (yalagoAPIProperties.bypassBooking)
		{
			log.debug("book::bypassBooking");
			BookingRS bookingRS = new BookingRS();
			bookingRS.setRooms(new ArrayList<>());
			BookingRS.BookedRoom bookedRoom = new BookingRS.BookedRoom();
			bookingRS.getRooms().add(bookedRoom);
			bookedRoom.setProviderRef("YL1234");
			bookedRoom.setAffiliateRoomRef("YL1234");
			bookingRS.setStatus(2);
			return bookingRS;
		}

		String responseString = httpService.doCallPost("hotels/bookings/create", bookingRQ);
		if (log.isDebugEnabled())
		log.debug("book::responseString = " + responseString);

		if (log.isDebugEnabled())
		log.debug("book::time taken = " + (System.currentTimeMillis() - searchStartTime));
		try
		{
			BookingRS bookingRS = getObjectMapper().readValue(responseString, BookingRS.class);
			return bookingRS;
		}
		catch (Exception e)
		{
			log.error("book::caught exception " + e.toString(), e);
			return null;
		}
	}

	public YalagoCancelChargeRS cancelCharge(YalagoCancelChargeRQ yalagoCancelChargeRQ) throws Exception
	{
		if (log.isDebugEnabled())
		log.debug("cancelCharge::entering");
		long startTime = System.currentTimeMillis();

		HttpService httpService = new YalagoHttpService(yalagoAPIProperties);

		String responseString = httpService.doCallPost("hotels/bookings/getcancellationcharges", yalagoCancelChargeRQ);
		if (log.isDebugEnabled())
		log.debug("cancelCharge::responseString = " + responseString);

		if (log.isDebugEnabled())
		log.debug("cancelCharge::time taken = " + (System.currentTimeMillis() - startTime));
		try
		{
			YalagoCancelChargeRS yalagoCancelChargeRS = getObjectMapper().readValue(responseString, YalagoCancelChargeRS.class);
			return yalagoCancelChargeRS;
		}
		catch (Exception e)
		{
			log.error("cancelCharge::caught exception " + e.toString(), e);
			return null;
		}
	}

	public YalagoCancelRS cancel(YalagoCancelRQ yalagoCancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
		log.debug("cancel::entering");
		long startTime = System.currentTimeMillis();

		HttpService httpService = new YalagoHttpService(yalagoAPIProperties);

		String responseString = httpService.doCallPost("hotels/bookings/cancel", yalagoCancelRQ);
		if (log.isDebugEnabled())
		log.debug("cancel::responseString = " + responseString);

		if (log.isDebugEnabled())
		log.debug("cancel::time taken = " + (System.currentTimeMillis() - startTime));
		try
		{
			YalagoCancelRS yalagoCancelRS = getObjectMapper().readValue(responseString, YalagoCancelRS.class);
			return yalagoCancelRS;
		}
		catch (Exception e)
		{
			log.error("cancel::caught exception " + e.toString(), e);
			return null;
		}
	}

	public YalagoGetBookingRS getBooking(YalagoGetBookingRQ yalagoGetBookingRQ) throws Exception
	{
		if (log.isDebugEnabled())
		log.debug("getBooking::entering");
		long startTime = System.currentTimeMillis();

		HttpService httpService = new YalagoHttpService(yalagoAPIProperties);

		String responseString = httpService.doCallPost("hotels/bookings/getbooking", yalagoGetBookingRQ);
		if (log.isDebugEnabled())
		log.debug("getBooking::responseString = " + responseString);

		if (log.isDebugEnabled())
			log.debug("getBooking::time taken = " + (System.currentTimeMillis() - startTime));
		try
		{
			YalagoGetBookingRS yalagoGetBookingRS = getObjectMapper().readValue(responseString, YalagoGetBookingRS.class);
			return yalagoGetBookingRS;
		}
		catch (Exception e)
		{
			log.error("getBooking::caught exception " + e.toString(), e);
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

	private static Cache<String, String> _cache = null;

	private ObjectMapper _objectMapper = null;
	
	private static DateTimeFormatter formatterYYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
