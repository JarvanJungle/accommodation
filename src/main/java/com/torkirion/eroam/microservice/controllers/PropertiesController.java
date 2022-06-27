package com.torkirion.eroam.microservice.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.datadomain.Location;
import com.torkirion.eroam.microservice.accommodation.dto.*;
import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryAsync;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryService;
import com.torkirion.eroam.microservice.accommodation.repository.*;
import com.torkirion.eroam.microservice.accommodation.services.*;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationNameSearcher.NameMatch;
import com.torkirion.eroam.microservice.activities.services.ActivityChannelService;
import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemProperty;
import com.torkirion.eroam.microservice.cruise.services.CruiseChannelService;
import com.torkirion.eroam.microservice.datadomain.Country;
import com.torkirion.eroam.microservice.datadomain.CountryRepo;
import com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType;
import com.torkirion.eroam.microservice.events.services.EventsChannelService;
import com.torkirion.eroam.microservice.hirecars.services.HireCarChannelService;
import com.torkirion.eroam.microservice.merchandise.services.MerchandiseChannelService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transfers.services.TransferChannelService;
import com.torkirion.eroam.microservice.transport.services.TransportChannelService;

@RestController
@RequestMapping("/systemproperties/v1")
@Api(value = "System Properties Service API")
@Slf4j
@AllArgsConstructor
public class PropertiesController
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private AccommodationChannelService accommodationChannelService;

	@Autowired
	private ActivityChannelService activityChannelService;

	@Autowired
	private EventsChannelService eventsChannelService;

	@Autowired
	private MerchandiseChannelService merchandiseChannelService;

	@Autowired
	private TransferChannelService transferChannelService;

	@Autowired
	private TransportChannelService transportChannelService;

	@Autowired
	private DataService imsDataService;

	@Autowired
	private CruiseChannelService cruiseChannelService;

	@Autowired
	private HireCarChannelService hireCarChannelService;

	private static int pingCount = 0;

	@ApiOperation(value = "Ping Test Call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Boolean ping()
	{
		try
		{
			log.info("ping::freeMemory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + "M, totalMemory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + "M, usedMemory:"
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "M");
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("ping::error " + e.toString(), e);
			return false;
		}
	}

	@ApiOperation(value = "Get existing Clients")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/systemProperties/clients", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<Set<String>> getSystemPropertyClients()
	{
		log.debug("getSystemPropertyClients::enter");
		try
		{
			Set<String> clients = propertiesDAO.getSiteList();
			Set<String> result = new HashSet<>();
			for (String c : clients)
			{
				if (c != null & !c.equals("null"))
					result.add(c);
			}
			return new ResponseData<>(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getSystemPropertyClients::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Get System Properties")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/systemProperties/{productType}/{client}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<List<SystemProperty>> getSystemProperties(@PathVariable String client,
			@PathVariable com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType productType)
	{
		log.debug("getSystemProperties::enter");
		try
		{
			List<SystemProperty> systemProperties = new ArrayList<>();
			Set<String> channels = propertiesDAO.getSiteChannelList(client);
			for (String channel : channels)
			{
				Map<String, String> channelProperties = null;
				channelProperties = propertiesDAO.getSiteChannelPropertyList(client, channel, productType);
				for (Map.Entry<String, String> entry : channelProperties.entrySet())
				{
					SystemProperty systemProperty = new SystemProperty(client, channel, entry.getKey(), entry.getValue(), productType);
					systemProperties.add(systemProperty);
				}
			}
			return new ResponseData<>(systemProperties);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getSystemProperties::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Get System Properties")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/systemProperties/{client}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<List<SystemProperty>> getSystemProperties(@PathVariable String client)
	{
		log.debug("getSystemProperties::enter");
		try
		{
			List<SystemProperty> systemProperties = new ArrayList<>();
			Set<String> channels = propertiesDAO.getSiteChannelList(client);
			for (String channel : channels)
			{
				Map<String, String> channelProperties = null;
				for ( ProductType productType : com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.values()) 
				{
					channelProperties = propertiesDAO.getSiteChannelPropertyList(client, channel, productType);
					for (Map.Entry<String, String> entry : channelProperties.entrySet())
					{
						SystemProperty systemProperty = new SystemProperty(client, channel, entry.getKey(), entry.getValue(), productType);
						systemProperties.add(systemProperty);
					}
					
				}
			}
			return new ResponseData<>(systemProperties);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getSystemProperties::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Set System Properties")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/systemProperties/{client}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<Boolean> setSystemProperties(@PathVariable String client, @RequestBody List<SystemProperty> systemPropertiesToChange)
	{
		log.debug("setSystemProperties::enter with " + systemPropertiesToChange);
		try
		{
			for (SystemProperty systemPropertytoChange : systemPropertiesToChange)
			{
				if ( !client.matches("[A-Za-z0-9]+") || !systemPropertytoChange.getClient().equals(client))
				{
					log.warn("setSystemProperties::invalid client name " + client);
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Invalid client name"));
				}
				propertiesDAO.saveSiteChannelProperty(client, systemPropertytoChange.getProductType(), systemPropertytoChange.getChannel(), systemPropertytoChange.getParameter(), systemPropertytoChange.getValue());
				if ( "LOCALIMS".equals(systemPropertytoChange.getChannel()) && "enabled".equals(systemPropertytoChange.getParameter()) && "true".equals(systemPropertytoChange.getValue()) )
				{
					log.debug("setSystemProperties::enabling ims");
					imsDataService.createIMS(systemPropertytoChange.getClient());
				}
			}

			return new ResponseData<>(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getSystemProperties::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Get System Property Definitions")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/systemPropertyDefinitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<SystemPropertiesDescription> getSystemPropertyDefinitions()
	{
		log.debug("getSystemPropertyDefinitions::enter");
		try
		{
			SystemPropertiesDescription systemPropertiesDescription = new SystemPropertiesDescription();
			systemPropertiesDescription.getProductTypes().put(com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.ACCOMMODATION,
					accommodationChannelService.getSystemPropertiesDescription());
			systemPropertiesDescription.getProductTypes().put(com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.ACTIVITIES,
					activityChannelService.getSystemPropertiesDescription());
			systemPropertiesDescription.getProductTypes().put(com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.EVENTS, eventsChannelService.getSystemPropertiesDescription());
			systemPropertiesDescription.getProductTypes().put(com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.MERCHANDISE,
					merchandiseChannelService.getSystemPropertiesDescription());
			systemPropertiesDescription.getProductTypes().put(com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.TRANSFERS,
					transferChannelService.getSystemPropertiesDescription());
			systemPropertiesDescription.getProductTypes().put(com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.TRANSPORT,
					transportChannelService.getSystemPropertiesDescription());
			systemPropertiesDescription.getProductTypes().put(com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.CRUISE,
					cruiseChannelService.getSystemPropertiesDescription());
			systemPropertiesDescription.getProductTypes().put(com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType.HIRECAR,
					hireCarChannelService.getSystemPropertiesDescription());
			return new ResponseData<>(systemPropertiesDescription);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getSystemPropertyDefinitions::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

}
