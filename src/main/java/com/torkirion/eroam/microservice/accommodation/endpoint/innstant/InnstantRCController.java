package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.InnstantRCHotelsStatic;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
@Slf4j
public class InnstantRCController extends RCController
{
	@Autowired
	private InnstantRCLoader loader;

	@Autowired
	private InnstantRCDataSearch dataSearch;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	private static final String SITE_DEFAULT = "eroam";

	@Async
	public void process(String code) throws Exception
	{
		log.debug("processHotels::entering");
		try
		{
			InnstantRCAPIProperties innstantRCAPIProperties = new InnstantRCAPIProperties(propertiesDAO, SITE_DEFAULT);
			HttpService httpService = new InnstantRCHttpService(innstantRCAPIProperties);

			if ("COUNTRY".equals(code))
			{
				try
				{
					loader.loadCountry(httpService);
				}
				catch (Exception e)
				{
					log.error("Loader Country error: ", e);
				}
			}
			if ("HOTELS".equals(code))
			{
				Integer hotelFromLoop = propertiesDAO.getProperty(SITE_DEFAULT, InnstantService.CHANNEL, "hotelFromLoop", 0);
				loader.loadHotels(httpService, hotelFromLoop);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Scheduled(fixedDelay = 86400000) // 24 hours
	public void deleteDataSearch()
	{
		try
		{
			log.info("deleteDataSearch::entering");
			dataSearch.deleteSearch();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
