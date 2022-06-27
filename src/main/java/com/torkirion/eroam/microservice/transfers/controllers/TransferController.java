package com.torkirion.eroam.microservice.transfers.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.SystemProperty;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRS;
import com.torkirion.eroam.microservice.transfers.apidomain.EndpointType;
import com.torkirion.eroam.microservice.transfers.apidomain.LookupEndpointResult;
import com.torkirion.eroam.microservice.transfers.apidomain.RetrieveTransferRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.RetrieveTransferRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferSearchRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO.Endpoint;
import com.torkirion.eroam.microservice.transfers.endpoint.TransferServiceIF;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.JayrideService;
import com.torkirion.eroam.microservice.transfers.services.TransferChannelService;
import com.torkirion.eroam.microservice.transfers.services.TransferNameSearcher;
import com.torkirion.eroam.microservice.transfers.services.TransferSearchService;
import com.torkirion.eroam.microservice.transfers.services.TransferNameSearcher.NameMatch;

@RestController
@RequestMapping("/transfers/v1")
@Api(value = "Transfer Service API")
@Slf4j
@AllArgsConstructor
public class TransferController
{
	@Autowired
	private TransferSearchService searchService;

	@Autowired
	private TransferChannelService channelService;

	@Autowired
	private TransferNameSearcher transferNameSearcher;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

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
	
	@ApiOperation(value = "Lookup endpoint")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/lookupEndpoint", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public Collection<LookupEndpointResult> lookupEndpoint(@RequestParam(required = true) String text)
	{
		log.debug("lookupEndpoint::enter for " + text);

		try
		{
			List<NameMatch> matches = transferNameSearcher.getMatches(text);
			
			Collection<LookupEndpointResult> results = new TreeSet<>();
			int order = 0;
			for ( NameMatch nameMatch : matches )
			{
				LookupEndpointResult lookupEndpointResult = new LookupEndpointResult();
				lookupEndpointResult.setEndpointType(EndpointType.valueOf(nameMatch.getType()));
				lookupEndpointResult.setTypeAndCode(nameMatch.getCode());
				lookupEndpointResult.setDescription(nameMatch.getName());
				lookupEndpointResult.setOrder(order++);
				results.add(lookupEndpointResult);
			}
			
			return results;
		}
		catch (Exception e)
		{
			log.warn("searchByCode::caught exception " + e.toString(), e);
			return null;
		}
	}

	@ApiOperation(value = "Search Transfers by codes")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/searchByCode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<List<TransferResult>> searchByCode(@RequestBody RequestData<TransferSearchRQ> searchByCodeInput)
	{
		log.debug("searchByCode::enter for " + searchByCodeInput);

		try
		{
			SearchRQDTO dto = new SearchRQDTO();
			
			dto.setClient(searchByCodeInput.getClient());
			dto.setSubclient(searchByCodeInput.getSubclient());
			dto.setChannel(searchByCodeInput.getData().getChannel());
			dto.setStartPoint(new Endpoint());
			dto.getStartPoint().setEndpointType(searchByCodeInput.getData().getStartPoint().getEndpointType());
			dto.getStartPoint().setEndpointCode(searchByCodeInput.getData().getStartPoint().getEndpointCode());
			dto.setEndPoint(new Endpoint());
			dto.getEndPoint().setEndpointType(searchByCodeInput.getData().getEndPoint().getEndpointType());
			dto.getEndPoint().setEndpointCode(searchByCodeInput.getData().getEndPoint().getEndpointCode());
			dto.setIncludeReturn(searchByCodeInput.getData().getIncludeReturn());
			dto.setFlightArrivalTime(searchByCodeInput.getData().getFlightArrivalTime());
			dto.setFlightDepartureTime(searchByCodeInput.getData().getFlightDepartureTime());
			dto.setPickupTime(searchByCodeInput.getData().getPickupTime());
			dto.setReturnPickupTime(searchByCodeInput.getData().getReturnPickupTime());
			dto.setTravellers(searchByCodeInput.getData().getTravellers());
			dto.setSupplierName(searchByCodeInput.getData().getSupplierName());

			return new ResponseData<>(searchService.searchTransfers(dto));
		}
		catch (Exception e)
		{
			log.warn("searchByCode::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	
	@ApiOperation(value = "Book")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/book", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<TransferBookRS> book(@RequestBody RequestData<TransferBookRQ> transferBookRQ)
	{
		log.debug("book::enter with " + transferBookRQ);
		try
		{
			TransferBookRS bookRS = searchService.book(transferBookRQ.getClient(), transferBookRQ.getSubclient(), transferBookRQ.getData());
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
	public ResponseData<TransferCancelRS> cancel(@RequestBody RequestData<TransferCancelRQ> cancelRQ)
	{
		log.debug("cancel::enter with " + cancelRQ);
		try
		{
			// hack fixup
			if ( cancelRQ.getClient().equals("JAYRIDE"))
			{
				log.warn("cancel::incorrect client");
				cancelRQ.setClient("eroam");
			}
			TransferCancelRS cancelRS = searchService.cancel(cancelRQ.getClient(), cancelRQ.getSubclient(), cancelRQ.getData());
			if ( cancelRS.getErrors().size() > 0 )
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

	@ApiOperation(value = "Retrieve")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/retrieve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<RetrieveTransferRS> retrieve(@RequestBody RequestData<RetrieveTransferRQ> retrieveRQ)
	{
		log.debug("retrieve::enter with " + retrieveRQ);
		try
		{
			RetrieveTransferRS retrieveRS = searchService.retrieve(retrieveRQ.getClient(), retrieveRQ.getSubclient(), retrieveRQ.getData());
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
	
	@ApiOperation(value = "Initiate an RC Load for a channel")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/initiateRCLoad/{channel}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Boolean> initiateRCLoad(@PathVariable String channel, @RequestParam(value = "code", required = false) String code)
	{
		log.debug("initiateRCLoad::enter with " + channel);
		try
		{
			TransferServiceIF transferService = channelService.getTransferService(channel);
			transferService.initiateRCLoad(code);

			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			log.warn("cancel::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}


}
