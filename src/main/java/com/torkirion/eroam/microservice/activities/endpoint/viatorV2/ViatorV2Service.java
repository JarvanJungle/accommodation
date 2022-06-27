package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import com.torkirion.eroam.ims.apidomain.DaysOfTheWeek;
import com.torkirion.eroam.ims.services.MapperService;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ.ActivityRequestItem;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRS;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBooking;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityCancelRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityCancelRS;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityDeparture;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityOption;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityResult;
import com.torkirion.eroam.microservice.activities.apidomain.BookingAnswers;
import com.torkirion.eroam.microservice.activities.apidomain.BookingQuestion;
import com.torkirion.eroam.microservice.activities.apidomain.BookingQuestion.QuestionType;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCData;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCRepo;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByActivityIdRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.activities.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailablityCheck;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailablityCheckResult;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailablityCheckResult.BookableItem;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.CancelVerificationValue;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ConfirmBooking;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ConfirmHoldBooking;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.HoldBooking;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.HoldBookingResponse;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.PaxMix;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartCancelRQ;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartCancelRS;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartCheckingCancelRQ;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartCheckingCancelRS;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartConfirmBookingRS;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ViatorResult.LineItem;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class ViatorV2Service implements ActivityServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private MapperService mapperService;

	private final ActivityRCRepo activityRCRepo;

	private ViatorV2ScheduleDataRepo activityScheduleRepo;

	private ViatorV2UnavailableDataRepo activityUnavailableRepo;

	private ViatorV2ActivityRepo viatorV2ActivityRepo;

	private ViatorV2ActivityProductOptionRepo viatorV2ActivityProductOptionRepo;

	private BookingQuestionRepo viatorV2BookingQuestionRepo;

	private ViatorV2Controller viatorV2Controller;

	public static final String CHANNEL = "VIATORV2";

	public static final String CHANNEL_PREFIX = "V2";

	private static final String SITE_DEFAULT = "eroam";

	public static final LocalTime NO_TIME = LocalTime.parse("00:01");

	private static final int MAX_RESPONSE = 30;

	public Set<ActivityResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		Set<ActivityResult> results = new HashSet<>();
		try
		{
			List<ViatorV2ScheduleData> boxedSchedules = activityScheduleRepo.findByGeoboxAndDate(availSearchRQ.getNorthwest().getLatitude(), availSearchRQ.getNorthwest().getLongitude(),
					availSearchRQ.getSoutheast().getLatitude(), availSearchRQ.getSoutheast().getLongitude(),
					availSearchRQ.getActivityDateFrom(), availSearchRQ.getActivityDateTo());
			log.debug("search::" + boxedSchedules.size() + " latlong filtered activities");
			List<String> codes = getAllProductCodesFromViatorSchedules(boxedSchedules);
			log.debug("search::codes:size: {}", codes.size());
			List<ActivityRCData> boxedRC = activityRCRepo.findAllByCodeIn(convertProductCodeToCode(codes));
			//loadViatorV2Activity(codes);
			if (boxedRC.size() > 0) {
				/*
					Load ViatorV2Activity into cache to caculate optional prices
				 */
				results = makeActivityResults(boxedRC, boxedSchedules, availSearchRQ);
			}
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
		log.debug("searchByActivityId::searchByActivityId(AvailSearchByActivityIdRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		Set<ActivityResult> results = new HashSet<>();
		try
		{
			List<ActivityRCData> rcs = new ArrayList<ActivityRCData>();
			for (String productCode : availSearchRQ.getActivityIds())
			{
				String channelCode = productCode.startsWith(CHANNEL_PREFIX) ? productCode.substring(CHANNEL_PREFIX.length()) : productCode;
				List<ActivityRCData> rList = activityRCRepo.findByChannelAndChannelCode(ViatorV2Service.CHANNEL, channelCode);
				rcs.addAll(rList);
			}

			log.debug("search::" + rcs.size() + " code filtered activities");
			if (rcs.size() > 0)
				results = makeActivityResults(rcs, availSearchRQ);
		}
		catch (Exception e)
		{
			log.error("searchByActivityId::threw exception " + e.toString(), e);
		}
		log.info("searchByActivityId::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	public ActivityResult rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		String productCode = makeProductCodeFromActivityId(rateCheckRQDTO.getActivityId());
		ViatorV2Activity viatorSpecificData = getViatorV2Activity(productCode);
		if (viatorSpecificData == null)
		{
			log.warn("rateCheck::productCode " + productCode + " not found in local tables");
			return null;
		}
		String currency = propertiesDAO.getProperty(rateCheckRQDTO.getClient(), CHANNEL.toString(), "currency");
		AvailablityCheck availablityCheck = new AvailablityCheck(productCode, rateCheckRQDTO, viatorSpecificData, currency);
		ViatorV2Interface viatorV2Interface = new ViatorV2Interface(propertiesDAO, rateCheckRQDTO.getClient());
		AvailablityCheckResult availablityCheckResult = viatorV2Interface.startAvailabilityCheck(availablityCheck);
		return makeActivityResult(availablityCheckResult, rateCheckRQDTO);
	}

	@Override
	public ActivityBookRS book(String client, ActivityBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);
		long timer1 = System.currentTimeMillis();
		ActivityBookRS bookRS = processBook(bookRQ, client);
		log.info("book:: time taken = " + (System.currentTimeMillis() - timer1));
		return bookRS;
	}

	@Override
	public void initiateRCLoad(String code) {
		log.debug("initiateRCLoad::enter");
		try {
			if ( "LOOKUPS".equals(code)) {
				viatorV2Controller.startLookups();
			}
			if ( "ACTIVITIES".equals(code)) {
				viatorV2Controller.startActivities();
			}
		}
		catch (Exception e) {
			log.warn("initiateRCLoad::caught " + e.toString(), e);
		}
	}

	@Override
	public ActivityCancelRS cancel(String client, ActivityCancelRQ cancelRQ) throws Exception {
		if(cancelRQ.getBookingReference() == null || "".equals(cancelRQ.getBookingReference())) {
			throw new Exception("bookingReference is empty");
		}
		String[] bookingItemReferences = cancelRQ.getBookingReference().split("\\|");

		ViatorV2Interface viatorV2Interface = new ViatorV2Interface(propertiesDAO, client);

		ActivityCancelRS activityCancelRS = new ActivityCancelRS("", new CurrencyValue());
		CancelVerificationValue cancelVerification = checkBeforeCancel(viatorV2Interface, bookingItemReferences);
		if(!cancelVerification.isCancelable()) {
			for(String error : cancelVerification.getErrors()) {
				activityCancelRS.getErrors().add(new ResponseExtraInformation("401", error));
			}
			return activityCancelRS;
		}
		for(String bookingItemReference : bookingItemReferences) {
			StartCancelRS startCancelRS = viatorV2Interface.startCancel(new StartCancelRQ(bookingItemReference));
		}
		return new ActivityCancelRS(client + "_" + cancelRQ.getBookingReference(), cancelVerification.getCancelationFee());
	}

	private CancelVerificationValue checkBeforeCancel(ViatorV2Interface viatorV2Interface, String[] bookingItemReferences) {
		log.debug("checkBeforeCancel::bookingItemReferences: ", bookingItemReferences);
		double totalCharge = 0;
		List<String> errors = new ArrayList<>();
		boolean isCancelable = true;
		String currency = "";
		for(String bookingItemReference : bookingItemReferences) {
			StartCheckingCancelRS startCheckingCancelRS = viatorV2Interface.startCheckingCancel(new StartCheckingCancelRQ(bookingItemReference));
			if("CANCELLED".equals(startCheckingCancelRS.getStatus())) {
				continue;
			}
			if("CANCELLABLE".equals(startCheckingCancelRS.getStatus())) {
				totalCharge = totalCharge + (startCheckingCancelRS.getRefundDetails().getItemPrice() - startCheckingCancelRS.getRefundDetails().getRefundAmount());
				currency = startCheckingCancelRS.getRefundDetails().getCurrencyCode();
			} else {
				if(startCheckingCancelRS.getMessage() != null) {
					errors.add(startCheckingCancelRS.getMessage());
 				}  else {
					errors.add("Cancel error, Please try again or make a phone call");
				}
				isCancelable = false;
			}
		}
		CancelVerificationValue rs = new CancelVerificationValue();
		rs.setCancelable(isCancelable);
		if(isCancelable) {
			rs.setCancelationFee(new CurrencyValue(currency, new BigDecimal(totalCharge)));
		} else {
			rs.setErrors(errors);
		}
		return rs;
	}

	private ActivityBookRS processBook(ActivityBookRQ bookRQ, String client) throws Exception {
		log.debug("processBook::bookRQ: {}", bookRQ);
		//hold add books
		ViatorV2Interface viatorV2Interface = new ViatorV2Interface(propertiesDAO, client);
		List<ConfirmBooking> confirmBookings = new ArrayList<>();
		for(ActivityRequestItem item : bookRQ.getItems()) {
			//Update Value to hold the booking
			HoldBooking holdBooking = new HoldBooking();
			updateConfirmHoldBooking(client, holdBooking, item, bookRQ);
			HoldBookingResponse holdBookingResponse = makeHoldBooking(viatorV2Interface, holdBooking);
			if(holdBookingResponse == null) {
				throw new Exception("Item (internalItemReference = " + item.getInternalItemReference() + " ) is invalid");
			}

			//Update Value to confirm the booking
			ConfirmBooking confirmBooking = new ConfirmBooking(bookRQ, item);
			updateConfirmHoldBooking(client, confirmBooking, item, bookRQ);
			confirmBooking.getCommunication().setEmail(propertiesDAO.getProperty(client, CHANNEL, "agentSupportEmail"));
			confirmBooking.getCommunication().setPhone(propertiesDAO.getProperty(client, CHANNEL, "agentSupportPhone"));
			confirmBooking.setPartnerBookingRef(holdBookingResponse.getBookingRef());
			confirmBooking.setInternalItemReference(item.getInternalItemReference());
			confirmBookings.add(confirmBooking);
		}
		ActivityBookRS activityBookRS = makeActivityBookRS(bookRQ);
		for(ConfirmBooking confirmBooking : confirmBookings) {
			StartConfirmBookingRS confirmBookingRS = viatorV2Interface.startConfirmBooking(confirmBooking);
			log.debug("processBook::confirmBookingRS: {}", confirmBookingRS);
			updateActivityResponseItem(activityBookRS, confirmBookingRS, confirmBooking);
		}
		return activityBookRS;
	}

	private ActivityBookRS makeActivityBookRS(ActivityBookRQ bookRQ) {
		ActivityBookRS  activityBookRS = new ActivityBookRS();
		activityBookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
		return activityBookRS;
	}

	private void updateActivityResponseItem(ActivityBookRS activityBookRS, StartConfirmBookingRS confirmBookingRS, ConfirmBooking confirmBooking) {
		ActivityBookRS.ActivityResponseItem responseItem = new ActivityBookRS.ActivityResponseItem();
		if("CONFIRMED".equals(confirmBookingRS.getStatus())) {
			responseItem.setItemStatus(ActivityBooking.ItemStatus.BOOKED);
		} else {
			responseItem.setItemStatus(ActivityBooking.ItemStatus.FAILED);
		}
		responseItem.setChannel(CHANNEL);
		for(ConfirmBooking.BookingQuestionAnswers bookingQA : confirmBooking.getBookingQuestionAnswers()) {
			BookingAnswers bookingAnswersRS = new BookingAnswers();
			bookingAnswersRS.setQuestionId(bookingQA.getQuestion());
			bookingAnswersRS.setAnswer(bookingQA.getAnswer());
			bookingAnswersRS.setTravelerNum(bookingQA.getTravelerNum());
			responseItem.getBookingQuestionAnswers().add(bookingAnswersRS);
		}
		responseItem.setBookingItemReference(confirmBookingRS.getBookingRef());
		responseItem.setInternalItemReference(confirmBooking.getInternalItemReference());
		if(confirmBookingRS.getCancellationPolicy() != null) {
			responseItem.setItemRemark(confirmBookingRS.getCancellationPolicy().getDescription());
		}
		activityBookRS.getItems().add(responseItem);

		//generate booking reference by concatenating
		if(activityBookRS.getBookingReference() == null || "".equals(activityBookRS.getBookingReference())) {
			activityBookRS.setBookingReference(confirmBookingRS.getBookingRef());
		} else {
			activityBookRS.setBookingReference(activityBookRS.getBookingReference() + "|" + confirmBookingRS.getBookingRef());
		}
	}

	protected HoldBookingResponse makeHoldBooking(ViatorV2Interface viatorV2Interface, HoldBooking holdBooking)
	{
		log.debug("makeHoldBooking::confirmHoldBooking: {}", holdBooking);
		HoldBookingResponse holdBookingResponse = viatorV2Interface.startHoldBooking(holdBooking);
		if ( holdBookingResponse != null && holdBookingResponse.getBookingHoldInfo() != null
				&& holdBookingResponse.getBookingHoldInfo().getPricing() != null
				&& holdBookingResponse.getBookingHoldInfo().getPricing().getStatus().equals("HOLDING")) {
			return holdBookingResponse;
		}
		return null;
	}

	protected void updateConfirmHoldBooking(String client, ConfirmHoldBooking confirmHoldBooking, ActivityRequestItem item, ActivityBookRQ bookRQ) throws Exception
	{
		if (item.getActivityId().startsWith(CHANNEL_PREFIX))
			confirmHoldBooking.setProductCode(item.getActivityId().substring(CHANNEL_PREFIX.length()));
		else
			confirmHoldBooking.setProductCode(item.getActivityId());
		if ( item.getOptionId() != null && item.getOptionId().length() > 0 && !item.getOptionId().equals("DEFAULT"))
			confirmHoldBooking.setProductOptionCode(item.getOptionId());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		confirmHoldBooking.setTravelDate(item.getDate().format(formatter));
		confirmHoldBooking.setCurrency(propertiesDAO.getProperty(client, CHANNEL.toString(), "currency"));
		if ( item.getDepartureId() != null && !item.getDepartureId().equals("00:01"))
			confirmHoldBooking.setStartTime(item.getDepartureId());
		ViatorV2Activity viatorSpecificData = getViatorV2Activity(confirmHoldBooking.getProductCode());
		if (viatorSpecificData == null)
		{
			log.warn("makeHoldBooking::productCode " + confirmHoldBooking.getProductCode() + " not found in local tables");
			throw new Exception("productCode " + confirmHoldBooking.getProductCode() + " not found in local tables");
		}
		Map<String, PaxMix> ageBandCounts = new HashMap<>();
		PaxMix paxmix = new PaxMix();
		//paxmix.setAgeBand("ADULT");
		//paxmix.setNumberOfTravelers(item.getTravellerIndex().size());
		//ageBandCounts.put(paxmix.getAgeBand(), paxmix);
		// given an age, find the band
		for (Integer travellerIndex : item.getTravellerIndex())
		{
			if (log.isDebugEnabled())
				log.debug("makeHoldBooking::testing travellerIndex + " + travellerIndex);
			Traveller traveller = bookRQ.getTravellers().get(travellerIndex);
			int age = 30;
			if (traveller.getBirthDate() != null)
			{
				age = traveller.getAge(item.getDate());
			}
			if (log.isDebugEnabled())
				log.debug("makeHoldBooking::age=" + age);
			if (age >= viatorSpecificData.getInfantMinAge().intValue() && age <= viatorSpecificData.getInfantMaxAge().intValue())
			{
				paxmix = ageBandCounts.get("INFANT");
				if (paxmix == null)
				{
					paxmix = new PaxMix();
					paxmix.setAgeBand("INFANT");
					ageBandCounts.put(paxmix.getAgeBand(), paxmix);
				}
				paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
			}
			if (age >= viatorSpecificData.getChildMinAge().intValue() && age <= viatorSpecificData.getChildMaxAge().intValue())
			{
				paxmix = ageBandCounts.get("CHILD");
				if (paxmix == null)
				{
					paxmix = new PaxMix();
					paxmix.setAgeBand("CHILD");
					ageBandCounts.put(paxmix.getAgeBand(), paxmix);
				}
				paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
			}
			if (age >= viatorSpecificData.getYouthMinAge() && age <= viatorSpecificData.getYouthMaxAge())
			{
				paxmix = ageBandCounts.get("YOUTH");
				if (paxmix == null)
				{
					paxmix = new PaxMix();
					paxmix.setAgeBand("YOUTH");
					ageBandCounts.put(paxmix.getAgeBand(), paxmix);
				}
				paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
			}
			if (age >= viatorSpecificData.getAdultMinAge().intValue() && age <= viatorSpecificData.getAdultMaxAge().intValue())
			{
				paxmix = ageBandCounts.get("ADULT");
				if (paxmix == null)
				{
					paxmix = new PaxMix();
					paxmix.setAgeBand("ADULT");
					ageBandCounts.put(paxmix.getAgeBand(), paxmix);
				}
				paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
			}
			if (age >= viatorSpecificData.getSeniorMinAge().intValue() && age <= viatorSpecificData.getSeniorMaxAge().intValue())
			{
				paxmix = ageBandCounts.get("SENIOR");
				if (paxmix == null)
				{
					paxmix = new PaxMix();
					paxmix.setAgeBand("SENIOR");
					ageBandCounts.put(paxmix.getAgeBand(), paxmix);
				}
				paxmix.setNumberOfTravelers(paxmix.getNumberOfTravelers() + 1);
			}
		}
		for (PaxMix p : ageBandCounts.values())
			confirmHoldBooking.getPaxMix().add(p);
	}

	public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	/*v2*/
	private Set<ActivityResult> makeActivityResults(List<ActivityRCData> rcDataList, List<ViatorV2ScheduleData> boxedSchedules,
													AvailSearchRQDTO availSearchRQDTO) throws Exception {
		log.debug("makeActivityResults::enter for " + rcDataList.size() + " products");

		Set<ActivityResult> results = new HashSet<>();
		Map<String, List<FormalViatorV2ScheduleData>> mapOfProductionCodeAndSchedules = makeMapOfProductCodeAndFormalSchedulesBySchedules(boxedSchedules);
		log.debug("mapOfProductionCodeAndSchedules::size: {}", mapOfProductionCodeAndSchedules.size());
		if(mapOfProductionCodeAndSchedules == null) {
			return Collections.EMPTY_SET;
		}
		int numberOfItems = 0;
		for (ActivityRCData rcData : rcDataList) {
			List<FormalViatorV2ScheduleData> formalViatorV2Schedules = mapOfProductionCodeAndSchedules.get(rcData.getChannelCode());
			if(formalViatorV2Schedules == null) {
				continue;
			}
			Collection<ActivityDeparture> departures = makeActivityDepartures(availSearchRQDTO, formalViatorV2Schedules);
			if(departures != null) {
				ActivityResult activityResult = new ActivityResult();
				activityResult.setActivityRC(mapperService.mapActivityRC(rcData));
				activityResult.getDepartures().addAll(departures);
				results.add(activityResult);
				if(numberOfItems++ == MAX_RESPONSE) {
					return results;
				}
			}
		}
		return results;
	}

	/*v1*/
	private Set<ActivityResult> makeActivityResults(List<ActivityRCData> rcDataList, AvailSearchRQDTO availSearchRQDTO) throws Exception {
		if (log.isDebugEnabled())
			log.debug("makeActivityResults::enter for " + rcDataList.size() + " products");
		// schedules are sorted by product_code, time, product_option_code, special. And filtered by dates
		Set<ActivityResult> results = new HashSet<>();

		Map<String, List<FormalViatorV2ScheduleData>> mapOfProductionCodeAndSchedules = makeMapOfProductCodeAndFormalScheduleList(rcDataList);
		log.debug("mapOfProductionCodeAndSchedules::size: {}", mapOfProductionCodeAndSchedules.size());
		if(mapOfProductionCodeAndSchedules == null) {
			return Collections.EMPTY_SET;
		}
		for (ActivityRCData rcData : rcDataList) {
			List<FormalViatorV2ScheduleData> formalViatorV2Schedules = mapOfProductionCodeAndSchedules.get(rcData.getChannelCode());
			if(formalViatorV2Schedules == null) {
				continue;
			}
			Collection<ActivityDeparture> departures = makeActivityDepartures(availSearchRQDTO, formalViatorV2Schedules);
			if(!departures.isEmpty()) {
				ActivityResult activityResult = new ActivityResult();
				activityResult.setActivityRC(mapperService.mapActivityRC(rcData));
				activityResult.getDepartures().addAll(departures);
				results.add(activityResult);
			}
		}
		return results;
	}


	private Collection<ActivityDeparture> makeActivityDepartures(AvailSearchRQDTO availSearchRQDTO, List<FormalViatorV2ScheduleData> formalViatorV2Schedules) {
		if(formalViatorV2Schedules.isEmpty()) {
			return null;
		}
		Map<String, ActivityDeparture> activityDepartureMap = new HashMap<>();
		LocalDate workDate = availSearchRQDTO.getActivityDateFrom().minusDays(1);
		while (!workDate.isAfter(availSearchRQDTO.getActivityDateTo())) {
			workDate = workDate.plusDays(1);
			log.debug("makeActivityResults::testing workDate " + workDate);
			for (FormalViatorV2ScheduleData scheduleData : formalViatorV2Schedules) {
				log.debug("makeActivityResults::scheduleData=" + scheduleData);
				if (workDate.isBefore(scheduleData.getStartDate()) || workDate.isAfter(scheduleData.getEndDate())) {
					log.debug("makeActivityResults::workDate outside schedule dates");
					continue;
				}
				if(!isDateScheduled(workDate, scheduleData)) {
					continue;
				}
				log.debug("makeActivityResults::workDate within schedule and day checks");
				String departureId = workDate.format(yyyymmdd) + " " + scheduleData.getTime().format(hhmm);
				log.debug("makeActivityResults::departureId " + departureId + " from " + scheduleData.getTime());
				ActivityDeparture activityDeparture = activityDepartureMap.get(departureId);
				if (activityDeparture == null)
				{
					activityDeparture = new ActivityDeparture();
					activityDeparture.setDate(workDate);
					activityDeparture.setDepartureTime(scheduleData.getTime());
					if (scheduleData.getTime().equals(NO_TIME))
						activityDeparture.setDepartureName("No time specified");
					else
						activityDeparture.setDepartureName(scheduleData.getTime().format(hhmmNice));
					activityDeparture.setDepartureId(scheduleData.getTime().format(timeFormatter));
					activityDepartureMap.put(departureId, activityDeparture);
				}
				ActivityOption activityOption = makeActivityOptions(scheduleData, availSearchRQDTO);
				if(activityOption != null) {
					activityDeparture.getOptions().add(activityOption);
				}
			}
		}
		return activityDepartureMap.values();
	}

	private boolean isDateScheduled(LocalDate workDate, FormalViatorV2ScheduleData scheduleData) {
		switch (workDate.getDayOfWeek())
		{
			case MONDAY:
				if (!scheduleData.getMonday())
					return false;
			case TUESDAY:
				if (!scheduleData.getTuesday())
					return false;
			case WEDNESDAY:
				if (!scheduleData.getWednesday())
					return false;
			case THURSDAY:
				if (!scheduleData.getThursday())
					return false;
			case FRIDAY:
				if (!scheduleData.getFriday())
					return false;
			case SATURDAY:
				if (!scheduleData.getSaturday())
					return false;
			case SUNDAY:
				if (!scheduleData.getSunday())
					return false;
		}
		return true;
	}

	private ActivityOption makeActivityOptions(FormalViatorV2ScheduleData scheduleData, AvailSearchRQDTO availSearchRQDTO) {
		com.torkirion.eroam.microservice.activities.apidomain.ActivityOption activityOption = new com.torkirion.eroam.microservice.activities.apidomain.ActivityOption();
		activityOption.setOptionId(scheduleData.getProductOptionCode());
		ViatorV2ActivityProductOption option = getViatorOption(scheduleData.getProductCode(), scheduleData.getProductOptionCode());
		if (option != null && option.getDescription() != null && option.getDescription().length() > 0)
			activityOption.setOptionName(option.getDescription());
		else
			activityOption.setOptionName("Default");
		ViatorV2Activity viatorSpecificData = getViatorV2Activity(scheduleData.getProductCode());
		if (viatorSpecificData == null)
		{
			if (log.isDebugEnabled())
				log.debug("makeActivityResults::could not find data for " + scheduleData.getProductCode());
			return null;
		}
		if (viatorSpecificData != null && viatorSpecificData.getOptions() != null)
		{
			for (ViatorV2ActivityProductOption optionSummary : viatorSpecificData.getOptions())
			{
				if (optionSummary.getProductOptionCode().equals(scheduleData.getProductOptionCode()))
					activityOption.setOptionName(optionSummary.getDescription());
			}
		}
		activityOption.setBundlesOnly(false);
		if (viatorSpecificData.getBookingQuestions() != null && viatorSpecificData.getBookingQuestions().length() > 0)
		{
			String[] bookingQuestionKeys = viatorSpecificData.getBookingQuestions().split(",");
			for (int i = 0; i < bookingQuestionKeys.length; i++)
			{
				BookingQuestion q = getBookingDataQuestion(bookingQuestionKeys[i]);
				activityOption.getBookingQuestions().add(q);
			}
		}
		// pricing
		List<Integer> allAges = new ArrayList<>();
		for (int i = 0; i < availSearchRQDTO.getTravellers().getAdultCount(); i++)
		{
			allAges.add(30);
		}
		allAges.addAll(availSearchRQDTO.getTravellers().getChildAges());
		BigDecimal nettPriceBD = makeOptionNettPrice(scheduleData, viatorSpecificData, allAges);
		if (nettPriceBD == null)
		{
			if (log.isDebugEnabled())
				log.debug("makeActivityResults::nettPrice is null, bypassing");
			return null;
		}
		activityOption.setNettPrice(new CurrencyValue(scheduleData.getCurrencyId(), nettPriceBD));
		BigDecimal rrpPriceBD = makeOptionRrpPrice(scheduleData, viatorSpecificData, allAges);
		if (rrpPriceBD == null)
		{
			if (log.isDebugEnabled())
				log.debug("makeActivityResults::rrpPrice is null, bypassing");
			return null;
		}
		activityOption.setRrpPrice(new CurrencyValue(scheduleData.getCurrencyId(), rrpPriceBD));

		return activityOption;
	}

	/* this method to load list of ViatorV2Schedule by product code
	* Load one time then put to Map
	*/
	private Map<String, List<FormalViatorV2ScheduleData>> makeMapOfProductCodeAndFormalScheduleList(List<ActivityRCData> rcDataList) {
		log.debug("getMapOfProductionCodeAndScheduleList::enter::");
		List<String> productCodes = makeProductCodesFromRcDataList(rcDataList);
		log.debug("getMapOfProductionCodeAndScheduleList::productCodes:{}", productCodes);
		Map<String, List<ViatorV2ScheduleData>> mapOfProductCodeAndSchedules = loadAllSchedulesByProductCodes(productCodes);
		if(mapOfProductCodeAndSchedules == null) {
			return null;
		}
		Map<String, List<FormalViatorV2ScheduleData>> mapOfProductionCodeAndFormalSchedules = new HashMap<>();
		Set<String> productionCodeSet = mapOfProductCodeAndSchedules.keySet();
		for(String productionCode : productionCodeSet) {
			mapOfProductionCodeAndFormalSchedules.put(productionCode,  FormalViatorV2ScheduleData.transform(mapOfProductCodeAndSchedules.get(productionCode)));
		}
		return mapOfProductionCodeAndFormalSchedules;
	}

	private Map<String, List<FormalViatorV2ScheduleData>> makeMapOfProductCodeAndFormalSchedulesBySchedules(List<ViatorV2ScheduleData> viatorV2Schedules) {
		log.debug("getMapOfProductionCodeAndScheduleList::enter::");
		Map<String, List<ViatorV2ScheduleData>> mapProductCodeAndSchedules = mapProductCodeAndSchedules(viatorV2Schedules);
		Map<String, List<FormalViatorV2ScheduleData>> map = new HashMap<>();
		for(String productCode : mapProductCodeAndSchedules.keySet()) {
			map.put(productCode,  FormalViatorV2ScheduleData.transform(mapProductCodeAndSchedules.get(productCode)));
		}
		return map;
	}

	private Map<String, List<ViatorV2ScheduleData>> loadAllSchedulesByProductCodes(List<String> productCodes) {
		List<ViatorV2ScheduleData> viatorV2Schedules = activityScheduleRepo.findAllByProductCodeIn(productCodes);
		return mapProductCodeAndSchedules(viatorV2Schedules);
	}

	private Map<String, List<ViatorV2ScheduleData>> mapProductCodeAndSchedules(List<ViatorV2ScheduleData> viatorV2Schedules) {
		log.debug("getMapOfProductionCodeAndScheduleList::viatorV2Schedules:{}", viatorV2Schedules);
		if(CollectionUtils.isEmpty(viatorV2Schedules)) {
			return null;
		}
		return viatorV2Schedules.stream().collect(
				Collectors.groupingBy(ViatorV2ScheduleData::getProductCode, Collectors.toCollection(ArrayList::new))
		);
	}

	private List<String> makeProductCodesFromRcDataList(List<ActivityRCData> rcDataList) {
		return rcDataList.stream().map(rc -> rc.getChannelCode()).collect(Collectors.toList());
	}

	private List<String> getAllProductCodesFromViatorSchedules(List<ViatorV2ScheduleData> schedules) {
		return schedules.stream().map(s -> s.getProductCode()).distinct().collect(Collectors.toList());
	}

	private List<String> convertProductCodeToCode(List<String> productCodes) {
		return productCodes.stream().map(pc -> CHANNEL_PREFIX + pc).collect(Collectors.toList());
	}

	private BigDecimal makeOptionNettPrice(FormalViatorV2ScheduleData scheduleData, ViatorV2Activity viatorSpecificData, List<Integer> allAges)
	{
		if (log.isDebugEnabled())
			log.debug("makeOptionNettPrice::enter");
		BigDecimal totalAmount = BigDecimal.ZERO;
		Map<Integer, Integer> ageCounts = new HashMap<>();
		for (Integer age : allAges)
		{
			// find the band that applies to this age, then go find the price per band. Add to total
			BigDecimal amountPerPerson = getBandPrice(age, scheduleData, viatorSpecificData, true);
			if (amountPerPerson == null)
			{
				if (log.isDebugEnabled())
					log.debug("makeOptionNettPrice::no pricing for age " + age + " for product " + viatorSpecificData.getProductCode());
				return null;
			}
			totalAmount = totalAmount.add(amountPerPerson);
			if (ageCounts.get(age) == null)
				ageCounts.put(age, 0);
			ageCounts.put(age, ageCounts.get(age) + 1);
		}
		if (fallsOutsideMinMaxPax(viatorSpecificData, ageCounts))
		{
			return null;
		}
		if (log.isDebugEnabled())
			log.debug("makeOptionNettPrice::return " + totalAmount);
		return totalAmount;
	}


	private BigDecimal makeOptionRrpPrice(FormalViatorV2ScheduleData scheduleData, ViatorV2Activity viatorSpecificData, List<Integer> allAges)
	{
		if (log.isDebugEnabled())
			log.debug("makeOptionRrpPrice::enter");
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (Integer age : allAges)
		{
			// find the band that applies to this age, then go find the price per band. Add to total
			BigDecimal amountPerPerson = getBandPrice(age, scheduleData, viatorSpecificData, false);
			if (amountPerPerson == null)
			{
				if (log.isDebugEnabled())
					log.debug("makeOptionRrpPrice::no pricing for age " + age + " for product " + viatorSpecificData.getProductCode());
				return null;
			}
			totalAmount = totalAmount.add(amountPerPerson);
		}
		if (log.isDebugEnabled())
			log.debug("makeOptionRrpPrice::return " + totalAmount);
		return totalAmount;
	}

	private BigDecimal getBandPrice(int age, FormalViatorV2ScheduleData scheduleData, ViatorV2Activity viatorSpecificData, boolean net) {
		if (log.isDebugEnabled())
			log.debug("getBandPrice::looking for age " + age + " in " + viatorSpecificData);

		if (age >= viatorSpecificData.getInfantMinAge() && age <= viatorSpecificData.getInfantMaxAge())
		{
			if (net)
				return scheduleData.getInfantPriceNet();
			else
				return scheduleData.getInfantPriceRrp();
		}
		if (age >= viatorSpecificData.getChildMinAge() && age <= viatorSpecificData.getChildMaxAge())
		{
			if (net)
				return scheduleData.getChildPriceNet();
			else
				return scheduleData.getChildPriceRrp();
		}
		if (age >= viatorSpecificData.getYouthMinAge() && age <= viatorSpecificData.getYouthMaxAge())
		{
			if (net)
				return scheduleData.getYouthPriceNet();
			else
				return scheduleData.getYouthPriceRrp();
		}
		if (age >= viatorSpecificData.getAdultMinAge() && age <= viatorSpecificData.getAdultMaxAge())
		{
			if (net)
				return scheduleData.getAdultPriceNet();
			else
				return scheduleData.getAdultPriceRrp();
		}
		if (age >= viatorSpecificData.getSeniorMinAge() && age <= viatorSpecificData.getSeniorMaxAge())
		{
			if (net)
				return scheduleData.getSeniorPriceNet();
			else
				return scheduleData.getSeniorPriceRrp();
		}
		return null;
	}

	private boolean fallsOutsideMinMaxPax(ViatorV2Activity viatorSpecificData, Map<Integer, Integer> ageCounts)
	{
		for (Entry<Integer, Integer> entry : ageCounts.entrySet())
		{
			int age = entry.getKey();
			int count = entry.getValue();
			if (age >= viatorSpecificData.getInfantMaxAge() && age <= viatorSpecificData.getInfantMaxAge())
			{
				if (count < viatorSpecificData.getInfantMinPax() || count > viatorSpecificData.getInfantMinPax())
				{
					if (log.isDebugEnabled())
						log.debug("fallsOutsideMinMaxPax::age " + age + " for product " + viatorSpecificData.getProductCode() + " falls outside infant minmax");
					return true;
				}
			}
			if (age >= viatorSpecificData.getChildMaxAge() && age <= viatorSpecificData.getChildMaxAge())
			{
				if (count < viatorSpecificData.getChildMinPax() || count > viatorSpecificData.getChildMinPax())
				{
					if (log.isDebugEnabled())
						log.debug("fallsOutsideMinMaxPax::age " + age + " for product " + viatorSpecificData.getProductCode() + " falls outside child minmax");
					return true;
				}
			}
			if (age >= viatorSpecificData.getYouthMaxAge() && age <= viatorSpecificData.getYouthMaxAge())
			{
				if (count < viatorSpecificData.getYouthMinPax() || count > viatorSpecificData.getYouthMinPax())
				{
					if (log.isDebugEnabled())
						log.debug("fallsOutsideMinMaxPax::age " + age + " for product " + viatorSpecificData.getProductCode() + " falls outside youth minmax");
					return true;
				}
			}
			if (age >= viatorSpecificData.getAdultMaxAge() && age <= viatorSpecificData.getAdultMaxAge())
			{
				if (count < viatorSpecificData.getAdultMinPax() || count > viatorSpecificData.getAdultMinPax())
				{
					if (log.isDebugEnabled())
						log.debug("fallsOutsideMinMaxPax::age " + age + " for product " + viatorSpecificData.getProductCode() + " falls outside adult minmax");
					return true;
				}
			}
			if (age >= viatorSpecificData.getSeniorMaxAge() && age <= viatorSpecificData.getSeniorMaxAge())
			{
				if (count < viatorSpecificData.getSeniorMinPax() || count > viatorSpecificData.getSeniorMinPax())
				{
					if (log.isDebugEnabled())
						log.debug("fallsOutsideMinMaxPax::age " + age + " for product " + viatorSpecificData.getProductCode() + " falls outside senior minmax");
					return true;
				}
			}
		}
		return false;
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

	private static DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH-mm");

	private static DateTimeFormatter hhmmNice = DateTimeFormatter.ofPattern("hh:mm a");

	private static final Map<String, ViatorV2Activity> activitySummaryCache = new HashMap<>();

	private ViatorV2ActivityProductOption getViatorOption(String productCode, String productOptionCode)
	{
		List<ViatorV2ActivityProductOption> options = viatorV2ActivityProductOptionRepo.findAllByProductCodeAndProductOptionCode(productCode, productOptionCode);
		if (options.size() == 1)
			return options.get(0);
		if (log.isDebugEnabled())
			log.debug("getViatorOption::number options returned for " + productCode + "/" + productOptionCode + " is " + options.size());
		return null;
	}

	private void loadViatorV2Activity(List<String> productCodes) {
		List<ViatorV2Activity> viatorV2Activities = viatorV2ActivityRepo.findAllByProductCodeIn(productCodes);
		if(viatorV2Activities.isEmpty()) {
			return;
		}
		for(ViatorV2Activity viatorV2Activity : viatorV2Activities) {
			correctViatorV2Activity(viatorV2Activity);
			activitySummaryCache.put(viatorV2Activity.getProductCode(), viatorV2Activity);
		}
	}

	private ViatorV2Activity getViatorV2Activity(String productCode)
	{
		ViatorV2Activity viatorV2Activity = activitySummaryCache.get(productCode);
		if (viatorV2Activity == null)
		{
			List<ViatorV2Activity> aList = viatorV2ActivityRepo.findByProductCode(productCode);
			log.warn("getViatorV2Activity::aList=" + aList);
			if (aList.size() == 1)
			{
				viatorV2Activity = aList.get(0);
				correctViatorV2Activity(viatorV2Activity);
				activitySummaryCache.put(productCode, viatorV2Activity);
			}
		}
		return viatorV2Activity;
	}

	private void correctViatorV2Activity(ViatorV2Activity viatorV2Activity) {
		if (viatorV2Activity.getInfantMinAge() == null)
			viatorV2Activity.setInfantMinAge(-1);
		if (viatorV2Activity.getInfantMaxAge() == null)
			viatorV2Activity.setInfantMaxAge(-1);
		if (viatorV2Activity.getChildMinAge() == null)
			viatorV2Activity.setChildMinAge(-1);
		if (viatorV2Activity.getChildMaxAge() == null)
			viatorV2Activity.setChildMaxAge(-1);
		if (viatorV2Activity.getYouthMinAge() == null)
			viatorV2Activity.setYouthMinAge(-1);
		if (viatorV2Activity.getYouthMaxAge() == null)
			viatorV2Activity.setYouthMaxAge(-1);
		if (viatorV2Activity.getAdultMinAge() == null)
			viatorV2Activity.setAdultMinAge(-1);
		if (viatorV2Activity.getAdultMaxAge() == null)
			viatorV2Activity.setAdultMaxAge(-1);
		if (viatorV2Activity.getSeniorMinAge() == null)
			viatorV2Activity.setSeniorMinAge(-1);
		if (viatorV2Activity.getSeniorMaxAge() == null)
			viatorV2Activity.setSeniorMaxAge(-1);
	}

	static void resetActivitySummaryCache()
	{
		activitySummaryCache.clear();
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

	private BookingQuestion getBookingDataQuestion(String key)
	{
		if (log.isDebugEnabled())
			log.debug("getBookingDataQuestion::load for key " + key);
		if (bookingQuestionsCache.size() == 0)
		{
			for (BookingQuestionData bookingQuestionData : viatorV2BookingQuestionRepo.findAll())
			{
				BookingQuestion bookingQuestion = new BookingQuestion();
				bookingQuestion.setQuestionId(bookingQuestionData.getQuestionId());
				String bookingQuestionText = bookingQuestionData.getLabel();
				if (bookingQuestionData.getHint() != null && bookingQuestionData.getHint().length() > 0)
					bookingQuestionText = bookingQuestionData.getLabel() + " - " + bookingQuestionData.getHint();
				switch (bookingQuestionData.getType())
				{
					case "DATE":
						bookingQuestion.setQuestionType(QuestionType.DATE);
						break;
					case "NUMBER_AND_UNIT":
						bookingQuestion.setQuestionType(QuestionType.NUMBER);
						if (bookingQuestionData.getUnits() != null && bookingQuestionData.getUnits().length() > 0)
						{
							String[] units = bookingQuestionData.getUnits().split(",");
							if (units.length > 0)
								bookingQuestionText = bookingQuestionText + " (please provide answer in " + units[0] + ")";
						}
						break;
					case "STRING":
						bookingQuestion.setQuestionType(QuestionType.STRING);
						break;
					case "LOCATION_REF_OR_FREE_TEXT":
						bookingQuestion.setQuestionType(QuestionType.STRING);
						break;
					case "TIME":
						bookingQuestion.setQuestionType(QuestionType.TIME);
						break;
					default:
						log.warn("getBookingDataQuestion::unknown type " + bookingQuestionData.getType());
				}
				bookingQuestion.setQuestionText(bookingQuestionText);
				if (bookingQuestionData.getType().equals("STRING") && bookingQuestionData.getAllowedAnswers() != null && bookingQuestionData.getAllowedAnswers().length() > 0)
				{
					bookingQuestion.setList(Arrays.asList(bookingQuestionData.getAllowedAnswers().split(",")));
				}
				switch (bookingQuestionData.getBookingGroup())
				{
					case "PER_TRAVELER":
						bookingQuestion.setPerTraveller(true);
						break;
					case "PER_BOOKING":
						bookingQuestion.setPerTraveller(false);
						break;
					default:
						log.warn("getBookingDataQuestion::unknown booking group " + bookingQuestionData.getBookingGroup());
				}
				bookingQuestionsCache.put(bookingQuestionData.getQuestionId(), bookingQuestion);
			}
		}
		return bookingQuestionsCache.get(key);
	}

	static void resetBookingQuestionsCache()
	{
		bookingQuestionsCache.clear();
	}

	private static DateTimeFormatter yyyymmdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final Map<String, BookingQuestion> bookingQuestionsCache = new HashMap<>();

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The API currency for this client", "currency", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The V1 URL endpoint", "viatorV1URL", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The V2 URL endpoint", "viatorV2URL", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The unique API key for this client", "apikey", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("If bookings should be 'faked' and NOT sent to the server, just return a dummy confirmation", "bypassBooking", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}

	private String makeProductCodeFromActivityId(String activityId) {
		if(activityId == null)
			return null;
		if (activityId.startsWith(CHANNEL_PREFIX))
			return activityId.substring(CHANNEL_PREFIX.length());
		return activityId;
	}

	private ActivityResult makeActivityResult(AvailablityCheckResult availablityCheckResult, RateCheckRQDTO rateCheckRQDTO) throws Exception {
		if (availablityCheckResult == null || availablityCheckResult.getBookableItems() == null) {
			return null;
		}
		for (BookableItem bookableItem : availablityCheckResult.getBookableItems()) {
			if (bookableItem.getStartTime() == null || bookableItem.getStartTime().equals(rateCheckRQDTO.getDepartureId())) {
				if (log.isDebugEnabled())
					log.debug("rateCheck::found it!");
				if (!bookableItem.getAvailable()) {
					throw new Exception("Departure not available");
				}
				if (log.isDebugEnabled())
					log.debug("rateCheck::load the structure we want from the DB, and replace the pricing");

				AvailSearchByActivityIdRQDTO availSearchRQ = new AvailSearchByActivityIdRQDTO();
				availSearchRQ.setActivityIds(new HashSet<>());
				availSearchRQ.getActivityIds().add(rateCheckRQDTO.getActivityId());
				availSearchRQ.setCountryCodeOfOrigin(rateCheckRQDTO.getCountryCodeOfOrigin());
				availSearchRQ.setActivityDateFrom(rateCheckRQDTO.getActivityDate());
				availSearchRQ.setActivityDateTo(rateCheckRQDTO.getActivityDate());
				availSearchRQ.setTravellers(rateCheckRQDTO.getTravellers());
				availSearchRQ.setClient(rateCheckRQDTO.getClient());
				availSearchRQ.setChannel(CHANNEL);
				Set<ActivityResult> activityResults = searchByActivityId(availSearchRQ);
				ActivityResult activityResult = activityResults.iterator().next();
				// just pull out the one departure and option we are interested in
				ActivityDeparture departure = null;
				com.torkirion.eroam.microservice.activities.apidomain.ActivityOption option = null;
				for (ActivityDeparture deps : activityResult.getDepartures()) {
					String availDepartureTime = deps.getDepartureTime().format(timeFormatter);
					if (log.isDebugEnabled())
						log.debug("rateCheck::checking DB loaded availDepartureTime " + availDepartureTime + " against " + rateCheckRQDTO.getDepartureId());
					if (availDepartureTime.equals(rateCheckRQDTO.getDepartureId())) {
						departure = deps;
						for (com.torkirion.eroam.microservice.activities.apidomain.ActivityOption o : departure.getOptions()) {
							if (log.isDebugEnabled())
								log.debug("rateCheck::checking DB loaded option  " + o.getOptionId() + " against " + rateCheckRQDTO.getOptionId());
							if (o.getOptionId().equals(rateCheckRQDTO.getOptionId())) {
								option = o;
								option.setNettPrice(new CurrencyValue(availablityCheckResult.getCurrency(), bookableItem.getTotalPrice().getPrice().getPartnerTotalPrice()));
								option.setRrpPrice(new CurrencyValue(availablityCheckResult.getCurrency(), bookableItem.getTotalPrice().getPrice().getRecommendedRetailPrice()));
								if (log.isDebugEnabled())
									log.debug("rateCheck::found, setting nett=" + option.getNettPrice() + ", rrp=" + option.getRrpPrice());
								option.setPricePer(new ArrayList<>());
								for (LineItem lineItem : bookableItem.getLineItems()) {
									com.torkirion.eroam.microservice.activities.apidomain.ActivityOption.ActivityOptionPriceBand band = new com.torkirion.eroam.microservice.activities.apidomain.ActivityOption.ActivityOptionPriceBand();
									band.setAgeBandName(lineItem.getAgeBand());
									band.setNettPrice(new CurrencyValue(availablityCheckResult.getCurrency(), lineItem.getSubtotalPrice().getPrice().getPartnerNetPrice().divide(BigDecimal.valueOf(lineItem.getNumberOfTravelers()), 2, RoundingMode.HALF_DOWN)));
									band.setRrpPrice(new CurrencyValue(availablityCheckResult.getCurrency(), lineItem.getSubtotalPrice().getPrice().getRecommendedRetailPrice().divide(BigDecimal.valueOf(lineItem.getNumberOfTravelers()), 2, RoundingMode.HALF_DOWN)));
									option.getPricePer().add(band);
								}
							}
						}
					}
				}
				activityResult.setDepartures(new TreeSet<>());
				activityResult.getDepartures().add(departure);
				departure.setOptions(new TreeSet<>());
				departure.getOptions().add(option);
				if (log.isDebugEnabled())
					log.debug("rateCheck::found, returning " + activityResult);
				return activityResult;
			}
		}
		return null;
	}
 }
