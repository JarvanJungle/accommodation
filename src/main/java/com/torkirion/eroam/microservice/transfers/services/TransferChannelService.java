package com.torkirion.eroam.microservice.transfers.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.StaticRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsRCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoCache;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoRCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoService;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.BoardTypeInclusionRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.FacilityRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.LocationRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.ProvinceRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.SupplierBoardTypeRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.YalagoCountryRepo;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.events.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transfers.endpoint.TransferServiceIF;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.AirportRepo;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.AirportTerminalRepo;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.JayrideService;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.JayrideStaticLoader;

@Service
@AllArgsConstructor
@Slf4j
public class TransferChannelService
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private AccommodationRCService accommodationRCService;

	@Autowired
	private AirportRepo airportRepo;
	
	@Autowired
	private AirportTerminalRepo airportTerminalRepo;

	@Autowired
	private JayrideStaticLoader jayrideLoader;

	public TransferServiceIF getTransferService(String channel) throws Exception
	{
		log.debug("getTransferService::channel=" + channel);
		if (channel.equals(JayrideService.CHANNEL))
		{
			JayrideService jayrideService = new JayrideService(propertiesDAO, accommodationRCService, airportRepo, airportTerminalRepo, jayrideLoader);
			return jayrideService;
		}
		log.warn("getTransferService::unknown channel" + channel);
		return null;
	}

	public static String getChannelForTransferId(String transferId)
	{
		if ( transferId.startsWith(JayrideService.CHANNEL_PREFIX))
			return JayrideService.CHANNEL;
		return null;
	}
	
	public SystemPropertiesDescription.ProductType getSystemPropertiesDescription()
	{
		SystemPropertiesDescription.ProductType productPropertiesDescription = new SystemPropertiesDescription.ProductType();
		productPropertiesDescription.getChannels().put(JayrideService.CHANNEL, JayrideService.getSystemPropertiesDescription());
		return productPropertiesDescription;
	}
}
