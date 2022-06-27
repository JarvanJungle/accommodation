package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ.SpecialRequest;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRS.ResponseItem;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomExtraFee.FeeType;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomPromotion.PromotionType;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.BoardCodes;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.BookingRQ.ContactDetails;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.BookingRQ.Guest;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.BookingRS.BookedRoom;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.CancellationCharge;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.Establishment;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.Extra;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.ExtraOption;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.Inclusion;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.InfoItem;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.LocalCharge;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.CommonRQRS.Money;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.BoardTypeInclusionData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.SupplierBoardTypeData;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationChannelService;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class YalagoService implements AccommodationServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private AccommodationRCRepo accommodationRCRepo;

	private AccommodationRCService accommodationRCService;

	private YalagoCache yalagoCache;

	public static final String CHANNEL = "YALAGO";

	public static final String CHANNEL_PREFIX = "YL";

	public List<AccommodationResult> searchByHotelId(AvailSearchByHotelIdRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByHotelIdRQDTO)=" + availSearchRQ);

		AvailabilityRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);
		for (String hotelId : availSearchRQ.getHotelIds())
		{
			if (!YalagoService.CHANNEL.equals(AccommodationChannelService.getChannelForHotelId(hotelId)))
			{
				continue;
			}
			Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(hotelId);
			if (accommodationRCOpt.isPresent())
			{
				availabilityRQ.getEstablishmentIds().add(accommodationRCOpt.get().getChannelCode());
			}
			else
			{
				log.error("search::accommodation " + hotelId + " not found");
			}
		}

		return search(availabilityRQ, availSearchRQ);
	}

	public List<AccommodationResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);

		AvailabilityRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);
		List<AccommodationRCData> boxedRC = accommodationRCRepo.findByGeoboxAndChannel(availSearchRQ.getNorthwest().getLatitude(), availSearchRQ.getNorthwest().getLongitude(),
				availSearchRQ.getSoutheast().getLatitude(), availSearchRQ.getSoutheast().getLongitude(), YalagoService.CHANNEL);
		if (log.isDebugEnabled())
			log.debug("startSearchHotels::found " + boxedRC.size() + " hotels");
		for (AccommodationRCData rc : boxedRC)
		{
			availabilityRQ.getEstablishmentIds().add(rc.getChannelCode());
		}

		return search(availabilityRQ, availSearchRQ);
	}

	protected AvailabilityRQ makeAvailabilityRQ(AvailSearchRQDTO availSearchRQ)
	{
		if (log.isDebugEnabled())
			log.debug("makeAvailabilityRQ::entering for availSearchRQ=" + availSearchRQ);

		AvailabilityRQ availabilityRQ = new AvailabilityRQ();
		String countryCodeOfOrigin = availSearchRQ.getCountryCodeOfOrigin();
		if (countryCodeOfOrigin == null || countryCodeOfOrigin.length() == 0)
		{
			countryCodeOfOrigin = propertiesDAO.getProperty(availSearchRQ.getClient(), CHANNEL, "sourceMarket", "GB");
		}
		availabilityRQ.setSourceMarket(countryCodeOfOrigin);
		availabilityRQ.setCheckInDate(formatterYYYYMMDD.format(availSearchRQ.getCheckin()));
		availabilityRQ.setCheckOutDate(formatterYYYYMMDD.format(availSearchRQ.getCheckout()));
		for (TravellerMix travellers : availSearchRQ.getTravellers())
		{
			AvailabilityRQ.Room room = new AvailabilityRQ.Room();
			room.setAdults(travellers.getAdultCount());
			room.setChildAges(travellers.getChildAges());
			availabilityRQ.getRooms().add(room);
		}

		return availabilityRQ;
	}

	protected List<AccommodationResult> search(AvailabilityRQ availabilityRQ, AvailSearchRQDTO availSearchRQ)
	{
		if (log.isDebugEnabled())
			log.debug("search::entering");
		try
		{
			YalagoInterface yalagoInterface = new YalagoInterface(propertiesDAO, accommodationRCRepo, availSearchRQ.getClient(), CHANNEL);
			long timer1 = System.currentTimeMillis();
			long timer2 = System.currentTimeMillis();
			AvailabilityRS availabilityRS = yalagoInterface.searchHotels(availabilityRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("search::time in yalago search was " + totalTime2 + " millis");
			if (availabilityRS == null || availabilityRS.getEstablishments() == null || availabilityRS.getEstablishments().size() == 0)
			{
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned no lists");
				return new ArrayList<>();
			}
			List<AccommodationResult> results = mapAvailability(availabilityRS);
			log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
			return results;
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		return new ArrayList<>();
	}

	protected List<AccommodationResult> mapAvailability(AvailabilityRS availabilityRS) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("mapAvailability::entering for " + availabilityRS.getEstablishments().size() + " items");

		int listNo = 0;

		List<AccommodationResult> results = new ArrayList<>();
		hotelLoop: for (Establishment establishment : availabilityRS.getEstablishments())
		{
			if (log.isDebugEnabled())
				log.debug("mapAvailability::processing item " + listNo);
			AccommodationResult accommodationResult = new AccommodationResult();
			String property_id = new String(CHANNEL_PREFIX + establishment.getEstablishmentId());
			Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(property_id);
			if (!accommodationRCOpt.isPresent())
			{
				if (log.isDebugEnabled())
					log.debug("mapAvailability::rc not found, bypassing");
				continue hotelLoop;
			}
			AccommodationProperty property = new AccommodationProperty();
			property.setCode(property_id);
			property.setChannel(YalagoService.CHANNEL);
			property.setChannelCode(establishment.getEstablishmentId().toString());
			// rest of the details are set in the AccommodationSearchService
			accommodationResult.setProperty(property);
			accommodationResult.setRooms(loadRooms(establishment.getRooms(), property_id));

			if (accommodationResult.getRooms() == null || accommodationResult.getRooms().size() == 0)
			{
				if (log.isDebugEnabled())
					log.debug("search::hotel " + property_id + " not added, has no rooms");
			}
			else
			{
				results.add(accommodationResult);
			}
		}
		return results;
	}

	protected List<RoomResult> loadRateCheckRooms(List<AvailabilityRS.Room> availabilityRSRooms, String property_id, RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("loadRateCheckRooms::loading rooms, availabilityRSRooms.size=" + (availabilityRSRooms == null ? 0 : availabilityRSRooms.size()));
		List<RoomResult> rooms = new ArrayList<>();
		if (availabilityRSRooms == null || availabilityRSRooms.size() == 0)
		{
			return rooms;
		}
		long timer1 = System.currentTimeMillis();

		if ( availabilityRSRooms.size() != availabilityRSRooms.size())
		{
			throw new Exception("Request rooms count does not match returned room count");
		}
		travellerLoop: for (int index = 0; index < availabilityRSRooms.size(); index++)
		{
			BookingCodeStruct unmakeBookingCode = unmakeBookingCode(rateCheckRQDTO.getBookingCodes().get(index));
			if (log.isDebugEnabled())
				log.debug("loadRateCheckRooms::traveller index " + index + ", looking for unmakeBookingCode " + unmakeBookingCode);

			AvailabilityRS.Room availabilityRSRoom = availabilityRSRooms.get(index);
			if (log.isDebugEnabled())
				log.debug("loadRateCheckRooms::processing room " + availabilityRSRoom.getCode() + ", availabilityRSRoom.getBoards().size="
						+ (availabilityRSRoom.getBoards() == null ? 0 : availabilityRSRoom.getBoards().size()));

			boardLoop: for (AvailabilityRS.Board availabilityRSBoard : availabilityRSRoom.getBoards())
			{
				if (log.isDebugEnabled())
					log.debug("loadRateCheckRooms::processing board '" + availabilityRSBoard.getCode() + "' with nett " + availabilityRSBoard.getNetCost());

				if (availabilityRSBoard.getIsPayAtHotel())
				{
					if (log.isDebugEnabled())
						log.debug("loadRateCheckRooms::getIsPayAtHotel, bypassing");
					continue boardLoop;
				}
				if (availabilityRSBoard.getIsOnRequest())
				{
					if (log.isDebugEnabled())
						log.debug("loadRateCheckRooms::getIsOnRequest, bypassing");
					continue boardLoop;
				}

				if (availabilityRSBoard.getExtras() == null)
				{
					RoomResult room = makeRoomBaseWithExtra(null, null, availabilityRSRoom, availabilityRSBoard, property_id);
					if (room != null)
					{
						room.setRoomNumber(index + 1);
						BookingCodeStruct testBookingCode = unmakeBookingCode(room.getBookingCode());
						if (log.isDebugEnabled())
							log.debug("loadRateCheckRooms::testing room " + testBookingCode);
						if (testBookingCode.roomCode.equals(unmakeBookingCode.roomCode) && testBookingCode.boardCode.equals(unmakeBookingCode.boardCode))
						{
							if (log.isDebugEnabled())
								log.debug("loadRateCheckRooms::found room " + room.getBookingCode() + " for index " + index);
							rooms.add(room);
							if (log.isDebugEnabled())
								log.debug("loadRateCheckRooms::continuing...");
							continue travellerLoop;
						}
					}
				}
				else
				{
					for (Extra extra : availabilityRSBoard.getExtras())
					{
						if (extra.getIsMandatory())
						{
							for (ExtraOption extraOption : extra.getOptions())
							{
								RoomResult room = makeRoomBaseWithExtra(extra, extraOption, availabilityRSRoom, availabilityRSBoard, property_id);
								if (room != null)
								{
									room.setRoomNumber(index + 1);
									BookingCodeStruct testBookingCode = unmakeBookingCode(room.getBookingCode());
									if (log.isDebugEnabled())
										log.debug("loadRateCheckRooms::testing room " + testBookingCode);
									if (testBookingCode.roomCode.equals(unmakeBookingCode.roomCode) && testBookingCode.boardCode.equals(unmakeBookingCode.boardCode)
											&& testBookingCode.extraId.equals(unmakeBookingCode.extraId))
									{
										if ((testBookingCode.extraOptionId == null && unmakeBookingCode.extraOptionId == null)
												|| (testBookingCode.extraOptionId != null && testBookingCode.extraOptionId.equals(unmakeBookingCode.extraOptionId)))
										{
											if (log.isDebugEnabled())
												log.debug("loadRateCheckRooms::found room " + room.getBookingCode() + " for index " + index);
											rooms.add(room);
											if (log.isDebugEnabled())
												log.debug("loadRateCheckRooms::continuing...");
											continue travellerLoop;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("loadRateCheckRooms::roomcount=" + rooms.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return rooms;
	}

	protected SortedSet<RoomResult> loadRooms(List<AvailabilityRS.Room> availabilityRSRooms, String property_id) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("loadRooms::loading rooms, availabilityRSRooms.size=" + (availabilityRSRooms == null ? 0 : availabilityRSRooms.size()));
		SortedSet<RoomResult> rooms = new TreeSet<>();
		if (availabilityRSRooms == null || availabilityRSRooms.size() == 0)
		{
			return rooms;
		}
		long timer1 = System.currentTimeMillis();

		roomLoop: for (AvailabilityRS.Room availabilityRSRoom : availabilityRSRooms)
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::processing room " + availabilityRSRoom.getCode() + "availabilityRSRoom.getBoards().size="
						+ (availabilityRSRoom.getBoards() == null ? 0 : availabilityRSRoom.getBoards().size()));

			boardLoop: for (AvailabilityRS.Board availabilityRSBoard : availabilityRSRoom.getBoards())
			{
				if (log.isDebugEnabled())
					log.debug("loadRooms::processing board '" + availabilityRSBoard.getCode() + "' with nett " + availabilityRSBoard.getNetCost());

				if (availabilityRSBoard.getIsPayAtHotel())
				{
					if (log.isDebugEnabled())
						log.debug("loadRooms::getIsPayAtHotel, bypassing");
					continue boardLoop;
				}
				if (availabilityRSBoard.getIsOnRequest())
				{
					if (log.isDebugEnabled())
						log.debug("loadRooms::getIsOnRequest, bypassing");
					continue boardLoop;
				}

				if (availabilityRSBoard.getExtras() == null)
				{
					RoomResult room = makeRoomBaseWithExtra(null, null, availabilityRSRoom, availabilityRSBoard, property_id);
					if (room != null)
					{
						rooms.add(room);
					}
				}
				else
				{
					for (Extra extra : availabilityRSBoard.getExtras())
					{
						if (extra.getIsMandatory())
						{
							if (log.isDebugEnabled())
								log.debug("loadRooms::extra " + extra.getExtraId() + " has " + extra.getOptions().size() + " options");
							for (ExtraOption extraOption : extra.getOptions())
							{
								RoomResult room = makeRoomBaseWithExtra(extra, extraOption, availabilityRSRoom, availabilityRSBoard, property_id);
								if (room != null)
								{
									rooms.add(room);
								}
							}
						}
					}
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("loadRooms::roomcount=" + rooms.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return rooms;
	}

	protected RoomResult makeRoomBaseWithExtra(Extra extra, ExtraOption extraOption, AvailabilityRS.Room availabilityRSRoom, AvailabilityRS.Board availabilityRSBoard, String property_id)
	{
		if (log.isDebugEnabled())
			log.debug("makeRoomBaseWithExtra::extra=" + extra + ", extraOption=" + extraOption + ", roomCode=" + availabilityRSRoom.getCode());

		RoomResult room = makeRoomBase(availabilityRSRoom, availabilityRSBoard, property_id);
		if (availabilityRSBoard.getNonRefundable() || availabilityRSBoard.getCancellationPolicy() == null || availabilityRSBoard.getCancellationPolicy().getCancellationCharges() == null
				|| availabilityRSBoard.getCancellationPolicy().getCancellationCharges().size() == 0)
			room.setCancellationPolicy(makeNonRefundableRoomCNXPolicy());
		else
			room.setCancellationPolicy(makeRoomCNXPolicy(availabilityRSBoard));
		room.setCancellationPolicyText(makeRoomCNXPolicyText(room.getCancellationPolicy()));
		room.getPromotions().addAll(makeBoardPromotions(availabilityRSBoard));
		if (availabilityRSBoard.getBoardBasis() != null && availabilityRSBoard.getBoardBasis().getSupplierBoardTypeId() != null)
		{
			if (log.isDebugEnabled())
				log.debug("makeRoomBaseWithExtra::getSupplierBoardTypeId=" + availabilityRSBoard.getBoardBasis().getSupplierBoardTypeId());
			room.setBoardCode(mapBoardCode(availabilityRSBoard.getBoardBasis().getSupplierBoardTypeId()));
			room.setBoardDescription(BoardCodes.mapBoardDescription(mapBoardCode(availabilityRSBoard.getBoardBasis().getSupplierBoardTypeId())));
		}
		room.setBedding("");

		if (extra == null)
		{
			if (log.isDebugEnabled())
				log.debug("makeRoomBaseWithExtra::extra is null");
			room.setRoomStandard("Standard");
			return room;
		}

		if (extra.getExtraId().equals("42"))
		{
			if (log.isDebugEnabled())
				log.debug("makeRoomBaseWithExtra::extraId 42 requires flight details.  Bypassing for now");
			return null;
		}
		room.setRoomCode(room.getRoomCode());
		if (extraOption.getCustomerText() != null && extraOption.getCustomerText().length() > 30)
		{
			room.setRoomName(room.getRoomName() + " with " + extra.getTitle() + " (" + extraOption.getTitle() + ")");
			room.setRoomStandard("Standard");
			room.setRoomExtraInformation(extraOption.getCustomerText());
			if (log.isDebugEnabled())
				log.debug("makeRoomBaseWithExtra::for hotel " + property_id + " made large extra info " + room.getRoomExtraInformation());
		}
		else
		{
			String extraOptionTitle = extraOption.getTitle().trim();
			if (extraOption.getCustomerText().length() > 0)
				extraOptionTitle = extraOption.getTitle() + ", " + extraOption.getCustomerText();
			room.setRoomName(room.getRoomName() + " with " + extra.getTitle() + " (" + extraOptionTitle + ")");
			room.setRoomStandard("Standard");
			// room.setRoomExtraInformation("Make sure this text appears. And make sure it wraps - this can get VERY long!");
			room.setRoomExtraInformation("");
		}
		room.setRateCode(room.getRateCode());
		room.setMatchCode(room.getRateCode());
		room.setSupplyRate(new CurrencyValue(room.getSupplyRate().getCurrencyId(), room.getSupplyRate().getAmount().add(extraOption.getNetCost().getAmount())));
		room.setRrpIsMandatory(room.getRrpIsMandatory());
		if (extraOption.getGrossCost() != null)
		{
			room.setBookingCode(
					room.getBookingCode() + "_" + extra.getExtraId() + "_" + extraOption.getOptionId() + "_" + extraOption.getNetCost().getAmount() + "_" + extraOption.getGrossCost().getAmount());
			room.setTotalRate(new CurrencyValue(room.getTotalRate().getCurrencyId(), room.getTotalRate().getAmount().add(extraOption.getGrossCost().getAmount())));
		}
		else
		{
			room.setBookingCode(
					room.getBookingCode() + "_" + extra.getExtraId() + "_" + extraOption.getOptionId() + "_" + extraOption.getNetCost().getAmount() + "_" + extraOption.getNetCost().getAmount());
			room.setTotalRate(new CurrencyValue(room.getTotalRate().getCurrencyId(), room.getTotalRate().getAmount().add(extraOption.getNetCost().getAmount())));
		}
		if (log.isDebugEnabled())
			log.debug("makeRoomBaseWithExtra::roomStandard=" + room.getRoomStandard() + ", room.getTotalRate=" + room.getTotalRate() + ", room.getSupplyRate=" + room.getSupplyRate());

		return room;
	}

	protected RoomResult makeRoomBase(AvailabilityRS.Room availabilityRSRoom, AvailabilityRS.Board availabilityRSBoard, String property_id)
	{
		if (log.isDebugEnabled())
			log.debug("makeRoomBase::roomCode=" + availabilityRSRoom.getCode() + ", roomNumber=" + availabilityRSBoard.getRequestedRoomIndex());

		String roomCode = availabilityRSRoom.getCode();
		String fullRoomCode = makeBookingCode(availabilityRSRoom.getCode(), availabilityRSBoard.getCode());
		StringBuffer fullRoomName = new StringBuffer(availabilityRSRoom.getDescription());
		if (availabilityRSBoard.getDescription() != null && availabilityRSBoard.getDescription().length() > 0)
		{
			fullRoomName = fullRoomName.append(", " + availabilityRSBoard.getDescription());
		}
		RoomResult room = new RoomResult();
		room.setChannel(CHANNEL);
		room.setChannelPropertyCode(property_id);
		room.setRoomNumber(availabilityRSBoard.getRequestedRoomIndex() == null ? 1 : availabilityRSBoard.getRequestedRoomIndex() + 1);
		if (log.isDebugEnabled())
			log.debug("makeRoomBase::availabilityRSBoard.getRequestedRoomIndex()=" + availabilityRSBoard.getRequestedRoomIndex() + ", roomNumber=" + room.getRoomNumber());
		room.setRoomCode(roomCode);
		room.setRoomName(fullRoomName.toString());
		room.setRateCode(availabilityRSBoard.getCode());
		room.setMatchCode(availabilityRSBoard.getCode());
		room.setBookingCode(fullRoomCode);
		if (log.isDebugEnabled())
			log.debug("makeRoomBase::netCost=" + availabilityRSBoard.getNetCost() + ", grossCost=" + availabilityRSBoard.getGrossCost());
		room.setSupplyRate(new CurrencyValue(availabilityRSBoard.getNetCost().getCurrency(), availabilityRSBoard.getNetCost().getAmount()));
		room.setRrpIsMandatory(availabilityRSBoard.getIsBindingPrice());
		if (availabilityRSBoard.getGrossCost() != null)
		{
			room.setTotalRate(new CurrencyValue(availabilityRSBoard.getGrossCost().getCurrency(), availabilityRSBoard.getGrossCost().getAmount()));
		}
		else
		{
			room.setTotalRate(new CurrencyValue(availabilityRSBoard.getNetCost().getCurrency(), availabilityRSBoard.getNetCost().getAmount().multiply(STANDARD_MARKUP)));
			room.setRrpIsMandatory(false);
		}
		if (log.isDebugEnabled())
			log.debug("makeRoomBase::room.getTotalRate=" + room.getTotalRate() + ", room.getSupplyRate=" + room.getSupplyRate());
		room.setInventory(availabilityRSRoom.getQuantityAvailable() == null ? null : BigInteger.valueOf(availabilityRSRoom.getQuantityAvailable()));
		room.setRequiresRecheck(true);
		if (availabilityRSBoard.getLocalChargesAmount() != null)
		{
			RoomExtraFee extraFee = new RoomExtraFee();
			extraFee.setFee(new CurrencyValue(availabilityRSBoard.getLocalChargesAmount().getCurrency(), availabilityRSBoard.getLocalChargesAmount().getAmount()));
			extraFee.setDescription("Local charges payable at hotel");
			extraFee.setFeeType(FeeType.CheckinFees);
			room.getExtraFees().add(extraFee);
		}
		if (availabilityRSBoard.getLocalCharges() != null)
		{
			for (LocalCharge localCharge : availabilityRSBoard.getLocalCharges())
			{
				RoomExtraFee extraFee = new RoomExtraFee();
				extraFee.setFee(new CurrencyValue(localCharge.getAmount().getCurrency(), localCharge.getAmount().getAmount()));
				extraFee.setDescription(localCharge.getTitle() + " : " + localCharge.getDescription());
				extraFee.setFeeType(FeeType.CheckinFees);
				room.getExtraFees().add(extraFee);
			}
		}
		return room;
	}

	protected SortedSet<RoomCancellationPolicyLine> makeRoomCNXPolicy(AvailabilityRS.Board availabilityRSBoard)
	{
		SortedSet<RoomCancellationPolicyLine> policyLines = new TreeSet<>();

		for (CancellationCharge cancellationCharge : availabilityRSBoard.getCancellationPolicy().getCancellationCharges())
		{
			if ( cancellationCharge.getExpiryDate().contains("+"))
			{
				cancellationCharge.setExpiryDate(cancellationCharge.getExpiryDate().substring(0,  cancellationCharge.getExpiryDate().indexOf("+")));
				if (log.isDebugEnabled())
					log.debug("makeRoomCNXPolicy::trimmed CNX Expiry to " + cancellationCharge.getExpiryDate());
			}
			LocalDate asOf = LocalDate.parse(cancellationCharge.getExpiryDate(), formatterYYYYMMDDhhmmss);
			CurrencyValue fee = new CurrencyValue(cancellationCharge.getCharge().getCurrency(), cancellationCharge.getCharge().getAmount());
			RoomCancellationPolicyLine cancellationPolicyLine = new RoomCancellationPolicyLine();
			cancellationPolicyLine.setAsOf(asOf);
			cancellationPolicyLine.setBefore(false);
			if (fee.getAmount().compareTo(BigDecimal.ZERO) == 0)
				cancellationPolicyLine.setPenaltyDescription("If cancelled after " + formatterDDMMMYYYY.format(asOf) + ", no charge applies.");
			else
				cancellationPolicyLine.setPenaltyDescription("If cancelled after " + formatterDDMMMYYYY.format(asOf) + ", a charge of " + fee.getCurrencyId() + " " + fee.getAmount() + " applies.");
			cancellationPolicyLine.setPenalty(fee);
			policyLines.add(cancellationPolicyLine);
		}
		// Fill in any gaps left by Yalago
		if (log.isDebugEnabled())
			log.debug("makeRoomCNXPolicy::first date=" + policyLines.first().getAsOf());
		if (policyLines.first().getAsOf().isAfter(LocalDate.now()))
		{
			// need something from now until first item
			if (log.isDebugEnabled())
				log.debug("makeRoomCNXPolicy::first penalty=" + policyLines.first().getPenalty());
			if (policyLines.first().getPenalty() != null && policyLines.first().getPenalty().getAmount().compareTo(BigDecimal.ZERO) == 0)
			{
				// first line is 'after X is $0', make X today
				policyLines.first().setAsOf(LocalDate.now());
				policyLines.first().setPenaltyDescription("If cancelled after " + formatterDDMMMYYYY.format(policyLines.first().getAsOf()) + ", no charge applies.");
			}
			else
			{
				RoomCancellationPolicyLine cancellationPolicyLine = new RoomCancellationPolicyLine();
				cancellationPolicyLine.setAsOf(policyLines.first().getAsOf());
				cancellationPolicyLine.setBefore(true);
				policyLines.first().setPenaltyDescription("If cancelled before " + formatterDDMMMYYYY.format(policyLines.first().getAsOf()) + ", no charge applies.");
				cancellationPolicyLine.setPenaltyPercent(BigDecimal.ZERO);
				policyLines.add(cancellationPolicyLine);
			}
		}
		return policyLines;
	}

	protected String makeRoomCNXPolicyText(SortedSet<RoomCancellationPolicyLine> roomCancellationPolicyLines)
	{
		StringBuffer fullPolicyText = new StringBuffer();
		for (RoomCancellationPolicyLine roomCancellationPolicyLine : roomCancellationPolicyLines)
		{
			if (fullPolicyText.length() > 0)
				fullPolicyText.append(" ");
			fullPolicyText.append(roomCancellationPolicyLine.getPenaltyDescription());
		}
		return fullPolicyText.toString();
	}

	protected SortedSet<RoomCancellationPolicyLine> makeNonRefundableRoomCNXPolicy()
	{
		SortedSet<RoomCancellationPolicyLine> policyLines = new TreeSet<>();

		RoomCancellationPolicyLine cancellationPolicyLine = new RoomCancellationPolicyLine();
		cancellationPolicyLine.setAsOf(LocalDate.now());
		cancellationPolicyLine.setBefore(false);
		cancellationPolicyLine.setPenaltyDescription("Non Refundable");
		cancellationPolicyLine.setPenaltyPercent(BD_100);
		policyLines.add(cancellationPolicyLine);
		return policyLines;
	}

	protected Set<RoomPromotion> makeBoardPromotions(AvailabilityRS.Board availabilityRSBoard)
	{
		Set<RoomPromotion> boardPromotions = new HashSet<>();
		if (availabilityRSBoard.getBoardBasis() != null && availabilityRSBoard.getBoardBasis().getInclusions() != null)
		{
			for (Inclusion inclusion : availabilityRSBoard.getBoardBasis().getInclusions())
			{
				RoomPromotion roomPromotion = new RoomPromotion();
				roomPromotion.setPromoType(derivePromotionType(inclusion.getInclusionId()));
				BoardTypeInclusionData boardTypeInclusionData = yalagoCache.getCachedBoardTypeInclusion(inclusion.getInclusionId());
				if (boardTypeInclusionData != null)
				{
					String inclusionText = boardTypeInclusionData.getTitle();
					roomPromotion.setShortMarketingText(inclusionText);
					if (inclusion.getReplacementText() != null && inclusion.getReplacementText().length() > 0)
					{
						String replacedText = MessageFormat.format(inclusionText, inclusion.getReplacementText());
						roomPromotion.setShortMarketingText(replacedText);
					}
					boardPromotions.add(roomPromotion);
				}
			}
			for (Inclusion exclusion : availabilityRSBoard.getBoardBasis().getExclusions())
			{
				RoomPromotion roomPromotion = new RoomPromotion();
				roomPromotion.setPromoType(derivePromotionType(exclusion.getInclusionId()));
				BoardTypeInclusionData boardTypeInclusionData = yalagoCache.getCachedBoardTypeInclusion(exclusion.getInclusionId());
				if ( boardTypeInclusionData != null )
				{
					String exclusionText = boardTypeInclusionData.getTitle();
					roomPromotion.setShortMarketingText("Excludes " + exclusionText);
					if (exclusion.getReplacementText() != null && exclusion.getReplacementText().length() > 0)
					{
						String replacedText = MessageFormat.format(exclusionText, exclusion.getReplacementText());
						roomPromotion.setShortMarketingText(replacedText);
					}
				}
				else
				{
					log.debug("makeBoardPromotions::exclusion id " + exclusion.getInclusionId() + " not found, bypassing");
				}
				boardPromotions.add(roomPromotion);
			}
			if (availabilityRSBoard.getSupplierBoardTypeId() != null)
			{
				SupplierBoardTypeData supplierBoardTypeData = yalagoCache.getCachedSupplierBoardType(availabilityRSBoard.getSupplierBoardTypeId());
				if (supplierBoardTypeData != null)
				{
					RoomPromotion roomPromotion = new RoomPromotion();
					roomPromotion.setPromoType(PromotionType.MEAL);
					roomPromotion.setShortMarketingText(supplierBoardTypeData.getTitle());
					boardPromotions.add(roomPromotion);
				}
			}
		}
		return boardPromotions;
	}

	protected AccommodationRateCheckRS mapRateCheck(DetailsRS detailsRS, RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		AccommodationRateCheckRS rateCheckRS = new AccommodationRateCheckRS();
		String property_id = new String(CHANNEL_PREFIX + detailsRS.getEstablishment().getEstablishmentId());
		Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(property_id);
		if (!accommodationRCOpt.isPresent())
		{
			if (log.isDebugEnabled())
				log.debug("mapRateCheck::rc not found, bypassing");
			return null;
		}
		AccommodationRC accommodationRC = accommodationRCOpt.get();
		String property_name = accommodationRC.getAccommodationName();

		AccommodationProperty property = new AccommodationProperty();
		property.setCode(property_id);
		property.setChannel(YalagoService.CHANNEL);
		property.setChannelCode(detailsRS.getEstablishment().getEstablishmentId().toString());
		property.setAccommodationName(property_name);
		rateCheckRS.setProperty(property);
		rateCheckRS.setRooms(loadRateCheckRooms(detailsRS.getEstablishment().getRooms(), property_id, rateCheckRQDTO));

		if (detailsRS.getInfoItems() != null)
		{
			for (InfoItem infoItem : detailsRS.getInfoItems())
			{
				String errata = infoItem.getTitle() + " : " + infoItem.getDescription();
				rateCheckRS.getProperty().getErrata().add(errata);
			}
		}
		return rateCheckRS;
	}

	AccommodationBookRS mapBooking(BookingRS bookingRS)
	{
		if (log.isDebugEnabled())
			log.debug("mapBooking::enter");
		AccommodationBookRS bookRS = new AccommodationBookRS();
		bookRS.setBookingReference(bookingRS.getBookingRef());
		bookRS.setInternalBookingReference(bookingRS.getAffiliateRef());

		if (bookingRS.getInfoItems() != null)
		{
			for (InfoItem infoItem : bookingRS.getInfoItems())
			{
				String remark = infoItem.getTitle() + " : " + infoItem.getDescription();
				bookRS.getRemarks().add(remark);
			}
		}
		if (log.isDebugEnabled())
			log.debug("mapBooking::bookingRS has " + bookingRS.getRooms().size() + " rooms");
		for (BookedRoom room : bookingRS.getRooms())
		{
			ResponseItem responseItem = new ResponseItem();
			responseItem.setChannel(CHANNEL);
			responseItem.setItemRemark("");
			responseItem.setBookingItemReference(room.getProviderRef());
			responseItem.setInternalItemReference(room.getAffiliateRoomRef());
			switch (bookingRS.getStatus())
			{
				case 1: // UNKNOWN
					responseItem.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.FAILED);
					break;
				case 2: // GOOD
					responseItem.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.BOOKED);
					break;
				case 3: // FAIL
					responseItem.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.FAILED);
					break;
				default: // UNKNOWN
					responseItem.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.FAILED);
					break;
			}
			bookRS.getItems().add(responseItem);
		}
		return bookRS;
	}

	protected PromotionType derivePromotionType(Integer inclusionId)
	{
		if (inclusionId == 4 || inclusionId == 12 || inclusionId == 13 || inclusionId == 24 || inclusionId == 25)
			return PromotionType.VALUEADD;
		else
			return PromotionType.MEAL;
	}

	public AccommodationRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		log.info("search::rateCheck with " + rateCheckRQDTO);

		DetailsRQ detailsRQ = new DetailsRQ();
		Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(rateCheckRQDTO.getHotelId());
		if (accommodationRCOpt.isPresent())
		{
			detailsRQ.setEstablishmentId(Integer.parseInt(accommodationRCOpt.get().getChannelCode()));
		}
		else
		{
			log.error("search::accommdation " + rateCheckRQDTO.getHotelId() + " not found");
			throw new Exception("Invalid hotelId provided");
		}
		String countryCodeOfOrigin = rateCheckRQDTO.getCountryCodeOfOrigin();
		if (countryCodeOfOrigin == null || countryCodeOfOrigin.length() == 0)
		{
			countryCodeOfOrigin = propertiesDAO.getProperty(rateCheckRQDTO.getClient(), CHANNEL, "sourceMarket", "GB");
		}
		detailsRQ.setSourceMarket(countryCodeOfOrigin);
		detailsRQ.setCheckInDate(formatterYYYYMMDD.format(rateCheckRQDTO.getCheckin()));
		detailsRQ.setCheckOutDate(formatterYYYYMMDD.format(rateCheckRQDTO.getCheckout()));
		int index = 0;
		for (TravellerMix travellers : rateCheckRQDTO.getTravellers())
		{
			DetailsRQ.Room room = new DetailsRQ.Room();
			room.setAdults(travellers.getAdultCount());
			room.setChildAges(travellers.getChildAges());
			BookingCodeStruct unmakeBookingCode = unmakeBookingCode(rateCheckRQDTO.getBookingCodes().get(index));
			room.setRoomCode(unmakeBookingCode.roomCode);
			room.setBoardCode(unmakeBookingCode.boardCode);
			detailsRQ.getRooms().add(room);
			index++;
		}

		try
		{
			YalagoInterface yalagoInterface = new YalagoInterface(propertiesDAO, accommodationRCRepo, rateCheckRQDTO.getClient(), CHANNEL);
			long timer1 = System.currentTimeMillis();
			long timer2 = System.currentTimeMillis();
			DetailsRS detailsRS = yalagoInterface.directRateCheck(detailsRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("rateCheck::time in rateCheck was " + totalTime2 + " millis");
			if (detailsRS.getEstablishment() == null)
			{
				log.warn("rateCheck::failed");
				if (detailsRS.getMessage() != null)
				{
					throw new Exception("Error in Yalago Service:" + detailsRS.getMessage());
				}
				throw new Exception("Error in Yalago Service:network error");
			}
			AccommodationRateCheckRS rateCheckRS = mapRateCheck(detailsRS, rateCheckRQDTO);
			log.info("rateCheck::time taken = " + (System.currentTimeMillis() - timer1));
			return rateCheckRS;
		}
		catch (Exception e)
		{
			log.error("rateCheck::threw exception " + e.toString(), e);
			throw new Exception("Error in Yalago Service:" + e.toString());
		}
	}

	public AccommodationBookRS book(String client, AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);

		BookingRQ bookingRQ = new BookingRQ();
		bookingRQ.setAffiliateRef(bookRQ.getInternalBookingReference());
		LocalDate checkin = null;
		LocalDate checkout = null;
		String hotelId = null;
		Date now = new Date();
		String countryCodeOfOrigin = bookRQ.getCountryCodeOfOrigin();
		if (countryCodeOfOrigin == null || countryCodeOfOrigin.length() == 0)
		{
			countryCodeOfOrigin = propertiesDAO.getProperty(client, CHANNEL, "sourceMarket", "GB");
		}
		bookingRQ.setSourceMarket(countryCodeOfOrigin);

		int itemIndex = 0;
		for (AccommodationBookRQ.AccommodationRequestItem item : bookRQ.getItems())
		{
			// eRoam sometimes forget to put dates in multirooms!
			if (checkin == null && item.getCheckin() != null)
				checkin = item.getCheckin();
			if (checkout == null && item.getCheckout() != null)
				checkout = item.getCheckout();
			if (hotelId == null)
				hotelId = item.getHotelID();
			if (checkin != null && checkout != null && item.getCheckin() != null && item.getCheckout() != null && (!checkin.equals(item.getCheckin()) || !checkout.equals(item.getCheckout()) || !hotelId.equals(item.getHotelID())))
			{
				throw new Exception("Multi-room bookings allowed, multi-date not allowed");
			}
			BookingRQ.Room room = new BookingRQ.Room();
			bookingRQ.getRooms().add(room);
			room.setAffiliateRef(item.getInternalItemReference());
			room.setAffiliateRoomRef(item.getInternalItemReference());
			BookingCodeStruct unmakeBookingCode = unmakeBookingCode(item.getBookingCode());
			room.setRoomCode(unmakeBookingCode.roomCode);
			room.setBoardCode(unmakeBookingCode.boardCode);
			BigDecimal extraNetAmount = BigDecimal.ZERO;
			BigDecimal extraAmount = BigDecimal.ZERO;
			if (log.isDebugEnabled())
				log.debug("book::unmakeBookingCode.extraId= " + unmakeBookingCode.extraId);
			if (unmakeBookingCode.extraId != null)
			{
				BookingRQ.BookedRoomExtra bookedRoomExtra = new BookingRQ.BookedRoomExtra();
				bookedRoomExtra.setExtraId(Integer.parseInt(unmakeBookingCode.extraId));
				bookedRoomExtra.setOptionId(Integer.parseInt(unmakeBookingCode.extraOptionId));
				bookedRoomExtra.setExpectedNetCost(new Money());
				bookedRoomExtra.getExpectedNetCost().setCurrency(item.getSupplyRate().getCurrencyId());
				;
				bookedRoomExtra.getExpectedNetCost().setAmount(unmakeBookingCode.optionNetAmount);
				extraNetAmount = unmakeBookingCode.optionNetAmount;
				// bookedRoomExtra.setExpectedCost(new Money());
				// bookedRoomExtra.getExpectedCost().setCurrency(item.getSupplyRate().getCurrencyId());
				// bookedRoomExtra.getExpectedCost().setAmount(unmakeBookingCode.optionAmount);
				extraAmount = unmakeBookingCode.optionAmount;
				room.getExtras().add(bookedRoomExtra);
			}
			room.setExpectedNetCost(new Money());
			room.getExpectedNetCost().setCurrency(item.getSupplyRate().getCurrencyId());
			room.getExpectedNetCost().setAmount(item.getSupplyRate().getAmount().subtract(extraNetAmount));
			StringBuffer specialRequests = new StringBuffer();
			for (SpecialRequest specialRequest : item.getSpecialRequests())
			{
				if (specialRequests.length() > 0)
					specialRequests.append(", ");
				specialRequests.append(specialRequest.getValue());
			}
			room.setSpecialRequests(specialRequests.toString());
			for (Integer travellerindex : item.getTravellerIndex())
			{
				Traveller traveller = bookRQ.getTravellers().get(travellerindex);
				BookingRQ.Guest guest = new BookingRQ.Guest();
				guest.setTitle(traveller.getTitle());
				guest.setFirstName(traveller.getGivenName());
				guest.setLastName(traveller.getSurname());
				guest.setAge(traveller.getAge(now));
				room.getGuests().add(guest);
			}
			int adultCount = 0;
			for (Guest guest : room.getGuests())
			{
				if (log.isDebugEnabled())
					log.debug("book::item " + itemIndex + ", guest age=" + guest.getAge());
				if (guest.getAge() >= 18)
					adultCount++;
			}
			if (adultCount == 0)
			{
				log.error("book::each room must have at least one adult");
				throw new Exception("Each room must have at least one adult >= 18 years old");
			}
			itemIndex++;
		}
		bookingRQ.setCheckInDate(formatterYYYYMMDD.format(checkin));
		bookingRQ.setCheckOutDate(formatterYYYYMMDD.format(checkout));
		Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(hotelId);
		if (accommodationRCOpt.isPresent())
		{
			bookingRQ.setEstablishmentId(Integer.parseInt(accommodationRCOpt.get().getChannelCode()));
		}
		else
		{
			log.error("book::accommdation " + hotelId + " not found");
			throw new Exception("Invalid hotelId provided");
		}
		bookingRQ.setContactDetails(new ContactDetails());
		bookingRQ.getContactDetails().setTitle(bookRQ.getBooker().getTitle());
		bookingRQ.getContactDetails().setFirstName(bookRQ.getBooker().getGivenName());
		bookingRQ.getContactDetails().setLastName(bookRQ.getBooker().getSurname());

		long timer1 = System.currentTimeMillis();
		try
		{
			YalagoInterface yalagoInterface = new YalagoInterface(propertiesDAO, accommodationRCRepo, client, CHANNEL);
			BookingRS bookingRS = yalagoInterface.book(bookingRQ);
			long totalTime2 = (System.currentTimeMillis() - timer1);
			log.info("book::time in bookings was " + totalTime2 + " millis");
			if (bookingRS.getEstablishment() == null)
			{
				if (bookingRS.getErrorMessage() != null)
				{
					log.warn("book::failed:bookingRS.getErrorMessage()=" + bookingRS.getErrorMessage());
					throw new Exception("Error in Yalago Service:" + bookingRS.getErrorMessage());
				}
				if (bookingRS.getMessage() != null)
				{
					log.warn("book::failed:bookingRS.getMessage()=" + bookingRS.getMessage());
					throw new Exception(
							"Error in Yalago Service:" + (bookingRS.getErrorCode() != null && bookingRS.getErrorCode().length() > 0 ? bookingRS.getErrorCode() + ":" : "") + bookingRS.getMessage());
				}
				throw new Exception("Error in Yalago Service:network error");
			}
			AccommodationBookRS bookRS = mapBooking(bookingRS);
			log.info("book::time taken = " + (System.currentTimeMillis() - timer1));
			return bookRS;
		}
		catch (Exception e)
		{
			log.error("book::threw exception " + e.toString(), e);
			if (e.getMessage().contains("Error in Yalago Service"))
				throw new Exception(e.getMessage());
			else
				throw new Exception("Error in Yalago Service:" + e.getMessage());
		}
	}

	public AccommodationCancelRS cancel(String site, AccommodationCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::received " + cancelRQ);

		long timer1 = System.currentTimeMillis();
		try
		{
			YalagoCancelChargeRQ yalagoCancelChargeRQ = new YalagoCancelChargeRQ();
			yalagoCancelChargeRQ.setBookingRef(cancelRQ.getBookingReference());

			YalagoInterface yalagoInterface = new YalagoInterface(propertiesDAO, accommodationRCRepo, site, CHANNEL);
			YalagoCancelChargeRS yalagoCancelChargeRS = yalagoInterface.cancelCharge(yalagoCancelChargeRQ);
			log.info("cancel::charge time taken = " + (System.currentTimeMillis() - timer1));

			if (!yalagoCancelChargeRS.getIsCancellable())
			{
				log.error("cancel::Yalago says item is non-cancellable");
				throw new Exception("Error in Yalago Service:item is non-cancellable");
			}

			YalagoCancelRQ yalagoCancelRQ = new YalagoCancelRQ();
			yalagoCancelRQ.setBookingRef(cancelRQ.getBookingReference());
			YalagoCancelRQ.ExpectedCharge expectedCharge = new YalagoCancelRQ.ExpectedCharge();
			yalagoCancelRQ.setExpectedCharge(expectedCharge);
			expectedCharge.setCharge(yalagoCancelChargeRS.getCharge().getCharge());

			if (log.isDebugEnabled())
				log.debug("cancel::CNX charge =  " + yalagoCancelChargeRS.getCharge().getCharge());

			long timer2 = System.currentTimeMillis();
			YalagoCancelRS yalagoCancelRS = yalagoInterface.cancel(yalagoCancelRQ);
			log.info("cancel::time taken = " + (System.currentTimeMillis() - timer2));

			CurrencyValue cancellationCharge = new CurrencyValue(yalagoCancelChargeRS.getCharge().getCharge().getCurrency(), yalagoCancelChargeRS.getCharge().getCharge().getAmount());
			AccommodationCancelRS cancelRS = new AccommodationCancelRS(yalagoCancelRS.getBookingRef(), cancellationCharge);
			return cancelRS;
		}
		catch (Exception e)
		{
			log.error("cancel::threw exception " + e.toString(), e);
			throw new Exception("Error in Yalago Service:" + e.getMessage());
		}
	}

	public AccommodationRetrieveRS retrieve(String site, AccommodationRetrieveRQ retrieveRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("retrieve::received " + retrieveRQ);

		long timer1 = System.currentTimeMillis();
		try
		{
			YalagoGetBookingRQ yalagoGetBookingRQ = new YalagoGetBookingRQ();
			yalagoGetBookingRQ.setBookingRef(retrieveRQ.getBookingReference());

			YalagoInterface yalagoInterface = new YalagoInterface(propertiesDAO, accommodationRCRepo, site, CHANNEL);
			YalagoGetBookingRS yalagoGetBookingRS = yalagoInterface.getBooking(yalagoGetBookingRQ);
			log.info("retrieve::charge time taken = " + (System.currentTimeMillis() - timer1));

			AccommodationRetrieveRS retrieveRS = new AccommodationRetrieveRS();

			switch (yalagoGetBookingRS.getStatus())
			{
				case 1: // UNKNOWN
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.FAILED);
					break;
				case 2: // GOOD
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.BOOKED);
					break;
				case 3: // FAIL
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.FAILED);
					break;
				case 4: // GOOD
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.BOOKED);
					break;
				default: // UNKNOWN
					retrieveRS.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.FAILED);
					break;
			}
			return retrieveRS;
		}
		catch (Exception e)
		{
			log.error("retrieve::threw exception " + e.toString(), e);
			throw new Exception("Error in Yalago Service:" + e.getMessage());
		}
	}

	protected String makeBookingCode(String roomCode, String boardCode)
	{
		return roomCode + "_" + boardCode;
	}

	@ToString
	public static class BookingCodeStruct
	{
		String roomCode;

		String boardCode;

		String extraId;

		String extraOptionId;

		BigDecimal optionNetAmount;

		BigDecimal optionAmount;
	}

	protected BookingCodeStruct unmakeBookingCode(String bookingCode)
	{
		String[] parts = bookingCode.split("_");
		BookingCodeStruct bookingCodeStruct = new BookingCodeStruct();
		bookingCodeStruct.roomCode = parts[0];
		bookingCodeStruct.boardCode = parts[1];
		if (parts.length > 2)
		{
			bookingCodeStruct.extraId = parts[2];
			bookingCodeStruct.extraOptionId = parts[3];
			bookingCodeStruct.optionNetAmount = new BigDecimal(parts[4]);
			bookingCodeStruct.optionAmount = new BigDecimal(parts[5]);
		}
		return bookingCodeStruct;
	}

	private String mapBoardCode(Integer ylBoardCode)
	{
		switch (ylBoardCode)
		{
			case 1:
				return "RO";
			case 2:
				return "Bed and Breakfast";
			case 3:
				return "HB";
			case 4:
				return "FB";
			case 5:
				return "AI";
			case 6:
				return "SC";
			case 7:
				return "AB";
			case 59:
				return "CB";
			case 64:
				return "DB";
			default:
				return "OT";
		}
	}

	private static final DateTimeFormatter formatterYYYYMMDDhhmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private static final DateTimeFormatter formatterDDMMMYYYY = DateTimeFormatter.ofPattern("dd MMM yyyy");

	private static final BigDecimal BD_100 = new BigDecimal("100");

	private static final BigDecimal STANDARD_MARKUP = new BigDecimal("1.20");

	private static DateTimeFormatter formatterYYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("Source Market, e.g. 'GB'", "sourceMarket", FieldType.STRING, false, "GB"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("URL endpoint of Yalago API", "url", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The Yalago APIKey", "apikey", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("If bookings should be 'faked' and NOT sent to the server, just return a dummy confirmation", "bypassBooking", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}
}
