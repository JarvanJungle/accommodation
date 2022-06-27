package com.torkirion.eroam.microservice.merchandise.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseResult;
import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseRateCheckRQ;
import com.torkirion.eroam.microservice.merchandise.apidomain.AvailMerchandiseSearchByBrandRQ;
import com.torkirion.eroam.microservice.merchandise.apidomain.AvailMerchandiseSearchByCodeRQ;
import com.torkirion.eroam.microservice.merchandise.apidomain.AvailMerchandiseSearchRQ;
import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseBookRQ;
import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseBookRS;
import com.torkirion.eroam.microservice.merchandise.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.merchandise.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.merchandise.services.MerchandiseChannelService;
import com.torkirion.eroam.microservice.merchandise.services.MerchandiseSearchService;
import com.torkirion.eroam.ims.datadomain.EventMerchandiseLink;
import com.torkirion.eroam.ims.datadomain.EventSeries;
import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.SystemProperty;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@RestController
@RequestMapping("/merchandise/v1")
@Api(value = "Merchandise Service API")
@Slf4j
@AllArgsConstructor
public class MerchandiseController
{
	@Autowired
	private MerchandiseSearchService searchService;

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

	@ApiOperation(value = "List all brands/series")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/listBrands", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<Collection<String>> listBrands(@RequestBody RequestData<AvailMerchandiseSearchRQ> availSearchRQ)
	{
		log.debug("listBrands::enter for " + availSearchRQ);

		try
		{
			SortedSet<String> brands = new TreeSet<>();
			// hackety hack hack!
			// Since brand linking can ONLY occur between IMS Events and IMS Merchandise, just slide right over to the IMS..
			/*
			List<EventSeries> eventSeries = dataService.getEventSeriesRepo().findAll();
			for ( EventSeries es : eventSeries)
			{
				if ( es.getEventMerchandiseLinks() != null )
				{
					for ( EventMerchandiseLink link : es.getEventMerchandiseLinks())
					{
						// we may eventually not show mandatory linked items? 
						brands.add(es.getName());
					}
				}
			}
			return new ResponseData<>(brands);
			*/
			// TODO
			return null;
		}
		catch (Exception e)
		{
			log.warn("listBrands::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Merchandise Search Accommodation ")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<Collection<MerchandiseResult>> availSearch(@RequestBody RequestData<AvailMerchandiseSearchRQ> availSearchRQ)
	{
		log.debug("availSearch::enter for " + availSearchRQ);

		try
		{
			AvailSearchRQDTO dto = new AvailSearchRQDTO();
			dto.setClient(availSearchRQ.getClient());
			dto.setCountryCodeOfOrigin(availSearchRQ.getData().getCountryCodeOfOrigin());
			dto.setChannel(availSearchRQ.getData().getChannel());

			return new ResponseData<>(searchService.searchMerchandise(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearch::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Merchandise Search Accommodation by Brand")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByBrand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<Collection<MerchandiseResult>> availSearchByBrand(@RequestBody RequestData<AvailMerchandiseSearchByBrandRQ> availSearchRQ)
	{
		log.debug("availSearch::enter for " + availSearchRQ);

		try
		{
			AvailSearchRQDTO dto = new AvailSearchRQDTO();
			dto.setClient(availSearchRQ.getClient());
			dto.setCountryCodeOfOrigin(availSearchRQ.getData().getCountryCodeOfOrigin());
			dto.setChannel(availSearchRQ.getData().getChannel());
			dto.setBrand(availSearchRQ.getData().getBrand());

			return new ResponseData<>(searchService.searchMerchandise(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearch::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	@ApiOperation(value = "Merchandise Search Accommodation by Code")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByCode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<Collection<MerchandiseResult>> availSearchByCode(@RequestBody RequestData<AvailMerchandiseSearchByCodeRQ> availSearchRQ)
	{
		log.debug("availSearchByCode::enter for " + availSearchRQ);

		try
		{
			AvailSearchRQDTO dto = new AvailSearchRQDTO();
			dto.setClient(availSearchRQ.getClient());
			dto.setCountryCodeOfOrigin(availSearchRQ.getData().getCountryCodeOfOrigin());
			dto.setChannel(availSearchRQ.getData().getChannel());
			dto.setMerchandiseId(availSearchRQ.getData().getMerchandiseId());

			return new ResponseData<>(searchService.searchMerchandise(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearch::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Perform a rate check on a specific rateCode")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/rateCheck", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<MerchandiseResult> rateCheck(@RequestBody RequestData<MerchandiseRateCheckRQ> rateCheckRQ)
	{
		log.debug("rateCheck::enter for " + rateCheckRQ);

		try
		{
			RateCheckRQDTO dto = new RateCheckRQDTO();
			dto.setClient(rateCheckRQ.getClient());
			dto.setCountryCodeOfOrigin(rateCheckRQ.getData().getCountryCodeOfOrigin());
			dto.setChannel(rateCheckRQ.getData().getChannel());
			dto.setMerchandiseId(rateCheckRQ.getData().getMerchandiseId());
			dto.setOptionId(rateCheckRQ.getData().getOptionId());
			dto.setCount(rateCheckRQ.getData().getCount());

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
	public ResponseData<MerchandiseBookRS> book(@RequestBody RequestData<MerchandiseBookRQ> bookRQ)
	{
		log.debug("book::enter with " + bookRQ);
		try
		{
			MerchandiseBookRS bookRS = searchService.book(bookRQ.getClient(), bookRQ.getData());
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
