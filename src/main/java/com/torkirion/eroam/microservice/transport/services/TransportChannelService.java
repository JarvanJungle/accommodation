package com.torkirion.eroam.microservice.transport.services;

import com.torkirion.eroam.microservice.cache.AirlineCacheUtil;
import com.torkirion.eroam.microservice.transport.repository.SaveATrainVendorStationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.endpoint.TransportServiceIF;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.CTWService;
import com.torkirion.eroam.microservice.transport.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;

@Service
@AllArgsConstructor
@Slf4j
public class TransportChannelService
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private SaveATrainVendorStationRepository saveATrainVendorStationRepository;

	@Autowired
	private DataService imsDataService;

	@Autowired
	private AirlineCacheUtil airlineCacheUtil;

	public TransportServiceIF getTransportServiceIF(String channel) throws Exception
	{
		log.debug("getTransportServiceIF::channel=" + channel);
		if (channel.equals(IMSService.CHANNEL))
		{
			IMSService imsService = new IMSService(propertiesDAO, imsDataService);
			return imsService;
		}
		if (channel.equals(SaveATrainService.CHANNEL))
		{
			SaveATrainService saveATrainService = new SaveATrainService(propertiesDAO, saveATrainVendorStationRepository);
			return saveATrainService;
		}
		if (channel.equals(CTWService.CHANNEL))
		{
			CTWService ctwService = new CTWService(propertiesDAO, airlineCacheUtil);
			return ctwService;
		}
		log.warn("getTransportServiceIF::unknown channel" + channel);
		return null;
	}
	
	public SystemPropertiesDescription.ProductType getSystemPropertiesDescription()
	{
		SystemPropertiesDescription.ProductType productPropertiesDescription = new SystemPropertiesDescription.ProductType();
		productPropertiesDescription.getChannels().put(IMSService.CHANNEL, IMSService.getSystemPropertiesDescription());
		productPropertiesDescription.getChannels().put(SaveATrainService.CHANNEL, SaveATrainService.getSystemPropertiesDescription());
		return productPropertiesDescription;
	}
}
