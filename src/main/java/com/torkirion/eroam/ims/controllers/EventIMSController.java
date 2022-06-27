package com.torkirion.eroam.ims.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.http.HttpMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.events.services.EventsSearchService;
import com.torkirion.eroam.ims.Functions;
import com.torkirion.eroam.ims.apidomain.APIError;
import com.torkirion.eroam.ims.apidomain.Activity;
import com.torkirion.eroam.ims.apidomain.ActivitySale;
import com.torkirion.eroam.ims.apidomain.Event;
import com.torkirion.eroam.ims.apidomain.EventAllotment;
import com.torkirion.eroam.ims.apidomain.EventClassification;
import com.torkirion.eroam.ims.apidomain.EventMerchandiseAPILink;
import com.torkirion.eroam.ims.apidomain.EventSale;
import com.torkirion.eroam.ims.apidomain.EventSeries;
import com.torkirion.eroam.ims.apidomain.EventSupplier;
import com.torkirion.eroam.ims.apidomain.EventType;
import com.torkirion.eroam.ims.apidomain.EventVenue;
import com.torkirion.eroam.ims.apidomain.Rates;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.apidomain.Roomtypes;
import com.torkirion.eroam.ims.apidomain.Seasons;
import com.torkirion.eroam.ims.apidomain.Specials;
import com.torkirion.eroam.ims.apidomain.SupplierSummary;
import com.torkirion.eroam.ims.datadomain.EventMerchandiseLink;
import com.torkirion.eroam.ims.datadomain.Merchandise;
import com.torkirion.eroam.ims.services.*;

@RestController
@RequestMapping("/eventims/v1")
@Api(value = "Event IMS API")
@Slf4j
@AllArgsConstructor
public class EventIMSController
{
	@Autowired
	private DataService dataService;

	@Autowired
	private MapperService mapperService;

	@Autowired
	private EventsSearchService eventsSearchService;

	@Autowired
	private ImportService importService;

