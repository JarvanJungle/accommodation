package com.torkirion.eroam.microservice.events.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.microservice.events.apidomain.EventResult;
import com.torkirion.eroam.microservice.events.apidomain.EventSeries;
import com.torkirion.eroam.microservice.events.apidomain.EventRateCheckRQ;
import com.torkirion.eroam.microservice.events.apidomain.AvailEventSearchByGeocoordBoxRQ;
import com.torkirion.eroam.microservice.events.apidomain.EventsBookRQ;
import com.torkirion.eroam.microservice.events.apidomain.EventsBookRS;
import com.torkirion.eroam.microservice.events.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.events.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.events.services.EventsChannelService;
import com.torkirion.eroam.microservice.events.services.EventsSearchService;
import com.torkirion.eroam.ims.apidomain.EventClassification;
import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.SystemProperty;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@RestController
@RequestMapping("/events/v1")
@Api(value = "Events Service API")
@Slf4j
@AllArgsConstructor
public class EventsController
{
	@Autowired
	private EventsSearchService searchService;

	@Autowired
	private EventsChannelService channelService;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@ApiOperation(value = "Ping Test Call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Boolean ping()
	{
		log.debug("ping::enter");
		try
		{
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("ping::error " + e.toString(), e);
			return false;
		}
	}

	@ApiOperation(value = "Event Search Accommodation by LatLong box")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByGeoBox", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<Collection<EventResult>> availSearchByGeoBox(@RequestBody RequestData<AvailEventSearchByGeocoordBoxRQ> availSearchByGeocoordBoxRQ)
	{
		log.debug("availSearchByGeoBox::enter for " + availSearchByGeocoordBoxRQ);

		try
		{
			AvailSearchByGeocordBoxRQDTO dto = new AvailSearchByGeocordBoxRQDTO();
			dto.setClient(availSearchByGeocoordBoxRQ.getClient());
			dto.setCountryCodeOfOrigin(availSearchByGeocoordBoxRQ.getData().getCountryCodeOfOrigin());
			dto.setEventDateFrom(availSearchByGeocoordBoxRQ.getData().getEventDateFrom());
			dto.setEventDateTo(availSearchByGeocoordBoxRQ.getData().getEventDateTo());
			dto.setTravellers(availSearchByGeocoordBoxRQ.getData().getTravellers());
			dto.setChannel(availSearchByGeocoordBoxRQ.getData().getChannel());
			BigDecimal zeroPointOne = BigDecimal.valueOf(0.1f);
			dto.setNorthwest(new LatitudeLongitude(availSearchByGeocoordBoxRQ.getData().getNorthwest().getLatitude().add(zeroPointOne), availSearchByGeocoordBoxRQ.getData().getNorthwest().getLongitude().subtract(zeroPointOne)));
			dto.setSoutheast(new LatitudeLongitude(availSearchByGeocoordBoxRQ.getData().getSoutheast().getLatitude().subtract(zeroPointOne), availSearchByGeocoordBoxRQ.getData().getSoutheast().getLongitude().add(zeroPointOne)));

			if (dto.getClient() == null || dto.getEventDateFrom() == null || dto.getEventDateTo() == null || dto.getTravellers() == null || dto.getNorthwest() == null || dto.getSoutheast() == null)
			{
				log.warn("availSearchByGeoBox::bad input " + availSearchByGeocoordBoxRQ);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing input data"));
			}
			if (dto.getNorthwest().getLatitude() == null || dto.getNorthwest().getLongitude() == null || dto.getSoutheast().getLatitude() == null || dto.getSoutheast().getLongitude() == null)
			{
				log.warn("availSearchByGeoBox::bad input " + availSearchByGeocoordBoxRQ);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing geocordinate input data"));
			}

			return new ResponseData<>(searchService.searchEvents(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearchByGeoBox::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "read an event definition")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/event/{client}/{eventId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<EventResult> readEvent(@PathVariable String client, @PathVariable String eventId)
	{
		log.debug("readEvent::enter");
		try
		{
			return new ResponseData<>(searchService.readEvent(client, eventId));
		}
		catch (Exception e)
		{
			log.warn("readEvent::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	
	@ApiOperation(value = "list series")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/series/{client}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<EventSeries>> listSeries(@PathVariable String client)
	{
		log.debug("listSeries::enter");
		try
		{
			return new ResponseData<>(searchService.listSeries(client));
		}
		catch (Exception e)
		{
			log.warn("listSeries::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	
	@ApiOperation(value = "Perform a rate check on a specific rateCode")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/rateCheck", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<EventResult> rateCheck(@RequestBody RequestData<EventRateCheckRQ> rateCheckRQ)
	{
		log.debug("rateCheck::enter for " + rateCheckRQ);

		try
		{
			if (rateCheckRQ.getData().getChannel() == null)
			{
				log.warn("rateCheck::channel is empty");
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing channel"));
			}
			RateCheckRQDTO dto = new RateCheckRQDTO();
			dto.setClient(rateCheckRQ.getClient());
			dto.setCountryCodeOfOrigin(rateCheckRQ.getData().getCountryCodeOfOrigin());
			dto.setEventDateFrom(rateCheckRQ.getData().getEventDateFrom());
			dto.setEventDateTo(rateCheckRQ.getData().getEventDateTo());
			dto.setTravellers(rateCheckRQ.getData().getTravellers());
			dto.setChannel(rateCheckRQ.getData().getChannel());
			dto.setEventId(rateCheckRQ.getData().getEventId());
			dto.setClassificationId(rateCheckRQ.getData().getClassificationId());
			dto.setNumberOfTickets(rateCheckRQ.getData().getNumberOfTickets());

			return new ResponseData<>(searchService.rateCheck(dto));
		}
		catch (Exception e)
		{
			log.warn("rateCheck::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Book")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/book", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<EventsBookRS> book(@RequestBody RequestData<EventsBookRQ> bookRQ)
	{
		log.debug("book::enter with " + bookRQ);
		try
		{
			EventsBookRS bookRS = searchService.book(bookRQ.getClient(), bookRQ.getData());
			if (bookRS.getErrors().size() > 0)
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, bookRS.getErrors());
			else
				return new ResponseData<>(bookRS);
		}
		catch (Exception e)
		{
			log.warn("book::caught exception " + e.toString() + " '" + e.getMessage() + "'", e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.getMessage()));
		}
	}

	/*
	 * 
	 * @ApiOperation(value = "Cancel")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	 * 
	 * @RequestMapping(value = "/cancel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @Transactional public ResponseData<CancelRS> cancel(@RequestBody RequestData<CancelRQ> cancelRQ) {
	 * log.debug("cancel::enter with " + cancelRQ); try { CancelRS cancelRS = searchService.cancel(cancelRQ.getClient(),
	 * cancelRQ.getData()); if ( cancelRS.getErrors().size() > 0 ) return new
	 * ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cancelRS.getErrors()); else return new
	 * ResponseData<>(cancelRS); } catch (Exception e) { log.warn("cancel::caught exception " + e.toString(), e); return new
	 * ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString())); } }
	 */
	// TODO
	/*
	 * 
	 * @ApiOperation(value = "Retrieve")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	 * 
	 * @RequestMapping(value = "/retrieve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @Transactional public ResponseData<RetrieveRS> retrieve(@RequestBody RequestData<RetrieveRQ> retrieveRQ) {
	 * log.debug("retrieve::enter with " + retrieveRQ); try { RetrieveRS retrieveRS =
	 * searchService.retrieve(retrieveRQ.getClient(), retrieveRQ.getData()); if ( retrieveRS.getErrors().size() > 0 ) return new
	 * ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, retrieveRS.getErrors()); else return new
	 * ResponseData<>(retrieveRS); } catch (Exception e) { log.warn("retrieve::caught exception " + e.toString(), e); return new
	 * ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString())); } }
	 */
}
