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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.ims.Functions;
import com.torkirion.eroam.ims.apidomain.APIError;
import com.torkirion.eroam.ims.apidomain.Activity;
import com.torkirion.eroam.ims.apidomain.Event;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.apidomain.SupplierSummary;
import com.torkirion.eroam.ims.apidomain.Transportation;
import com.torkirion.eroam.ims.apidomain.TransportationSummary;
import com.torkirion.eroam.ims.apidomain.TransportationUpdate;
import com.torkirion.eroam.ims.datadomain.DaysOfTheWeek;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IataAirport;
import com.torkirion.eroam.ims.datadomain.TransportationBasic;
import com.torkirion.eroam.ims.datadomain.TransportationBasicClass;
import com.torkirion.eroam.ims.datadomain.TransportationBasicSegment;
import com.torkirion.eroam.ims.repository.IMSTransportationBasicRepo;
import com.torkirion.eroam.ims.services.*;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.datadomain.Airline;
import com.torkirion.eroam.microservice.events.services.EventsSearchService;

@RestController
@RequestMapping("/transportims/v1")
@Api(value = "Transportation IMS API")
@Slf4j
@AllArgsConstructor
public class TransportationIMSController
{
	@Autowired
	private DataService dataService;

	@Autowired
	private MapperService mapperService;

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

