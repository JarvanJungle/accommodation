package com.torkirion.eroam.microservice.accommodation.services;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.*;
import com.torkirion.eroam.microservice.accommodation.endpoint.youtravel.YoutravelRCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.youtravel.YoutravelService;
import com.torkirion.eroam.microservice.accommodation.endpoint.youtravel.YoutravelStaticRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.ims.services.MapperService;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.StaticRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.InnstantRCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.InnstantService;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryService;
import com.torkirion.eroam.microservice.accommodation.endpoint.sabrecsl.SabreCSLService;
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
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@Service
@AllArgsConstructor
@Slf4j
public class AccommodationChannelService
{
	@Autowired
	private InnstantRCController innstantRCController;

	@Autowired
	private InnstantRCLoader innstantRCLoader;

	@Autowired
	private YalagoRCController yalagoRCController;

	@Autowired
	private HotelbedsRCController hotelbedsRCController;

	@Autowired
	private YoutravelRCController youtravelRCController;

	@Autowired
	private AccommodationRCService rcService;

	//@Autowired
	private com.torkirion.eroam.ims.services.DataService imsDataService;
	
	@Autowired
	private MapperService imsMapperService;

	@Autowired
	private AccommodationRCService accommodationRCService;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private AccommodationRCRepo accommodationRCRepo;
	@Autowired
	private DestinationSearchRQDataRepo destinationSearchRQDataRepo;

	private static YalagoCache yalagoCache = null;

	// items to build the YalagoCache for the service. Should really be put into a Builder, but had difficulties with Spring Boot
	// service resolution! Feel free to clean up!
	private FacilityRepo facilityRepo;

	@Autowired
	private YalagoCountryRepo countryRepo;

	@Autowired
	private ProvinceRepo provinceRepo;

	@Autowired
	private LocationRepo locationRepo;

	@Autowired
	private BoardTypeInclusionRepo boardTypeInclusionsRepo;

	@Autowired
	private SupplierBoardTypeRepo supplierBoardTypeRepo;

	@Autowired
	private StaticRepo staticRepo;

	@Autowired
	private YoutravelStaticRepo youtravelStaticRepo;
	
	public AccommodationServiceIF getAccommodationService(String channel) throws Exception
	{
		log.debug("getAccommodationService::channel=" + channel);
		if (channel.equals(YalagoService.CHANNEL))
		{
			if (yalagoCache == null)
			{
				yalagoCache = new YalagoCache(facilityRepo, countryRepo, provinceRepo, locationRepo, boardTypeInclusionsRepo, supplierBoardTypeRepo);
			}
			YalagoService yalagoService = new YalagoService(propertiesDAO, accommodationRCRepo, accommodationRCService, yalagoCache);
			return yalagoService;
		}
		if (channel.equals(HotelbedsService.CHANNEL))
		{
			HotelbedsService hotelbedsService = new HotelbedsService(propertiesDAO, staticRepo);
			return hotelbedsService;
		}
		if (channel.equals(YoutravelService.CHANNEL))
		{
			YoutravelService youtravelService = new YoutravelService(propertiesDAO, youtravelStaticRepo);
			return youtravelService;
		}
		if (channel.equals(IMSService.CHANNEL))
		{
			IMSService imsService = new IMSService(imsDataService, imsMapperService);
			return imsService;
		}
		if (channel.equals(SabreCSLService.CHANNEL))
		{
			SabreCSLService sabreCSLService = new SabreCSLService(propertiesDAO, accommodationRCService);
			return sabreCSLService;
		}
		if (channel.equals(InnstantService.CHANNEL))
		{
			InnstantService innstantService = new InnstantService(propertiesDAO, accommodationRCRepo, accommodationRCService, destinationSearchRQDataRepo, innstantRCLoader );
			return innstantService;
		}
		log.warn("getAccommodationService::unknown channel" + channel);
		return null;
	}

	public RCController getRCController(String channel) throws Exception
	{
		log.debug("getRCController::channel=" + channel);
		if (channel.equals(YoutravelService.CHANNEL))
		{
			return youtravelRCController;
		}
		if (channel.equals(YalagoService.CHANNEL))
		{
			return yalagoRCController;
		}
		if (channel.equals(HotelbedsService.CHANNEL))
		{
			return hotelbedsRCController;
		}
		if (channel.equals(InnstantService.CHANNEL))
		{
			return innstantRCController;
		}
		if (channel.equals(IMSService.CHANNEL))
		{
			throw new Exception("IMSService does not implement RCController");
		}
		if (channel.equals(SabreCSLService.CHANNEL))
		{
			throw new Exception("IMSService does not implement RCController");
		}
		log.warn("getRCController::unknown channel" + channel);
		return null;
	}
	
	public static String getChannelForHotelId(String hotelId)
	{
		if ( hotelId.startsWith(YalagoService.CHANNEL_PREFIX))
			return YalagoService.CHANNEL;
		if ( hotelId.startsWith(HotelbedsService.CHANNEL_PREFIX))
			return HotelbedsService.CHANNEL;
		if ( hotelId.startsWith(IMSService.CHANNEL_PREFIX))
			return IMSService.CHANNEL;
		if ( hotelId.startsWith(SabreCSLService.CHANNEL_PREFIX))
			return SabreCSLService.CHANNEL;
		if ( hotelId.startsWith(InnstantService.CHANNEL_PREFIX))
			return InnstantService.CHANNEL;
		if ( hotelId.startsWith(YoutravelService.CHANNEL_PREFIX))
			return YoutravelService.CHANNEL;
		return null;
	}
	
	public void clearChannelCaches()
	{
		if ( log.isDebugEnabled())
			log.debug("clearChannelCaches::enter");
		IMSService imsService = new IMSService(imsDataService, imsMapperService);
		imsService.clearCache();
	}
	
	/**
	 * returns a 'ranking' of a channel, to determine on a merged accommodqtion set, which RC to show.. 
	 * @param channel
	 * @return
	 */
	public int getChannelRCRank(String channel)
	{
		// higher is better!
		switch (channel)
		{
			case InnstantService.CHANNEL: return 5;
			case YalagoService.CHANNEL: return 5;
			case HotelbedsService.CHANNEL: return 10;
			case SabreCSLService.CHANNEL: return 20;
			case IMSService.CHANNEL: return 99;
		}
		return 0;
	}
	
	public SystemPropertiesDescription.ProductType getSystemPropertiesDescription()
	{
		SystemPropertiesDescription.ProductType productPropertiesDescription = new SystemPropertiesDescription.ProductType();
		productPropertiesDescription.getChannels().put(YalagoService.CHANNEL, YalagoService.getSystemPropertiesDescription());
		productPropertiesDescription.getChannels().put(HotelbedsService.CHANNEL, HotelbedsService.getSystemPropertiesDescription());
		productPropertiesDescription.getChannels().put(IMSService.CHANNEL, IMSService.getSystemPropertiesDescription());
		productPropertiesDescription.getChannels().put(SabreCSLService.CHANNEL, SabreCSLService.getSystemPropertiesDescription());
		return productPropertiesDescription;
	}
}
