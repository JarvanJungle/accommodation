package com.torkirion.eroam.microservice.transfers.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRS;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancelRS;
import com.torkirion.eroam.microservice.transfers.apidomain.LookupEndpointResult;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferSearchRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO.Endpoint;
import com.torkirion.eroam.microservice.transfers.services.TransferChannelService;
import com.torkirion.eroam.microservice.transfers.services.TransferSearchService;

@RestController
@RequestMapping("/transfers/v1")
@Api(value = "Transfer Service API")
@Slf4j
@AllArgsConstructor
public class TransferERoamController
{
	@Autowired
	private TransferController transferController;

	@ApiOperation(value = "Get Transfer Default call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transferDefault", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public TransferResult transferDefault(@RequestBody RequestData<TransferSearchRQ> searchByCodeInput)
	{
		log.debug("transferDefault::enter for " + searchByCodeInput);

		try
		{
			ResponseData<List<TransferResult>> response = transferController.searchByCode(searchByCodeInput);

			if (response.getData() != null && response.getData().size() > 0)
				return response.getData().get(0);
			else
				log.warn("transferDefault::empty result");
		}
		catch (Exception e)
		{
			log.warn("searchByCode::caught exception " + e.toString(), e);
		}
		return null;
	}

	@ApiOperation(value = "Get Transfer List call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/transferList", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public List<TransferResult> transferList(@RequestBody RequestData<TransferSearchRQ> searchByCodeInput)
	{
		log.debug("transferDefault::enter for " + searchByCodeInput);

		try
		{
			ResponseData<List<TransferResult>> response = transferController.searchByCode(searchByCodeInput);

			if (response.getData() != null)
				return response.getData();
			else
				log.warn("transferDefault::empty result");
		}
		catch (Exception e)
		{
			log.warn("searchByCode::caught exception " + e.toString(), e);
		}
		return null;
	}

}