	@ApiOperation(value = "list all transportation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transportation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<TransportationSummary>> listTransportation(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listTransportation::enter");
		try
		{
			List<TransportationSummary> allSummary = new ArrayList<>();
			List<TransportationBasic> allData = dataService.getTransportationBasicRepo().findAll();
			log.debug("listTransportation::found " + allData.size() + " from basic repo");
			for (TransportationBasic t : allData)
			{
				TransportationSummary transportationSummary = new TransportationSummary();
				transportationSummary.setId(t.getId());
				transportationSummary.setSearchIATAFrom(t.getSearchIataFrom());
				transportationSummary.setSearchIATATo(t.getSearchIataTo());
				transportationSummary.setFlight(t.getFlight());
				transportationSummary.setScheduleFrom(t.getScheduleFrom());
				transportationSummary.setScheduleTo(t.getScheduleTo());
				transportationSummary.setLastUpdated(t.getLastUpdated());
				allSummary.add(transportationSummary);
			}
			return new ResponseData<>(allSummary);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listTransportation::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read transportation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transportation/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Transportation> readTransportation(@RequestHeader("X-imsclient") String imsclient, @PathVariable Long id)
	{
		log.debug("readTransportation::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.TransportationBasic> dataOpt = dataService.getTransportationBasicRepo().findById(id);
			if (!dataOpt.isPresent())
			{
				log.error("readTransportation::transport not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "transportation item " + id + " not found"));
			}
			int segmentCount = 0;
			for (TransportationBasicSegment dataSegment : dataOpt.get().getSegments())
			{
				log.debug("readTransportation::checking segment " + dataSegment);
				segmentCount++;
			}
			log.debug("readTransportation::checked " + segmentCount + " segments ");

			Transportation api = (Transportation) mapperService.mapTransportation(dataOpt.get());
			log.debug("readTransportation::returning " + api);
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("readTransportation::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update transportation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transportation/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Transportation> updateTransportation(@RequestHeader("X-imsclient") String imsclient, @RequestBody TransportationUpdate api)
	{
		log.debug("updateTransportation::enter for " + api.getId());
		try
		{
			if (StringUtils.isEmpty(api.getCurrency()) || StringUtils.isEmpty(api.getFlight()) || StringUtils.isEmpty(api.getSearchIataFrom()) || StringUtils.isEmpty(api.getSearchIataTo())
					|| api.getScheduleFrom() == null || api.getScheduleTo() == null || api.getDaysOfTheWeek() == null)
			{
				log.error("updateTransportation::empty fields for input " + api);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "All fields must be completed"));
			}
			if (!api.getDaysOfTheWeek().getSunday() && !api.getDaysOfTheWeek().getMonday() && !api.getDaysOfTheWeek().getTuesday() && !api.getDaysOfTheWeek().getWednesday()
					&& !api.getDaysOfTheWeek().getThursday() && !api.getDaysOfTheWeek().getFriday() && !api.getDaysOfTheWeek().getSaturday())
			{
				log.error("updateTransportation::no days for input " + api);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "At least one day must be scheduled"));
			}
			if (!api.getScheduleFrom().isBefore(api.getScheduleTo()))
			{
				log.error("updateTransportation::from must be before to " + api);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Schedule from must be before schedule to"));
			}
			List<TransportationBasic> listByFlight = dataService.getTransportationBasicRepo().findAllByFlight(api.getFlight());
			for (TransportationBasic transportationBasic : listByFlight)
			{
				if (api.getId() != null && api.getId().longValue() == transportationBasic.getId().longValue())
				{
					continue;
				}
				if (!api.getScheduleTo().isBefore(transportationBasic.getScheduleFrom()) && !api.getScheduleFrom().isBefore(transportationBasic.getScheduleTo()))
				{
					if ((api.getDaysOfTheWeek().getSunday() && transportationBasic.getDaysOfTheWeek().getSunday())
							|| (api.getDaysOfTheWeek().getMonday() && transportationBasic.getDaysOfTheWeek().getMonday())
							|| (api.getDaysOfTheWeek().getTuesday() && transportationBasic.getDaysOfTheWeek().getTuesday())
							|| (api.getDaysOfTheWeek().getWednesday() && transportationBasic.getDaysOfTheWeek().getWednesday())
							|| (api.getDaysOfTheWeek().getThursday() && transportationBasic.getDaysOfTheWeek().getThursday())
							|| (api.getDaysOfTheWeek().getFriday() && transportationBasic.getDaysOfTheWeek().getFriday())
							|| (api.getDaysOfTheWeek().getSaturday() && transportationBasic.getDaysOfTheWeek().getSaturday()))
					{
						log.error("updateTransportation::flight number must be unique, found conflict on  " + api.getId());
						return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Flight number must be unique between schedule from and schedule to"));
					}
				}
			}

			TransportationBasic data = null;
			if (api.getId() == null || api.getId().intValue() == 0)
			{
				data = new TransportationBasic();
			}
			else
			{
				Optional<TransportationBasic> dataOpt = dataService.getTransportationBasicRepo().findById(api.getId());
				if (!dataOpt.isPresent())
				{
					log.error("updateTransportation::updating unknown transportation " + api.getId());
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "updating unknown transportation " + api.getId()));
				}
				data = dataOpt.get();
			}
			data.setCurrency(api.getCurrency());
			data.setRrpCurrency(api.getRrpCurrency());
			data.setFlight(api.getFlight());
			data.setScheduleFrom(api.getScheduleFrom());
			data.setScheduleTo(api.getScheduleTo());
			data.setSearchIataFrom(api.getSearchIataFrom());
			data.setSearchIataTo(api.getSearchIataTo());
			data.setRequiresPassport(api.getRequiresPassport());
			data.setOnRequest(api.getOnRequest());
			data.setSupplier(api.getSupplier());
			if ( api.getBookingConditions() == null || api.getBookingConditions().length() < 1000)
				data.setBookingConditions(api.getBookingConditions());
			else
				data.setBookingConditions(api.getBookingConditions().substring(0, 1000));
			data.setDaysOfTheWeek(new DaysOfTheWeek());
			BeanUtils.copyProperties(api.getDaysOfTheWeek(), data.getDaysOfTheWeek());
			completeTransportRecord(data);
			data.setLastUpdated(LocalDateTime.now());
			data = dataService.getTransportationBasicRepo().save(data);

			Transportation apiResponse = mapperService.mapTransportation(data);
			return new ResponseData<>(apiResponse);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateTransportation::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete transportation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transportation/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteTransportation(@RequestHeader("X-imsclient") String imsclient, @PathVariable Long id)
	{
		log.debug("deleteTransportation::enter for " + id);
		try
		{
			dataService.getTransportationBasicRepo().deleteById(id);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteTransportation::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update segment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transportation/{id}/segment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Transportation.Segment> updateSegment(@RequestHeader("X-imsclient") String imsclient, @PathVariable Long id, @RequestBody Transportation.Segment api)
	{
		log.debug("updateSegment::enter for " + api.getId());
		try
		{
			if (StringUtils.isEmpty(api.getArrivalAirportLocationCode()) || StringUtils.isEmpty(api.getDepartureAirportLocationCode()) || StringUtils.isEmpty(api.getMarketingAirlineCode())
					|| StringUtils.isEmpty(api.getMarketingAirlineFlightNumber()) || StringUtils.isEmpty(api.getOperatingAirlineCode()) || StringUtils.isEmpty(api.getOperatingAirlineFlightNumber())
					|| api.getArrivalTime() == null || api.getDepartureTime() == null || api.getFlightDurationMinutes() == null || api.getFlightDurationMinutes().intValue() <= 0
					|| api.getSegmentNumber() == null)
			{
				log.error("updateSegment::empty fields for input " + api);
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "All fields must be completed"));
			}
			if (getAirport(api.getDepartureAirportLocationCode()) == null)
			{
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Unknown airport code " + api.getDepartureAirportLocationCode()));
			}
			if (getAirport(api.getArrivalAirportLocationCode()) == null)
			{
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Unknown airport code " + api.getArrivalAirportLocationCode()));
			}
			if (getAirline(api.getMarketingAirlineCode()) == null)
			{
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Unknown airline code " + api.getMarketingAirlineCode()));
			}
			if (getAirline(api.getOperatingAirlineCode()) == null)
			{
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Unknown airline code " + api.getOperatingAirlineCode()));
			}

			Optional<com.torkirion.eroam.ims.datadomain.TransportationBasic> dataTOpt = dataService.getTransportationBasicRepo().findById(id);
			if (!dataTOpt.isPresent())
			{
				log.error("updateSegment::transport not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "transportation item " + id + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.TransportationBasic dataTransportation = dataTOpt.get();

			TransportationBasicSegment data = null;
			if (api.getId() == null || api.getId().intValue() == 0)
			{
				data = new TransportationBasicSegment();
			}
			else
			{
				Optional<TransportationBasicSegment> dataOpt = dataService.getTransportationBasicSegmentRepo().findById(api.getId());
				if (!dataOpt.isPresent())
				{
					log.error("updateSegment::updating unknown transportation " + api.getId());
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "updating unknown transportation " + api.getId()));
				}
				data = dataOpt.get();
				if (data.getTransportation().getId().longValue() != id.longValue())
				{
					log.error("updateSegment::mismatch in transportation id");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "mismatch in transportation id " + data.getTransportation().getId() + " to " + id));
				}
			}
			BeanUtils.copyProperties(api, data);
			data.setTransportation(dataTransportation);
			data.setLastUpdated(LocalDateTime.now());
			data = dataService.getTransportationBasicSegmentRepo().save(data);

			if (api.getId() == null)
			{
				dataTransportation.getSegments().add(data);
			}
			completeTransportRecord(dataTransportation);
			dataService.getTransportationBasicRepo().save(dataTransportation);
			api.setId(data.getId());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateSegment::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete segment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transportation/{id}/segment/{segmentId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteSegment(@RequestHeader("X-imsclient") String imsclient, @PathVariable Long id, @PathVariable Long segmentId)
	{
		log.debug("deleteSegment::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.TransportationBasic> dataTOpt = dataService.getTransportationBasicRepo().findById(id);
			if (!dataTOpt.isPresent())
			{
				log.error("deleteSegment::transport not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "transportation item " + id + " not found"));
			}
			dataService.getTransportationBasicSegmentRepo().deleteById(segmentId);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteSegment::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "create or update class")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transportation/{id}/class", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Transportation.TransportationClass> updateTransportationClass(@RequestHeader("X-imsclient") String imsclient, @PathVariable Long id, @RequestBody Transportation.TransportationClass api)
	{
		log.debug("updateTransportationClass::enter for " + api.getId());
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.TransportationBasic> dataTOpt = dataService.getTransportationBasicRepo().findById(id);
			if (!dataTOpt.isPresent())
			{
				log.error("updateTransportationClass::transport not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "transportation item " + id + " not found"));
			}
			com.torkirion.eroam.ims.datadomain.TransportationBasic dataTransportation = dataTOpt.get();

			if (api.getClassCode() == null || api.getClassCode().length() > 1)
			{
				log.debug("updateTransportationClass::class code should be 1 character");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Class Code should be 1 character, received '" + api.getClassCode() + "'"));
			}
			api.setClassCode(api.getClassCode().toUpperCase());

			TransportationBasicClass data = null;
			if (api.getId() == null || api.getId().intValue() == 0)
			{
				data = new TransportationBasicClass();
			}
			else
			{
				Optional<TransportationBasicClass> dataOpt = dataService.getTransportationBasicClassRepo().findById(api.getId());
				if (!dataOpt.isPresent())
				{
					log.error("updateTransportationClass::updating unknown transportation " + api.getId());
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "updating unknown transportation " + api.getId()));
				}
				data = dataOpt.get();
				if (data.getTransportation().getId().longValue() != id.longValue())
				{
					log.error("updateTransportationClass::mismatch in transportation id");
					return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "mismatch in transportation id"));
				}
			}
			BeanUtils.copyProperties(api, data);
			data.setTransportation(dataTransportation);
			data.setLastUpdated(LocalDateTime.now());
			data = dataService.getTransportationBasicClassRepo().save(data);
			api.setId(data.getId());
			return new ResponseData<>(api);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("updateTransportationClass::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete class")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transportation/{id}/class/{classId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteTransportClass(@RequestHeader("X-imsclient") String imsclient, @PathVariable Long id, @PathVariable Long classId)
	{
		log.debug("deleteTransportClass::enter for " + id);
		try
		{
			Optional<com.torkirion.eroam.ims.datadomain.TransportationBasic> dataTOpt = dataService.getTransportationBasicRepo().findById(id);
			if (!dataTOpt.isPresent())
			{
				log.error("deleteTransportClass::transport not found");
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "transportation item " + id + " not found"));
			}
			dataService.getTransportationBasicClassRepo().deleteById(classId);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteTransportClass::error " + e.toString(), e);
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
			List<com.torkirion.eroam.microservice.ims.datadomain.Supplier> allData = dataService.getSupplierRepo().findByForTransportation(true);
			log.debug("listSuppliers::found " + allData.size() + " from basic repo");
			for (com.torkirion.eroam.microservice.ims.datadomain.Supplier s : allData)
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
	@ApiOperation(value = "list suppliers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/suppliers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Collection<String>> listSupplier(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listSuppliers::enter");
		try
		{
			SortedSet<String> suppliers = new TreeSet<>();
			for (TransportationBasic data : dataService.getTransportationBasicRepo().findAll())
			{
				if ( data.getSupplier() != null && data.getSupplier().length() > 0)
					suppliers.add(data.getSupplier());
			}
			return new ResponseData<>(suppliers);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listSuppliers::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	
	@ApiOperation(value = "Import CSV")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/importCSV", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Integer> importCSV(@RequestHeader("X-imsclient") String imsclient, @RequestBody String filename)
	{
		log.debug("importCSV::enter with " + filename);
		try
		{
			return new ResponseData<>(importService.importTransportation(filename, "eroam", "SN"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("importCSV::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	
	protected void completeTransportRecord(com.torkirion.eroam.ims.datadomain.TransportationBasic transportData)
	{
		log.debug("updateTransportRecord::enter");
		TransportationBasicSegment firstSegment = null;
		TransportationBasicSegment lastSegment = null;
		if (transportData.getSegments() != null)
		{
			for (TransportationBasicSegment segment : transportData.getSegments())
			{
				if (firstSegment == null)
					firstSegment = segment;
				if (lastSegment == null)
					lastSegment = segment;
				if (segment.getSegmentNumber().intValue() < firstSegment.getSegmentNumber().intValue())
					firstSegment = segment;
				if (segment.getSegmentNumber().intValue() > lastSegment.getSegmentNumber().intValue())
					lastSegment = segment;
			}
		}
		log.debug("updateTransportRecord::firstSegment=" + firstSegment);
		log.debug("updateTransportRecord::lastSegment=" + lastSegment);
		if (firstSegment == null || lastSegment == null)
		{
			transportData.setFromIata("XXX");
			transportData.setToIata("XXX");
		}
		else
		{
			transportData.setFromIata(firstSegment.getDepartureAirportLocationCode());
			transportData.setToIata(lastSegment.getArrivalAirportLocationCode());
		}
	}

	private static final HashMap<String, IataAirport> airportCache = new HashMap<>();

	private IataAirport getAirport(String iataCode)
	{
		if (airportCache.get(iataCode) != null)
			return airportCache.get(iataCode);
		List<IataAirport> recs = dataService.getIataAirportRepo().findByIataCode(iataCode);
		if (recs.size() > 0)
		{
			airportCache.put(iataCode, recs.get(0));
			return recs.get(0);
		}
		else
		{
			log.warn("getAirport::unknown airport " + iataCode);
			return null;
		}
	}

	private static final HashMap<String, Airline> airlineCache = new HashMap<>();

	private Airline getAirline(String iataCode)
	{
		if (airlineCache.get(iataCode) != null)
			return airlineCache.get(iataCode);
		Optional<Airline> airlineOpt = dataService.getAirlineRepo().findById(iataCode);
		if (airlineOpt.isPresent())
		{
			airlineCache.put(iataCode, airlineOpt.get());
			return airlineOpt.get();
		}
		else
		{
			log.warn("getAirline::unknown airline " + iataCode);
			return null;
		}
	}
}
