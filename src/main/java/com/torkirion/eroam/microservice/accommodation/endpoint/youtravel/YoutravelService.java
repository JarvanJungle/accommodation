package com.torkirion.eroam.microservice.accommodation.endpoint.youtravel;

import com.hotelbeds.schemas.messages.*;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomExtraFee.FeeType;
import com.torkirion.eroam.microservice.accommodation.apidomain.RoomPromotion.PromotionType;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.BoardCodes;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@AllArgsConstructor
@Slf4j
public class YoutravelService implements AccommodationServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private YoutravelStaticRepo youtravelStaticRepo;

	private static YoutravelCache _youtravelCache = null;

	public static final String CHANNEL = "YOUTRAVEL";

	public static final String CHANNEL_PREFIX = "YT";

	public List<AccommodationResult> searchByHotelId(AvailSearchByHotelIdRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByHotelIdRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		AvailabilityRS availabilityRS = null;
		List<AccommodationResult> results = new ArrayList<>();

		try
		{
			YoutravelInterface youtravelInterface = new YoutravelInterface(propertiesDAO, availSearchRQ.getClient(), CHANNEL);
			long timer2 = System.currentTimeMillis();
			availabilityRS = youtravelInterface.startSearchHotels(availSearchRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("search::time in hotelbeds search was " + totalTime2 + " millis");
			if (availabilityRS == null || availabilityRS.getHotels() == null || availabilityRS.getHotels().getHotel() == null || availabilityRS.getHotels().getHotel().size() == 0)
			{
				log.debug("search::availabilityRS returned no lists");
			}
			else
			{
				int listNo = 0;
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned " + availabilityRS.getHotels().getHotel().size() + " hotels");
				for (HotelResponse hotelResponse : availabilityRS.getHotels().getHotel())
				{
					if (log.isDebugEnabled())
						log.debug("search::processing item " + listNo);
					AccommodationResult accommodationResult = new AccommodationResult();
					String property_id = new String(CHANNEL_PREFIX + hotelResponse.getCode());
					AccommodationProperty property = new AccommodationProperty();
					property.setCode(property_id);
					property.setChannel(YoutravelService.CHANNEL);
					property.setChannelCode(hotelResponse.getCode());
					// rest of the details are set in the AccommodationSearchService
					accommodationResult.setProperty(property);

					accommodationResult.setRooms(loadRooms(youtravelInterface, hotelResponse.getRooms().getRoom(), hotelResponse.getCurrency(), availSearchRQ, property_id));

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
			}
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	public List<AccommodationResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		AvailabilityRS availabilityRS = null;
		List<AccommodationResult> results = new ArrayList<>();

		try
		{
			YoutravelInterface youtravelInterface = new YoutravelInterface(propertiesDAO, availSearchRQ.getClient(), CHANNEL);
			long timer2 = System.currentTimeMillis();
			availabilityRS = youtravelInterface.startSearchHotels(availSearchRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("search::time in hotelbeds search was " + totalTime2 + " millis");
			if (availabilityRS == null || availabilityRS.getHotels() == null || availabilityRS.getHotels().getHotel() == null || availabilityRS.getHotels().getHotel().size() == 0)
			{
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned no lists");
			}
			else
			{
				int listNo = 0;
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned " + availabilityRS.getHotels().getHotel().size() + " hotels");
				for (HotelResponse hotelResponse : availabilityRS.getHotels().getHotel())
				{
					if (log.isDebugEnabled())
						log.debug("search::processing item " + listNo);
					AccommodationResult accommodationResult = new AccommodationResult();
					String property_id = new String(CHANNEL_PREFIX + hotelResponse.getCode());
					AccommodationProperty property = new AccommodationProperty();
					property.setCode(property_id);
					property.setChannel(YoutravelService.CHANNEL);
					property.setChannelCode(hotelResponse.getCode());
					// rest of the details are set in the AccommodationSearchService
					accommodationResult.setProperty(property);

					accommodationResult.setRooms(loadRooms(youtravelInterface, hotelResponse.getRooms().getRoom(), hotelResponse.getCurrency(), availSearchRQ, property_id));

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
			}
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	public AccommodationRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDTO)
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter for " + rateCheckRQDTO);

		long timer1 = System.currentTimeMillis();

		try
		{
			YoutravelInterface youtravelInterface = new YoutravelInterface(propertiesDAO, rateCheckRQDTO.getClient(), CHANNEL);

			AccommodationRateCheckRS rateCheckRS = new AccommodationRateCheckRS();
			rateCheckRS.setRooms(new ArrayList<>());
			int roomNumber = 1;

			List<String> rateKeys = new ArrayList<>();
			for (String rateKeyQuery : rateCheckRQDTO.getBookingCodes())
			{
				if (log.isDebugEnabled())
					log.debug("rateCheck::testing rateKey " + rateKeyQuery);
				rateKeys.add(rateKeyQuery);
			}
				// RoomRateCheck roomRateCheck = new RoomRateCheck();
				CheckRateRS checkRateRS = youtravelInterface.checkRates(rateCheckRQDTO);
				if (checkRateRS != null && checkRateRS.getHotel() != null && checkRateRS.getHotel().getRooms() != null)
				{
					AccommodationProperty property = new AccommodationProperty();
					property.setCode(CHANNEL_PREFIX + checkRateRS.getHotel().getCode());
					property.setChannel(YoutravelService.CHANNEL);
					property.setChannelCode(checkRateRS.getHotel().getCode());
					property.setAccommodationName(checkRateRS.getHotel().getName());
					rateCheckRS.setProperty(property);
					if (log.isDebugEnabled())
						log.debug("rateCheck::returned " + checkRateRS.getHotel().getRooms().getRoom().size() + " rooms");
					for (RoomHotelResponse roomHotelResponse : checkRateRS.getHotel().getRooms().getRoom())
					{
						if (log.isDebugEnabled())
							log.debug("rateCheck::looping roomHotelResponse, roomNumber=" + roomNumber);
						rateLoop: for (RateHotelResponse rateHotelResponse : roomHotelResponse.getRates().getRate())
						{
							for (String rateKey : rateKeys)
							{
								if (log.isDebugEnabled())
									log.debug("rateCheck::testing roomNumber " + roomNumber + " response rateKey " + rateHotelResponse.getRateKey() + " against desired " + rateKey);
								if (rateHotelResponse.getRateKey().equals(rateKey))
								{
									if (log.isDebugEnabled())
										log.debug("rateCheck::ratekey found");

									RoomResult room = makeRoomBase(roomHotelResponse, rateHotelResponse, null, roomNumber, property.getCode(),
											checkRateRS.getHotel().getCurrency());
									if (room == null)
									{
										if (log.isDebugEnabled())
											log.debug("rateCheck::room from makeRoom is null");
										continue rateLoop;
									}
									if (rateHotelResponse.getRateCommentsId() != null && rateHotelResponse.getRateCommentsId().length() > 0)
									{
										loadRateComments(youtravelInterface, rateHotelResponse.getRateCommentsId(), property.getCode(), rateCheckRQDTO.getCheckin(), room);
									}
									if (rateHotelResponse.getRateComments() != null && rateHotelResponse.getRateComments().length() > 0)
									{
										RoomExtraFee extraFee = new RoomExtraFee();
										extraFee.setDescription(rateHotelResponse.getRateComments());
										extraFee.setFeeType(FeeType.FeeComments);
										room.getExtraFees().add(extraFee);
										if (log.isDebugEnabled())
											log.debug("rateCheck::adding rateComment " + rateHotelResponse.getRateComments());
									}
									makeRoomPromotions(room, rateHotelResponse);
									makeRoomOffers(room, rateHotelResponse, checkRateRS.getHotel().getCurrency());
									makeRoomCNX(room, rateHotelResponse, checkRateRS.getHotel().getCurrency(), rateCheckRQDTO.getCheckin(), rateCheckRQDTO.getCheckout());
									room.setRoomExtraInformation(makeERoamExtraRoomInformation(room));
									Integer hashMatch = (room.getRoomName() + room.getCancellationPolicyText() + room.getBoardDescription()).hashCode(); 
									room.setMatchCode(hashMatch.toString());
									
									rateCheckRS.getRooms().add(room);
									//roomNumber++;
								}
							}
						}
					}
				}
			
			if (log.isDebugEnabled())
				log.debug("rateCheck::returning " + rateCheckRS.getRooms().size() + " rateKeys");
			log.info("rateCheck:: time taken = " + (System.currentTimeMillis() - timer1));
			return rateCheckRS;
		}
		catch (Exception e)
		{
			log.error("rateCheck::threw exception " + e.toString(), e);
		}

		return null;
	}

	public AccommodationBookRS book(String site, AccommodationBookRQ bookRQ)
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);
		long timer1 = System.currentTimeMillis();
		try
		{
			YoutravelInterface youtravelInterface = new YoutravelInterface(propertiesDAO, site, CHANNEL);
			AccommodationBookRS bookRS = youtravelInterface.book(bookRQ);
			log.info("book:: time taken = " + (System.currentTimeMillis() - timer1));
			return bookRS;
		}
		catch (Exception e)
		{
			log.error("book::threw exception " + e.toString(), e);
		}
		return null;
	}

	public AccommodationCancelRS cancel(String site, AccommodationCancelRQ cancelRQ)
	{
		if (log.isDebugEnabled())
			log.debug("cancel::received " + cancelRQ);
		long timer1 = System.currentTimeMillis();
		try
		{
			YoutravelInterface youtravelInterface = new YoutravelInterface(propertiesDAO, site, CHANNEL);
			AccommodationCancelRS cancelRS = youtravelInterface.cancel(cancelRQ);
			log.info("cancel:: time taken = " + (System.currentTimeMillis() - timer1));
			return cancelRS;
		}
		catch (Exception e)
		{
			log.error("cancel::threw exception " + e.toString(), e);
		}
		return null;
	}

	public AccommodationRetrieveRS retrieve(String site, AccommodationRetrieveRQ retrieveRQ)
	{
		if (log.isDebugEnabled())
			log.debug("retrieve::received " + retrieveRQ);
		long timer1 = System.currentTimeMillis();
		try
		{
			YoutravelInterface youtravelInterface = new YoutravelInterface(propertiesDAO, site, CHANNEL);
			AccommodationRetrieveRS retrieveRS = youtravelInterface.retrieve(retrieveRQ);
			log.info("retrieve:: time taken = " + (System.currentTimeMillis() - timer1));
			return retrieveRS;
		}
		catch (Exception e)
		{
			log.error("retrieve::threw exception " + e.toString(), e);
		}
		return null;
	}

	private SortedSet<RoomResult> loadRooms(YoutravelInterface youtravelInterface, List<RoomHotelResponse> roomRateDetailsList, String currency, AvailSearchRQDTO availSearchRQ,
											String hotelbedsHotelCode) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("loadRooms::loading rooms, roomRateDetailsList.size=" + (roomRateDetailsList == null ? 0 : roomRateDetailsList.size()));

		if (roomRateDetailsList == null || roomRateDetailsList.size() == 0)
		{
			return null;
		}

		long timer1 = System.currentTimeMillis();

		SortedSet<RoomResult> rooms = new TreeSet<>();
		
		int roomCount = availSearchRQ.getTravellers().size();

		Set<String> existingRoomCodes = new HashSet<String>();
		for (RoomHotelResponse roomHotelResponse : roomRateDetailsList)
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::processing room " + roomHotelResponse.getCode());
			if (log.isDebugEnabled())
				log.debug("loadRooms::loading rooms, hotelRoomResponseBlock.getRates().getRate().size="
						+ (roomHotelResponse.getRates().getRate() == null ? 0 : roomHotelResponse.getRates().getRate().size()));
			rateLoop: for (RateHotelResponse rateHotelResponse : roomHotelResponse.getRates().getRate())
			{
				if (log.isDebugEnabled())
					log.debug("loadRooms::processing rateKey '" + rateHotelResponse.getRateKey() + "' with nett " + rateHotelResponse.getNet());

				for ( int roomIdx = 1; roomIdx <= roomCount; roomIdx++)
				{
					TravellerMix travellerCheck = availSearchRQ.getTravellers().get(roomIdx-1);
					if ( travellerCheck.getAdultCount() != rateHotelResponse.getAdults().intValue() || travellerCheck.getChildAges().size() != rateHotelResponse.getChildren().intValue() )
					{
						if (log.isDebugEnabled())
							log.debug("loadRooms::bypassing roomIdx " + roomIdx + " mix " + travellerCheck + " for " + rateHotelResponse.getAdults() + "/" + travellerCheck.getChildAges() + "/" + rateHotelResponse.getChildrenAges());
						continue;
					}
					if ( rateHotelResponse.getAllotment() != null && rateHotelResponse.getAllotment().intValue() < availSearchRQ.getTravellers().size())
					{
						if (log.isDebugEnabled())
							log.debug("loadRooms::bypassing roomIdx " + roomIdx + " since allotment " + rateHotelResponse.getAllotment() + " less than rooms requested " + availSearchRQ.getTravellers().size());
						continue;
					}
					if ( travellerCheck.getChildAges().size() > 0 )
					{
						String childAgeCheck = "," + rateHotelResponse.getChildrenAges() + ",";
						for ( Integer childAge : travellerCheck.getChildAges())
						{
							if ( !childAgeCheck.contains(childAge.toString()))
							{
								if (log.isDebugEnabled())
									log.debug("loadRooms::bypassing roomIdx " + roomIdx + " mix " + travellerCheck + " for " + rateHotelResponse.getAdults() + "/" + rateHotelResponse.getChildren() + "/" + rateHotelResponse.getChildrenAges());
								continue;
							}
						}
					}
					RoomResult room = makeRoomBase(roomHotelResponse, rateHotelResponse, existingRoomCodes, roomIdx, hotelbedsHotelCode, currency);
					if (room == null)
					{
						if (log.isDebugEnabled())
							log.debug("loadRooms::room from makeRoom is null");
						continue rateLoop;
					}
					if (rateHotelResponse.getRateCommentsId() != null && rateHotelResponse.getRateCommentsId().length() > 0)
					{
						loadRateComments(youtravelInterface, rateHotelResponse.getRateCommentsId(), hotelbedsHotelCode, availSearchRQ.getCheckin(), room);
					}
					makeRoomPromotions(room, rateHotelResponse);
					makeRoomOffers(room, rateHotelResponse, currency);
					makeRoomCNX(room, rateHotelResponse, currency, availSearchRQ.getCheckin(), availSearchRQ.getCheckout());
	
					RoomExtraFee extraFee = new RoomExtraFee();
					extraFee.setDescription("Local charges (taxes and fees) if applicable will be displayed during checkout");
					extraFee.setFeeType(FeeType.CheckinFees);
					room.getExtraFees().add(extraFee);
					Integer hashMatch = (room.getRoomName() + room.getCancellationPolicyText() + room.getBoardDescription()).hashCode(); 
					room.setMatchCode(hashMatch.toString());
	
					if (log.isDebugEnabled())
						log.debug("loadRooms::adding room with rate :" + room.getSupplyRate() + " code " + room.getBookingCode());
					rooms.add(room);
				}
			}
		}
		if (log.isDebugEnabled())
			log.debug("loadRooms::roomcount=" + rooms.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return rooms;
	}

	protected RoomResult makeRoomBase(RoomHotelResponse roomHotelResponse, RateHotelResponse rateHotelResponse, Set<String> existingRoomCodes, Integer roomNumber, String hotelbedsHotelCode,
			String currency) throws Exception
	{
		String roomCode = roomHotelResponse.getCode();
		String standardDescription = roomHotelResponse.getName();
		String roomname = standardDescription;

		if (log.isDebugEnabled())
			log.debug("makeRoomBase::processing rateKey '" + rateHotelResponse.getRateKey() + "' with nett " + rateHotelResponse.getNet());
		String fullRoomCode = roomCode + "_" + rateHotelResponse.getRateClass() + "_" + rateHotelResponse.getRateType() + "_" + rateHotelResponse.getBoardCode() + "_"
				+ rateHotelResponse.getPaymentType().replaceAll("_", "");
		if (existingRoomCodes != null && existingRoomCodes.contains(fullRoomCode + "R" + roomNumber))
		{
			if (log.isDebugEnabled())
				log.debug("makeRoomBase::duplicate roomCode info '" + (fullRoomCode + "R" + roomNumber) + "', bypassing");
			return null;
		}
		if (existingRoomCodes != null)
			existingRoomCodes.add(fullRoomCode + "R" + roomNumber);

		RoomResult room = new RoomResult();
		room.setChannel(CHANNEL);
		room.setChannelPropertyCode(hotelbedsHotelCode);
		room.setRoomNumber(roomNumber.intValue());
		room.setRoomCode(fullRoomCode);
		room.setBedding("");
		room.setRoomStandard("");
		StringBuffer roomName = new StringBuffer(Functions.cleanHTML(roomname));
		String boardCode = rateHotelResponse.getBoardCode();
		room.setBoardCode(mapBoardCode(rateHotelResponse.getBoardCode()));
		Optional<String> boardDescriptionOpt = getHotelbedsCache().getBoard(boardCode);
		if (log.isDebugEnabled())
			log.debug("makeRoomBase::found boardDescription '" + boardDescriptionOpt + "' for boardCode " + boardCode);
		if (boardDescriptionOpt.isPresent())
		{
			roomName.append(" - " + boardDescriptionOpt.get());
			room.setBoardDescription(boardDescriptionOpt.get());
		}
		else if (rateHotelResponse.getBoardName() != null && rateHotelResponse.getBoardName().length() > 0)
		{
			roomName.append(" - " + WordUtils.capitalizeFully(rateHotelResponse.getBoardName()));
			room.setBoardDescription(WordUtils.capitalizeFully(rateHotelResponse.getBoardName()));
		}
		else
		{
			room.setBoardDescription(BoardCodes.mapBoardDescription(rateHotelResponse.getBoardCode()));
		}
		room.setRoomName(Functions.cleanHTML(roomName.toString()));
		if (log.isDebugEnabled())
			log.debug("makeRoomBase::created hrt code=" + room.getRoomCode() + " name=" + room.getRoomName());
		BigDecimal nettPrice_bd = rateHotelResponse.getNet();
		BigDecimal totalPrice_bd = null;
		if (rateHotelResponse.getSellingRate() != null)
		{
			totalPrice_bd = rateHotelResponse.getSellingRate();
			if (log.isDebugEnabled())
				log.debug("makeRoomBase::found fixed sellPrice of " + totalPrice_bd);
		}
		else
		{
			totalPrice_bd = YoutravelInterface.applyInventoryMarkup(rateHotelResponse.getNet(), null);
			if (log.isDebugEnabled())
				log.debug("makeRoomBase::applying markup");
		}
		BigDecimal fullCommission = totalPrice_bd.subtract(nettPrice_bd).multiply(Functions.BD_100).divide(totalPrice_bd, 2, RoundingMode.HALF_DOWN);
		BigDecimal numberOfRooms = BigDecimal.ONE;
		if (rateHotelResponse.getRooms() != null)
		{
			numberOfRooms = new BigDecimal(rateHotelResponse.getRooms());
			if (numberOfRooms.compareTo(BigDecimal.ONE) != 0)
			{
				nettPrice_bd = nettPrice_bd.divide(numberOfRooms, 2, RoundingMode.HALF_EVEN);
				totalPrice_bd = totalPrice_bd.divide(numberOfRooms, 2, RoundingMode.HALF_EVEN);
				if (log.isDebugEnabled())
					log.debug("makeRoomBase::multi-rooms, dividing by " + numberOfRooms + " to get net " + nettPrice_bd + " and gross " + totalPrice_bd);
			}
		}
		CurrencyValue nettRate = new CurrencyValue(currency, nettPrice_bd);
		room.setSupplyRate(nettRate);
		CurrencyValue totalRate = new CurrencyValue(currency, totalPrice_bd);
		if (rateHotelResponse.isHotelMandatory() != null && rateHotelResponse.isHotelMandatory())
			room.setRrpIsMandatory(true);
		room.setTotalRate(totalRate);
		if (log.isDebugEnabled())
			log.debug("makeRoomBase::nettRate=" + nettRate + ", totalRate=" + totalRate + ", mandatory=" + room.getRrpIsMandatory() + ", fullCommission=" + fullCommission);
		if (totalRate.getAmount().compareTo(nettRate.getAmount()) < 0)
		{
			log.warn("makeRoomBase::nettRate=" + nettRate + " less than totalRate=" + totalRate + " for hotel " + hotelbedsHotelCode + " room " + room.getRoomCode() + " " + room.getRoomName()
					+ ", bypassing");
			return null;
		}

		room.setRateCode(rateHotelResponse.getRateKey());
		room.setBookingCode(rateHotelResponse.getRateKey());
		room.setInventory(rateHotelResponse.getAllotment());
		if (rateHotelResponse.getRateType().equals("RECHECK"))
			room.setRequiresRecheck(true);
		else
			room.setRequiresRecheck(false);
		return room;
	}

	protected void makeRoomPromotions(RoomResult room, RateHotelResponse rateHotelResponse)
	{
		if (rateHotelResponse.getPromotions() != null && rateHotelResponse.getPromotions().getPromotion() != null)
		{
			for (RatePromotion ratePromotion : rateHotelResponse.getPromotions().getPromotion())
			{
				String promoCode = ratePromotion.getCode();
				if (promoCode.equals("073"))
				{
					if (log.isDebugEnabled())
						log.debug("makeRoomPromotions::bypass Non-refundable rate. No amendments permitted");
					continue;
				}
				Optional<String> descriptionOpt = getHotelbedsCache().getPromotion(promoCode);
				if (descriptionOpt.isPresent())
				{
					String description = descriptionOpt.get();
					if (ratePromotion.getRemark() != null && !ratePromotion.getRemark().equals(description))
					{
						description = description + ", " + ratePromotion.getRemark();
					}
					RoomPromotion promo = new RoomPromotion();
					promo.setPromoType(PromotionType.VALUEADD);
					promo.setShortMarketingText(description);
					room.getPromotions().add(promo);
					if (log.isDebugEnabled())
						log.debug("makeRoomPromotions::added promotion " + promo.getShortMarketingText() + " for HB promotion code " + promoCode);
				}
			}
		}
	}

	protected void makeRoomOffers(RoomResult room, RateHotelResponse rateHotelResponse, String currency)
	{
		if (rateHotelResponse.getOffers() != null && rateHotelResponse.getOffers().getOffer() != null)
		{
			offerLoop: for (Offer offer : rateHotelResponse.getOffers().getOffer())
			{
				String promoCode = offer.getCode();
				String description = offer.getName();
				Optional<String> codedDescriptionOpt = getHotelbedsCache().getPromotion(promoCode);
				if (codedDescriptionOpt.isPresent())
					description = codedDescriptionOpt.get();
				if (description == null)
				{
					if (log.isDebugEnabled())
						log.debug("makeRoomOffers::promotion " + promoCode + " has no description, bypassing");
					continue offerLoop;
				}
				boolean hasAmount = false;
				if (offer.getAmount() != null)
				{
					CurrencyValue discount = null;
					if (offer.getAmount().compareTo(BigDecimal.ZERO) < 0)
					{
						discount = new CurrencyValue(currency, offer.getAmount().multiply(Functions.BD_1_NEG));
					}
					else
					{
						discount = new CurrencyValue(currency, offer.getAmount());
					}
					hasAmount = true;
					description = description + " : " + Functions.formatCurrencyDisplay(discount);
				}
				RoomPromotion promo = new RoomPromotion();
				if (hasAmount)
				{
					promo.setPromoType(PromotionType.DISCOUNT_OFFER);
				}
				else
				{
					promo.setPromoType(PromotionType.VALUEADD);
				}
				promo.setShortMarketingText(description);
				room.getPromotions().add(promo);
				if (log.isDebugEnabled())
					log.debug("makeRoomOffers::added promotion " + promo.getShortMarketingText());
			}
		}
		if (rateHotelResponse.getBoardCode() != null)
		{
			if (rateHotelResponse.getBoardCode().equals("BB"))
			{
				RoomPromotion promo = new RoomPromotion();
				promo.setPromoType(PromotionType.MEAL);
				promo.setShortMarketingText(rateHotelResponse.getBoardName());
				room.getPromotions().add(promo);
				if (log.isDebugEnabled())
					log.debug("makeRoomOffers::added promotion " + promo.getShortMarketingText());
			}
			if (rateHotelResponse.getBoardCode().equals("HB"))
			{
				RoomPromotion promo = new RoomPromotion();
				promo.setPromoType(PromotionType.MEAL);
				promo.setShortMarketingText("Breakfast and Dinner");
				room.getPromotions().add(promo);
				if (log.isDebugEnabled())
					log.debug("makeRoomOffers::added promotion " + promo.getShortMarketingText());
			}
			if (rateHotelResponse.getBoardCode().equals("FB"))
			{
				RoomPromotion promo = new RoomPromotion();
				promo.setPromoType(PromotionType.MEAL);
				promo.setShortMarketingText("Breakfast, Lunch and Dinner");
				room.getPromotions().add(promo);
				if (log.isDebugEnabled())
					log.debug("makeRoomOffers::added promotion " + promo.getShortMarketingText());
			}
			if (rateHotelResponse.getBoardCode().equals("AB"))
			{
				RoomPromotion promo = new RoomPromotion();
				promo.setPromoType(PromotionType.MEAL);
				promo.setShortMarketingText("American Breakfast");
				room.getPromotions().add(promo);
				if (log.isDebugEnabled())
					log.debug("makeRoomOffers::added promotion " + promo.getShortMarketingText());
			}
		}
	}

	protected void makeRoomCNX(RoomResult room, RateHotelResponse rateHotelResponse, String currency, LocalDate checkin, LocalDate checkout) throws Exception
	{
		CNXDetails cnxDetails = makeCNXPolicy(rateHotelResponse, currency, checkin, checkout, room.getTotalRate().getAmount(), false);
		SortedSet<RoomCancellationPolicyLine> roomCancellationPolicy = cnxDetails.policy;
		room.setCancellationPolicy(roomCancellationPolicy);
		room.setCancellationPolicyText(cnxDetails.fullPolicyText);
		if (log.isDebugEnabled())
			log.debug("makeRoomCNX::CNX policy for " + room.getRoomCode() + " " + roomCancellationPolicy);
	}

	private void loadRateComments(YoutravelInterface youtravelInterface, String rateCommentsID, String hotelID, LocalDate checkin, RoomResult room)
	{
		if (log.isDebugEnabled())
			log.debug("loadRateComments::entering for " + rateCommentsID);

		String[] rateCommentsParts = rateCommentsID.split("\\|");
		if (rateCommentsParts.length != 3)
		{
			log.warn("loadRateComments::bypassing, as rateCommentsParts.length()=" + rateCommentsParts.length);
			return;
		}
		String fullCode = rateCommentsParts[0] + "|" + hotelID + "|" + rateCommentsParts[1] + "|" + rateCommentsParts[2];
		if (log.isDebugEnabled())
			log.debug("loadRateComments::checking code " + fullCode);

		try
		{
			Set<YoutravelCache.RateComment> comments = getHotelbedsCache().getRateComments(fullCode);

			for (YoutravelCache.RateComment comment : comments)
			{
				if (log.isDebugEnabled())
					log.debug("loadRateComments::for hotel " + hotelID + " roomCode " + room.getRoomCode() + " compare checkin " + checkin + " between " + comment.dateFrom + " and " + comment.dateTo);
				if (!checkin.isBefore(comment.dateFrom) && !checkin.isAfter(comment.dateTo))
				{
					RoomExtraFee extraFee = new RoomExtraFee();
					extraFee.setDescription(comment.comment);
					extraFee.setFeeType(FeeType.CheckinFees);
					room.getExtraFees().add(extraFee);
					if (log.isDebugEnabled())
						log.debug("loadRateComments::adding rateComment " + comment.comment);
				}
			}
		}
		catch (Exception e)
		{
			log.warn("loadRateComments::error " + e.toString(), e);
		}
	}

	private static class CNXDetails
	{
		public SortedSet<RoomCancellationPolicyLine> policy = new TreeSet<>();

		public String fullPolicyText;
	}

	private CNXDetails makeCNXPolicy(RateHotelResponse rateHotelResponse, String currency, LocalDate checkin, LocalDate checkout, BigDecimal totalPrice_bd, boolean markupCNXValues) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("makeCNXPolicy::entering");
		List<RoomCancellationPolicyLine> hotelRoomTypeCancellationPolicy = new ArrayList<RoomCancellationPolicyLine>();
		StringBuffer fullPolicyText = new StringBuffer();
		BigDecimal numberOfRooms = BigDecimal.ONE;
		LocalDate today = LocalDate.now();
		if (rateHotelResponse.getRooms() != null)
		{
			numberOfRooms = new BigDecimal(rateHotelResponse.getRooms());
		}

		CNXDetails cnxDetails = new CNXDetails();
		if (rateHotelResponse.getCancellationPolicies() != null && rateHotelResponse.getCancellationPolicies().getCancellationPolicy().size() > 0)
		{

			for (CancellationPolicy cancellationPolicy : rateHotelResponse.getCancellationPolicies().getCancellationPolicy())
			{
				RoomCancellationPolicyLine policyLine = new RoomCancellationPolicyLine();
				Calendar asAtDateCal = Calendar.getInstance();
				asAtDateCal.setTime(Functions.convertGeoToTimestamp(cancellationPolicy.getFrom()));
				asAtDateCal.add(Calendar.DATE, -2);
				LocalDate asAtDate = Functions.normaliseLocalDate(asAtDateCal.getTime());
				BigDecimal penaltyAmount = cancellationPolicy.getAmount();
				if (markupCNXValues)
					penaltyAmount = YoutravelInterface.applyInventoryMarkup(cancellationPolicy.getAmount(), null);
				CurrencyValue penalty = new CurrencyValue(currency, penaltyAmount);
				String cnxPolicyText = new String("If cancelled on or after " + df2ddmmmYY.format(asAtDate) + " - a charge of " + Functions.formatCurrencyDisplay(penalty) + " applies. ");

				Integer nightsFromPrice = Functions.getNightsFromPrice(cancellationPolicy.getAmount(), rateHotelResponse.getNet(), rateHotelResponse.getSellingRate(),
						(int) ChronoUnit.DAYS.between(checkin, checkout), numberOfRooms.intValue());
				if (nightsFromPrice != null)
				{
					cnxPolicyText = new String(
							"If cancelled on or after " + df2ddmmmYY.format(asAtDate) + " - a charge of " + nightsFromPrice + " night" + (nightsFromPrice > 1 ? "s" : "") + " applies. ");
				}
				fullPolicyText.append(cnxPolicyText);
				policyLine.setAsOf(asAtDate);
				policyLine.setPenaltyDescription(cnxPolicyText);
				policyLine.setPenalty(penalty);
				hotelRoomTypeCancellationPolicy.add(policyLine);
			}
		}
		else if (rateHotelResponse.getRateClass().equals("NRF"))
		{
			RoomCancellationPolicyLine policyLine = new RoomCancellationPolicyLine();
			String cnxPolicyText = "Non refundable";
			fullPolicyText.append(cnxPolicyText);
			policyLine.setAsOf(today);
			policyLine.setPenaltyDescription(cnxPolicyText);
			CurrencyValue penalty = new CurrencyValue(currency, totalPrice_bd);
			policyLine.setPenalty(penalty);
			hotelRoomTypeCancellationPolicy.add(policyLine);
		}
		else if (rateHotelResponse.getRateType().equals("RECHECK") && rateHotelResponse.getRateClass().equals("NOR"))
		{
			RoomCancellationPolicyLine policyLine = new RoomCancellationPolicyLine();
			String cnxPolicyText = "Non refundable";
			fullPolicyText.append(cnxPolicyText);
			policyLine.setAsOf(today);
			policyLine.setPenaltyDescription(cnxPolicyText);
			CurrencyValue penalty = new CurrencyValue(currency, totalPrice_bd);
			policyLine.setPenalty(penalty);
			hotelRoomTypeCancellationPolicy.add(policyLine);
		}
		else
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::no CNS policies, not NRF rate class");
			// so nonRefundable is FALSE, but we have no CNX info - just make up some up for now. We're probably in a query where
			// includeDetails has been set to false
			RoomCancellationPolicyLine policyLine = new RoomCancellationPolicyLine();
			String cnxPolicyText = "Fully Refundable";
			fullPolicyText.append(cnxPolicyText);
			policyLine.setAsOf(checkin);
			policyLine.setPenaltyDescription(cnxPolicyText);
			CurrencyValue penalty = new CurrencyValue(currency, BigDecimal.ZERO);
			policyLine.setPenalty(penalty);
			hotelRoomTypeCancellationPolicy.add(policyLine);
		}
		cnxDetails.fullPolicyText = Functions.cleanHTML(fullPolicyText.toString());
		cnxDetails.policy.addAll(hotelRoomTypeCancellationPolicy);
		return cnxDetails;
	}

	private String mapBoardCode(String hbBoardCode)
	{
		if (hbBoardCode == null || hbBoardCode.length() < 2)
		{
			return "OT";
		}
		String twoChars = hbBoardCode.substring(0, 2);
		switch (twoChars)
		{
			case "RO":
				return "RO";
			case "SC":
				return "SC";
			case "BB":
				return "BB";
			case "HB":
				return "HB";
			case "FB":
				return "FB";
			case "TL":
				return "AI";
			case "AS":
				return "AI";
			case "AI":
				return "AI";
			case "CB":
				return "CB";
			case "AB":
				return "AB";
			case "DB":
				return "DB";
			default:
				return "OT";
		}
	}

	private String makeERoamExtraRoomInformation(RoomResult room)
	{
		// eRoam does not chec extrafees etc! So put it into roomExtraInformation
		StringBuffer buf = new StringBuffer();

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

		for (RoomExtraFee fee : room.getExtraFees())
		{
			if (buf.length() > 0)
				buf.append(". ");
			buf.append(fee.getDescription());
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

	private static DateTimeFormatter df2ddmmmYY = DateTimeFormatter.ofPattern("dd MMM yy");

	public void clearCache()
	{
		getHotelbedsCache().clear();
	}

	private YoutravelCache getHotelbedsCache()
	{
		if (_youtravelCache == null)
			_youtravelCache = new YoutravelCache(youtravelStaticRepo);
		return _youtravelCache;
	}

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("URL endpoint of Hotelbeds API", "hotelbedsURL", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The Hotelbeds APIKey", "apikey", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The Hotelbeds secret value", "secret", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("If bookings should be 'faked' and NOT sent to the server, just return a dummy confirmation", "bypassBooking", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Booking percentage tolerance", "tolerance", FieldType.INTEGER, false, "0"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The default source market", "sourceMarket", FieldType.STRING, false, "GB"));
		return channelType;
	}
}
