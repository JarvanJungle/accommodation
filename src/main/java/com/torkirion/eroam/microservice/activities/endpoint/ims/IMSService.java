package com.torkirion.eroam.microservice.activities.endpoint.ims;

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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.apidomain.Address;
import com.torkirion.eroam.ims.apidomain.DaysOfTheWeek;
import com.torkirion.eroam.ims.apidomain.GeoCoordinates;
import com.torkirion.eroam.ims.apidomain.Activity.HotelPickup;
import com.torkirion.eroam.ims.apidomain.ActivityOption.ActivityOptionBlock;
import com.torkirion.eroam.ims.apidomain.ActivityOption.ActivityOptionPriceBand;
import com.torkirion.eroam.ims.datadomain.Activity;
import com.torkirion.eroam.ims.datadomain.ActivityAllotment;
import com.torkirion.eroam.ims.datadomain.ActivityDepartureTime;
import com.torkirion.eroam.ims.datadomain.ActivityOption;
import com.torkirion.eroam.ims.datadomain.ActivitySale;
import com.torkirion.eroam.ims.datadomain.ActivitySupplierAgeBand;
import com.torkirion.eroam.ims.datadomain.EventSale;
import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.ims.services.MapperService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationResult;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityDeparture;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityRC;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityResult;
import com.torkirion.eroam.microservice.activities.apidomain.BookingAnswers;
import com.torkirion.eroam.microservice.activities.apidomain.BookingQuestion;
import com.torkirion.eroam.microservice.activities.apidomain.BookingQuestionList;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ.ActivityRequestItem;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRS;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBooking.ItemStatus;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityCancelRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityCancelRS;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityRC.Image;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByActivityIdRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.activities.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class IMSService implements ActivityServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private DataService imsDataService;

	private MapperService mapperService;

	public static final String CHANNEL = "LOCALIMS";

	public static final String CHANNEL_PREFIX = "IM";

	@Override
	public void initiateRCLoad(String code)
	{
		log.debug("initiateRCLoad::none required");
	}
	
	public Set<ActivityResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		Set<ActivityResult> results = new HashSet<>();
		try
		{
			List<Activity> allActivities = imsDataService.getActivityRepo().findAll();
			log.debug("search::testing " + allActivities.size() + " activities");
			for (Activity activityData : allActivities)
			{
				if (activityData.getGeoCoordinates() != null && activityData.getGeoCoordinates().getLatitude() != null && activityData.getGeoCoordinates().getLongitude() != null)
				{
					log.debug("search::testing activity " + activityData.getId() + " : latitude " + activityData.getGeoCoordinates().getLatitude() + " longitude "
							+ activityData.getGeoCoordinates().getLongitude());
					if (activityData.getGeoCoordinates().getLatitude().compareTo(availSearchRQ.getNorthwest().getLatitude()) < 0
							&& activityData.getGeoCoordinates().getLatitude().compareTo(availSearchRQ.getSoutheast().getLatitude()) > 0
							&& activityData.getGeoCoordinates().getLongitude().compareTo(availSearchRQ.getNorthwest().getLongitude()) > 0
							&& activityData.getGeoCoordinates().getLongitude().compareTo(availSearchRQ.getSoutheast().getLongitude()) < 0)
					{
						log.debug("search::found candidate venue:" + activityData.getName());
						ActivityResult activityResult = makeActivityResult(activityData, availSearchRQ, false);
						if (activityResult != null)
						{
							results.add(activityResult);
						}
					}
				}
				else
				{
					log.debug("search::activity " + activityData.getId() + " has no geocordinates");
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

	public Set<ActivityResult> searchByActivityId(AvailSearchByActivityIdRQDTO availSearchRQ)
	{
		log.info("searchByActivityId::searchByActivityId(AvailSearchByActivityIdRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		Set<ActivityResult> results = new HashSet<>();
		try
		{
			for (String id : availSearchRQ.getActivityIds())
			{
				Integer activityId = null;
				if (id.startsWith(CHANNEL_PREFIX))
					activityId = Integer.parseInt(id.substring(CHANNEL_PREFIX.length()));
				else
					activityId = Integer.parseInt(id);
				Optional<Activity> activityOpt = imsDataService.getActivityRepo().findById(activityId);
				if (activityOpt.isPresent())
				{
					ActivityResult activityResult = makeActivityResult(activityOpt.get(), availSearchRQ, false);
					if (activityResult != null)
					{
						results.add(activityResult);
					}
				}
				else
				{
					log.debug("searchByActivityId::activity " + id + " not found");
				}
			}
			return results;
		}
		catch (Exception e)
		{
			log.error("searchByActivityId::threw exception " + e.toString(), e);
		}
		log.info("searchByActivityId::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	@Override
	public ActivityResult rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck()::enter for " + rateCheckRQDTO);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(rateCheckRQDTO.getClient(), 1);
		Callable<ActivityResult> callableTask = () -> {
		    return rateCheckThreaded(rateCheckRQDTO);
		};
		Future<ActivityResult> future = threadPoolExecutor.submit(callableTask);
		ActivityResult activityResult = future.get(30, TimeUnit.SECONDS);
		return activityResult;
	}
	
	protected ActivityResult rateCheckThreaded(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter for " + rateCheckRQDTO);

		Integer activityId = null;
		if (rateCheckRQDTO.getActivityId().startsWith(CHANNEL_PREFIX))
			activityId = Integer.parseInt(rateCheckRQDTO.getActivityId().substring(CHANNEL_PREFIX.length()));
		else
			activityId = Integer.parseInt(rateCheckRQDTO.getActivityId());
		Optional<Activity> activityOpt = imsDataService.getActivityRepo().findById(activityId);
		if (activityOpt.isPresent())
		{
			ActivityResult activityResult = makeActivityResult(activityOpt.get(), rateCheckRQDTO, false);
			for (ActivityDeparture departure : activityResult.getDepartures())
			{
				if (departure.getDepartureId().equals(rateCheckRQDTO.getDepartureId()))
				{
					for (com.torkirion.eroam.microservice.activities.apidomain.ActivityOption option : departure.getOptions())
					{
						if (option.getOptionId().equals(rateCheckRQDTO.getOptionId()))
						{
							if (log.isDebugEnabled())
								log.debug("rateCheck::found matching, return");
							return activityResult;
						}
					}
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("rateCheck::did NOT find matching, return null");
		return null;
	}

	@Override
	public ActivityBookRS book(String client, ActivityBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book()::recevied " + bookRQ);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<ActivityBookRS> callableTask = () -> {
		    return bookThreaded(client, bookRQ);
		};
		Future<ActivityBookRS> future = threadPoolExecutor.submit(callableTask);
		ActivityBookRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	
	protected ActivityBookRS bookThreaded(String client, ActivityBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);
		long timer1 = System.currentTimeMillis();
		ActivityBookRS bookRS = new ActivityBookRS();
		bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
		Date today = Functions.normaliseDate(new Date());
		try
		{
			for (ActivityRequestItem item : bookRQ.getItems())
			{
				RateCheckRQDTO rateCheckRQDTO = new RateCheckRQDTO();
				rateCheckRQDTO.setActivityDate(item.getDate());
				rateCheckRQDTO.setActivityId(item.getActivityId());
				rateCheckRQDTO.setChannel(item.getChannel());
				rateCheckRQDTO.setCountryCodeOfOrigin(bookRQ.getCountryCodeOfOrigin());
				rateCheckRQDTO.setDepartureId(item.getDepartureId());
				rateCheckRQDTO.setOptionId(item.getOptionId());
				rateCheckRQDTO.setTravellers(new TravellerMix());
				rateCheckRQDTO.getTravellers().setAdultCount(0);
				rateCheckRQDTO.getTravellers().setChildAges(new ArrayList<>());
				int travellerIndex = 0;
				StringBuffer ageList = new StringBuffer();
				for (Traveller traveller : bookRQ.getTravellers())
				{
					for (Integer requiredIndex : item.getTravellerIndex())
					{
						if (requiredIndex.intValue() == travellerIndex)
						{
							int travellerAge = traveller.getAge(today);
							if (travellerAge > 18)
								rateCheckRQDTO.getTravellers().setAdultCount(rateCheckRQDTO.getTravellers().getAdultCount() + 1);
							else
								rateCheckRQDTO.getTravellers().getChildAges().add(travellerAge);
							if (ageList.length() > 0)
								ageList.append(",");
							ageList.append(travellerAge);

						}
					}
				}
				int itemCount = rateCheckRQDTO.getTravellers().getAdultCount() + rateCheckRQDTO.getTravellers().getChildAges().size();
				ActivityResult activityResult = rateCheck(rateCheckRQDTO);
				if (activityResult == null)
				{
					if (log.isDebugEnabled())
						log.debug("book::cannot complete booking for " + rateCheckRQDTO);
					throw new Exception("Activity unavailable");
				}
				com.torkirion.eroam.microservice.activities.apidomain.ActivityDeparture chosenDeparture = null;
				com.torkirion.eroam.microservice.activities.apidomain.ActivityOption chosenOption = null;
				for (ActivityDeparture departure : activityResult.getDepartures())
				{
					if (departure.getDepartureId().equals(rateCheckRQDTO.getDepartureId()))
					{
						chosenDeparture = departure;
						for (com.torkirion.eroam.microservice.activities.apidomain.ActivityOption option : departure.getOptions())
						{
							if (option.getOptionId().equals(rateCheckRQDTO.getOptionId()))
							{
								chosenOption = option;
								if (log.isDebugEnabled())
									log.debug("rateCheck::found matching");
								if (!option.getNettPrice().equals(item.getSupplyRate()))
								{
									if (log.isDebugEnabled())
										log.debug("book::supply rate change for " + rateCheckRQDTO + " optionPrice=" + option.getNettPrice() + ", RQ supplyRate=" + item.getSupplyRate());
									throw new Exception("Activity price change");
								}
							}
						}
					}
				}
				Integer activityId = null;
				if (item.getActivityId().startsWith(CHANNEL_PREFIX))
					activityId = Integer.parseInt(item.getActivityId().substring(CHANNEL_PREFIX.length()));
				else
					activityId = Integer.parseInt(item.getActivityId());
				Optional<Activity> activityOpt = imsDataService.getActivityRepo().findById(activityId);
				Activity activity = activityOpt.get();

				List<ActivityAllotment> allotments = null;
				if (activity.getAllotmentByDepartureAndOption())
				{
					// look for allotment by departureANDOption
					allotments = imsDataService.getActivityAllotmentRepo().findByActivityIdAndOptionIdAndDepartureTimeIdOrderByAllotmentDate(activity.getId(),
							Integer.parseInt(chosenOption.getOptionId()), Integer.parseInt(chosenDeparture.getDepartureId()));
				}
				else
				{
					// look for allotment by option
					allotments = imsDataService.getActivityAllotmentRepo().findByActivityIdAndOptionIdAndDepartureTimeIdOrderByAllotmentDate(activity.getId(),
							Integer.parseInt(chosenOption.getOptionId()), null);
				}
				for (ActivityAllotment activityAllotment : allotments)
				{
					if (activityAllotment.getAllotmentDate().equals(item.getDate()))
					{
						activityAllotment.setAllotment(activityAllotment.getAllotment().intValue() - itemCount);
						imsDataService.getActivityAllotmentRepo().save(activityAllotment);
						break;
					}
				}

				ActivitySale sale = new ActivitySale();
				sale.setActivity(activity);
				sale.setActivityDate(item.getDate());
				sale.setBookingDateTime(LocalDateTime.now());
				sale.setName(activity.getName());
				sale.setCurrency(chosenOption.getNettPrice().getCurrencyId());
				sale.setNettPrice(chosenOption.getNettPrice().getAmount());
				sale.setRrpCurrency(chosenOption.getRrpPrice().getCurrencyId());
				sale.setRrpPrice(chosenOption.getRrpPrice().getAmount());
				sale.setItemStatus(ActivitySale.ItemStatus.BOOKED);
				sale.setDepartureTimeId(Integer.parseInt(chosenDeparture.getDepartureId()));
				sale.setDepartureTimeName(chosenDeparture.getDepartureName());
				sale.setOptionId(Integer.parseInt(chosenOption.getOptionId()));
				sale.setOptionName(chosenOption.getOptionName());
				sale.setCount(itemCount);
				sale.setAgeList(ageList.toString());
				sale.setCountryCodeOfOrigin(bookRQ.getCountryCodeOfOrigin());
				sale.setTitle(bookRQ.getBooker().getTitle());
				sale.setGivenName(bookRQ.getBooker().getGivenName());
				sale.setSurname(bookRQ.getBooker().getSurname());
				sale.setTelephone(bookRQ.getBooker().getTelephone());
				sale.setInternalBookingReference(bookRQ.getInternalBookingReference());
				sale.setInternalItemReference(item.getInternalItemReference());
				if (item.getBookingQuestionAnswers() != null && item.getBookingQuestionAnswers().size() > 0)
				{
					StringBuffer buf = new StringBuffer();
					for (BookingAnswers bookingQuestionAnswer : item.getBookingQuestionAnswers())
					{
						if (buf.length() > 0)
							buf.append(", ");
						buf.append(bookingQuestionAnswer.getQuestionId() + ":" + bookingQuestionAnswer.getAnswer());
					}
					sale.setBookingQuestionAnswers(buf.toString());
				}
				sale = imsDataService.getActivitySaleRepo().save(sale);
				activity.getSales().add(sale);
				imsDataService.getActivityRepo().save(activity);
				if (log.isDebugEnabled())
					log.debug("book::saved sale : " + sale);

				ActivityBookRS.ActivityResponseItem responseItem = new ActivityBookRS.ActivityResponseItem();
				responseItem.setChannel(CHANNEL);
				responseItem.setBookingItemReference(sale.getId().toString());
				responseItem.setInternalItemReference(item.getInternalItemReference());
				responseItem.setItemStatus(ItemStatus.BOOKED);
				responseItem.setBookingQuestionAnswers(item.getBookingQuestionAnswers());
				bookRS.getItems().add(responseItem);
				if (bookRS.getBookingReference() == null)
					bookRS.setBookingReference(responseItem.getBookingItemReference());
				if (log.isDebugEnabled())
					log.debug("book::responseItem=" + responseItem);
			}
		}
		catch (Exception e)
		{
			log.error("book::threw exception " + e.toString(), e);
			ResponseExtraInformation error = new ResponseExtraInformation("500", e.getMessage());
			bookRS.getErrors().add(error);
		}
		log.info("book:: time taken = " + (System.currentTimeMillis() - timer1));
		return bookRS;
	}

	@Override
	public ActivityCancelRS cancel(String client, ActivityCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel()::recevied " + cancelRQ);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<ActivityCancelRS> callableTask = () -> {
		    return cancelThreaded(client, cancelRQ);
		};
		Future<ActivityCancelRS> future = threadPoolExecutor.submit(callableTask);
		ActivityCancelRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}

	protected ActivityCancelRS cancelThreaded(String client, ActivityCancelRQ cancelRQ) throws Exception
	{
		return null;
	}

	/*
	 * protected void validateItem(EventRequestItem bookingItem, com.torkirion.eroam.microservice.ims.datadomain.Event eventData,
	 * com.torkirion.eroam.microservice.ims.datadomain.EventClassification eventClassification,
	 * com.torkirion.eroam.microservice.ims.datadomain.EventAllotment eventAllotment) throws Exception { if (log.isDebugEnabled())
	 * log.debug("validateItem::enter"); if (eventAllotment == null) { throw new Exception("Allotment not found"); } if
	 * (bookingItem.getNumberOfTickets().intValue() > eventAllotment.getAllotment().intValue()) { throw new
	 * Exception("Allotment no longer available, requested " + 0 + " less than available " + eventAllotment.getAllotment()); }
	 * Set<Integer> multiplePatterns = new HashSet<>(); if (eventAllotment.getMultiplePattern() != null) { String[] split =
	 * eventAllotment.getMultiplePattern().split(","); for (int i = 0; i < split.length; i++) {
	 * multiplePatterns.add(Integer.parseInt(split[i])); } } if (bookingItem.getNumberOfTickets().intValue() <
	 * eventAllotment.getMinimumSale().intValue() || bookingItem.getNumberOfTickets().intValue() > eventAllotment.getMaximumSale()
	 * || (multiplePatterns.size() > 0 && !multiplePatterns.contains(bookingItem.getNumberOfTickets()))) { throw new
	 * Exception("A purchase of " + bookingItem.getNumberOfTickets() + " tickets it not permitted"); } if
	 * (bookingItem.getSupplyRate().getAmount().compareTo(eventClassification.getNettPrice()) != 0) { throw new
	 * Exception("Price has changed"); } if (log.isDebugEnabled()) log.debug("validateItem::ok"); }
	 */
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

	protected ActivityResult makeActivityResult(com.torkirion.eroam.ims.datadomain.Activity activity, AvailSearchRQDTO availSearchRQDTO, boolean checkAllotments)
	{
		log.debug("makeActivityResult::enter for " + activity.getName());

		int numberAllotmentRequired = availSearchRQDTO.getTravellers().getAdultCount()
				+ (availSearchRQDTO.getTravellers().getChildAges() != null ? availSearchRQDTO.getTravellers().getChildAges().size() : 0);
		// check pricing and allotment

		ActivityResult activityResult = new ActivityResult();
		com.torkirion.eroam.ims.apidomain.Activity apiActivity = null;
		try
		{
			apiActivity = mapperService.mapActivity(activity);
			activityResult.setActivityRC(makeRC(activity));
		}
		catch (Exception e)
		{
			log.warn("makeActivityResult::caught exception " + e.toString(), e);
			return null;
		}

		LocalDate dateFrom = availSearchRQDTO.getActivityDateFrom() == null ? availSearchRQDTO.getActivityDate() : availSearchRQDTO.getActivityDateFrom();

		LocalDate dateTo = availSearchRQDTO.getActivityDateTo() == null ? availSearchRQDTO.getActivityDate() : availSearchRQDTO.getActivityDateTo();

		// for each date in the search, see if we have availability and pricing
		LocalDate searchDate = dateFrom;
		while (!searchDate.isAfter(dateTo))
		{
			log.debug("makeActivityResult::testing " + searchDate);
			for (ActivityDepartureTime departureTime : activity.getDepartureTimes())
			{
				ActivityDeparture activityDeparture = new ActivityDeparture();
				activityDeparture.setDepartureId(departureTime.getId().toString());
				activityDeparture.setDate(searchDate);
				activityDeparture.setDepartureName(departureTime.getName());
				activityDeparture.setDepartureTime(departureTime.getDepartureTime());
				option: for (ActivityOption option : activity.getOptions())
				{
					com.torkirion.eroam.microservice.activities.apidomain.ActivityOption activityOption = new com.torkirion.eroam.microservice.activities.apidomain.ActivityOption();
					activityOption.setOptionId(option.getId().toString());
					activityOption.setOptionName(option.getName());
					activityOption.setBundlesOnly(option.getBundlesOnly());
					if (apiActivity.getHotelPickup() != null && apiActivity.getHotelPickup() && apiActivity.getHotelPickups() != null)
					{
						BookingQuestionList bookingQuestionList = new BookingQuestionList();
						bookingQuestionList.setFreeFormatAllowed(true);
						bookingQuestionList.setFreeFormatValue("or enter another hotel");
						bookingQuestionList.setPerTraveller(false);
						bookingQuestionList.setQuestionId("HOTELPICKUP");
						bookingQuestionList.setQuestionText("Choose hotel pickup");
						bookingQuestionList.setQuestionType(BookingQuestion.QuestionType.LIST);
						for (HotelPickup hotelPickup : apiActivity.getHotelPickups())
						{
							bookingQuestionList.getValidValues().add(hotelPickup.getHotelName());
						}
						activityOption.getBookingQuestions().add(bookingQuestionList);
					}
					List<ActivityAllotment> allotments = null;
					log.debug("makeActivityResult::looking for allotments for departure " + departureTime.toString() + " option " + option.toString());
					if (activity.getAllotmentByDepartureAndOption())
					{
						// look for allotment by departureANDOption
						allotments = imsDataService.getActivityAllotmentRepo().findByActivityIdAndOptionIdAndDepartureTimeIdOrderByAllotmentDate(activity.getId(), option.getId(),
								departureTime.getId());
					}
					else
					{
						// look for allotment by option
						allotments = imsDataService.getActivityAllotmentRepo().findByActivityIdAndOptionIdAndDepartureTimeIdOrderByAllotmentDate(activity.getId(), option.getId(), null);
					}
					if (allotments == null)
					{
						log.debug("makeActivityResult::no allotments found");
						continue option;
					}
					boolean allotmentAvailable = false;
					for (ActivityAllotment activityAllotment : allotments)
					{
						if (activityAllotment.getAllotmentDate().equals(searchDate))
						{
							// check availability
							if (activityAllotment.getAllotment().intValue() < numberAllotmentRequired)
							{
								log.debug("makeActivityResult::available " + activityAllotment.getAllotment() + " less than required " + numberAllotmentRequired);
								continue option;
							}
							else
								allotmentAvailable = true;
						}
					}
					if (!allotmentAvailable)
					{
						log.debug("makeActivityResult::allotment date not found");
						continue option;
					}
					// check pricing available
					com.torkirion.eroam.ims.apidomain.ActivityOption apiOption = null;
					try
					{
						apiOption = mapperService.mapActivityOption(option);
					}
					catch (Exception e)
					{
						log.debug("makeActivityResult::caught exception " + e.toString(), e);
						continue option;
					}
					CurrencyValue nett = null;
					CurrencyValue rrp = null;
					List<Integer> ages = new ArrayList<>();
					for (int i = 0; i < availSearchRQDTO.getTravellers().getAdultCount(); i++)
					{
						ages.add(30);
					}
					if (availSearchRQDTO.getTravellers().getChildAges() != null)
						ages.addAll(availSearchRQDTO.getTravellers().getChildAges());
					for (Integer age : ages)
					{
						boolean agePriceFound = false;
						if (log.isDebugEnabled())
							log.debug("makeActivityResult::looking for age " + age);
						for (ActivityOptionBlock ageBand : apiOption.getPriceBlocks())
						{
							if (!searchDate.isAfter(ageBand.getToDate()) && !searchDate.isBefore(ageBand.getFromDate()) && daysOfTheWeekMatch(searchDate, ageBand.getDaysOfTheWeek()))
							{
								if (log.isDebugEnabled())
									log.debug("makeActivityResult::found ageBand in date range:" + ageBand);
								for (Entry<Integer, ActivityOptionPriceBand> priceBand : ageBand.getPriceBands().entrySet())
								{
									for (ActivitySupplierAgeBand ageBandDefinition : activity.getActivitySupplier().getAgebands())
									{
										if (log.isDebugEnabled())
											log.debug("makeActivityResult::looking for definition for " + priceBand + ", checking def " + ageBandDefinition);
										if (ageBandDefinition.getId().intValue() == priceBand.getKey().intValue())
										{
											if (log.isDebugEnabled())
												log.debug("makeActivityResult::found ageBandDefinition!:" + ageBandDefinition);
											if (age <= ageBandDefinition.getMaxAge().intValue() && age >= ageBandDefinition.getMinAge())
											{
												if (nett == null)
													nett = new CurrencyValue(priceBand.getValue().getCurrency(), priceBand.getValue().getNettPrice());
												else
													nett = new CurrencyValue(priceBand.getValue().getCurrency(), priceBand.getValue().getNettPrice().add(nett.getAmount()));
												if (rrp == null)
													rrp = new CurrencyValue(priceBand.getValue().getRrpCurrency(), priceBand.getValue().getRrpPrice());
												else
													rrp = new CurrencyValue(priceBand.getValue().getRrpCurrency(), priceBand.getValue().getRrpPrice().add(rrp.getAmount()));
												agePriceFound = true;
											}
										}
									}
								}
							}
						}
						if (!agePriceFound)
						{
							if (log.isDebugEnabled())
								log.debug("makeActivityResult::no pricing found for age " + age);
							continue option;
						}
					}
					activityOption.setNettPrice(nett);
					activityOption.setRrpPrice(rrp);
					for (ActivityOptionBlock ageBand : apiOption.getPriceBlocks())
					{
						for (ActivityOptionPriceBand priceBand : ageBand.getPriceBands().values())
						{
							if (log.isDebugEnabled())
								log.debug("makeActivityResult::making band from " + priceBand);
							com.torkirion.eroam.microservice.activities.apidomain.ActivityOption.ActivityOptionPriceBand activityOptionPriceBand = new com.torkirion.eroam.microservice.activities.apidomain.ActivityOption.ActivityOptionPriceBand();
							activityOptionPriceBand.setAgeBandName(priceBand.getAgeBandName());
							activityOptionPriceBand.setNettPrice(new CurrencyValue(priceBand.getCurrency(), priceBand.getNettPrice()));
							activityOptionPriceBand.setRrpPrice(new CurrencyValue(priceBand.getRrpCurrency(), priceBand.getRrpPrice()));
							activityOptionPriceBand.setAgeBandName(priceBand.getAgeBandName());
							activityOption.getPricePer().add(activityOptionPriceBand);
						}

					}
					activityDeparture.getOptions().add(activityOption);
				}
				if (activityDeparture.getOptions().size() > 0)
					activityResult.getDepartures().add(activityDeparture);
				else
				{
					if (log.isDebugEnabled())
						log.debug("makeActivityResult::no options, returning null");
				}
			}
			searchDate = searchDate.plusDays(1);
		}

		if (activityResult.getDepartures().size() > 0)
			return activityResult;
		else
		{
			if (log.isDebugEnabled())
				log.debug("makeActivityResult::no departures, returning null");
			return null;
		}
	}

	private ActivityRC makeRC(com.torkirion.eroam.ims.datadomain.Activity activity) throws Exception
	{
		com.torkirion.eroam.ims.apidomain.Activity apiActivity = mapperService.mapActivity(activity);
		ActivityRC activityRC = new ActivityRC();
		activityRC.setCode(CHANNEL_PREFIX + activity.getId().toString());
		activityRC.setChannelCode(activity.getId().toString());
		activityRC.setChannel(CHANNEL);
		activityRC.setSupplierName("LocalIMS");
		activityRC.setActivityName(activity.getName());
		activityRC.setGeoCoordinates(new LatitudeLongitude());
		activityRC.getGeoCoordinates().setLatitude(activity.getGeoCoordinates().getLatitude());
		activityRC.getGeoCoordinates().setLongitude(activity.getGeoCoordinates().getLongitude());
		// activityRC.setProductType();
		// activityRC.setDuration();
		activityRC.setDurationText(activity.getDuration());
		// activityRC.setFromPerPerson(fromPerPerson);
		activityRC.setOperator(activity.getOperator());
		activityRC.setDeparturePoint(activity.getDeparturePoint());
		activityRC.setHotelPickupAvailable(activity.getHotelPickup());
		if (activity.getHotelPickup() != null && activity.getHotelPickup())
		{
			List<String> pickups = new ArrayList<>();
			for (HotelPickup pickup : apiActivity.getHotelPickups())
			{
				pickups.add(pickup.getHotelName());
			}
			activityRC.setHotelPickupList(pickups);
		}
		activityRC.setOverview(activity.getOverview());
		// activityRC.setIntroduction();
		// activityRC.setItinearyHighlights(itinearyHighlights);
		activityRC.setInclusions(listToString(apiActivity.getInclusions()));
		activityRC.setExclusions(listToString(apiActivity.getExclusions()));
		activityRC.setAdditionalInformation(listToString(apiActivity.getAdditionalInformation()));
		activityRC.setVoucherInformation(activity.getVoucherInformation());
		// activityRC.setLocalOperatorInformation(localOperatorInformation);
		activityRC.setCancellationPolicyOverview(apiActivity.getTermsAndConditions());
		// activityRC.setCountryCode(countryCode);
		activityRC.setImages(new ArrayList<>());
		for (String imageUrl : apiActivity.getImages())
		{
			ActivityRC.Image i = new ActivityRC.Image();
			i.setImageURL(imageUrl);
			activityRC.getImages().add(i);
		}
		return activityRC;
	}

	private String listToString(List<String> l)
	{
		StringBuffer buf = new StringBuffer();
		for (String s : l)
		{
			if (buf.length() > 0)
				buf.append(".");
			buf.append(s);
		}
		return buf.toString();
	}

	private boolean daysOfTheWeekMatch(LocalDate d, DaysOfTheWeek dow)
	{
		switch (d.getDayOfWeek())
		{
			case SUNDAY:
				if (dow.getSunday())
					return true;
				else
					break;
			case MONDAY:
				if (dow.getMonday())
					return true;
				else
					break;
			case TUESDAY:
				if (dow.getTuesday())
					return true;
				else
					break;
			case WEDNESDAY:
				if (dow.getWednesday())
					return true;
				else
					break;
			case THURSDAY:
				if (dow.getThursday())
					return true;
				else
					break;
			case FRIDAY:
				if (dow.getFriday())
					return true;
				else
					break;
			case SATURDAY:
				if (dow.getSaturday())
					return true;
				else
					break;
		}
		return false;
	}

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}
}
