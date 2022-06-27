package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailablityCheck;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailablityCheckResult;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.CancelRQ;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ConfirmBooking;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.HoldBooking;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.HoldBookingResponse;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartCancelRQ;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartCancelRS;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartCheckingCancelRQ;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartCheckingCancelRS;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.StartConfirmBookingRS;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViatorV2Interface
{
	private SystemPropertiesDAO properties;

	private ViatorV2APIProperties viatorV2APIProperties;

	private HttpService httpService;

	private final static String AVAILABILITY_CHECK_URI = "partner/availability/check";
	private final static String CONFIRM_BOOKINGS = "partner/bookings/book";
	private final static String HOLD_BOOKING = "partner/bookings/hold";
	private final static String BOOKING_REFERENCE_KEY = "{{booking-reference}}";
	private final static String BOOKING_CANCEL_REG = "partner/bookings/" + BOOKING_REFERENCE_KEY + "/cancel";
	private final static String BOOKING_CANCEL_QUOTE_REG = "partner/bookings/" + BOOKING_REFERENCE_KEY + "/cancel-quote";

	public ViatorV2Interface(SystemPropertiesDAO properties, String site) throws Exception
	{
		this.properties = properties;
		init(site);
	}

	private void init(String site) throws Exception
	{
		log.debug("init::entering with site " + site);
		viatorV2APIProperties = new ViatorV2APIProperties(properties, site);
		httpService = new ViatorV2HttpService(viatorV2APIProperties);
	}

	public AvailablityCheckResult startAvailabilityCheck(AvailablityCheck availablityCheck) {
		log.debug("startAvailabilityCheck::availablityCheck: {}", availablityCheck);
		String response = httpService.doCallPost(AVAILABILITY_CHECK_URI, availablityCheck);
		log.debug("startAvailabilityCheck::response=" + response);
		return JsonUtil.parse(response, AvailablityCheckResult.class);
	}

	public StartConfirmBookingRS startConfirmBooking(ConfirmBooking confirmBooking)  {
		log.debug("startConfirmBooking::confirmBooking: {}", confirmBooking);
		if(viatorV2APIProperties.bypassBooking) {
			return makeBypassBookingConfirmBook(confirmBooking);
		}
		String response = httpService.doCallPost(CONFIRM_BOOKINGS, confirmBooking);
		log.debug("startConfirmBooking::response={}", response);
		StartConfirmBookingRS bookingRS = JsonUtil.parse(response, StartConfirmBookingRS.class);
		return bookingRS;
	}

	private StartConfirmBookingRS makeBypassBookingConfirmBook(ConfirmBooking confirmBooking) {
		log.info("makeBypassBooking::ConfirmBook{}", confirmBooking);
		StartConfirmBookingRS startConfirmBookingRS = new StartConfirmBookingRS();
		startConfirmBookingRS.setStatus("CONFIRMED");
		startConfirmBookingRS.setBookingRef("test_13243435464");
		StartConfirmBookingRS.CancellationPolicy cancellationPolicy = new StartConfirmBookingRS.CancellationPolicy();
		cancellationPolicy.setDescription("For a full refund, cancel at least 24 hours before the scheduled departure time.");
		startConfirmBookingRS.setCancellationPolicy(cancellationPolicy);
		return startConfirmBookingRS;
	}

	public HoldBookingResponse startHoldBooking(HoldBooking holdBooking) {
		log.debug("startHoldBooking::holdBooking: {}", holdBooking);
		if(viatorV2APIProperties.bypassBooking) {
			return makeBypassBookingStartHoldBooking(holdBooking);
		}
		String response = httpService.doCallPost(HOLD_BOOKING, holdBooking);
		log.debug("startHoldBooking::response: {}", response);
		HoldBookingResponse holdBookingResponse = JsonUtil.parse(response, HoldBookingResponse.class);
		log.debug("startHoldBooking::holdBookingResponse: {}", holdBookingResponse);
		return holdBookingResponse;
	}

	private HoldBookingResponse makeBypassBookingStartHoldBooking(HoldBooking holdBooking) {
		log.info("makeBypassBooking::StartHoldBooking::holdBooking: {}", holdBooking);
		HoldBookingResponse response = new HoldBookingResponse();
		//response.setBookingRef(holdBooking.get);
		HoldBookingResponse.BookingHoldInfo info = new HoldBookingResponse.BookingHoldInfo();
		HoldBookingResponse.Pricing pricing = new HoldBookingResponse.Pricing();
		pricing.setStatus("HOLDING");
		info.setPricing(pricing);
		info.setPricing(pricing);
		response.setBookingHoldInfo(info);
		return response;
	}

	public StartCheckingCancelRS startCheckingCancel(StartCheckingCancelRQ cancelRQ) {
		log.debug("startCheckingCancel::cancelRQ: {}", cancelRQ);
		if(viatorV2APIProperties.bypassBooking) {
			return makeBypassBookingStartCheckingCancel(cancelRQ);
		}
		String uri = BOOKING_CANCEL_QUOTE_REG.replace(BOOKING_REFERENCE_KEY, cancelRQ.getBookingItemReference());
		String response = httpService.doCallGet(uri, null);
		log.debug("startCheckingCancel::response: {}", response);
		return JsonUtil.parse(response, StartCheckingCancelRS.class);
	}

	private StartCheckingCancelRS makeBypassBookingStartCheckingCancel(StartCheckingCancelRQ cancelRQ) {
		log.info("make::bypassBooking::StartCheckingCancel: cancelRQ: {}", cancelRQ);
		StartCheckingCancelRS checkingCancelRS = new StartCheckingCancelRS();
		checkingCancelRS.setStatus("CANCELLABLE");
		StartCheckingCancelRS.RefundDetails refundDetails = new StartCheckingCancelRS.RefundDetails();
		refundDetails.setItemPrice(100);
		refundDetails.setRefundAmount(70);
		refundDetails.setCurrencyCode("AUD");
		checkingCancelRS.setRefundDetails(refundDetails);
		return checkingCancelRS;
	}

	public StartCancelRS startCancel(StartCancelRQ startCancelRQ) {
		log.debug("startCancel::startCancelRQ: {}", startCancelRQ);
		if(viatorV2APIProperties.bypassBooking) {
			return makeBypassBookingStartCancel(startCancelRQ);
		}
		String uri = BOOKING_CANCEL_REG.replace(BOOKING_REFERENCE_KEY, startCancelRQ.getBookingItemReference());
		String cancelRS = httpService.doCallPost(uri, CANCELRQ_DEFAULT);
		log.debug("startCancel::cancelRS: {}", cancelRS);
		if(cancelRS == null || "".equals(cancelRS)) {
			return null;
		}
		StartCancelRS startCancelRS = JsonUtil.parse(cancelRS, StartCancelRS.class);
		return startCancelRS;
	}

	private StartCancelRS makeBypassBookingStartCancel(StartCancelRQ startCancelRQ) {
		log.info("make::bypassBooking::StartCancel: startCancelRQ: {}", startCancelRQ);
		StartCancelRS startCancelRS = new StartCancelRS();
		startCancelRS.setBookingId(startCancelRQ.getBookingItemReference());
		startCancelRS.setStatus("CONFIRMED");
		return startCancelRS;
	}

	private static final CancelRQ CANCELRQ_DEFAULT = new CancelRQ("Customer_Service.I_canceled_my_entire_trip");
}
