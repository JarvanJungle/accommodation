package com.torkirion.eroam.microservice.activities.controllers;

import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRS;
import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import com.torkirion.eroam.microservice.activities.apidomain.*;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.microservice.activities.dto.AvailSearchByActivityIdRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.activities.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;
import com.torkirion.eroam.microservice.activities.services.ActivityChannelService;
import com.torkirion.eroam.microservice.activities.services.ActivitySearchService;
import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.SystemProperty;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@RestController
@RequestMapping("/activities/v1")
@Api(value = "Activities Service API")
@Slf4j
@AllArgsConstructor
public class ActivitiesController
{
	@Autowired
	private ActivitySearchService searchService;

	@Autowired
	private ActivityChannelService channelService;

	//@Autowired
	//private AccommodationRCService rcService;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;
	
	//@Autowired
	//private OleryService oleryService;

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
	
	@ApiOperation(value = "Initiate an RC Load for a channel")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/initiateRCLoad/{channel}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Boolean> initiateRCLoad(@PathVariable String channel, @RequestParam(value = "code", required = false) String code)
	{
		log.debug("initiateRCLoad::enter with " + channel);
		try
		{
			ActivityServiceIF activityServiceIF = channelService.getActivityServiceIF(channel);
			activityServiceIF.initiateRCLoad(code);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			log.warn("initiateRCLoad::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Availability Search Accommodation by LatLong box")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByGeoBox", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Collection<ActivityResult>> availSearchByGeoBox(@RequestBody RequestData<AvailActivitySearchByGeocoordBoxRQ> availSearchByGeocoordBoxRQ)
	{
		log.debug("availSearchByGeoBox::enter for " + availSearchByGeocoordBoxRQ);

		try
		{
			AvailSearchByGeocordBoxRQDTO dto = new AvailSearchByGeocordBoxRQDTO();
			dto.setClient(availSearchByGeocoordBoxRQ.getClient());
			dto.setCountryCodeOfOrigin(availSearchByGeocoordBoxRQ.getData().getCountryCodeOfOrigin());
			dto.setActivityDateFrom(availSearchByGeocoordBoxRQ.getData().getActivityDateFrom());
			dto.setActivityDateTo(availSearchByGeocoordBoxRQ.getData().getActivityDateTo());
			dto.setTravellers(availSearchByGeocoordBoxRQ.getData().getTravellers());
			dto.setChannel(availSearchByGeocoordBoxRQ.getData().getChannel());
			dto.setNorthwest(availSearchByGeocoordBoxRQ.getData().getNorthwest());
			dto.setSoutheast(availSearchByGeocoordBoxRQ.getData().getSoutheast());
			
			if ( dto.getClient() == null || dto.getActivityDateFrom() == null || dto.getActivityDateTo() == null || dto.getTravellers() == null || dto.getNorthwest() == null || dto.getSoutheast() == null )
			{
				log.warn("availSearchByGeoBox::bad input " + availSearchByGeocoordBoxRQ);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing input data"));
			}
			if ( dto.getNorthwest().getLatitude() == null || dto.getNorthwest().getLongitude() == null || dto.getSoutheast().getLatitude() == null || dto.getSoutheast().getLongitude() == null )
			{
				log.warn("availSearchByGeoBox::bad input " + availSearchByGeocoordBoxRQ);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing geocordinate input data"));
			}
			Period period = Period.between(dto.getActivityDateFrom(), dto.getActivityDateTo());
			if ( period.getDays() > 30 )
			{
				log.warn("availSearchByGeoBox::period between " + dto.getActivityDateFrom() + " and " + dto.getActivityDateTo());
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Maximum date range is 30 days"));
			}

			return new ResponseData<>(searchService.searchActivities(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearchByGeoBox::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Availability Search Accommodation by Activity Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByActivityID", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Collection<ActivityResult>> availSearchByActivityId(@RequestBody RequestData<AvailActivitySearchByActivityIdRQ> availSearchByActivityIdInput)
	{
		log.debug("availSearchByActivityId::enter for " + availSearchByActivityIdInput);

		try
		{
			AvailSearchByActivityIdRQDTO dto = new AvailSearchByActivityIdRQDTO();
			dto.setClient(availSearchByActivityIdInput.getClient());
			dto.setCountryCodeOfOrigin(availSearchByActivityIdInput.getData().getCountryCodeOfOrigin());
			dto.setActivityDateFrom(availSearchByActivityIdInput.getData().getActivityDateFrom());
			dto.setActivityDateTo(availSearchByActivityIdInput.getData().getActivityDateTo());
			dto.setTravellers(availSearchByActivityIdInput.getData().getTravellers());
			dto.setChannel(availSearchByActivityIdInput.getData().getChannel());
			dto.setActivityIds(availSearchByActivityIdInput.getData().getActivityIds());
			
			if ( dto.getClient() == null || dto.getActivityDateFrom() == null || dto.getActivityDateTo() == null || dto.getTravellers() == null )
			{
				log.warn("availSearchByActivityId::bad input " + availSearchByActivityIdInput);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing input data"));
			}
			Period period = Period.between(dto.getActivityDateFrom(), dto.getActivityDateTo());
			if ( period.getDays() > 30 )
			{
				log.warn("availSearchByActivityId::period between " + dto.getActivityDateFrom() + " and " + dto.getActivityDateTo());
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Maximum date range is 30 days"));
			}
			if(CollectionUtils.isEmpty(dto.getActivityIds())) {
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing input activityIds"));
			}

			return new ResponseData<>(searchService.searchActivities(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearchByActivityId::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Perform a rate check on a specific rateCode")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/rateCheck", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<ActivityResult> rateCheck(@RequestBody RequestData<ActivityRateCheckRQ> rateCheckRQ)
	{
		log.debug("rateCheck::enter for " + rateCheckRQ);

		try
		{
			RateCheckRQDTO dto = new RateCheckRQDTO();
			dto.setClient(rateCheckRQ.getClient());
			dto.setCountryCodeOfOrigin(rateCheckRQ.getData().getCountryCodeOfOrigin());
			dto.setActivityDate(rateCheckRQ.getData().getActivityDate());
			dto.setTravellers(rateCheckRQ.getData().getTravellers());
			dto.setChannel(rateCheckRQ.getData().getChannel());
			dto.setActivityId(rateCheckRQ.getData().getActivityId());
			dto.setDepartureId(rateCheckRQ.getData().getDepartureId());
			dto.setOptionId(rateCheckRQ.getData().getOptionId());

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
	public ResponseData<ActivityBookRS> book(@RequestBody RequestData<ActivityBookRQ> bookRQ)
	{
		log.debug("book::enter with " + bookRQ);
		try
		{
			ActivityBookRS bookRS = searchService.book(bookRQ.getClient(), bookRQ.getData());
			if ( bookRS.getErrors().size() > 0 )
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, bookRS.getErrors());
			else
				return new ResponseData<>(bookRS);
		}
		catch (Exception e)
		{
			log.warn("book::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.getMessage()));
		}
	}

	@ApiOperation(value = "Cancel")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/cancel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<ActivityCancelRS> cancel(@RequestBody RequestData<ActivityCancelRQ> cancelRQ)
	{
		log.debug("cancel::enter with cancelRQ", cancelRQ);
		try
		{
			ActivityCancelRS cancelRS = searchService.cancel(cancelRQ.getClient(), cancelRQ.getData());
			if ( cancelRS.getErrors().size() > 0 )
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cancelRS.getErrors());
			else
				return new ResponseData<>(cancelRS);
		}
		catch (Exception e)
		{
			log.warn("book::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.getMessage()));
		}
	}

	/*

	@ApiOperation(value = "Retrieve")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/retrieve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<RetrieveRS> retrieve(@RequestBody RequestData<RetrieveRQ> retrieveRQ)
	{
		log.debug("retrieve::enter with " + retrieveRQ);
		try
		{
			RetrieveRS retrieveRS = searchService.retrieve(retrieveRQ.getClient(), retrieveRQ.getData());
			if ( retrieveRS.getErrors().size() > 0 )
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, retrieveRS.getErrors());
			else
				return new ResponseData<>(retrieveRS);
		}
		catch (Exception e)
		{
			log.warn("retrieve::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	*/
	/*

	@ApiOperation(value = "Initiate an RC Load for a channel")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/initiateRCLoad", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Boolean> initiateRCLoad(@RequestBody RequestData<String> channel)
	{
		log.debug("initiateRCLoad::enter with " + channel);
		try
		{
			RCController rcController = channelService.getRCController(channel.getData());
			rcController.process();
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			log.warn("cancel::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	*/
	
}
