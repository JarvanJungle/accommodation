package com.torkirion.eroam.microservice.accommodation.endpoint.ims;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;

import org.springframework.beans.BeanUtils;
//import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import com.torkirion.eroam.ims.apidomain.AccommodationContent;
import com.torkirion.eroam.ims.apidomain.Allocation;
import com.torkirion.eroam.ims.apidomain.Boards;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies;
import com.torkirion.eroam.ims.apidomain.Rates;
import com.torkirion.eroam.ims.apidomain.Roomtypes;
import com.torkirion.eroam.ims.apidomain.Seasons;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.BeforeCheckinAfterBooking;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.CancellationPolicy;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.CancellationPolicyLine;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.PenaltyType;
import com.torkirion.eroam.ims.apidomain.Rates.DOTWRate;
import com.torkirion.eroam.ims.apidomain.Rates.Rate;
import com.torkirion.eroam.ims.apidomain.Roomtypes.Roomtype;
import com.torkirion.eroam.ims.apidomain.Seasons.Season;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocationSummary;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationBoard;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRate;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRoomtype;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSale;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSpecial;
import com.torkirion.eroam.ims.datadomain.TransportSale;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation.AllocationKey;
import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.ims.services.MapperService;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ.AccommodationRequestItem;
import com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomPromotion.PromotionType;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRS;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityResult;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationChannelService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class IMSService implements AccommodationServiceIF
{
	//private AccommodationRCRepo accommodationRCRepo;

	private DataService dataService;

	private MapperService mapperService;

	public static final String CHANNEL = "LOCALIMS";

	public static final String CHANNEL_PREFIX = "IM";

	// a mapping from client => ( hotelId => rooms )
	private static Map<String,Map<String, Rates>> imsRates = Collections.synchronizedMap(new HashMap<>());

	private static Collection<IMSAccommodationSpecial> imsSpecials = Collections.synchronizedSet(new HashSet<>());

	@Data
	private static class AvailabilityRQ
	{
		@Data
		public static class Room
		{
			private Integer Adults;

			private List<Integer> childAges;
		}

		private List<Room> rooms = new ArrayList<>();

		private LocalDate checkInDate;

		private LocalDate checkOutDate;

		private List<String> hotelIds = new ArrayList<>();;

	}

	public List<AccommodationResult> searchByHotelId(AvailSearchByHotelIdRQDTO availSearchRQ)
	{
		log.info("searchByHotelId::search(AvailSearchByHotelIdRQDTO)=" + availSearchRQ);

		if (imsRates.size() == 0 || imsRates.get(availSearchRQ.getClient()) == null)
		{
			loadIMSRates(availSearchRQ.getClient());
			if (log.isDebugEnabled())
				log.debug("searchByHotelId::rates loaded");
		}

		AvailabilityRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);
		Map<String, Rates> clientRates = imsRates.get(availSearchRQ.getClient());
		for (String hotelId : availSearchRQ.getHotelIds())
		{
			if (log.isDebugEnabled())
				log.debug("searchByHotelId::test hotel " + hotelId);
			if (!IMSService.CHANNEL.equals(AccommodationChannelService.getChannelForHotelId(hotelId)))
			{
				if (log.isDebugEnabled())
					log.debug("searchByHotelId::not an IMS hotel:channel is " + AccommodationChannelService.getChannelForHotelId(hotelId));
				continue;
			}
			String channelHotelId = hotelId;
			if (channelHotelId.startsWith(IMSService.CHANNEL_PREFIX))
				channelHotelId = channelHotelId.substring(IMSService.CHANNEL_PREFIX.length());

			if (clientRates.get(channelHotelId) == null)
			{
				if (log.isDebugEnabled())
					log.debug("searchByHotelId::no rates for ");
				continue;
			}
			Optional<IMSAccommodationRCData> accommodationRCOpt = dataService.getAccommodationRCDataRepo().findById(channelHotelId);
			if (accommodationRCOpt.isPresent())
			{
				availabilityRQ.getHotelIds().add(channelHotelId);
			}
			else
			{
				log.error("searchByHotelId::accommodation " + hotelId + " not found");
			}
		}

		return search(availabilityRQ, availSearchRQ.getClient());
	}

	public List<AccommodationResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("searchByGeocordBox::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);

		if (imsRates.size() == 0 || imsRates.get(availSearchRQ.getClient()) == null)
		{
			loadIMSRates(availSearchRQ.getClient());
			if (log.isDebugEnabled())
				log.debug("searchByGeocordBox::rates loaded");
		}

		AvailabilityRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);
		List<IMSAccommodationRCData> boxedRC = dataService.getAccommodationRCDataRepo().findByGeobox(availSearchRQ.getNorthwest().getLatitude(), availSearchRQ.getNorthwest().getLongitude(),
				availSearchRQ.getSoutheast().getLatitude(), availSearchRQ.getSoutheast().getLongitude());

		if (log.isDebugEnabled())
			log.debug("searchByGeocordBox::found " + boxedRC.size() + " hotels");
		if ( boxedRC.size() > 0 )
		{
			for (IMSAccommodationRCData rc : boxedRC)
			{
				availabilityRQ.getHotelIds().add(rc.getHotelId());
			}
			return search(availabilityRQ, availSearchRQ.getClient());
		}
		log.warn("searchByGeocordBox::no hotels found for client " + availSearchRQ.getClient());
		return new ArrayList<>();
	}

	protected AvailabilityRQ makeAvailabilityRQ(AvailSearchRQDTO availSearchRQ)
	{
		if (log.isDebugEnabled())
			log.debug("makeAvailabilityRQ::entering for availSearchRQ=" + availSearchRQ);

		AvailabilityRQ availabilityRQ = new AvailabilityRQ();
		availabilityRQ.setCheckInDate(availSearchRQ.getCheckin());
		availabilityRQ.setCheckOutDate(availSearchRQ.getCheckout());
		for (TravellerMix travellers : availSearchRQ.getTravellers())
		{
			AvailabilityRQ.Room room = new AvailabilityRQ.Room();
			room.setAdults(travellers.getAdultCount());
			room.setChildAges(travellers.getChildAges());
			availabilityRQ.getRooms().add(room);
		}

		return availabilityRQ;
	}

	protected List<AccommodationResult> search(AvailabilityRQ availabilityRQ, String client)
	{
		if (log.isDebugEnabled())
			log.debug("search::entering:availabilityRQ=" + availabilityRQ);
		try
		{
			long timer1 = System.currentTimeMillis();
			List<AccommodationResult> results = new ArrayList<>();

			Set<Rates> targetHotels = new HashSet<>();
			for (String hotelId : availabilityRQ.getHotelIds())
			{
				Map<String, Rates> clientRates = imsRates.get(client);
				Rates rates = clientRates.get(hotelId);
				if (rates != null)
				{
					targetHotels.add(rates);
				}
				else
				{
					if (log.isDebugEnabled())
						log.debug("search::rates are null for '" + hotelId + "', making an empty result IF Olery is not zero");
					AccommodationRC accommodationRC = getAccommodationRC(hotelId);
					if ( accommodationRC.getOleryCompanyCode() != null && accommodationRC.getOleryCompanyCode()!= 0L )
					{
						if (log.isDebugEnabled())
							log.debug("search::making empty IMS RC entry");
						AccommodationResult accommodationResult = new AccommodationResult();
						accommodationResult.setProperty(new AccommodationProperty());
						BeanUtils.copyProperties(accommodationRC, accommodationResult.getProperty());
						accommodationResult.getProperty().setChannelCode(hotelId);
						accommodationResult.getProperty().setChannel(CHANNEL.toString());
						if (accommodationRC.getImageThumbnail() != null)
							accommodationResult.getProperty().setImageThumbnailUrl(accommodationRC.getImageThumbnail().getImageURL());
						results.add(accommodationResult);
					}
				}
			}

			for (Rates rates : targetHotels)
			{
				AccommodationResult accommodationResult = makeResult(rates, availabilityRQ);
				if (accommodationResult != null)
					results.add(accommodationResult);
			}
			log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
			if (log.isDebugEnabled())
				log.debug("search::returning " + results);
			return results;
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		return new ArrayList<>();
	}

	/**
	 * Given the rates for a hotel, return any results that match
	 * 
	 * @param hotelRates
	 * @return
	 */
	@Data
	private static class RoomRateStore
	{
		public RoomRateStore(String hotelId, Integer roomIdx, Integer rateId, Roomtypes.Roomtype roomtype, CancellationPolicies.CancellationPolicy policy, Boards.Board board, String description,
				String rateGroup, Boolean bundlesOnly)
		{
			super();
			this.hotelId = hotelId;
			this.roomIdx = roomIdx;
			this.roomtype = roomtype;
			this.policy = policy;
			this.board = board;
			this.bundlesOnly = bundlesOnly;
			this.description = description;
			this.rateGroup = rateGroup;
			this.rateId = rateId;
			nights = 0;
		}

		private String hotelId;

		private Integer roomIdx;

		private Integer nights;

		private Integer rateId;

		private Roomtypes.Roomtype roomtype;

		private CancellationPolicies.CancellationPolicy policy;

		private Boards.Board board;

		private String description;

		private String rateGroup;

		private Boolean bundlesOnly;

		private Boolean available = false;

		private Integer inventory;

		private CurrencyValue nett = null;

		private CurrencyValue rrp = null;

		private Set<RoomPromotion> promotions = new HashSet<>();

		public String getRoomRateStoreIdx()
		{
			if ( rateGroup != null && rateGroup.length() > 0)
				return hotelId.toString() + "-" + roomIdx.toString() + "-" + roomtype.getRoomtypeId().toString() + "-" + board.getBoardCode() + "-" + rateGroup;
			else
				return hotelId.toString() + "-" + roomIdx.toString() + "-" + roomtype.getRoomtypeId().toString() + "-" + board.getBoardCode() + "-" + description;
		}
	}

	protected AccommodationResult makeResult(Rates hotelRates, AvailabilityRQ availabilityRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("makeResult::entering for " + availabilityRQ + " and rates for hotel " + hotelRates.getHotelId());

		AccommodationRC accommodationRC = getAccommodationRC(hotelRates.getHotelId());
		IMSAccommodationRCData imsAccommodationRCData = getIMSAccommodationRC(hotelRates.getHotelId());
		
		int nightsStay = (int) ChronoUnit.DAYS.between(availabilityRQ.getCheckInDate(), availabilityRQ.getCheckOutDate());

		Map<String, RoomRateStore> roomRateStore = new HashMap<>();
		// loop through each room request
		int roomIdx = -1;
		roomRQLoop: for (com.torkirion.eroam.microservice.accommodation.endpoint.ims.IMSService.AvailabilityRQ.Room roomRQ : availabilityRQ.getRooms())
		{
			roomIdx++;
			// check paxmix suitability
			if (log.isDebugEnabled())
				log.debug("makeResult::roomRQLoop:roomIdx=" + roomIdx);

			dateLoop: for (LocalDate date = availabilityRQ.getCheckInDate(); date.isBefore(availabilityRQ.getCheckOutDate()); date = date.plusDays(1))
			{
				if (log.isDebugEnabled())
					log.debug("makeResult::dateLoop:date=" + date);
				Map<String, RoomRateStore> roomRateDayStore = new HashMap<>();
				rateLoop: for (Rates.Rate rate : hotelRates.getRates())
				{
					if (log.isDebugEnabled())
						log.debug("makeResult::rateLoop:rate description=" + rate.getDescription());
					Roomtype roomType = rate.getRoomType();
					if ( roomType == null )
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::roomType is null, continuing");
						continue;
					}
					if ( rate.getSeason() == null  )
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::season is null, continuing");
						continue;
					}
					if ( rate.getPolicy() == null  )
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::policy is null, continuing");
						continue;
					}
					if ( rate.getBoard() == null  )
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::board is null, continuing");
						continue;
					}
					if ( rate.getAllocation() == null )
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::rate allocation is null, continuing");
						continue rateLoop;
					}
					int numAdults = roomRQ.getAdults();
					int numChildren = 0;
					int numInfants = 0;
					if ( roomRQ.getChildAges() != null )
					{
						for ( Integer age : roomRQ.getChildAges())
						{
							if ( age < imsAccommodationRCData.getInfantAge())
								numInfants++;
							else if ( age < imsAccommodationRCData.getChildAge())
								numChildren++;
							else
								numAdults++;
						}
					}
					if (log.isDebugEnabled())
						log.debug("makeResult::numAdults=" + numAdults + ", numChildren=" + numChildren + ", numInfants=" + numInfants);
					if (numAdults > roomType.getMaximumAdults())
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::adults " + numAdults + " > " + roomType.getMaximumAdults());
						continue rateLoop;
					}
					if (numAdults + numChildren + numInfants > roomType.getMaximumPeople())
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::pax " + (numAdults + numChildren + numInfants) + " > " + roomType.getMaximumPeople());
						continue rateLoop;
					}
					if (date.isBefore(rate.getSeason().getSeasonStartDate()) || date.isAfter(rate.getSeason().getSeasonEndDate()))
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::bad date " + date + ",season is " + rate.getSeason().getSeasonStartDate() + " to " + rate.getSeason().getSeasonEndDate());
						continue rateLoop;
					}
					if (rate.getMinimumNights() != null && rate.getMinimumNights().intValue() > 0 && rate.getMinimumNights() > nightsStay)
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::minimum nights restriction " + rate.getMinimumNights() + " is greater than stay " + nightsStay);
						continue rateLoop;
					}
					NettAndRRPCurrencyValue nettAndRRPCurrencyValue = calculateDayRoomPrice(availabilityRQ, rate, date, numAdults, numChildren, numInfants);
					if (nettAndRRPCurrencyValue == null || nettAndRRPCurrencyValue.getNett() == null || nettAndRRPCurrencyValue.getNett().getAmount().compareTo(BigDecimal.ZERO) == 0)
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::nettAndRRPCurrencyValue=" + nettAndRRPCurrencyValue);
						continue rateLoop;
					}
					Integer allocation = rate.getAllocation().getAllocationMap().get(date);
					if (allocation == null || allocation == 0)
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::allocation=" + allocation);
						continue rateLoop;
					}
					
					// look up if we've hit this rateDesc or rateGroup already for this day
					String room_name = roomType.getDescription().equals(rate.getDescription()) ? roomType.getDescription() : roomType.getDescription() + ", " + rate.getDescription();
					if (log.isDebugEnabled())
						log.debug("makeResult::working on room_name=" + room_name + " rateGroup " + rate.getRateGroup());
					RoomRateStore roomDayRateCheck = new RoomRateStore(rate.getHotelId(), roomIdx, rate.getRateId(), rate.getRoomType(), rate.getPolicy(), rate.getBoard(), room_name, rate.getRateGroup(), rate.getBundlesOnly());
					RoomRateStore roomDayRate = roomRateDayStore.get(roomDayRateCheck.getRoomRateStoreIdx());
					if ( roomDayRate == null )
					{
						roomDayRateCheck.setNett(nettAndRRPCurrencyValue.getNett());
						roomDayRateCheck.setRrp(nettAndRRPCurrencyValue.getRrp());
						roomDayRateCheck.setInventory(allocation);
						roomRateDayStore.put(roomDayRateCheck.getRoomRateStoreIdx(), roomDayRateCheck);
						if (log.isDebugEnabled())
							log.debug("makeResult::saving new roomRateDayStore for " + roomDayRateCheck.getRoomRateStoreIdx());
					}
					else
					{
						if ( roomDayRate.getNett().getAmount().compareTo(nettAndRRPCurrencyValue.getNett().getAmount()) < 0 )
						{
							if (log.isDebugEnabled())
								log.debug("makeResult::existing rate on this day " + roomDayRate + " less than this, bypassing");
							continue;
						}
						else
						{
							roomDayRateCheck.setNett(nettAndRRPCurrencyValue.getNett());
							roomDayRateCheck.setRrp(nettAndRRPCurrencyValue.getRrp());
							roomDayRateCheck.setInventory(allocation);
							roomRateDayStore.put(roomDayRateCheck.getRoomRateStoreIdx(), roomDayRateCheck);
							if (log.isDebugEnabled())
								log.debug("makeResult::existing rate on this day " + roomDayRate + " more than this, updating");
						}
					}
				}
				// we've now looped through all the rates for this date.  Try and add them to the totals ...
				if (log.isDebugEnabled())
					log.debug("makeResult::looped through all rates for this day (" + date + "), add to totals");
				for ( RoomRateStore roomDayRate: roomRateDayStore.values())
				{
					
					if (roomRateStore.get(roomDayRate.getRoomRateStoreIdx()) == null)
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::working on new roomRateIdx '" + roomDayRate.getRoomRateStoreIdx() + "'");
						roomRateStore.put(roomDayRate.getRoomRateStoreIdx(), roomDayRate);
						roomDayRate.setAvailable(true);
						roomDayRate.setNights(1);
						if (log.isDebugEnabled())
							log.debug("makeResult::roomRateIdx total " + roomDayRate.getNett());
					}
					else
					{
						if (log.isDebugEnabled())
							log.debug("makeResult::working on existing roomRateIdx '" + roomDayRate.getRoomRateStoreIdx() + "'");
						RoomRateStore roomRate = roomRateStore.get(roomDayRate.getRoomRateStoreIdx());
						roomRate = roomRateStore.get(roomDayRate.getRoomRateStoreIdx());
						roomRate.setNett(roomRate.getNett().add(roomDayRate.getNett()));
						roomRate.setRrp(roomRate.getRrp().add(roomDayRate.getRrp()));
						roomRate.setNights(roomRate.getNights() + 1);
						if ( roomDayRate.getInventory() < roomRate.getInventory())
							roomRate.setInventory(roomDayRate.getInventory());
						if ( comparePolicies(roomRate.getPolicy(), roomDayRate.getPolicy()))
							roomRate.setPolicy(roomDayRate.getPolicy());
						if (log.isDebugEnabled())
							log.debug("makeResult::roomRateIdx total now " + roomRate.getNett());
					}
				}
			}
		}
		// if multi-room, make sure we have enough of same rooms to satisfy
		// TODO multiroom check

		if (log.isDebugEnabled())
			log.debug("makeResult::roomRateStore.size=" + roomRateStore.size());

		AccommodationResult accommodationResult = new AccommodationResult();
		for (RoomRateStore roomRate : roomRateStore.values())
		{
			if (log.isDebugEnabled())
				log.debug("makeResult::looped for roomRateIdx '" + roomRate.getRoomRateStoreIdx() + "'");
			
			applySpecials(roomRate, availabilityRQ);

			if (log.isDebugEnabled())
				log.debug("makeResult::roomRate= " + roomRate);
			if (!roomRate.getAvailable())
			{
				if (log.isDebugEnabled())
					log.debug("makeResult::room not available, bypassing");
				continue;
			}
			if (roomRate.getNights().intValue() != nightsStay)
			{
				if (log.isDebugEnabled())
					log.debug("makeResult::room not available on every night (found " + roomRate.getNights() + ", required " + nightsStay + ", bypassing");
				continue;
			}

			com.torkirion.eroam.microservice.accommodation.apidomain.RoomResult room = new com.torkirion.eroam.microservice.accommodation.apidomain.RoomResult();

			room.setChannel(IMSService.CHANNEL);
			room.setChannelPropertyCode(CHANNEL_PREFIX + hotelRates.getHotelId());
			room.setRoomName(roomRate.getDescription());
			room.setRoomNumber(roomRate.getRoomIdx() + 1);
			room.setRoomCode(roomRate.getRoomtype().getRoomtypeId().toString());
			room.setRateCode(roomRate.getRateId().toString());
			room.setMatchCode(roomRate.getRateId().toString());
			room.setBookingCode(roomRate.getRateId().toString());
			room.setBoardCode(roomRate.getBoard().getBoardCode());
			room.setBoardDescription(roomRate.getBoard().getBoardDescription());
			room.setBedding(roomRate.getRoomtype().getBeddingDescription());
			room.setRoomStandard(roomRate.getRoomtype().getRoomSize());
			room.setTotalRate(roomRate.getRrp());
			room.setSupplyRate(roomRate.getNett());
			room.setRrpIsMandatory(roomRate.getRrp().getAmount().compareTo(BigDecimal.ZERO) > 0);
			room.setRequiresRecheck(false);
			room.setBundlesOnly(roomRate.getBundlesOnly());
			room.setInventory(BigInteger.valueOf(roomRate.getInventory()));
			room.setPromotions(roomRate.getPromotions());
			room.setCancellationPolicy(makeCancellationPolicy(roomRate.getPolicy(), availabilityRQ.getCheckInDate(), roomRate.getNett().getCurrencyId(), BigDecimal.ZERO));
			room.setCancellationPolicyText(makeCancellationPolicytext(room.getCancellationPolicy()));
			room.setBookingConditions(room.getBookingConditions());

			if (log.isDebugEnabled())
				log.debug("makeResult::add room " + room);
			accommodationResult.getRooms().add(room);
		}
		if (accommodationResult.getRooms() == null || accommodationResult.getRooms().isEmpty())
		{
			if (log.isDebugEnabled())
				log.debug("makeResult::no rooms");
			return null;
		}
		accommodationResult = checkSupplyRate(accommodationResult);
		if(accommodationResult==null){
			if (log.isDebugEnabled())
				log.debug("makeResult::no supply rate");
			return null;
		}
		accommodationResult.setProperty(new AccommodationProperty());
		BeanUtils.copyProperties(accommodationRC, accommodationResult.getProperty());
		accommodationResult.getProperty().setChannelCode(hotelRates.getHotelId());
		accommodationResult.getProperty().setChannel(CHANNEL.toString());
		if (accommodationRC.getImageThumbnail() != null)
			accommodationResult.getProperty().setImageThumbnailUrl(accommodationRC.getImageThumbnail().getImageURL());

		if (log.isDebugEnabled())
			log.debug("makeResult::returning result for " + accommodationResult.getProperty().getCode() + " with " + accommodationResult.getRooms().size() + " rooms");
		return accommodationResult;
	}

	protected void applySpecials(RoomRateStore roomRate, AvailabilityRQ availabilityRQ)
	{
		if (log.isDebugEnabled())
			log.debug("applySpecials::entering");

		BigDecimal adjustmentNett = BigDecimal.ZERO;
		BigDecimal adjustmentGross = BigDecimal.ZERO;
		String description = null;

		LocalDate now = LocalDate.now();
		for (IMSAccommodationSpecial special : imsSpecials)
		{
			if (!special.getHotelId().equals(roomRate.getHotelId()))
				continue;
			if (special.getRateId() == null || !special.getRateId().equals(roomRate.getRateId()))
				if ( special.getRateIds() != null && !special.getRateIds().contains("," + roomRate.getRateId() + ","))
				{
					if (log.isDebugEnabled())
						log.debug("applySpecials::rate check failed on ," + roomRate.getRateId() + ", in " + special.getRateId() + " or " + special.getRateIds());
					continue;
				}
			if (special.getCheckinFrom() != null && special.getCheckinTo() != null)
			{
				if (availabilityRQ.getCheckInDate().isBefore(special.getCheckinFrom()) || availabilityRQ.getCheckInDate().isAfter(special.getCheckinTo()))
				{
					if (log.isDebugEnabled())
						log.debug("applySpecials::filter out on checkinDate, " + availabilityRQ.getCheckInDate() + " not between " + special.getCheckinFrom() + " and " + special.getCheckinTo());
					continue;
				}
			}
			if (special.getBookFrom() != null && special.getBookTo() != null)
			{
				if (now.isBefore(special.getBookFrom()) || now.isAfter(special.getBookTo()))
				{
					if (log.isDebugEnabled())
						log.debug("applySpecials::filter out on bookingDate, " + now + " not between " + special.getBookFrom() + " and " + special.getBookTo());
					continue;
				}
			}
			if (special.getBookFrom() != null && special.getBookTo() != null)
			{
				if (now.isBefore(special.getBookFrom()) || now.isAfter(special.getBookTo()))
				{
					if (log.isDebugEnabled())
						log.debug("applySpecials::filter out on bookingDate, " + now + " not between " + special.getBookFrom() + " and " + special.getBookTo());
					continue;
				}
			}
			int daysInAdvance = (int) ChronoUnit.DAYS.between(now, availabilityRQ.getCheckInDate());
			if (special.getDaysInAdvanceLess() != null && special.getDaysInAdvanceLess().intValue() != 0)
			{
				if (daysInAdvance >= special.getDaysInAdvanceLess().intValue())
				{
					if (log.isDebugEnabled())
						log.debug("applySpecials::filter out on daysInAdvanceLess, " + daysInAdvance + " not less than " + special.getDaysInAdvanceLess());
					continue;
				}
			}
			if (special.getDaysInAdvanceMore() != null && special.getDaysInAdvanceMore().intValue() != 0)
			{
				if (daysInAdvance <= special.getDaysInAdvanceMore().intValue())
				{
					if (log.isDebugEnabled())
						log.debug("applySpecials::filter out on daysInAdvanceMore, " + daysInAdvance + " not More than " + special.getDaysInAdvanceMore());
					continue;
				}
			}
			int nightsStay = (int) ChronoUnit.DAYS.between(availabilityRQ.getCheckInDate(), availabilityRQ.getCheckOutDate());
			if (special.getMinimumStay() != null && special.getMinimumStay().intValue() != 0)
			{
				if (nightsStay < special.getMinimumStay().intValue())
				{
					if (log.isDebugEnabled())
						log.debug("applySpecials::filter out on minimumStay, " + nightsStay + " not more or equal than " + special.getMinimumStay());
					continue;
				}
			}
			// we have a winner!
			BigDecimal thisAdjustmentNett = BigDecimal.ZERO;
			BigDecimal thisAdjustmentGross = BigDecimal.ZERO;
			if (special.getAdjustPercentage() != null && special.getAdjustPercentage().compareTo(BigDecimal.ZERO) != 0)
			{
				thisAdjustmentNett = roomRate.nett.getAmount().multiply(special.getAdjustPercentage()).divide(BD_100, 2, RoundingMode.HALF_DOWN).multiply(BD_M1);
				thisAdjustmentGross = roomRate.rrp.getAmount().multiply(special.getAdjustPercentage()).divide(BD_100, 2, RoundingMode.HALF_DOWN).multiply(BD_M1);
			}
			else if (special.getAdjustValue() != null && special.getAdjustValue().compareTo(BigDecimal.ZERO) != 0)
			{
				thisAdjustmentNett = special.getAdjustValue().multiply(BD_M1);
				thisAdjustmentGross = special.getAdjustValue().multiply(BD_M1);
			}
			else if (special.getFreeNights() != null && special.getFreeNights().intValue() != 0)
			{
				BigDecimal adjustmentPercent = BigDecimal.valueOf(special.getFreeNights()).divide(BigDecimal.valueOf(nightsStay), 5, RoundingMode.HALF_DOWN);
				thisAdjustmentNett = roomRate.nett.getAmount().multiply(adjustmentPercent).multiply(BD_M1).setScale(2);
				thisAdjustmentGross = roomRate.rrp.getAmount().multiply(adjustmentPercent).multiply(BD_M1).setScale(2);
			}
			if (log.isDebugEnabled())
				log.debug("applySpecials::special " + special.getId() + ", adjustment to nett/gross is " + thisAdjustmentNett + "/" + thisAdjustmentGross);
			if (thisAdjustmentNett.compareTo(adjustmentNett) < 0)
			{
				adjustmentNett = thisAdjustmentNett;
				adjustmentGross = thisAdjustmentGross;
				description = special.getDescription();
			}
		}
		if (adjustmentNett.compareTo(BigDecimal.ZERO) != 0)
		{
			if (log.isDebugEnabled())
				log.debug("applySpecials::adjusting nett and gross, description = " + description);
			roomRate.setNett(new CurrencyValue(roomRate.getNett().getCurrencyId(), roomRate.getNett().getAmount().add(adjustmentNett)));
			roomRate.setRrp(new CurrencyValue(roomRate.getRrp().getCurrencyId(), roomRate.getRrp().getAmount().add(adjustmentGross)));
			if (description != null && description.length() > 0)
			{
				RoomPromotion roomPromo = new RoomPromotion();
				roomPromo.setPromoType(PromotionType.DISCOUNT_OFFER);
				roomPromo.setShortMarketingText(description);
				roomRate.getPromotions().add(roomPromo);
			}
		}
	}

	protected SortedSet<RoomCancellationPolicyLine> makeCancellationPolicy(CancellationPolicies.CancellationPolicy imsPolicy, LocalDate checkinDate, String currencyId, BigDecimal averageNightlyFee)
	{
		if (log.isDebugEnabled())
			log.debug("makeCancellationPolicy::entering");
		SortedSet<RoomCancellationPolicyLine> policy = new TreeSet<>();

		for (CancellationPolicyLine imsLine : imsPolicy.getLines())
		{
			RoomCancellationPolicyLine roomCancellationPolicyLine = new RoomCancellationPolicyLine();
			roomCancellationPolicyLine.setBefore(false);
			StringBuffer penaltyDescription = new StringBuffer();
			LocalDate asOf = null;
			if (imsLine.getPenaltyType().equals(PenaltyType.PERCENTAGE))
			{
				roomCancellationPolicyLine.setPenaltyPercent(imsLine.getPenalty());
				if (imsLine.getPenalty().compareTo(BD_100) == 0)
					penaltyDescription.append("Item is non-refundable");
				else if (imsLine.getPenalty().compareTo(BigDecimal.ZERO) == 0)
					penaltyDescription.append("Item is fully refundable");
				else
					penaltyDescription.append("A charge of " + imsLine.getPenalty() + "% applies");
			}
			if (imsLine.getPenaltyType().equals(PenaltyType.DOLLAR_VALUE))
			{
				roomCancellationPolicyLine.setPenalty(new CurrencyValue(currencyId, imsLine.getPenalty()));
				if (imsLine.getPenalty().compareTo(BigDecimal.ZERO) == 0)
					penaltyDescription.append("Item is fully refundable");
				else
					penaltyDescription.append("A charge of " + roomCancellationPolicyLine.getPenalty() + " applies");
			}
			if (imsLine.getPenaltyType().equals(PenaltyType.NUMBER_OF_NIGHTS))
			{
				roomCancellationPolicyLine.setPenalty(new CurrencyValue(currencyId, averageNightlyFee.multiply(imsLine.getPenalty())));
				penaltyDescription.append("A charge of " + imsLine.getPenalty() + (imsLine.getPenalty().compareTo(BigDecimal.ONE) == 0 ? " night" : " nights") + " applies");
			}
			if (imsLine.getBeforeCheckinAfterBooking().equals(BeforeCheckinAfterBooking.AFTER_BOOKING))
			{
				asOf = LocalDate.now().plusDays(imsLine.getNumberOfDays());
				if (imsLine.getNumberOfDays().intValue() == 0)
					penaltyDescription.append(".");
				else
					penaltyDescription.append(" if cancelled " + imsLine.getNumberOfDays() + " days after booking.");
			}
			else
			{
				asOf = checkinDate.minusDays(imsLine.getNumberOfDays());
				if (imsLine.getNumberOfDays().intValue() == 0)
					penaltyDescription.append(".");
				else
					penaltyDescription.append(" if cancelled " + imsLine.getNumberOfDays() + " days before checkin.");
			}
			roomCancellationPolicyLine.setAsOf(asOf);
			roomCancellationPolicyLine.setPenaltyDescription(penaltyDescription.toString());
			policy.add(roomCancellationPolicyLine);
		}
		return policy;
	}

	protected String makeCancellationPolicytext(SortedSet<RoomCancellationPolicyLine> policyLines)
	{
		StringBuffer generatedPolicyText = new StringBuffer();
		for (RoomCancellationPolicyLine line : policyLines)
		{
			generatedPolicyText.append(line.getPenaltyDescription() + " ");
		}
		return generatedPolicyText.toString().trim();
	}

	@Data
	private static class NettAndRRPCurrencyValue
	{
		CurrencyValue nett;

		CurrencyValue rrp;
	}

	protected NettAndRRPCurrencyValue calculateDayRoomPrice(AvailabilityRQ availabilityRQ, Rates.Rate rate, LocalDate date, int numAdults, int numChildren, int numInfants)
	{
		if (log.isDebugEnabled())
			log.debug("calculateDayRoomPrice::entering");

		DayOfWeek dayOfWeek = date.getDayOfWeek();
		for (DOTWRate dotwRate : rate.getDotwRates())
		{
			if (dayOfWeek == DayOfWeek.SUNDAY && !dotwRate.getDaysOfTheWeek().getSunday())
			{
				continue;
			}
			if (dayOfWeek == DayOfWeek.MONDAY && !dotwRate.getDaysOfTheWeek().getMonday())
			{
				continue;
			}
			if (dayOfWeek == DayOfWeek.TUESDAY && !dotwRate.getDaysOfTheWeek().getTuesday())
			{
				continue;
			}
			if (dayOfWeek == DayOfWeek.WEDNESDAY && !dotwRate.getDaysOfTheWeek().getWednesday())
			{
				continue;
			}
			if (dayOfWeek == DayOfWeek.THURSDAY && !dotwRate.getDaysOfTheWeek().getThursday())
			{
				continue;
			}
			if (dayOfWeek == DayOfWeek.FRIDAY && !dotwRate.getDaysOfTheWeek().getFriday())
			{
				continue;
			}
			if (dayOfWeek == DayOfWeek.SATURDAY && !dotwRate.getDaysOfTheWeek().getSaturday())
			{
				continue;
			}
			if (dotwRate.getPaxmixPricing())
			{
				if (log.isDebugEnabled())
					log.debug("calculateDayRoomPrice::passed DOTW check, has paxmix pricing");
				for (Rates.PaxmixRate paxmixRate : dotwRate.getPaxmixRates())
				{
					if (log.isDebugEnabled())
						log.debug("calculateDayRoomPrice::checking paxmixRate " + paxmixRate);
					if (paxmixRate.getNumberOfAdults() == numAdults && paxmixRate.getNumberOfChildren() == numChildren)
					{
						NettAndRRPCurrencyValue nettAndRRPCurrencyValue = new NettAndRRPCurrencyValue();
						nettAndRRPCurrencyValue.setNett(new CurrencyValue(paxmixRate.getNett().getCurrencyId(), paxmixRate.getNett().getAmount()));
						if (paxmixRate.getRrp() != null)
							nettAndRRPCurrencyValue.setRrp(new CurrencyValue(paxmixRate.getRrp().getCurrencyId(), paxmixRate.getRrp().getAmount()));
						if ( rate.getPerInfantSurcharge() != null && numInfants > 0)
						{
							BigDecimal totalInfantSurcharge = rate.getPerInfantSurcharge().multiply(new BigDecimal(numInfants));
							nettAndRRPCurrencyValue.getNett().setAmount(nettAndRRPCurrencyValue.getNett().getAmount().add(totalInfantSurcharge));
							if ( nettAndRRPCurrencyValue.getRrp() != null )
							{
								nettAndRRPCurrencyValue.getRrp().setAmount(nettAndRRPCurrencyValue.getRrp().getAmount().add(totalInfantSurcharge));
							}
						}
						if (log.isDebugEnabled())
							log.debug("calculateDayRoomPrice::passed DOTW check, matched paxmix pricing for adult and children, nettAndRRPCurrencyValue=" + nettAndRRPCurrencyValue);
						return nettAndRRPCurrencyValue;
					}
				}
			}
			else
			{
				NettAndRRPCurrencyValue nettAndRRPCurrencyValue = new NettAndRRPCurrencyValue();
				nettAndRRPCurrencyValue.setNett(new CurrencyValue(dotwRate.getNett().getCurrencyId(), dotwRate.getNett().getAmount()));
				if (dotwRate.getRrp() != null)
					nettAndRRPCurrencyValue.setRrp(new CurrencyValue(dotwRate.getRrp().getCurrencyId(), dotwRate.getRrp().getAmount()));
				if ( rate.getPerInfantSurcharge() != null && numInfants > 0)
				{
					BigDecimal totalInfantSurcharge = rate.getPerInfantSurcharge().multiply(new BigDecimal(numInfants));
					nettAndRRPCurrencyValue.getNett().setAmount(nettAndRRPCurrencyValue.getNett().getAmount().add(totalInfantSurcharge));
					if ( nettAndRRPCurrencyValue.getRrp() != null )
					{
						nettAndRRPCurrencyValue.getRrp().setAmount(nettAndRRPCurrencyValue.getRrp().getAmount().add(totalInfantSurcharge));
					}
				}
				if (log.isDebugEnabled())
					log.debug("calculateDayRoomPrice::passed DOTW check, has room pricing, nettAndRRPCurrencyValue=" + nettAndRRPCurrencyValue);
				return nettAndRRPCurrencyValue;
			}
		}
		if (log.isDebugEnabled())
			log.debug("calculateDayRoomPrice::rate not found, returning null");
		return null;
	}

	@Override
	public AccommodationRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck()::enter for " + rateCheckRQDTO);
		// we do this to make sure we have the right client schema
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(rateCheckRQDTO.getClient(), 1);
		Callable<AccommodationRateCheckRS> callableTask = () -> {
		    return rateCheckThreaded(rateCheckRQDTO);
		};
		Future<AccommodationRateCheckRS> future = threadPoolExecutor.submit(callableTask);
		AccommodationRateCheckRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}

	protected AccommodationRateCheckRS rateCheckThreaded(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter with " + rateCheckRQDTO);

		AvailSearchByHotelIdRQDTO availSearchRQ = new AvailSearchByHotelIdRQDTO();
		availSearchRQ.setHotelIds(new HashSet<>());
		availSearchRQ.getHotelIds().add(rateCheckRQDTO.getHotelId());
		availSearchRQ.getHotelIds().add(rateCheckRQDTO.getHotelId());
		availSearchRQ.setCountryCodeOfOrigin(rateCheckRQDTO.getCountryCodeOfOrigin());
		availSearchRQ.setCheckin(rateCheckRQDTO.getCheckin());
		availSearchRQ.setCheckout(rateCheckRQDTO.getCheckout());
		availSearchRQ.setTravellers(rateCheckRQDTO.getTravellers());
		availSearchRQ.setClient(rateCheckRQDTO.getClient());
		List<AccommodationResult> searchResult = searchByHotelId(availSearchRQ);
		AccommodationRateCheckRS accommodationRateCheckRS = new AccommodationRateCheckRS();
		for (AccommodationResult result : searchResult)
		{
			if (result.getProperty().getChannelCode().equals(rateCheckRQDTO.getHotelId()) || result.getProperty().getCode().equals(rateCheckRQDTO.getHotelId()))
			{
				accommodationRateCheckRS.setProperty(result.getProperty());
				accommodationRateCheckRS.setRooms(new ArrayList<>());
				for (com.torkirion.eroam.microservice.accommodation.apidomain.RoomResult room : result.getRooms())
				{
					if (log.isDebugEnabled())
						log.debug("rateCheck::checking room# " + room.getRoomNumber() + ", bookingCode " + room.getBookingCode() + " against " + rateCheckRQDTO.getBookingCodes());
					if (rateCheckRQDTO.getBookingCodes().contains(room.getBookingCode()))
					{
						if ( room.getRoomNumber().intValue() <= rateCheckRQDTO.getBookingCodes().size())
						{
							if ( rateCheckRQDTO.getBookingCodes().get(room.getRoomNumber().intValue() - 1).equals(room.getBookingCode()))
							{
								if (log.isDebugEnabled())
									log.debug("rateCheck::adding!");
								accommodationRateCheckRS.getRooms().add(room);
							}
						}
					}
				}
			}
		}
		return accommodationRateCheckRS;
	}

	@Override
	public AccommodationBookRS book(String client, AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book()::recevied " + bookRQ);
		Booker booker = this.new Booker();
		// we do this to make sure we have the right client schema
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<AccommodationBookRS> callableTask = () -> {
		    return booker.book(client, bookRQ);
		};
		Future<AccommodationBookRS> future = threadPoolExecutor.submit(callableTask);
		AccommodationBookRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	
	public class Booker
	{
		protected IMSService imsService;
		
		@Transactional
		public AccommodationBookRS book(String client, AccommodationBookRQ bookRQ) throws Exception
		{
			return bookThreaded(client, bookRQ);
		}
	}

	protected AccommodationBookRS bookThreaded(String client, AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);

		Date nowDate = new Date();
		AccommodationBookRS bookRS = new AccommodationBookRS();
		int itemNumber = 0;
		for (AccommodationRequestItem item : bookRQ.getItems())
		{
			// check room and price still avail by doing a rate check first
			RateCheckRQDTO rateCheckRQDTO = new RateCheckRQDTO();
			rateCheckRQDTO.setHotelId(item.getHotelID());
			rateCheckRQDTO.setBookingCodes(new ArrayList<>());
			rateCheckRQDTO.getBookingCodes().add(item.getBookingCode());
			rateCheckRQDTO.setCountryCodeOfOrigin("ANY");
			rateCheckRQDTO.setCheckin(item.getCheckin());
			rateCheckRQDTO.setCheckout(item.getCheckout());
			rateCheckRQDTO.setClient(client);
			rateCheckRQDTO.setChannel(CHANNEL);
			rateCheckRQDTO.setTravellers(new ArrayList<>());
			TravellerMix travellerMix = new TravellerMix();
			for (int travellerIndex : item.getTravellerIndex())
			{
				Traveller traveller = bookRQ.getTravellers().get(travellerIndex);
				if (traveller.getAge(nowDate) > 18)
				{
					travellerMix.setAdultCount(travellerMix.getAdultCount() + 1);
				}
				else
				{
					travellerMix.getChildAges().add(traveller.getAge(nowDate));
				}
			}
			rateCheckRQDTO.getTravellers().add(travellerMix);
			AccommodationRateCheckRS rateCheckRS = rateCheckThreaded(rateCheckRQDTO);
			boolean verified = false;
			String error = null;
			if (rateCheckRS != null && rateCheckRS.getRooms() != null)
			{
				for (com.torkirion.eroam.microservice.accommodation.apidomain.RoomResult roomCheck : rateCheckRS.getRooms())
				{
					if (roomCheck.getBookingCode().equals(item.getBookingCode()))
					{
						if (roomCheck.getSupplyRate().equals(item.getSupplyRate()))
						{
							verified = true;
						}
						else
						{
							if (log.isDebugEnabled())
								log.debug("book::rate has changed:roomCheck.getSupplyRate()" + roomCheck.getSupplyRate() + " != " + item.getSupplyRate());
							error = "Rate has changed";
						}
					}
				}
			}
			if (!verified && error == null)
			{
				error = "Room rate unavailable";
			}
			if (verified)
			{
				Integer rateId = Integer.parseInt(item.getBookingCode());
				IMSAccommodationRate rate = null;
				IMSAccommodationRoomtype room = null;
				IMSAccommodationCancellationPolicy cnx = null;
				IMSAccommodationBoard board = null;
				List<IMSAccommodationRate> dbRates = dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(item.getHotelID().substring(CHANNEL_PREFIX.length()));
				for ( IMSAccommodationRate dbRate : dbRates)
				{
					if ( dbRate.getRateId().intValue() == rateId.intValue())
					{
						rate = dbRate;
						// get room, board and cnx policy
						for ( IMSAccommodationRoomtype dbRoom : dataService.getAccommodationRoomtypeRepo().findByHotelIdOrderByRoomtypeIdAsc(item.getHotelID().substring(CHANNEL_PREFIX.length())))
						{
							if ( dbRoom.getRoomtypeId().intValue() == dbRate.getRoomtypeId().intValue())
							{
								room = dbRoom;
							}
						}
						for ( IMSAccommodationCancellationPolicy dbCnx : dataService.getAccommodationCancellationPolicyRepo().findByHotelIdOrderByPolicyIdAscLineIdAsc(item.getHotelID().substring(CHANNEL_PREFIX.length())))
						{
							if ( dbCnx.getPolicyId().intValue() == dbRate.getPolicyId().intValue())
							{
								cnx = dbCnx;
							}
						}
						for ( IMSAccommodationBoard dbBoard : dataService.getAccommodationBoardRepo().findByHotelIdOrderByBoardCodeAsc(item.getHotelID().substring(CHANNEL_PREFIX.length())))
						{
							if ( dbBoard.getBoardCode().equals(dbRate.getBoardCode()))
							{
								board = dbBoard;
							}
						}
					}
				}
				// decrement allocation
				try
				{
				if (log.isDebugEnabled())
					log.debug("book::decrementing allocation");
				LocalDate dateOf = item.getCheckin();
				while ( dateOf.isBefore(item.getCheckout()))
				{
					AllocationKey key = new IMSAccommodationAllocation.AllocationKey();
					key.setHotelId(item.getHotelID().substring(CHANNEL_PREFIX.length()));
					key.setAllocationId(rate.getAllocationId());
					key.setAllocationDate(dateOf);
					Optional<IMSAccommodationAllocation> accommodationAllocationOpt = dataService.getAccommodationAllocationRepo().findById(key);
					if ( !accommodationAllocationOpt.isPresent())
					{
						if (log.isDebugEnabled())
							log.warn("book::something went wrong, allotment is null for hotel " +  item.getHotelID() + " ratecode " + item.getBookingCode());
						throw new Exception("Allocation error in booking subsystem");
					}
					IMSAccommodationAllocation accommodationAllocation = accommodationAllocationOpt.get();
					if (log.isDebugEnabled())
						log.debug("book::accommodationAllocation=" + accommodationAllocation);
					if (log.isDebugEnabled())
						log.debug("book::changing allocation on " + dateOf + " from " + accommodationAllocation.getAllocation() + " to " + (accommodationAllocation.getAllocation()-1));
					if ( accommodationAllocation.getAllocation().intValue() == 0 )
					{
						if (log.isDebugEnabled())
							log.warn("book::something went wrong, allotment is zero for hotel " +  item.getHotelID() + " ratecode " + item.getBookingCode());
						throw new Exception("Allocation error in booking subsystem");
					}
					accommodationAllocation.setAllocation(accommodationAllocation.getAllocation() - 1);;
					dataService.getAccommodationAllocationRepo().save(accommodationAllocation);
					dateOf = dateOf.plusDays(1);
				}
				// save in bookings table
				IMSAccommodationSale accommodationSale = new IMSAccommodationSale();
				accommodationSale.setBookingDateTime(LocalDateTime.now());
				accommodationSale.setCurrency(item.getSupplyRate().getCurrencyId());
				accommodationSale.setItemStatus(IMSAccommodationSale.ItemStatus.BOOKED);
				accommodationSale.setCountryCodeOfOrigin(bookRQ.getCountryCodeOfOrigin());
				accommodationSale.setTitle(bookRQ.getBooker().getTitle());
				accommodationSale.setGivenName(bookRQ.getBooker().getGivenName());
				accommodationSale.setSurname(bookRQ.getBooker().getSurname());
				accommodationSale.setTelephone(bookRQ.getBooker().getTelephone());
				accommodationSale.setInternalBookingReference(bookRQ.getInternalBookingReference());
				accommodationSale.setInternalItemReference(item.getInternalItemReference());
				accommodationSale.setHotelId(item.getHotelID().substring(CHANNEL_PREFIX.length()));
				accommodationSale.setAllocationId(rate.getAllocationId());
				accommodationSale.setAccommodationName("");
				accommodationSale.setRoomName(room == null ? "" : room.getDescription());
				accommodationSale.setCheckin(item.getCheckin());
				accommodationSale.setCheckout(item.getCheckout());
				accommodationSale.setRateName(rate == null ? "" : rate.getDescription());
				accommodationSale.setRoomNumber(itemNumber);
				accommodationSale.setNettPrice(item.getSupplyRate().getAmount());
				accommodationSale.setBoard(board == null ? "" : board.getBoardDescription());
				accommodationSale.setCnxPolicy(cnx == null ? "" : cnx.getPolicyName());
				StringBuffer guestNames = new StringBuffer();
				for ( Integer index : item.getTravellerIndex())
				{
					Traveller traveller = bookRQ.getTravellers().get(index);
					if ( guestNames.length() > 0 )
						guestNames.append(",");
					guestNames.append(cleanName(traveller.getSurname()) + "/" + cleanName(traveller.getGivenName()) + "/" + cleanName(traveller.getTitle()) + " (" + traveller.getAge(nowDate) + ")");
				}
				accommodationSale.setGuestInformation(guestNames.toString());
				accommodationSale = dataService.getAccommodationSaleRepo().save(accommodationSale);

				// Worry - how do we relate the same RATE across maybe change in prices for dates?
				AccommodationBookRS.ResponseItem brs = new AccommodationBookRS.ResponseItem();
				brs.setChannel(CHANNEL);
				brs.setItemStatus(ItemStatus.BOOKED);
				brs.setInternalItemReference(accommodationSale.getId().toString());
				brs.setBookingItemReference(item.getInternalItemReference());
				if ( bookRS.getBookingReference() == null )
				bookRS.setBookingReference(accommodationSale.getId().toString());
				if ( bookRS.getInternalBookingReference() == null )
					bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
				bookRS.getItems().add(brs);
				}
				catch (Exception e)
				{
					log.warn("book::allocation udate:caught " + e.toString(), e);
				}
			}
			else
			{
				AccommodationBookRS.ResponseItem brs = new AccommodationBookRS.ResponseItem();
				brs.setChannel(CHANNEL);
				brs.setItemStatus(ItemStatus.FAILED);
				brs.setInternalItemReference(item.getInternalItemReference());
				bookRS.getItems().add(brs);
				bookRS.getErrors().add(new ResponseExtraInformation("300", error));
			}
			itemNumber++;
		}
		return bookRS;
	}
	
	private String cleanName(String name) 
	{
		return name.replaceAll(",", "").replaceAll("/", "");
	}

	@Override
	public AccommodationCancelRS cancel(String client, AccommodationCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::received " + cancelRQ);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<AccommodationCancelRS> callableTask = () -> {
		    return cancelThreaded(client, cancelRQ);
		};
		Future<AccommodationCancelRS> future = threadPoolExecutor.submit(callableTask);
		AccommodationCancelRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	protected AccommodationCancelRS cancelThreaded(String client, AccommodationCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::received " + cancelRQ);

		AccommodationCancelRS cancelRS = new AccommodationCancelRS("Cancelled", new CurrencyValue("AUD", BigDecimal.ZERO));
		
		Optional<IMSAccommodationSale> accommodationSaleOpt = dataService.getAccommodationSaleRepo().findById(Integer.parseInt(cancelRQ.getBookingReference()));
		if ( accommodationSaleOpt.isEmpty() )
		{
			if (log.isDebugEnabled())
				log.warn("cancel::booking refernece " + cancelRQ.getBookingReference() + " not found");
			throw new Exception("Booking reference " + cancelRQ.getBookingReference() + " not found");
		}
		IMSAccommodationSale accommodationSale = accommodationSaleOpt.get();
		LocalDate dateOf = accommodationSale.getCheckin();
		while ( dateOf.isBefore(accommodationSale.getCheckout()))
		{
			AllocationKey key = new IMSAccommodationAllocation.AllocationKey();
			key.setHotelId(accommodationSale.getHotelId());
			key.setAllocationId(accommodationSale.getAllocationId());
			key.setAllocationDate(dateOf);
			Optional<IMSAccommodationAllocation> accommodationAllocationOpt = dataService.getAccommodationAllocationRepo().findById(key);
			if ( !accommodationAllocationOpt.isPresent())
			{
				if (log.isDebugEnabled())
					log.warn("cancel::something went wrong, allotment is null for hotel " +  accommodationSale.getHotelId());
				throw new Exception("Allocation error in booking subsystem");
			}
			IMSAccommodationAllocation accommodationAllocation = accommodationAllocationOpt.get();
			if (log.isDebugEnabled())
				log.debug("book::accommodationAllocation=" + accommodationAllocation);
			if (log.isDebugEnabled())
				log.debug("book::changing allocation on " + dateOf + " from " + accommodationAllocation.getAllocation() + " to " + (accommodationAllocation.getAllocation()+1));
			accommodationAllocation.setAllocation(accommodationAllocation.getAllocation() + 1);;
			dataService.getAccommodationAllocationRepo().save(accommodationAllocation);
			dateOf = dateOf.plusDays(1);
		}
		accommodationSale.setItemStatus(IMSAccommodationSale.ItemStatus.CANCELLED);
		accommodationSale = dataService.getAccommodationSaleRepo().save(accommodationSale);

		return cancelRS;
	}

	public AccommodationRetrieveRS retrieve(String site, AccommodationRetrieveRQ retrieveRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("retrieve::received " + retrieveRQ);

		return null;
	}

	public void clearCache()
	{
		if (log.isDebugEnabled())
			log.debug("clearCache::entered");
		imsRates.clear();
	}

	protected synchronized void loadIMSRates(String client)
	{
		if (log.isDebugEnabled())
			log.debug("loadIMSRates::enter for client " + client);

		Map<String, Rates> rates = Collections.synchronizedMap(new HashMap<>());

		Map<String, Roomtypes> roomtypes = new HashMap<>();
		Map<String, Seasons> seasons = new HashMap<>();
		Map<String, CancellationPolicies> policies = new HashMap<>();
		Map<String, Boards> boards = new HashMap<>();
		Map<String, AccommodationContent> hotelData = new HashMap<>();
		Map<String, Map<Integer, Allocation>> allocations = new HashMap<>();
		Set<String> hotelIds = new HashSet<>();

		for (IMSAccommodationRoomtype entry : dataService.getAccommodationRoomtypeRepo().findAll())
		{
			hotelIds.add(entry.getHotelId());
		}
		for (String hotelId : hotelIds)
		{
			try
			{
				Optional<IMSAccommodationRCData> hotelDataOpt = dataService.getAccommodationRCDataRepo().findById(hotelId);
				if (!hotelDataOpt.isPresent())
				{
					if (log.isDebugEnabled())
						log.debug("loadIMSRates::no RC for hotelId " + hotelId);
					continue;
				}
				AccommodationContent ac = mapperService.map(hotelDataOpt.get());
				hotelData.put(hotelId, ac);

				List<IMSAccommodationRoomtype> roomtypeDatas = dataService.getAccommodationRoomtypeRepo().findByHotelIdOrderByRoomtypeIdAsc(hotelId);
				Roomtypes r = mapperService.mapRoomtypes(roomtypeDatas);
				roomtypes.put(hotelId, r);

				List<IMSAccommodationSeason> seasonDatas = dataService.getAccommodationSeasonRepo().findByHotelIdOrderByDateFromAsc(hotelId);
				Seasons s = mapperService.mapSeasons(seasonDatas);
				seasons.put(hotelId, s);

				List<IMSAccommodationBoard> boardData = dataService.getAccommodationBoardRepo().findByHotelIdOrderByBoardCodeAsc(hotelId);
				Boards b = mapperService.mapBoards(boardData);
				boards.put(hotelId, b);

				List<IMSAccommodationCancellationPolicy> policyData = dataService.getAccommodationCancellationPolicyRepo().findByHotelIdOrderByPolicyIdAscLineIdAsc(hotelId);
				CancellationPolicies c = mapperService.mapPolicies(policyData);
				policies.put(hotelId, c);

				List<IMSAccommodationAllocationSummary> allocationSummaryDatas = dataService.getAccommodationAllocationSummaryRepo().findByHotelId(hotelId);
				Map<Integer, Allocation> allocationMap = new HashMap<>();
				allocations.put(hotelId, allocationMap);
				for (IMSAccommodationAllocationSummary allocationSummaryData : allocationSummaryDatas)
				{
					Allocation a = allocationMap.get(allocationSummaryData.getAllocationId());
					List<IMSAccommodationAllocation> allocationDatas = dataService.getAccommodationAllocationRepo().findByHotelIdAndAllocationId(hotelId, allocationSummaryData.getAllocationId());
					Allocation allocation = mapperService.mapAllocation(allocationDatas, allocationSummaryData);
					allocationMap.put(allocationSummaryData.getAllocationId(), allocation);
				}
			}
			catch (Exception e)
			{
				log.debug("loadIMSRates::threw " + e.toString(), e);
			}
		}

		Collection<IMSAccommodationSpecial> imsSpecialsN = Collections.synchronizedSet(new HashSet<>());
		imsSpecialsN.addAll(dataService.getAccommodationSpecialRepo().findAll());
		imsSpecials = imsSpecialsN;

		if (log.isDebugEnabled())
			log.debug("loadIMSRates::loaded tables");

		Map<String, List<IMSAccommodationRate>> hotelRates = new HashMap<>();
		for (IMSAccommodationRate r : dataService.getAccommodationRateRepo().findAll())
		{
			if (log.isDebugEnabled())
				log.debug("loadIMSRates::loading IMSAccommodationRate " + r);
			List<IMSAccommodationRate> hotelRate = hotelRates.get(r.getHotelId());
			if (hotelRate == null)
			{
				hotelRate = new ArrayList<>();
				hotelRates.put(r.getHotelId(), hotelRate);
			}
			hotelRate.add(r);
		}
		for (Entry<String, List<IMSAccommodationRate>> rateDataEntry : hotelRates.entrySet())
		{
			List<IMSAccommodationRate> rateDatas = rateDataEntry.getValue();
			if (log.isDebugEnabled())
				log.debug("loadIMSRates::processing hotel " + rateDataEntry.getKey() + " IMSAccommodationRate " + rateDatas);
			if (rateDataEntry.getKey() == null || hotelData.get(rateDataEntry.getKey()) == null || hotelData.get(rateDataEntry.getKey()).getCurrency() == null)
			{
				if (log.isDebugEnabled())
					log.debug("loadIMSRates::hotelData.get(rateKey) is null, bypassing");
				continue;
			}
			try
			{
				Rates rr = mapperService.mapRates(rateDatas, hotelData.get(rateDataEntry.getKey()).getCurrency(), hotelData.get(rateDataEntry.getKey()).getRrpCurrency());
				for (Rate r : rr.getRates())
				{
					if (log.isDebugEnabled())
						log.debug("loadIMSRates::setting Rate " + r);
					try
					{
						if (roomtypes.get(rr.getHotelId()) != null)
						{
							if (log.isDebugEnabled())
								log.debug("loadIMSRates::looking for " + r.getRoomtypeId() + ", scanning roomtypes " + roomtypes.get(rr.getHotelId()));
							for (Roomtype rt : roomtypes.get(rr.getHotelId()).getRoomtypes())
							{
								if (log.isDebugEnabled())
									log.debug("loadIMSRates::against " + rt.getRoomtypeId());
								if (r.getRoomtypeId() != null && rt.getRoomtypeId() != null && rt.getRoomtypeId().equals(r.getRoomtypeId()))
								{
									if (log.isDebugEnabled())
										log.debug("loadIMSRates::resolved roomtypeid " + r.getRoomtypeId() + " to " + rt);
									r.setRoomType(rt);
								}
							}
						}
						if (seasons.get(rr.getHotelId()) != null)
						{
							for (Season s : seasons.get(rr.getHotelId()).getSeasons())
							{
								if (s.getSeasonId().equals(r.getSeasonId()))
									r.setSeason(s);
							}
						}
						if (boards.get(rr.getHotelId()) != null)
						{
							for (Boards.Board b : boards.get(rr.getHotelId()).getBoards())
							{
								if (b.getBoardCode().equals(r.getBoardCode()))
									r.setBoard(b);
							}
						}
						if (policies.get(rr.getHotelId()) != null)
						{
							for (CancellationPolicy c : policies.get(rr.getHotelId()).getPolicies())
							{
								if (c.getPolicyId().equals(r.getPolicyId()))
									r.setPolicy(c);
							}
						}
						if (allocations.get(rr.getHotelId()) != null)
						{
							r.setAllocation(allocations.get(rr.getHotelId()).get(r.getAllocationId()));
						}
					}
					catch (Exception e)
					{
						log.warn("loadIMSRates::caught error " + e.toString(), e);
					}
				}
				rates.put(rr.getHotelId(), rr);
				if (log.isDebugEnabled())
					log.debug("loadIMSRates::hotel '" + rr.getHotelId() + "' resolved to Rates " + rr);
			}
			catch (Exception e)
			{
				log.warn("loadIMSRates::caught error " + e.toString(), e);
			}
		}
		imsRates.put(client, rates);
		if (log.isDebugEnabled())
			log.debug("loadIMSRates::loaded " + imsRates.size() + " hotels for client " + client);
		//if (log.isDebugEnabled())
		//	log.debug("loadIMSRates::loaded " + imsRates);
	}

	/**
	 * Returns TRUE if policy1 '<' policy2, where policy1 is LESS restrictive
	 * @param policy1
	 * @param policy2
	 * @return
	 */
	private boolean comparePolicies(CancellationPolicies.CancellationPolicy policy1, CancellationPolicies.CancellationPolicy policy2 )
	{
		BigDecimal maxPenalty1 = BigDecimal.ZERO;
		BigDecimal maxPenalty2 = BigDecimal.ZERO;
		int maxDays1 = 0;
		int maxDays2 = 0;
		for ( CancellationPolicyLine line : policy1.getLines())
		{
			if ( line.getPenalty().compareTo(maxPenalty1) > 0 )
				maxPenalty1 = line.getPenalty();
			if ( line.getBeforeCheckinAfterBooking().equals(CancellationPolicies.BeforeCheckinAfterBooking.AFTER_BOOKING))
			{
				if ( line.getNumberOfDays() > maxDays1 )
					maxDays1 = line.getNumberOfDays();
			}
			else
			{
				if ( 1000 - line.getNumberOfDays() > maxDays1 )
					maxDays1 = 1000 - line.getNumberOfDays();
			}
		}
		for ( CancellationPolicyLine line : policy2.getLines())
		{
			if ( line.getPenalty().compareTo(maxPenalty2) > 0 )
				maxPenalty2 = line.getPenalty();
			if ( line.getBeforeCheckinAfterBooking().equals(CancellationPolicies.BeforeCheckinAfterBooking.AFTER_BOOKING))
			{
				if ( line.getNumberOfDays() > maxDays2 )
					maxDays2 = line.getNumberOfDays();
			}
			else
			{
				if ( 1000 - line.getNumberOfDays() > maxDays2 )
					maxDays2 = 1000 - line.getNumberOfDays();
			}
		}
		if ( maxPenalty1.compareTo(maxPenalty2) == 0)
		{
			return maxDays1 < maxDays2;
		}
		else
		{
			return maxPenalty1.compareTo(maxPenalty2) < 0;
		}	
	}
	
	private static final BigDecimal BD_100 = new BigDecimal("100");

	private static final BigDecimal BD_M1 = new BigDecimal("-1");

	private AccommodationRC getAccommodationRC(String hotelId) throws Exception
	{
		Optional<IMSAccommodationRCData> accommodationRCOpt = dataService.getAccommodationRCDataRepo().findById(hotelId);
		if (!accommodationRCOpt.isPresent())
		{
			if (log.isDebugEnabled())
				log.debug("makeResult::no rc for " + hotelId);
			return null;
		}
		IMSAccommodationRCData imsAccommodationRCData = accommodationRCOpt.get();
		if ( imsAccommodationRCData.getChildAge() == null )
			imsAccommodationRCData.setChildAge(19);
		if ( imsAccommodationRCData.getInfantAge() == null )
			imsAccommodationRCData.setInfantAge(0);
		AccommodationRC accommodationRC = mapperService.mapToRC(imsAccommodationRCData);
		return accommodationRC;
	}
	
	private IMSAccommodationRCData getIMSAccommodationRC(String hotelId) throws Exception
	{
		Optional<IMSAccommodationRCData> accommodationRCOpt = dataService.getAccommodationRCDataRepo().findById(hotelId);
		if (!accommodationRCOpt.isPresent())
		{
			if (log.isDebugEnabled())
				log.debug("makeResult::no rc for " + hotelId);
			return null;
		}
		IMSAccommodationRCData imsAccommodationRCData = accommodationRCOpt.get();
		return imsAccommodationRCData;
	}
	
	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}
	
	private AccommodationResult checkSupplyRate(AccommodationResult accommodationResult ){
		//check supply rate
		for (RoomResult e :accommodationResult.getRooms()) {
			if(e.getSupplyRate()!=null){
				return accommodationResult;
			}
		}
		return null;
	}
}
