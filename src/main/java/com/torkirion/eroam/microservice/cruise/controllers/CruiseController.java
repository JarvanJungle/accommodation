package com.torkirion.eroam.microservice.cruise.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.microservice.cruise.apidomain.*;
import com.torkirion.eroam.microservice.cruise.dto.*;
import com.torkirion.eroam.microservice.cruise.endpoint.CruiseServiceIF;
import com.torkirion.eroam.microservice.activities.endpoint.ActivityServiceIF;
import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.cruise.services.CruiseChannelService;
import com.torkirion.eroam.microservice.cruise.services.CruiseSearchService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@RestController
@RequestMapping("/cruise/v1")
@Api(value = "Accommodation Service API")
@Slf4j
@AllArgsConstructor
public class CruiseController
{
	@Autowired
	private CruiseSearchService searchService;

	@Autowired
	private CruiseChannelService channelService;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

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
			if ( pingCount++ % 10 == 0 )
			{
				searchService.logStats();
			}
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
			CruiseServiceIF cruiseServiceIF = channelService.getCruiseService(channel);
			cruiseServiceIF.initiateRCLoad(code);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			log.warn("initiateRCLoad::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}


	@ApiOperation(value = "Get available CruiseLines")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availCruiseLines", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<List<CruiseLine>> availCruiseLines(@RequestBody RequestData<Object> input)
	{
		log.info("availCruiseLines::enter for " + input);

		try
		{
			return new ResponseData<>(searchService.availCruiseLines(input.getClient()));
		}
		catch (Exception e)
		{
			log.warn("availCruiseLines::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Get available Destinations")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availDestinations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<List<String>> availDestinations(@RequestBody RequestData<Object> input)
	{
		log.info("availDestinations::enter for " + input);

		try
		{
			return new ResponseData<>(searchService.availDestinations(input.getClient()));
		}
		catch (Exception e)
		{
			log.warn("availDestinations::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Get available Locations (regions)")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availLocations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<List<Location>> availLocations(@RequestBody RequestData<Object> input)
	{
		log.info("availLocations::enter for " + input);

		try
		{
			return new ResponseData<>(searchService.availLocations(input.getClient()));
		}
		catch (Exception e)
		{
			log.warn("availLocations::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Search")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByDestination", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<List<CruiseResult>> availSearchByDestination(@RequestBody RequestData<AvailSearchByDestinationRQ> availSearchByDestinationInput)
	{
		log.info("availSearchByDestination::enter for " + availSearchByDestinationInput);

		SearchRQDTO dto = new SearchRQDTO();
		BeanUtils.copyProperties(availSearchByDestinationInput.getData(), dto);
		dto.setClient(availSearchByDestinationInput.getClient());

		try
		{
			if (availSearchByDestinationInput.getClient() == null)
			{
				log.warn("availSearchByDestination::bad input " + availSearchByDestinationInput);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing input data"));
			}

			return new ResponseData<>(searchService.searchCruises(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearchByDestination::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

    @ApiOperation(value = "detail cruise")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed")})
    @RequestMapping(value = "/availCruiseDetail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseData<CruiseResult> availCruiseDetail(@RequestBody @Validated RequestData<AvailDetailRQ> cruiseDetailRQRequestInput) {
        log.info("availCruiseDetail::enter for " + cruiseDetailRQRequestInput);

        try {
			var dto = new DetailRQDTO();
			BeanUtils.copyProperties(cruiseDetailRQRequestInput.getData(), dto);
			dto.setClient(cruiseDetailRQRequestInput.getClient());
            if (cruiseDetailRQRequestInput.getData().getCruiseId() == null) {
                if (log.isDebugEnabled())
                    log.info("availCruiseDetail::cruiseId not null");
                return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "cruiseId not null"));
            }
            return new ResponseData<>(searchService.detailCruise(dto));
        } catch (Exception e) {
            log.warn("availCruiseDetail::caught exception " + e.toString(), e);
            return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
        }
    }

}
