package com.torkirion.eroam.microservice.merchandise.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.merchandise.endpoint.MerchandiseServiceIF;
import com.torkirion.eroam.microservice.merchandise.endpoint.ims.IMSService;
import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@Service
@AllArgsConstructor
@Slf4j
public class MerchandiseChannelService
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private DataService imsDataService;

	public MerchandiseServiceIF getMerchandiseServiceIF(String channel) throws Exception
	{
		log.debug("getMerchandiseServiceIF::channel=" + channel);
		if (channel.equals(IMSService.CHANNEL))
		{
			IMSService imsService = new IMSService(propertiesDAO, imsDataService);
			return imsService;
		}
		log.warn("getMerchandiseServiceIF::unknown channel" + channel);
		return null;
	}

	public static String getChannelForMerchandiseId(String merchandiseId)
	{
		if (merchandiseId.startsWith(IMSService.CHANNEL_PREFIX))
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
