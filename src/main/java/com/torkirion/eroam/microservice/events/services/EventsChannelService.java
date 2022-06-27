package com.torkirion.eroam.microservice.events.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;
import com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.ViatorV2Service;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.events.endpoint.EventsServiceIF;
import com.torkirion.eroam.microservice.events.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@Service
@AllArgsConstructor
@Slf4j
public class EventsChannelService
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private DataService imsDataService;

	public EventsServiceIF getEventsServiceIF(String channel) throws Exception
	{
		log.debug("getEventsServiceIF::channel=" + channel);
		if (channel.equals(IMSService.CHANNEL))
		{
			IMSService imsService = new IMSService(propertiesDAO, imsDataService);
			return imsService;
		}
		log.warn("getEventsServiceIF::unknown channel" + channel);
		return null;
	}

	public Set<EventsServiceIF> getAllEventsServiceIF() throws Exception
	{
		Set<EventsServiceIF> serviceIFs = new HashSet<>();
		serviceIFs.add(new IMSService(propertiesDAO, imsDataService));
		return serviceIFs;
	}

	public static String getChannelForEventId(String eventId)
	{
		if ( eventId.startsWith(IMSService.CHANNEL_PREFIX))
			return IMSService.CHANNEL;
		return null;
	}
	
	public SystemPropertiesDescription.ProductType getSystemPropertiesDescription()
	{
		SystemPropertiesDescription.ProductType productPropertiesDescription = new SystemPropertiesDescription.ProductType();
		productPropertiesDescription.getChannels().put(IMSService.CHANNEL, IMSService.getSystemPropertiesDescription());
		return productPropertiesDescription;
	}
}
