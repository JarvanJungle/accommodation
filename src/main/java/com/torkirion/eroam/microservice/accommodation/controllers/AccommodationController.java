package com.torkirion.eroam.microservice.accommodation.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.temporal.ChronoUnit;
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

import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.datadomain.Location;
import com.torkirion.eroam.microservice.accommodation.dto.*;
import com.torkirion.eroam.microservice.accommodation.endpoint.RCController;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryAsync;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryService;
import com.torkirion.eroam.microservice.accommodation.repository.*;
import com.torkirion.eroam.microservice.accommodation.services.*;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationNameSearcher.NameMatch;
import com.torkirion.eroam.microservice.apidomain.APIError;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.apidomain.ResponseData;
import com.torkirion.eroam.microservice.apidomain.SystemProperty;
import com.torkirion.eroam.microservice.config.TenantContext;
import com.torkirion.eroam.microservice.datadomain.Country;
import com.torkirion.eroam.microservice.datadomain.CountryRepo;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

@RestController
@RequestMapping("/accommodation/v1")
@Api(value = "Accommodation Service API")
@Slf4j
@AllArgsConstructor
public class AccommodationController
{
	@Autowired
	private AccommodationSearchService searchService;

	@Autowired
	private AccommodationChannelService channelService;

	@Autowired
	private AccommodationRCService rcService;

	@Autowired
	private AccommodationNameSearcher accommodationNameSearcher;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private OleryService oleryService;

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
				rcService.logStats();
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

