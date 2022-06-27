package com.torkirion.eroam.microservice.accommodation.endpoint.youtravel;

import com.hotelbeds.schemas.messages.*;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.cache.AirlineCacheUtil;
import com.torkirion.eroam.microservice.datadomain.Country;
import com.torkirion.eroam.microservice.datadomain.CountryRepo;
import com.torkirion.eroam.microservice.datadomain.IataAirportV2;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.util.StringUtils;
import com.youtravel.schemas.messages.HTDetail;
import com.youtravel.schemas.messages.HotelDetail;
import com.youtravel.schemas.messages.HtSearchRq;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@AllArgsConstructor
@Slf4j
@Service
public class YoutravelRCLoader {
    private static final int HOTEL_INCREMENT = 100;
    private static final String imageURLPrefix = "https://photos.hotelbeds.com/giata/original/";
    private static final String imageThumbnailURLPrefix = "https://photos.hotelbeds.com/giata/small/";
    private static final BigInteger BI_1000 = new BigInteger("1000");
    private static NumberFormat starsFormat = new DecimalFormat("###.#");
    private static JAXBContext jaxbContext;
    private static Unmarshaller unmarshaller;
    private static DateFormat hh_mm_ss = new SimpleDateFormat("HH:mm:ss");
    private static DateFormat hh_mm_P = new SimpleDateFormat("h:mma");
    private static DateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat dd_mm_yyyy = new SimpleDateFormat("dd/MM/yyyy");
    private static DateTimeFormatter yyyy_mm_ddDF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Autowired
    AirlineCacheUtil airlineCacheUtil;
    @Autowired
    private SystemPropertiesDAO propertiesDAO;
    @Autowired
    private YoutravelStaticRepo youtravelStaticRepo;
    @Autowired
    private YoutravelLoadRepo youtravelLoadRepo;
    @Autowired
    private CountryRepo countryRepo;
    @Autowired
    private AccommodationRCService accommodationRCService;


