package com.torkirion.eroam.microservice.events.endpoint.ims;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
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
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.torkirion.eroam.microservice.events.apidomain.EventsBookRQ;
import com.torkirion.eroam.microservice.events.apidomain.EventsBookRQ.EventRequestItem;
import com.torkirion.eroam.microservice.events.apidomain.Booking.ItemStatus;
import com.torkirion.eroam.microservice.events.apidomain.EventMerchandiseSalesAPILink;
import com.torkirion.eroam.microservice.events.apidomain.EventsBookRS;
import com.torkirion.eroam.microservice.events.apidomain.EventResult;
import com.torkirion.eroam.microservice.events.apidomain.EventSeries;
import com.torkirion.eroam.microservice.events.apidomain.EventTicketAllotment;
import com.torkirion.eroam.microservice.events.apidomain.EventTicketClassification;
import com.torkirion.eroam.microservice.events.apidomain.Venue;
import com.torkirion.eroam.microservice.events.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.events.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.events.dto.RateCheckRQDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.apidomain.Address;
import com.torkirion.eroam.ims.apidomain.GeoCoordinates;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.datadomain.Event;
import com.torkirion.eroam.ims.datadomain.EventAllotment;
import com.torkirion.eroam.ims.datadomain.EventClassification;
import com.torkirion.eroam.ims.datadomain.EventMerchandiseLink;
import com.torkirion.eroam.ims.datadomain.EventSale;
import com.torkirion.eroam.ims.datadomain.EventVenue;
import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRS;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.events.endpoint.EventsServiceIF;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class IMSService implements EventsServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private DataService imsDataService;

	public static final String CHANNEL = "LOCALIMS";

	public static final String CHANNEL_PREFIX = "IM";

	public Set<EventResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		Set<EventResult> results = new HashSet<>();
		Map<Integer, Venue> venues = new HashMap<>();
		try
		{
			List<EventVenue> allVenues = imsDataService.getEventVenueRepo().findAll();
			log.debug("search::testing " + allVenues.size() + " venues");
			for (EventVenue eventVenue : allVenues)
			{
				log.debug("search::testing venue " + eventVenue.getId() + " : " + eventVenue.getName());
				if (eventVenue.getAddress() != null && eventVenue.getAddress().getGeoCoordinates() != null)
				{
					log.debug(
							"search::testing venue latitude " + eventVenue.getAddress().getGeoCoordinates().getLatitude() + " longitude " + eventVenue.getAddress().getGeoCoordinates().getLongitude());
					if (eventVenue.getAddress().getGeoCoordinates().getLatitude().compareTo(availSearchRQ.getNorthwest().getLatitude()) < 0
							&& eventVenue.getAddress().getGeoCoordinates().getLatitude().compareTo(availSearchRQ.getSoutheast().getLatitude()) > 0
							&& eventVenue.getAddress().getGeoCoordinates().getLongitude().compareTo(availSearchRQ.getNorthwest().getLongitude()) > 0
							&& eventVenue.getAddress().getGeoCoordinates().getLongitude().compareTo(availSearchRQ.getSoutheast().getLongitude()) < 0)
					{
						log.debug("search::found candidate venue:" + eventVenue.getName());
						List<com.torkirion.eroam.ims.datadomain.Event> allEvents = imsDataService.getEventRepo().findByEventVenue(eventVenue);
						for (com.torkirion.eroam.ims.datadomain.Event event : allEvents)
						{
							log.debug("search::found possbile event " + event.getName());
							EventResult eventResult = makeEventResult(event, availSearchRQ, false);
							if (eventResult != null)
							{
								Venue venue = venues.get(eventVenue.getId());
								if (venue == null)
								{
									venue = makeVenue(eventVenue);
									venues.put(venue.getId(), venue);
								}
								eventResult.setVenue(venue);
								results.add(eventResult);
							}
						}
					}
					else
					{
						log.debug("search::venue was outside searchBox");
					}
				}
				else
				{
					log.debug("search::venue " + eventVenue.getId() + "/" + eventVenue.getName() + " has no address and geocordinates");
				}
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
	public EventResult rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter with " + rateCheckRQDTO);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(rateCheckRQDTO.getClient(), 1);
		Callable<EventResult> callableTask = () -> {
			return rateCheckThreaded(rateCheckRQDTO);
		};
		Future<EventResult> future = threadPoolExecutor.submit(callableTask);
		EventResult result = future.get(30, TimeUnit.SECONDS);
		return result;
	}

	protected EventResult rateCheckThreaded(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter with " + rateCheckRQDTO);

		Integer eventId = (rateCheckRQDTO.getEventId().startsWith(IMSService.CHANNEL_PREFIX) ? Integer.parseInt(rateCheckRQDTO.getEventId().substring(IMSService.CHANNEL_PREFIX.length()))
				: Integer.parseInt(rateCheckRQDTO.getEventId()));
		Optional<com.torkirion.eroam.ims.datadomain.Event> eventDataOpt = imsDataService.getEventRepo().findById(eventId);
		if (!eventDataOpt.isPresent())
		{
			throw new Exception("Event not found");
		}
		com.torkirion.eroam.ims.datadomain.Event eventData = eventDataOpt.get();
		com.torkirion.eroam.ims.datadomain.EventClassification eventClassification = null;
		for (EventClassification c : eventData.getClassifications())
		{
			if (log.isDebugEnabled())
				log.debug("rateCheck::compare " + c.getId().toString() + " and " + rateCheckRQDTO.getClassificationId());
			if (c.getId().toString().equals(rateCheckRQDTO.getClassificationId()))
			{
				eventClassification = c;
				break;
			}
		}
		if (eventClassification == null)
		{
			throw new Exception("Classification not found");
		}
		com.torkirion.eroam.ims.datadomain.EventAllotment eventAllotment = null;
		for (EventAllotment a : eventData.getAllotments())
		{
			if (a.getId().intValue() == eventClassification.getAllotmentId().intValue())
			{
				eventAllotment = a;
				break;
			}
		}
		if (rateCheckRQDTO.getNumberOfTickets() != null && rateCheckRQDTO.getNumberOfTickets().intValue() != 0)
			validateItem(rateCheckRQDTO.getNumberOfTickets(), null, eventData, eventClassification, eventAllotment);

		AvailSearchRQDTO availSearchRQDTO = new AvailSearchRQDTO();
		availSearchRQDTO.setTravellers(rateCheckRQDTO.getTravellers());
		availSearchRQDTO.setCountryCodeOfOrigin(rateCheckRQDTO.getCountryCodeOfOrigin());
		availSearchRQDTO.setEventDateFrom(rateCheckRQDTO.getEventDateFrom());
		availSearchRQDTO.setEventDateTo(rateCheckRQDTO.getEventDateTo());
		EventResult eventResult = makeEventResult(eventData, availSearchRQDTO, true);
		return eventResult;
	}

	@Override
	@Transactional
	public EventResult readEvent(String client, String eventId_S) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("readEvent::enter with " + eventId_S);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<EventResult> callableTask = () -> {
			return readEventThreaded(eventId_S);
		};
		Future<EventResult> future = threadPoolExecutor.submit(callableTask);
		EventResult result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	protected EventResult readEventThreaded(String eventId_S) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("readEvent::enter with " + eventId_S);

		Integer eventId = (eventId_S.startsWith(IMSService.CHANNEL_PREFIX) ? Integer.parseInt(eventId_S.substring(IMSService.CHANNEL_PREFIX.length())) : Integer.parseInt(eventId_S));
		Optional<com.torkirion.eroam.ims.datadomain.Event> eventDataOpt = imsDataService.getEventRepo().findById(eventId);
		if (!eventDataOpt.isPresent())
		{
			throw new Exception("Event not found");
		}
		com.torkirion.eroam.ims.datadomain.Event eventData = eventDataOpt.get();
		List<EventTicketAllotment> allotments = makeEventAllotments(eventData, 1);
		EventResult eventResult = new EventResult();
		eventResult.setId(IMSService.CHANNEL_PREFIX + eventData.getId());
		eventResult.setChannelId(eventData.getId().toString());
		eventResult.setChannel(IMSService.CHANNEL);
		eventResult.setExternalEventId(eventData.getExternalEventId());
		eventResult.setName(eventData.getName());
		eventResult.setTeamOrPerformer(eventData.getTeamOrPerformer());
		eventResult.setSeriesId(eventData.getEventSeries().getId());
		eventResult.setSeriesName(eventData.getEventSeries().getName());
		eventResult.setType(eventData.getEventSeries().getEventType().getName());
		eventResult.setSupplierName(eventData.getEventSupplier().getName());
		eventResult.setStartDate(eventData.getStartDate().format(dateFormatter));
		eventResult.setEndDate(eventData.getEndDate().format(dateFormatter));
		if (eventData.getStartTime() != null)
			eventResult.setStartTime(eventData.getStartTime().format(timeFormatter));
		eventResult.setOverview(eventData.getOverview());
		eventResult.setImageUrl(eventData.getImageUrl());
		eventResult.setTermsAndConditions(eventData.getTermsAndConditions());
		eventResult.setDefaultSeatmapImageUrl(eventData.getDefaultSeatmapImageUrl());
		eventResult.setSeatMapNotAvailable(eventData.getSeatMapNotAvailable());
		eventResult.setEventTicketAllotments(allotments);
		for (EventTicketAllotment eventTicketAllotment : allotments)
		{
			for (EventTicketClassification eventTicketClassification : eventTicketAllotment.getEventTicketClassifications())
			{
				eventResult.setCurrency(eventTicketClassification.getTotalRetailPrice().getCurrencyId());
				if (eventResult.getTotalNetPrice() == null || eventResult.getTotalNetPrice().getAmount().compareTo(eventTicketClassification.getTotalNetPrice().getAmount()) > 0)
				{
					eventResult.setTotalNetPrice(eventTicketClassification.getTotalNetPrice());
					eventResult.setFromNettPrice(eventTicketClassification.getTotalNetPrice().getAmount());
				}
				if (eventTicketClassification.getTotalRetailPrice() != null)
				{
					if (eventResult.getTotalRetailPrice() == null || eventResult.getTotalRetailPrice().getAmount().compareTo(eventTicketClassification.getTotalRetailPrice().getAmount()) > 0)
					{
						eventResult.setTotalRetailPrice(eventTicketClassification.getTotalRetailPrice());
						eventResult.setFromRrpPrice(eventTicketClassification.getTotalRetailPrice().getAmount());
					}
				}
			}
		}
		if (eventData.getEventSeries().getEventMerchandiseLinks() != null)
		{
			for (EventMerchandiseLink merchlink : eventData.getEventSeries().getEventMerchandiseLinks())
			{
				EventMerchandiseSalesAPILink l = new EventMerchandiseSalesAPILink();
				l.setMerchandiseId(CHANNEL_PREFIX + merchlink.getMerchandise().getId());
				l.setMandatoryInclusion(merchlink.getMandatoryInclusion());
				eventResult.getEventMerchandiseAPILink().add(l);
			}
		}

		return eventResult;
	}

	@Override
	@Transactional
	public List<EventSeries> listSeries(String client) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("listSeries::enter");
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<List<EventSeries>> callableTask = () -> {
			return listSeriesThreaded();
		};
		Future<List<EventSeries>> future = threadPoolExecutor.submit(callableTask);
		List<EventSeries> result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	protected List<EventSeries> listSeriesThreaded() throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("listSeries::enter");

		List<EventSeries> allSeries = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.EventSeries eventSeriesData : imsDataService.getEventSeriesRepo().findAll())
		{
			if (eventSeriesData.getActive())
			{
				EventSeries s = new EventSeries();
				BeanUtils.copyProperties(eventSeriesData, s);
				s.setType(eventSeriesData.getEventType().getName());
				if (eventSeriesData.getCountries() != null)
				{
					String[] split = eventSeriesData.getCountries().split(",");
					s.setCountries(Arrays.asList(split));
				}
				if (eventSeriesData.getMarketingCountries() != null)
				{
					String[] split = eventSeriesData.getMarketingCountries().split(",");
					s.setMarketingCountries(Arrays.asList(split));
				}
				if (eventSeriesData.getExcludedMarketingCountries() != null)
				{
					String[] split = eventSeriesData.getExcludedMarketingCountries().split(",");
					s.setExcludedMarketingCountries(Arrays.asList(split));
				}
				List<Event> eventsData = imsDataService.getEventRepo().findByEventSeries(eventSeriesData);
				for (Event eventData : eventsData)
				{
					EventResult eventResult = makeEventResult(eventData);
					s.getEvents().add(eventResult);
				}
				allSeries.add(s);
			}
		}
		return allSeries;
	}

	@Override
	@Transactional
	public EventsBookRS book(String client, EventsBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<EventsBookRS> callableTask = () -> {
			return bookThreaded(client, bookRQ);
		};
		Future<EventsBookRS> future = threadPoolExecutor.submit(callableTask);
		EventsBookRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}

	@Transactional
	protected EventsBookRS bookThreaded(String client, EventsBookRQ bookRQ)
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);
		long timer1 = System.currentTimeMillis();
		EventsBookRS bookRS = new EventsBookRS();
		bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
		try
		{

			for (EventRequestItem bookingItem : bookRQ.getItems())
			{
				Integer eventId = (bookingItem.getEventId().startsWith(IMSService.CHANNEL_PREFIX) ? Integer.parseInt(bookingItem.getEventId().substring(IMSService.CHANNEL_PREFIX.length()))
						: Integer.parseInt(bookingItem.getEventId()));
				Optional<com.torkirion.eroam.ims.datadomain.Event> eventDataOpt = imsDataService.getEventRepo().findById(eventId);
				if (!eventDataOpt.isPresent())
				{
					throw new Exception("Event not found");
				}
				com.torkirion.eroam.ims.datadomain.Event eventData = eventDataOpt.get();
				com.torkirion.eroam.ims.datadomain.EventClassification eventClassification = null;
				for (EventClassification c : eventData.getClassifications())
				{
					if (log.isDebugEnabled())
						log.debug("book::compare " + c.getId().toString() + " and " + bookingItem.getClassificationId());
					if (c.getId().toString().equals(bookingItem.getClassificationId()))
					{
						eventClassification = c;
						break;
					}
				}
				if (eventClassification == null)
				{
					throw new Exception("Classification not found");
				}
				com.torkirion.eroam.ims.datadomain.EventAllotment eventAllotment = null;
				for (EventAllotment a : eventData.getAllotments())
				{
					if (a.getId().intValue() == eventClassification.getAllotmentId().intValue())
					{
						eventAllotment = a;
						break;
					}
				}
				validateItem(bookingItem.getNumberOfTickets(), bookingItem.getSupplyRate(), eventData, eventClassification, eventAllotment);

				eventAllotment.setAllotment(eventAllotment.getAllotment().intValue() - bookingItem.getNumberOfTickets().intValue());
				eventAllotment = imsDataService.getEventAllotmentRepo().save(eventAllotment);
				if (log.isDebugEnabled())
					log.debug("book::updated eventAllotment : " + eventAllotment);
				EventSale eventSale = new EventSale();
				// blah set sale values
				eventSale.setEvent(eventData);
				eventSale.setEventDate(eventData.getStartDate());
				eventSale.setBookingDateTime(LocalDateTime.now());
				eventSale.setName(eventData.getName());
				eventSale.setCurrency(eventClassification.getCurrency());
				eventSale.setNettPrice(eventClassification.getNettPrice());
				eventSale.setRrpPrice(eventClassification.getRrpPrice());
				eventSale.setTicketingDescription(eventClassification.getTicketingDescription());
				eventSale.setItemStatus(EventSale.ItemStatus.BOOKED);
				eventSale.setAllotmentId(eventAllotment.getId());
				eventSale.setAllotmentName(eventAllotment.getName());
				eventSale.setClassificationId(eventClassification.getId());
				eventSale.setClassificationName(eventClassification.getName());
				eventSale.setCount(bookingItem.getNumberOfTickets());
				eventSale.setCountryCodeOfOrigin(bookRQ.getCountryCodeOfOrigin());
				eventSale.setTitle(bookRQ.getBooker().getTitle());
				eventSale.setGivenName(bookRQ.getBooker().getGivenName());
				eventSale.setSurname(bookRQ.getBooker().getSurname());
				eventSale.setTelephone(bookRQ.getBooker().getTelephone());
				eventSale.setInternalBookingReference(bookRQ.getInternalBookingReference());
				eventSale.setInternalItemReference(bookingItem.getInternalItemReference());
				eventSale = imsDataService.getEventSaleRepo().save(eventSale);
				eventData.getSales().add(eventSale);
				imsDataService.getEventRepo().save(eventData);
				if (log.isDebugEnabled())
					log.debug("book::saved sale : " + eventSale);

				EventsBookRS.ResponseItem responseItem = new EventsBookRS.ResponseItem();
				responseItem.setChannel(CHANNEL);
				responseItem.setBookingItemReference(eventSale.getId().toString());
				responseItem.setInternalItemReference(bookingItem.getInternalItemReference());
				responseItem.setItemStatus(ItemStatus.BOOKED);
				bookRS.getItems().add(responseItem);
				if (bookRS.getBookingReference() == null)
					bookRS.setBookingReference(responseItem.getBookingItemReference());
				if (log.isDebugEnabled())
					log.debug("book::responseItem=" + responseItem);
			}
			log.info("book:: time taken = " + (System.currentTimeMillis() - timer1));
		}
		catch (Exception e)
		{
			log.error("book::threw exception " + e.toString(), e);
			ResponseExtraInformation error = new ResponseExtraInformation("500", e.getMessage());
			bookRS.getErrors().add(error);
		}
		return bookRS;
	}

	protected void validateItem(Integer numberOfTickets, CurrencyValue supplyRate, com.torkirion.eroam.ims.datadomain.Event eventData,
			com.torkirion.eroam.ims.datadomain.EventClassification eventClassification, com.torkirion.eroam.ims.datadomain.EventAllotment eventAllotment) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("validateItem::enter");
		if (eventAllotment == null)
		{
			throw new Exception("Allotment not found");
		}
		if (numberOfTickets.intValue() > eventAllotment.getAllotment().intValue())
		{
			throw new Exception("Allotment no longer available, requested " + numberOfTickets.intValue() + " less than available " + eventAllotment.getAllotment());
		}
		Set<Integer> multiplePatterns = new HashSet<>();
		if (eventAllotment.getMultiplePattern() != null && eventAllotment.getMultiplePattern().length() > 0)
		{
			String[] split = eventAllotment.getMultiplePattern().split(",");
			for (int i = 0; i < split.length; i++)
			{
				try
				{
					multiplePatterns.add(Integer.parseInt(split[i]));
				}
				catch (NumberFormatException e)
				{
					log.warn("validateItem::invalid internal MultiplePattern '" + split[i] + "' from '" + eventAllotment.getMultiplePattern() + "'");
				}
			}
		}
		if (numberOfTickets.intValue() < eventAllotment.getMinimumSale().intValue() || numberOfTickets > eventAllotment.getMaximumSale()
				|| (multiplePatterns.size() > 0 && !multiplePatterns.contains(numberOfTickets)))
		{
			throw new Exception("A purchase of " + numberOfTickets + " tickets is not permitted");
		}
		if (supplyRate != null && supplyRate.getAmount().compareTo(eventClassification.getNettPrice()) != 0)
		{
			throw new Exception("Price has changed");
		}
		if (log.isDebugEnabled())
			log.debug("validateItem::ok");
	}

	/*
	 * public CancelRS cancel(String site, CancelRQ cancelRQ) { if (log.isDebugEnabled()) log.debug("cancel::received " +
	 * cancelRQ); long timer1 = System.currentTimeMillis(); try { HotelbedsInterface hotelBedsInterface = new
	 * HotelbedsInterface(propertiesDAO, site, CHANNEL); CancelRS cancelRS = hotelBedsInterface.cancel(cancelRQ);
	 * log.info("cancel:: time taken = " + (System.currentTimeMillis() - timer1)); return cancelRS; } catch (Exception e) {
	 * log.error("cancel::threw exception " + e.toString(), e); } return null; }
	 */
	/*
	 * public RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) { if (log.isDebugEnabled()) log.debug("retrieve::received "
	 * + retrieveRQ); long timer1 = System.currentTimeMillis(); try { HotelbedsInterface hotelBedsInterface = new
	 * HotelbedsInterface(propertiesDAO, site, CHANNEL); RetrieveRS retrieveRS = hotelBedsInterface.retrieve(retrieveRQ);
	 * log.info("retrieve:: time taken = " + (System.currentTimeMillis() - timer1)); return retrieveRS; } catch (Exception e) {
	 * log.error("retrieve::threw exception " + e.toString(), e); } return null; }
	 */
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	protected EventResult makeEventResult(com.torkirion.eroam.ims.datadomain.Event event)
	{
		List<EventTicketAllotment> allotments = makeEventAllotments(event, 1);
		if (allotments.size() > 0)
		{
			EventResult eventResult = new EventResult();
			eventResult.setId(IMSService.CHANNEL_PREFIX + event.getId());
			eventResult.setChannelId(event.getId().toString());
			eventResult.setChannel(IMSService.CHANNEL);
			eventResult.setExternalEventId(event.getExternalEventId());
			eventResult.setName(event.getName());
			eventResult.setTeamOrPerformer(event.getTeamOrPerformer());
			eventResult.setSeriesId(event.getEventSeries().getId());
			eventResult.setSeriesName(event.getEventSeries().getName());
			eventResult.setType(event.getEventSeries().getEventType().getName());
			eventResult.setSupplierName(event.getEventSupplier().getName());
			eventResult.setStartDate(event.getStartDate().format(dateFormatter));
			eventResult.setEndDate(event.getEndDate().format(dateFormatter));
			if (event.getStartTime() != null)
				eventResult.setStartTime(event.getStartTime().format(timeFormatter));
			eventResult.setOverview(event.getOverview());
			eventResult.setImageUrl(event.getImageUrl());
			eventResult.setTermsAndConditions(event.getTermsAndConditions());
			eventResult.setDefaultSeatmapImageUrl(event.getDefaultSeatmapImageUrl());
			eventResult.setSeatMapNotAvailable(event.getSeatMapNotAvailable());
			eventResult.setEventTicketAllotments(allotments);
			for (EventTicketAllotment eventTicketAllotment : allotments)
			{
				for (EventTicketClassification eventTicketClassification : eventTicketAllotment.getEventTicketClassifications())
				{
					eventResult.setCurrency(eventTicketClassification.getTotalRetailPrice().getCurrencyId());
					if (eventResult.getTotalNetPrice() == null || eventResult.getTotalNetPrice().getAmount().compareTo(eventTicketClassification.getTotalNetPrice().getAmount()) > 0)
					{
						eventResult.setTotalNetPrice(eventTicketClassification.getTotalNetPrice());
						eventResult.setFromNettPrice(eventTicketClassification.getTotalNetPrice().getAmount());
					}
					if (eventTicketClassification.getTotalRetailPrice() != null)
					{
						if (eventResult.getTotalRetailPrice() == null || eventResult.getTotalRetailPrice().getAmount().compareTo(eventTicketClassification.getTotalRetailPrice().getAmount()) > 0)
						{
							eventResult.setTotalRetailPrice(eventTicketClassification.getTotalRetailPrice());
							eventResult.setFromRrpPrice(eventTicketClassification.getTotalRetailPrice().getAmount());
						}
					}
				}
			}
			if (event.getEventSeries().getEventMerchandiseLinks() != null)
			{
				for (EventMerchandiseLink merchlink : event.getEventSeries().getEventMerchandiseLinks())
				{
					EventMerchandiseSalesAPILink l = new EventMerchandiseSalesAPILink();
					l.setMerchandiseId(CHANNEL_PREFIX + merchlink.getMerchandise().getId());
					l.setMandatoryInclusion(merchlink.getMandatoryInclusion());
					eventResult.getEventMerchandiseAPILink().add(l);
				}
			}
			return eventResult;
		}
		return null;
	}

	protected EventResult makeEventResult(com.torkirion.eroam.ims.datadomain.Event event, AvailSearchRQDTO availSearchRQDTO, boolean checkAllotments)
	{
		log.debug("makeEventResult::enter for " + event.getName());

		log.debug("makeEventResult::compare search " + availSearchRQDTO.getEventDateFrom() + " to " + availSearchRQDTO.getEventDateTo() + " for event " + event.getStartDate() + " to "
				+ event.getEndDate());
		if (availSearchRQDTO.getEventDateFrom().isAfter(event.getStartDate()))
		{
			log.debug("makeEventResult::event start date " + event.getStartDate() + " is before search date " + availSearchRQDTO.getEventDateFrom());
			return null;
		}
		LocalDate endDate = event.getStartDate();
		if (event.getEndDate() != null)
			endDate = event.getEndDate();
		if (availSearchRQDTO.getEventDateTo().isBefore(endDate))
		{
			log.debug("makeEventResult::event end date " + endDate + " is after search date " + availSearchRQDTO.getEventDateTo());
			return null;
		}
		if (!event.getEventSeries().getActive())
		{
			log.debug("makeEventResult::series is inactive");
			return null;
		}
		log.debug("makeEventResult::check marketing country=" + availSearchRQDTO.getCountryCodeOfOrigin() + " against " + event.getEventSeries().getMarketingCountries());
		log.info("makeEventResult::since countryOfOrigin is NOT collected by eRoam before a search is initiated, this filter is moot");
		// if (event.getEventSeries().getMarketingCountries() != null && event.getEventSeries().getMarketingCountries().length() >
		// 0 && !event.getEventSeries().getMarketingCountries().contains(availSearchRQDTO.getCountryCodeOfOrigin()))
		// {
		// log.debug("makeEventResult::event series not available for country, marketingCountries=" +
		// event.getEventSeries().getMarketingCountries());
		// return null;
		// }
		List<EventTicketAllotment> allotments = makeEventAllotments(event, checkAllotments ? availSearchRQDTO.getTravellers() : 1);
		if (allotments.size() > 0)
		{
			EventResult eventResult = new EventResult();
			eventResult.setId(IMSService.CHANNEL_PREFIX + event.getId());
			eventResult.setChannelId(event.getId().toString());
			eventResult.setChannel(IMSService.CHANNEL);
			eventResult.setExternalEventId(event.getExternalEventId());
			eventResult.setName(event.getName());
			eventResult.setTeamOrPerformer(event.getTeamOrPerformer());
			eventResult.setSeriesId(event.getEventSeries().getId());
			eventResult.setSeriesName(event.getEventSeries().getName());
			eventResult.setType(event.getEventSeries().getEventType().getName());
			eventResult.setSupplierName(event.getEventSupplier().getName());
			eventResult.setStartDate(event.getStartDate().format(dateFormatter));
			eventResult.setEndDate(event.getEndDate().format(dateFormatter));
			if (event.getStartTime() != null)
				eventResult.setStartTime(event.getStartTime().format(timeFormatter));
			eventResult.setOverview(event.getOverview());
			eventResult.setImageUrl(event.getImageUrl());
			eventResult.setTermsAndConditions(event.getTermsAndConditions());
			eventResult.setDefaultSeatmapImageUrl(event.getDefaultSeatmapImageUrl());
			eventResult.setSeatMapNotAvailable(event.getSeatMapNotAvailable());
			eventResult.setEventTicketAllotments(allotments);
			for (EventTicketAllotment eventTicketAllotment : allotments)
			{
				for (EventTicketClassification eventTicketClassification : eventTicketAllotment.getEventTicketClassifications())
				{
					eventResult.setCurrency(eventTicketClassification.getTotalRetailPrice().getCurrencyId());
					if (eventResult.getTotalNetPrice() == null || eventResult.getTotalNetPrice().getAmount().compareTo(eventTicketClassification.getTotalNetPrice().getAmount()) > 0)
					{
						eventResult.setTotalNetPrice(eventTicketClassification.getTotalNetPrice());
						eventResult.setFromNettPrice(eventTicketClassification.getTotalNetPrice().getAmount());
					}
					if (eventTicketClassification.getTotalRetailPrice() != null)
					{
						if (eventResult.getTotalRetailPrice() == null || eventResult.getTotalRetailPrice().getAmount().compareTo(eventTicketClassification.getTotalRetailPrice().getAmount()) > 0)
						{
							eventResult.setTotalRetailPrice(eventTicketClassification.getTotalRetailPrice());
							eventResult.setFromRrpPrice(eventTicketClassification.getTotalRetailPrice().getAmount());
						}
					}
				}
			}
			if (event.getEventSeries().getEventMerchandiseLinks() != null)
			{
				for (EventMerchandiseLink merchlink : event.getEventSeries().getEventMerchandiseLinks())
				{
					EventMerchandiseSalesAPILink l = new EventMerchandiseSalesAPILink();
					l.setMerchandiseId(CHANNEL_PREFIX + merchlink.getMerchandise().getId());
					l.setMandatoryInclusion(merchlink.getMandatoryInclusion());
					eventResult.getEventMerchandiseAPILink().add(l);
				}
			}
			return eventResult;
		}
		log.debug("makeEventResult::no classifications have available allotments");
		return null;
	}

	protected List<EventTicketAllotment> makeEventAllotments(com.torkirion.eroam.ims.datadomain.Event event, int requiredCount)
	{
		log.debug("makeEventAllotments::enter");

		Map<Integer, EventTicketAllotment> allotmentMap = new HashMap<>();
		for (EventClassification c : event.getClassifications())
		{
			EventTicketClassification eventTicketClassification = new EventTicketClassification();
			eventTicketClassification.setClassificationId(c.getId());
			eventTicketClassification.setName(c.getName());
			eventTicketClassification.setCurrency(c.getCurrency());
			eventTicketClassification.setNettPrice(c.getNettPrice());
			eventTicketClassification.setRrpPrice(c.getRrpPrice());
			eventTicketClassification.setTotalNetPrice(new CurrencyValue(c.getCurrency(), c.getNettPrice()));
			eventTicketClassification.setTotalRetailPrice(new CurrencyValue(c.getRrpCurrency(), c.getRrpPrice()));
			eventTicketClassification.setTicketingDescription(c.getTicketingDescription());
			eventTicketClassification.setBundlesOnly(c.getBundlesOnly());
			eventTicketClassification.setAllowInfantIfUnder(c.getAllowInfantIfUnder());
			List<Integer> days = new ArrayList<>();
			if (c.getDays() != null)
			{
				String[] split = c.getDays().split(",");
				for (int i = 0; i < split.length; i++)
				{
					days.add(Integer.parseInt(split[i]));
				}
			}
			log.debug("makeEventAllotments::days=" + days);
			if (!event.getStartDate().equals(event.getEndDate()))
			{
				log.debug("makeEventAllotments::checking days");
				Integer dayCount = 1;
				LocalDate d = event.getStartDate().plusDays(0);
				while (!d.isAfter(event.getEndDate()))
				{
					log.debug("makeEventAllotments::checking array " + days + " for dayCount " + dayCount);
					if (days.contains(dayCount))
					{
						eventTicketClassification.getDates().add(d.format(dateFormatter));
					}
					dayCount++;
					d = d.plusDays(1);
				}
			}
			else
			{
				eventTicketClassification.getDates().add(event.getStartDate().format(dateFormatter));
			}
			EventAllotment eventAllotment = null;
			for (EventAllotment a : event.getAllotments())
			{
				if (a.getId().intValue() == c.getAllotmentId().intValue())
					eventAllotment = a;
			}
			if (eventAllotment == null)
			{
				log.warn("makeEventAllotments::allotment " + c.getAllotmentId() + " not found!");
				continue;
			}
			Set<Integer> multiplePatterns = new HashSet<>();
			if (eventAllotment.getMultiplePattern() != null && eventAllotment.getMultiplePattern().length() > 0)
			{
				String[] split = eventAllotment.getMultiplePattern().split(",");
				for (int i = 0; i < split.length; i++)
				{
					multiplePatterns.add(Integer.parseInt(split[i]));
				}
			}
			for (int i = eventAllotment.getMinimumSale(); i <= eventAllotment.getMaximumSale(); i++)
			{
				log.debug("makeEventAllotments::checking " + i + " > req " + requiredCount + " & <= allot " + eventAllotment.getAllotment().intValue() + " & in " + multiplePatterns);
				if (i >= requiredCount && i <= eventAllotment.getAllotment().intValue())
				{
					if (multiplePatterns.size() == 0 || multiplePatterns.contains(i))
					{
						eventTicketClassification.getAllowedTicketSales().add(i);
					}
				}
			}
			log.debug("makeEventAllotments::allowed tickets=" + eventTicketClassification.getAllowedTicketSales());
			if (eventTicketClassification.getAllowedTicketSales().size() > 0)
			{
				EventTicketAllotment eventTicketAllotment = allotmentMap.get(eventAllotment.getId());
				if (eventTicketAllotment == null)
				{
					eventTicketAllotment = new EventTicketAllotment();
					eventTicketAllotment.setAllotmentId(eventAllotment.getId());
					eventTicketAllotment.setName(eventAllotment.getName());
					eventTicketAllotment.setAllotment(eventAllotment.getAllotment());
					eventTicketAllotment.setOnRequest(eventAllotment.getOnRequest());
					allotmentMap.put(eventAllotment.getId(), eventTicketAllotment);
				}
				eventTicketAllotment.getEventTicketClassifications().add(eventTicketClassification);
			}
		}
		List<EventTicketAllotment> allotments = new ArrayList<>();
		for (EventTicketAllotment a : allotmentMap.values())
		{
			if (a.getEventTicketClassifications().size() > 0)
				allotments.add(a);
		}
		return allotments;
	}

	protected Venue makeVenue(com.torkirion.eroam.ims.datadomain.EventVenue eventVenue)
	{
		Venue venue = new Venue();
		BeanUtils.copyProperties(eventVenue, venue);
		venue.setAddress(new Address());
		BeanUtils.copyProperties(eventVenue.getAddress(), venue.getAddress());
		if (eventVenue.getAddress() != null)
		{
			venue.getAddress().setGeoCoordinates(new GeoCoordinates());
			BeanUtils.copyProperties(eventVenue.getAddress().getGeoCoordinates(), venue.getAddress().getGeoCoordinates());
		}
		return venue;
	}

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}
}
