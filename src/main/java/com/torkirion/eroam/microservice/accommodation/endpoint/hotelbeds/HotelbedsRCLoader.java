package com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import javax.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;

import com.hotelbeds.schemas.messages.Accommodation;
import com.hotelbeds.schemas.messages.AccommodationsRS;
import com.hotelbeds.schemas.messages.BoardsRS;
import com.hotelbeds.schemas.messages.CategoriesRS;
import com.hotelbeds.schemas.messages.ChainsRS;
import com.hotelbeds.schemas.messages.CommentByRates;
import com.hotelbeds.schemas.messages.Comments;
import com.hotelbeds.schemas.messages.CountriesRS;
import com.hotelbeds.schemas.messages.FacilitiesRS;
import com.hotelbeds.schemas.messages.Facility;
import com.hotelbeds.schemas.messages.FacilityGroupsRS;
import com.hotelbeds.schemas.messages.GroupCategoriesRS;
import com.hotelbeds.schemas.messages.GroupCategory;
import com.hotelbeds.schemas.messages.HotelIssue;
import com.hotelbeds.schemas.messages.Image;
import com.hotelbeds.schemas.messages.ImageTypesRS;
import com.hotelbeds.schemas.messages.IssuesRS;
import com.hotelbeds.schemas.messages.Phone;
import com.hotelbeds.schemas.messages.PromotionsRS;
import com.hotelbeds.schemas.messages.RateCommentsRS;
import com.hotelbeds.schemas.messages.RoomsRS;
import com.hotelbeds.schemas.messages.SegmentsRS;
import com.hotelbeds.schemas.messages.Terminal;
import com.hotelbeds.schemas.messages.TerminalsRS;
import com.hotelbeds.schemas.messages.Wildcard;
import com.hotelbeds.schemas.messages.Wildcards;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.GeoCoordinates;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.datadomain.Country;
import com.torkirion.eroam.microservice.datadomain.CountryRepo;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class HotelbedsRCLoader
{
	private static final int HOTEL_INCREMENT = 100;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private StaticRepo staticRepo;

	@Autowired
	private LoadRepo loadRepo;

	@Autowired
	private CountryRepo countryRepo;

	@Autowired
	private AccommodationRCService accommodationRCService;

	@Transactional
	public void loadCountries(HttpService httpService) throws Exception
	{
		log.debug("loadCountries::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "All");
		parameters.put("language", "ENG");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/locations/countries", parameters));
		CountriesRS countriesRS = (CountriesRS) unMarshal(response);
		for (com.hotelbeds.schemas.messages.Country c : countriesRS.getCountries().getCountry())
		{
			log.debug("loadCountries::country.getCode()=" + c.getCode());
			staticRepo.deleteByStaticTypeAndCode("CountriesRS", c.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("CountriesRS");
			staticData.setCode(c.getCode());
			staticRepo.save(staticData);
		}
		log.debug("loadCountries::loaded " + countriesRS.getCountries().getCountry().size() + " entries");
	}

	@Transactional
	public void loadAccommodations(HttpService httpService) throws Exception
	{
		log.debug("loadAccommodations::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "All");
		parameters.put("language", "ENG");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/accommodations", parameters));
		AccommodationsRS accommodationsRS = (AccommodationsRS) unMarshal(response);
		for (Accommodation a : accommodationsRS.getAccommodations().getAccommodation())
		{
			log.debug("loadAccommodations::code=" + a.getCode());
			staticRepo.deleteByStaticTypeAndCode("AccommodationsRS", a.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("AccommodationsRS");
			staticData.setCode(a.getCode());
			staticData.setDescription(a.getTypeDescription());
			staticRepo.save(staticData);
		}
		log.debug("loadAccommodations::loaded " + accommodationsRS.getAccommodations().getAccommodation().size() + " entries");
	}

	@Transactional
	public void loadTerminals(HttpService httpService) throws Exception
	{
		log.debug("loadTerminals::entering");

		int from = 1;
		int size = 1000;
		int loaded = 1000;
		int totalLoaded = 0;

		while (loaded == size)
		{
			Map<String, String> parameters = new HashMap<>();
			parameters.put("fields", "All");
			parameters.put("language", "ENG");
			parameters.put("from", Integer.toString(from));
			parameters.put("to", Integer.toString(from + size - 1));
			String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/terminals", parameters));
			TerminalsRS terminalsRS = (TerminalsRS) unMarshal(response);
			loaded = terminalsRS.getTerminals().getTerminal().size();
			for (Terminal t : terminalsRS.getTerminals().getTerminal())
			{
				log.debug("loadTerminals::code=" + t.getCode());
				staticRepo.deleteByStaticTypeAndCode("TerminalsRS", t.getCode());

				StaticData staticData = new StaticData();
				staticData.setStaticType("TerminalsRS");
				staticData.setCode(t.getCode());
				staticData.setDescription(t.getName());
				staticRepo.save(staticData);
				totalLoaded++;
			}
			from += size;
			log.debug("loadTerminals::loaded " + loaded);
		}
		log.debug("loadTerminals::loaded " + totalLoaded + " entries");
	}

	@Transactional
	public void loadGroupCategories(HttpService httpService) throws Exception
	{
		log.debug("loadGroupCategories::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "All");
		parameters.put("language", "ENG");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/groupcategories", parameters));
		GroupCategoriesRS groupCategoriesRS = (GroupCategoriesRS) unMarshal(response);
		for (GroupCategory g : groupCategoriesRS.getGroupCategories().getGroupCategory())
		{
			log.debug("loadGroupCategories::code=" + g.getCode());
			staticRepo.deleteByStaticTypeAndCode("GroupCategoriesRS", g.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("GroupCategoriesRS");
			staticData.setCode(g.getCode());
			staticData.setDescription(g.getDescription());
			staticRepo.save(staticData);
		}
		log.debug("loadGroupCategories::loaded " + groupCategoriesRS.getGroupCategories().getGroupCategory().size() + " entries");
	}

	@Transactional
	public void loadChains(HttpService httpService) throws Exception
	{
		log.debug("loadChains::entering");
		int from = 1;
		int size = 1000;
		int loaded = 1000;
		int totalLoaded = 0;

		while (loaded == size)
		{
			Map<String, String> parameters = new HashMap<>();
			parameters.put("fields", "All");
			parameters.put("language", "ENG");
			parameters.put("from", Integer.toString(from));
			parameters.put("to", Integer.toString(from + size - 1));
			String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/chains", parameters));
			ChainsRS chainsRS = (ChainsRS) unMarshal(response);
			loaded = chainsRS.getChains().getChain().size();
			for (com.hotelbeds.schemas.messages.Chain c : chainsRS.getChains().getChain())
			{
				log.debug("loadChains::c.getCode()=" + c.getCode());
				staticRepo.deleteByStaticTypeAndCode("ChainsRS", c.getCode());

				StaticData staticData = new StaticData();
				staticData.setStaticType("ChainsRS");
				staticData.setCode(c.getCode());
				staticData.setDescription(c.getDescription());
				staticRepo.save(staticData);
				totalLoaded++;
			}
			from += size;
			log.debug("loadChains::loaded " + loaded);
		}
		log.debug("loadChains::loaded " + totalLoaded + " entries");
	}

	@Transactional
	public void loadBoards(HttpService httpService) throws Exception
	{
		log.debug("loadBoards::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "all");
		parameters.put("language", "ENG");
		parameters.put("from", "1");
		parameters.put("to", "1000");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/boards", parameters));
		BoardsRS boardsRS = (BoardsRS) unMarshal(response);
		for (com.hotelbeds.schemas.messages.Board b : boardsRS.getBoards().getBoard())
		{
			staticRepo.deleteByStaticTypeAndCode("BoardsRS", b.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("BoardsRS");
			staticData.setCode(b.getCode());
			staticData.setDescription(b.getDescription());
			staticRepo.save(staticData);
			log.debug("loadBoards::saving " + b.getCode() + ":" + b.getDescription());
		}
		log.debug("loadBoards::loaded " + boardsRS.getBoards().getBoard().size() + " entries");
	}

	@Transactional
	public void loadSegments(HttpService httpService) throws Exception
	{
		log.debug("loadSegments::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "All");
		parameters.put("language", "ENG");
		parameters.put("from", "1");
		parameters.put("to", "1000");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/segments", parameters));
		SegmentsRS segmentsRS = (SegmentsRS) unMarshal(response);
		for (com.hotelbeds.schemas.messages.Segment s : segmentsRS.getSegments().getSegment())
		{
			staticRepo.deleteByStaticTypeAndCode("SegmentsRS", s.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("SegmentsRS");
			staticData.setCode(s.getCode());
			staticData.setDescription(s.getDescription());
			staticRepo.save(staticData);
		}
		log.debug("loadSegments::loaded " + segmentsRS.getSegments().getSegment().size() + " entries");
	}

	@Transactional
	public void loadRoomsStatic(HttpService httpService) throws Exception
	{
		log.debug("loadRoomsStatic::entering");

		int from = 1;
		int size = 1000;
		int loaded = 1000;
		int totalLoaded = 0;

		while (loaded == size)
		{
			Map<String, String> parameters = new HashMap<>();
			parameters.put("fields", "all");
			parameters.put("language", "ENG");
			parameters.put("from", Integer.toString(from));
			parameters.put("to", Integer.toString(from + size - 1));
			String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/rooms", parameters));
			RoomsRS roomsRS = (RoomsRS) unMarshal(response);
			loaded = roomsRS.getRooms().getRoom().size();
			for (com.hotelbeds.schemas.messages.RoomStatic r : roomsRS.getRooms().getRoom())
			{
				staticRepo.deleteByStaticTypeAndCode("RoomsRS", r.getCode());

				StaticData staticData = new StaticData();
				staticData.setStaticType("RoomsRS");
				staticData.setCode(r.getCode());
				staticData.setDescription(r.getDescription());
				String paxmixes = r.getMaxPax() + "|" + r.getMaxAdults() + "|" + r.getMaxChildren() + "|" + r.getMinPax() + "|" + r.getMinAdults();
				staticData.setFacilityFlags(paxmixes);
				staticRepo.save(staticData);
				totalLoaded++;
			}
			from += size;
			log.debug("loadRoomsStatic::loaded " + loaded);
		}
		log.debug("loadRoomsStatic::loaded " + totalLoaded + " entries");
	}

	@Transactional
	public void loadCategories(HttpService httpService) throws Exception
	{
		log.debug("loadCategories::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "All");
		parameters.put("language", "ENG");
		parameters.put("from", "1");
		parameters.put("to", "1000");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/categories", parameters));
		CategoriesRS categoriesRS = (CategoriesRS) unMarshal(response);
		for (com.hotelbeds.schemas.messages.Category c : categoriesRS.getCategories().getCategory())
		{
			staticRepo.deleteByStaticTypeAndCode("CategoriesRS", c.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("CategoriesRS");
			staticData.setCode(c.getCode());
			staticData.setDescription(c.getDescription());
			staticData.setSimpleCode(c.getSimpleCode());
			staticData.setCgroup(c.getGroup());
			staticRepo.save(staticData);
		}
		log.debug("loadCategories::loaded " + categoriesRS.getCategories().getCategory().size() + " entries");
	}

	@Transactional
	public void loadImageTypes(HttpService httpService) throws Exception
	{
		log.debug("loadImageTypes::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "all");
		parameters.put("language", "ENG");
		parameters.put("from", "1");
		parameters.put("to", "1000");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/imagetypes", parameters));
		ImageTypesRS imageTypesRS = (ImageTypesRS) unMarshal(response);
		for (com.hotelbeds.schemas.messages.ImageType i : imageTypesRS.getImageTypes().getImageType())
		{
			staticRepo.deleteByStaticTypeAndCode("ImageTypesRS", i.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("ImageTypesRS");
			staticData.setCode(i.getCode());
			staticData.setDescription(i.getDescription());
			staticRepo.save(staticData);
		}
		log.debug("loadImageTypes::loaded " + imageTypesRS.getImageTypes().getImageType().size() + " entries");
	}

	@Transactional
	public void loadFacilities(HttpService httpService) throws Exception
	{
		log.debug("loadFacilities::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "all");
		parameters.put("language", "ENG");
		parameters.put("from", "1");
		parameters.put("to", "1000");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/facilities", parameters));
		FacilitiesRS facilitiesRS = (FacilitiesRS) unMarshal(response);
		for (com.hotelbeds.schemas.messages.Facility f : facilitiesRS.getFacilities().getFacility())
		{
			staticRepo.deleteByStaticTypeAndCode("FacilitiesRS", f.getFacilityGroupCode() + "-" + f.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("FacilitiesRS");
			staticData.setCode(f.getFacilityGroupCode() + "-" + f.getCode());
			staticData.setDescription(f.getDescription());
			staticRepo.save(staticData);
		}
		log.debug("loadFacilities::loaded " + facilitiesRS.getFacilities().getFacility().size() + " entries");
	}

	@Transactional
	public void loadFacilityGroups(HttpService httpService) throws Exception
	{
		log.debug("loadFacilityGroups::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "All");
		parameters.put("language", "ENG");
		parameters.put("from", "1");
		parameters.put("to", "1000");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/facilitygroups", parameters));
		FacilityGroupsRS facilityGroupsRS = (FacilityGroupsRS) unMarshal(response);
		for (com.hotelbeds.schemas.messages.Group g : facilityGroupsRS.getFacilityGroups().getGroup())
		{
			staticRepo.deleteByStaticTypeAndCode("FacilityGroupsRS", g.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("FacilityGroupsRS");
			staticData.setCode(g.getCode());
			staticData.setDescription(g.getDescription());
			staticRepo.save(staticData);
		}
		log.debug("loadFacilityGroups::loaded " + facilityGroupsRS.getFacilityGroups().getGroup().size() + " entries");
	}

	@Transactional
	public void loadPromotions(HttpService httpService) throws Exception
	{
		log.debug("loadPromotions::entering");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("fields", "All");
		parameters.put("language", "ENG");
		parameters.put("from", "1");
		parameters.put("to", "1000");
		String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/promotions", parameters));
		PromotionsRS promotionsRS = (PromotionsRS) unMarshal(response);
		for (com.hotelbeds.schemas.messages.Promotion p : promotionsRS.getPromotions().getPromotion())
		{
			staticRepo.deleteByStaticTypeAndCode("PromotionsRS", p.getCode());

			StaticData staticData = new StaticData();
			staticData.setStaticType("PromotionsRS");
			staticData.setCode(p.getCode());
			staticData.setDescription(p.getDescription());
			staticData.setSimpleCode(p.getCode());
			staticRepo.save(staticData);
		}
		log.debug("loadPromotions::loaded " + promotionsRS.getPromotions().getPromotion().size() + " entries");
	}

	@Transactional
	public void loadIssues(HttpService httpService) throws Exception
	{
		log.debug("loadIssues::entering");

		int from = 1;
		int size = 1000;
		int loaded = 1000;
		int totalLoaded = 0;

		while (loaded == size)
		{
			Map<String, String> parameters = new HashMap<>();
			parameters.put("fields", "All");
			parameters.put("language", "ENG");
			parameters.put("from", Integer.toString(from));
			parameters.put("to", Integer.toString(from + size - 1));
			String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/issues", parameters));
			IssuesRS issuesRS = (IssuesRS) unMarshal(response);
			loaded = issuesRS.getIssues().size();
			for (com.hotelbeds.schemas.messages.Issues i : issuesRS.getIssues())
			{
				staticRepo.deleteByStaticTypeAndCode("IssuesRS", i.getType() + i.getCode());

				StaticData staticData = new StaticData();
				staticData.setStaticType("IssuesRS");
				staticData.setCode(i.getType() + i.getCode());
				staticData.setName(i.getName());
				staticData.setDescription(i.getDescription());
				staticData.setSimpleCode(i.getCode());
				staticData.setType(i.getType());
				staticRepo.save(staticData);
				totalLoaded++;
			}
			from += size;
			log.debug("loadIssues::loaded " + loaded);
		}
		log.debug("loadIssues::loaded " + totalLoaded + " entries");
	}

	@Transactional
	public void loadRateComments(HttpService httpService) throws Exception
	{
		log.debug("loadRateComments::entering");

		int from = 1;
		int size = 1000;
		int loaded = 1000;
		int totalLoaded = 0;

		while (loaded == size)
		{
			try
			{
				Map<String, String> parameters = new HashMap<>();
				parameters.put("fields", "all");
				parameters.put("language", "ENG");
				parameters.put("from", Integer.toString(from));
				parameters.put("to", Integer.toString(from + size - 1));
				String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/ratecomments", parameters));
				from += size;
				RateCommentsRS rateCommentsRS = (RateCommentsRS) unMarshal(response);
				loaded = rateCommentsRS.getRateComments().getRateComment().size();
				for (com.hotelbeds.schemas.messages.RateComment r : rateCommentsRS.getRateComments().getRateComment())
				{
					String incoming = r.getIncoming();
					String hotel = r.getHotel();
					String code = r.getCode();
					log.debug("loadRateComments::incoming=" + incoming + ", hotel=" + hotel + ", code=" + code + " from=" + from + ", r.size=" + r.getCommentsByRates().getCommentByRates().size());
					for (CommentByRates commentByRates : r.getCommentsByRates().getCommentByRates())
					{
						log.debug("loadRateComments::commentByRates=" + commentByRates.getRateCodes());
						String[] rateCodes = commentByRates.getRateCodes().split(" ");
						for (int i = 0; i < rateCodes.length; i++)
						{
							String fullCode = incoming + "|" + hotel + "|" + code + "|" + rateCodes[i];
							log.debug("loadRateComments::processing " + fullCode);
							for (Comments comment : commentByRates.getComments())
							{
								LocalDate dFrom = LocalDate.parse(comment.getDateStart(), yyyy_mm_ddDF);
								LocalDate dTo = LocalDate.parse(comment.getDateEnd(), yyyy_mm_ddDF);
								log.debug("loadRateComments::comment length " + comment.getDescription().length() + " = " + comment.getDescription());

								staticRepo.deleteByStaticTypeAndCode("RateCommentsRS", fullCode);

								StaticData staticData = new StaticData();
								staticData.setStaticType("RateCommentsRS");
								staticData.setCode(fullCode);
								staticData.setDescription(comment.getDescription());
								staticData.setDateFrom(dFrom);
								staticData.setDateTo(dTo);
								staticRepo.save(staticData);
								totalLoaded++;
							}
						}
					}
				}
				log.debug("loadRateComments::loaded " + loaded);
			}
			catch (Exception e)
			{
				log.warn("loadRateComments::error in unmarshal " + e.toString(), e);
			}
		}
		log.debug("loadRateComments::loaded " + totalLoaded + " entries");
	}

	@Transactional
	public Optional<Integer> loadHotels(HttpService httpService, int hotelFromLoop, String hotelcodes) throws Exception
	{
		log.debug("loadHotels::entering with hotelFromLoop " + hotelFromLoop + " and codes=" + hotelcodes);

		HotelbedsCache hotelbedsCache = new HotelbedsCache(staticRepo);
		LoadData loadData = new LoadData();
		Optional<LoadData> loadDataOpt = loadRepo.findById(0);
		if (loadDataOpt.isPresent())
		{
			loadData = loadDataOpt.get();
		}
		LocalDate lastUpdateDate = loadData.getLastHotelUpdate();
		log.debug("loadHotels::lastUpdateDate=" + lastUpdateDate);

		LocalDate now = LocalDate.now();
		LocalDate mostRecentHotelUpdate = null;

		int insertCount = 0;
		int loadCount = 0;
		int fetchCount = 0;

		boolean loop = true;
		boolean reloop = false;
		while (loop)
		{
			log.debug("loadHotels::test fetchCount=" + fetchCount);
			if (fetchCount > 0 && fetchCount % 10 == 0)
			{
				log.debug("loadHotels::break out of loop every 1000 hotels to allow transactions to settle");
				reloop = true;
				break;
			}
			fetchCount++;
			Map<String, String> parameters = new HashMap<>();
			parameters.put("fields", "all");
			parameters.put("language", "ENG");
			parameters.put("from", Integer.toString(hotelFromLoop));
			parameters.put("to", Integer.toString(hotelFromLoop + HOTEL_INCREMENT - 1));
			hotelFromLoop += HOTEL_INCREMENT;
			if (hotelcodes != null && hotelcodes.trim().length() > 0)
			{
				parameters.put("codes", hotelcodes.trim());
			}
			else if (lastUpdateDate != null)
			{
				parameters.put("lastUpdateTime", yyyy_mm_ddDF.format(lastUpdateDate));
			}
			try
			{
				String response = HotelbedsInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/hotels", parameters));
				com.hotelbeds.schemas.messages.HotelsRS hotelsRS = (com.hotelbeds.schemas.messages.HotelsRS) unMarshal(response);
				log.info("loadHotels::from=" + hotelsRS.getFrom() + ", to=" + hotelsRS.getTo() + ", total=" + hotelsRS.getTotal() + ", size=" + hotelsRS.getHotels().getHotel().size());
				if (hotelsRS.getTo().compareTo(hotelsRS.getTotal()) >= 0)
				{
					log.debug("loadHotels::flag end of loop");
					loop = false;
				}
				int innerLoopCount = 0;
				for (com.hotelbeds.schemas.messages.Hotel hotel : hotelsRS.getHotels().getHotel())
				{
					if (hotel.getName() == null || hotel.getName().length() == 0)
					{
						log.info("loadHotels::no name given for code " + hotel.getCode() + ", bypassing");
						continue;
					}
					LocalDate lastHotelUpdate = LocalDate.parse(hotel.getLastUpdate(), yyyy_mm_ddDF);
					if (mostRecentHotelUpdate == null || lastHotelUpdate.isAfter(mostRecentHotelUpdate))
					{
						mostRecentHotelUpdate = lastHotelUpdate;
					}
					String hotelCode = HotelbedsService.CHANNEL_PREFIX + hotel.getCode();
					Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(hotelCode);
					AccommodationRC accommodationRC = null;
					if (accommodationRCOpt.isPresent())
					{
						accommodationRC = accommodationRCOpt.get();
					}
					else
					{
						accommodationRC = new AccommodationRC();
						accommodationRC.setCode(hotelCode);
						insertCount++;
					}

					makeRCCore(accommodationRC, hotel, hotelbedsCache);
					makeRCErrata(accommodationRC, hotel, hotelbedsCache);
					makeRCImages(accommodationRC, hotel, hotelbedsCache);
					makeRCFacilities(accommodationRC, hotel, hotelbedsCache);

					accommodationRC.setLastUpdate(now);

					// TODO rooms
					// Map<String, String> wildCardMap = loadWildcards(conn, hotelBedsInterface, hotel.getWildcards(),
					// hotelCode);
					// loadRooms(conn, hotelBedsInterface, hotel.getRooms(), hotelCode, wildCardMap);

					accommodationRCService.saveAccommodationRC(accommodationRC);
					log.debug("loadHotels::innerLoopCount=" + innerLoopCount++ + ", loadCount = " + loadCount + ", added new=" + insertCount);
					loadCount++;
				}
			}
			catch (Exception e)
			{
				log.debug("loadHotels::caught exception " + e.toString(), e);
			}
			Functions.logMemAndYield();
		}
		if (hotelcodes == null && !reloop)
		{
			loadData.setLastHotelUpdate(mostRecentHotelUpdate);
			loadRepo.save(loadData);
		}
		log.info("loadHotels::loaded " + loadCount + ", added new " + insertCount);
		if (reloop)
		{
			log.debug("loadHotels::looping out and back to allow transactions");
			return Optional.of(hotelFromLoop);
		}
		return Optional.empty();
	}

	protected void makeRCCore(AccommodationRC accommodationRC, com.hotelbeds.schemas.messages.Hotel hotel, HotelbedsCache hotelbedsCache)
	{
		accommodationRC.setChannel(HotelbedsService.CHANNEL);
		accommodationRC.setChannelCode(hotel.getCode());

		accommodationRC.setAddress(new AccommodationRC.Address());
		String countryCode = hotel.getCountryCode();
		if (countryCode.equals("UK"))
			countryCode = "GB";
		accommodationRC.getAddress().setCountryCode(countryCode);
		Optional<Country> countryOpt = countryRepo.findById(countryCode);
		if (countryOpt.isPresent())
		{
			accommodationRC.getAddress().setCountryName(countryOpt.get().getCountryName());
		}

		accommodationRC.setRating(makeRating(hotel.getCategoryCode()));
		Optional<String> chainOpt = hotelbedsCache.getChainDescription(hotel.getChainCode());
		if (chainOpt.isPresent())
			accommodationRC.setChain(chainOpt.get());

		Optional<String> ratingNameOpt = hotelbedsCache.getCategoryDescription(hotel.getCategoryCode());
		if (ratingNameOpt.isPresent())
			accommodationRC.setRatingText(ratingNameOpt.get());
		else
		{
			log.debug("loadHotels::did not find rasting name for category code " + hotel.getCategoryCode());
			String stars = starsFormat.format(accommodationRC.getRating()) + " stars";
			accommodationRC.setRatingText(stars);
		}
		accommodationRC.setInternalDestinationCode(hotel.getDestinationCode());

		String accommodationType = hotel.getAccommodationTypeCode();
		Optional<String> accommodationTypeOpt = hotelbedsCache.getCategoryType(hotel.getCategoryCode());
		if (accommodationTypeOpt.isPresent())
		{
			accommodationType = accommodationTypeOpt.get();
		}
		accommodationRC.setProductType(makeAccommodationType(accommodationType));
		accommodationRC.setAccommodationName(hotel.getName());
		accommodationRC.setIntroduction(hotel.getDescription());
		accommodationRC.getAddress().setStreet(hotel.getAddress());
		accommodationRC.getAddress().setCity(hotel.getCity());
		accommodationRC.getAddress().setPostcode(hotel.getPostalCode());
		accommodationRC.getAddress().setState("");
		String phoneNumber = null;
		if (hotel.getPhones() != null && hotel.getPhones().getPhone() != null)
		{
			for (Phone phone : hotel.getPhones().getPhone())
			{
				if (phoneNumber == null || phone.getPhoneType().equals("PHONEHOTEL"))
				{
					phoneNumber = phone.getPhoneNumber();
					accommodationRC.setPhone(phoneNumber);
				}
			}
		}
		accommodationRC.setEmail(hotel.getEmail());
		if (hotel.getCoordinates() != null && hotel.getCoordinates().getLatitude() != null && hotel.getCoordinates().getLongitude() != null)
		{
			accommodationRC.getAddress().setGeoCoordinates(new GeoCoordinates());
			accommodationRC.getAddress().getGeoCoordinates().setLatitude(hotel.getCoordinates().getLatitude());
			accommodationRC.getAddress().getGeoCoordinates().setLongitude(hotel.getCoordinates().getLongitude());
		}
		else
		{
			log.warn("loadHotels::null lat/long loaded for " + accommodationRC.getCode());
		}

		accommodationRC.getLandmarkDistances().clear();
		if (hotel.getTerminals() != null && hotel.getTerminals().getTerminal() != null)
		{
			for (Terminal terminal : hotel.getTerminals().getTerminal())
			{
				Optional<String> terminalDescriptionOpt = hotelbedsCache.getTerminalDescription(terminal.getTerminalCode());
				if (terminalDescriptionOpt.isPresent())
				{
					AccommodationRC.Distance distance = new AccommodationRC.Distance();
					distance.setLandmark(terminalDescriptionOpt.get());
					distance.setKilometers(terminal.getDistance());
					accommodationRC.getLandmarkDistances().add(distance);
				}
			}
		}
	}

	protected void makeRCErrata(AccommodationRC accommodationRC, com.hotelbeds.schemas.messages.Hotel hotel, HotelbedsCache hotelbedsCache)
	{
		accommodationRC.getErrata().clear();
		if (hotel.getIssues() != null && hotel.getIssues().getIssue() != null)
		{
			for (HotelIssue hotelIssue : hotel.getIssues().getIssue())
			{
				StringBuffer errata = new StringBuffer();
				Optional<String> issueDescriptionOpt = hotelbedsCache.getIssueDescription(hotelIssue.getIssueType(), hotelIssue.getIssueCode());
				if (issueDescriptionOpt.isPresent())
				{
					errata.append(issueDescriptionOpt.get());
				}
				else
				{
					log.warn("loadHotels::Issues:no errata description generated for issues for " + hotelIssue.getIssueType() + "," + hotelIssue.getIssueCode() + " for " + accommodationRC.getCode());
					continue;
				}
				if (hotelIssue.getDateFrom() != null)
				{
					try
					{
						java.util.Date d = yyyy_mm_dd.parse(hotelIssue.getDateFrom());
						errata.append(" from " + dd_mm_yyyy.format(d));
					}
					catch (ParseException pe)
					{
						log.debug("loadHotels::Issues:error for date " + hotelIssue.getDateFrom() + " for " + accommodationRC.getCode());
					}
				}
				if (hotelIssue.getDateTo() != null)
				{
					try
					{
						java.util.Date d = yyyy_mm_dd.parse(hotelIssue.getDateTo());
						errata.append(" to " + dd_mm_yyyy.format(d));
					}
					catch (ParseException pe)
					{
						log.debug("loadHotels::Issues:error for date " + hotelIssue.getDateTo() + " for " + accommodationRC.getCode());
					}
				}
				if (errata.length() > 0)
					accommodationRC.getErrata().add(errata.toString());
				else
					log.warn("loadHotels::Issues:no errata generated for issues for " + accommodationRC.getCode());
			}
		}
	}

	private static final String imageURLPrefix = "https://photos.hotelbeds.com/giata/original/";

	private static final String imageThumbnailURLPrefix = "https://photos.hotelbeds.com/giata/small/";

	protected void makeRCImages(AccommodationRC accommodationRC, com.hotelbeds.schemas.messages.Hotel hotel, HotelbedsCache hotelbedsCache)
	{
		accommodationRC.getImages().clear();
		if (hotel.getImages() != null && hotel.getImages().getImage() != null)
		{
			log.debug("loadHotels::hotel has " + hotel.getImages().getImage().size() + " images");
			accommodationRC.setImageThumbnail(null);
			for (Image image : hotel.getImages().getImage())
			{
				String imageURL = imageURLPrefix + image.getPath();
				Optional<String> descriptionOpt = hotelbedsCache.getImageDescription(image.getImageTypeCode());
				log.debug("loadHotels::setting image to " + imageURL + " /" + image.getCharacteristicCode() + "/" + image.getRoomType() + "/" + image.getOrder() + "/" + descriptionOpt);
				AccommodationRC.Image rcimage = new AccommodationRC.Image();
				rcimage.setImageOrder(Integer.parseInt(image.getOrder()));
				rcimage.setImageURL(imageURL);
				rcimage.setImageDescription(descriptionOpt.isPresent() ? descriptionOpt.get() : "");
				rcimage.setImageTag(makeImageTag(image.getImageTypeCode()));
				rcimage.setTagCode(image.getRoomCode());
				accommodationRC.getImages().add(rcimage);
				if (accommodationRC.getImageThumbnail() == null)
				{
					AccommodationRC.Image rcThumbnailImage = new AccommodationRC.Image();
					rcThumbnailImage.setImageOrder(0);
					rcThumbnailImage.setImageURL(imageThumbnailURLPrefix + image.getPath());
					rcThumbnailImage.setImageDescription(descriptionOpt.isPresent() ? descriptionOpt.get() : "");
					rcThumbnailImage.setImageTag(makeImageTag(image.getImageTypeCode()));
					rcThumbnailImage.setTagCode(image.getRoomCode());
					accommodationRC.setImageThumbnail(rcThumbnailImage);
					log.debug("loadHotels::setting thumbnail image to " + rcThumbnailImage);
				}
			}
		}
		else
		{
			log.debug("loadHotels::hotel has no images");
		}
	}

	protected void makeRCFacilities(AccommodationRC accommodationRC, com.hotelbeds.schemas.messages.Hotel hotel, HotelbedsCache hotelbedsCache) throws Exception
	{
		// If a hotel doesnt specify, we take this as reasonable defaults
		String checkin = "2:00pm";
		String checkout = "11:00am";
		Map<String, List<String>> codedAmenities = new HashMap<String, List<String>>();
		accommodationRC.getFacilityGroups().clear();
		if (hotel.getFacilities() != null)
		{
			for (Facility facility : hotel.getFacilities().getFacility())
			{
				if (facility != null && facility.getFacilityCode() != null)
				{
					Optional<String> facilityDescriptionOpt = makeFacilityDescription(facility, hotelbedsCache);
					if (facilityDescriptionOpt.isPresent())
					{
						String codedGroup = makeFacilityCodedGroup(facility.getFacilityGroupCode());
						List<String> f = codedAmenities.get(codedGroup);
						if (f == null)
						{
							f = new ArrayList<String>();
							codedAmenities.put(codedGroup, f);
						}
						f.add(facilityDescriptionOpt.get());
					}
					else
					{
						continue;
					}
					if (facility.getFacilityGroupCode() != null && facility.getFacilityGroupCode().equals("70") && facility.getFacilityCode() != null)
					{
						if (facility.getFacilityCode().equals("260") && facility.getTimeFrom() != null)
						{
							try
							{
								java.util.Date t = hh_mm_ss.parse(facility.getTimeFrom());
								checkin = hh_mm_P.format(t).toLowerCase();
							}
							catch (ParseException pe)
							{
								checkin = facility.getTimeFrom();
							}
						}
						if (facility.getFacilityCode().equals("390") && facility.getTimeTo() != null)
						{
							try
							{
								java.util.Date t = hh_mm_ss.parse(facility.getTimeTo());
								checkout = hh_mm_P.format(t).toLowerCase();
							}
							catch (ParseException pe)
							{
								checkout = facility.getTimeFrom();
							}
						}
					}
				}
			}
		}

		AccommodationRC.FacilityGroup segmentGroup = null;
		if (hotel.getSegmentCodes() != null && hotel.getSegmentCodes().getSegmentCode() != null)
		{
			for (String segmentCode : hotel.getSegmentCodes().getSegmentCode())
			{
				Optional<String> segmentDescriptionOpt = hotelbedsCache.getSegmentDescription(segmentCode);
				if (segmentDescriptionOpt.isPresent())
				{
					if (segmentGroup == null)
					{
						segmentGroup = new AccommodationRC.FacilityGroup();
						segmentGroup.setGroupName("Hotel Classification");
						accommodationRC.getFacilityGroups().add(segmentGroup);
					}
					segmentGroup.getFacilities().add(segmentDescriptionOpt.get());
				}
			}
		}

		for (String s1 : codedAmenities.keySet())
		{
			AccommodationRC.FacilityGroup facilityGroup = new AccommodationRC.FacilityGroup();
			facilityGroup.setGroupName(s1);
			for (String s2 : codedAmenities.get(s1))
			{
				facilityGroup.getFacilities().add(s2);
			}
			accommodationRC.getFacilityGroups().add(facilityGroup);
		}
		accommodationRC.setCheckinTime(checkin);
		accommodationRC.setCheckoutTime(checkout);
	}

	private Map<String, String> loadWildcards(Connection conn, HotelbedsInterface hotelBedsInterface, Wildcards wildcards, String hotelID) throws Exception
	{
		log.debug("loadWildcards::entering");

		Map<String, String> wildCardMap = new HashMap<String, String>();
		if (wildcards == null || wildcards.getWildcard().size() == 0)
		{
			log.debug("loadWildcards::null or zero for hotel " + hotelID);
			return wildCardMap;
		}

		for (Wildcard wildcard : wildcards.getWildcard())
		{
			String code = wildcard.getRoomType() + "#" + wildcard.getRoomCode() + "#" + wildcard.getCharacteristicCode();
			log.debug("loadWildcards::loading code " + code + " with description " + wildcard.getHotelRoomDescription());
			wildCardMap.put(code, wildcard.getHotelRoomDescription());
		}
		log.debug("loadWildcards::loaded " + wildCardMap.size() + " wildcards for hotel " + hotelID);
		return wildCardMap;
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * private java.util.Date getLastHotelUpdateDate(Connection conn) throws Exception {
	 * log.debug("getLastHotelUpdateDate::entering");
	 * 
	 * Statement stmnt = conn.createStatement(); ResultSet rs =
	 * stmnt.executeQuery("select lastUpdateDate from hotelbedslastupdate "); java.util.Date lastUpdateDate = null; while
	 * (rs.next()) { java.sql.Date d = rs.getDate("lastUpdateDate"); lastUpdateDate = d; } return lastUpdateDate; }
	 * 
	 * private void updateLastHotelUpdateDate(Connection conn) throws Exception {
	 * log.debug("updateLastHotelUpdateDate::entering");
	 * 
	 * PreparedStatement stmnt = conn.prepareStatement("update hotelbedslastupdate set lastUpdateDate  = ?");
	 * stmnt.clearParameters(); stmnt.setDate(1, new java.sql.Date(System.currentTimeMillis())); stmnt.executeUpdate();
	 * stmnt.close(); }
	 * 
	 * public void clearDBConn() { try { if (pstmntSelectDescription != null) pstmntSelectDescription.close();
	 * 
	 * if (pstmntUpdateDescription != null) pstmntUpdateDescription.close();
	 * 
	 * if (pstmntDeleteLookup != null) pstmntDeleteLookup.close();
	 * 
	 * if (pstmntAddLookup != null) pstmntAddLookup.close(); } catch (Exception e) {
	 * 
	 * } pstmntSelectDescription = null; pstmntUpdateDescription = null; pstmntDeleteLookup = null; pstmntAddLookup = null; }
	 * 
	 * 
	 * private void loadRooms(Connection conn, HotelbedsInterface hotelBedsInterface, Rooms rooms, String hotelID, Map<String,
	 * String> wildCardMap) throws Exception { log.debug("loadRooms::entering");
	 * 
	 * if (rooms == null || rooms.getRoom().size() == 0) { log.debug("loadRooms::null or zero for hotel " + hotelID); return; }
	 * PreparedStatement pstmntGet = conn.prepareStatement("select code from rcroom where code = ?"); PreparedStatement
	 * pstmntInsert = conn.prepareStatement("insert into rcroom (code, room_name) values (?,?)"); PreparedStatement pstmntUpdate =
	 * conn.prepareStatement("update rcroom set room_name = ?, CODED_AMENITIES = ? where code = ?");
	 * 
	 * log.debug("loadRooms::loading " + rooms.getRoom().size() + " rooms"); for (Room room : rooms.getRoom()) { String roomcode =
	 * hotelID + room.getRoomCode(); log.debug("loadRooms::processing room " + roomcode);
	 * 
	 * String wildcardCode = room.getRoomType() + "#" + room.getRoomCode() + "#" + room.getCharacteristicCode(); String
	 * wildcardDescription = wildCardMap.get(wildcardCode); if (wildcardDescription == null) {
	 * log.debug("loadRooms::no Wildcard description loaded based on code " + wildcardCode); wildcardDescription = ""; } else {
	 * log.debug("loadRooms::loaded Wildcard description of " + wildcardDescription + " based on code " + wildcardCode); }
	 * 
	 * Map<String, List<String>> codedAmenities = new HashMap<String, List<String>>(); StringBuffer facilitesBuffer = new
	 * StringBuffer(); if (room.getRoomFacilities() != null && room.getRoomFacilities().getRoomFacility() != null) { for (Facility
	 * facility : room.getRoomFacilities().getRoomFacility()) { if (facility != null && facility.getFacilityCode() != null) {
	 * log.debug("loadRooms::processing facilityCode " + facility.getFacilityCode() + ", facilityGroupCode " +
	 * facility.getFacilityGroupCode()); String facilityDescription = makeFacilityDescription(facility, hotelBedsInterface, conn);
	 * if (facilityDescription != null && facility.getFacilityGroupCode() != null) { String codedGroup =
	 * makeFacilityCodedGroup(facility.getFacilityGroupCode()); List<String> f = codedAmenities.get(codedGroup); if (f == null) {
	 * f = new ArrayList<String>(); codedAmenities.put(codedGroup, f); } f.add(facilityDescription); } } }
	 * 
	 * facilitesBuffer.append(ContentDB.SECTION_SEP); for (String s1 : codedAmenities.keySet()) { facilitesBuffer.append(s1 +
	 * ContentDB.KEY_SEP); for (String s2 : codedAmenities.get(s1)) { facilitesBuffer.append(ContentDB.VAL_SEP + s2); }
	 * facilitesBuffer.append(ContentDB.SECTION_SEP); }
	 * 
	 * }
	 * 
	 * if ( (wildcardDescription != null && wildcardDescription.length() > 0) || (facilitesBuffer.length() > 0)) {
	 * pstmntGet.clearParameters(); pstmntGet.setString(1, roomcode); ResultSet rs = pstmntGet.executeQuery(); if (!rs.next()) {
	 * pstmntInsert.clearParameters(); pstmntInsert.setString(1, roomcode); pstmntInsert.setString(2, wildcardDescription);
	 * pstmntInsert.executeUpdate(); log.debug("loadRooms::created record for " + roomcode); }
	 * 
	 * pstmntUpdate.clearParameters(); pstmntUpdate.setString(1, wildcardDescription); pstmntUpdate.setString(2,
	 * facilitesBuffer.toString()); pstmntUpdate.setString(3, roomcode); int updated = pstmntUpdate.executeUpdate();
	 * log.debug("loadRooms::updated " + updated + " record for " + roomcode); } } }
	 * 
	 * 
	 */
	private String makeFacilityCodedGroup(String s)
	{
		if (s.equals("70"))
			return "Property Features";
		else if (s.equals("80"))
			return "Dining";
		else if (s.equals("73"))
			return "Entertainment";
		else if (s.equals("74") || s.equals("90"))
			return "Leisure Facilities";
		else if (s.equals("72"))
			return "Business Facilities";
		else if (s.equals("60"))
			return "Room Features";
		else
			return "Other Facilities";
	}

	private Optional<String> makeFacilityDescription(Facility facility, HotelbedsCache hotelbedsCache) throws Exception
	{
		StringBuffer description = new StringBuffer();
		Optional<String> descriptionOpt = hotelbedsCache.getFacilityDescription(facility.getFacilityGroupCode() + "-" + facility.getFacilityCode());
		if (descriptionOpt.isPresent())
		{
			description.append(descriptionOpt.get());
		}
		else
		{
			return Optional.empty();
		}
		Boolean logic = Boolean.parseBoolean(facility.getIndLogic());
		if (facility.getIndLogic() != null && !logic)
		{
			return Optional.empty();
		}
		Boolean yesOrNo = Boolean.parseBoolean(facility.getIndYesOrNo());
		if (facility.getIndYesOrNo() != null && !yesOrNo)
		{
			return Optional.empty();
		}
		if (facility.getNumber() != null && facility.getNumber().compareTo(BigInteger.ZERO) != 0)
		{
			description.append(":" + facility.getNumber());
		}
		if (facility.getDistance() != null && facility.getDistance().compareTo(BigInteger.ZERO) != 0)
		{
			description.append(", " + facility.getDistance().divide(BI_1000) + " kms");
		}
		if (facility.getAgeFrom() != null)
		{
			description.append(", age from " + facility.getAgeFrom());
		}
		if (facility.getAgeTo() != null)
		{
			description.append(", age to " + facility.getAgeTo());
		}
		if (facility.getTimeFrom() != null && !facility.getTimeFrom().equals("00:00:00"))
		{
			java.util.Date d = hh_mm_ss.parse(facility.getTimeFrom());
			description.append(" from " + hh_mm_P.format(d).toLowerCase());
		}
		if (facility.getTimeTo() != null && !facility.getTimeTo().equals("00:00:00"))
		{
			java.util.Date d = hh_mm_ss.parse(facility.getTimeTo());
			description.append(" to " + hh_mm_P.format(d).toLowerCase());
		}
		if (facility.getDateFrom() != null)
		{
			description.append(", from " + facility.getDateFrom());
		}
		if (facility.getDateTo() != null)
		{
			description.append(", to " + facility.getDateTo());
		}
		if (facility.getAmount() != null)
		{
			if (facility.getCurrency() != null)
			{
				description.append(", " + facility.getCurrency() + " " + facility.getAmount());
			}
			else
			{
				description.append(", $" + facility.getAmount());
			}
		}
		if (facility.getIndFee() != null)
		{
			Boolean fee = Boolean.parseBoolean(facility.getIndFee());
			if (fee)
				description.append(" (Paid)");
		}

		return Optional.of(description.toString());
	}

	private Object unMarshal(String response) throws Exception
	{
		if (jaxbContext == null)
			jaxbContext = JAXBContext.newInstance("com.hotelbeds.schemas.messages");
		if (unmarshaller == null)
			unmarshaller = jaxbContext.createUnmarshaller();
		ByteArrayInputStream bin = new ByteArrayInputStream(response.getBytes());
		Object responseObject = unmarshaller.unmarshal(bin);
		return responseObject;
	}

	private BigDecimal makeRating(String categoryCode)
	{
		if (categoryCode == null)
			return new BigDecimal(0);
		if (categoryCode.equals("0"))
			return new BigDecimal(0);
		if (categoryCode.equals("1EST"))
			return new BigDecimal(1);
		if (categoryCode.equals("1LL"))
			return new BigDecimal(1);
		if (categoryCode.equals("2"))
			return new BigDecimal(2);
		if (categoryCode.equals("2EST"))
			return new BigDecimal(2);
		if (categoryCode.equals("2LL"))
			return new BigDecimal(2);
		if (categoryCode.equals("3"))
			return new BigDecimal(3);
		if (categoryCode.equals("3EST"))
			return new BigDecimal(3);
		if (categoryCode.equals("3LL"))
			return new BigDecimal(3);
		if (categoryCode.equals("4EST"))
			return new BigDecimal(4);
		if (categoryCode.equals("4LL"))
			return new BigDecimal(4);
		if (categoryCode.equals("4LUX"))
			return new BigDecimal(4);
		if (categoryCode.equals("5EST"))
			return new BigDecimal(5);
		if (categoryCode.equals("5LL"))
			return new BigDecimal(5);
		if (categoryCode.equals("5LUX"))
			return new BigDecimal(5);
		if (categoryCode.equals("AG"))
			return new BigDecimal(4);
		if (categoryCode.equals("ALBER"))
			return new BigDecimal(1);
		if (categoryCode.equals("APTH"))
			return new BigDecimal(1);
		if (categoryCode.equals("APTH2"))
			return new BigDecimal(2);
		if (categoryCode.equals("APTH3"))
			return new BigDecimal(3);
		if (categoryCode.equals("APTH4"))
			return new BigDecimal(4);
		if (categoryCode.equals("APTH5"))
			return new BigDecimal(5);
		if (categoryCode.equals("AT1"))
			return new BigDecimal(1);
		if (categoryCode.equals("AT2"))
			return new BigDecimal(2);
		if (categoryCode.equals("AT3"))
			return new BigDecimal(3);
		if (categoryCode.equals("BB"))
			return new BigDecimal(3);
		if (categoryCode.equals("BB3"))
			return new BigDecimal(3);
		if (categoryCode.equals("BB4"))
			return new BigDecimal(4);
		if (categoryCode.equals("BB5"))
			return new BigDecimal(5);
		if (categoryCode.equals("BOU"))
			return new BigDecimal(4);
		if (categoryCode.equals("CAMP1"))
			return new BigDecimal(1);
		if (categoryCode.equals("CAMP2"))
			return new BigDecimal(1);
		if (categoryCode.equals("CHUES"))
			return new BigDecimal(2);
		if (categoryCode.equals("H1_5"))
			return new BigDecimal(1.5);
		if (categoryCode.equals("H2S"))
			return new BigDecimal(2);
		if (categoryCode.equals("H2_5"))
			return new BigDecimal(2.5);
		if (categoryCode.equals("H3S"))
			return new BigDecimal(3);
		if (categoryCode.equals("H3_5"))
			return new BigDecimal(3.5);
		if (categoryCode.equals("H4S"))
			return new BigDecimal(4);
		if (categoryCode.equals("H4_5"))
			return new BigDecimal(4.5);
		if (categoryCode.equals("H5S"))
			return new BigDecimal(5);
		if (categoryCode.equals("H5_5"))
			return new BigDecimal(5.5);
		if (categoryCode.equals("HIST"))
			return new BigDecimal(5);
		if (categoryCode.equals("HR"))
			return new BigDecimal(3);
		if (categoryCode.equals("HR2"))
			return new BigDecimal(2);
		if (categoryCode.equals("HR3"))
			return new BigDecimal(3);
		if (categoryCode.equals("HR4"))
			return new BigDecimal(4);
		if (categoryCode.equals("HR5"))
			return new BigDecimal(5);
		if (categoryCode.equals("HRS"))
			return new BigDecimal(4);
		if (categoryCode.equals("HS"))
			return new BigDecimal(1);
		if (categoryCode.equals("HS2"))
			return new BigDecimal(2);
		if (categoryCode.equals("HS3"))
			return new BigDecimal(3);
		if (categoryCode.equals("HS4"))
			return new BigDecimal(4);
		if (categoryCode.equals("HS5"))
			return new BigDecimal(5);
		if (categoryCode.equals("HSR1"))
			return new BigDecimal(1);
		if (categoryCode.equals("HSR2"))
			return new BigDecimal(2);
		if (categoryCode.equals("LODGE"))
			return new BigDecimal(2);
		if (categoryCode.equals("MINI"))
			return new BigDecimal(2);
		if (categoryCode.equals("PENDI"))
			return new BigDecimal(0);
		if (categoryCode.equals("PENSI"))
			return new BigDecimal(1);
		if (categoryCode.equals("POUSA"))
			return new BigDecimal(4);
		if (categoryCode.equals("RESID"))
			return new BigDecimal(3);
		if (categoryCode.equals("RSORT"))
			return new BigDecimal(4);
		if (categoryCode.equals("SPC"))
			return new BigDecimal(3);
		if (categoryCode.equals("STD"))
			return new BigDecimal(2);
		if (categoryCode.equals("SUP"))
			return new BigDecimal(4);
		if (categoryCode.equals("VILLA"))
			return new BigDecimal(4);
		if (categoryCode.equals("VTV"))
			return new BigDecimal(4);
		log.warn("getRating::unknown categoryCode " + categoryCode);
		return BigDecimal.ZERO;
	}

	private AccommodationRC.AccommodationTypeTag makeAccommodationType(String accommodationType)
	{
		switch (accommodationType)
		{
			case "HOTEL":
				return AccommodationRC.AccommodationTypeTag.HOTEL;
			case "APART":
				return AccommodationRC.AccommodationTypeTag.APARTMENT;
			case "RURAL":
				return AccommodationRC.AccommodationTypeTag.RURAL;
			case "HOSTEL":
				return AccommodationRC.AccommodationTypeTag.HOSTEL;
			case "APTHOTEL":
				return AccommodationRC.AccommodationTypeTag.APARTMENT;
			case "CAMPING":
				return AccommodationRC.AccommodationTypeTag.CAMPING;
			case "PENDING":
				return AccommodationRC.AccommodationTypeTag.HOTEL;
			case "RESORT":
				return AccommodationRC.AccommodationTypeTag.RESORT;
			case "HOMES":
				return AccommodationRC.AccommodationTypeTag.HOME;
		}
		log.warn("getRating::unknown accommodationType " + accommodationType);
		return AccommodationRC.AccommodationTypeTag.HOTEL;
	}

	private AccommodationRC.ImageTag makeImageTag(String imageCode)
	{
		switch (imageCode)
		{
			case "HAB":
				return AccommodationRC.ImageTag.ROOM;
		}
		return AccommodationRC.ImageTag.GENERAL;
	}

	private static NumberFormat starsFormat = new DecimalFormat("###.#");
			
	private static JAXBContext jaxbContext;

	private static Unmarshaller unmarshaller;

	private static DateFormat hh_mm_ss = new SimpleDateFormat("HH:mm:ss");

	private static final BigInteger BI_1000 = new BigInteger("1000");

	private static DateFormat hh_mm_P = new SimpleDateFormat("h:mma");

	private static DateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-MM-dd");

	private static DateFormat dd_mm_yyyy = new SimpleDateFormat("dd/MM/yyyy");

	private static DateTimeFormatter yyyy_mm_ddDF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
