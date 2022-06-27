package com.torkirion.eroam.microservice.transport.endpoint.ims;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.ims.apidomain.Address;
import com.torkirion.eroam.ims.apidomain.DaysOfTheWeek;
import com.torkirion.eroam.ims.datadomain.EventMerchandiseLink;
import com.torkirion.eroam.ims.datadomain.IataAirport;
import com.torkirion.eroam.ims.datadomain.Merchandise;
import com.torkirion.eroam.ims.datadomain.MerchandiseSale;
import com.torkirion.eroam.ims.datadomain.TransportSale;
import com.torkirion.eroam.ims.datadomain.TransportationBasic;
import com.torkirion.eroam.ims.datadomain.TransportationBasicClass;
import com.torkirion.eroam.ims.datadomain.TransportationBasicSegment;
import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRS;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.datadomain.Airline;
import com.torkirion.eroam.microservice.events.apidomain.EventsBookRS;
import com.torkirion.eroam.microservice.merchandise.apidomain.*;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.datadomain.Airline;
import com.torkirion.eroam.microservice.merchandise.apidomain.Booking.ItemStatus;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.apidomain.*;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO;
import com.torkirion.eroam.microservice.transport.dto.RouteResult;
import com.torkirion.eroam.microservice.transport.dto.RouteResult.TransportationClass;
import com.torkirion.eroam.microservice.transport.endpoint.AbstractTransportService;
import com.torkirion.eroam.microservice.transport.endpoint.TransportServiceIF;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@AllArgsConstructor
@Slf4j
public class IMSService extends AbstractTransportService implements TransportServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private DataService imsDataService;

	public static final String CHANNEL = "LOCALIMS";

	public static final String CHANNEL_PREFIX = "IM";

	@Transactional
	public Collection<AvailTransportSearchRS> search(AvailTransportSearchRQDTO availTransportSearchRQDTO)
	{
		log.info("search::search(availTransportSearchRQDTO)=" + availTransportSearchRQDTO);

		// AvailTransportSearchRS is ONE solution to the full route problem
		// each LEG within an AvailTransportSearchRS is equivalent to one ROUTE
		long timer1 = System.currentTimeMillis();

		Set<AvailTransportSearchRS> results = new HashSet<>();
		try
		{
			List<List<RouteResult>> allResults = new ArrayList<>();
			for ( int i = 0; i < availTransportSearchRQDTO.getRoute().size(); i++ )
			{
				List<RouteResult> legsForRoute = searchOneRoute(availTransportSearchRQDTO.getRoute().get(i), availTransportSearchRQDTO.getTravellers());
				allResults.add(i, legsForRoute);
			}
			if (log.isDebugEnabled())
				log.debug("search::allResults.size=" + allResults.size());
			List<List<RouteResult>> collector = new ArrayList<>();
		    ArrayList<RouteResult> combo = new ArrayList<>();
		    combinations(collector, allResults, 0, combo);
			if (log.isDebugEnabled())
				log.debug("search::all combinations.size=" + collector.size());
			
			int sequence = 0;
			for ( List<RouteResult> routeResultList : collector )
			{
				AvailTransportSearchRS availTransportSearchRS = makeAvailTransportSearchRS(routeResultList, sequence++, availTransportSearchRQDTO.getTravellers());
				if ( availTransportSearchRS != null )
					results.add(availTransportSearchRS);
			}
			return results;
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	@Override
	public TransportBookRS book(String client, TransportBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<TransportBookRS> callableTask = () -> {
		    return bookThreaded(client, bookRQ);
		};
		Future<TransportBookRS> future = threadPoolExecutor.submit(callableTask);
		TransportBookRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	@Transactional
	protected TransportBookRS bookThreaded(String client, TransportBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);
		long timer1 = System.currentTimeMillis();
		TransportBookRS bookRS = new TransportBookRS();
		
		List<ResponseExtraInformation> errors = new ArrayList<>();
		List<TransportSale> salesRecords = new ArrayList<>();
		/*
		for ( TransportRequestItem item : bookRQ.getItems())
		{
			AvailTransportSearchRQDTO.Route route = new AvailTransportSearchRQDTO.Route();
			route.setFlight(item.getTransportCode());
			if ( item.getTransportClass() != null && item.getTransportClass().length() > 0)
				route.setTransportClass(item.getTransportClass());
			route.setTravelDate(item.getTransportDate());
			
			TravellerMix travellerMix = new TravellerMix();
			ZoneId systemTimeZone = ZoneId.systemDefault();
			for ( Integer travellerIndex : item.getTravellerIndex())
			{
				Traveller traveller = bookRQ.getTravellers().get(travellerIndex);
				ZonedDateTime zonedDateTime = item.getTransportDate().atStartOfDay(systemTimeZone);
				int age = traveller.getAge(Date.from(zonedDateTime.toInstant()));
				if ( age >= 12)
				{
					travellerMix.setAdultCount(travellerMix.getAdultCount() + 1);
				}
				else
				{
					travellerMix.getChildAges().add(age);
				}
			}
			// set mix
			boolean foundForItem = false;;
			List<RouteResult> legsForRoute = searchOneRoute(route, travellerMix);
			for ( RouteResult routeResult : legsForRoute)
			{
				if (log.isDebugEnabled())
					log.debug("book::checking result " + routeResult);
				if ( routeResult.getFlight().equals(item.getTransportCode()))
				{
					for ( TransportationClass transportationClass : routeResult.getClasses())
					{
						if ( transportationClass.getClassCode().equals(item.getTransportClass()))
						{
							// ok, good to go!
							TransportSale transportSale = new TransportSale();
							transportSale.setBookingDateTime(LocalDateTime.now());
							transportSale.setCurrency(routeResult.getCurrency());
							transportSale.setRrpCurrency(routeResult.getRrpCurrency());
							transportSale.setItemStatus(TransportSale.ItemStatus.BOOKED);
							transportSale.setCountryCodeOfOrigin(bookRQ.getCountryCodeOfOrigin());
							transportSale.setTitle(bookRQ.getBooker().getTitle());
							transportSale.setGivenName(bookRQ.getBooker().getGivenName());
							transportSale.setSurname(bookRQ.getBooker().getSurname());
							transportSale.setTelephone(bookRQ.getBooker().getTelephone());
							transportSale.setInternalBookingReference(bookRQ.getInternalBookingReference());
							transportSale.setInternalItemReference(item.getInternalItemReference());
							StringBuffer travellerNames = new StringBuffer();
							for ( Integer index : item.getTravellerIndex())
							{
								Traveller traveller = bookRQ.getTravellers().get(index);
								ZonedDateTime zonedDateTime = item.getTransportDate().atStartOfDay(systemTimeZone);
								int age = traveller.getAge(Date.from(zonedDateTime.toInstant()));
								if ( travellerNames.length() > 0 )
									travellerNames.append(",");
								travellerNames.append(cleanName(traveller.getSurname()) + "/" + cleanName(traveller.getGivenName()) + "/" + cleanName(traveller.getTitle()) + " (" + age + ")");
							}
							transportSale.setTravellerInformation(travellerNames.toString());
							salesRecords.add(transportSale);
							foundForItem = true;
							if (log.isDebugEnabled())
								log.debug("book::found!");
						}
					}
				}
			}
			if ( !foundForItem)
			{
				errors.add(new ResponseExtraInformation("400", "No transport available for " + item.getTransportCode() + " on " + item.getTransportDate()));
			}
		}
		// if sales records equals number of items, we're good to go !
		// save the sales record

		if ( errors.size() > 0 )
		{
			if (log.isDebugEnabled())
				log.debug("book::had errors : " + errors);
			bookRS.setErrors(errors);
			return bookRS;
		}
		
		for ( TransportSale salesRecord : salesRecords )
		{
			salesRecord = imsDataService.getTransportationSaleRepo().save(salesRecord);
			if ( bookRS.getBookingReference() == null )
			{
				bookRS.setBookingReference(salesRecord.getId().toString());
			}
			TransportBookRS.ResponseItem responseItem = new TransportBookRS.ResponseItem();
			responseItem.setBookingItemReference(salesRecord.getInternalItemReference());
			if ( bookRS.getInternalBookingReference() == null )
				bookRS.setInternalBookingReference(salesRecord.getInternalItemReference());
			responseItem.setChannel(CHANNEL);
			responseItem.setInternalItemReference(salesRecord.getId().toString());
			responseItem.setItemStatus(ItemStatus.BOOKED);
			bookRS.getItems().add(responseItem);
		}
		if (log.isDebugEnabled())
			log.debug("book::return " + bookRS);*/
		return bookRS;
	}

	@Override
	public TransportCancelRS cancel(String client, TransportCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::recevied " + cancelRQ);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<TransportCancelRS> callableTask = () -> {
		    return cancelThreaded(client, cancelRQ);
		};
		Future<TransportCancelRS> future = threadPoolExecutor.submit(callableTask);
		TransportCancelRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	protected TransportCancelRS cancelThreaded(String client, TransportCancelRQ cancelRQ) throws Exception
	{
		return null;
	}

	protected List<RouteResult> searchOneRoute(AvailTransportSearchRQDTO.Route routeRequest, TravellerMix travellerMix)
	{
		if (log.isDebugEnabled())
			log.debug("searchOneRoute::enter for " + routeRequest);

		List<RouteResult> results = new ArrayList<>();

		List<TransportationBasic> tBasics = null;
		if ( routeRequest.getFlight() != null && routeRequest.getFlight().length() > 0 )
		{
			tBasics = imsDataService.getTransportationBasicRepo().findAllByFlight(routeRequest.getFlight());
		}
		else
		{
			tBasics = imsDataService.getTransportationBasicRepo().findAllBySearchIataFromAndSearchIataTo(routeRequest.getDepartureIata(), routeRequest.getArrivalIata());
		}
		if (log.isDebugEnabled())
			log.debug("searchOneRoute::found " + tBasics.size() + "  database candidates");
		for (TransportationBasic tBasic : tBasics)
		{
			if (routeRequest.getTravelDate().isBefore(tBasic.getScheduleFrom()) || routeRequest.getTravelDate().isAfter(tBasic.getScheduleTo()))
			{
				if (log.isDebugEnabled())
					log.debug("searchOneRoute::transport record " + tBasic.getId() + " schedule outside travel date");
			}
			switch (routeRequest.getTravelDate().getDayOfWeek())
			{
				case SUNDAY:
					if (!tBasic.getDaysOfTheWeek().getSunday())
						continue;
					break;
				case MONDAY:
					if (!tBasic.getDaysOfTheWeek().getMonday())
						continue;
					break;
				case TUESDAY:
					if (!tBasic.getDaysOfTheWeek().getTuesday())
						continue;
					break;
				case WEDNESDAY:
					if (!tBasic.getDaysOfTheWeek().getWednesday())
						continue;
					break;
				case THURSDAY:
					if (!tBasic.getDaysOfTheWeek().getThursday())
						continue;
					break;
				case FRIDAY:
					if (!tBasic.getDaysOfTheWeek().getFriday())
						continue;
					break;
				case SATURDAY:
					if (!tBasic.getDaysOfTheWeek().getSaturday())
						continue;
					break;
				default:
			}
			if (log.isDebugEnabled())
				log.debug("searchOneRoute::we have a contender in record " + tBasic.getId());
			RouteResult routeResult = new RouteResult();
			routeResult.setFlight(tBasic.getFlight());
			routeResult.setSearchIataFrom(tBasic.getSearchIataFrom());
			routeResult.setSearchIataTo(tBasic.getSearchIataTo());
			routeResult.setSearchIataTo(tBasic.getSearchIataTo());
			routeResult.setFromIata(tBasic.getFromIata());
			routeResult.setToIata(tBasic.getToIata());
			routeResult.setCurrency(tBasic.getCurrency());
			routeResult.setRrpCurrency(tBasic.getRrpCurrency());
			routeResult.setBookingConditions(tBasic.getBookingConditions());

			SortedSet<TransportationBasicSegment> segments = new TreeSet<>(new TransportationBasicSegment.TransportationBasicSegmentComparator());
			segments.addAll(tBasic.getSegments());
			int arrivalDaysExtra = 0;
			for (TransportationBasicSegment tSegment : segments)
			{
				RouteResult.Segment rSegment = new RouteResult.Segment();
				BeanUtils.copyProperties(tSegment, rSegment, "transportation");
				LocalDateTime departureDateTime = tSegment.getDepartureTime().atDate(routeRequest.getTravelDate());
				LocalDateTime arrivalDateTime = tSegment.getArrivalTime().atDate(routeRequest.getTravelDate());
				arrivalDaysExtra += tSegment.getArrivalDayExtra();
				arrivalDateTime = arrivalDateTime.plusDays(arrivalDaysExtra);
				rSegment.setDepartureDateTime(departureDateTime);
				rSegment.setArrivalDateTime(arrivalDateTime);
				routeResult.getSegments().add(rSegment);
				if (routeResult.getDepartureDateTime() == null)
					routeResult.setDepartureDateTime(departureDateTime);
				routeResult.setArrivalDateTime(arrivalDateTime);
			}
			for (TransportationBasicClass tClass : tBasic.getClasses())
			{
				if (routeRequest.getTransportClass() != null)
				{
					if (!routeRequest.getTransportClass().equals(tClass.getClassCode()))
					{
						if (log.isDebugEnabled())
							log.debug("searchOneRoute::filtered out class " + tClass.getClassCode() + ", is not " + routeRequest.getTransportClass());
						continue;
					}
				}
				if ( tClass.getReference() == null || tClass.getAdultNett() == null )
				{
					if (log.isDebugEnabled())
						log.debug("searchOneRoute::bad data, null class data for " + tClass.getId() );
					continue;
				}
				RouteResult.TransportationClass rClass = new RouteResult.TransportationClass();
				BeanUtils.copyProperties(tClass, rClass, "transportation");
				routeResult.getClasses().add(rClass);
			}
			if (routeResult.getClasses().size() > 0)
			{
				results.add(routeResult);
			}
			else
			{
				if (log.isDebugEnabled())
					log.debug("searchOneRoute::route discarded as has no classes");
			}
		}
		if (log.isDebugEnabled())
			log.debug("searchOneRoute::return " + results.size() + " results");
		return results;
	}

	private static final HashMap<String, com.torkirion.eroam.microservice.transport.datadomain.IataAirport> airportCache = new HashMap<>();

	@Override
	protected com.torkirion.eroam.microservice.transport.datadomain.IataAirport getAirport(String iataCode)
	{
		if ( airportCache.get(iataCode) != null )
		{
			return airportCache.get(iataCode);
		}
		List<com.torkirion.eroam.ims.datadomain.IataAirport> recs = imsDataService.getIataAirportRepo().findByIataCode(iataCode);
		if ( recs.size() > 0 )
		{
			com.torkirion.eroam.microservice.transport.datadomain.IataAirport tAirport = new com.torkirion.eroam.microservice.transport.datadomain.IataAirport();
			BeanUtils.copyProperties(recs, tAirport);
			airportCache.put(iataCode, tAirport);
			return tAirport;
		}
		else
		{
			log.warn("getAirport::unknown airport " + iataCode);
			return null;
		}
	}

	private static final HashMap<String, Airline> airlineCache = new HashMap<>();

	@Override
	protected Airline getAirline(String iataCode)
	{
		if ( airlineCache.get(iataCode) != null )
			return airlineCache.get(iataCode);
		Optional<Airline> airlineOpt = imsDataService.getAirlineRepo().findById(iataCode);
		if ( airlineOpt.isPresent() )
		{
			airlineCache.put(iataCode, airlineOpt.get());
			return airlineOpt.get();
		}
		else
		{
			log.warn("getAirline::unknown airline " + iataCode);
			return null;
		}
	}

	@Override
	protected RouteResult.TransportationClass findCheapestClass(RouteResult routeResult)
	{
		RouteResult.TransportationClass cheapestTransportationClass = null;
		for (RouteResult.TransportationClass transportationClass : routeResult.getClasses())
		{
			if (log.isDebugEnabled())
				log.debug("findCheapestClass::transportationClass=" + transportationClass + ", cheapestTransportationClass=" + cheapestTransportationClass);

			if ( cheapestTransportationClass == null || cheapestTransportationClass.getAdultRrp().compareTo(transportationClass.getAdultRrp()) > 0 )
			{
				if ( transportationClass.getReference() != null )
					cheapestTransportationClass = transportationClass;
			}
		}
		return cheapestTransportationClass;
	}

	@Override
	protected String getType() {
		return "flight";
	}

	@Override
	protected String getProvider() {
		return "Local";
	}

	@Override
	protected String makeTransportSearchRSId(List<RouteResult> routeResults) {
		StringBuffer id = new StringBuffer(routeResults.get(0).getDepartureDateTime().format(yyyymmdd));
		for(RouteResult routeResult : routeResults) {
			id.append("|" + routeResult.getFlight());
		}
		return id.toString();
	}

	private static final ObjectMapper getObjectMapper()
	{
		if (_objectMapper == null)
		{
			_objectMapper = new ObjectMapper();
			_objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return _objectMapper;
	}

	private static ObjectMapper _objectMapper;
	
	private String cleanName(String name) 
	{
		return name.replaceAll(",", "").replaceAll("/", "");
	}
	
	public static ChannelType getSystemPropertiesDescription() {
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}

	@Override
	public TransportRateCheckRS rateCheck(String client, TransportRateCheckRQ rateCheckRQ) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
}
