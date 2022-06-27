package com.torkirion.eroam.microservice.hirecars.services;

import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.hirecars.datadomain.CarSearchEntryRCRepo;
import com.torkirion.eroam.microservice.hirecars.endpoint.HireCarServiceIF;
import com.torkirion.eroam.microservice.hirecars.endpoint.carnect.CarNectService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class HireCarChannelService
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private CarSearchEntryRCRepo carSearchEntryRCRepo;

	public HireCarServiceIF getHireCarServiceIF(String channel) throws Exception
	{
		log.debug("getActivityServiceIF::channel=" + channel);
		if (CarNectService.CHANNEL.equals(channel))
		{
			CarNectService carNectService = new CarNectService(propertiesDAO, carSearchEntryRCRepo);
			return carNectService;
		}
		log.warn("getActivityServiceIF::unknown channel" + channel);
		return null;

	}

	public static String getChannelForActivityId(String activityId)
	{
		if (activityId.startsWith(CarNectService.CHANNEL_PREFIX))
			return CarNectService.CHANNEL;
		return null;
	}
	
	public SystemPropertiesDescription.ProductType getSystemPropertiesDescription()
	{
		SystemPropertiesDescription.ProductType productPropertiesDescription = new SystemPropertiesDescription.ProductType();
		productPropertiesDescription.getChannels().put(CarNectService.CHANNEL, CarNectService.getSystemPropertiesDescription());
		return productPropertiesDescription;
	}
}
