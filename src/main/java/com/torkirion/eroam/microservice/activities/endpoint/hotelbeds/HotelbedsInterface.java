package com.torkirion.eroam.microservice.activities.endpoint.hotelbeds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotelbeds.activities.api.ApiException;
import com.hotelbeds.activities.model.*;
import com.torkirion.eroam.microservice.activities.apidomain.*;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByActivityIdRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.activities.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.util.HotelBedsUtil;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@Slf4j
public class HotelbedsInterface {

    private final HotelBedsClient hbClient;
    private final ActivityHBAPIProperties activityHBAPIProperties;
    private final static String HB_VOUCHER_INFO = "Payable through {0}, acting as agent for the service operating company, details of which can be provided upon request. VAT: {1} Reference: {2}";

    public HotelbedsInterface(SystemPropertiesDAO properties, String site) throws Exception {
        activityHBAPIProperties = new ActivityHBAPIProperties(properties, site);
        this.hbClient = new HotelBedsClient(activityHBAPIProperties);
    }

    public Set<ActivityResult> startSearchActivities(AvailSearchByGeocordBoxRQDTO availSearchRQ) throws Exception
    {
        if (log.isDebugEnabled())
            log.debug("startSearchActivities::entering for availSearchRQ=" + availSearchRQ);
        long searchStartTime = System.currentTimeMillis();
        try {
            AvailabilitybyhotelcodeRequest availabilityRequest = HotelbedsInterfaceMapper.makeAvailabilityRQ(availSearchRQ);
            if (log.isDebugEnabled()) {
                log.debug("startSearchActivities::availabilityRequest: {} ", availabilityRequest);
            }
            List<Activity> activities = makeCallSearchHotelBedsActivities(availabilityRequest, availSearchRQ.getClient());
            if (log.isDebugEnabled()) {
                log.debug("startSearchActivities::returned " + (activities == null ? 0 : activities.size()) + " activities : {} ", activities);
            }
            return HotelbedsInterfaceMapper.makeActivityResults(activities, availSearchRQ);
        } catch (ApiException e) {
            log.error("startSearchActivities::error= " + e.getMessage(), e);
        }
        if (log.isDebugEnabled())
            log.debug("startSearchActivities::time taken = " + (System.currentTimeMillis() - searchStartTime));
        return Collections.EMPTY_SET;
    }

    public Set<ActivityResult> startSearchActivities(AvailSearchByActivityIdRQDTO availSearchRQ) throws Exception {
        if (log.isDebugEnabled())
            log.debug("startSearchActivities::entering for availSearchRQ=" + availSearchRQ);
        long searchStartTime = System.currentTimeMillis();
        try {
            AvailabilitybyhotelcodeRequest availabilityRequest = HotelbedsInterfaceMapper.makeAvailabilityRQ(availSearchRQ);
            List<Activity> activities = makeCallSearchHotelBedsActivities(availabilityRequest, availSearchRQ.getClient());
            return HotelbedsInterfaceMapper.makeActivityResults(activities, availSearchRQ);
        } catch (ApiException e) {
            log.error("startSearchActivities::error= " + e.getMessage());
        }
        if (log.isDebugEnabled())
            log.debug("startSearchActivities::time taken = " + (System.currentTimeMillis() - searchStartTime));
        return Collections.EMPTY_SET;
    }

    public ActivityResult startCheckRate(RateCheckRQDTO rateCheckRQ) throws Exception {
        log.debug("startCheckRate::rateCheckRQ: {}", rateCheckRQ);
        DetailSimpleRequest detailRequest = HotelbedsInterfaceMapper.makeDetailRQ(rateCheckRQ);
        Activity activity = makeCallDetailHotelBedsActivities(detailRequest, rateCheckRQ.getClient());
        if ( activity == null )
            throw new Exception("Activity Not found");
        ActivityResult activitySelected = HotelbedsInterfaceMapper.makeActivityResult(activity, rateCheckRQ);
        
        String departureIdSelected = rateCheckRQ.getDepartureId();
        String[] optionIdGroups = rateCheckRQ.getOptionId().split("_");
        String sessionCodeSelected = "";
        String languageCodeSelected = "";
        String optionIdSelected = optionIdGroups[0];
        if ( optionIdGroups.length > 1)
        	sessionCodeSelected = optionIdGroups[1];
        if ( optionIdGroups.length > 2)
        	languageCodeSelected = optionIdGroups[2];
        log.debug("startCheckRate::looking for departureId " + departureIdSelected + ", optionId " + optionIdSelected + ", sessionCodeSelected " + sessionCodeSelected + ", languageCodeSelected " + languageCodeSelected);
        SortedSet<ActivityDeparture> departures = activitySelected.getDepartures();
        if(departures.isEmpty()) {
            throw new Exception("Departures are empty");
        }
        Optional<ActivityDeparture> departureSelectedOptional = departures.stream()
                .filter(de -> departureIdSelected.equals(de.getDepartureId())).findFirst();
        if(!departureSelectedOptional.isPresent()) {
            throw new Exception("Departure " + departureIdSelected + " not found");
        }
        ActivityDeparture departureSelected = departureSelectedOptional.get();

        if(departureSelected.getOptions() == null || departureSelected.getOptions().isEmpty()) {
            throw new Exception("Options are empty");
        }
        ActivityOption optionSelected = null;
        for ( ActivityOption activityOption : departureSelected.getOptions())
        {
            log.debug("startCheckRate::testing option " + activityOption.getOptionId());
            if ( activityOption.getOptionId().endsWith(rateCheckRQ.getOptionId()))
            {
            	optionSelected = activityOption;
            }
        }
        if(optionSelected == null) {
            throw new Exception("Option " + optionIdSelected + " not found");
        }

        log.debug("startCheckRate::found!");
        departureSelected.getOptions().clear();
        departureSelected.getOptions().add(optionSelected);
        activitySelected.getDepartures().clear();
        activitySelected.getDepartures().add(departureSelected);
        return activitySelected;
    }

