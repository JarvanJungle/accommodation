package com.torkirion.eroam.microservice.cruise.endpoint.traveltek;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;

import com.torkirion.eroam.microservice.cruise.apidomain.*;
import com.torkirion.eroam.microservice.cruise.apidomain.Deck;
import com.torkirion.eroam.microservice.cruise.apidomain.Facility;
import com.torkirion.eroam.microservice.cruise.apidomain.Image;
import com.torkirion.eroam.microservice.cruise.apidomain.Ship;
import com.torkirion.eroam.microservice.cruise.dto.DetailRQDTO;
import com.torkirion.eroam.microservice.cruise.dto.SearchRQDTO;
import com.torkirion.eroam.microservice.cruise.endpoint.*;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data.*;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import com.traveltek.schemas.messages.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class TravelTekService implements CruiseServiceIF
{
	private final SystemPropertiesDAO propertiesDAO;

	private final SidDataRepo sidDataRepo;

	private final ShipDataRepo shipDataRepo;

	private final CruiseDataRepo cruiseDataRepo;

	private final PortDataRepo portDataRepo;

	private final RegionDataRepo regionDataRepo;

	private TravelTekRCController travelTekRCController;

	private final CruiseLineDataRepo cruiseLineDataRepo;

	public static final String CHANNEL = "TRAVELTEK";

	public static final String SITE_DEFAULT = "eroam";

	public static final String CHANNEL_PREFIX = "TT";

	private static final String RESULT_KEY_DEFAULT = "default";

	@Override
	public List<CruiseResult> searchByDestination(SearchRQDTO availSearchRQ)
	{
        log.debug("searchByDestination::enter for " + availSearchRQ);
		try
		{
			var sidProperties = sidDataRepo.findSidDataByCountryCode(availSearchRQ.getCountryCodeOfOrigin().toLowerCase());
			if (sidProperties == null)
				return Collections.emptyList();
			var travelTekInterface = new TravelTekInterface(propertiesDAO, sidProperties, availSearchRQ.getClient(), availSearchRQ.getChannel());

			String sessionKey = travelTekInterface.createSession();

			var searchDetail = mapSearchCruise(availSearchRQ);
			searchDetail.setSid(BigInteger.valueOf(sidProperties.getSid()));
			var method = new Method();
			method.setSessionkey(sessionKey);
			method.setSearchdetail(searchDetail);
			travelTekInterface.createSearch(method);

			method = new Method();
			method.setSessionkey(sessionKey);
			method.setResultkey(RESULT_KEY_DEFAULT);
			travelTekInterface.performSearch(method);

			method = new Method();
			method.setType("cruise");
			method.setResultkey(RESULT_KEY_DEFAULT);
			method.setSessionkey(sessionKey);
			var results = travelTekInterface.getResults(method);
			if (results != null)
				return mapResult(results.getCruise());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public CruiseResult detailCruise(DetailRQDTO detailRQDTO) throws Exception
	{
        log.debug("detailCruise::enter for " + detailRQDTO);
		var cruiseData = cruiseDataRepo.findFirstByCodeToCruiseIdAndAndCountry(detailRQDTO.getCruiseId(), detailRQDTO.getCountryCodeOfOrigin().toLowerCase());
		if (cruiseData == null)
		{
	        log.debug("detailCruise::cruiseData was null");
			return null;
		}
		var cruise = cruiseData.getCruise();
		var cruiseResult = mapCruiseResult(cruise);
		if (cruise != null)
		{
			Optional<ShipData> shipDataOptional = shipDataRepo.findById(cruise.getShipid().intValue());
	        log.debug("detailCruise::shipDataOptional present is " + shipDataOptional.isPresent());
			shipDataOptional.ifPresent(shipData -> cruiseResult.setShip(mapShip(shipData.getShip())));
		}
		else
		{
	        log.debug("detailCruise::cruise was null");
		}
		return cruiseResult;
	}

	@Override
	public List<CruiseLine> availCruiseLines(String client)
	{
		List<CruiseLineData> cruiseLineDataList = cruiseLineDataRepo.findAll();
		List<CruiseLine> cruiseLineList = new ArrayList<>();
		for (var cruise : cruiseLineDataList)
		{
			var cruiseLine = new CruiseLine();
			cruiseLine.setCruiseLineId(cruise.getId());
			cruiseLine.setCruiselineName(cruise.getName());
			cruiseLine.setCruiselineCode(cruise.getCode());
			var image = new Image();
			image.setImageURL(cruise.getLogoUrl());
			cruiseLine.setImageThumbnail(image);
			cruiseLineList.add(cruiseLine);
		}
		return cruiseLineList;
	}

	@Override
	public List<String> availDestinations(String client)
	{
		List<PortData> portDataList = portDataRepo.findAll();
		return portDataList.stream().map(PortData::getName).collect(Collectors.toList());
	}

	@Override
	public List<Location> availLocations(String client)
	{
		List<RegionData> regionDataList = regionDataRepo.findAll();
		List<Location> locationList = new ArrayList<>();
		for (var region : regionDataList)
		{
			var location = new Location();
			location.setLocationId(region.getId());
			location.setLocationName(region.getName());
			locationList.add(location);
		}
		return locationList;
	}

	private Searchdetail mapSearchCruise(SearchRQDTO searchRQDTO)
	{
		var searchDetail = new Searchdetail();
		searchDetail.setType("cruise");
		searchDetail.setWithpricesonly(BigInteger.ONE);
		searchDetail.setResultkey(RESULT_KEY_DEFAULT);
		var startDate = searchRQDTO.getDepartureMonth();
		searchDetail.setStartdate(String.format("%s-%s-%s", startDate.getYear(), startDate.format(DateTimeFormatter.ofPattern("MM")), "01"));
		var endDate = searchRQDTO.getDepartureMonth();
		searchDetail.setEnddate(String.format("%s-%s-%s", endDate.getYear(), endDate.format(DateTimeFormatter.ofPattern("MM")), endDate.lengthOfMonth()));
		searchDetail.setNights(BigInteger.valueOf(searchRQDTO.getDurationNights()));
		searchDetail.setLineid(searchRQDTO.getCruiseLine());
		searchDetail.setPort(searchRQDTO.getDestination());
		searchDetail.setRegionid(searchRQDTO.getLocationId());
		List<Child> children = new ArrayList<>();
		for (TravellerMix travellerMix : searchRQDTO.getTravellers())
		{
			travellerMix.getChildAges().forEach(integer -> {
				var child = new Child();
				child.setAge(BigInteger.valueOf(integer));
				children.add(child);
			});
			searchDetail.setAdults(BigInteger.valueOf(travellerMix.getAdultCount()));
			searchDetail.setChildren(BigInteger.valueOf(children.size()));
			searchDetail.getChild().addAll(children);
			children.clear();
		}
		return searchDetail;
	}

	private List<CruiseResult> mapResult(List<Cruise> cruises)
	{
		List<CruiseResult> results = new ArrayList<>();
		for (Cruise cruise : cruises)
		{
			var cruiseResult = mapCruiseResult(cruise);
			results.add(cruiseResult);
		}
		return results;
	}

	private CruiseResult mapCruiseResult(Cruise cruise)
	{
        log.debug("mapCruiseResult::enter");
		if (cruise == null)
		{
	        log.debug("mapCruiseResult::cruise is null");
			return null;
		}
		var cruiseResult = new CruiseResult();
		cruiseResult.setCruiseId(cruise.getCodetocruiseid().intValue());
		cruiseResult.setDurationNights(cruise.getNights().intValue());
		cruiseResult.setCruiseTitle(cruise.getName());
		cruiseResult.setCruiseLine(mapLine(cruise.getLine()));
		cruiseResult.setShip(mapShip(cruise.getShip()));
		cruiseResult.setPortsOfCall(mapCruisePort(cruise.getPorts()));
		cruiseResult.setDepartures(mapCruiseDepartures(cruise.getSailings()));
		cruiseResult.setItineraries(mapItineraries(cruise.getItinerary()));
		cruiseResult.setStartDate(LocalDate.parse(cruise.getStartdate()));
		cruiseResult.setPrice(cruise.getPrice());
		if (cruise.getImages() != null)
		{
			for (var imageMap : cruise.getImages().getImage())
			{
				if (imageMap.getType().equals("map"))
				{
					var map = new Image();
					map.setImageURL(imageMap.getImageurl());
					map.setImageDescription(imageMap.getCaption());
					cruiseResult.setItineraryMap(map);
				}
			}
		}
		return cruiseResult;
	}

	private CruiseLine mapLine(Line line)
	{
		if (line == null)
			return null;
		var cruiseLine = new CruiseLine();
		cruiseLine.setCruiselineCode(line.getCode());
		cruiseLine.setCruiselineName(line.getName());
		var image = new Image();
		image.setImageURL(line.getLogourl());
		cruiseLine.setImageThumbnail(image);
		return cruiseLine;
	}

	private List<CruisePort> mapCruisePort(Ports ports)
	{
		if (ports == null)
			return Collections.emptyList();
		List<CruisePort> cruisePorts = new LinkedList<>();
		for (var port : ports.getPort())
		{
			var cruisePort = new CruisePort();
			cruisePort.setId(port.getId().toString());
			cruisePort.setLocationName(port.getName());
			cruisePorts.add(cruisePort);
		}
		return cruisePorts;
	}

	private List<ItineraryItem> mapItineraries(Itinerary itinerary)
	{
		if (itinerary == null)
			return Collections.emptyList();
		List<ItineraryItem> itineraryItems = new LinkedList<>();
		for (var item : itinerary.getItem())
		{
			var itineraryItem = new ItineraryItem();
			itineraryItem.setDayNumber(item.getDay().intValue());
			itineraryItem.setName(item.getName());
			itineraryItem.setDepartTime(LocalTime.parse(item.getDeparttime()));
			itineraryItem.setDepartDate(LocalDate.parse(item.getDepartdate()));
			itineraryItems.add(itineraryItem);
		}
		return itineraryItems;
	}

	private List<CruiseDeparture> mapCruiseDepartures(Sailings sailings)
	{
		if (sailings == null)
			return Collections.emptyList();
		List<CruiseDeparture> cruiseDepartures = new LinkedList<>();
		for (var sailing : sailings.getSailing())
		{
			var cruiseDeparture = new CruiseDeparture();
			cruiseDeparture.setDepartureDate(LocalDate.parse(sailing.getStartdate()));
			cruiseDeparture.setDeparturePort(sailing.getShipname());
			var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
			cruiseDeparture.setFinishDate(LocalDate.parse(sailing.getEnddate(), formatter));
			cruiseDepartures.add(cruiseDeparture);
		}
		return cruiseDepartures;
	}

	private Ship mapShip(com.traveltek.schemas.messages.Ship ship)
	{
		if (ship == null)
			return null;
		var shipResult = new Ship();
		shipResult.setShipId(ship.getId().intValue());
		shipResult.setShipName(ship.getName());
		shipResult.setShipCode(ship.getCode());
		if (ship.getImageurl() != null)
		{
			var image = new Image();
			image.setImageURL(ship.getImageurl());
			image.setImageDescription(ship.getImagecaption());
			shipResult.setImageThumbnail(image);
		}
		shipResult.setDecks(mapDeck(ship.getDecks()));
		shipResult.setCabinTypes(mapCabinType(ship.getCabintypes()));
		shipResult.setImages(mapImageShip(ship.getImages()));
		shipResult.setFacilities(mapFacilities(ship.getFacilities()));
		return shipResult;
	}

	private SortedSet<Image> mapImageShip(Images images)
	{
		if (images == null || images.getImage().isEmpty())
			return new TreeSet<>();
		SortedSet<Image> imagesResult = new TreeSet<>();
		for (var image : images.getImage())
		{
			var imageResult = new Image();
			imageResult.setImageURL(image.getImageurl());
			imageResult.setImageDescription(image.getCaption());
			imagesResult.add(imageResult);
		}
		return imagesResult;
	}

	private List<Deck> mapDeck(Decks decks)
	{
		if (decks == null || decks.getDeck().isEmpty())
			return Collections.emptyList();
		List<Deck> decksResult = new LinkedList<>();
		for (var deck : decks.getDeck())
		{
			var deckResult = new Deck();
			deckResult.setId(deck.getId().intValue());
			deckResult.setCaption(deck.getCaption());
			deckResult.setDescription(deck.getDescription());
			deckResult.setImageId(deck.getImageid());
			deckResult.setImageUrl(deck.getImageurl());
			deckResult.setName(deck.getName());
			decksResult.add(deckResult);
		}
		return decksResult;
	}

	private List<CabinType> mapCabinType(Cabintypes cabintypes)
	{
		if (cabintypes == null || cabintypes.getCabintype().isEmpty())
			return Collections.emptyList();
		List<CabinType> cabinTypesResult = new LinkedList<>();
		for (var cabinType : cabintypes.getCabintype())
		{
			var cabinTypeResult = new CabinType();
			cabinTypeResult.setId(cabinType.getId().intValue());
			cabinTypeResult.setCabinCode(cabinType.getCabincode());
			cabinTypeResult.setType(cabinType.getCabintype());
			cabinTypeResult.setCaption(cabinType.getCaption());
			cabinTypeResult.setColourCode(cabinType.getColourcode());
			cabinTypeResult.setDescription(cabinType.getDescription());
			cabinTypeResult.setImageUrl(cabinType.getImageurl());
			cabinTypeResult.setOriginalImageUrl(cabinType.getOriginalimageurl());
			cabinTypeResult.setSmallImageUrl(cabinType.getSmallimageurl());
			cabinTypeResult.setName(cabinType.getName());
			cabinTypeResult.setIsDefault(cabinType.getIsdefault());
			cabinTypeResult.setSortWeight(cabinType.getSortweight().floatValue());
			cabinTypesResult.add(cabinTypeResult);
		}
		return cabinTypesResult;
	}

	private List<Facility> mapFacilities(Facilities facilities)
	{
		if (facilities == null || facilities.getFacility().isEmpty())
			return Collections.emptyList();
		List<Facility> facilitiesResult = new LinkedList<>();
		for (var facility : facilities.getFacility())
		{
			var facilityResult = new Facility();
			facilityResult.setCategoryName(facility.getCategory());
			List<String> itemsName = new LinkedList<>();
			for (var item : facility.getItem())
			{
				itemsName.add(item.getName());
			}
			facilityResult.setFacilityList(itemsName);
			facilitiesResult.add(facilityResult);
		}
		return facilitiesResult;
	}

	public static ChannelType getSystemPropertiesDescription()
	{
		var channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("The default source market of the customers", "sourceMarket", FieldType.STRING, false, "GB"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("URL endpoint of TravelTek API", "url", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The TravelTek APIKey", "apikey", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}

	@Override
	@Async
	public void initiateRCLoad(String code)
	{
		try
		{
			if ( code != null && code.length() > 0)
				travelTekRCController.process(code);
			else
				travelTekRCController.process(null);
		}
		catch (Exception e)
		{
			log.warn("initiateRCLoad::caught " + e.toString(), e);
		}
	}

}
