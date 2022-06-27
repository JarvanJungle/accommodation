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
import java.util.stream.Collectors;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.http.HttpMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.ims.Functions;
import com.torkirion.eroam.ims.apidomain.APIError;
import com.torkirion.eroam.ims.apidomain.AccommodationContent;
import com.torkirion.eroam.ims.apidomain.AccommodationContentWithoutId;
import com.torkirion.eroam.ims.apidomain.AccommodationSale;
import com.torkirion.eroam.ims.apidomain.AccommodationSummary;
import com.torkirion.eroam.ims.apidomain.Activity;
import com.torkirion.eroam.ims.apidomain.ActivitySale;
import com.torkirion.eroam.ims.apidomain.Allocation;
import com.torkirion.eroam.ims.apidomain.Boards;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies;
import com.torkirion.eroam.ims.apidomain.EventType;
import com.torkirion.eroam.ims.apidomain.Rates;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.apidomain.Roomtypes;
import com.torkirion.eroam.ims.apidomain.Seasons;
import com.torkirion.eroam.ims.apidomain.Specials;
import com.torkirion.eroam.ims.apidomain.SupplierSummary;
import com.torkirion.eroam.ims.apidomain.AccommodationContentWithoutId.HotelImage;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.CancellationPolicy;
import com.torkirion.eroam.ims.apidomain.Rates.Rate;
import com.torkirion.eroam.ims.apidomain.Roomtypes.Roomtype;
import com.torkirion.eroam.ims.apidomain.Seasons.Season;
import com.torkirion.eroam.ims.apidomain.Specials.Special;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocationSummary;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationBoard;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCategory;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationFacility;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRate;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRoomtype;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSpecial;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocationSummary.AllocationKey;
import com.torkirion.eroam.ims.repository.IMSAccommodationCategoryRepo;
import com.torkirion.eroam.ims.repository.IMSAccommodationRCDataRepo;
import com.torkirion.eroam.ims.services.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.apidomain.LookupHotelbynameResult;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.controllers.AccommodationController;
import com.torkirion.eroam.microservice.accommodation.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationSearchService;
import com.torkirion.eroam.microservice.apidomain.RequestData;

@RestController
@RequestMapping("/accommodationims/v1")
@Api(value = "Accommodation IMS API")
@Slf4j
@AllArgsConstructor
public class AccommodationIMSController
{
	@Autowired
	private DataService dataService;

	@Autowired
	private AccommodationController accommodationController;

	@Autowired
	private AccommodationSearchService accommodationSearchService;

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