    public ActivityBookRS startBookActivities(String client, ActivityBookRQ bookRQ) throws Exception {
        log.debug("startBookActivities::requestString: {} ",  bookRQ);
        if (activityHBAPIProperties.bypassBooking)
        {
            log.warn("startBookActivities::bypassBooking is true");
            int randomInt = (int) (Math.random() * 10000.0);
            String randomHotelValue = Integer.toString(randomInt);
            int roomValue = 1;
            ActivityBookRS response = new ActivityBookRS();
            response.setBookingReference("HB" + randomHotelValue);
            response.setInternalBookingReference(bookRQ.getInternalBookingReference());
            for (ActivityBookRQ.ActivityRequestItem itemRQ : bookRQ.getItems())
            {
                randomInt = (int) (Math.random() * 10000.0);
                ActivityBookRS.ActivityResponseItem itemRS = new ActivityBookRS.ActivityResponseItem();
                itemRS.setBookingItemReference("HBH" + randomHotelValue + "_" + roomValue++);
                itemRS.setChannel(HotelbedsService.CHANNEL);
                itemRS.setItemStatus(ActivityBooking.ItemStatus.BOOKED);
                itemRS.setInternalItemReference(itemRQ.getInternalItemReference());
                BookingAnswers bookingAnswers = new BookingAnswers();
                bookingAnswers.setQuestionId("The question Id 001");
                bookingAnswers.setAnswer("The answer");
                itemRS.getBookingQuestionAnswers().add(bookingAnswers);
                String hbVoucherInfo = MessageFormat.format(HB_VOUCHER_INFO, "TRAVELCUBE PACIFIC PTY. LTD", "52099122577", itemRS.getBookingItemReference());
                itemRS.setItemRemark(hbVoucherInfo);
                if (log.isDebugEnabled())
                    log.debug("bookHotels::eRoam does not show itemRemarks - copy to main as well");
                if (!response.getRemarks().contains(hbVoucherInfo))
                    response.getRemarks().add(hbVoucherInfo);
    			itemRS.setItemVoucherURL("http://www.africau.edu/images/default/sample.pdf");
                response.getItems().add(itemRS);
            }
            return response;
        }
        //Booking follow: https://developer.hotelbeds.com/documentation/activities/knowledge-base/booking-flow/
        // Step1: get activityId and option from result of searching
        // Step2: Get Detail to get rateKey Id
        // Step3: book By the keyId

        //Step 2
        // get the activity detail
        // make a search call by type="service_modality" and value:  activityId + "@@#@@" + optionId
        // get detail to get a new rateKey for booking
        
        List<DetailSimpleRequest> detailRequests = HotelbedsInterfaceMapper.makeDetailRQ(bookRQ);
        List<Activity> activitiesWithRateKey = new ArrayList<>();
        for ( DetailSimpleRequest detailSimpleRequest : detailRequests)
        {
        	Activity activity = makeCallDetailHotelBedsActivities(detailSimpleRequest, client);
        	activitiesWithRateKey.add(activity);
        }
        log.debug("bookHotels::activitiesWithRateKey=" + activitiesWithRateKey);
        
        /*----------------------------------------------------------------------------------------------------------------------------*/
        //Step 2
        BookingConfirmRequest bookingRequest = HotelbedsInterfaceMapper.makeBookingConfirmRequest(client, bookRQ, activitiesWithRateKey);
        log.debug(prettyJson(bookingRequest));
        BookingResponse response = makeCallBookingActivity(client, bookingRequest);
        log.debug(prettyJson(response));
        return HotelbedsInterfaceMapper.makeActivityBookRS(response, bookRQ);
    }

