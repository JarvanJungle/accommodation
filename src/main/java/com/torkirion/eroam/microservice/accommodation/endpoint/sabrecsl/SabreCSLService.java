package com.torkirion.eroam.microservice.accommodation.endpoint.sabrecsl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.schema.hotel.avail.v4_0_0.*;
import com.sabre.schema.hotel.avail.v4_0_0.CancelPenaltiesType.CancelPenalty;
import com.sabre.schema.hotel.content.v4_0_0.Amenity;
import com.sabre.schema.hotel.content.v4_0_0.GetHotelContentRS;
import com.sabre.schema.hotel.content.v4_0_0.HotelContentInfos;
import com.sabre.schema.hotel.content.v4_0_0.Image;
import com.sabre.schema.hotel.content.v4_0_0.MediaItem;
import com.sabre.schema.hotel.content.v4_0_0.Policy;
import com.sabre.schema.hotel.content.v4_0_0.PolicyType;
import com.sabre.schema.hotel.details.v3_0_0.GetHotelDetailsRS;
import com.sabre.schema.hotel.details.v3_0_0.RoomSet;
import com.sabre.schema.hotel.pricecheck.v4_0_0.AdditionalDetail;
import com.sabre.schema.hotel.pricecheck.v4_0_0.HotelPriceCheckRS;