    @Transactional
    public void loadCountries(HttpService httpService) throws Exception {
        log.debug("loadCountries::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "All");
        parameters.put("language", "ENG");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/locations/countries", parameters));
        CountriesRS countriesRS = (CountriesRS) unMarshal(response);
        for (com.hotelbeds.schemas.messages.Country c : countriesRS.getCountries().getCountry()) {
            log.debug("loadCountries::country.getCode()=" + c.getCode());
            youtravelStaticRepo.deleteByStaticTypeAndCode("CountriesRS", c.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("CountriesRS");
            youtravelStaticData.setCode(c.getCode());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadCountries::loaded " + countriesRS.getCountries().getCountry().size() + " entries");
    }

    @Transactional
    public void loadAccommodations(HttpService httpService) throws Exception {
        log.debug("loadAccommodations::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "All");
        parameters.put("language", "ENG");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/accommodations", parameters));
        AccommodationsRS accommodationsRS = (AccommodationsRS) unMarshal(response);
        for (Accommodation a : accommodationsRS.getAccommodations().getAccommodation()) {
            log.debug("loadAccommodations::code=" + a.getCode());
            youtravelStaticRepo.deleteByStaticTypeAndCode("AccommodationsRS", a.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("AccommodationsRS");
            youtravelStaticData.setCode(a.getCode());
            youtravelStaticData.setDescription(a.getTypeDescription());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadAccommodations::loaded " + accommodationsRS.getAccommodations().getAccommodation().size() + " entries");
    }

    @Transactional
    public void loadTerminals(HttpService httpService) throws Exception {
        log.debug("loadTerminals::entering");

        int from = 1;
        int size = 1000;
        int loaded = 1000;
        int totalLoaded = 0;

        while (loaded == size) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("fields", "All");
            parameters.put("language", "ENG");
            parameters.put("from", Integer.toString(from));
            parameters.put("to", Integer.toString(from + size - 1));
            String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/terminals", parameters));
            TerminalsRS terminalsRS = (TerminalsRS) unMarshal(response);
            loaded = terminalsRS.getTerminals().getTerminal().size();
            for (Terminal t : terminalsRS.getTerminals().getTerminal()) {
                log.debug("loadTerminals::code=" + t.getCode());
                youtravelStaticRepo.deleteByStaticTypeAndCode("TerminalsRS", t.getCode());

                YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
                youtravelStaticData.setStaticType("TerminalsRS");
                youtravelStaticData.setCode(t.getCode());
                youtravelStaticData.setDescription(t.getName());
                youtravelStaticRepo.save(youtravelStaticData);
                totalLoaded++;
            }
            from += size;
            log.debug("loadTerminals::loaded " + loaded);
        }
        log.debug("loadTerminals::loaded " + totalLoaded + " entries");
    }

    @Transactional
    public void loadGroupCategories(HttpService httpService) throws Exception {
        log.debug("loadGroupCategories::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "All");
        parameters.put("language", "ENG");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/groupcategories", parameters));
        GroupCategoriesRS groupCategoriesRS = (GroupCategoriesRS) unMarshal(response);
        for (GroupCategory g : groupCategoriesRS.getGroupCategories().getGroupCategory()) {
            log.debug("loadGroupCategories::code=" + g.getCode());
            youtravelStaticRepo.deleteByStaticTypeAndCode("GroupCategoriesRS", g.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("GroupCategoriesRS");
            youtravelStaticData.setCode(g.getCode());
            youtravelStaticData.setDescription(g.getDescription());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadGroupCategories::loaded " + groupCategoriesRS.getGroupCategories().getGroupCategory().size() + " entries");
    }

    @Transactional
    public void loadChains(HttpService httpService) throws Exception {
        log.debug("loadChains::entering");
        int from = 1;
        int size = 1000;
        int loaded = 1000;
        int totalLoaded = 0;

        while (loaded == size) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("fields", "All");
            parameters.put("language", "ENG");
            parameters.put("from", Integer.toString(from));
            parameters.put("to", Integer.toString(from + size - 1));
            String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/chains", parameters));
            ChainsRS chainsRS = (ChainsRS) unMarshal(response);
            loaded = chainsRS.getChains().getChain().size();
            for (com.hotelbeds.schemas.messages.Chain c : chainsRS.getChains().getChain()) {
                log.debug("loadChains::c.getCode()=" + c.getCode());
                youtravelStaticRepo.deleteByStaticTypeAndCode("ChainsRS", c.getCode());

                YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
                youtravelStaticData.setStaticType("ChainsRS");
                youtravelStaticData.setCode(c.getCode());
                youtravelStaticData.setDescription(c.getDescription());
                youtravelStaticRepo.save(youtravelStaticData);
                totalLoaded++;
            }
            from += size;
            log.debug("loadChains::loaded " + loaded);
        }
        log.debug("loadChains::loaded " + totalLoaded + " entries");
    }

    @Transactional
    public void loadBoards(HttpService httpService) throws Exception {
        log.debug("loadBoards::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "all");
        parameters.put("language", "ENG");
        parameters.put("from", "1");
        parameters.put("to", "1000");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/boards", parameters));
        BoardsRS boardsRS = (BoardsRS) unMarshal(response);
        for (com.hotelbeds.schemas.messages.Board b : boardsRS.getBoards().getBoard()) {
            youtravelStaticRepo.deleteByStaticTypeAndCode("BoardsRS", b.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("BoardsRS");
            youtravelStaticData.setCode(b.getCode());
            youtravelStaticData.setDescription(b.getDescription());
            youtravelStaticRepo.save(youtravelStaticData);
            log.debug("loadBoards::saving " + b.getCode() + ":" + b.getDescription());
        }
        log.debug("loadBoards::loaded " + boardsRS.getBoards().getBoard().size() + " entries");
    }

    @Transactional
    public void loadSegments(HttpService httpService) throws Exception {
        log.debug("loadSegments::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "All");
        parameters.put("language", "ENG");
        parameters.put("from", "1");
        parameters.put("to", "1000");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/segments", parameters));
        SegmentsRS segmentsRS = (SegmentsRS) unMarshal(response);
        for (com.hotelbeds.schemas.messages.Segment s : segmentsRS.getSegments().getSegment()) {
            youtravelStaticRepo.deleteByStaticTypeAndCode("SegmentsRS", s.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("SegmentsRS");
            youtravelStaticData.setCode(s.getCode());
            youtravelStaticData.setDescription(s.getDescription());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadSegments::loaded " + segmentsRS.getSegments().getSegment().size() + " entries");
    }

    @Transactional
    public void loadRoomsStatic(HttpService httpService) throws Exception {
        log.debug("loadRoomsStatic::entering");

        int from = 1;
        int size = 1000;
        int loaded = 1000;
        int totalLoaded = 0;

        while (loaded == size) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("fields", "all");
            parameters.put("language", "ENG");
            parameters.put("from", Integer.toString(from));
            parameters.put("to", Integer.toString(from + size - 1));
            String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/rooms", parameters));
            RoomsRS roomsRS = (RoomsRS) unMarshal(response);
            loaded = roomsRS.getRooms().getRoom().size();
            for (com.hotelbeds.schemas.messages.RoomStatic r : roomsRS.getRooms().getRoom()) {
                youtravelStaticRepo.deleteByStaticTypeAndCode("RoomsRS", r.getCode());

                YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
                youtravelStaticData.setStaticType("RoomsRS");
                youtravelStaticData.setCode(r.getCode());
                youtravelStaticData.setDescription(r.getDescription());
                String paxmixes = r.getMaxPax() + "|" + r.getMaxAdults() + "|" + r.getMaxChildren() + "|" + r.getMinPax() + "|" + r.getMinAdults();
                youtravelStaticData.setFacilityFlags(paxmixes);
                youtravelStaticRepo.save(youtravelStaticData);
                totalLoaded++;
            }
            from += size;
            log.debug("loadRoomsStatic::loaded " + loaded);
        }
        log.debug("loadRoomsStatic::loaded " + totalLoaded + " entries");
    }

    @Transactional
    public void loadCategories(HttpService httpService) throws Exception {
        log.debug("loadCategories::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "All");
        parameters.put("language", "ENG");
        parameters.put("from", "1");
        parameters.put("to", "1000");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/categories", parameters));
        CategoriesRS categoriesRS = (CategoriesRS) unMarshal(response);
        for (com.hotelbeds.schemas.messages.Category c : categoriesRS.getCategories().getCategory()) {
            youtravelStaticRepo.deleteByStaticTypeAndCode("CategoriesRS", c.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("CategoriesRS");
            youtravelStaticData.setCode(c.getCode());
            youtravelStaticData.setDescription(c.getDescription());
            youtravelStaticData.setSimpleCode(c.getSimpleCode());
            youtravelStaticData.setCgroup(c.getGroup());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadCategories::loaded " + categoriesRS.getCategories().getCategory().size() + " entries");
    }

    @Transactional
    public void loadImageTypes(HttpService httpService) throws Exception {
        log.debug("loadImageTypes::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "all");
        parameters.put("language", "ENG");
        parameters.put("from", "1");
        parameters.put("to", "1000");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/imagetypes", parameters));
        ImageTypesRS imageTypesRS = (ImageTypesRS) unMarshal(response);
        for (com.hotelbeds.schemas.messages.ImageType i : imageTypesRS.getImageTypes().getImageType()) {
            youtravelStaticRepo.deleteByStaticTypeAndCode("ImageTypesRS", i.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("ImageTypesRS");
            youtravelStaticData.setCode(i.getCode());
            youtravelStaticData.setDescription(i.getDescription());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadImageTypes::loaded " + imageTypesRS.getImageTypes().getImageType().size() + " entries");
    }

    @Transactional
    public void loadFacilities(HttpService httpService) throws Exception {
        log.debug("loadFacilities::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "all");
        parameters.put("language", "ENG");
        parameters.put("from", "1");
        parameters.put("to", "1000");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/facilities", parameters));
        FacilitiesRS facilitiesRS = (FacilitiesRS) unMarshal(response);
        for (Facility f : facilitiesRS.getFacilities().getFacility()) {
            youtravelStaticRepo.deleteByStaticTypeAndCode("FacilitiesRS", f.getFacilityGroupCode() + "-" + f.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("FacilitiesRS");
            youtravelStaticData.setCode(f.getFacilityGroupCode() + "-" + f.getCode());
            youtravelStaticData.setDescription(f.getDescription());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadFacilities::loaded " + facilitiesRS.getFacilities().getFacility().size() + " entries");
    }

    @Transactional
    public void loadFacilityGroups(HttpService httpService) throws Exception {
        log.debug("loadFacilityGroups::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "All");
        parameters.put("language", "ENG");
        parameters.put("from", "1");
        parameters.put("to", "1000");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/facilitygroups", parameters));
        FacilityGroupsRS facilityGroupsRS = (FacilityGroupsRS) unMarshal(response);
        for (com.hotelbeds.schemas.messages.Group g : facilityGroupsRS.getFacilityGroups().getGroup()) {
            youtravelStaticRepo.deleteByStaticTypeAndCode("FacilityGroupsRS", g.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("FacilityGroupsRS");
            youtravelStaticData.setCode(g.getCode());
            youtravelStaticData.setDescription(g.getDescription());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadFacilityGroups::loaded " + facilityGroupsRS.getFacilityGroups().getGroup().size() + " entries");
    }

    @Transactional
    public void loadPromotions(HttpService httpService) throws Exception {
        log.debug("loadPromotions::entering");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fields", "All");
        parameters.put("language", "ENG");
        parameters.put("from", "1");
        parameters.put("to", "1000");
        String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/promotions", parameters));
        PromotionsRS promotionsRS = (PromotionsRS) unMarshal(response);
        for (com.hotelbeds.schemas.messages.Promotion p : promotionsRS.getPromotions().getPromotion()) {
            youtravelStaticRepo.deleteByStaticTypeAndCode("PromotionsRS", p.getCode());

            YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
            youtravelStaticData.setStaticType("PromotionsRS");
            youtravelStaticData.setCode(p.getCode());
            youtravelStaticData.setDescription(p.getDescription());
            youtravelStaticData.setSimpleCode(p.getCode());
            youtravelStaticRepo.save(youtravelStaticData);
        }
        log.debug("loadPromotions::loaded " + promotionsRS.getPromotions().getPromotion().size() + " entries");
    }

    @Transactional
    public void loadIssues(HttpService httpService) throws Exception {
        log.debug("loadIssues::entering");

        int from = 1;
        int size = 1000;
        int loaded = 1000;
        int totalLoaded = 0;

        while (loaded == size) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("fields", "All");
            parameters.put("language", "ENG");
            parameters.put("from", Integer.toString(from));
            parameters.put("to", Integer.toString(from + size - 1));
            String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/issues", parameters));
            IssuesRS issuesRS = (IssuesRS) unMarshal(response);
            loaded = issuesRS.getIssues().size();
            for (com.hotelbeds.schemas.messages.Issues i : issuesRS.getIssues()) {
                youtravelStaticRepo.deleteByStaticTypeAndCode("IssuesRS", i.getType() + i.getCode());

                YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
                youtravelStaticData.setStaticType("IssuesRS");
                youtravelStaticData.setCode(i.getType() + i.getCode());
                youtravelStaticData.setName(i.getName());
                youtravelStaticData.setDescription(i.getDescription());
                youtravelStaticData.setSimpleCode(i.getCode());
                youtravelStaticData.setType(i.getType());
                youtravelStaticRepo.save(youtravelStaticData);
                totalLoaded++;
            }
            from += size;
            log.debug("loadIssues::loaded " + loaded);
        }
        log.debug("loadIssues::loaded " + totalLoaded + " entries");
    }

    @Transactional
    public void loadRateComments(HttpService httpService) throws Exception {
        log.debug("loadRateComments::entering");

        int from = 1;
        int size = 1000;
        int loaded = 1000;
        int totalLoaded = 0;

        while (loaded == size) {
            try {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("fields", "all");
                parameters.put("language", "ENG");
                parameters.put("from", Integer.toString(from));
                parameters.put("to", Integer.toString(from + size - 1));
                String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("hotel-content-api/1.0/types/ratecomments", parameters));
                from += size;
                RateCommentsRS rateCommentsRS = (RateCommentsRS) unMarshal(response);
                loaded = rateCommentsRS.getRateComments().getRateComment().size();
                for (com.hotelbeds.schemas.messages.RateComment r : rateCommentsRS.getRateComments().getRateComment()) {
                    String incoming = r.getIncoming();
                    String hotel = r.getHotel();
                    String code = r.getCode();
                    log.debug("loadRateComments::incoming=" + incoming + ", hotel=" + hotel + ", code=" + code + " from=" + from + ", r.size=" + r.getCommentsByRates().getCommentByRates().size());
                    for (CommentByRates commentByRates : r.getCommentsByRates().getCommentByRates()) {
                        log.debug("loadRateComments::commentByRates=" + commentByRates.getRateCodes());
                        String[] rateCodes = commentByRates.getRateCodes().split(" ");
                        for (int i = 0; i < rateCodes.length; i++) {
                            String fullCode = incoming + "|" + hotel + "|" + code + "|" + rateCodes[i];
                            log.debug("loadRateComments::processing " + fullCode);
                            for (Comments comment : commentByRates.getComments()) {
                                LocalDate dFrom = LocalDate.parse(comment.getDateStart(), yyyy_mm_ddDF);
                                LocalDate dTo = LocalDate.parse(comment.getDateEnd(), yyyy_mm_ddDF);
                                log.debug("loadRateComments::comment length " + comment.getDescription().length() + " = " + comment.getDescription());

                                youtravelStaticRepo.deleteByStaticTypeAndCode("RateCommentsRS", fullCode);

                                YoutravelStaticData youtravelStaticData = new YoutravelStaticData();
                                youtravelStaticData.setStaticType("RateCommentsRS");
                                youtravelStaticData.setCode(fullCode);
                                youtravelStaticData.setDescription(comment.getDescription());
                                youtravelStaticData.setDateFrom(dFrom);
                                youtravelStaticData.setDateTo(dTo);
                                youtravelStaticRepo.save(youtravelStaticData);
                                totalLoaded++;
                            }
                        }
                    }
                }
                log.debug("loadRateComments::loaded " + loaded);
            } catch (Exception e) {
                log.warn("loadRateComments::error in unmarshal " + e.toString(), e);
            }
        }
        log.debug("loadRateComments::loaded " + totalLoaded + " entries");
    }

    @Transactional
    public Optional<Integer> loadHotels(HttpService httpService, int hotelFromLoop, String hotelcodes) throws Exception {
        log.debug("loadHotels::entering with hotelFromLoop " + hotelFromLoop + " and codes=" + hotelcodes);
        YoutravelCache youtravelCache = new YoutravelCache(youtravelStaticRepo);
        YoutravelLoadData youtravelLoadData = new YoutravelLoadData();
        Optional<YoutravelLoadData> loadDataOpt = youtravelLoadRepo.findById(0);
        if (loadDataOpt.isPresent()) {
            youtravelLoadData = loadDataOpt.get();
        }
        LocalDate lastUpdateDate = youtravelLoadData.getLastHotelUpdate();
        log.debug("loadHotels::lastUpdateDate=" + lastUpdateDate);

        LocalDate now = LocalDate.now();
        LocalDate mostRecentHotelUpdate = null;

        int insertCount = 0;
        int loadCount = 0;
        int fetchCount = 0;

        boolean loop = true;
        boolean reloop = false;
        jaxbContext = JAXBContext.newInstance(HTDetail.class);
        Unmarshaller unmHotel = jaxbContext.createUnmarshaller();
        jaxbContext = JAXBContext.newInstance(HtSearchRq.class);
        Unmarshaller unmCountry = jaxbContext.createUnmarshaller();
        while (loop) {
            log.debug("loadHotels::test fetchCount=" + fetchCount);
            if (fetchCount > 0 && fetchCount % 10 == 0) {
                log.debug("loadHotels::break out of loop every 1000 hotels to allow transactions to settle");
                reloop = true;
                break;
            }
            fetchCount++;
            Map<String, String> parameters = new HashMap<>();
            parameters.put("LangID", "ENG");
            parameters.put("Username", propertiesDAO.getProperty("eroam", "YOUTRAVEL", "apiKey"));
            parameters.put("Password", propertiesDAO.getProperty("eroam", "YOUTRAVEL", "secret"));
            hotelFromLoop += HOTEL_INCREMENT;
            try {
                String response = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("get_hotel_list.asp", parameters));
                HtSearchRq htSearchRq = (HtSearchRq) unMarshal(response, unmCountry);
                int innerLoopCount = 0;
                for (com.youtravel.schemas.messages.Country country : htSearchRq.getCountries()) {
                    for (com.youtravel.schemas.messages.Destination destination : country.getDestinations()) {
                        for (com.youtravel.schemas.messages.Resort resort : destination.getResorts()) {
                            for (com.youtravel.schemas.messages.Hotel hotel : resort.getHotels()) {
                                if (hotel.getHotelName() == null || hotel.getHotelName().length() == 0) {
                                    log.info("loadHotels::no name given for id " + hotel.getHotelId() + ", bypassing");
                                    continue;
                                }

//					LocalDate lastHotelUpdate = LocalDate.parse(hotel.getLastUpdate(), yyyy_mm_ddDF);
//					if (mostRecentHotelUpdate == null || lastHotelUpdate.isAfter(mostRecentHotelUpdate))
//					{
//						mostRecentHotelUpdate = lastHotelUpdate;
//					}
                                String hotelCode = YoutravelService.CHANNEL_PREFIX + hotel.getHotelId();
                                AccommodationRC accommodationRC = new AccommodationRC();
                                accommodationRC.setCode(hotelCode);
                                insertCount++;

//                                if (accommodationRCOpt.isPresent()) {
//                                    accommodationRC = accommodationRCOpt.get();
//                                    accommodationRC
//                                } else {
//                                    accommodationRC = new AccommodationRC();
//                                    accommodationRC.setCode(hotelCode);
//                                    insertCount++;
//                                }

                                // call api detail by Id
                                Map<String, String> parameterMap = new HashMap<>();
                                parameterMap.put("LangID", "EN");
                                parameterMap.put("Username", propertiesDAO.getProperty("eroam", "YOUTRAVEL", "apiKey"));
                                parameterMap.put("Password", propertiesDAO.getProperty("eroam", "YOUTRAVEL", "secret"));
                                parameterMap.put("HID", hotel.getHotelId().toString());
                                parameterMap.put("sha", "1");
                                String htDetailResponse = YoutravelInterface.fixResponseNamespaces(httpService.doCallGet("get_hoteldetails.asp", parameterMap));
                                HTDetail htDetail = (HTDetail) unMarshal(htDetailResponse, unmHotel);
                                HotelDetail hotelDetail = htDetail.getHotelDetail();
                                makeRCCore(accommodationRC, hotelDetail, youtravelCache, hotel, country, destination);
//						    makeRCErrata(accommodationRC, hotel, youtravelCache);
                                makeRCImages(accommodationRC, hotelDetail, youtravelCache);
                                makeRCFacilities(accommodationRC, hotelDetail, youtravelCache);
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
                    }
                }
            } catch (Exception e) {
                log.debug("loadHotels::caught exception " + e.toString(), e);
            }
            Functions.logMemAndYield();
        }
        if (hotelcodes == null && !reloop) {
            youtravelLoadData.setLastHotelUpdate(mostRecentHotelUpdate);
            youtravelLoadRepo.save(youtravelLoadData);
        }
        log.info("loadHotels::loaded " + loadCount + ", added new " + insertCount);
        if (reloop) {
            log.debug("loadHotels::looping out and back to allow transactions");
            return Optional.of(hotelFromLoop);
        }
        return Optional.empty();
    }

    protected void makeRCCore(AccommodationRC accommodationRC, com.youtravel.schemas.messages.HotelDetail hotelDetail,
                              YoutravelCache youtravelCache, com.youtravel.schemas.messages.Hotel hotel,
                              com.youtravel.schemas.messages.Country country, com.youtravel.schemas.messages.Destination destination) throws Exception {
        accommodationRC.setChannel(YoutravelService.CHANNEL);
        accommodationRC.setChannelCode(hotel.getHotelId().toString());
        String countryCode = country.getCode();
        accommodationRC.setAddress(new AccommodationRC.Address());
        if (countryCode.equals("UK"))
            countryCode = "GB";
        accommodationRC.getAddress().setCountryCode(countryCode);
        String isoDestinationCode = destination.getIsoCodes().getCode1();

        Optional<Country> countryOpt = countryRepo.findById(countryCode);
        if (countryOpt.isPresent()) {
            accommodationRC.getAddress().setCountryName(countryOpt.get().getCountryName());
        }
        if (hotelDetail != null) {
            BigDecimal rating = new BigDecimal(hotelDetail.getYoutravelRating());
            accommodationRC.setRating(rating);
            String stars = hotelDetail.getOfficialRating();
            accommodationRC.setRatingText(stars);
//            accommodationRC.setDescription(hotelDetail.getHotelDesc());
//        }

//        accommodationRC.setInternalDestinationCode(hotel.getDestinationCode());

//        String accommodationType = hotel.getAccommodationTypeCode();
//        Optional<String> accommodationTypeOpt = youtravelCache.getCategoryType(hotel.getCategoryCode());
//        if (accommodationTypeOpt.isPresent()) {
//            accommodationType = accommodationTypeOpt.get();
//        }
//        accommodationRC.setProductType(makeAccommodationType(accommodationType));
            accommodationRC.setAccommodationName(hotelDetail.getName());
            accommodationRC.setIntroduction(hotelDetail.getHotelDesc());
        }
        if (hotelDetail.getHotelAddress() != null) {
            accommodationRC.getAddress().setFullFormAddress(hotelDetail.getHotelAddress().getAddress());
            accommodationRC.getAddress().setCity(hotelDetail.getHotelAddress().getCity());
            accommodationRC.getAddress().setPostcode(hotelDetail.getHotelAddress().getPostCode());
            accommodationRC.setPhone(hotelDetail.getHotelAddress().getPhone());
        } else {
            IataAirportV2 iataAirportV2 = airlineCacheUtil.getByCode(isoDestinationCode);
            if (iataAirportV2 != null) {
                accommodationRC.getAddress().setCity(iataAirportV2.getCityname());
                accommodationRC.getAddress().setFullFormAddress(destination.getName());
            }
        }
        accommodationRC.getAddress().setState("");
        String lat = hotel.getMapping().getLatitude();
        String lon = hotel.getMapping().getLongitude();
        if (hotel.getMapping() != null && lat != null && lon != null && StringUtils.isNumeric(lat) && StringUtils.isNumeric(lon)) {
            accommodationRC.getAddress().setGeoCoordinates(new AccommodationRC.GeoCoordinates());
            accommodationRC.getAddress().getGeoCoordinates().setLatitude(new BigDecimal(hotel.getMapping().getLatitude()));
            accommodationRC.getAddress().getGeoCoordinates().setLongitude(new BigDecimal(hotel.getMapping().getLongitude()));
        } else {
            log.warn("loadHotels::null lat/long loaded for " + accommodationRC.getCode());
        }
        List<String> reratas = new ArrayList<>();
        reratas.add(hotelDetail.getReratas());
        accommodationRC.setErrata(reratas);
        accommodationRC.getLandmarkDistances().clear();
//        if (hotel.getTerminals() != null && hotel.getTerminals().getTerminal() != null) {
//            for (Terminal terminal : hotel.getTerminals().getTerminal()) {
//                Optional<String> terminalDescriptionOpt = youtravelCache.getTerminalDescription(terminal.getTerminalCode());
//                if (terminalDescriptionOpt.isPresent()) {
//                    AccommodationRC.Distance distance = new AccommodationRC.Distance();
//                    distance.setLandmark(terminalDescriptionOpt.get());
//                    distance.setKilometers(terminal.getDistance());
//                    accommodationRC.getLandmarkDistances().add(distance);
//                }
//            }
//        }

    }

//    protected void makeRCErrata(AccommodationRC accommodationRC, com.youtravel.schemas.messages.Hotel hotel, YoutravelCache youtravelCache) {
//        accommodationRC.getErrata().clear();
//        if (hotel.getIssues() != null && hotel.getIssues().getIssue() != null) {
//            for (HotelIssue hotelIssue : hotel.getIssues().getIssue()) {
//                StringBuffer errata = new StringBuffer();
//                Optional<String> issueDescriptionOpt = youtravelCache.getIssueDescription(hotelIssue.getIssueType(), hotelIssue.getIssueCode());
//                if (issueDescriptionOpt.isPresent()) {
//                    errata.append(issueDescriptionOpt.get());
//                } else {
//                    log.warn("loadHotels::Issues:no errata description generated for issues for " + hotelIssue.getIssueType() + "," + hotelIssue.getIssueCode() + " for " + accommodationRC.getCode());
//                    continue;
//                }
//                if (hotelIssue.getDateFrom() != null) {
//                    try {
//                        Date d = yyyy_mm_dd.parse(hotelIssue.getDateFrom());
//                        errata.append(" from " + dd_mm_yyyy.format(d));
//                    } catch (ParseException pe) {
//                        log.debug("loadHotels::Issues:error for date " + hotelIssue.getDateFrom() + " for " + accommodationRC.getCode());
//                    }
//                }
//                if (hotelIssue.getDateTo() != null) {
//                    try {
//                        Date d = yyyy_mm_dd.parse(hotelIssue.getDateTo());
//                        errata.append(" to " + dd_mm_yyyy.format(d));
//                    } catch (ParseException pe) {
//                        log.debug("loadHotels::Issues:error for date " + hotelIssue.getDateTo() + " for " + accommodationRC.getCode());
//                    }
//                }
//                if (errata.length() > 0)
//                    accommodationRC.getErrata().add(errata.toString());
//                else
//                    log.warn("loadHotels::Issues:no errata generated for issues for " + accommodationRC.getCode());
//            }
//        }
//    }

    protected void makeRCImages(AccommodationRC accommodationRC, com.youtravel.schemas.messages.HotelDetail hotel, YoutravelCache youtravelCache) {
        accommodationRC.getImages().clear();
        if (hotel != null && hotel.getPhotos() != null) {
            log.debug("loadHotels::hotel has " + hotel.getPhotos().size() + " images");
            if (hotel.getPhotos().size() != 0) {
                AccommodationRC.Image imageThumbnail = new AccommodationRC.Image();
                imageThumbnail.setImageURL(hotel.getPhotos().get(0));
                accommodationRC.setImageThumbnail(imageThumbnail);
            }
            for (String image : hotel.getPhotos()) {
//                String imageURL = imageURLPrefix + image.getPath();
//                Optional<String> descriptionOpt = youtravelCache.getImageDescription(image.getImageTypeCode());
//                log.debug("loadHotels::setting image to " + imageURL + " /" + image.getCharacteristicCode() + "/" + image.getRoomType() + "/" + image.getOrder() + "/" + descriptionOpt);
                AccommodationRC.Image rcimage = new AccommodationRC.Image();
//                rcimage.setImageOrder(Integer.parseInt(image.getOrder()));
                rcimage.setImageURL(image);
                rcimage.setImageDescription("");
                rcimage.setChannelCode("");
                rcimage.setImageOrder(0);
                rcimage.setImageTag(null);
                rcimage.setTagCode("");
//                rcimage.setImageDescription(descriptionOpt.isPresent() ? descriptionOpt.get() : "");
//                rcimage.setImageDescription(descriptionOpt.isPresent() ? descriptionOpt.get() : "");
//                rcimage.setImageTag(makeImageTag(image.getImageTypeCode()));
//                rcimage.setTagCode(image.getRoomCode());
                accommodationRC.getImages().add(rcimage);
//                if (accommodationRC.getImageThumbnail() == null) {
//                    AccommodationRC.Image rcThumbnailImage = new AccommodationRC.Image();
//                    rcThumbnailImage.setImageOrder(0);
////                    rcThumbnailImage.setImageURL(imageThumbnailURLPrefix + image.getPath());
////                    rcThumbnailImage.setImageDescription(descriptionOpt.isPresent() ? descriptionOpt.get() : "");
////                    rcThumbnailImage.setImageTag(makeImageTag(image.getImageTypeCode()));
////                    rcThumbnailImage.setTagCode(image.getRoomCode());
//                    accommodationRC.setImageThumbnail(rcThumbnailImage);
//                    log.debug("loadHotels::setting thumbnail image to " + rcThumbnailImage);
//                }
            }
        } else {
            log.debug("loadHotels::hotel has no images");
        }
    }

    protected void makeRCFacilities(AccommodationRC accommodationRC, com.youtravel.schemas.messages.HotelDetail hotel, YoutravelCache youtravelCache) throws Exception {
        // If a hotel doesnt specify, we take this as reasonable defaults
        String checkin = "2:00pm";
        String checkout = "11:00am";
        Map<String, List<String>> codedAmenities = new HashMap<String, List<String>>();
        accommodationRC.getFacilityGroups().clear();
//        if (hotel.getFacilities() != null) {
//            for (String facility : hotel.getFacilities()) {
////                if (facility != null && facility.getFacilityCode() != null) {
////                    Optional<String> facilityDescriptionOpt = makeFacilityDescription(facility, youtravelCache);
//                    if (facilityDescriptionOpt.isPresent()) {
//                        String codedGroup = makeFacilityCodedGroup(facility.getFacilityGroupCode());
//                        List<String> f = codedAmenities.get(codedGroup);
//                        if (f == null) {
//                            f = new ArrayList<String>();
//                            codedAmenities.put(codedGroup, f);
//                        }
//                        f.add(facilityDescriptionOpt.get());
//                    } else {
//                        continue;
//                    }
//                    if (facility.getFacilityGroupCode() != null && facility.getFacilityGroupCode().equals("70") && facility.getFacilityCode() != null) {
//                        if (facility.getFacilityCode().equals("260") && facility.getTimeFrom() != null) {
//                            try {
//                                Date t = hh_mm_ss.parse(facility.getTimeFrom());
//                                checkin = hh_mm_P.format(t).toLowerCase();
//                            } catch (ParseException pe) {
//                                checkin = facility.getTimeFrom();
//                            }
//                        }
//                        if (facility.getFacilityCode().equals("390") && facility.getTimeTo() != null) {
//                            try {
//                                Date t = hh_mm_ss.parse(facility.getTimeTo());
//                                checkout = hh_mm_P.format(t).toLowerCase();
//                            } catch (ParseException pe) {
//                                checkout = facility.getTimeFrom();
//                            }
//                        }
//                    }
////                }
//            }
//        }

//        AccommodationRC.FacilityGroup segmentGroup = null;
//        if (hotel.getSegmentCodes() != null && hotel.getSegmentCodes().getSegmentCode() != null) {
//            for (String segmentCode : hotel.getSegmentCodes().getSegmentCode()) {
//                Optional<String> segmentDescriptionOpt = youtravelCache.getSegmentDescription(segmentCode);
//                if (segmentDescriptionOpt.isPresent()) {
//                    if (segmentGroup == null) {
//                        segmentGroup = new AccommodationRC.FacilityGroup();
//                        segmentGroup.setGroupName("Hotel Classification");
//                        accommodationRC.getFacilityGroups().add(segmentGroup);
//                    }
//                    segmentGroup.getFacilities().add(segmentDescriptionOpt.get());
//                }
//            }
//        }
//
//        for (String s1 : codedAmenities.keySet()) {
//            AccommodationRC.FacilityGroup facilityGroup = new AccommodationRC.FacilityGroup();
//            facilityGroup.setGroupName(s1);
//            for (String s2 : codedAmenities.get(s1)) {
//                facilityGroup.getFacilities().add(s2);
//            }
//            accommodationRC.getFacilityGroups().add(facilityGroup);
//        }
        List<AccommodationRC.FacilityGroup> facilityGroupList = new ArrayList<>();
        AccommodationRC.FacilityGroup facilityGroup = new AccommodationRC.FacilityGroup();
        facilityGroup.setGroupName("other");
        facilityGroup.setFacilities(hotel.getFacilities());
        if (hotel.getFacilities().size() != 0) {
            System.out.println("aa");
        }
        facilityGroupList.add(facilityGroup);
        accommodationRC.setFacilityGroups(facilityGroupList);
        accommodationRC.setCheckinTime(checkin);
        accommodationRC.setCheckoutTime(checkout);
    }

    private Map<String, String> loadWildcards(Connection conn, YoutravelInterface hotelBedsInterface, Wildcards wildcards, String hotelID) throws Exception {
        log.debug("loadWildcards::entering");

        Map<String, String> wildCardMap = new HashMap<String, String>();
        if (wildcards == null || wildcards.getWildcard().size() == 0) {
            log.debug("loadWildcards::null or zero for hotel " + hotelID);
            return wildCardMap;
        }

        for (Wildcard wildcard : wildcards.getWildcard()) {
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
    private String makeFacilityCodedGroup(String s) {
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

    private Optional<String> makeFacilityDescription(Facility facility, YoutravelCache youtravelCache) throws Exception {
        StringBuffer description = new StringBuffer();
        Optional<String> descriptionOpt = youtravelCache.getFacilityDescription(facility.getFacilityGroupCode() + "-" + facility.getFacilityCode());
        if (descriptionOpt.isPresent()) {
            description.append(descriptionOpt.get());
        } else {
            return Optional.empty();
        }
        Boolean logic = Boolean.parseBoolean(facility.getIndLogic());
        if (facility.getIndLogic() != null && !logic) {
            return Optional.empty();
        }
        Boolean yesOrNo = Boolean.parseBoolean(facility.getIndYesOrNo());
        if (facility.getIndYesOrNo() != null && !yesOrNo) {
            return Optional.empty();
        }
        if (facility.getNumber() != null && facility.getNumber().compareTo(BigInteger.ZERO) != 0) {
            description.append(":" + facility.getNumber());
        }
        if (facility.getDistance() != null && facility.getDistance().compareTo(BigInteger.ZERO) != 0) {
            description.append(", " + facility.getDistance().divide(BI_1000) + " kms");
        }
        if (facility.getAgeFrom() != null) {
            description.append(", age from " + facility.getAgeFrom());
        }
        if (facility.getAgeTo() != null) {
            description.append(", age to " + facility.getAgeTo());
        }
        if (facility.getTimeFrom() != null && !facility.getTimeFrom().equals("00:00:00")) {
            Date d = hh_mm_ss.parse(facility.getTimeFrom());
            description.append(" from " + hh_mm_P.format(d).toLowerCase());
        }
        if (facility.getTimeTo() != null && !facility.getTimeTo().equals("00:00:00")) {
            Date d = hh_mm_ss.parse(facility.getTimeTo());
            description.append(" to " + hh_mm_P.format(d).toLowerCase());
        }
        if (facility.getDateFrom() != null) {
            description.append(", from " + facility.getDateFrom());
        }
        if (facility.getDateTo() != null) {
            description.append(", to " + facility.getDateTo());
        }
        if (facility.getAmount() != null) {
            if (facility.getCurrency() != null) {
                description.append(", " + facility.getCurrency() + " " + facility.getAmount());
            } else {
                description.append(", $" + facility.getAmount());
            }
        }
        if (facility.getIndFee() != null) {
            Boolean fee = Boolean.parseBoolean(facility.getIndFee());
            if (fee)
                description.append(" (Paid)");
        }

        return Optional.of(description.toString());
    }

    private Object unMarshal(String response) throws Exception {
        if (jaxbContext == null)
            jaxbContext = JAXBContext.newInstance("com.hotelbeds.schemas.messages");
        if (unmarshaller == null)
            unmarshaller = jaxbContext.createUnmarshaller();
        ByteArrayInputStream bin = new ByteArrayInputStream(response.getBytes());
        Object responseObject = unmarshaller.unmarshal(bin);
        return responseObject;
    }

    //    private Object unMarshal(String response, String className) throws Exception {
//        switch (className) {
//            case "HtSearchRq":
//                jaxbContext = JAXBContext.newInstance(HtSearchRq.class);
//                break;
//            case "HTDetail":
//                jaxbContext = JAXBContext.newInstance(HTDetail.class);
//                break;
//        }
//        unmarshaller = jaxbContext.createUnmarshaller();
//        ByteArrayInputStream bin = new ByteArrayInputStream(response.getBytes());
//        Object responseObject = unmarshaller.unmarshal(bin);
//        return responseObject;
//    }
    private Object unMarshal(String response, Unmarshaller unm) throws Exception {
        ByteArrayInputStream bin = new ByteArrayInputStream(response.getBytes());
        Object responseObject = unm.unmarshal(bin);
        return responseObject;
    }

    private BigDecimal makeRating(String categoryCode) {
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

    private AccommodationRC.AccommodationTypeTag makeAccommodationType(String accommodationType) {
        switch (accommodationType) {
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

    private AccommodationRC.ImageTag makeImageTag(String imageCode) {
        switch (imageCode) {
            case "HAB":
                return AccommodationRC.ImageTag.ROOM;
        }
        return AccommodationRC.ImageTag.GENERAL;
    }
}
