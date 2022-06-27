package com.torkirion.eroam.microservice.cruise.endpoint.traveltek;

import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@AllArgsConstructor
@Slf4j
@Service
public class TravelTekRCController extends RCController
{

	@Resource
	private final TravelTekRCLoader loader;

	public void process(String code) throws Exception
	{
		log.debug("process::entering");
		try
		{
			if (code == null || code.equals("loadRegions"))
				loader.loadRegions();
			if (code == null || code.equals("loadShips"))
				loader.loadShips();
			if (code == null || code.equals("loadCruiseLine"))
				loader.loadCruiseLine();
			if (code == null || code.equals("loadCruiseFlatFile"))
				loader.loadCruiseFlatFile();
		}
		catch (Exception e)
		{
			log.warn("process::caught " + e.toString(), e);
		}
		log.debug("process::end");
	}
}
