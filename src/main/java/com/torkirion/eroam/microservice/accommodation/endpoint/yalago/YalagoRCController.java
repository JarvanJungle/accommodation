package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class YalagoRCController extends RCController
{
	@Autowired
	private YalagoRCLoader loader;

	public void process(String code) throws Exception
	{
		try
		{
			log.info("process:entering");
			loader.fetchLookupData();
			
			int processCount = 1;
			//while (processCount > 0)
			//{
			processCount = loader.convertToRC(code);
			//}
			log.info("process:finished, processCount=" + processCount);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