	@ApiOperation(value = "Ping Test Call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> ping(HttpServletRequest httpRequest)
	{
		log.debug("ping::enter");
		try
		{
			return new ResponseData<>(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("ping::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list event types")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventType>> listTypes()
	{
		log.debug("listEventTypes::enter");
		try
		{
			List<EventType> all = mapperService.mapEventTypes(dataService.getEventTypeRepo().findAll());
			SortedSet<EventType> sortedTypes = new TreeSet<>(new EventType.EventTypeSorterByName());
			sortedTypes.addAll(all);
			return new ResponseData<>(new ArrayList<EventType>(sortedTypes));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listEventTypes::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "add event types")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/type/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventType>> createType(@RequestBody String name)
	{
		log.debug("createEventType::enter");
		try
		{
			List<com.torkirion.eroam.ims.datadomain.EventType> existings = dataService.getEventTypeRepo().findByName(name);
			if (existings == null || existings.size() == 0)
			{
				com.torkirion.eroam.ims.datadomain.EventType data = new com.torkirion.eroam.ims.datadomain.EventType();
				data.setName(name);
				dataService.getEventTypeRepo().save(data);
			}
			List<EventType> all = mapperService.mapEventTypes(dataService.getEventTypeRepo().findAll());
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("createEventType::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list event series")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/series", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventSeries>> listSeries()
	{
		log.debug("listSeries::enter");
		try
		{
			List<EventSeries> all = mapperService.mapEventSeries(dataService.getEventSeriesRepo().findAll());
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listSeries::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update event series")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/series/updateOLD", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<EventSeries> updateSeriesOLD(@RequestBody EventSeries eventSeries)
	{
		log.debug("updateSeries::enter for " + eventSeries.getId());
		try
		{
			List<com.torkirion.eroam.ims.datadomain.EventType> existings = dataService.getEventTypeRepo().findByName(eventSeries.getType());
			if (existings == null || existings.size() == 0)
			{
				log.error("readSeries::eventType not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "eventType " + eventSeries.getType() + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.EventType eventType = existings.get(0);
			com.torkirion.eroam.ims.datadomain.EventSeries dataFromAPI = mapperService.mapEventSeries(eventSeries, eventType);
			Optional<com.torkirion.eroam.ims.datadomain.EventSeries> dataOpt = Optional.empty();
			if (eventSeries.getId() != null)
				dataOpt = dataService.getEventSeriesRepo().findById(eventSeries.getId());
			com.torkirion.eroam.ims.datadomain.EventSeries dataFromDB = null;
			com.torkirion.eroam.ims.datadomain.EventSeries saveData = null;
			boolean reSave = false;
			if (dataOpt.isPresent())
			{
				dataFromDB = dataOpt.get();
				// copy old merchandise links for now, update them after the main save
				dataFromAPI.setEventMerchandiseLinks(dataFromDB.getEventMerchandiseLinks());
				saveData = dataFromDB;
				reSave = true;
			}
			else
			{
				log.debug("updateSeries::saving dataFromAPI:" + dataFromAPI);
				saveData = dataService.getEventSeriesRepo().save(dataFromAPI);
			}
			dataFromAPI.setLastUpdated(LocalDateTime.now());
			eventType.getSeries().add(dataFromAPI);
			dataService.getEventTypeRepo().save(eventType);

			EventSeries testApi = mapperService.mapEventSeries(dataFromAPI);
			if (eventSeries.getEventMerchandiseLinks() == null)
				eventSeries.setEventMerchandiseLinks(new ArrayList<>());
			if (testApi.getEventMerchandiseLinks() == null)
				testApi.setEventMerchandiseLinks(new ArrayList<>());
			for (EventMerchandiseAPILink mlApi : eventSeries.getEventMerchandiseLinks())
			{
				if (mlApi.getEventSeriesId() == null || mlApi.getEventSeriesId() == 0)
					mlApi.setEventSeriesId(eventSeries.getId());
			}
			if (!testApi.getEventMerchandiseLinks().equals(eventSeries.getEventMerchandiseLinks()))
			{
				log.debug("updateSeries::differences in MerchandiseLinks, data(" + testApi.getEventMerchandiseLinks().size() + ")=" + testApi.getEventMerchandiseLinks() + ", api("
						+ eventSeries.getEventMerchandiseLinks().size() + ")=" + eventSeries.getEventMerchandiseLinks());
				for (EventMerchandiseAPILink apiMl : eventSeries.getEventMerchandiseLinks())
				{
					if (!testApi.getEventMerchandiseLinks().contains(apiMl))
					{
						// new!
						EventMerchandiseLink dataLink = new EventMerchandiseLink();
						dataLink.setEventSeries(saveData);
						Optional<Merchandise> merchandiseOpt = dataService.getMerchandiseRepo().findById(apiMl.getMerchandiseId());
						if (merchandiseOpt.isPresent())
						{
							dataLink.setMerchandise(merchandiseOpt.get());
							dataLink.setMandatoryInclusion(apiMl.getMandatoryInclusion());
							log.debug("updateSeries::adding ML:" + dataLink);
							dataService.getEventMerchandiseLinkRepo().save(dataLink);
						}
					}
				}
				for (EventMerchandiseAPILink dataMl : testApi.getEventMerchandiseLinks())
				{
					if (!eventSeries.getEventMerchandiseLinks().contains(dataMl))
					{
						// delete!
						Optional<Merchandise> merchandiseOpt = dataService.getMerchandiseRepo().findById(dataMl.getMerchandiseId());
						if (merchandiseOpt.isPresent())
						{
							log.debug("updateSeries::deleting ML:" + dataMl);
							int deleteCount = dataService.getEventMerchandiseLinkRepo().deleteByEventSeriesAndMerchandise(saveData, merchandiseOpt.get());
							log.debug("updateSeries::deleted " + deleteCount);
							for (Iterator<EventMerchandiseLink> i = saveData.getEventMerchandiseLinks().iterator(); i.hasNext();)
							{
								EventMerchandiseLink element = i.next();
								if (element.getEventSeries().getId().intValue() == dataMl.getEventSeriesId().intValue() && element.getMerchandise().getId().intValue() == dataMl.getMerchandiseId().intValue())
								{
									i.remove();
								}
							}
							reSave = true;
						}
					}
				}
				for (EventMerchandiseAPILink mlData : testApi.getEventMerchandiseLinks())
				{
					for (EventMerchandiseAPILink mlApi : eventSeries.getEventMerchandiseLinks())
					{
						if (mlData.equals(mlApi) && !mlData.getMandatoryInclusion().equals(mlApi.getMandatoryInclusion()))
						{
							// update!
							log.debug("updateSeries::updating ML:" + mlData + " to " + mlApi);
						}
					}
				}
			}
			else
			{
				log.debug("updateSeries::MerchandiseLinsk same:data=" + testApi.getEventMerchandiseLinks() + ", api=" + eventSeries.getEventMerchandiseLinks());
			}
			if (reSave)
			{
				log.debug("updateSeries::resaving " + saveData);
				saveData = dataService.getEventSeriesRepo().save(saveData);
			}

			EventSeries newAPi = mapperService.mapEventSeries(saveData);
			eventsSearchService.clearSearchCache();
			return new ResponseData<>(newAPi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateSeries::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update event series")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/series/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<EventSeries> updateSeries(@RequestBody EventSeries eventSeries)
	{
		log.debug("updateSeries::enter for " + eventSeries.getId());
		try
		{
			log.info("updateSeries::temporarily removing EventSeries <=> Merchandise linking");
			eventSeries.setEventMerchandiseLinks(null);
			
			List<com.torkirion.eroam.ims.datadomain.EventType> eventTypes = dataService.getEventTypeRepo().findByName(eventSeries.getType());
			if (eventTypes == null || eventTypes.size() == 0)
			{
				log.error("readSeries::eventType not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "eventType " + eventSeries.getType() + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.EventType eventType = eventTypes.get(0);
			com.torkirion.eroam.ims.datadomain.EventSeries dataFromAPI = mapperService.mapEventSeries(eventSeries, eventType);
			
			com.torkirion.eroam.ims.datadomain.EventSeries dataFromDB = null;
			Optional<com.torkirion.eroam.ims.datadomain.EventSeries> dataOpt = Optional.empty();
			if (eventSeries.getId() != null)
			{
				dataOpt = dataService.getEventSeriesRepo().findById(eventSeries.getId());
				if (dataOpt.isPresent())
					dataFromDB = dataOpt.get();
			}
			
			if ( dataFromDB == null )
			{
				log.debug("updateSeries::new Series");
				dataFromAPI.setLastUpdated(LocalDateTime.now());
				eventType.getSeries().add(dataFromAPI);
				dataService.getEventTypeRepo().save(eventType);
				log.debug("updateSeries::saving new " + dataFromAPI);
				com.torkirion.eroam.ims.datadomain.EventSeries saveData = dataService.getEventSeriesRepo().save(dataFromAPI);
				if ( eventSeries.getEventMerchandiseLinks() != null )
				{
					for ( EventMerchandiseAPILink apiMl : eventSeries.getEventMerchandiseLinks())
					{
						EventMerchandiseLink dataLink = new EventMerchandiseLink();
						dataLink.setEventSeries(saveData);
						Optional<Merchandise> merchandiseOpt = dataService.getMerchandiseRepo().findById(apiMl.getMerchandiseId());
						if (merchandiseOpt.isPresent())
						{
							dataLink.setMerchandise(merchandiseOpt.get());
							dataLink.setMandatoryInclusion(apiMl.getMandatoryInclusion());
							saveData.getEventMerchandiseLinks().add(dataLink);
							log.debug("updateSeries::adding merch link " + dataLink);
							dataService.getEventMerchandiseLinkRepo().save(dataLink);
						}
					}
				}
				eventType.getSeries().add(saveData);
				dataService.getEventTypeRepo().save(eventType);
				
				EventSeries newAPi = mapperService.mapEventSeries(saveData);
				eventsSearchService.clearSearchCache();
				return new ResponseData<>(newAPi);
			}
			
			// OK, from here, we're updating
			log.debug("updateSeries::update series, existing series is " + dataFromDB);
			BeanUtils.copyProperties(dataFromAPI, dataFromDB, "eventType", "events", "eventMerchandiseLinks");
			dataFromDB.setLastUpdated(LocalDateTime.now());
			if ( eventType.getId().intValue() != dataFromDB.getEventType().getId().intValue() )
			{
				dataFromDB.setEventType(eventType);
			}
			for ( EventMerchandiseLink dataML : dataFromDB.getEventMerchandiseLinks())
			{
				log.debug("updateSeries::delete Merch link " + dataML.getId());
				dataService.getEventMerchandiseLinkRepo().deleteById(dataML.getId());
			}
			dataFromDB.getEventMerchandiseLinks().clear();
			dataService.getEventMerchandiseLinkRepo().deleteByEventSeries(dataFromDB);
			log.debug("updateSeries::saving existing " + dataFromDB);
			com.torkirion.eroam.ims.datadomain.EventSeries saveData = dataService.getEventSeriesRepo().save(dataFromDB);
			log.debug("updateSeries::saved=" + saveData);
			
			saveData.getEventMerchandiseLinks().clear();
			if ( eventSeries.getEventMerchandiseLinks() != null )
			{
				for ( EventMerchandiseAPILink apiMl : eventSeries.getEventMerchandiseLinks())
				{
					EventMerchandiseLink dataLink = new EventMerchandiseLink();
					dataLink.setEventSeries(saveData);
					Optional<Merchandise> merchandiseOpt = dataService.getMerchandiseRepo().findById(apiMl.getMerchandiseId());
					if (merchandiseOpt.isPresent())
					{
						dataLink.setMerchandise(merchandiseOpt.get());
						dataLink.setMandatoryInclusion(apiMl.getMandatoryInclusion());
						saveData.getEventMerchandiseLinks().add(dataLink);
						log.debug("updateSeries::adding merch link " + dataLink);
						dataService.getEventMerchandiseLinkRepo().save(dataLink);
					}
				}
			}
			
			EventSeries newAPi = mapperService.mapEventSeries(saveData);
			eventsSearchService.clearSearchCache();
			return new ResponseData<>(newAPi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateSeries::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read event series")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/series/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventSeries> readSeries(@PathVariable Integer id)
	{
		log.debug("readSeries::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventSeries> dataOpt = dataService.getEventSeriesRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readSeries::event series not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "eventSeries " + id + " not found"));
			}
			EventSeries api = mapperService.mapEventSeries(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readSeries::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete event series")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/series/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> deleteSeries(@PathVariable Integer id)
	{
		log.debug("deleteSeries::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventSeries> dataOpt = dataService.getEventSeriesRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("deleteSeries::event venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Series " + id + " not found"));
			}
			if (dataOpt.get().getEvents() != null && dataOpt.get().getEvents().size() > 0)
			{
				log.error("deleteSeries::series still has events");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Series has events"));
			}
			if (dataOpt.get().getEventMerchandiseLinks() != null && dataOpt.get().getEventMerchandiseLinks().size() > 0)
			{
				log.error("deleteSeries::series still has merchandise");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Series has merchandise"));
			}
			dataService.getEventSeriesRepo().delete(dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteSeries::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list event suppliers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventSupplier>> listSuppliers()
	{
		log.debug("listSuppliers::enter");
		try
		{
			List<EventSupplier> all = mapperService.mapEventSuppliers(dataService.getEventSupplierRepo().findAll());
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listSuppliers::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list operators")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/operators", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Collection<String>> listOperators()
	{
		log.debug("listOperators::enter");
		try
		{
			SortedSet<String> operators = new TreeSet<>();
			for (com.torkirion.eroam.ims.datadomain.Event event : dataService.getEventRepo().findAll())
			{
				if ( event.getOperator() != null && event.getOperator().length() > 0)
				operators.add(event.getOperator());
			}
			return new ResponseData<>(operators);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listOperators::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update event supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventSupplier> updateSupplier(@RequestBody EventSupplier eventSupplier)
	{
		log.debug("updateSupplier::enter for " + eventSupplier.getId());
		try
		{
			com.torkirion.eroam.ims.datadomain.EventSupplier data = mapperService.mapEventSupplier(eventSupplier);
			data.setLastUpdated(LocalDateTime.now());
			com.torkirion.eroam.ims.datadomain.EventSupplier savedData = dataService.getEventSupplierRepo().save(data);

			EventSupplier newAPi = mapperService.mapEventSupplier(savedData);
			eventsSearchService.clearSearchCache();
			return new ResponseData<>(newAPi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read event supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventSupplier> readSupplier(@PathVariable Integer id)
	{
		log.debug("readSupplier::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventSupplier> dataOpt = dataService.getEventSupplierRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readSupplier::event supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "eventSupplier " + id + " not found"));
			}
			EventSupplier api = mapperService.mapEventSupplier(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete event supplier")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/supplier/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> deleteSupplier(@PathVariable Integer id)
	{
		log.debug("deleteSupplier::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventSupplier> dataOpt = dataService.getEventSupplierRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("deleteSupplier::event supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Supplier " + id + " not found"));
			}
			if (dataOpt.get().getEvents() != null && dataOpt.get().getEvents().size() > 0)
			{
				log.error("deleteSupplier::supplier still has events");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Supplier has events"));
			}
			dataService.getEventSupplierRepo().delete(dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteSupplier::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list event venue")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/venue", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventVenue>> listVenues()
	{
		log.debug("listVenues::enter");
		try
		{
			List<EventVenue> all = mapperService.mapEventVenues(dataService.getEventVenueRepo().findAll());
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listVenues::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update event venue")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/venue/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventVenue> updateVenue(@RequestBody EventVenue eventVenue)
	{
		log.debug("updateVenue::enter for " + eventVenue.toString());
		try
		{
			if (eventVenue.getAddress() == null || eventVenue.getAddress().getGeoCoordinates() == null)
			{
				log.error("updateVenue::error, lat/long is null");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Latitude and Longitude must not be null"));
			}

			com.torkirion.eroam.ims.datadomain.EventVenue data = mapperService.mapEventVenue(eventVenue);
			data.setLastUpdated(LocalDateTime.now());
			com.torkirion.eroam.ims.datadomain.EventVenue savedData = dataService.getEventVenueRepo().save(data);

			EventVenue newAPi = mapperService.mapEventVenue(savedData);
			eventsSearchService.clearSearchCache();
			return new ResponseData<>(newAPi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateVenue::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read event venue")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/venue/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventVenue> readVenue(@PathVariable Integer id)
	{
		log.debug("readVenue::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventVenue> dataOpt = dataService.getEventVenueRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readVenue::event venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "eventVenue " + id + " not found"));
			}
			EventVenue api = mapperService.mapEventVenue(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readVenue::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete event venue")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/venue/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> deleteVenue(@PathVariable Integer id)
	{
		log.debug("deleteVenue::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventVenue> dataOpt = dataService.getEventVenueRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("deleteVenue::event venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Venue " + id + " not found"));
			}
			if (dataOpt.get().getEvents() != null && dataOpt.get().getEvents().size() > 0)
			{
				log.error("deleteVenue::venue still has events");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Venue has events"));
			}
			dataService.getEventVenueRepo().delete(dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteVenue::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list event")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<Event>> listEvents(@RequestParam(required = false) Long seriesId)
	{
		log.debug("listEvents::enter");
		try
		{
			List<Event> all = mapperService.mapEvents(dataService.getEventRepo().findAll());
			if ( seriesId != null )
			{
				all = all.stream().filter(e -> e.getSeriesId().longValue() == seriesId.longValue()).collect(Collectors.toList());
			}
			SortedSet<Event> sortedTypes = new TreeSet<>(new Event.EventSorterByDate());
			sortedTypes.addAll(all);
			return new ResponseData<>(new ArrayList<Event>(sortedTypes));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listEvents::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update event")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Event> updateEvent(@RequestBody Event event)
	{
		log.debug("updateEvent::enter for " + event.getId());
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventSeries> seriesOpt = dataService.getEventSeriesRepo().findById(event.getSeriesId());
			if (!seriesOpt.isPresent())
			{
				log.error("updateEvent::eventSeries not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event Series " + event.getSeriesId() + " not found"));
			}
			Optional<com.torkirion.eroam.ims.datadomain.EventSupplier> supplierOpt = dataService.getEventSupplierRepo().findById(event.getSupplierId());
			if (!supplierOpt.isPresent())
			{
				log.error("updateEvent::event supplier not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event Supplier " + event.getSupplierId() + " not found"));
			}
			Optional<com.torkirion.eroam.ims.datadomain.EventVenue> venueOpt = dataService.getEventVenueRepo().findById(event.getVenueId());
			if (!venueOpt.isPresent())
			{
				log.error("updateEvent::eventVenue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event Venue " + event.getVenueId() + " not found"));
			}
			if (event.getAssociatedExternalMerchandiseId() != null && event.getAssociatedExternalMerchandiseId().length() > 0)
			{
				List<com.torkirion.eroam.ims.datadomain.Merchandise> merchandiseList = dataService.getMerchandiseRepo()
						.findByExternalMerchandiseId(event.getAssociatedExternalMerchandiseId());
				if (merchandiseList == null || merchandiseList.size() != 1)
				{
					log.error("updateEvent::associatedExternalMerchandiseId not found");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "merchandise " + event.getAssociatedExternalMerchandiseId() + " not found"));
				}
			}

			com.torkirion.eroam.ims.datadomain.Event data = mapperService.mapEvent(event, seriesOpt.get(), supplierOpt.get(), venueOpt.get());
			if (event.getId() != null)
			{
				Optional<com.torkirion.eroam.ims.datadomain.Event> dataDBOpt = dataService.getEventRepo().findById(event.getId());
				if (!dataDBOpt.isPresent())
				{
					log.error("updateEvent::event ID " + event.getId() + " not found");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event ID " + event.getId() + " not found"));
				}
				data.setAllotments(dataDBOpt.get().getAllotments());
				data.setClassifications(dataDBOpt.get().getClassifications());
				data.setSales(dataDBOpt.get().getSales());
			}
			data.setLastUpdated(LocalDateTime.now());
			com.torkirion.eroam.ims.datadomain.Event savedData = dataService.getEventRepo().save(data);

			Event newAPi = mapperService.mapEvent(savedData);
			eventsSearchService.clearSearchCache();
			return new ResponseData<>(newAPi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateEvent::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read event")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Event> readEvent(@PathVariable Integer id)
	{
		log.debug("readEvent::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Event> dataOpt = dataService.getEventRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readEvent::event not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + id + " not found"));
			}
			Event api = mapperService.mapEvent(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readEvent::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete event")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteEvent(@PathVariable Integer id)
	{
		log.debug("deleteEvent::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Event> dataOpt = dataService.getEventRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("deleteEvent::event not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + id + " not found"));
			}
			// check sales
			List<EventSale> eventSales = mapperService.mapEventSales(dataService.getEventSaleRepo().findByEvent(dataOpt.get()));
			if (eventSales != null && eventSales.size() > 0)
			{
				log.error("deleteEvent::event has sales");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event has sales"));
			}
			dataService.getEventAllotmentRepo().deleteByEvent(dataOpt.get());
			dataService.getEventClassificationRepo().deleteByEvent(dataOpt.get());
			dataService.getEventRepo().delete(dataOpt.get());
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteEvent::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list allotments")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{eventId}/allotment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventAllotment>> listAllotments(@PathVariable Integer eventId)
	{
		log.debug("listAllotments::enter for eventId " + eventId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Event> dataOpt = dataService.getEventRepo().findById(eventId);
			if (!dataOpt.isPresent())
			{
				log.error("listAllotments::event not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + eventId + " not found"));
			}

			List<EventAllotment> all = mapperService.mapEventAllotments(dataService.getEventAllotmentRepo().findByEvent(dataOpt.get()));
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listAllotments::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update allotment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{eventId}/allotment/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventAllotment> updateAllotment(@PathVariable Integer eventId, @RequestBody EventAllotment eventAllotment)
	{
		log.debug("updateAllotment::enter for " + eventAllotment.getId());
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Event> eventOpt = dataService.getEventRepo().findById(eventId);
			if (!eventOpt.isPresent())
			{
				log.error("updateAllotment::event not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + eventId + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.Event eventData = eventOpt.get();
			com.torkirion.eroam.ims.datadomain.EventAllotment allotmentData = null;
			if (eventAllotment.getId() != null)
			{
				Optional<com.torkirion.eroam.ims.datadomain.EventAllotment> allotmentOpt = dataService.getEventAllotmentRepo().findById(eventAllotment.getId());
				if (!allotmentOpt.isPresent())
				{
					log.error("updateAllotment::event allotment not found");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event allotment " + eventAllotment.getId() + " not found"));
				}
				allotmentData = allotmentOpt.get();
			}
			if (allotmentData == null)
			{
				com.torkirion.eroam.ims.datadomain.EventAllotment data = mapperService.mapEventAllotment(eventAllotment, eventData);
				data.setLastUpdated(LocalDateTime.now());
				allotmentData = dataService.getEventAllotmentRepo().save(data);
				eventData.getAllotments().add(allotmentData);
				dataService.getEventRepo().save(eventData);
			}
			else
			{
				com.torkirion.eroam.ims.datadomain.EventAllotment data = mapperService.mapEventAllotment(eventAllotment, eventData);
				data.setLastUpdated(LocalDateTime.now());
				allotmentData = dataService.getEventAllotmentRepo().save(data);
			}
			EventAllotment api = mapperService.mapEventAllotment(allotmentData);
			eventsSearchService.clearSearchCache();
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateAllotment::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read event allotment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{eventId}/allotment/{allotmentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventAllotment> readAllotment(@PathVariable Integer eventId, @PathVariable Integer allotmentId)
	{
		log.debug("readAllotment::enter for " + allotmentId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventAllotment> dataOpt = dataService.getEventAllotmentRepo().findById(allotmentId);
			if (!dataOpt.isPresent())
			{
				log.error("readAllotment::event allotment not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event allotment " + allotmentId + " not found"));
			}
			if (dataOpt.get().getEvent().getId().intValue() != eventId.intValue())
			{
				log.error("readAllotment::eventId " + dataOpt.get().getEvent().getId() + " not matched with " + eventId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + dataOpt.get().getEvent().getId() + " not matched with " + eventId));
			}
			EventAllotment api = mapperService.mapEventAllotment(dataOpt.get());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readAllotment::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete event allotment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{eventId}/allotment/{allotmentId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> deleteAllotment(@PathVariable Integer eventId, @PathVariable Integer allotmentId)
	{
		log.debug("deleteAllotment::enter for " + allotmentId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventAllotment> dataOpt = dataService.getEventAllotmentRepo().findById(allotmentId);
			if (!dataOpt.isPresent())
			{
				log.error("deleteAllotment::allotment not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Allotment " + allotmentId + " not found"));
			}
			if (dataOpt.get().getEvent().getId().intValue() != eventId.intValue())
			{
				log.error("readAllotment::eventId " + dataOpt.get().getEvent().getId() + " not matched with " + eventId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + dataOpt.get().getEvent().getId() + " not matched with " + eventId));
			}
			Optional<com.torkirion.eroam.ims.datadomain.Event> eventOpt = dataService.getEventRepo().findById(eventId);
			if (!eventOpt.isPresent())
			{
				log.error("deleteAllotment::event not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Event " + eventId + " not found"));
			}
			List<EventAllotment> allAllotments = mapperService.mapEventAllotments(dataService.getEventAllotmentRepo().findByEvent(eventOpt.get()));
			List<EventClassification> allClassifications = mapperService.mapEventClassifications(dataService.getEventClassificationRepo().findByEvent(eventOpt.get()), allAllotments);
			for (EventClassification eventClassification : allClassifications)
			{
				if (eventClassification.getAllotmentId().intValue() == allotmentId)
				{
					log.error("deleteAllotment::allotment in use");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Allotment " + allotmentId + " in use by " + eventClassification.getName()));
				}
			}
			eventOpt.get().getAllotments().remove(dataOpt.get());
			dataService.getEventRepo().save(eventOpt.get());
			try
			{
				dataService.getEventAllotmentRepo().deleteById(allotmentId);
			}
			catch ( Exception e)
			{
				log.debug("deleteAllotment::safely caught " + e.toString());
			}
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteAllotment::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list classifications")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{eventId}/classification", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventClassification>> listClassifications(@PathVariable Integer eventId)
	{
		log.debug("listClassifications::enter");
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Event> dataOpt = dataService.getEventRepo().findById(eventId);
			if (!dataOpt.isPresent())
			{
				log.error("listClassifications::event classification not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + eventId + " not found"));
			}

			List<EventAllotment> allAllotments = mapperService.mapEventAllotments(dataService.getEventAllotmentRepo().findByEvent(dataOpt.get()));
			List<EventClassification> all = mapperService.mapEventClassifications(dataService.getEventClassificationRepo().findByEvent(dataOpt.get()), allAllotments);
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listClassifications::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list sales")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/sales", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventSale>> listSales()
	{
		log.debug("listSales::enter");
		try
		{

			List<EventSale> allSales = mapperService.mapEventSales(dataService.getEventSaleRepo().findAll());
			return new ResponseData<>(allSales);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listClassifications::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update classification")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{eventId}/classification/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventClassification> updateClassification(@PathVariable Integer eventId, @RequestBody EventClassification eventClassification)
	{
		log.debug("updateClassification::enter for " + eventClassification.getId());
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.Event> eventOpt = dataService.getEventRepo().findById(eventId);
			if (!eventOpt.isPresent())
			{
				log.error("updateClassification::event venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + eventId + " not found"));
			}
			Optional<com.torkirion.eroam.ims.datadomain.EventAllotment> allotmentOpt = dataService.getEventAllotmentRepo().findById(eventClassification.getAllotmentId());
			if (!allotmentOpt.isPresent())
			{
				log.error("updateClassification::event allotment not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event allotment " + eventClassification.getAllotmentId() + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.Event eventData = eventOpt.get();
			com.torkirion.eroam.ims.datadomain.EventClassification classificationData = null;
			if (eventClassification.getId() != null)
			{
				Optional<com.torkirion.eroam.ims.datadomain.EventClassification> classificationOpt = dataService.getEventClassificationRepo().findById(eventClassification.getId());
				if (!classificationOpt.isPresent())
				{
					log.error("updateClassification::event classification not found");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event classification " + eventClassification.getId() + " not found"));
				}
				classificationData = classificationOpt.get();
			}
			if (classificationData == null)
			{
				com.torkirion.eroam.ims.datadomain.EventClassification data = mapperService.mapEventClassification(eventClassification, eventData);
				data.setLastUpdated(LocalDateTime.now());
				;
				classificationData = dataService.getEventClassificationRepo().save(data);
				eventData.getClassifications().add(classificationData);
				dataService.getEventRepo().save(eventData);
			}
			else
			{
				com.torkirion.eroam.ims.datadomain.EventClassification data = mapperService.mapEventClassification(eventClassification, eventData);
				data.setLastUpdated(LocalDateTime.now());
				;
				classificationData = dataService.getEventClassificationRepo().save(data);
			}
			// validate that all days are between start and end date distance
			LocalDate eventEndDate = eventData.getStartDate();
			if (eventData.getEndDate() != null)
				eventEndDate = eventData.getEndDate();
			long maxNumberOFDays = java.time.temporal.ChronoUnit.DAYS.between(eventData.getStartDate(), eventEndDate) + 1;
			if (eventClassification.getDays() != null)
			{
				for (Integer dayNum : eventClassification.getDays())
				{
					if (dayNum < 1 || dayNum > maxNumberOFDays)
					{
						log.error("updateClassification::day number " + dayNum + " must be within startDate " + eventData.getStartDate() + " and endDate " + eventEndDate);
						return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "day number " + dayNum + " must be within start date and end date"));
					}
				}
			}
			EventClassification api = mapperService.mapEventClassification(classificationData, mapperService.mapEventAllotment(allotmentOpt.get()));
			eventsSearchService.clearSearchCache();
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateClassification::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read event classification")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{eventId}/classification/{classificationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventClassification> readClassification(@PathVariable Integer eventId, @PathVariable Integer classificationId)
	{
		log.debug("readClassification::enter for " + classificationId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventClassification> dataOpt = dataService.getEventClassificationRepo().findById(classificationId);
			if (!dataOpt.isPresent())
			{
				log.error("readClassification::event classification not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event classification " + classificationId + " not found"));
			}
			if (dataOpt.get().getEvent().getId().intValue() != eventId.intValue())
			{
				log.error("readClassification::eventId " + dataOpt.get().getEvent().getId() + " not matched with " + eventId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + dataOpt.get().getEvent().getId() + " not matched with " + eventId));
			}
			Optional<com.torkirion.eroam.ims.datadomain.EventAllotment> allotmentOpt = dataService.getEventAllotmentRepo().findById(dataOpt.get().getAllotmentId());
			if (!allotmentOpt.isPresent())
			{
				log.error("readClassification::event allotment not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event allotment " + dataOpt.get().getAllotmentId() + " not found"));
			}
			EventAllotment allotmentApi = mapperService.mapEventAllotment(allotmentOpt.get());

			EventClassification api = mapperService.mapEventClassification(dataOpt.get(), allotmentApi);
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readClassification::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete event classification")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{eventId}/classification/{classificationId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boolean> deleteClassification(@PathVariable Integer eventId, @PathVariable Integer classificationId)
	{
		log.debug("deleteClassification::enter for " + classificationId);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.EventClassification> dataOpt = dataService.getEventClassificationRepo().findById(classificationId);
			if (!dataOpt.isPresent())
			{
				log.error("deleteClassification::event venue not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Classification " + classificationId + " not found"));
			}
			if (dataOpt.get().getEvent().getId().intValue() != eventId.intValue())
			{
				log.error("deleteClassification::eventId " + dataOpt.get().getEvent().getId() + " not matched with " + eventId);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "event " + dataOpt.get().getEvent().getId() + " not matched with " + eventId));
			}
			Optional<com.torkirion.eroam.ims.datadomain.Event> eventOpt = dataService.getEventRepo().findById(eventId);
			if (!eventOpt.isPresent())
			{
				log.error("deleteClassification::event not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Event " + eventId + " not found"));
			}
			eventOpt.get().getClassifications().remove(dataOpt.get());
			dataService.getEventRepo().save(eventOpt.get());
			try
			{
				dataService.getEventClassificationRepo().deleteById(classificationId);
			}
			catch ( Exception e)
			{
				log.debug("deleteClassification::safely caught " + e.toString());
			}
			
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteAllotment::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	
	@ApiOperation(value = "Import CSV")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/importCSV", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Integer> importCSV(@RequestBody String filename)
	{
		log.debug("importCSV::enter with " + filename);
		try
		{
			return new ResponseData<>(importService.importEvent(filename, "eroam", "SN"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("importCSV::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	
	/*
	@ApiOperation(value = "list suppliers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/supplier", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<SupplierSummary>> listSuppliers()
	{
		log.debug("listSuppliers::enter");
		try
		{
			List<SupplierSummary> allSummary = new ArrayList<>();
			List<com.torkirion.eroam.ims.datadomain.Supplier> allData = dataService.getSupplierRepo().findByForAccommodation(true);
			log.debug("listSuppliers::found " + allData.size() + " from basic repo");
			for (com.torkirion.eroam.ims.datadomain.Supplier s : allData)
			{
				SupplierSummary supplierSummary = mapperService.mapSupplierSummary(s);
				allSummary.add(supplierSummary);
			}
			return new ResponseData<>(allSummary);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listSuppliers::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	} */
}