package com.torkirion.eroam.microservice.activities.services;

import com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelBedsClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.ims.services.MapperService;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryService;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoService;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCRepo;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;
import com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.activities.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.*;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ActivityChannelService
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private DataService imsDataService;

	@Autowired
	private MapperService mapperService;

	@Autowired
	private ActivityRCRepo activityRCRepo;

	@Autowired
	private ViatorV2ActivityRepo viatorV2ActivityRepo;

	@Autowired
	private ViatorV2ActivityProductOptionRepo viatorV2ActivityProductOptionRepo;
	
	@Autowired
	private ViatorV2ScheduleDataRepo viatorV2ActivityScheduleRepo;

	@Autowired
	private ViatorV2UnavailableDataRepo viatorV2ActivityUnavailableRepo;

	@Autowired
	private BookingQuestionRepo viatorV2BookingQuestionRepo;

	@Autowired
	private ViatorV2Controller viatorV2Controller;

	public ActivityServiceIF getActivityServiceIF(String channel) throws Exception
	{
		log.debug("getActivityServiceIF::channel=" + channel);
		if (HotelbedsService.CHANNEL.equals(channel))
		{
			HotelbedsService hotelbedsService = new HotelbedsService(propertiesDAO);
			return hotelbedsService;
		}
		if (IMSService.CHANNEL.equals(channel))
		{
			IMSService imsService = new IMSService(propertiesDAO, imsDataService, mapperService);
			return imsService;
		}
		if (ViatorV2Service.CHANNEL.equals(channel))
		{
			ViatorV2Service viatorV2Service = new ViatorV2Service(propertiesDAO, mapperService, activityRCRepo, viatorV2ActivityScheduleRepo,
					viatorV2ActivityUnavailableRepo, viatorV2ActivityRepo, viatorV2ActivityProductOptionRepo,
					viatorV2BookingQuestionRepo, viatorV2Controller);
			return viatorV2Service;
		}
		log.warn("getActivityServiceIF::unknown channel" + channel);
		return null;

	}

	public static String getChannelForActivityId(String activityId)
	{
		if (activityId.startsWith(HotelbedsService.CHANNEL_PREFIX))
			return HotelbedsService.CHANNEL;
		if (activityId.startsWith(IMSService.CHANNEL_PREFIX))
			return IMSService.CHANNEL;
		if (activityId.startsWith(ViatorV2Service.CHANNEL_PREFIX))
			return ViatorV2Service.CHANNEL;
		return null;
	}
	
	public SystemPropertiesDescription.ProductType getSystemPropertiesDescription()
	{
		SystemPropertiesDescription.ProductType productPropertiesDescription = new SystemPropertiesDescription.ProductType();
		productPropertiesDescription.getChannels().put(ViatorV2Service.CHANNEL, ViatorV2Service.getSystemPropertiesDescription());
		productPropertiesDescription.getChannels().put(IMSService.CHANNEL, IMSService.getSystemPropertiesDescription());
		productPropertiesDescription.getChannels().put(HotelbedsService.CHANNEL, HotelbedsService.getSystemPropertiesDescription());
		return productPropertiesDescription;
	}
}
