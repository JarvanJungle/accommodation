package com.torkirion.eroam.microservice.hirecars.controllers;

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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.SystemProperty;
import com.torkirion.eroam.microservice.hirecars.apidomain.*;
import com.torkirion.eroam.microservice.hirecars.dto.DetailRQDTO;
import com.torkirion.eroam.microservice.hirecars.dto.HireCarSearchRQDTO;
import com.torkirion.eroam.microservice.hirecars.services.HireCarSearchService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@RestController
@RequestMapping("/hirecars/v1")
@Api(value = "Hire cars Service API")
@Slf4j
@AllArgsConstructor
public class HireCarController
{
	@Autowired
	private HireCarSearchService searchService;

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

	@ApiOperation(value = "Search by LatLong box")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByGeoBox", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Collection<HireCarResult>> availSearchByGeoBox(@RequestBody RequestData<HireCarSearchRQ> availSearchByGeocoordBoxRQ)
	{
		log.debug("availSearchByGeoBox::enter for " + availSearchByGeocoordBoxRQ);

		try
		{
			HireCarSearchRQDTO dto = new HireCarSearchRQDTO();
			dto.setClient(availSearchByGeocoordBoxRQ.getClient());
			BeanUtils.copyProperties(availSearchByGeocoordBoxRQ.getData(), dto);
			
			if ( dto.getClient() == null || dto.getPickupLocation() == null )
			{
				log.warn("availSearchByGeoBox::bad input " + availSearchByGeocoordBoxRQ);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing input data"));
			}
			Period period = Period.between(dto.getPickupDateTime().toLocalDate(), dto.getDropoffDateTime().toLocalDate());
			if ( period.getDays() > 30 )
			{
				log.warn("availSearchByGeoBox::period between " + dto.getPickupDateTime() + " and " + dto.getDropoffDateTime());
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Maximum date range is 30 days"));
			}

			return new ResponseData<>(searchService.search(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearchByGeoBox::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Get more information on a specific vehicleID")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/detail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<HireCarDetailResult> getDetail(@RequestBody RequestData<HireCarDetailRQ> detailRQ)
	{
		log.debug("getDetail::enter for " + detailRQ);

		try
		{
			DetailRQDTO dto = new DetailRQDTO();
			dto.setClient(detailRQ.getClient());
			dto.setChannel(detailRQ.getData().getChannel());
			dto.setVehicleId(detailRQ.getData().getVehicleId());

			return new ResponseData<>(searchService.getDetail(dto));
		}
		catch (Exception e)
		{
			log.warn("getDetail::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Book")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/book", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<HireCarBookRS> book(@RequestBody RequestData<HireCarBookRQ> bookRQ)
	{
		log.debug("book::enter with " + bookRQ);
		try
		{
			HireCarBookRS bookRS = searchService.book(bookRQ.getClient(), bookRQ.getData());
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
	public ResponseData<HireCarCancelRS> cancel(@RequestBody RequestData<HireCarCancelRQ> cancelRQ)
	{
		log.debug("cancel::enter with " + cancelRQ);
		try
		{
			HireCarCancelRS cancelRS = searchService.cancel(cancelRQ.getClient(), cancelRQ.getData());
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