	@ApiOperation(value = "Load Rich Content for a single property")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/loadRichContent/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<AccommodationRC> loadRichContentForProperty(@PathVariable String code)
	{
		log.debug("loadRichContent::enter for code " + code);
		try
		{
			Optional<AccommodationRC> opt = rcService.getAccommodationRC(code);
			if (opt.isPresent())
				return new ResponseData<>(opt.get());
			else
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(-1, "Code invalid"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("loadRichContent::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Load Rich Content for a single property")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/loadRichContent/{client}/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<AccommodationRC> loadRichContentForClientProperty(@PathVariable String client, @PathVariable String code)
	{
		log.debug("loadRichContent::enter for client " + client + " code " + code);
		try
		{
			Optional<AccommodationRC> opt = rcService.getAccommodationRC(client, code);
			if (opt.isPresent())
				return new ResponseData<>(opt.get());
			else
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(-1, "Code invalid"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("loadRichContent::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Load Rich Content for 'top x' properties in a LatLong box")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/loadTopRichContentForGeoBox", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<List<AccommodationResult>> loadTopRichContentForGeoBox(@RequestBody RequestData<LookupTopRCByGeocoordBoxRQ> lookupTopRCByGrocoordBoxRQ)
	{
		log.debug("loadTopRichContentForGeoBox::enter for " + lookupTopRCByGrocoordBoxRQ);
		try
		{
			List<AccommodationResult> accommodationList = searchService.getTopRichContentForGeoBox(lookupTopRCByGrocoordBoxRQ.getData(), lookupTopRCByGrocoordBoxRQ.getClient());
			log.debug("loadTopRichContentForGeoBox::returning " + accommodationList.size() + " results");
			return new ResponseData<>(accommodationList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("loadTopRichContentForGeoBox::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Save Rich Content for a single property")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/saveRichContent/{code}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<AccommodationRC> saveRichContentForProperty(@PathVariable String code, @RequestBody RequestData<AccommodationRC> asccommodationRCRQ)
	{
		log.debug("saveRichContentForProperty::enter for code " + code);
		try
		{
			rcService.saveAccommodationRC(asccommodationRCRQ.getData());
			return new ResponseData<>(asccommodationRCRQ.getData());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveRichContentForProperty::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Delete Rich Content for a single property")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/deleteRichContent/{code}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<Boolean> deleteRichContentForProperty(@PathVariable String code)
	{
		log.debug("deleteRichContentForProperty::enter for code " + code);
		try
		{
			rcService.deleteAccommodationRC(code);
			return new ResponseData<>(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteRichContentForProperty::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Load Rich Content for a country")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/loadRichContent/countryCode/{countryCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<List<AccommodationRC>> loadRichContentForCountry(@PathVariable String countryCode,
			@RequestParam(value = "lastUpdate", defaultValue = "2000-01-01", required = false) String lastUpdate)
	{
		log.debug("loadRichContentForCountry::enter for " + countryCode);
		try
		{
			List<AccommodationRC> accommodationList = rcService.getAccommodationRCByCountryCode(countryCode);
			return new ResponseData<>(accommodationList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("loadRichContentForCountry::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Map Olery for a country")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/olery/mapCountryCode/{countryCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<Boolean> mapOleryCountryCode(@PathVariable String countryCode, @RequestParam(required = false) String channel)
	{
		log.debug("mapOleryCountryCode::enter for " + countryCode + ", channel " + channel);
		try
		{
			OleryAsync oleryAsync = new OleryAsync(oleryService);
			oleryAsync.mapOleryCountryCodeAsync(countryCode, channel);
			return new ResponseData<>(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("mapOleryCountryCode::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Load Olery for a country. Either enter two character country, or 'ALL'")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/olery/loadCountryCode/{countryCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseData<Boolean> loadOleryCountryCode(@PathVariable String countryCode, @RequestParam Integer passNumber)
	{
		log.debug("loadOleryCountryCode::enter for " + countryCode + " and passNumber " + passNumber);
		try
		{
			OleryAsync oleryAsync = new OleryAsync(oleryService);
			oleryAsync.loadReviews(countryCode, passNumber);
			return new ResponseData<>(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("loadOleryCountryCode::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	/*
	 * @ApiOperation(value = "Availability Search Accommodation by Location Id")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") }) //
	 * Temporaro;ly removed, as we are not doing location name searches //@RequestMapping(value = "/availSearchByLocationID",
	 * method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @Transactional public ResponseData<Set<AccommodationResult>> availSearchByLocationId(@RequestBody
	 * RequestData<AvailSearchByLocationIdRQ> availSearchByLocationInput) { log.debug("availSearchByLocationId::enter for " +
	 * availSearchByLocationInput);
	 * 
	 * try { Set<String> hotelIDs =
	 * locationDAO.getHotelIdsForInternalDestinationCode(availSearchByLocationInput.getData().getLocationId());
	 * log.debug("availSearchByLocationId::loaded iDs " + hotelIDs); TravellerMix travellerMix = new
	 * TravellerMix(availSearchByLocationInput.getData().getTravellerMix());
	 * 
	 * AvailSearchByHotelIdRQDTO dto = new AvailSearchByHotelIdRQDTO(); dto.setClient(availSearchByLocationInput.getClient());
	 * dto.setCheckin(availSearchByLocationInput.getData().getCheckin());
	 * dto.setCheckout(availSearchByLocationInput.getData().getCheckout()); dto.setTravellerMix(travellerMix);
	 * dto.setHotelIds(hotelIDs);
	 * 
	 * return new ResponseData<>(searchService.searchHotels(dto)); } catch (Exception e) {
	 * log.warn("availSearchByLocationId::caught exception " + e.toString(), e); return new
	 * ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString())); } }
	 */

	@ApiOperation(value = "Lookup hotels by name")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/lookupHotelByName", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public List<LookupHotelbynameResult> lookupHotelbyName(@RequestParam(required = true) String text)
	{
		log.debug("lookupEndpoint::enter for " + text);

		try
		{
			List<NameMatch> matches = accommodationNameSearcher.getMatches(text);

			SortedSet<LookupHotelbynameResult> results = new TreeSet<>();
			int order = 0;
			for (NameMatch nameMatch : matches)
			{
				LookupHotelbynameResult lookupEndpointResult = new LookupHotelbynameResult();
				lookupEndpointResult.setCode(nameMatch.getCode());
				lookupEndpointResult.setDescription(nameMatch.getName());
				lookupEndpointResult.setOrder(order++);
				results.add(lookupEndpointResult);
			}

			return new ArrayList<>(results);
		}
		catch (Exception e)
		{
			log.warn("lookupHotelbyName::caught exception " + e.toString(), e);
			return null;
		}
	}

	@ApiOperation(value = "Availability Search Accommodation by LatLong box")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByGeoBox", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<List<AccommodationResult>> availSearchByGeoBox(@RequestBody RequestData<AvailSearchByGeocoordBoxRQ> availSearchByGeoBoxInput)
	{
		log.info("availSearchByGeoBox::enter for " + availSearchByGeoBoxInput);

		try
		{
			AvailSearchByGeocordBoxRQDTO dto = new AvailSearchByGeocordBoxRQDTO();
			dto.setClient(availSearchByGeoBoxInput.getClient());
			dto.setCountryCodeOfOrigin(availSearchByGeoBoxInput.getData().getCountryCodeOfOrigin());
			dto.setCheckin(availSearchByGeoBoxInput.getData().getCheckin());
			dto.setCheckout(availSearchByGeoBoxInput.getData().getCheckout());
			dto.setTravellers(availSearchByGeoBoxInput.getData().getTravellers());
			dto.setChannel(availSearchByGeoBoxInput.getData().getChannel());
			dto.setChannelExceptions(availSearchByGeoBoxInput.getData().getChannelExceptions());
			dto.setNorthwest(availSearchByGeoBoxInput.getData().getNorthwest());
			dto.setSoutheast(availSearchByGeoBoxInput.getData().getSoutheast());
			dto.setKilometerFilter(availSearchByGeoBoxInput.getData().getKilometerFilter());
			dto.setDistanceCentrepoint(availSearchByGeoBoxInput.getData().getDistanceCentrepoint());

			if (dto.getClient() == null || dto.getCheckin() == null || dto.getCheckout() == null || dto.getTravellers() == null || dto.getNorthwest() == null || dto.getSoutheast() == null)
			{
				log.warn("availSearchByGeoBox::bad input " + availSearchByGeoBoxInput);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing input data"));
			}
			if (dto.getNorthwest().getLatitude() == null || dto.getNorthwest().getLongitude() == null || dto.getSoutheast().getLatitude() == null || dto.getSoutheast().getLongitude() == null)
			{
				log.warn("availSearchByGeoBox::bad input " + availSearchByGeoBoxInput);
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Missing geocordinate input data"));
			}
			if ( ChronoUnit.DAYS.between(java.time.LocalDate.now(), availSearchByGeoBoxInput.getData().getCheckin()) < 1)
			{
				if ( log.isDebugEnabled())
					log.info("availSearchByGeoBox::must search 1 day in advance");
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Dates must be one day in the future"));
			}

			return new ResponseData<>(searchService.searchHotels(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearchByGeoBox::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Availability Search Accommodation by Hotel Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/availSearchByHotelID", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<List<AccommodationResult>> availSearchByHotelId(@RequestBody RequestData<AvailSearchByHotelIdRQ> availSearchByHotelIdInput)
	{
		log.info("availSearchByHotelId::enter for " + availSearchByHotelIdInput);

		try
		{
			AvailSearchByHotelIdRQDTO dto = new AvailSearchByHotelIdRQDTO();
			dto.setClient(availSearchByHotelIdInput.getClient());
			dto.setCountryCodeOfOrigin(availSearchByHotelIdInput.getData().getCountryCodeOfOrigin());
			dto.setCheckin(availSearchByHotelIdInput.getData().getCheckin());
			dto.setCheckout(availSearchByHotelIdInput.getData().getCheckout());
			dto.setTravellers(availSearchByHotelIdInput.getData().getTravellers());
			dto.setChannel(availSearchByHotelIdInput.getData().getChannel());
			dto.setChannelExceptions(availSearchByHotelIdInput.getData().getChannelExceptions());
			dto.setHotelIds(new HashSet<String>());
			dto.getHotelIds().addAll(availSearchByHotelIdInput.getData().getHotelIds());
			if ( ChronoUnit.DAYS.between(java.time.LocalDate.now(), availSearchByHotelIdInput.getData().getCheckin()) < 1)
			{
				if ( log.isDebugEnabled())
					log.info("availSearchByHotelId::must search 1 day in advance");
				return new ResponseData<>(HttpServletResponse.SC_BAD_REQUEST, new APIError(HttpServletResponse.SC_BAD_REQUEST, "Dates must be one day in the future"));
			}
			return new ResponseData<>(searchService.searchHotels(dto));
		}
		catch (Exception e)
		{
			log.warn("availSearchByHotelId::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "Perform a rate check on a specific rateCode")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/rateCheck", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	//@Transactional
	public ResponseData<AccommodationRateCheckRS> rateCheck(@RequestBody RequestData<AccommodationRateCheckRQ> rateCheckRQ)
	{
		log.info("rateCheck::enter for " + rateCheckRQ);
		TenantContext.setTenantId(rateCheckRQ.getClient());

		try
		{
			RateCheckRQDTO dto = new RateCheckRQDTO();
			dto.setClient(rateCheckRQ.getClient());
			dto.setCountryCodeOfOrigin(rateCheckRQ.getData().getCountryCodeOfOrigin());
			dto.setCheckin(rateCheckRQ.getData().getCheckin());
			dto.setCheckout(rateCheckRQ.getData().getCheckout());
			dto.setTravellers(rateCheckRQ.getData().getTravellers());
			dto.setHotelId(rateCheckRQ.getData().getHotelId());
			dto.setChannel(rateCheckRQ.getData().getChannel());
			dto.setChannelExceptions(rateCheckRQ.getData().getChannelExceptions());
			dto.setBookingCodes(rateCheckRQ.getData().getBookingCodes());

			AccommodationRateCheckRS rateCheckRS = searchService.rateCheck(dto);
			log.debug("rateCheck::return " + rateCheckRS);
			return new ResponseData<>(rateCheckRS);
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
	public ResponseData<AccommodationBookRS> book(@RequestBody RequestData<AccommodationBookRQ> accommodationBookRQ)
	{
		log.info("book::enter with " + accommodationBookRQ);
		try
		{
			AccommodationBookRS bookRS = searchService.book(accommodationBookRQ.getClient(), accommodationBookRQ.getData());
			log.debug("book::return " + bookRS);
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
	public ResponseData<AccommodationCancelRS> cancel(@RequestBody RequestData<AccommodationCancelRQ> cancelRQ)
	{
		log.info("cancel::enter with " + cancelRQ);
		try
		{
			AccommodationCancelRS cancelRS = searchService.cancel(cancelRQ.getClient(), cancelRQ.getData());
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

	@ApiOperation(value = "Retrieve")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/retrieve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseData<AccommodationRetrieveRS> retrieve(@RequestBody RequestData<AccommodationRetrieveRQ> retrieveRQ)
	{
		log.info("retrieve::enter with " + retrieveRQ);
		try
		{
			AccommodationRetrieveRS retrieveRS = searchService.retrieve(retrieveRQ.getClient(), retrieveRQ.getData());
			if (retrieveRS.getErrors().size() > 0)
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
			RCController rcController = channelService.getRCController(channel);
			rcController.process(code);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			log.warn("cancel::caught exception " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	/*
	 * @ApiOperation(value = "Availability Search Accommodation by Geocoords")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	 * 
	 * @RequestMapping(value = "/availSearchByGeocoords", method = RequestMethod.POST, consumes =
	 * MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @Transactional public ResponseData<Object> availSearchByGeocoords(@RequestBody RequestData<AvailSearchByGeocoordRadiusRQ>
	 * availSearchByLocationInput) { log.debug("availSearchByLocation::enter for " + availSearchByLocationInput); return null; }
	 */
}
