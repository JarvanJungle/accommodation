package com.torkirion.eroam.microservice.transport.controllers;

import com.torkirion.eroam.microservice.transport.apidomain.*;
import com.torkirion.eroam.microservice.transport.dto.TransportChosenRQDTO;
import com.torkirion.eroam.microservice.transport.repository.SaveATrainVendorStationRepository;
import com.torkirion.eroam.microservice.transport.services.TransportBackgroundService;
import com.torkirion.eroam.microservice.util.JsonUtil;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.apidomain.AvailTransportSearchRQ.Route;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO;
import com.torkirion.eroam.microservice.transport.services.TransportSearchService;

@RestController
@RequestMapping("/transport/v1")
@Api(value = "Transportation Service API")
@Slf4j
@AllArgsConstructor
public class TransportController
{
	@Autowired
	private TransportSearchService searchService;

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

	@ApiOperation(value = "search")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<Collection<AvailTransportSearchRS>> searchTransport(@RequestBody RequestData<AvailTransportSearchRQ> availTransportSearchRQ)
	{
		log.debug("searchTransport::enter for " + availTransportSearchRQ);
		try
		{
			AvailTransportSearchRQDTO dto = new AvailTransportSearchRQDTO();
			dto.setTravellers(new TravellerMix());
			dto.getTravellers().setAdultCount(availTransportSearchRQ.getData().getTotalPassenger().getTotalAdult());
			dto.setTransportCallType(availTransportSearchRQ.getData().getTransport_call_type());
			dto.setCurrency(availTransportSearchRQ.getData().getCurrency());
			if (availTransportSearchRQ.getData().getTotalPassenger().getChild() != null)
			{
				for (List<Integer> l1 : availTransportSearchRQ.getData().getTotalPassenger().getChild())
				{
					for (Integer l2 : l1)
					{
						dto.getTravellers().getChildAges().add(l2);
					}
				}
			}
			for (Route route : availTransportSearchRQ.getData().getRoute())
			{
				AvailTransportSearchRQDTO.Route r = new AvailTransportSearchRQDTO.Route();
				BeanUtils.copyProperties(route, r);
				if(route.getTransportType() != null && !StringUtils.isBlank(route.getTransportType())) {
					r.setTransportType(TransportType.valueOf(route.getTransportType()));
				}
				if ( route.getDepartureLatitudeLongitude() != null )
				{
					r.setDepartureNorthwest(route.getDepartureLatitudeLongitude().getNorthwest());
					r.setDepartureSoutheast(route.getDepartureLatitudeLongitude().getSoutheast());
				}
				if ( route.getArrivalLatitudeLongitude() != null )
				{
					r.setArrivalNorthwest(route.getArrivalLatitudeLongitude().getNorthwest());
					r.setArrivalSoutheast(route.getArrivalLatitudeLongitude().getSoutheast());
				}
				dto.getRoute().add(r);
			}
			return new ResponseData<>(searchService.searchBasic(dto));
		}
		catch (Exception e)
		{
			log.warn("listBrands::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "choose")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/choose", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<Collection<AvailTransportSearchRS>> choose(@RequestBody RequestData<TransportChooseRQ> chooseRQ) {
		log.debug("chooseTransport::enter for: \n{} ", JsonUtil.convertToPrettyJson(chooseRQ));
		TransportChosenRQDTO transportChooseRQDTO = new TransportChosenRQDTO();

		try {
			return new ResponseData<>(searchService.chooseBasic(chooseRQ.getClient(), chooseRQ.getData()));
		} catch (Exception e) {
			log.warn("choose::caught exception {}", e.getMessage());
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.getMessage()));
		}
	}

	@ApiOperation(value = "Price Check")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/rateCheck", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<TransportRateCheckRS> rateCheck(@RequestBody RequestData<TransportRateCheckRQ> transportRateCheckRQ)
	{
		log.debug("rateCheck::enter with " + transportRateCheckRQ);
		try
		{
			TransportRateCheckRS rateCheckRS = searchService.rateCheck(transportRateCheckRQ.getClient(), transportRateCheckRQ.getData());
			if (rateCheckRS.getErrors().size() > 0)
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, rateCheckRS.getErrors());
			else
				return new ResponseData<>(rateCheckRS);
		}
		catch (Exception e)
		{
			log.warn("rateCheck::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.getMessage()));
		}
	}

	@ApiOperation(value = "Book")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/book", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<TransportBookRS> book(@RequestBody RequestData<TransportBookRQ> transportBookRQ)
	{
		log.debug("book::enter with " + transportBookRQ);
		try
		{
			TransportBookRS bookRS = searchService.book(transportBookRQ.getClient(), transportBookRQ.getData());
			if (bookRS.getErrors().size() > 0)
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
	public ResponseData<TransportCancelRS> cancel(@RequestBody RequestData<TransportCancelRQ> cancelRQ)
	{
		log.debug("cancel::enter with " + cancelRQ);
		try
		{
			TransportCancelRS cancelRS = searchService.cancel(cancelRQ.getClient(), cancelRQ.getData());
			if (cancelRS.getErrors().size() > 0)
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cancelRS.getErrors());
			else
				return new ResponseData<>(cancelRS);
		}
		catch (Exception e)
		{
			log.warn("cancel::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@Autowired
	private SaveATrainVendorStationRepository saveATrainVendorStationRepository;

	@RequestMapping(value = "/job/get/stations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData getStations()  {
		TransportBackgroundService service = new TransportBackgroundService(saveATrainVendorStationRepository, propertiesDAO);
		try {
			service.cloneAllVendorsStations("eroam");
		} catch (Exception e) {
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
		return null;
	}
}
