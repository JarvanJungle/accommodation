package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class InnstantRCLoader
{
    @Autowired
    private AccommodationRCService accommodationRCService;

    @Autowired
    private LoadRepoInnstant loadRepo;

    @Autowired
    private CountryStaticDataRepo countryStaticDataRepo;

    @Autowired
    private DestinationsStaticDataRepo destinationsStaticDataRepo;

    @Autowired
    private ObjectMapper objectMapper;

    public static final int LIMITED_ID_SEARCH = 500;

    @Transactional
    public void loadCountry(HttpService httpService) throws Exception
    {
        log.debug("loadCountry::entering with loadCountry ");
        String url = "countries";
        String response = httpService.doCallGet(url, null);
        List<CountryStaticDTO> countryList = objectMapper.readValue(response, new TypeReference<List<CountryStaticDTO>>() {});
        log.info("Size country by name: {}", countryList.size());
        CompletableFuture<Void> futureCountries = null;
        for (CountryStaticDTO countryStaticDTO : countryList) {
            futureCountries = CompletableFuture.runAsync(()->{
                try {
                    countryStaticDataRepo.save(new CountryStaticData(countryStaticDTO));

                    String urlDestination = "destinations/" + countryStaticDTO.getId();
                    String responseDes = httpService.doCallGet(urlDestination, null);
                    List<DestinationsStaticData> destinationsStaticData = objectMapper.readValue(responseDes, new TypeReference<List<DestinationsStaticData>>() {});
                    log.info("Size responseDes by name: {}", destinationsStaticData.size());
                    if (destinationsStaticData != null){
                        destinationsStaticDataRepo.saveAll(destinationsStaticData);
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });

        }
        CompletableFuture<Void> combinedFuture = null;
        if (futureCountries != null) {
            combinedFuture = CompletableFuture.allOf(futureCountries);
        }
        if(combinedFuture != null )	combinedFuture.get();
    }
    
    @Transactional
    public AccommodationRC loadHotelDB(HttpService httpService ,String hotelId) throws Exception
    {
        log.debug("loadHotelDB::enter for " + hotelId);
        String urlHotel = "hotels/" + hotelId ;
        String responseHotel = httpService.doCallGet(urlHotel, null);

        AccommodationRC accommodationRC = new AccommodationRC();
        InnstantRCHotelsStatic hotelStatic = new ObjectMapper().readValue(responseHotel, new TypeReference<List<InnstantRCHotelsStatic>>() {}).get(0);

        makeRCCore(accommodationRC, hotelStatic, hotelId);
        makeRCImages(accommodationRC, hotelStatic);
        makeRCFacilities(accommodationRC, hotelStatic);

        accommodationRC.setLastUpdate(LocalDate.now());

        accommodationRCService.saveAccommodationRC(accommodationRC);
        return accommodationRC;
    }
    
    @Transactional
    public void loadHotels(HttpService httpService, int hotelFromLoop) throws Exception
    {
        log.debug("loadHotels::entering with hotelFromLoop " + hotelFromLoop);

        LocalDate now = LocalDate.now();
        LoadDataInnstant loadData = new LoadDataInnstant();
        Optional<LoadDataInnstant> loadDataOpt = loadRepo.findById(0);
        if (loadDataOpt.isPresent())
        {
            loadData = loadDataOpt.get();
        }
        LocalDate lastUpdateDate = loadData.getLastHotelUpdate();
        log.debug("loadHotels::lastUpdateDate=" + lastUpdateDate);
        if (lastUpdateDate == null)
            lastUpdateDate = now.minusYears(2);

        int insertCount = 0;
        int loadCount = 0;
        int fetchCount = 0;

        boolean loop = true;
        boolean reloop = false;
        while (loop)
        {
            log.debug("loadHotels::test fetchCount=" + fetchCount);
            if (lastUpdateDate.isAfter(now))
            {
                log.debug("loadHotels::break out of loop ");
                reloop = false;
                break;
            }
            fetchCount++;
            try
            {
                String url = "hotels-diff/" + yyyy_mm_ddDF.format(lastUpdateDate);
                String response = httpService.doCallGet(url, null);
                List<Integer> hotelList = new ObjectMapper().readValue(response, List.class);
                log.info("loadHotels::size=" + hotelList.size());
                if (hotelList.size() == 0) {
                    log.debug("loadHotels::insert a new hotel");
                }
                else {
                    List<InnstantRCHotelsStatic> hotelsStatics = getHotelDataString(hotelList, httpService);
                    CompletableFuture<Void> futureHotels = null;
                    for (InnstantRCHotelsStatic hotelsStatic : hotelsStatics){
                        log.debug("loadHotels::loadCount=" + loadCount);

                        int finalLoadCount = loadCount;
                        insertDataRC(hotelsStatic, finalLoadCount);
                        loadCount++;
                    }
                    //CompletableFuture<Void> combinedFuture = null;
                    //if (futureHotels != null) {
                    //    combinedFuture = CompletableFuture.allOf(futureHotels);
                    //}
                    //if(combinedFuture != null )	combinedFuture.get();
                }
            }
            catch (Exception e)
            {
                log.debug("loadHotels::caught exception " + e);
            }
            log.debug("loadHotels::looping");
            lastUpdateDate = lastUpdateDate.plusDays(1);
            Functions.logMemAndYield();
        }
        log.debug("loadHotels::end loop");
        if (!reloop)
        {
            loadData.setLastHotelUpdate(now);
            loadRepo.save(loadData);
        }
        log.info("loadHotels::loaded " + loadCount + ", added new " + insertCount);
        if (reloop)
        {
            log.debug("loadHotels::looping out and back to allow transactions");
            //return Optional.of(hotelFromLoop);
            return;
        }
    }
    @Transactional
    public void insertDataRC(InnstantRCHotelsStatic hotel, int index) throws Exception{
        log.debug("loadHotels::hotel {} - index: {}", hotel.getId(), index);

        try
        {
	        String hotelId = String.valueOf(hotel.getId());
	        String hotelCode = InnstantService.CHANNEL_PREFIX + hotelId;
	        Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(hotelCode);
	        AccommodationRC accommodationRC;
	        if (accommodationRCOpt.isPresent())
	        {
	            accommodationRC = accommodationRCOpt.get();
	            log.debug("loadHotels::update hotel");
	        }
	        else
	        {
	            accommodationRC = new AccommodationRC();
	            log.debug("loadHotels::insert a new hotel");
	        }
	
	        makeRCCore(accommodationRC, hotel, hotelId);
	//      makeRCErrata(accommodationRC, hotel);
	        makeRCImages(accommodationRC, hotel);
	        makeRCFacilities(accommodationRC, hotel);
	
	        accommodationRC.setLastUpdate(LocalDate.now());
	
	        // TODO rooms
	        // Map<String, String> wildCardMap = loadWildcards(conn, hotelBedsInterface, hotel.getWildcards(),
	        // hotelCode);
	        // loadRooms(conn, hotelBedsInterface, hotel.getRooms(), hotelCode, wildCardMap);
	
	        accommodationRCService.saveAccommodationRC(accommodationRC);
        }
        catch ( Exception e )
        {
            log.warn("loadHotels::caught exception " + e.toString() + " for hotel " + hotel.getId(), e);
        }
    }

    protected void makeRCFacilities(AccommodationRC accommodationRC, InnstantRCHotelsStatic hotel)
    {
        log.debug("makeRCFacilities::enter");
        // If a hotel doesnt specify, we take this as reasonable defaults
        String checkin = "2:00pm";
        String checkout = "11:00am";
        accommodationRC.getFacilityGroups().clear();
        if (hotel.getFacilities() != null)
        {
            Facilities facility = hotel.getFacilities();
            AccommodationRC.FacilityGroup facilityGroup;
            if (facility.getTags() != null) {
                facilityGroup = new AccommodationRC.FacilityGroup();
                facilityGroup.setGroupName("tags");
                facilityGroup.setFacilities(facility.getTags());
                accommodationRC.getFacilityGroups().add(facilityGroup);
            } else if (facility.getList() != null) {
                facilityGroup = new AccommodationRC.FacilityGroup();
                facilityGroup.setGroupName("list");
                facilityGroup.setFacilities(facility.getList());
                accommodationRC.getFacilityGroups().add(facilityGroup);
            }

        }
        accommodationRC.setCheckinTime(checkin);
        accommodationRC.setCheckoutTime(checkout);
    }

    private static final String imageURLPrefix = "https://cdn-images.innstant-servers.com/%sx%s/%s";

    protected void makeRCImages(AccommodationRC accommodationRC, InnstantRCHotelsStatic hotel)
    {
        log.debug("makeRCImages::enter");
        accommodationRC.getImages().clear();
        if (hotel.getImages() != null)
        {
            log.debug("loadHotels::hotel has " + hotel.getImages().size() + " images");
            int index = 0;
            AccommodationRC.Image rcThumbnailImage = new AccommodationRC.Image();
            for (Image image : hotel.getImages())
            {
                String imageURL = String.format(imageURLPrefix, image.getWidth(), image.getHeight(), image.getUrl());
                log.debug("loadHotels::setting image to " + imageURL + " /" + image.getTitle());
                AccommodationRC.Image rcimage = new AccommodationRC.Image();
                rcimage.setImageOrder(index);
                rcimage.setImageURL(imageURL);
                rcimage.setImageDescription(image.getTitle());
                rcimage.setImageTag(AccommodationRC.ImageTag.HOTEL);
                rcimage.setTagCode("");
                accommodationRC.getImages().add(rcimage);
                index++;
                if ( rcThumbnailImage == null || image.getId() == hotel.getMainImageId())
                {
                    rcThumbnailImage = new AccommodationRC.Image();
                    rcThumbnailImage.setImageOrder(0);
                    rcThumbnailImage.setImageURL(imageURL);
                    rcThumbnailImage.setImageDescription(image.getTitle());
                    rcThumbnailImage.setImageTag(AccommodationRC.ImageTag.GENERAL);
                    rcThumbnailImage.setTagCode("");
                    accommodationRC.setImageThumbnail(rcThumbnailImage);
                }
            }
        }
        else
        {
            log.debug("loadHotels::hotel has no images");
        }
    }

    protected void makeRCCore(AccommodationRC accommodationRC, InnstantRCHotelsStatic hotel, String hotelId)
    {
        log.debug("makeRCCore::enter");
        accommodationRC.setCode(InnstantService.CHANNEL_PREFIX + hotelId);
        accommodationRC.setChannel(InnstantService.CHANNEL);
        accommodationRC.setChannelCode(hotelId);
        accommodationRC.setAccommodationName(hotel.getName());

        accommodationRC.setAddress(new AccommodationRC.Address());
        if (hotel.getStars() != null) {
            accommodationRC.setRating(new BigDecimal(hotel.getStars()));

            log.debug("loadHotels::did not find rating name for category code " + hotel.getName());
            String stars = hotel.getStars() + " stars";
            accommodationRC.setRatingText(stars);
        }
//        accommodationRC.setInternalDestinationCode(hotel.get());
        accommodationRC.setProductType(AccommodationRC.AccommodationTypeTag.HOTEL);
        accommodationRC.setIntroduction(hotel.getDescription()!=null ? hotel.getDescription():"");
        accommodationRC.setDescription(hotel.getDescription()!=null ? hotel.getDescription():"");
        accommodationRC.setChain(null);
        accommodationRC.setCategory(null);
        accommodationRC.setLastUpdate(LocalDate.now());
        accommodationRC.getAddress().setStreet(hotel.getAddress());
        accommodationRC.getAddress().setPostcode(hotel.getZip());
        accommodationRC.getAddress().setState("");
        accommodationRC.setPhone(hotel.getPhone());

        if(hotel.getDestinations().size()>0){
            Optional<DestinationsStaticData> optDes =  destinationsStaticDataRepo.findById(hotel.getDestinations().get(0).getDestinationId().toString());
            if (optDes.isPresent()){
                accommodationRC.getAddress().setCountryCode(optDes.get().getCountryid());
                accommodationRC.getAddress().setCity(optDes.get().getName());
            }
            else {
                log.warn("loadHotels::not found destination for " + accommodationRC.getCode());
            }
        }
        else{
            log.warn("loadHotels::found manual destination " + accommodationRC.getCode());

            List<DestinationsStaticData> desStaticList = destinationsStaticDataRepo.findAll();
            int indexNotNull = 0;
            while (desStaticList.get(indexNotNull).getLon() == null || desStaticList.get(indexNotNull).getLat() == null) indexNotNull++;
            BigDecimal min =  hotel.getLat().subtract(desStaticList.get(indexNotNull).getLat()).abs().add(hotel.getLon().subtract(desStaticList.get(indexNotNull).getLon().abs()));
            String countryCode = desStaticList.get(indexNotNull).getCountryid(), city = desStaticList.get(indexNotNull).getName();
            for (DestinationsStaticData desStatic : desStaticList){
                if(desStatic.getLat() != null && desStatic.getLon() != null ){
                    BigDecimal min2 = hotel.getLat().subtract(desStatic.getLat()).abs().add(hotel.getLon().subtract(desStatic.getLon()).abs());
                    if(min.compareTo(min2) > 0){
                        countryCode = desStatic.getCountryid();
                        city = desStatic.getName();
                        min = min2;
                    }
                }
                else
                    continue;
            }
            accommodationRC.getAddress().setCountryCode(countryCode);
            accommodationRC.getAddress().setCity(city);
        }

        if (hotel.getLat() != null && hotel.getLon() != null)
        {
            accommodationRC.getAddress().setGeoCoordinates(new AccommodationRC.GeoCoordinates());
            accommodationRC.getAddress().getGeoCoordinates().setLatitude(hotel.getLat());
            accommodationRC.getAddress().getGeoCoordinates().setLongitude(hotel.getLon());
        }
        else
        {
            log.warn("loadHotels::null lat/long loaded for " + accommodationRC.getCode());
        }

        accommodationRC.setOleryCompanyCode(0L);
    }

    private static DateTimeFormatter yyyy_mm_ddDF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private List<InnstantRCHotelsStatic> getHotelDataString(List<Integer> hotelIds, HttpService httpService) throws JsonProcessingException {
        log.debug("getHotelDataString::enter for " + hotelIds);
        int batch = 1;
        int count = 0;
        int page = LIMITED_ID_SEARCH;
        List<InnstantRCHotelsStatic> listRS = new ArrayList<>();
        while (count < hotelIds.size()){
            int startIndex = (batch - 1)* page;
            int lastIndex = batch * page;
            if (lastIndex >= hotelIds.size()) {
                lastIndex = hotelIds.size();
            }
            log.info("start {}", startIndex);
            log.info("lastIndex {}", lastIndex);
            List<Integer> dataSub = hotelIds.subList(startIndex, lastIndex);
            String urlHotel = "hotels/" + dataSub.stream().map(item -> item.toString()).collect(Collectors.joining(","));
            String responseHotel = httpService.doCallGet(urlHotel, null);
            List<InnstantRCHotelsStatic> hotelsStatics = new ObjectMapper().readValue(responseHotel, new TypeReference<List<InnstantRCHotelsStatic>>() {});
            listRS.addAll(hotelsStatics);

            count = lastIndex;
            batch++;
        }

        return listRS;
    };
}
