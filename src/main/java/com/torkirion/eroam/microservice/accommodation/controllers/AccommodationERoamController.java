package com.torkirion.eroam.microservice.accommodation.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.FacilityGroup;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelBaseRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelBaseRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelDefaultRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelDefaultRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelDetailRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelDetailRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelListRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelListRS;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelBaseRS.Hotel;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.services.*;
import com.torkirion.eroam.microservice.apidomain.*;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;

@RestController
@RequestMapping("/accommodation/v1/eroam")
@Api(value = "Accommodation Service API - eRoam front end specifics")
@Slf4j
@AllArgsConstructor
public class AccommodationERoamController
{
	@Autowired
	private AccommodationController accommodationController;

	@Autowired
	private AccommodationRCService rcService;

	@ApiOperation(value = "Get Hotel Default call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotelDefault", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public HotelDefaultRS getHotelDefault(@RequestBody HotelDefaultRQ hotelDefaultRQ)
	{
		log.info("getHotelDefault::enter for " + hotelDefaultRQ);
		long timer1 = System.currentTimeMillis();

		try
		{
			if (hotelDefaultRQ.getNorthwest() == null || hotelDefaultRQ.getSoutheast() == null)
			{
				log.debug("getHotelDefault::latlongs are empty");
				return null;
			}
			AvailSearchByGeocoordBoxRQ availSearchByGeocoordBoxRQ = new AvailSearchByGeocoordBoxRQ();
			availSearchByGeocoordBoxRQ.setCheckin(hotelDefaultRQ.getCheckin());
			availSearchByGeocoordBoxRQ.setCheckout(hotelDefaultRQ.getCheckout());
			availSearchByGeocoordBoxRQ.setCountryCodeOfOrigin(hotelDefaultRQ.getCountryCodeOfOrigin());
			availSearchByGeocoordBoxRQ.setNorthwest(hotelDefaultRQ.getNorthwest());
			availSearchByGeocoordBoxRQ.setSoutheast(hotelDefaultRQ.getSoutheast());
			availSearchByGeocoordBoxRQ.setDistanceCentrepoint(hotelDefaultRQ.getDistanceCentrepoint());
			availSearchByGeocoordBoxRQ.setTravellers(hotelDefaultRQ.getTravellers());
			availSearchByGeocoordBoxRQ.setChannel(hotelDefaultRQ.getChannel());
			availSearchByGeocoordBoxRQ.setChannelExceptions(hotelDefaultRQ.getChannelExceptions());
			RequestData<AvailSearchByGeocoordBoxRQ> rq = new RequestData<>(availSearchByGeocoordBoxRQ);
			rq.setClient(hotelDefaultRQ.getClient());
			ResponseData<List<AccommodationResult>> rs = accommodationController.availSearchByGeoBox(rq);

			if (rs.getData() == null)
			{
				log.debug("getHotelDefault::data is empty");
				return null;
			}

			List<AccommodationResult> rsSet = rs.getData();

			log.debug("getHotelDefault::rsSet has " + rsSet.size() + " results");

			if (hotelDefaultRQ.getAccommodation_rating() != null && hotelDefaultRQ.getAccommodation_rating().compareTo(BigDecimal.ZERO) > 0)
			{
				rsSet = filterByStarRating(rsSet, hotelDefaultRQ.getAccommodation_rating());
			}

			for (AccommodationResult accommodationResult : rsSet)
			{
				if (accommodationResult.getRooms() == null || accommodationResult.getRooms().size() == 0)
				{
					log.debug("getHotelDefault::accommodationResult " + accommodationResult.getProperty() + " has no rooms, continuing...");
					continue;
				}
				if (accommodationResult.getProperty().getImageThumbnailUrl() == null || accommodationResult.getProperty().getImageThumbnailUrl().length() == 0)
				{
					log.debug("getHotelDefault::accommodationResult " + accommodationResult.getProperty() + " has no thumbimage, continuing...");
					continue;
				}
				Optional<AccommodationRC> accommodationRCOpt = rcService.getAccommodationRC(hotelDefaultRQ.getClient(), accommodationResult.getProperty().getCode());
				if (!accommodationRCOpt.isPresent())
				{
					log.debug("getHotelDefault::accommodationResult " + accommodationResult.getProperty() + " has no thumbimage, continuing...");
					continue;
				}

				// if we have any Hotelbeds rooms, re-call them!
				rateCheckHotelbeds(accommodationResult.getRooms(), hotelDefaultRQ, accommodationResult.getProperty().getCode());

				HotelDefaultRS hotelDefaultRS = new HotelDefaultRS();
				hotelDefaultRS.setDefault_hotel(mapHotelBaseRS(accommodationResult, hotelDefaultRQ, false));
				log.debug("getHotelDefault::hotelDefaultRS=" + hotelDefaultRS);
				log.info("getHotelDefault finished::" + (System.currentTimeMillis() - timer1));
				return hotelDefaultRS;
			}

			log.debug("getHotelDefault::exit empty");
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelDefault::error " + e.toString(), e);
			return null;
		}
	}

	@ApiOperation(value = "Get Hotel List call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotelList", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public HotelListRS getHotelList(@RequestBody HotelListRQ hotelListRQ)
	{
		log.info("getHotelList::enter for " + hotelListRQ);
		long timer1 = System.currentTimeMillis();
		try
		{
			AvailSearchByGeocoordBoxRQ availSearchByGeocoordBoxRQ = new AvailSearchByGeocoordBoxRQ();
			availSearchByGeocoordBoxRQ.setCheckin(hotelListRQ.getCheckin());
			availSearchByGeocoordBoxRQ.setCheckout(hotelListRQ.getCheckout());
			availSearchByGeocoordBoxRQ.setCountryCodeOfOrigin(hotelListRQ.getCountryCodeOfOrigin());
			availSearchByGeocoordBoxRQ.setNorthwest(hotelListRQ.getNorthwest());
			availSearchByGeocoordBoxRQ.setSoutheast(hotelListRQ.getSoutheast());
			availSearchByGeocoordBoxRQ.setDistanceCentrepoint(hotelListRQ.getDistanceCentrepoint());
			availSearchByGeocoordBoxRQ.setTravellers(hotelListRQ.getTravellers());
			availSearchByGeocoordBoxRQ.setChannel(hotelListRQ.getChannel());
			availSearchByGeocoordBoxRQ.setChannelExceptions(hotelListRQ.getChannelExceptions());
			availSearchByGeocoordBoxRQ.setKilometerFilter(hotelListRQ.getKilometerFilter());
			RequestData<AvailSearchByGeocoordBoxRQ> rq = new RequestData<>(availSearchByGeocoordBoxRQ);
			rq.setClient(hotelListRQ.getClient());
			ResponseData<List<AccommodationResult>> rs = accommodationController.availSearchByGeoBox(rq);

			if (rs.getData() == null)
			{
				log.debug("getHotelList::data is empty");
				return null;
			}

			List<AccommodationResult> rsSet = rs.getData();

			log.debug("getHotelList::rsSet has " + rsSet.size() + " results");

			if (hotelListRQ.getAccommodation_rating() != null && hotelListRQ.getAccommodation_rating().compareTo(BigDecimal.ZERO) > 0)
			{
				rsSet = filterByStarRating(rsSet, hotelListRQ.getAccommodation_rating());
			}

			HotelListRS hotelListRS = new HotelListRS();
			for (AccommodationResult accommodationResult : rsSet)
			{
				HotelBaseRS.Hotel hotel = mapHotelBaseRS(accommodationResult, hotelListRQ, true);
				if (hotel != null)
				{
					if (hotelListRQ.getHighlightedHotelId() != null && hotelListRQ.getHighlightedHotelId().equals(hotel.getId()))
					{
						hotel.setHighlightedHotel(Boolean.TRUE);
					}
					hotelListRS.getHotel_list().add(hotel);
				}
			}
			log.debug("getHotelList::exit with " + hotelListRS.getHotel_list().size() + " hotels");
			log.info("getHotelList finished::" + (System.currentTimeMillis() - timer1));
			return hotelListRS;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelList::error " + e.toString(), e);
			return null;
		}
	}

	@ApiOperation(value = "Get Hotel Detail call")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Failed") })
	@RequestMapping(value = "/hotelDetail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public HotelDetailRS getHotelDetail(@RequestBody HotelDetailRQ hotelDetailRQ)
	{
		log.info("getHotelDetail::enter for " + hotelDetailRQ);
		long timer1 = System.currentTimeMillis();
		try
		{
			AvailSearchByHotelIdRQ availSearchByHotelIdRQ = new AvailSearchByHotelIdRQ();
			availSearchByHotelIdRQ.setCheckin(hotelDetailRQ.getCheckin());
			availSearchByHotelIdRQ.setCheckout(hotelDetailRQ.getCheckout());
			availSearchByHotelIdRQ.setCountryCodeOfOrigin(hotelDetailRQ.getCountryCodeOfOrigin());
			availSearchByHotelIdRQ.setTravellers(hotelDetailRQ.getTravellers());
			availSearchByHotelIdRQ.setHotelIds(new HashSet<>());
			availSearchByHotelIdRQ.getHotelIds().add(hotelDetailRQ.getHotel_id());
			availSearchByHotelIdRQ.setChannel(hotelDetailRQ.getChannel());
			availSearchByHotelIdRQ.setChannelExceptions(hotelDetailRQ.getChannelExceptions());
			RequestData<AvailSearchByHotelIdRQ> rq = new RequestData<>(availSearchByHotelIdRQ);
			rq.setClient(hotelDetailRQ.getClient());
			ResponseData<List<AccommodationResult>> rs = accommodationController.availSearchByHotelId(rq);

			if (rs != null && rs.getData() != null && rs.getData().size() > 0)
			{
				for (AccommodationResult accommodationResult : rs.getData())
				{
					rateCheckHotelbeds(accommodationResult.getRooms(), hotelDetailRQ, accommodationResult.getProperty().getCode());
					HotelDetailRS hotelDetailRS = new HotelDetailRS();
					hotelDetailRS.setBasic_detail(mapHotelDetailRS(accommodationResult, hotelDetailRQ));
					log.info("getHotelDetail finished::" + (System.currentTimeMillis() - timer1));
					if (log.isDebugEnabled())
						log.debug("getHotelDetail::returning " + hotelDetailRS);
					return hotelDetailRS;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getHotelDetail::error " + e.toString(), e);
			return null;
		}
		log.error("getHotelDetail::no result found for " + hotelDetailRQ);
		return null;
	}

	protected HotelBaseRS.BasicHotelDetail mapHotelDetailRS(AccommodationResult accommodationResult, HotelBaseRQ rq)
	{
		log.debug("mapHotelDetailRS::enter");
		Optional<AccommodationRC> accommodationRCOpt = rcService.getAccommodationRC(rq.getClient(), accommodationResult.getProperty().getCode());
		if (!accommodationRCOpt.isPresent())
		{
			return null;
		}
		AccommodationRC accommodationRC = accommodationRCOpt.get();
		HotelBaseRS.BasicHotelDetail hotel = new HotelBaseRS.BasicHotelDetail();
		hotel.setHotel_id(accommodationRC.getCode());
		hotel.setHotel_name(accommodationRC.getAccommodationName());
		if (accommodationRC.getAddress().getFullFormAddress() != null && accommodationRC.getAddress().getFullFormAddress().length() > 0)
		{
			hotel.setAddress(accommodationRC.getAddress().getFullFormAddress());
		}
		else
		{
			hotel.setAddress(accommodationRC.getAddress().getStreet());
		}
		hotel.setZip_code(accommodationRC.getAddress().getPostcode());
		hotel.setPhone(accommodationRC.getPhone());
		hotel.setStar(accommodationRC.getRating());
		hotel.setCategory(accommodationRC.getCategory());
		if (accommodationRC.getAddress().getGeoCoordinates() != null)
		{
			hotel.setLatitude(accommodationRC.getAddress().getGeoCoordinates().getLatitude());
			hotel.setLongitude(accommodationRC.getAddress().getGeoCoordinates().getLongitude());
		}
		hotel.setDescription(accommodationRC.getIntroduction());
		StringBuffer amenityTags = new StringBuffer();
		for (FacilityGroup facilityGroup : accommodationRC.getFacilityGroups())
		{
			for (String facility : facilityGroup.getFacilities())
			{
				if (amenityTags.length() > 0)
					amenityTags.append("<br>");
				amenityTags.append(facility);
			}
		}
		hotel.setTags(amenityTags.toString());
		hotel.setAmenities(amenityTags.toString());
		for (Image image : accommodationRC.getImages())
		{
			HotelBaseRS.HotelImage hotelImage = new HotelBaseRS.HotelImage();
			hotelImage.setHotel_id(image.getChannelCode());
			hotelImage.setImages(new HotelBaseRS.Image());
			hotelImage.getImages().setMain(yalagoImageHack(image.getImageURL()));
			hotelImage.getImages().setThumbnail(yalagoImageHack(image.getImageURL()));
			hotel.getHotel_images().add(hotelImage);
		}
		hotel.setOleryData(accommodationResult.getProperty().getOleryData());

		// find cheapest room
		BestRooms bestRooms = findBestRooms(accommodationResult.getRooms());

		for (RoomResult room : bestRooms.multiBest.values())
		{
			HotelDefaultRS.Hotel.Room r = mapRoom(room, accommodationResult, rq.getTravellers(), rq.getCheckin());
			hotel.getSelectedRooms().add(r);
			if (hotel.getTotal_net_price() == null)
			{
				hotel.setTotal_net_price(r.getNet_price());
			}
			else
			{
				hotel.setTotal_net_price(new CurrencyValue(hotel.getTotal_net_price().getCurrencyId(), hotel.getTotal_net_price().getAmount().add(r.getNet_price().getAmount())));
			}
			if (hotel.getTotal_retail_price() == null)
			{
				hotel.setTotal_retail_price(r.getRetail_price());
			}
			else
			{
				hotel.setTotal_retail_price(new CurrencyValue(hotel.getTotal_retail_price().getCurrencyId(), hotel.getTotal_retail_price().getAmount().add(r.getRetail_price().getAmount())));
			}
			log.debug("mapHotelDetailRS::multiBest:" + room + ", adding " + r);
		}
		Map<Integer, List<HotelDefaultRS.Hotel.Room>> roomsByNumber = new HashMap<>();
		for (RoomResult room : accommodationResult.getRooms())
		{
			HotelDefaultRS.Hotel.Room r = mapRoom(room, accommodationResult, rq.getTravellers(), rq.getCheckin());
			List<HotelDefaultRS.Hotel.Room> rooms = roomsByNumber.get(r.getRoomNumber());
			if (rooms == null)
			{
				rooms = new ArrayList<>();
				roomsByNumber.put(r.getRoomNumber(), rooms);
			}
			rooms.add(r);
		}
		for (Entry<Integer, List<com.torkirion.eroam.microservice.accommodation.apidomain.eroam.HotelBaseRS.Hotel.Room>> e : roomsByNumber.entrySet())
		{
			HotelDefaultRS.Hotel.RoomNumber rNum = new HotelDefaultRS.Hotel.RoomNumber();
			rNum.setRoomNumber(e.getKey());
			rNum.setRoomList(e.getValue());
			hotel.getHotel_room_list().add(rNum);
		}
		return hotel;
	}

	@ToString
	private static class BestRooms
	{
		RoomResult bestRoom;

		Map<String, RoomResult> channelBest;

		Map<Integer, RoomResult> multiBest;

		Map<String, Map<Integer, RoomResult>> channelMultiBest;
	}

	protected BestRooms findBestRooms(Collection<RoomResult> rooms)
	{
		if (log.isDebugEnabled())
			log.debug("findBestRooms::enter for " + rooms.size() + " rooms");
		// this will be THE cheapest room in the property
		RoomResult bestRoom = null;
		// the best room from each channel
		Map<String, RoomResult> channelBest = new HashMap<>();
		if (log.isDebugEnabled())
			log.debug("findBestRooms::find channelBest");
		for (RoomResult room : rooms)
		{
			if (log.isDebugEnabled())
				log.debug("findBestRooms::room=" + room);
			if (room.getBundlesOnly() != null && room.getBundlesOnly())
			{
				continue;
			}
			if (bestRoom == null)
				bestRoom = room;
			else if (bestRoom.getSupplyRate().getAmount().compareTo(room.getSupplyRate().getAmount()) > 0)
			{
				bestRoom = room;
			}
			if (channelBest.get(room.getChannel()) == null)
			{
				channelBest.put(room.getChannel(), room);
			}
			else if (channelBest.get(room.getChannel()).getSupplyRate().getAmount().compareTo(room.getSupplyRate().getAmount()) > 0)
			{
				channelBest.put(room.getChannel(), room);
			}
		}
		if (log.isDebugEnabled())
			log.debug("findBestRooms::best room=" + bestRoom);
		if (bestRoom == null)
		{
			return null;
		}
		// this finds the SINGLE best multi-room combo (actually not, it used the cheapest room, and pairs with it the cheapest
		// other valid room)
		Map<Integer, RoomResult> multiBest = new HashMap<>();
		multiBest.put(bestRoom.getRoomNumber(), bestRoom);
		if (log.isDebugEnabled())
			log.debug("findBestRooms::find multiBest");
		for (RoomResult room : rooms)
		{
			if (log.isDebugEnabled())
				log.debug("findBestRooms::room=" + room);
			if (room.getRoomNumber().intValue() == bestRoom.getRoomNumber().intValue())
			{
				if (log.isDebugEnabled())
					log.debug("findBestRooms::skipping best room#" + room.getRoomNumber());
				continue;
			}
			if (!room.getMultiRoomMatchCode().equals(bestRoom.getMultiRoomMatchCode()))
			{
				if (log.isDebugEnabled())
					log.debug("findBestRooms::skipping different match code " + room.getMultiRoomMatchCode());
				continue;
			}
			if (multiBest.get(room.getRoomNumber()) == null)
			{
				if (log.isDebugEnabled())
					log.debug("findBestRooms::setting new entry for room#" + room.getRoomNumber());
				multiBest.put(room.getRoomNumber(), room);
			}
			else
			{
				if (multiBest.get(room.getRoomNumber()).getTotalRate().getAmount().compareTo(room.getTotalRate().getAmount()) > 0)
				{
					if (log.isDebugEnabled())
						log.debug("findBestRooms::updating new entry for room#" + room.getRoomNumber());
					multiBest.put(room.getRoomNumber(), room);
				}
			}
		}
		int maxRoomNumber = 0;
		Map<String, Map<Integer, RoomResult>> channelMultiBest = new HashMap<>();
		for (RoomResult room : rooms)
		{
			if (log.isDebugEnabled())
				log.debug("findBestRooms::channelMultiBest::room=" + room);
			if (room.getRoomNumber() > maxRoomNumber)
				maxRoomNumber = room.getRoomNumber();
			if (channelMultiBest.get(room.getChannel()) == null)
			{
				if (log.isDebugEnabled())
					log.debug("findBestRooms::channelMultiBest::channel " + room.getChannel() + " empty");
				channelMultiBest.put(room.getChannel(), new HashMap<Integer, RoomResult>());
			}
			if (channelMultiBest.get(room.getChannel()).get(room.getRoomNumber()) == null)
			{
				if (log.isDebugEnabled())
					log.debug("findBestRooms::channelMultiBest::channel " + room.getChannel() + " room number " + room.getRoomNumber() + " empty");
				channelMultiBest.get(room.getChannel()).put(room.getRoomNumber(), room);
			}
			else
			{
				if (channelMultiBest.get(room.getChannel()).get(room.getRoomNumber()).getTotalRate().getAmount().compareTo(room.getTotalRate().getAmount()) > 0)
				{
					if (log.isDebugEnabled())
						log.debug("findBestRooms::channelMultiBest::channel " + room.getChannel() + " room number " + room.getRoomNumber() + " room better price than "
								+ channelMultiBest.get(room.getChannel()).get(room.getRoomNumber()).getTotalRate().getAmount());
					channelMultiBest.get(room.getChannel()).put(room.getRoomNumber(), room);
				}
			}
		}
		BestRooms bestRooms = new BestRooms();
		bestRooms.bestRoom = bestRoom;
		bestRooms.channelBest = channelBest;
		bestRooms.multiBest = multiBest;
		bestRooms.channelMultiBest = new HashMap<>();
		if (log.isDebugEnabled())
			log.debug("findBestRooms::looking for channels with " + maxRoomNumber + " rooms");
		for (String channel : channelMultiBest.keySet())
		{
			Map<Integer, RoomResult> multiCBest = channelMultiBest.get(channel);
			if (multiCBest.size() == maxRoomNumber)
			{
				bestRooms.channelMultiBest.put(channel, multiCBest);
			}
			else
			{
				if (log.isDebugEnabled())
					log.debug("findBestRooms::channel " + channel + " only had " + multiCBest.size() + " rooms");
			}
		}
		if (log.isDebugEnabled())
			log.debug("findBestRooms::bestRooms=" + bestRooms);
		return bestRooms;
	}

	/**
	 * Used for both Default and List
	 */
	protected HotelBaseRS.Hotel mapHotelBaseRS(AccommodationResult accommodationResult, HotelBaseRQ rq, boolean isListView)
	{
		if (log.isDebugEnabled())
			log.debug("mapHotelBaseRS::enter for " + accommodationResult.getProperty().getCode() + ", with isListView=" + isListView);
		Optional<AccommodationRC> accommodationRCOpt = rcService.getAccommodationRC(rq.getClient(), accommodationResult.getProperty().getCode());
		if (!accommodationRCOpt.isPresent())
		{
			if (log.isDebugEnabled())
				log.debug("mapHotelBaseRS::no rich content, returning null");
			return null;
		}
		AccommodationRC accommodationRC = accommodationRCOpt.get();
		HotelBaseRS.Hotel hotel = new HotelBaseRS.Hotel();
		hotel.setId(accommodationRC.getCode());
		hotel.setName(accommodationRC.getAccommodationName());
		hotel.setAddress(accommodationRC.getAddress().getStreet());
		hotel.setCity(accommodationRC.getAddress().getCity());
		hotel.setCountryCode(accommodationRC.getAddress().getCountryCode());
		hotel.setHotelRating(accommodationRC.getRating());
		hotel.setCategory(accommodationRC.getCategory());
		if (accommodationRC.getRatingText() != null && accommodationRC.getRatingText().length() == 0)
			hotel.setHotelRatingDisplay(accommodationRC.getRatingText());
		else
		{
			try
			{
			String stars = starsFormat.format(accommodationRC.getRating()) + " stars";
			hotel.setHotelRatingDisplay(stars);
			}
			catch (Exception e)
			{
				if (log.isDebugEnabled())
					log.warn("mapHotelBaseRS::invalid star rating '" + accommodationRC.getRating() + "'");
			}
		}
		hotel.setShortDescription(accommodationRC.getIntroduction());
		if (accommodationRC.getAddress().getGeoCoordinates() != null)
		{
			hotel.setLatitude(accommodationRC.getAddress().getGeoCoordinates().getLatitude());
			hotel.setLongitude(accommodationRC.getAddress().getGeoCoordinates().getLongitude());
		}
		if (accommodationRC.getImageThumbnail() != null)
			hotel.setImage(yalagoImageHack(accommodationRC.getImageThumbnail().getImageURL()));
		else if (accommodationRC.getImages() != null && accommodationRC.getImages().size() > 0)
			hotel.setImage(yalagoImageHack(accommodationRC.getImages().first().getImageURL()));
		hotel.setCheck_in_date(rq.getCheckin().toString());
		hotel.setCheck_out_date(rq.getCheckout().toString());
		hotel.setDuration(Long.valueOf(java.time.temporal.ChronoUnit.DAYS.between(rq.getCheckin(), rq.getCheckout())).intValue());
		hotel.setPhone(accommodationRC.getPhone());
		hotel.setDistance(accommodationResult.getDistancefromCentrepoint());
		hotel.setOleryData(accommodationResult.getProperty().getOleryData());

		// find cheapest room
		BestRooms bestRooms = findBestRooms(accommodationResult.getRooms());
		if (bestRooms == null)
		{
			if (log.isDebugEnabled())
				log.debug("mapHotelBaseRS::no best room");
			return null;
		}
		RoomResult bestRoom = bestRooms.bestRoom;
		hotel.setCurrency(bestRoom.getSupplyRate().getCurrencyId());
		hotel.setPrice(bestRoom.getSupplyRate().getAmount());
		hotel.setRetail_price(bestRoom.getTotalRate().getAmount());
		hotel.setProvider(bestRoom.getChannel());
		hotel.setHotel_refundable_status(!Functions.isAccommodationCNXNonRefundable(bestRoom.getCancellationPolicy()));
		if (log.isDebugEnabled())
			log.debug("mapHotelBaseRS::refundable=" + hotel.getHotel_refundable_status());
		hotel.setHotel_category(bestRoom.getRoomStandard());
		hotel.setMeal_plan("FB"); // TODO
		hotel.setFacilities(Functions.flattenFacilityGroups(accommodationRC.getFacilityGroups()));
		HotelDefaultRS.Hotel.Room besteRoom = mapRoom(bestRoom, accommodationResult, rq.getTravellers(), rq.getCheckin());
		if (log.isDebugEnabled())
			log.debug("mapHotelBaseRS::mapped room=" + besteRoom);
		hotel.setSelectedRoom(besteRoom);
		if (isListView)
		{
			if (log.isDebugEnabled())
				log.debug("mapHotelBaseRS::list view processing");
			for (Map<Integer, RoomResult> channelRooms : bestRooms.channelMultiBest.values())
			{
				for (RoomResult room : channelRooms.values())
				{
					HotelDefaultRS.Hotel.Room r = mapRoom(room, accommodationResult, rq.getTravellers(), rq.getCheckin());
					hotel.getSelectedRooms().add(r);
					if (log.isDebugEnabled())
						log.debug("mapHotelBaseRS::channelMultiBestRoom:" + room + ", adding " + r);
				}
			}
			for (Map.Entry<Integer, RoomResult> roomE : bestRooms.multiBest.entrySet())
			{
				// HotelDefaultRS.Hotel.Room r = mapRoom(roomE.getValue(), accommodationResult, rq.getTravellers(),
				// rq.getCheckin());
				if (hotel.getTotal_net_price() == null)
				{
					hotel.setTotal_net_price(roomE.getValue().getSupplyRate());
				}
				else
				{
					hotel.setTotal_net_price(new CurrencyValue(hotel.getTotal_net_price().getCurrencyId(), hotel.getTotal_net_price().getAmount().add(roomE.getValue().getSupplyRate().getAmount())));
				}
				if (hotel.getTotal_retail_price() == null)
				{
					hotel.setTotal_retail_price(roomE.getValue().getTotalRate());
				}
				else
				{
					hotel.setTotal_retail_price(
							new CurrencyValue(hotel.getTotal_retail_price().getCurrencyId(), hotel.getTotal_retail_price().getAmount().add(roomE.getValue().getTotalRate().getAmount())));
				}
				if (log.isDebugEnabled())
					log.debug("mapHotelBaseRS::multiBest:room#" + roomE.getKey() + " room " + roomE.getValue() + ", totalRetail=" + hotel.getTotal_retail_price());
			}
			if (log.isDebugEnabled())
				log.debug("mapHotelBaseRS::totalRetail=" + hotel.getTotal_retail_price());
		}
		else
		{
			for (RoomResult room : bestRooms.multiBest.values())
			{
				HotelDefaultRS.Hotel.Room r = mapRoom(room, accommodationResult, rq.getTravellers(), rq.getCheckin());
				hotel.getSelectedRooms().add(r);
				if (hotel.getTotal_net_price() == null)
				{
					hotel.setTotal_net_price(r.getNet_price());
				}
				else
				{
					hotel.setTotal_net_price(new CurrencyValue(hotel.getTotal_net_price().getCurrencyId(), hotel.getTotal_net_price().getAmount().add(r.getNet_price().getAmount())));
				}
				if (hotel.getTotal_retail_price() == null)
				{
					hotel.setTotal_retail_price(r.getRetail_price());
				}
				else
				{
					hotel.setTotal_retail_price(new CurrencyValue(hotel.getTotal_retail_price().getCurrencyId(), hotel.getTotal_retail_price().getAmount().add(r.getRetail_price().getAmount())));
				}
				if (log.isDebugEnabled())
					log.debug("mapHotelBaseRS::multiBest:" + room + ", adding " + r + ", totalRetail=" + hotel.getTotal_retail_price());
			}
		}
		if (log.isDebugEnabled())
			log.debug("mapHotelBaseRS::totalRetail=" + hotel.getTotal_retail_price());
		return hotel;
	}

	protected HotelDefaultRS.Hotel.Room mapRoom(RoomResult room, AccommodationResult accommodationResult, List<TravellerMix> travellers, LocalDate checkinDate)
	{
		if (log.isDebugEnabled())
			log.debug("mapRoom::enter for " + room);
		HotelDefaultRS.Hotel.Room r = new HotelDefaultRS.Hotel.Room();
		r.setRoomNumber(room.getRoomNumber());
		r.setPrice(room.getTotalRate());
		r.setPackageRate(room.getBundlesOnly());
		r.setRetail_price(room.getTotalRate());
		r.setBarRate(room.getTotalRate());
		r.setNetPrice(room.getSupplyRate());
		r.setNet_price(room.getSupplyRate());
		HotelDefaultRS.Hotel.Provider provider = new HotelDefaultRS.Hotel.Provider();
		provider.setId(room.getChannel());
		provider.setName(room.getChannel());
		r.getProviders().add(provider);
		r.setCode(room.getBookingCode());
		r.setName(room.getRoomName());
		if ( room.getPromotions() != null )
		{
			for (RoomPromotion roomPromotion : room.getPromotions())
			{
				r.getSpecialOffers().add(roomPromotion.getShortMarketingText());
			}
		}
		HotelDefaultRS.Hotel.RoomItem roomItem = new HotelDefaultRS.Hotel.RoomItem();
		roomItem.setName(room.getRoomName());
		roomItem.setCategory(room.getRoomStandard());
		roomItem.setRoomExtraInformation(makeERoamExtraRoomInformation(room));
		if (log.isDebugEnabled())
			log.debug("mapRoom::category=" + roomItem.getCategory());
		roomItem.setBedding(room.getBedding());
		roomItem.setBoard(room.getBoardCode());
		roomItem.setBoardDescription(room.getBoardDescription());
		roomItem.setHotelId(room.getChannelPropertyCode());
		TravellerMix travellerDetail = travellers.get(room.getRoomNumber() - 1);
		HotelDefaultRS.Hotel.Passengers pax = new HotelDefaultRS.Hotel.Passengers();
		pax.setAdults(travellerDetail.getAdultCount());
		pax.setChildren(travellerDetail.getChildAges());
		roomItem.setPax(pax);
		HotelDefaultRS.Hotel.Quantity quantity = new HotelDefaultRS.Hotel.Quantity();
		quantity.setMin(1);
		// quantity.setMax(room.getInventory().intValue());
		roomItem.setQuantity(quantity);
		roomItem.setDetailsAvailable(true);
		roomItem.setMatchCode(room.getMatchCode());
		r.getItems().add(roomItem);

		HotelDefaultRS.Hotel.RoomCancellation roomCancellation = new HotelDefaultRS.Hotel.RoomCancellation();
		if (Functions.isAccommodationCNXNonRefundable(room.getCancellationPolicy()))
		{
			roomCancellation.setType("non-refundable");
		}
		else
		{
			roomCancellation.setType("fully-refundable");
		}
		roomCancellation.setCancellationPolicyText(room.getCancellationPolicyText());
		LocalDate lastAsOf = LocalDate.now();
		HotelDefaultRS.Hotel.RoomCancellationFrame lastFrame = null;
		for (RoomCancellationPolicyLine roomCancellationPolicyLine : room.getCancellationPolicy())
		{
			HotelDefaultRS.Hotel.RoomCancellationFrame frame = new HotelDefaultRS.Hotel.RoomCancellationFrame();
			if (roomCancellationPolicyLine.getBefore())
			{
				frame.setFrom(formatCancellationDate(lastAsOf));
				frame.setTo(formatCancellationDate(roomCancellationPolicyLine.getAsOf()));
			}
			else
			{
				frame.setFrom(formatCancellationDate(roomCancellationPolicyLine.getAsOf()));
			}
			if (lastFrame != null && lastFrame.getTo() == null)
			{
				if (log.isDebugEnabled())
					log.debug("mapRoom::fixing up last frame TO date to " + roomCancellationPolicyLine.getAsOf());
				lastFrame.setTo(formatCancellationDate(roomCancellationPolicyLine.getAsOf()));
			}
			if (roomCancellationPolicyLine.getPenalty() != null)
				frame.setPenalty(new CurrencyValue(roomCancellationPolicyLine.getPenalty().getCurrencyId(), roomCancellationPolicyLine.getPenalty().getAmount()));
			lastFrame = frame;
			lastAsOf = roomCancellationPolicyLine.getAsOf();
			roomCancellation.getFrames().add(frame);
		}
		if (lastFrame != null && lastFrame.getTo() == null)
		{
			if (log.isDebugEnabled())
				log.debug("mapRoom::fixing up final last frame TO date to " + checkinDate);
			lastFrame.setTo(formatCancellationDate(checkinDate));
		}
		if (log.isDebugEnabled())
			log.debug("mapRoom::cnx policy=" + room.getCancellationPolicy() + ", generated=" + roomCancellation.getFrames());

		r.setCancellation(roomCancellation);
		r.setBookingConditions(room.getBookingConditions());
		return r;
	}

	private String makeERoamExtraRoomInformation(RoomResult room)
	{
		StringBuffer buf = new StringBuffer();

		if ( room.getPromotions() != null)
		{
			for (RoomPromotion promo : room.getPromotions())
			{
				if (buf.length() > 0)
					buf.append(". ");
				buf.append(promo.getShortMarketingText());
				if (promo.getCustomerFulfillmentRequirements() != null && promo.getCustomerFulfillmentRequirements().length() > 0)
					buf.append(", " + promo.getCustomerFulfillmentRequirements());
				if (promo.getTermsAndConditions() != null && promo.getTermsAndConditions().length() > 0)
					buf.append(", " + promo.getTermsAndConditions());
			}
		}

		if ( room.getExtraFees() != null)
		{
			for (RoomExtraFee fee : room.getExtraFees())
			{
				if (buf.length() > 0)
					buf.append(". ");
				buf.append(fee.getDescription());
			}
		}

		if (room.getRoomExtraInformation() != null && room.getRoomExtraInformation().length() > 0)
		{
			if (buf.length() > 0)
				buf.append(". ");
			buf.append(room.getRoomExtraInformation());
		}
		String extraInformation = buf.toString();
		if (log.isDebugEnabled() && extraInformation.length() > 0)
			log.debug("makeERoamExtraRoomInformation::roomExtraInformation=" + extraInformation);

		return extraInformation;
	}

	// one call for each roomNumber ...
	class RateCheckCallableService implements Callable<Set<RoomResult>>
	{
		public RateCheckCallableService(Integer roomNumber, Set<RoomResult> roomResultsIn, HotelBaseRQ hotelBaseRQ, String hotelId)
		{
			super();
			log.debug("CallableService::enter with roomNumber " + roomNumber);
			this.roomResultsIn = roomResultsIn;
			this.hotelBaseRQ = hotelBaseRQ;
			this.hotelId = hotelId;
			this.roomNumber = roomNumber;
		}

		public Set<RoomResult> call() throws Exception
		{
			if (log.isDebugEnabled())
				log.debug("call::enter for hotel " + hotelId);
			Set<RoomResult> rateCheckedRooms = new HashSet<>();
			try
			{
				AccommodationRateCheckRQ accommodationRateCheckRQ = null;
				for (RoomResult roomResultIn : roomResultsIn)
				{
					if (accommodationRateCheckRQ == null)
					{
						accommodationRateCheckRQ = new AccommodationRateCheckRQ();
						accommodationRateCheckRQ.setCheckin(hotelBaseRQ.getCheckin());
						accommodationRateCheckRQ.setCheckout(hotelBaseRQ.getCheckout());
						accommodationRateCheckRQ.setCountryCodeOfOrigin(hotelBaseRQ.getCountryCodeOfOrigin());
						accommodationRateCheckRQ.setTravellers(hotelBaseRQ.getTravellers());
						accommodationRateCheckRQ.setChannel(HotelbedsService.CHANNEL);
						accommodationRateCheckRQ.setHotelId(hotelId);
						accommodationRateCheckRQ.setBookingCodes(new ArrayList<>());
					}
					accommodationRateCheckRQ.getBookingCodes().add(roomResultIn.getRateCode());
				}
				if (log.isDebugEnabled())
					log.debug("call::calling roomNumber " + roomNumber + " with " + accommodationRateCheckRQ.getBookingCodes().size() + " keys : " + accommodationRateCheckRQ);
				RequestData<AccommodationRateCheckRQ> rqRateCheck = new RequestData<>(accommodationRateCheckRQ);
				rqRateCheck.setClient(hotelBaseRQ.getClient());
				ResponseData<AccommodationRateCheckRS> rsRateCheck = accommodationController.rateCheck(rqRateCheck);
				for (RoomResult r : rsRateCheck.getData().getRooms())
				{
					r.setRoomNumber(roomNumber);
					rateCheckedRooms.add(r);
				}
				if (log.isDebugEnabled())
					log.debug("call::for room number " + roomNumber + " and hotel " + hotelId + " returning " + rateCheckedRooms.size() + " rooms");
				return rateCheckedRooms;
			}
			catch (Exception e)
			{
				log.warn("call::call failed with " + e.toString(), e);
				return null;
			}
		}

		Set<RoomResult> roomResultsIn;

		HotelBaseRQ hotelBaseRQ;

		String hotelId;

		Integer roomNumber;
	}

	private void rateCheckHotelbeds(SortedSet<RoomResult> rooms, HotelBaseRQ hotelBaseRQ, String hotelId)
	{
		if (log.isDebugEnabled())
			log.debug("rateCheckHotelbeds::enter");
		long timer1 = System.currentTimeMillis();
		// if we have any Hotelbeds rooms, re-call them!
		List<RoomResult> rateCheckedRooms = new ArrayList<>();
		HashMap<Integer, Set<RoomResult>> roomResultsByRoomNumber = new HashMap<>();
		for (RoomResult roomResult : rooms)
		{
			if (roomResult.getChannel().equals(HotelbedsService.CHANNEL))
			{
				if ( roomResultsByRoomNumber.get(roomResult.getRoomNumber()) == null )
					roomResultsByRoomNumber.put(roomResult.getRoomNumber(), new HashSet<>());
				roomResultsByRoomNumber.get(roomResult.getRoomNumber()).add(roomResult);
			}
		}
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool("public", 15);
		Set<Future<Set<RoomResult>>> futures = new HashSet<Future<Set<RoomResult>>>();
		for ( Entry<Integer, Set<RoomResult>> roomResults : roomResultsByRoomNumber.entrySet())
		{
			for ( RoomResult singleCheck : roomResults.getValue())
			{
				Set<RoomResult> roomResultsIn = new HashSet<>();
				roomResultsIn.add(singleCheck);
				RateCheckCallableService callableService = new RateCheckCallableService(roomResults.getKey(), roomResultsIn, hotelBaseRQ, hotelId);
				Future<Set<RoomResult>> f = threadPoolExecutor.submit(callableService);
				futures.add(f);
			}
			//RateCheckCallableService callableService = new RateCheckCallableService(roomResults.getKey(), roomResults.getValue(), hotelBaseRQ, hotelId);
			//Future<Set<RoomResult>> f = threadPoolExecutor.submit(callableService);
			//futures.add(f);
		}

		if (log.isDebugEnabled())
			log.debug("rateCheckHotelbeds::waiting on " + futures.size() + " futures");
		for (Future<Set<RoomResult>> f : futures)
		{
			try
			{
				if (log.isDebugEnabled())
					log.debug("rateCheckHotelbeds::waiting on future " + f);
				Set<RoomResult> results = f.get(15, TimeUnit.SECONDS);
				if (results != null)
				{
					if (log.isDebugEnabled())
						log.debug("rateCheckHotelbeds::adding " + results.size() + " results");
					rateCheckedRooms.addAll(results);
				}
			}
			catch (Exception e)
			{
				log.warn("rateCheckHotelbeds::search call threw exception " + e.toString(), e);
			}
		}
		threadPoolExecutor.shutdown();
		if (log.isDebugEnabled())
			log.debug("rateCheckHotelbeds::loaded total of  " + rateCheckedRooms.size() + " results");

		if (rateCheckedRooms.size() > 0)
		{
			List<RoomResult> roomsUpdated = rooms.stream().filter(r -> !r.getChannel().equals(HotelbedsService.CHANNEL)).collect(Collectors.toList());
			roomsUpdated.addAll(rateCheckedRooms);
			rooms.clear();
			rooms.addAll(roomsUpdated);
		}
		if (log.isDebugEnabled())
			log.debug("rateCheckHotelbeds::returning " + rooms.size() + " rooms in " + (System.currentTimeMillis() - timer1) + " millis");
	}

	private List<AccommodationResult> filterByStarRating(Collection<AccommodationResult> rs, BigDecimal starRating)
	{
		List<AccommodationResult> exactSet = new ArrayList<>();
		for (AccommodationResult accommodationResult : rs)
		{
			if (accommodationResult.getProperty().getRating() != null && accommodationResult.getProperty().getRating().compareTo(starRating) == 0)
			{
				exactSet.add(accommodationResult);
			}
		}
		if (exactSet.size() > 0)
			return exactSet;

		List<AccommodationResult> lessSet = new ArrayList<>();
		for (AccommodationResult accommodationResult : rs)
		{
			if (accommodationResult.getProperty().getRating().compareTo(starRating) <= 0)
			{
				lessSet.add(accommodationResult);
			}
		}
		return lessSet;
	}

	private String yalagoImageHack(String s)
	{
		// Yalago have a problem with their test images - the https://pp.images.dnatatravel.com/ei/6/8/9/9/0/5/1/12.jpg domain is
		// not signed! Replace on the fly with the real domain
		if (s != null && s.contains("pp.images.dnatatravel.com"))
			return s.replace("pp.images.dnatatravel.com", "images.dnatatravel.com");
		else
			return s;
	}

	private String formatCancellationDate(LocalDate d)
	{
		return d.format(cancellationDateFormat) + " 00:00:00";
	}

	private static NumberFormat starsFormat = new DecimalFormat("###.#");

	private static DateTimeFormatter cancellationDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
