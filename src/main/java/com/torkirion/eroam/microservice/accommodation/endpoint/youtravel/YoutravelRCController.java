package com.torkirion.eroam.microservice.accommodation.endpoint.youtravel;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
@Slf4j
public class YoutravelRCController extends RCController
{
	@Autowired
	private YoutravelRCLoader youtravelRCLoader;

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

	public Future<Boolean> startHotels() throws Exception
	{
		log.debug("startHotels");
		Future<Boolean> hotelResult = processHotels();
		log.debug("process::finished, hotelResult");
		return hotelResult; 
	}

	@Override
	public void process(String code) throws Exception
	{
		log.debug("process::code=" + code);
		if ( "LOOKUPS".equals(code))
			processLookups();
		if ( "HOTELS".equals(code))
			processHotels();
	}

	@Async
	protected Future<Boolean> processLookups() throws Exception
	{

		YoutravelAPIProperties hotelbedsProperties = new YoutravelAPIProperties(propertiesDAO, SITE_DEFAULT);
		HttpService httpService = new YoutravelHttpService(hotelbedsProperties);

		Boolean loadLookupTables = propertiesDAO.getProperty(SITE_DEFAULT, "HOTELBEDS", "loadLookupTables", true);
		log.debug("processLookups::loadLookupTables=" + loadLookupTables);
		if (loadLookupTables)
		{/*
			hotelbedsRCLoader.loadCountries(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadAccommodations(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadTerminals(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadGroupCategories(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadChains(httpService);
			Thread.sleep(2000);
*/
			youtravelRCLoader.loadBoards(httpService);
			Thread.sleep(2000);
/*
			hotelbedsRCLoader.loadSegments(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadRoomsStatic(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadCategories(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadImageTypes(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadFacilities(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadFacilityGroups(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadPromotions(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadIssues(httpService);
			Thread.sleep(2000);

			hotelbedsRCLoader.loadRateComments(httpService);
			Thread.sleep(2000); */
		}
		return new AsyncResult<Boolean>(true);
	}

	@Async
	public Future<Boolean> processHotels() throws Exception
	{
		log.debug("processHotels::entering");

		YoutravelAPIProperties youtravelProperties = new YoutravelAPIProperties(propertiesDAO, SITE_DEFAULT);
		HttpService httpService = new YoutravelHttpService(youtravelProperties);

		Integer hotelFromLoop = propertiesDAO.getProperty(SITE_DEFAULT, "YOUTRAVEL", "hotelFromLoop", 0);
		Boolean loadhotels = propertiesDAO.getProperty(SITE_DEFAULT, "YOUTRAVEL", "loadhotels", true);
		if (loadhotels)
		{
			Optional<Integer> reLoop = null;
			String hotelcodes = propertiesDAO.getProperty(SITE_DEFAULT, "YOUTRAVEL", "hotelcodes", "");
			youtravelRCLoader.loadHotels(httpService, hotelFromLoop, hotelcodes);
		}
		return new AsyncResult<Boolean>(true);
	}
}
