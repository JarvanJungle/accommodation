package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ViatorV2Controller 
{
	@Autowired
	private ViatorV2Loader viatorV2Loader;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	private static final String SITE_DEFAULT = "eroam";

	public Future<Boolean> startLookups() throws Exception
	{
		log.info("startLookups::");
		Future<Boolean> lookupResult = processLookups();
		log.debug("startLookups::finished");
		return lookupResult;
	}

	public Future<Boolean> startActivities() throws Exception
	{
		log.info("startActivities::");
		Future<Boolean> activityResult = processActivities();
		log.debug("startActivities::finished");
		return activityResult;
	}

	@Async
	protected Future<Boolean> processLookups() throws Exception
	{
		log.debug("processLookups::enter");
		ViatorV2APIProperties viatorV2APIProperties = new ViatorV2APIProperties(propertiesDAO, SITE_DEFAULT);
		HttpService httpService = new ViatorV2HttpService(viatorV2APIProperties, true);

		log.debug("processLookups::enter");
		viatorV2Loader.loadDestinations(httpService);
		viatorV2Loader.loadBookingQuestions(httpService);
		Set<String> locationCodes = new HashSet<>();
		locationCodes.add("LOC-6eKJ+or5y8o99Qw0C8xWyFQoc/UzVbfx5W1lPfYMTuk=");
		locationCodes.add("LOC-f698f2a1-a53a-46bb-8708-3d45bf740f59");
		locationCodes.add("CONTACT_SUPPLIER_LATER");
		locationCodes.add("MEET_AT_DEPARTURE_POINT");
		viatorV2Loader.loadLocations(httpService, locationCodes);

		return new AsyncResult<Boolean>(true);
	}

	@Async
	protected Future<Boolean> processActivities() throws Exception
	{
		log.debug("processActivities::enter");
		ViatorV2APIProperties viatorV2APIProperties = new ViatorV2APIProperties(propertiesDAO, SITE_DEFAULT);
		HttpService httpService = new ViatorV2HttpService(viatorV2APIProperties);

		log.debug("processActivities::enter");

		ThreadPoolExecutor executor =
				(ThreadPoolExecutor) Executors.newFixedThreadPool(2);
		executor.submit(() -> viatorV2Loader.loadActivityRC(httpService));
		executor.submit(() -> viatorV2Loader.loadAvailability(httpService));
//		viatorV2Loader.loadAvailability(httpService);
//		LocalDateTime oldestUpdate = viatorV2Loader.loadActivityRC(httpService);
//		LocalDateTime oldestAvailUpdate = viatorV2Loader.loadAvailability(httpService);
//		viatorV2Loader.loadSingleActivityRC(httpService, "30621P7");
//		viatorV2Loader.loadSingleAvailability(httpService, "30621P7");
//
//		viatorV2Loader.loadSingleActivityRC(httpService, "30621P6");
//		viatorV2Loader.loadSingleAvailability(httpService, "30621P6");
//
//		viatorV2Loader.loadSingleActivityRC(httpService, "43433P3");
//		viatorV2Loader.loadSingleAvailability(httpService, "43433P3");

//		viatorV2Loader.loadSingleActivityRC(httpService, "64464P40");
//		viatorV2Loader.loadSingleAvailability(httpService, "64464P40");

//		viatorV2Loader.loadSingleActivityRC(httpService, "5010SYDNEY");
//		viatorV2Loader.loadSingleAvailability(httpService, "5010SYDNEY");

		return new AsyncResult<Boolean>(true);
	}
}