	@ApiOperation(value = "list all hotels")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/listAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<AccommodationSummary>> listAll(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listAll::enter");
		try
		{
			List<AccommodationSummary> all = dataService.returnSummaryOfAll();
			return new ResponseData<>(all);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listAll::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@Data
	public static class SearchMasterResult
	{
		public SearchMasterResult(String code, String description)
		{
			super();
			this.code = code;
			this.description = description;
		}

		private String code;

		private String description;
	}

	@ApiOperation(value = "search master hotel database")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/searchMaster", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<SearchMasterResult>> searchMaster(@RequestHeader("X-imsclient") String imsclient,  @RequestParam String text)
	{
		log.debug("searchMaster::enter");
		try
		{
			List<LookupHotelbynameResult> masters = accommodationController.lookupHotelbyName(text);
			List<SearchMasterResult> masterResults = new ArrayList<>();
			for (LookupHotelbynameResult lookupHotelbynameResult : masters)
			{
				masterResults.add(new SearchMasterResult(lookupHotelbynameResult.getCode(), lookupHotelbynameResult.getDescription()));
			}
			return new ResponseData<>(masterResults);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("searchMaster::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getHotelContentFromMaster")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotelMaster/{hotelId}/content", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<AccommodationContentWithoutId> getHotelContentFromMaster(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelContentFromMaster::enter for " + hotelId);
		try
		{
			com.torkirion.eroam.microservice.apidomain.ResponseData<AccommodationRC> masterContent = accommodationController.loadRichContentForProperty(hotelId);
			if (masterContent.getData() != null)
			{
				AccommodationRC accommodationRC = masterContent.getData();
				AccommodationContent accommodationContent = mapperService.mapContent(accommodationRC);
				AccommodationContentWithoutId accommodationContentWithoutId = new AccommodationContentWithoutId();
				BeanUtils.copyProperties(accommodationContent, accommodationContentWithoutId);
				return new ResponseData<>(accommodationContentWithoutId);
			}
			return new ResponseData<>(HttpServletResponse.SC_NO_CONTENT, new APIError(-1, "Content not found"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelContentFromMaster::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getHotelContent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/content", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<AccommodationContent> getHotelContent(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelContent::enter for " + hotelId);
		try
		{
			Optional<IMSAccommodationRCData> opt = dataService.getAccommodationRCDataRepo().findById(hotelId);
			if (opt.isPresent())
			{
				IMSAccommodationRCData accommodationRCData = opt.get();
				AccommodationContent accommodationContent = mapperService.map(accommodationRCData);
				return new ResponseData<>(accommodationContent);
			}
			return new ResponseData<>(HttpServletResponse.SC_NO_CONTENT, new APIError(-1, "Content not found"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelContent::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "saveHotelContent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/content", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<AccommodationContent> saveHotelContent(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @RequestBody com.torkirion.eroam.ims.apidomain.RequestData<AccommodationContent> accommodationContent)
	{
		log.debug("saveHotelContent::enter for " + accommodationContent.getData().getHotelId());
//		if ( log.isDebugEnabled())
			log.debug("saveHotelContent::content is " + accommodationContent.getData());
		try
		{
			IMSAccommodationRCData accommodationRCData = null;
			Optional<IMSAccommodationRCData> opt = dataService.getAccommodationRCDataRepo().findById(accommodationContent.getData().getHotelId());
			if (opt.isPresent())
			{
				accommodationRCData = opt.get();
			}
			if ( accommodationContent.getData().getCurrency() == null)
			{
				log.error("saveHotelContent::currency must be supplied");
				return new ResponseData<>(HttpServletResponse.SC_CONFLICT, new APIError(-1, "Currency must be supplied"));
			}

			accommodationRCData = mapperService.mapContent(accommodationContent.getData(), accommodationRCData);
			accommodationRCData.setLastUpdated(LocalDateTime.now());
			dataService.getAccommodationRCDataRepo().save(accommodationRCData);

			//AccommodationRC accommodationRC =  mapperService.mapToRC(accommodationRCData);
			//accommodationController.saveRichContentForProperty(accommodationRC.getCode(), new RequestData<>(accommodationRC));

			accommodationSearchService.clearSearchCache();
			return new ResponseData<>(accommodationContent.getData());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveHotelContent::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

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
			for (IMSAccommodationRCData rcData : dataService.getAccommodationRCDataRepo().findAll())
			{
				if ( rcData.getSupplier() != null && rcData.getSupplier().length() > 0)
					suppliers.add(rcData.getSupplier());
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

	@ApiOperation(value = "getHotelCancellationPolicies")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/cancellationPolicies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<CancellationPolicies> getHotelCancellationPolicies(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelCancellationPolicies::enter for " + hotelId);
		try
		{
			List<IMSAccommodationCancellationPolicy> policyData = dataService.getAccommodationCancellationPolicyRepo().findByHotelIdOrderByPolicyIdAscLineIdAsc(hotelId);
			CancellationPolicies cancellationPolicies = mapperService.mapPolicies(policyData);
			return new ResponseData<>(cancellationPolicies);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelCancellationPolicies::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "saveHotelCancellationPolicies")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/cancellationPolicies", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<CancellationPolicies> saveHotelCancellationPolicies(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId,
			@RequestBody com.torkirion.eroam.ims.apidomain.RequestData<CancellationPolicies> cancellationPolicies)
	{
		log.debug("saveHotelCancellationPolicies::enter for hotelId " + hotelId + " with " + cancellationPolicies);
		try
		{
			// replace any id '0' with the next available integer - so we treat '0' as "new"
			List<IMSAccommodationCancellationPolicy> existingPolicyData = dataService.getAccommodationCancellationPolicyRepo().findByHotelIdOrderByPolicyIdAscLineIdAsc(hotelId);
			int nextId = 1;
			for ( IMSAccommodationCancellationPolicy p : existingPolicyData)
			{
				if ( p.getPolicyId().intValue() >= nextId)
					nextId = p.getPolicyId().intValue() + 1;
			}
			for ( CancellationPolicy p : cancellationPolicies.getData().getPolicies())
			{
				if ( p.getPolicyId() == null || p.getPolicyId().intValue() == 0 )
				{
					log.debug("saveHotelCancellationPolicies::replacing policyId 0 for " + p.getPolicyName() + " with " + nextId);
					p.setPolicyId(nextId++);
				}
			}

			List<IMSAccommodationCancellationPolicy> policyData = mapperService.mapPolicies(cancellationPolicies.getData());

			// Dummy currency used here to allow loading and checking policy existence
			Optional<String> errorOpt = mapperService.validatePolicies(dataService.getAccommodationCancellationPolicyRepo().findByHotelIdOrderByPolicyIdAscLineIdAsc(hotelId), policyData, dataService, hotelId, "XXX", "XXX");
			if ( errorOpt.isPresent())
			{
				log.error("saveHotelCancellationPolicies::caught validation error " + errorOpt.get());
				return new ResponseData<>(HttpServletResponse.SC_CONFLICT, new APIError(-1, errorOpt.get()));
			}
			dataService.getAccommodationCancellationPolicyRepo().deleteByHotelId(hotelId);
			for (IMSAccommodationCancellationPolicy p : policyData)
			{
				if (!p.getHotelId().equals(hotelId))
				{
					throw new Exception("HotelId Mismatch");
				}
				dataService.getAccommodationCancellationPolicyRepo().save(p);
			}
			accommodationSearchService.clearSearchCache();
			return new ResponseData<>(cancellationPolicies.getData());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveHotelCancellationPolicies::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getHotelSeasons")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/seasons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Seasons> getHotelSeasons(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelSeasons::enter for " + hotelId);
		try
		{
			List<IMSAccommodationSeason> seasonData = dataService.getAccommodationSeasonRepo().findByHotelIdOrderByDateFromAsc(hotelId);
			Seasons seasons = mapperService.mapSeasons(seasonData);
			return new ResponseData<>(seasons);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelSeasons::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "saveHotelSeasons")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/seasons", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Seasons> saveHotelSeasons(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @RequestBody com.torkirion.eroam.ims.apidomain.RequestData<Seasons> seasons)
	{
		log.debug("saveHotelSeasons::enter for " + hotelId + " with " + seasons);
		try
		{
			// replace any id '0' with the next available integer - so we treat '0' as "new"
			List<IMSAccommodationSeason> existingSeasonData = dataService.getAccommodationSeasonRepo().findByHotelIdOrderByDateFromAsc(hotelId);
			int nextId = 1;
			for ( IMSAccommodationSeason p : existingSeasonData)
			{
				if ( p.getSeasonId().intValue() >= nextId)
					nextId = p.getSeasonId().intValue() + 1;
			}
			for ( Season s : seasons.getData().getSeasons())
			{
				if ( s.getSeasonId() == null || s.getSeasonId().intValue() == 0 )
				{
					log.debug("saveHotelSeasons::replacing seasonId 0 for " + s.getSeasonName() + " with " + nextId);
					s.setSeasonId(nextId++);
				}
			}

			List<IMSAccommodationSeason> seasonData = mapperService.mapSeasons(seasons.getData());

			Optional<String> errorOpt = mapperService.validateSeasons(dataService.getAccommodationSeasonRepo().findByHotelIdOrderByDateFromAsc(hotelId), seasonData, dataService, hotelId, "XXX", "XXX");
			if ( errorOpt.isPresent())
			{
				log.error("saveHotelSeasons::caught validation error " + errorOpt.get());
				return new ResponseData<>(HttpServletResponse.SC_CONFLICT, new APIError(-1, errorOpt.get()));
			}

			dataService.getAccommodationSeasonRepo().deleteByHotelId(hotelId);
			for (IMSAccommodationSeason p : seasonData)
			{
				if (!p.getHotelId().equals(hotelId))
				{
					throw new Exception("HotelId Mismatch");
				}
				dataService.getAccommodationSeasonRepo().save(p);
				log.debug("saveHotelSeasons::saving " + p);
			}
			accommodationSearchService.clearSearchCache();
			return getHotelSeasons(imsclient, hotelId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveHotelSeasons::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getHotelBoards")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/boards", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Boards> getHotelBoards(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelBoards::enter for " + hotelId);
		try
		{
			List<IMSAccommodationBoard> boardData = dataService.getAccommodationBoardRepo().findByHotelIdOrderByBoardCodeAsc(hotelId);
			Boards boards = mapperService.mapBoards(boardData);
			return new ResponseData<>(boards);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelBoards::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "saveHotelBoards")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/boards", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boards> saveHotelBoards(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @RequestBody com.torkirion.eroam.ims.apidomain.RequestData<Boards> boards)
	{
		log.debug("saveHotelBoards::enter for " + hotelId + "data: " + boards);
		try
		{
			List<IMSAccommodationBoard> boardData = mapperService.mapBoards(boards.getData());

			Optional<String> errorOpt = mapperService.validateBoards(dataService.getAccommodationBoardRepo().findByHotelIdOrderByBoardCodeAsc(hotelId), boardData, dataService, hotelId, "XXX", "XXX");
			if ( errorOpt.isPresent())
			{
				log.error("saveHotelBoards::caught validation error " + errorOpt.get());
				return new ResponseData<>(HttpServletResponse.SC_CONFLICT, new APIError(-1, errorOpt.get()));
			}

			dataService.getAccommodationBoardRepo().deleteByHotelId(hotelId);
			for (IMSAccommodationBoard b : boardData)
			{
				if (!b.getHotelId().equals(hotelId))
				{
					throw new Exception("HotelId Mismatch");
				}
				dataService.getAccommodationBoardRepo().save(b);
			}
			accommodationSearchService.clearSearchCache();
			return new ResponseData<>(mapperService.mapBoards(boardData));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveHotelBoards::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getHotelRoomtypes")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/roomtypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Roomtypes> getHotelRoomtypes(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelRoomtypes::enter for " + hotelId);
		try
		{
			Optional<IMSAccommodationRCData> rcOpt = dataService.getAccommodationRCDataRepo().findById(hotelId);
			if (rcOpt.isPresent())
			{
				Map<Integer, IMSAccommodationRate> rateMap = new HashMap<>();
				for ( IMSAccommodationRate r : dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(hotelId))
				{
					rateMap.put(r.getRateId(), r);
				}
				log.debug("getHotelRoomtypes::loaded " + rateMap.size() + " rates...");
				List<IMSAccommodationRoomtype> roomtypeData = dataService.getAccommodationRoomtypeRepo().findByHotelIdOrderByRoomtypeIdAsc(hotelId);
				Roomtypes roomtypes = mapperService.mapRoomtypes(roomtypeData);
				log.debug("getHotelRoomtypes::loaded " + roomtypes.getRoomtypes().size() + " roomtypes...");
				for ( Roomtype roomtype : roomtypes.getRoomtypes() )
				{
					if ( roomtype.getSimpleAllocation() )
					{
						log.debug("getHotelRoomtypes::roomtype " + roomtype.getRoomtypeId() + " has simple allocation");
						// simple allocation => load the rates and allocations with the same
						List<IMSAccommodationRate> rs = new ArrayList<>();
						rs.add(rateMap.get(roomtype.getRoomtypeId()));
						roomtype.setRates(mapperService.mapRates(rs, rcOpt.get().getCurrency(), rcOpt.get().getRrpCurrency()));
						roomtype.getRates().setHotelId(roomtypes.getHotelId()); // just in case the rates ome back empty ...
					}
				}
				return new ResponseData<>(roomtypes);
			}
			else
				return new ResponseData<>(HttpServletResponse.SC_NO_CONTENT, new APIError(-1, "Content not found"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelRoomtypes::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "saveHotelRoomtypes")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/roomtypes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Roomtypes> saveHotelRoomtypes(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @RequestBody com.torkirion.eroam.ims.apidomain.RequestData<Roomtypes> roomtypes)
	{
		log.debug("saveHotelRoomtypes::enter for " + hotelId + " with " + roomtypes);
		try
		{
			String currency = "";
			Optional<IMSAccommodationRCData> rcOpt = dataService.getAccommodationRCDataRepo().findById(hotelId);
			if (rcOpt.isPresent())
				currency = rcOpt.get().getCurrency();

			// replace any id '0' with the next available integer - so we treat '0' as "new"
			List<IMSAccommodationRoomtype> existingRoomData = dataService.getAccommodationRoomtypeRepo().findByHotelIdOrderByRoomtypeIdAsc(hotelId);
			int nextRoomId = 1;
			for ( IMSAccommodationRoomtype r : existingRoomData)
			{
				if ( r.getRoomtypeId().intValue() >= nextRoomId)
					nextRoomId = r.getRoomtypeId().intValue() + 1;
			}
			List<IMSAccommodationRate> existingRateData = dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(hotelId);
			int nextRateId = 1;
			for ( IMSAccommodationRate r : existingRateData)
			{
				if ( r.getRateId().intValue() >= nextRateId)
					nextRateId = r.getRateId().intValue() + 1;
			}
			for ( Roomtype r : roomtypes.getData().getRoomtypes())
			{
				if ( r.getRoomtypeId() == null || r.getRoomtypeId().intValue() == 0 )
				{
					log.debug("saveHotelRoomtypes::replacing roomtypeId 0 for " + r.getDescription() + " with " + nextRoomId);
					r.setRoomtypeId(nextRoomId++);
				}
				if ( r.getRates() != null && r.getRates().getRates() != null )
				{
					for ( Rate rate : r.getRates().getRates())
					{
						if ( rate.getRateId() == null || rate.getRateId().intValue() == 0 )
						{
							log.debug("saveHotelRoomtypes::replacing rateId 0 for " + r.getDescription() + " with " + nextRateId);
							rate.setRateId(nextRateId++);
						}
					}
				}
			}

			List<IMSAccommodationRoomtype> roomtypeData = mapperService.mapRoomtypes(roomtypes.getData(), currency);

			Optional<String> errorOpt = mapperService.validateRoomtypes(dataService.getAccommodationRoomtypeRepo().findByHotelIdOrderByRoomtypeIdAsc(hotelId), roomtypeData, dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(hotelId),  dataService, hotelId, "XXX", "XXX");
			if ( errorOpt.isPresent())
			{
				log.error("saveHotelRoomtypes::caught validation error " + errorOpt.get());
				return new ResponseData<>(HttpServletResponse.SC_CONFLICT, new APIError(-1, errorOpt.get()));
			}

			dataService.getAccommodationRoomtypeRepo().deleteByHotelId(hotelId);
			Map<Integer, IMSAccommodationAllocationSummary> allocationSummaries = new HashMap<>();
			for ( IMSAccommodationAllocationSummary i : dataService.getAccommodationAllocationSummaryRepo().findByHotelId(hotelId))
			{
				allocationSummaries.put(i.getAllocationId(), i);
			}
			for (IMSAccommodationRoomtype rt : roomtypeData)
			{
				if (!rt.getHotelId().equals(hotelId))
				{
					throw new Exception("HotelId Mismatch");
				}
				dataService.getAccommodationRoomtypeRepo().save(rt);
				log.debug("saveHotelRoomtypes::saving " + rt);
				if ( rt.getSimpleAllocation() )
				{
					log.debug("saveHotelRoomtypes::simple allocation - also saving rates, and ensuring allocation is set up");
					// also save attached rate and allocation data

					// roomtypeId == rateId == allocationId
					dataService.getAccommodationRateRepo().deleteByHotelIdAndRateId(hotelId, rt.getRoomtypeId());
					for ( IMSAccommodationRate imsRate : rt.getRates() )
					{
						imsRate.setAllocationId(rt.getRoomtypeId());
						imsRate.setDescription(rt.getDescription());
						imsRate.setRoomtypeId(rt.getRoomtypeId());
						imsRate.setRateId(rt.getRoomtypeId());
						imsRate.setHotelId(hotelId);
						dataService.getAccommodationRateRepo().save(imsRate);
						log.debug("saveHotelRoomtypes::saving rates " + imsRate);
						if ( allocationSummaries.get(imsRate.getAllocationId()) == null )
						{
							// make Allocation summary
							IMSAccommodationAllocationSummary accommodationAllocationSummary = new IMSAccommodationAllocationSummary();
							accommodationAllocationSummary.setHotelId(hotelId);
							accommodationAllocationSummary.setAllocationId(rt.getRoomtypeId());
							accommodationAllocationSummary.setAllocationDescription(rt.getDescription());
							accommodationAllocationSummary.setHandbackDays(0);
							dataService.getAccommodationAllocationSummaryRepo().save(accommodationAllocationSummary);
							allocationSummaries.put(accommodationAllocationSummary.getAllocationId(), accommodationAllocationSummary);
						}
					}
				}
			}
			accommodationSearchService.clearSearchCache();
			return new ResponseData<>(roomtypes.getData());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveHotelRoomtypes::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getHotelRates")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/rates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Rates> getHotelRates(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelRates::enter for " + hotelId);
		try
		{
			Optional<IMSAccommodationRCData> opt = dataService.getAccommodationRCDataRepo().findById(hotelId);
			if (opt.isPresent())
			{
				IMSAccommodationRCData accommodationRCData = opt.get();
				List<IMSAccommodationRate> rateData = dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(hotelId);
				log.debug("getHotelRates::loaded " + rateData.size() + " rateData records");
				Rates rates = mapperService.mapRates(rateData, accommodationRCData.getCurrency(), accommodationRCData.getRrpCurrency());
				return new ResponseData<>(rates);
			}
			else
				return new ResponseData<>(HttpServletResponse.SC_NO_CONTENT, new APIError(-1, "Content not found"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelRates::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "saveHotelRates")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/rates", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Rates> saveHotelRates(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @RequestBody com.torkirion.eroam.ims.apidomain.RequestData<Rates> rates)
	{
		log.debug("saveHotelRates::enter for " + hotelId + " with " + rates);
		try
		{
			Optional<IMSAccommodationRCData> opt = dataService.getAccommodationRCDataRepo().findById(hotelId);
			IMSAccommodationRCData accommodationRCData = null;
			if (opt.isPresent())
			{
				accommodationRCData = opt.get();
			}
			else
			{
				log.error("saveHotelRates::hotel not found " + hotelId);
				return new ResponseData<>(HttpServletResponse.SC_NOT_FOUND, new APIError(-1, "Hotel not found"));
			}

			// replace any id '0' with the next available integer - so we treat '0' as "new"
			List<IMSAccommodationRate> existingRateData = dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(hotelId);
			int nextId = 1;
			for ( IMSAccommodationRate r : existingRateData)
			{
				if ( r.getRateId().intValue() >= nextId)
					nextId = r.getRateId().intValue() + 1;
			}
			for ( Rate r : rates.getData().getRates())
			{
				if ( r.getRateId() == null || r.getRateId().intValue() == 0 )
				{
					log.debug("saveHotelRates::replacing rateId 0 for " + r.getDescription() + " with " + nextId);
					r.setRateId(nextId++);
				}
			}

			List<IMSAccommodationRate> rateData = mapperService.mapRates(rates.getData(), accommodationRCData.getCurrency());
			Optional<String> errorOpt = mapperService.validateRates(dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(hotelId), rateData, dataService, hotelId, "XXX");
			if ( errorOpt.isPresent())
			{
				log.error("saveHotelRoomtypes::caught validation error " + errorOpt.get());
				return new ResponseData<>(HttpServletResponse.SC_CONFLICT, new APIError(-1, errorOpt.get()));
			}
			dataService.getAccommodationRateRepo().deleteByHotelId(hotelId);
			log.debug("saveHotelRates::rateData has " + rateData.size() + " records");
			for (IMSAccommodationRate r : rateData)
			{
				if (!r.getHotelId().equals(hotelId))
				{
					throw new Exception("HotelId Mismatch");
				}
				dataService.getAccommodationRateRepo().save(r);
				log.debug("saveHotelRates::saving " + r);
			}
			accommodationSearchService.clearSearchCache();
			return new ResponseData<>(rates.getData());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveHotelRates::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getAllocations")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/allocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<Allocation.AllocationSummary>> getHotelAllocations(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelAllocations::enter for " + hotelId);
		try
		{
			List<IMSAccommodationAllocationSummary> dbSummaries = dataService.getAccommodationAllocationSummaryRepo().findByHotelId(hotelId);
			List<Allocation.AllocationSummary> allocationSummaries = mapperService.mapAllocationSummaries(dbSummaries);
			return new ResponseData<>(allocationSummaries);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelRates::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getAllocation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/allocations/{allocationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Allocation> getHotelAllocation(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @PathVariable Integer allocationId)
	{
		log.debug("getHotelAllocation::enter for " + hotelId + " and " + allocationId);
		try
		{

			IMSAccommodationAllocationSummary.AllocationKey allocationKey = new IMSAccommodationAllocationSummary.AllocationKey();
			allocationKey.setHotelId(hotelId);
			allocationKey.setAllocationId(allocationId);
			Optional<IMSAccommodationAllocationSummary> summaryOpt = dataService.getAccommodationAllocationSummaryRepo().findById(allocationKey);
			if (!summaryOpt.isPresent())
			{
				log.error("getHotelAllocation::not found");
				return new ResponseData<>(HttpServletResponse.SC_NO_CONTENT, new APIError(-1, "Entry not found"));
			}
			IMSAccommodationAllocationSummary s = summaryOpt.get();

			List<IMSAccommodationAllocation> dbAllocation = dataService.getAccommodationAllocationRepo().findByHotelIdAndAllocationId(hotelId, allocationId);
			log.debug("getHotelAllocation::found " + dbAllocation.size() + " records for hotel and allocationId");
			Allocation allocation = mapperService.mapAllocation(dbAllocation, s);
			return new ResponseData<>(allocation);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelAllocation::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "saveHotelAllocation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/allocations/{allocationId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Allocation> saveHotelAllocation(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @PathVariable Integer allocationId, @RequestBody com.torkirion.eroam.ims.apidomain.RequestData<Allocation> allocation)
	{
		log.debug("saveHotelAllocation::enter for " + hotelId + "data " + allocation);
		try
		{
			//if has existed allocationId then remove all of items and create again totally
			int newAllocationId = 0;
			//calculate id
			if(allocationId !=0){
				//remove all
				newAllocationId = allocationId;
				dataService.getAccommodationAllocationRepo().deleteByHotelIdAndAllocationId(hotelId,allocationId);
				dataService.getAccommodationAllocationSummaryRepo().deleteByHotelIdAndAllocationId(hotelId, allocationId);
			}else{
				List<IMSAccommodationAllocationSummary> imsAccommodationAllocationSummaryList = dataService.getAccommodationAllocationSummaryRepo().findByHotelId(hotelId);
				newAllocationId = imsAccommodationAllocationSummaryList.size() + 1;
			}
			IMSAccommodationAllocationSummary accommodationAllocationSummary = new IMSAccommodationAllocationSummary();
			accommodationAllocationSummary.setAllocationDescription(allocation.getData().getAllocationSummary().getAllocationDescription());
			accommodationAllocationSummary.setHandbackDays(allocation.getData().getAllocationSummary().getHandbackDays());
			accommodationAllocationSummary.setAllocationId(newAllocationId);
			accommodationAllocationSummary.setHotelId(allocation.getData().getAllocationSummary().getHotelId());
			dataService.getAccommodationAllocationSummaryRepo().save(accommodationAllocationSummary);
			for ( Allocation.AccommodationAllocationDateData a : allocation.getData().getAllocationDates())
			{
				IMSAccommodationAllocation imsAccommodationAllocation = new IMSAccommodationAllocation();
				imsAccommodationAllocation.setHotelId(hotelId);
				imsAccommodationAllocation.setAllocationId(newAllocationId );
				imsAccommodationAllocation.setAllocationDate(a.getDate());
				imsAccommodationAllocation.setAllocation(a.getAllocation());
				dataService.getAccommodationAllocationRepo().save(imsAccommodationAllocation);
			}
			accommodationSearchService.clearSearchCache();
			return getHotelAllocation(imsclient, hotelId, newAllocationId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveHotelAllocation::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "delete Allocation")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/allocations/{allocationId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteHotelAllocation(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @PathVariable Integer allocationId)
	{
		log.debug("deleteHotelAllocation::enter for " + hotelId + " and " + allocationId);
		try
		{
			List<IMSAccommodationRate> allRates = dataService.getAccommodationRateRepo().findAll();
			for ( IMSAccommodationRate rate : allRates )
			{
				if ( rate.getHotelId().equals(hotelId) && rate.getAllocationId()!= null && rate.getAllocationId().intValue() == allocationId.intValue())
				{
					log.info("deleteHotelAllocation::allocation " + allocationId + " in use by rate " + rate.getDescription());
					return new ResponseData<>(HttpServletResponse.SC_NO_CONTENT, new APIError(-1, "Allocation " + allocationId + " in use by rate " + rate.getDescription()));
				}
			}

			dataService.getAccommodationAllocationRepo().deleteByHotelIdAndAllocationId(hotelId, allocationId);
			IMSAccommodationAllocationSummary.AllocationKey allocationKey = new IMSAccommodationAllocationSummary.AllocationKey();
			allocationKey.setHotelId(hotelId);
			allocationKey.setAllocationId(allocationId);
			dataService.getAccommodationAllocationSummaryRepo().deleteById(allocationKey);
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelAllocation::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "getHotelSpecials")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/specials", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<Specials> getHotelSpecials(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("getHotelSpecials::enter for " + hotelId);
		try
		{
			List<IMSAccommodationSpecial> specialData = dataService.getAccommodationSpecialRepo().findByHotelId(hotelId);
			Specials specials = mapperService.mapSpecials(specialData);
			return new ResponseData<>(specials);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelSpecials::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "saveHotelSpecials")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}/specials", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Specials> saveHotelSpecials(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId, @RequestBody com.torkirion.eroam.ims.apidomain.RequestData<Specials> specials)
	{
		log.debug("saveHotelSpecials::enter for " + hotelId + " with " + specials);
		try
		{
		    validateHotelSpecials(specials);
			// replace any id '0' with the next available integer - so we treat '0' as "new"
			List<IMSAccommodationSpecial> existingSpecialsData = dataService.getAccommodationSpecialRepo().findByHotelId(hotelId);
			int nextId = 1;
			for ( IMSAccommodationSpecial s : existingSpecialsData)
			{
				if ( s.getSpecialId().intValue() >= nextId)
					nextId = s.getSpecialId().intValue() + 1;
			}
			for ( Special s : specials.getData().getSpecials())
			{
				if ( s.getSpecialId() == null || s.getSpecialId().intValue() == 0 )
				{
					log.debug("saveHotelSpecials::replacing specialId 0 for " + s.getDescription() + " with " + nextId);
					s.setSpecialId(nextId++);
				}
			}

			List<IMSAccommodationSpecial> specialData = mapperService.mapSpecials(specials.getData());

			Optional<String> errorOpt = mapperService.validateSpecials(dataService.getAccommodationSpecialRepo().findByHotelId(hotelId), specialData, dataService, hotelId, "XXX");
			if ( errorOpt.isPresent())
			{
				log.error("saveHotelSpecials::caught validation error " + errorOpt.get());
				return new ResponseData<>(HttpServletResponse.SC_CONFLICT, new APIError(-1, errorOpt.get()));
			}

			dataService.getAccommodationSpecialRepo().deleteByHotelId(hotelId);
			for (IMSAccommodationSpecial s : specialData)
			{
				if (!s.getHotelId().equals(hotelId))
				{
					throw new Exception("HotelId Mismatch");
				}
				log.debug("saveHotelSpecials::save " + s);
				dataService.getAccommodationSpecialRepo().save(s);
			}

			accommodationSearchService.clearSearchCache();
			List<IMSAccommodationSpecial> specialReturnData = dataService.getAccommodationSpecialRepo().findByHotelId(hotelId);
			Specials specialsReturn = mapperService.mapSpecials(specialReturnData);
			return new ResponseData<>(specialsReturn);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("saveHotelSpecials::error " + e, e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.getMessage()));
		}
	}

	@ApiOperation(value = "list sales")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/sales", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<AccommodationSale>> listSales(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listSales::enter");
		try
		{

			List<AccommodationSale> allSales = mapperService.mapAccommodationSales(dataService.getAccommodationSaleRepo().findAll());
			return new ResponseData<>(allSales);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listClassifications::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list suppliers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/supplier", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<SupplierSummary>> listSuppliers(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listSuppliers::enter");
		try
		{
			List<SupplierSummary> allSummary = new ArrayList<>();
			List<com.torkirion.eroam.ims.datadomain.Supplier> allData = dataService.getSupplierRepo().findByForAccommodation(true);
			log.debug("listSuppliers::found " + allData.size() + " from basic repo");
			for (com.torkirion.eroam.ims.datadomain.Supplier s : allData)
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
	}

	@ApiOperation(value = "getHotelContent")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotel/{hotelId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	@Transactional
	public @ResponseBody ResponseData<Boolean> deleteHotel(@RequestHeader("X-imsclient") String imsclient, @PathVariable String hotelId)
	{
		log.debug("deleteHotel::enter for " + hotelId);
		try
		{
			List<IMSAccommodationRate> rateData = dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(hotelId);
			List<IMSAccommodationSpecial> specialData = dataService.getAccommodationSpecialRepo().findByHotelId(hotelId);
			List<IMSAccommodationRoomtype> roomtypeData = dataService.getAccommodationRoomtypeRepo().findByHotelIdOrderByRoomtypeIdAsc(hotelId);
			if ( rateData.size() > 0 || specialData.size() > 0 || roomtypeData.size() > 0 )
			{
				return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, "Must delete specials, rates and rooms first"));
			}
			for (IMSAccommodationAllocationSummary allocSummary : dataService.getAccommodationAllocationSummaryRepo().findByHotelId(hotelId))
			{
				dataService.getAccommodationAllocationRepo().deleteByHotelIdAndAllocationId(hotelId, allocSummary.getAllocationId());
			}
			dataService.getAccommodationAllocationSummaryRepo().deleteByHotelId(hotelId);
			dataService.getAccommodationBoardRepo().deleteByHotelId(hotelId);
			dataService.getAccommodationCancellationPolicyRepo().deleteByHotelId(hotelId);
			dataService.getAccommodationSeasonRepo().deleteByHotelId(hotelId);
			dataService.getAccommodationRCDataRepo().deleteById(hotelId);
			accommodationController.deleteRichContentForProperty(IMSService.CHANNEL_PREFIX + hotelId);
			accommodationSearchService.clearSearchCache();
			return new ResponseData<>(Boolean.TRUE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("deleteHotel::error " + e.toString(), e);
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
			return new ResponseData<>(importService.importAccommodation(filename, "eroam", "SN"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("importCSV::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list categories")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<String>> listCategories(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listCategories::enter");
		try
		{
			List<String> categories = dataService.getAccommodationRCDataRepo().findDistinctCategories();
			log.debug("listCategories::found " + (categories == null ? -1 : categories.size()) + " categories");
			log.debug("listCategories::categories=" + categories);
			categories = categories.stream().filter(item-> !(item == null || item.isEmpty())).collect(Collectors.toList());
			SortedSet<String> sortedCategories = new TreeSet<>(categories);

			return new ResponseData<>(new ArrayList<String>(sortedCategories));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listCategories::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}

	@ApiOperation(value = "list facilities")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/facilities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public @ResponseBody ResponseData<List<String>> listFacilities(@RequestHeader("X-imsclient") String imsclient)
	{
		log.debug("listFacilities::enter");
		try
		{
			List<IMSAccommodationFacility> all = dataService.getAccommodationFacilityRepo().findAll();
			SortedSet<String> sortedFacilities = new TreeSet<>();
			sortedFacilities.addAll(all.stream().map(facility -> facility.getFacility()).collect(Collectors.toList()));
			return new ResponseData<>(new ArrayList<String>(sortedFacilities));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("listFacilities::error " + e.toString(), e);
			return new ResponseData<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new APIError(-1, e.toString()));
		}
	}
	private void validateHotelSpecials(com.torkirion.eroam.ims.apidomain.RequestData<Specials> specials) throws Exception {
        for (Special special:specials.getData().getSpecials()) {
            for (Integer rate :special.getRateIds()) {
                if(rate==null) throw new Exception("A null value passed in rate field");
            }
        }
    }
}