import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsInterface;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@AllArgsConstructor
@Slf4j
public class SabreCSLService implements AccommodationServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private AccommodationRCService accommodationRCService;

	public static final String CHANNEL = "SABRECSL";

	public static final String CHANNEL_PREFIX = "SC";

	@Override
	public List<AccommodationResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		GetHotelAvailRS availabilityRS;
		List<AccommodationResult> results = new ArrayList<>();

		try
		{
			SaberCSLInterface saberCSLInterface = new SaberCSLInterface(propertiesDAO, availSearchRQ.getClient(), CHANNEL);
			long timer2 = System.currentTimeMillis();
			availabilityRS = saberCSLInterface.startSearchHotels(availSearchRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("search::time in hotelbeds search was " + totalTime2 + " millis");
			if (availabilityRS == null || availabilityRS.getHotelAvailInfos() == null || availabilityRS.getHotelAvailInfos().getHotelAvailInfos().size() == 0)
			{
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned no lists");
			}
			else
			{
				int listNo = 0;
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned " + availabilityRS.getHotelAvailInfos().getHotelAvailInfos().size() + " hotels");
				for (HotelAvailInfo hotelResponse : availabilityRS.getHotelAvailInfos().getHotelAvailInfos())
				{
					if (log.isDebugEnabled())
						log.debug("search::processing item " + listNo);

					Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(CHANNEL_PREFIX + hotelResponse.getHotelInfo().getHotelCode());
					AccommodationRC accommodationRC;
					if (accommodationRCOpt.isPresent())
					{
						accommodationRC = accommodationRCOpt.get();
					}
					else
					{
						GetHotelContentRS hotelContentRS = saberCSLInterface.getHotelContent(hotelResponse.getHotelInfo().getHotelCode());
						accommodationRC = mappingAccommodationResult(hotelContentRS.getHotelContentInfos());
						accommodationRCService.saveAccommodationRC(accommodationRC);
					}

					AccommodationResult accommodationResult = new AccommodationResult();

					accommodationResult.setProperty(new AccommodationProperty());
					BeanUtils.copyProperties(accommodationRC, accommodationResult.getProperty());
					if (accommodationRC.getImageThumbnail() != null)
						accommodationResult.getProperty().setImageThumbnailUrl(accommodationRC.getImageThumbnail().getImageURL());

					if (log.isDebugEnabled())
						log.debug("makeResult::returning result for " + accommodationResult.getProperty().getCode() + " with " + accommodationResult.getRooms().size() + " rooms");

					accommodationResult.setRooms(loadRoomsA4(hotelResponse.getHotelRateInfo().getRooms(), hotelResponse.getHotelInfo().getHotelCode()));
					results.add(accommodationResult);
					if (accommodationResult.getRooms() == null || accommodationResult.getRooms().size() == 0)
					{
						if (log.isDebugEnabled())
							log.debug("search::hotel " + accommodationRC.getCode() + " not added, has no rooms");
					}
					else
					{
						results.add(accommodationResult);
					}
				}
			}
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	private AccommodationRC mappingAccommodationResult(HotelContentInfos hotelContentInfos)
	{
		if (log.isDebugEnabled())
			log.debug("mappingAccommodationResult::enter");
		AccommodationRC accommodationRC = new AccommodationRC();
		accommodationRC.setCode(CHANNEL_PREFIX + hotelContentInfos.getHotelContentInfo().getHotelInfo().getHotelCode());
		accommodationRC.setChannel(CHANNEL);
		accommodationRC.setChannelCode(hotelContentInfos.getHotelContentInfo().getHotelInfo().getHotelCode());
		accommodationRC.setAccommodationName(hotelContentInfos.getHotelContentInfo().getHotelInfo().getHotelName());
		accommodationRC.setProductType(AccommodationRC.AccommodationTypeTag.HOTEL);
//		accommodationRC.setDescription(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getDescriptions().ge);
		accommodationRC.setChain(hotelContentInfos.getHotelContentInfo().getHotelInfo().getChainName());

		accommodationRC.setRating(new BigDecimal(hotelContentInfos.getHotelContentInfo().getHotelInfo().getSabreRating()));
		accommodationRC.setRatingText("");
		if (hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getPropertyInfo().getPolicies() != null){
			for (Policy policy:hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getPropertyInfo().getPolicies().getPolicies()){
				if (policy.getText().getType().equals(PolicyType.CHECK_IN)){
					accommodationRC.setCheckinTime(policy.getText().getValue());
				} else if (policy.getText().getType().equals(PolicyType.CHECK_OUT)) {
					accommodationRC.setCheckoutTime(policy.getText().getValue());
				}
			}
		}

		accommodationRC.setPhone(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getContact().getPhone());
		accommodationRC.setLastUpdate(LocalDate.now());
		accommodationRC.setOleryCompanyCode(-1l);
		AccommodationRC.Address address = new AccommodationRC.Address();

		AccommodationRC.GeoCoordinates geoCoordinates = new AccommodationRC.GeoCoordinates();
		geoCoordinates.setLatitude(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getLatitude());
		geoCoordinates.setLongitude(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getLongitude());

		address.setCountryName(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getAddress().getCountryName().getValue());
		address.setCountryCode(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getAddress().getCountryName().getCode());
		address.setState("");
		address.setCity(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getAddress().getCityName().getValue());
		String addressLine2 = hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getAddress().getAddressLine2();
		address.setStreet(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getAddress().getAddressLine1() + (addressLine2 == null ? "" : " " + addressLine2));
		address.setPostcode(hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getLocationInfo().getAddress().getPostalCode());
		address.setFullFormAddress(address.getStreet() + ", " + address.getCity() + ", " + address.getPostcode() + ", " + address.getCountryName());
		address.setGeoCoordinates(geoCoordinates);
		accommodationRC.setAddress(address);
		accommodationRC.setFacilityGroups(convertListFacility(hotelContentInfos));
		accommodationRC.setImages(convertImages(hotelContentInfos));
		if ( accommodationRC.getImages().size() > 0 )
		{
			accommodationRC.setImageThumbnail(accommodationRC.getImages().first());
		}
		return accommodationRC;
	}

	private List<AccommodationRC.FacilityGroup> convertListFacility(HotelContentInfos hotelContentInfos){
		List<String> descList = new ArrayList<>();
		AccommodationRC.FacilityGroup facilityGroups = new AccommodationRC.FacilityGroup();
		for (Amenity amenities : hotelContentInfos.getHotelContentInfo().getHotelDescriptiveInfo().getAmenities().getAmenities()){
			descList.add(amenities.getDescription());
		}
		facilityGroups.setFacilities(descList);
		facilityGroups.setGroupName("Amenities");
		return Arrays.asList(facilityGroups);
	}

	private SortedSet<AccommodationRC.Image> convertImages(HotelContentInfos hotelContentInfos){
		SortedSet<AccommodationRC.Image> images = new TreeSet<>();
		int order = 0;
		if ( hotelContentInfos.getHotelContentInfo() != null && hotelContentInfos.getHotelContentInfo().getHotelMediaInfo() != null && hotelContentInfos.getHotelContentInfo().getHotelMediaInfo().getMediaItems() != null && hotelContentInfos.getHotelContentInfo().getHotelMediaInfo().getMediaItems().getMediaItems() != null)
		{
			for ( MediaItem mediaItem : hotelContentInfos.getHotelContentInfo().getHotelMediaInfo().getMediaItems().getMediaItems())
			{
				if ( mediaItem.getImageItems() != null && mediaItem.getImageItems().getImages() != null )
				{
					for (Image imageItem : mediaItem.getImageItems().getImages())
					{
						AccommodationRC.Image image = new AccommodationRC.Image();
						image.setImageURL(imageItem.getUrl());
						image.setImageOrder(order++);
						images.add(image);
					}
				}
			}
		}
		return images;
	}

	private SortedSet<RoomResult> loadRoomsA4(com.sabre.schema.hotel.avail.v4_0_0.Rooms roomRateDetailsList, String sabreHotelCode)
			throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("loadRooms(com.sabre.schema.hotel.avail.v4_0_0.Rooms)::loading rooms, roomRateDetailsList.size=" + (roomRateDetailsList == null ? 0 : roomRateDetailsList.getRooms().size()));

		if (roomRateDetailsList == null || roomRateDetailsList.getRooms().size() == 0)
		{
			return null;
		}

		long timer1 = System.currentTimeMillis();

		SortedSet<RoomResult> rooms = new TreeSet<>();

		Set<String> existingRoomCodes = new HashSet<String>();
		RoomResult fakeRoom = null;
		for (RoomType roomType : roomRateDetailsList.getRooms())
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::processing room " + roomType.getRoomID());
			if (log.isDebugEnabled())
				log.debug("loadRooms::loading rooms, roomType.getRatePlans().getRatePlen().size="
						+ (roomType.getRatePlans().getRatePlen() == null ? 0 : roomType.getRatePlans().getRatePlen().size()));
			rateLoop: for (RatePlanRef ratePlanRef : roomType.getRatePlans().getRatePlen())
			{
				if (log.isDebugEnabled())
					log.debug("loadRooms::processing rateKey '" + ratePlanRef.getRateKey() + "' with plan code " + ratePlanRef.getRatePlanCode());
				RoomResult room = makeRoomBaseA4(roomType, ratePlanRef, existingRoomCodes, roomType.getRoomIndex(), sabreHotelCode);
				if (room == null)
				{
					if (log.isDebugEnabled())
						log.debug("loadRooms::room from makeRoom is null");
					continue rateLoop;
				}
				fakeRoom = makeRoomBaseA4(roomType, ratePlanRef, null, roomType.getRoomIndex(), sabreHotelCode);
				fakeRoom.getSupplyRate().setAmount(fakeRoom.getSupplyRate().getAmount().add(BigDecimal.ONE));
				RoomExtraFee extraFee = new RoomExtraFee();
				extraFee.setDescription("Local charges (taxes and fees) if applicable will be displayed during checkout");
				extraFee.setFeeType(RoomExtraFee.FeeType.CheckinFees);
				room.getExtraFees().add(extraFee);

				if (log.isDebugEnabled())
					log.debug("loadRooms::adding room with rate :" + room.getSupplyRate() + " code " + room.getBookingCode());
				rooms.add(room);
			}
		}
		if (log.isDebugEnabled())
			log.debug("loadRooms::roomcount=" + rooms.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		if ( rooms.size() == 1)
		{
			// what is all this about?  In inventory search, Sabre returns just the first room.  If we only show thta, front end will not open the room list.  So we add a fake room
			// to make sure the front end shows the room selection list, which will call again and NOT insert fake rooms next call!
			if (log.isDebugEnabled())
				log.debug("loadRooms::adding fake room");
			rooms.add(fakeRoom);
		}
		return rooms;
	}

	private SortedSet<RoomResult> loadRoomsP4(com.sabre.schema.hotel.pricecheck.v4_0_0.Rooms roomRateDetailsList, String sabreHotelCode, String bookingCode)
			throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("loadRooms(com.sabre.schema.hotel.pricecheck.v4_0_0.Rooms)::loading rooms, roomRateDetailsList.size=" + (roomRateDetailsList == null ? 0 : roomRateDetailsList.getRooms().size()));

		if (roomRateDetailsList == null || roomRateDetailsList.getRooms().size() == 0)
		{
			return null;
		}

		long timer1 = System.currentTimeMillis();

		SortedSet<RoomResult> rooms = new TreeSet<>();

		Set<String> existingRoomCodes = new HashSet<String>();
		for (com.sabre.schema.hotel.pricecheck.v4_0_0.RoomType roomType : roomRateDetailsList.getRooms())
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::processing room " + roomType.getRoomID());
			if (log.isDebugEnabled())
				log.debug("loadRooms::loading rooms, roomType.getRatePlans().getRatePlen().size="
						+ (roomType.getRatePlans().getRatePlen() == null ? 0 : roomType.getRatePlans().getRatePlen().size()));
			rateLoop: for (com.sabre.schema.hotel.pricecheck.v4_0_0.RatePlanRef ratePlanRef : roomType.getRatePlans().getRatePlen())
			{
				if (log.isDebugEnabled())
					log.debug("loadRooms::processing rateKey '" + ratePlanRef.getRateKey() + "' with plan code " + ratePlanRef.getRatePlanCode());
				RoomResult room = makeRoomBasePC4(roomType, ratePlanRef, existingRoomCodes, roomType.getRoomIndex(), sabreHotelCode);
				if (room == null)
				{
					if (log.isDebugEnabled())
						log.debug("loadRooms::room from makeRoom is null");
					continue rateLoop;
				}
				room.setBookingCode(bookingCode);

				if (log.isDebugEnabled())
					log.debug("loadRooms::adding room with rate :" + room.getSupplyRate() + " code " + room.getBookingCode());
				rooms.add(room);
			}
		}
		if (log.isDebugEnabled())
			log.debug("loadRooms::roomcount=" + rooms.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return rooms;
	}

	private SortedSet<RoomResult> loadRoomsD3(List<com.sabre.schema.hotel.details.v3_0_0.RoomType> roomRateDetailsList, String sabreHotelCode)
			throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("loadRooms(List<com.sabre.schema.hotel.details.v3_0_0.RoomType>)::loading rooms, roomRateDetailsList.size=" + (roomRateDetailsList == null ? 0 : roomRateDetailsList.size()));

		if (roomRateDetailsList == null || roomRateDetailsList.size() == 0)
		{
			return null;
		}

		long timer1 = System.currentTimeMillis();

		SortedSet<RoomResult> rooms = new TreeSet<>();

		Set<String> existingRoomCodes = new HashSet<String>();
		for (com.sabre.schema.hotel.details.v3_0_0.RoomType roomType : roomRateDetailsList)
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::processing room " + roomType.getRoomID());
			if (log.isDebugEnabled())
				log.debug("loadRooms::loading rooms, roomType.getRatePlans().getRatePlen().size="
						+ (roomType.getRatePlans().getRatePlen() == null ? 0 : roomType.getRatePlans().getRatePlen().size()));
			rateLoop: for (com.sabre.schema.hotel.details.v3_0_0.RatePlanRef ratePlanRef : roomType.getRatePlans().getRatePlen())
			{
				if (log.isDebugEnabled())
					log.debug("loadRooms::processing rateKey '" + ratePlanRef.getRateKey() + "' with plan code " + ratePlanRef.getRatePlanCode());
				RoomResult room = makeRoomBaseD3(roomType, ratePlanRef, existingRoomCodes, roomType.getRoomIndex(), sabreHotelCode);
				if (room == null)
				{
					if (log.isDebugEnabled())
						log.debug("loadRooms::room from makeRoom is null");
					continue rateLoop;
				}
				RoomExtraFee extraFee = new RoomExtraFee();
				extraFee.setDescription("Local charges (taxes and fees) if applicable will be displayed during checkout");
				extraFee.setFeeType(RoomExtraFee.FeeType.CheckinFees);
				room.getExtraFees().add(extraFee);

				if (log.isDebugEnabled())
					log.debug("loadRooms::adding room with rate :" + room.getSupplyRate() + " code " + room.getBookingCode());
				rooms.add(room);
			}
		}
		if (log.isDebugEnabled())
			log.debug("loadRooms::roomcount=" + rooms.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return rooms;
	}

	protected RoomResult makeRoomBaseA4(com.sabre.schema.hotel.avail.v4_0_0.RoomType roomHotelResponse, com.sabre.schema.hotel.avail.v4_0_0.RatePlanRef rateHotelResponse, Set<String> existingRoomCodes, BigInteger roomNumber, String hotelCode) throws Exception
	{
		String roomCode = rateHotelResponse.getProductCode();

		if (log.isDebugEnabled())
			log.debug("loadRooms::processing rateKey '" + rateHotelResponse.getRateKey() + "' with code " + rateHotelResponse.getRatePlanCode());
		String fullRoomCode = roomCode + "_" + rateHotelResponse.getRateKey() + "_" + rateHotelResponse.getRateSource() + "_" + rateHotelResponse.getProductCode() + "_"
				+ rateHotelResponse.getRateInfo().getCurrencyCode();
		if (existingRoomCodes != null && existingRoomCodes.contains(fullRoomCode + "R" + roomNumber))
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::duplicate roomCode info '" + (fullRoomCode + "R" + roomNumber) + "', bypassing");
			return null;
		}
		if (existingRoomCodes != null)
			existingRoomCodes.add(fullRoomCode + "R" + roomNumber);

		RoomResult room = new RoomResult();
		room.setChannel(CHANNEL);
		room.setChannelPropertyCode(hotelCode);
		room.setRoomName(roomHotelResponse.getRoomDescription().getName());
		room.setRoomNumber(roomHotelResponse.getRoomIndex().intValue());
		room.setMultiRoomMatchCode("0");
		room.setRoomCode(rateHotelResponse.getProductCode());
		room.setRateCode(rateHotelResponse.getRateKey());
		room.setMatchCode(rateHotelResponse.getProductCode());
		room.setBookingCode(rateHotelResponse.getRateKey());
		room.setBoardCode(null);
		StringBuilder bedding = new StringBuilder();
		if (roomHotelResponse.getBedTypeOptions() != null) {
			for (com.sabre.schema.hotel.avail.v4_0_0.BedTypes bedTypes : roomHotelResponse.getBedTypeOptions().getBedTypes()) {
				for (com.sabre.schema.hotel.avail.v4_0_0.BedType bedType : bedTypes.getBedTypes()){
					bedding.append(bedType.getDescription());
				}
			}
		}
		room.setBedding(bedding.toString());
		StringBuilder additionalDetails = new StringBuilder();
		if (roomHotelResponse.getAdditionalDetails() != null) {
			for (com.sabre.schema.hotel.avail.v4_0_0.AdditionalDetail additionalDetail : roomHotelResponse.getAdditionalDetails().getAdditionalDetails()) {
				for ( String text : additionalDetail.getTexts())
				{
					if ( additionalDetails.length() > 0 )
						additionalDetails.append(", ");
					additionalDetails.append(text);
				}
			}
		}
		room.setBookingConditions(additionalDetails.toString());
		room.setRoomStandard(null);
		room.setRoomExtraInformation(roomHotelResponse.getRoomDescription().getTexts().get(0));
		room.setRrpIsMandatory(false);
		room.setRequiresRecheck(true);
		room.setBundlesOnly(false);

		if (log.isDebugEnabled())
			log.debug("loadRooms::created hrt code=" + room.getRoomCode() + " name=" + room.getRoomName());
		BigDecimal nettPrice_bd = rateHotelResponse.getRateInfo().getAmountAfterTax();
		BigDecimal totalPrice_bd = rateHotelResponse.getRateInfo().getAmountAfterTax().multiply(new BigDecimal(1.2));

		BigDecimal fullCommission = totalPrice_bd.subtract(nettPrice_bd).multiply(Functions.BD_100).divide(totalPrice_bd, 2, RoundingMode.HALF_DOWN);

		CurrencyValue nettRate = new CurrencyValue(rateHotelResponse.getRateInfo().getCurrencyCode(), nettPrice_bd);
		room.setSupplyRate(nettRate);
		CurrencyValue totalRateRom = new CurrencyValue(rateHotelResponse.getRateInfo().getCurrencyCode(), totalPrice_bd);
		room.setTotalRate(totalRateRom);
		if (log.isDebugEnabled())
			log.debug("loadRooms::nettRate=" + nettRate + ", totalRate=" + totalRateRom + ", mandatory=" + room.getRrpIsMandatory() + ", fullCommission=" + fullCommission);
		if (totalRateRom.getAmount().compareTo(nettRate.getAmount()) < 0)
		{
			log.warn("loadRooms::nettRate=" + nettRate + " less than totalRate=" + totalRateRom + " for hotel code " + hotelCode + " room " + room.getRoomCode() + " " + room.getRoomName()
					+ ", bypassing");
			return null;
		}
		CNXDetails cnxDetails = makeCNXPolicyA4(room, rateHotelResponse);
		SortedSet<RoomCancellationPolicyLine> roomCancellationPolicy = cnxDetails.policy;
		room.setCancellationPolicy(roomCancellationPolicy);
		room.setCancellationPolicyText(cnxDetails.fullPolicyText);
		return room;
	}

	protected RoomResult makeRoomBaseD3(com.sabre.schema.hotel.details.v3_0_0.RoomType roomHotelResponse, com.sabre.schema.hotel.details.v3_0_0.RatePlanRef rateHotelResponse, Set<String> existingRoomCodes, BigInteger roomNumber, String hotelCode) throws Exception
	{
		String roomCode = rateHotelResponse.getProductCode();

		if (log.isDebugEnabled())
			log.debug("loadRooms::processing rateKey '" + rateHotelResponse.getRateKey() + "' with code " + rateHotelResponse.getRatePlanCode());
		String fullRoomCode = roomCode + "_" + rateHotelResponse.getRateKey() + "_" + rateHotelResponse.getRateSource() + "_" + rateHotelResponse.getProductCode();
		if (existingRoomCodes != null && existingRoomCodes.contains(fullRoomCode + "R" + roomNumber))
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::duplicate roomCode info '" + (fullRoomCode + "R" + roomNumber) + "', bypassing");
			return null;
		}
		if (existingRoomCodes != null)
			existingRoomCodes.add(fullRoomCode + "R" + roomNumber);

		RoomResult room = new RoomResult();
		room.setChannel(CHANNEL);
		room.setChannelPropertyCode(hotelCode);
		room.setRoomName(roomHotelResponse.getRoomType() + roomHotelResponse.getRoomDescription().getName() + "(" + roomHotelResponse.getRoomDescription().getTexts().get(0) + ")");
		room.setRoomNumber(roomHotelResponse.getRoomIndex().intValue());
		room.setMultiRoomMatchCode("0");
		room.setRoomCode(rateHotelResponse.getProductCode());
		room.setRateCode(rateHotelResponse.getRateKey());
		room.setBookingCode(rateHotelResponse.getRateKey());
		room.setBoardCode(null);
		StringBuilder bedding = new StringBuilder();
		if (roomHotelResponse.getBedTypeOptions() != null) {
			for (com.sabre.schema.hotel.details.v3_0_0.BedTypes bedTypes : roomHotelResponse.getBedTypeOptions().getBedTypes()) {
				for (com.sabre.schema.hotel.details.v3_0_0.BedType bedType : bedTypes.getBedTypes()){
					bedding.append(bedType.getDescription());
				}
			}
		}
		room.setBedding(bedding.toString());
		StringBuilder additionalDetails = new StringBuilder();
		if (roomHotelResponse.getAdditionalDetails() != null) {
			for (com.sabre.schema.hotel.details.v3_0_0.AdditionalDetail additionalDetail : roomHotelResponse.getAdditionalDetails().getAdditionalDetails()) {
				for ( String text : additionalDetail.getTexts())
				{
					if ( additionalDetails.length() > 0 )
						additionalDetails.append(", ");
					additionalDetails.append(text);
				}
			}
		}
		room.setBookingConditions(additionalDetails.toString());
		room.setRoomStandard(null);
		room.setRoomExtraInformation(roomHotelResponse.getRoomDescription().getTexts().get(0));
		room.setRrpIsMandatory(false);
		room.setRequiresRecheck(true);
		room.setExtraFees(new HashSet<>());
		room.setPromotions(new HashSet<>());
		CNXDetails cnxDetails = makeCNXPolicyD3(room, rateHotelResponse);
		SortedSet<RoomCancellationPolicyLine> roomCancellationPolicy = cnxDetails.policy;
		room.setCancellationPolicy(roomCancellationPolicy);
		room.setCancellationPolicyText(cnxDetails.fullPolicyText);
		room.setBundlesOnly(false);

		if (log.isDebugEnabled())
			log.debug("loadRooms::created hrt code=" + room.getRoomCode() + " name=" + room.getRoomName());
		BigDecimal nettPrice_bd = rateHotelResponse.getConvertedRateInfo().getAmountAfterTax();
		BigDecimal totalPrice_bd = applyInventoryMarkup(nettPrice_bd, null);

		BigDecimal fullCommission = totalPrice_bd.subtract(nettPrice_bd).multiply(Functions.BD_100).divide(totalPrice_bd, 2, RoundingMode.HALF_DOWN);

		CurrencyValue nettRate = new CurrencyValue(rateHotelResponse.getConvertedRateInfo().getCurrencyCode(), nettPrice_bd);
		room.setSupplyRate(nettRate);
		CurrencyValue totalRateRom = new CurrencyValue(rateHotelResponse.getConvertedRateInfo().getCurrencyCode(), totalPrice_bd);
		room.setTotalRate(totalRateRom);
		if (log.isDebugEnabled())
			log.debug("loadRooms::nettRate=" + nettRate + ", totalRate=" + totalRateRom + ", mandatory=" + room.getRrpIsMandatory() + ", fullCommission=" + fullCommission);
		if (totalRateRom.getAmount().compareTo(nettRate.getAmount()) < 0)
		{
			log.warn("loadRooms::nettRate=" + nettRate + " less than totalRate=" + totalRateRom + " for hotel code " + hotelCode + " room " + room.getRoomCode() + " " + room.getRoomName()
					+ ", bypassing");
			return null;
		}
		return room;
	}

	protected RoomResult makeRoomBasePC4(com.sabre.schema.hotel.pricecheck.v4_0_0.RoomType roomHotelResponse, com.sabre.schema.hotel.pricecheck.v4_0_0.RatePlanRef rateHotelResponse, Set<String> existingRoomCodes, BigInteger roomNumber, String hotelCode) throws Exception
	{
		String roomCode = rateHotelResponse.getProductCode();

		if (log.isDebugEnabled())
			log.debug("loadRooms::processing rateKey '" + rateHotelResponse.getRateKey() + "' with code " + rateHotelResponse.getRatePlanCode());
		String fullRoomCode = roomCode + "_" + rateHotelResponse.getRateKey() + "_" + rateHotelResponse.getRateSource() + "_" + rateHotelResponse.getProductCode();
		if (existingRoomCodes != null && existingRoomCodes.contains(fullRoomCode + "R" + roomNumber))
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::duplicate roomCode info '" + (fullRoomCode + "R" + roomNumber) + "', bypassing");
			return null;
		}
		if (existingRoomCodes != null)
			existingRoomCodes.add(fullRoomCode + "R" + roomNumber);

		RoomResult room = new RoomResult();
		room.setChannel(CHANNEL);
		room.setChannelPropertyCode(hotelCode);
		room.setRoomName(roomHotelResponse.getRoomType() + roomHotelResponse.getRoomDescription().getName() + "(" + roomHotelResponse.getRoomDescription().getTexts().get(0) + ")");
		room.setRoomNumber(roomHotelResponse.getRoomIndex().intValue());
		room.setMultiRoomMatchCode("0");
		room.setRoomCode(rateHotelResponse.getProductCode());
		room.setRateCode(rateHotelResponse.getRateKey());
		room.setBookingCode(rateHotelResponse.getRateKey());
		room.setBoardCode(null);
		StringBuilder bedding = new StringBuilder();
		if (roomHotelResponse.getBedTypeOptions() != null) {
			for (com.sabre.schema.hotel.pricecheck.v4_0_0.BedTypes bedTypes : roomHotelResponse.getBedTypeOptions().getBedTypes()) {
				for (com.sabre.schema.hotel.pricecheck.v4_0_0.BedType bedType : bedTypes.getBedTypes()){
					bedding.append(bedType.getDescription());
				}
			}
		}
		room.setBedding(bedding.toString());
		StringBuilder additionalDetails = new StringBuilder();
		if (roomHotelResponse.getAdditionalDetails() != null) {
			for (AdditionalDetail additionalDetail : roomHotelResponse.getAdditionalDetails().getAdditionalDetails()) {
				for ( String text : additionalDetail.getTexts())
				{
					if ( additionalDetails.length() > 0 )
						additionalDetails.append(", ");
					additionalDetails.append(text);
				}
			}
		}
		room.setBookingConditions(additionalDetails.toString());
		room.setRoomStandard(null);
		room.setRoomExtraInformation(roomHotelResponse.getRoomDescription().getTexts().get(0));
		room.setRrpIsMandatory(false);
		room.setRequiresRecheck(true);
		room.setExtraFees(new HashSet<>());
		room.setPromotions(new HashSet<>());
		CNXDetails cnxDetails = makeCNXPolicyP4(room, rateHotelResponse);
		SortedSet<RoomCancellationPolicyLine> roomCancellationPolicy = cnxDetails.policy;
		room.setCancellationPolicy(roomCancellationPolicy);
		room.setCancellationPolicyText(cnxDetails.fullPolicyText);
		room.setBundlesOnly(false);

		if (log.isDebugEnabled())
			log.debug("loadRooms::created hrt code=" + room.getRoomCode() + " name=" + room.getRoomName());
		BigDecimal nettPrice_bd = rateHotelResponse.getConvertedRateInfo().getAmountAfterTax();
		BigDecimal totalPrice_bd = applyInventoryMarkup(nettPrice_bd, null);
		BigDecimal fullCommission = totalPrice_bd.subtract(nettPrice_bd).multiply(Functions.BD_100).divide(totalPrice_bd, 2, RoundingMode.HALF_DOWN);
		CurrencyValue nettRate = new CurrencyValue(rateHotelResponse.getConvertedRateInfo().getCurrencyCode(), nettPrice_bd);
		room.setSupplyRate(nettRate);
		CurrencyValue totalRateRom = new CurrencyValue(rateHotelResponse.getConvertedRateInfo().getCurrencyCode(), totalPrice_bd);
		room.setTotalRate(totalRateRom);
		if (log.isDebugEnabled())
			log.debug("loadRooms::nettRate=" + nettRate + ", totalRate=" + totalRateRom + ", mandatory=" + room.getRrpIsMandatory() + ", fullCommission=" + fullCommission);
		if (totalRateRom.getAmount().compareTo(nettRate.getAmount()) < 0)
		{
			log.warn("loadRooms::nettRate=" + nettRate + " less than totalRate=" + totalRateRom + " for hotel code " + hotelCode + " room " + room.getRoomCode() + " " + room.getRoomName()
					+ ", bypassing");
			return null;
		}
		return room;
	}

	@Override
	public List<AccommodationResult> searchByHotelId(AvailSearchByHotelIdRQDTO availSearchRQ)
	{
		log.debug("search::search(AvailSearchByHotelIdRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		GetHotelDetailsRS hotelDetailsRS;
		List<AccommodationResult> results = new ArrayList<>();

		try
		{
			SaberCSLInterface saberCSLInterface = new SaberCSLInterface(propertiesDAO, availSearchRQ.getClient(), CHANNEL);
			long timer2 = System.currentTimeMillis();
			hotelDetailsRS = saberCSLInterface.startSearchHotelsDetail(availSearchRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("search::time in hotelbeds search was " + totalTime2 + " millis");
			if (hotelDetailsRS == null || hotelDetailsRS.getHotelDetailsInfo() == null || hotelDetailsRS.getHotelDetailsInfo().getHotelInfo() == null)
			{
				log.debug("search::availabilityRS returned no RS");
			}
			else
			{
				int listNo = 0;
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned " + hotelDetailsRS.getHotelDetailsInfo().getHotelInfo().getHotelCode() + " hotels");

				if (log.isDebugEnabled())
					log.debug("search::processing item " + listNo);

				Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(CHANNEL_PREFIX + hotelDetailsRS.getHotelDetailsInfo().getHotelInfo().getHotelCode());
				AccommodationRC accommodationRC;
				if (accommodationRCOpt.isPresent())
				{
					accommodationRC = accommodationRCOpt.get();
				}
				else
				{
					GetHotelContentRS hotelContentRS = saberCSLInterface.getHotelContent(hotelDetailsRS.getHotelDetailsInfo().getHotelInfo().getHotelCode());
					accommodationRC = mappingAccommodationResult(hotelContentRS.getHotelContentInfos());
					accommodationRCService.saveAccommodationRC(accommodationRC);
				}
				AccommodationResult accommodationResult = new AccommodationResult();

				accommodationResult.setProperty(new AccommodationProperty());
				BeanUtils.copyProperties(accommodationRC, accommodationResult.getProperty());

				accommodationResult.setRooms(new TreeSet<>());
				for (RoomSet roomSet : hotelDetailsRS.getHotelDetailsInfo().getHotelRateInfo().getRoomSets().getRoomSets()) {
					accommodationResult.getRooms().addAll(loadRoomsD3(roomSet.getRooms(), hotelDetailsRS.getHotelDetailsInfo().getHotelInfo().getHotelCode()));
				}

				if (accommodationResult.getRooms() == null || accommodationResult.getRooms().size() == 0)
				{
					if (log.isDebugEnabled())
						log.debug("search::hotel " + accommodationResult.getProperty().getCode() + " not added, has no rooms");
				}
				else
				{
					results.add(accommodationResult);
				}

			}
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	@Override
	public AccommodationRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter for " + rateCheckRQDTO);

		long timer1 = System.currentTimeMillis();

		try
		{
			SaberCSLInterface saberCSLInterface = new SaberCSLInterface(propertiesDAO, rateCheckRQDTO.getClient(), CHANNEL);

			AccommodationRateCheckRS rateCheckRS = new AccommodationRateCheckRS();

			String currency = "AUD";
			for ( String rateKeys : rateCheckRQDTO.getBookingCodes())
			{
				HotelPriceCheckRS checkRateRS = saberCSLInterface.checkRates(rateKeys);
				if (checkRateRS != null && checkRateRS.getPriceCheckInfo() != null && checkRateRS.getPriceCheckInfo().getHotelRateInfo().getRooms() != null)
				{
					if (log.isDebugEnabled())
						log.debug("rateCheck::rateKey found");
					rateCheckRS.getRooms().addAll(loadRoomsP4(checkRateRS.getPriceCheckInfo().getHotelRateInfo().getRooms(), currency, checkRateRS.getPriceCheckInfo().getBookingKey()));
				}
			}
			log.info("rateCheck:: time taken = " + (System.currentTimeMillis() - timer1));
			return rateCheckRS;
		}
		catch (Exception e)
		{
			log.error("rateCheck::threw exception " + e.toString(), e);
		}

		return null;
	}

	@Override
	public AccommodationBookRS book(String client, AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::received " + bookRQ);
		long timer1 = System.currentTimeMillis();
		try
		{
			SaberCSLInterface saberCSLInterface = new SaberCSLInterface(propertiesDAO, client, CHANNEL);
			AccommodationBookRS bookRS = saberCSLInterface.book(bookRQ);
			log.info("book:: time taken = " + (System.currentTimeMillis() - timer1));
			return bookRS;
		}
		catch (Exception e)
		{
			log.error("book::threw exception " + e.toString(), e);
		}
		return null;
	}

	@Override
	public AccommodationCancelRS cancel(String site, AccommodationCancelRQ cancelRQ) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccommodationRetrieveRS retrieve(String site, AccommodationRetrieveRQ retrieveRQ) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	private static class CNXDetails
	{
		public SortedSet<RoomCancellationPolicyLine> policy = new TreeSet<>();

		public String fullPolicyText;
	}
	private CNXDetails makeCNXPolicyA4(RoomResult room, com.sabre.schema.hotel.avail.v4_0_0.RatePlanRef rateHotelResponse) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("makeCNXPolicy::entering");
		Boolean markupCNXValues = false;
		List<RoomCancellationPolicyLine> hotelRoomTypeCancellationPolicy = new ArrayList<RoomCancellationPolicyLine>();
		StringBuffer fullPolicyText = new StringBuffer();
		BigDecimal numberOfRooms = BigDecimal.ONE;
		LocalDate today = LocalDate.now();
		CNXDetails cnxDetails = new CNXDetails();
		for ( CancelPenalty cancelPenalty : rateHotelResponse.getRateInfo().getCancelPenalties().getCancelPenalties() )
		{
			BigDecimal penaltyAmount = room.getSupplyRate().getAmount();
			if (markupCNXValues)
				penaltyAmount = applyInventoryMarkup(room.getSupplyRate().getAmount(), null);
			CurrencyValue penalty = new CurrencyValue(room.getSupplyRate().getCurrencyId(), penaltyAmount);
			RoomCancellationPolicyLine policyLine = new RoomCancellationPolicyLine();
			Calendar asAtDateCal = Calendar.getInstance();
			if ( cancelPenalty.getDeadline() == null || cancelPenalty.getDeadline().getAbsoluteDeadline() == null )
			{
				log.warn("makeCNXPolicy::absolute deadline not available");
				return makeNonRefundableCNXPolicy(penalty);
			}
			asAtDateCal.setTime(Functions.convertGeoToTimestamp(cancelPenalty.getDeadline().getAbsoluteDeadline()));
			asAtDateCal.add(Calendar.DATE, -2);
			LocalDate asAtDate = Functions.normaliseLocalDate(asAtDateCal.getTime());
			String cnxPolicyText = new String("If cancelled on or after " + df2ddmmmYY.format(asAtDate) + " - a charge of " + Functions.formatCurrencyDisplay(penalty) + " applies. ");

			//Integer nightsFromPrice = Functions.getNightsFromPrice(cancellationPolicy.getAmount(), rateHotelResponse.getNet(), rateHotelResponse.getSellingRate(),
			//		(int) ChronoUnit.DAYS.between(checkin, checkout), numberOfRooms.intValue());
			//if (nightsFromPrice != null)
			//{
			//	cnxPolicyText = new String(
			//			"If cancelled on or after " + df2ddmmmYY.format(asAtDate) + " - a charge of " + nightsFromPrice + " night" + (nightsFromPrice > 1 ? "s" : "") + " applies. ");
			//}
			fullPolicyText.append(cnxPolicyText);
			policyLine.setAsOf(asAtDate);
			policyLine.setPenaltyDescription(cnxPolicyText);
			policyLine.setPenalty(penalty);
			hotelRoomTypeCancellationPolicy.add(policyLine);
		}
		cnxDetails.fullPolicyText = Functions.cleanHTML(fullPolicyText.toString());
		cnxDetails.policy.addAll(hotelRoomTypeCancellationPolicy);
		return cnxDetails;
	}
	private CNXDetails makeCNXPolicyP4(RoomResult room, com.sabre.schema.hotel.pricecheck.v4_0_0.RatePlanRef rateHotelResponse) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("makeCNXPolicy::entering");
		Boolean markupCNXValues = false;
		List<RoomCancellationPolicyLine> hotelRoomTypeCancellationPolicy = new ArrayList<RoomCancellationPolicyLine>();
		StringBuffer fullPolicyText = new StringBuffer();
		BigDecimal numberOfRooms = BigDecimal.ONE;
		LocalDate today = LocalDate.now();
		CNXDetails cnxDetails = new CNXDetails();
		for ( com.sabre.schema.hotel.pricecheck.v4_0_0.CancelPenaltiesType.CancelPenalty cancelPenalty : rateHotelResponse.getRateInfo().getCancelPenalties().getCancelPenalties() )
		{
			BigDecimal penaltyAmount = room.getSupplyRate().getAmount();
			if (markupCNXValues)
				penaltyAmount = applyInventoryMarkup(room.getSupplyRate().getAmount(), null);
			CurrencyValue penalty = new CurrencyValue(room.getSupplyRate().getCurrencyId(), penaltyAmount);
			RoomCancellationPolicyLine policyLine = new RoomCancellationPolicyLine();
			Calendar asAtDateCal = Calendar.getInstance();
			if ( cancelPenalty.getDeadline() == null || cancelPenalty.getDeadline().getAbsoluteDeadline() == null )
			{
				log.warn("makeCNXPolicy::absolute deadline not available");
				return makeNonRefundableCNXPolicy(penalty);
			}
			asAtDateCal.setTime(Functions.convertGeoToTimestamp(cancelPenalty.getDeadline().getAbsoluteDeadline()));
			asAtDateCal.add(Calendar.DATE, -2);
			LocalDate asAtDate = Functions.normaliseLocalDate(asAtDateCal.getTime());
			String cnxPolicyText = new String("If cancelled on or after " + df2ddmmmYY.format(asAtDate) + " - a charge of " + Functions.formatCurrencyDisplay(penalty) + " applies. ");

			//Integer nightsFromPrice = Functions.getNightsFromPrice(cancellationPolicy.getAmount(), rateHotelResponse.getNet(), rateHotelResponse.getSellingRate(),
			//		(int) ChronoUnit.DAYS.between(checkin, checkout), numberOfRooms.intValue());
			//if (nightsFromPrice != null)
			//{
			//	cnxPolicyText = new String(
			//			"If cancelled on or after " + df2ddmmmYY.format(asAtDate) + " - a charge of " + nightsFromPrice + " night" + (nightsFromPrice > 1 ? "s" : "") + " applies. ");
			//}
			fullPolicyText.append(cnxPolicyText);
			policyLine.setAsOf(asAtDate);
			policyLine.setPenaltyDescription(cnxPolicyText);
			policyLine.setPenalty(penalty);
			hotelRoomTypeCancellationPolicy.add(policyLine);
		}
		cnxDetails.fullPolicyText = Functions.cleanHTML(fullPolicyText.toString());
		cnxDetails.policy.addAll(hotelRoomTypeCancellationPolicy);
		return cnxDetails;
	}
	private CNXDetails makeCNXPolicyD3(RoomResult room, com.sabre.schema.hotel.details.v3_0_0.RatePlanRef rateHotelResponse) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("makeCNXPolicy::entering");
		Boolean markupCNXValues = false;
		List<RoomCancellationPolicyLine> hotelRoomTypeCancellationPolicy = new ArrayList<RoomCancellationPolicyLine>();
		StringBuffer fullPolicyText = new StringBuffer();
		BigDecimal numberOfRooms = BigDecimal.ONE;
		LocalDate today = LocalDate.now();
		CNXDetails cnxDetails = new CNXDetails();
		for ( com.sabre.schema.hotel.details.v3_0_0.CancelPenaltiesType.CancelPenalty cancelPenalty : rateHotelResponse.getRateInfo().getCancelPenalties().getCancelPenalties() )
		{
			BigDecimal penaltyAmount = room.getSupplyRate().getAmount();
			if (markupCNXValues)
				penaltyAmount = applyInventoryMarkup(room.getSupplyRate().getAmount(), null);
			CurrencyValue penalty = new CurrencyValue(room.getSupplyRate().getCurrencyId(), penaltyAmount);
			RoomCancellationPolicyLine policyLine = new RoomCancellationPolicyLine();
			Calendar asAtDateCal = Calendar.getInstance();
			if ( cancelPenalty.getDeadline() == null || cancelPenalty.getDeadline().getAbsoluteDeadline() == null )
			{
				log.warn("makeCNXPolicy::absolute deadline not available");
				return makeNonRefundableCNXPolicy(penalty);
			}
			asAtDateCal.setTime(Functions.convertGeoToTimestamp(cancelPenalty.getDeadline().getAbsoluteDeadline()));
			asAtDateCal.add(Calendar.DATE, -2);
			LocalDate asAtDate = Functions.normaliseLocalDate(asAtDateCal.getTime());
			String cnxPolicyText = new String("If cancelled on or after " + df2ddmmmYY.format(asAtDate) + " - a charge of " + Functions.formatCurrencyDisplay(penalty) + " applies. ");

			//Integer nightsFromPrice = Functions.getNightsFromPrice(cancellationPolicy.getAmount(), rateHotelResponse.getNet(), rateHotelResponse.getSellingRate(),
			//		(int) ChronoUnit.DAYS.between(checkin, checkout), numberOfRooms.intValue());
			//if (nightsFromPrice != null)
			//{
			//	cnxPolicyText = new String(
			//			"If cancelled on or after " + df2ddmmmYY.format(asAtDate) + " - a charge of " + nightsFromPrice + " night" + (nightsFromPrice > 1 ? "s" : "") + " applies. ");
			//}
			fullPolicyText.append(cnxPolicyText);
			policyLine.setAsOf(asAtDate);
			policyLine.setPenaltyDescription(cnxPolicyText);
			policyLine.setPenalty(penalty);
			hotelRoomTypeCancellationPolicy.add(policyLine);
		}
		cnxDetails.fullPolicyText = Functions.cleanHTML(fullPolicyText.toString());
		cnxDetails.policy.addAll(hotelRoomTypeCancellationPolicy);
		return cnxDetails;
	}
	
	private CNXDetails makeNonRefundableCNXPolicy(CurrencyValue penalty)
	{
		StringBuffer fullPolicyText = new StringBuffer();
		List<RoomCancellationPolicyLine> hotelRoomTypeCancellationPolicy = new ArrayList<RoomCancellationPolicyLine>();
		LocalDate today = LocalDate.now();
		CNXDetails cnxDetails = new CNXDetails();
		RoomCancellationPolicyLine policyLine = new RoomCancellationPolicyLine();
		String cnxPolicyText = "Non refundable";
		fullPolicyText.append(cnxPolicyText);
		policyLine.setAsOf(today);
		policyLine.setPenaltyDescription(cnxPolicyText);
		policyLine.setPenalty(penalty);
		hotelRoomTypeCancellationPolicy.add(policyLine);
		cnxDetails.fullPolicyText = Functions.cleanHTML(fullPolicyText.toString());
		cnxDetails.policy.addAll(hotelRoomTypeCancellationPolicy);
		return cnxDetails;

	}
	private static final BigDecimal CSL_MARKUP = new BigDecimal("1.1363636");

	private static BigDecimal applyInventoryMarkup(BigDecimal nett, BigDecimal gross) throws Exception
	{
		return nett.multiply(CSL_MARKUP).setScale(0, RoundingMode.UP);
	}

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("sabreURL", "sabreURL", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("pcc", "pcc", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("username", "username", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("password", "password", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Agency addressLine", "addressLine", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Agency cityName", "cityName", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Agency countryCode", "countryCode", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Agency postalCode", "postalCode", FieldType.STRING, true, " "));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Agency streetNmbr", "streetNmbr", FieldType.STRING, true, " "));
		return channelType;
	}
	private static DateTimeFormatter df2ddmmmYY = DateTimeFormatter.ofPattern("dd MMM yy");
}