    public ActivityCancelRS startCancelActivities(String client, ActivityCancelRQ cancelRQ) throws Exception {
        if (activityHBAPIProperties.bypassBooking)
        {
            log.warn("cancel::bypassBooking is true");
            CurrencyValue cancellationCharge = new CurrencyValue("AUD", BigDecimal.ZERO);
            ActivityCancelRS cancellation = new ActivityCancelRS("HB123", cancellationCharge);
            return cancellation;
        }
        BookingResponse cancelResponse = makeCallCancel(client, HotelbedsInterfaceMapper.LANGUAGE_EN, cancelRQ.getBookingReference());
        log.debug("startCancelActivities::cancelResponse");
        log.debug(prettyJson(cancelResponse));
        return HotelbedsInterfaceMapper.makeActivityCancelRS(cancelResponse);
    }

    private List<Activity> makeCallSearchHotelBedsActivities(AvailabilitybyhotelcodeRequest availabilityRQ,
                                                          String client) throws ApiException {
        if (log.isDebugEnabled()) {
            log.debug("makeCallSearchHotelBedsActivities::client: {} ", client);
        }
        if (log.isDebugEnabled()) {
            log.debug("makeCallSearchHotelBedsActivities::availabilityRequest: {} ", availabilityRQ);
        }
        AvailabilityByHotelResponse objectRs = hbClient.availabilitybyhotelcode(client, availabilityRQ);
//        if (log.isDebugEnabled()) {
//            log.debug("makeCallSearchHotelBedsActivities::response: ");
//            log.debug(prettyJson(objectRs));
//        }
        if (log.isDebugEnabled()) {
            log.debug("makeCallSearchHotelBedsActivities::objectRs: {}", objectRs);
        }
        return objectRs.getActivities();
    }

    private Activity makeCallDetailHotelBedsActivities(DetailSimpleRequest detailRQ,
            String client) throws ApiException {
		if (log.isDebugEnabled()) {
			log.debug("makeCallDetailHotelBedsActivities::client: {} ", client);
		}
		if (log.isDebugEnabled()) {
			log.debug("makeCallDetailHotelBedsActivities::availabilityRequest: {} ", detailRQ);
		}
		DetailSimpleResponse objectRs = hbClient.detail(client, detailRQ);
		if (log.isDebugEnabled()) {
		log.debug("makeCallDetailHotelBedsActivities::objectRs: {}", objectRs);
		}
		return objectRs.getActivity();
}

    private BookingResponse makeCallBookingActivity(String client, BookingConfirmRequest bookingConfirmRequest) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("makeCallBookingActivity::client: {} ", client);
        }
        if (log.isDebugEnabled()) {
            log.debug("makeCallBookingActivity::bookingConfirmRequest: {} ", bookingConfirmRequest);
        }
        long timeBefore = System.currentTimeMillis();
        BookingResponse response = null;
        try {
            response = hbClient.bookingConfirm(client, bookingConfirmRequest);
            long timeAfter = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
                log.info("makeCallBookingActivity::time make call = {} millis", timeAfter - timeBefore);
                log.debug("makeCallingHotelBedsActivities::response: ");
                log.debug(prettyJson(response));
            }
        } catch (ApiException e) {
            log.error("makeCallBookingActivity::error: " , e.getMessage(), e);
            throw new Exception("HotelBed:: " + e.getMessage(), e);
        }
        return response;
    }
    /*
    private DetailSimpleResponse makeCallDetailSimple(String client, DetailSimpleRequest request) throws ApiException {
        if (log.isDebugEnabled()) {
            log.debug("makeCallBookingActivity::client: {} ", client);
        }
        if (log.isDebugEnabled()) {
            log.debug("makeCallBookingActivity::request: {} ", request);
        }
        long timeBefore = System.currentTimeMillis();
        DetailSimpleResponse response = hbClient.getActivitiesApi(client).detailSimple(apiKey,
                HotelBedsUtil.getXSignature(apiKey, secret), accept, acceptEncoding, request);
        long timeAfter = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.info("makeCallDetailSimple::time make call = {} millis", timeAfter - timeBefore);
            log.debug("makeCallDetailSimple::response: ");
            log.debug(prettyJson(response));
        }
        return response;
    }
    */

    private BookingResponse makeCallCancel(String client, String language, String bookingReference) throws ApiException {
        String cancellationFlag = "True";
        if (log.isDebugEnabled()) {
            log.debug("makeCallCancel::(client={}, language= {}, bookingReference= {}) ", client, language, bookingReference);
        }
        long timeBefore = System.currentTimeMillis();
        BookingResponse response = hbClient.bookingCancel(client, language, bookingReference);
        long timeAfter = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.info("makeBookingCancel::time make call = {} millis", timeAfter - timeBefore);
            log.debug("makeBookingCancel::response: ");
            log.debug(prettyJson(response));
        }
        return response;
    }

    public static BigDecimal applyInventoryMarkup(BigDecimal nett, BigDecimal gross) throws Exception
    {
        return nett.multiply(HB_MARKUP).setScale(0, RoundingMode.UP);
    }


    private static final BigDecimal HB_MARKUP = new BigDecimal("1.1363636");

    private static String prettyJson(Object someObject) {
        String jsonOutput = gson.toJson(someObject);
        return jsonOutput;
    }
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
}
