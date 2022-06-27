package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.AccommodationServiceIF;
import com.torkirion.eroam.microservice.accommodation.endpoint.BoardCodes;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.*;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.torkirion.eroam.microservice.accommodation.apidomain.RoomExtraFee.FeeType.CheckinFees;

@AllArgsConstructor
@Slf4j
public class InnstantService implements AccommodationServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private AccommodationRCRepo accommodationRCRepo;

	private AccommodationRCService accommodationRCService;

	private DestinationSearchRQDataRepo destinationSearchRQDataRepo;

	private InnstantRCLoader innstantRCLoader;

	public static final String CHANNEL = "INNSTANT";

	public static final String CHANNEL_PREFIX = "IN";

	private static final String SITE_DEFAULT = "eroam";

	private static final Integer MAXIMUM_INNSTANT_SEARCH_SIZE = 250;

	public List<AccommodationResult> searchByHotelId(AvailSearchByHotelIdRQDTO availSearchRQ)
	{
		log.info("searchByHotelId::search(AvailSearchByHotelIdRQDTO)=" + availSearchRQ);
		long timer1 = System.currentTimeMillis();
		Map<Integer, List<ResultDTO>> availabilityRS = new HashMap<>();
		List<AccommodationResult> results = new ArrayList<>();
		try
		{
			InnstantInterface innstantInterface = new InnstantInterface(propertiesDAO, availSearchRQ.getClient(), CHANNEL, destinationSearchRQDataRepo);
			long timer2 = System.currentTimeMillis();
			availabilityRS = innstantInterface.startSearchHotelById(availSearchRQ);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("search::time in innstant search was " + totalTime2 + " millis");
			if (availabilityRS == null || availabilityRS.size() < 1)
			{
				if (log.isDebugEnabled())
					log.debug("searchByHotelId::availabilityRS returned no lists");
				return new ArrayList<>();
			}
			else
			{
				// map result
				InnstantRCAPIProperties innstantRCAPIProperties = new InnstantRCAPIProperties(propertiesDAO, SITE_DEFAULT);
				HttpService httpService = new InnstantRCHttpService(innstantRCAPIProperties);
				for (Map.Entry<Integer, List<ResultDTO>> entry : availabilityRS.entrySet())
				{
					AccommodationResult accommodationResult = new AccommodationResult();
					String hotelId = entry.getKey().toString();
					String hotelCode = CHANNEL_PREFIX + hotelId;

					Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(hotelCode);
					AccommodationRC accommodationRC;
					if (accommodationRCOpt.isPresent())
					{
						accommodationRC = accommodationRCOpt.get();
					}
					else
					{
						accommodationRC = innstantRCLoader.loadHotelDB(httpService, hotelId);
					}
					accommodationResult.setProperty(loadProperty(accommodationRC));
					accommodationResult.setRooms(loadRooms(entry.getValue(), hotelId));
					results.add(accommodationResult);
				}
			}
		}
		catch (Exception e)
		{
			log.error("searchByHotelId::threw exception " + e, e);
		}
		log.info("searchByHotelId::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	public List<AccommodationResult> searchByGeocordBox(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchByGeocordBoxRQDTO)=" + availSearchRQ);
		long timer1 = System.currentTimeMillis();
		Map<Integer, List<ResultDTO>> availabilityRS = new HashMap<>();
		List<AccommodationResult> results = new ArrayList<>();
		try
		{
			InnstantInterface innstantInterface = new InnstantInterface(propertiesDAO, availSearchRQ.getClient(), CHANNEL, destinationSearchRQDataRepo);
			long timer2 = System.currentTimeMillis();
			BigDecimal latNorthwest = availSearchRQ.getNorthwest().getLatitude();
			BigDecimal lonNorthwest = availSearchRQ.getNorthwest().getLongitude();
			BigDecimal latSoutheast = availSearchRQ.getSoutheast().getLatitude();
			BigDecimal lonSoutheast = availSearchRQ.getSoutheast().getLongitude();
			// get list IDs of suiable lat lon
			List<String> hotelIds = accommodationRCRepo.findHotelCodeByGeoboxAndChannel(latNorthwest, latSoutheast, lonNorthwest, lonSoutheast, InnstantService.CHANNEL);
			
			if ( hotelIds.size() > MAXIMUM_INNSTANT_SEARCH_SIZE)
			{
				log.debug("search::trimming hotelIds to " + MAXIMUM_INNSTANT_SEARCH_SIZE + " hotels");
				hotelIds = new ArrayList<String>(hotelIds.subList(0, MAXIMUM_INNSTANT_SEARCH_SIZE));
			}

			availabilityRS = innstantInterface.startSearchHotels(availSearchRQ, hotelIds);

			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("search::time in innstant search was " + totalTime2 + " millis");
			if (availabilityRS == null || availabilityRS.size() == 0)
			{
				if (log.isDebugEnabled())
					log.debug("search::availabilityRS returned no lists");
				return new ArrayList<>();
			}
			else
			{
				// map result
				InnstantRCAPIProperties innstantRCAPIProperties = new InnstantRCAPIProperties(propertiesDAO, SITE_DEFAULT);
				HttpService httpService = new InnstantRCHttpService(innstantRCAPIProperties);
				for (Map.Entry<Integer, List<ResultDTO>> entry : availabilityRS.entrySet())
				{
					AccommodationResult accommodationResult = new AccommodationResult();
					String hotelId = entry.getKey().toString();
					String hotelCode = CHANNEL_PREFIX + hotelId;
					Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(hotelCode);
					AccommodationRC accommodationRC;
					if (accommodationRCOpt.isPresent())
					{
						accommodationRC = accommodationRCOpt.get();
					}
					else
					{
						accommodationRC = innstantRCLoader.loadHotelDB(httpService, hotelId);
					}
					accommodationResult.setProperty(loadProperty(accommodationRC));
					accommodationResult.setRooms(loadRooms(entry.getValue(), hotelId));
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

	public AccommodationRateCheckRS rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		log.info("search::rateCheck with " + rateCheckRQDTO);
		Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(rateCheckRQDTO.getHotelId());
		if (accommodationRCOpt.isPresent())
		{
			InnstantInterface innstantInterface = new InnstantInterface(propertiesDAO, rateCheckRQDTO.getClient(), CHANNEL, destinationSearchRQDataRepo);
			long timer1 = System.currentTimeMillis();
			long timer2 = System.currentTimeMillis();
			// Result call api Innstant
			InnstantPreBookRS innstantPreBookRS = innstantInterface.preBook(rateCheckRQDTO);
			long totalTime2 = (System.currentTimeMillis() - timer2);
			log.info("rateCheck::time in rateCheck was " + totalTime2 + " millis");
			if (innstantPreBookRS.getErrorMessage() != null)
			{
				log.warn("book::failed:bookingRS.getErrorMessage()=" + innstantPreBookRS.getErrorMessage());
				throw new Exception("Error in INNSTANT Service:" + innstantPreBookRS.getErrorMessage());
			}
			if (innstantPreBookRS.getContent().getServices().getHotels().size() < 1)
			{
				log.warn("rateCheck::failed");
				throw new Exception("Error! Could not find search.");
			}
			// Result mapping
			AccommodationRateCheckRS rateCheckRS = mapRateCheck(innstantPreBookRS, rateCheckRQDTO);
			log.info("rateCheck::time taken = " + (System.currentTimeMillis() - timer1));
			return rateCheckRS;
		}
		else
		{
			log.error("search::accommdation " + rateCheckRQDTO.getHotelId() + " not found");
			throw new Exception("Invalid hotelId provided");
		}
	}

	public AccommodationBookRS book(String client, AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);

		long timer1 = System.currentTimeMillis();
		try
		{
			long totalTime2 = (System.currentTimeMillis() - timer1);
			log.info("book::time in bookings was " + totalTime2 + " millis");
			InnstantInterface innstantInterface = new InnstantInterface(propertiesDAO, client, CHANNEL, destinationSearchRQDataRepo);
			InnstantBookRS innstantBookRS = innstantInterface.book(bookRQ);
			if (innstantBookRS.getErrorMessage() != null)
			{
				log.warn("book::failed:bookingRS.getErrorMessage()=" + innstantBookRS.getErrorMessage());
				throw new Exception("Error in INNSTANT Service:" + innstantBookRS.getErrorMessage());
			}
			log.info("book::time taken = " + (System.currentTimeMillis() - timer1));

			return mapBooking(innstantBookRS, bookRQ);
		}
		catch (Exception e)
		{
			log.error("book::threw exception " + e.toString(), e);
			if (e.getMessage().contains("Error in Innstant Service"))
				throw new Exception(e.getMessage());
			else
				throw new Exception("Error in Innstant Service:" + e.getMessage());
		}
	}

	public AccommodationCancelRS cancel(String site, AccommodationCancelRQ cancelRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::received " + cancelRQ);
		try
		{
			InnstantInterface innstantInterface = new InnstantInterface(propertiesDAO, site, CHANNEL, destinationSearchRQDataRepo);

			long timer1 = System.currentTimeMillis();
			InnstantCancelBookRS innstantCancelBookRS = innstantInterface.cancel(cancelRQ);

			if (innstantCancelBookRS.getErrorMessage() != null)
				throw new Exception("Error in Innstant Service: " + innstantCancelBookRS.getErrorMessage());

			log.info("cancel::time taken = " + (System.currentTimeMillis() - timer1));

			AccommodationCancelRS cancelRS = new AccommodationCancelRS(innstantCancelBookRS.getContent().getStatus(), null);

			return cancelRS;
		}
		catch (Exception e)
		{
			log.error("cancel::threw exception " + e, e);
			throw new Exception("Error in Innstant Service:" + e.getMessage());
		}
	}

	public AccommodationRetrieveRS retrieve(String site, AccommodationRetrieveRQ retrieveRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("retrieve::received " + retrieveRQ);

		// TODO
		return null;
	}

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("mishor data url", "mishor_data_url", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Static Data url", "innstantRCStatic", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Search URL", "innstantRCSearch", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("Book url", "innstantRCBook", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("aether access token", "aether_access_token", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("aether application key", "aether_application_key", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("mishor application key", "mishor_application_key", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The default address for the person making the booking", "address", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The default city for the person making the booking", "city", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The default email for the person making the booking", "email", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The default zip for the person making the booking", "zip", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The default country for the person making the booking", "country", FieldType.STRING, true, ""));
		return channelType;
	}

	public AccommodationRateCheckRS mapRateCheck(InnstantPreBookRS innstantPreBookRS, RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		AccommodationRateCheckRS rateCheckRS = new AccommodationRateCheckRS();
		String property_id = rateCheckRQDTO.getHotelId();
		Optional<AccommodationRC> accommodationRCOpt = accommodationRCService.getAccommodationRC(property_id);
		if (!accommodationRCOpt.isPresent())
		{
			if (log.isDebugEnabled())
				log.debug("mapRateCheck::rc not found, bypassing");
			return null;
		}
		AccommodationRC accommodationRC = accommodationRCOpt.get();
		rateCheckRS.setProperty(loadProperty(accommodationRC));

		List<RoomResult> roomResultsSS = new ArrayList<>();
		Integer countRoom = 1;
		for (HotelServicesDTO hotelServicesDTO : innstantPreBookRS.getContent().getServices().getHotels())
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::processing room " + hotelServicesDTO.getCode());

			HotelServicesDTO.Item item = hotelServicesDTO.getItems().get(0);
			RoomResult roomResult = makeRoomResult(hotelServicesDTO.getCode());

			roomResult.setChannelPropertyCode(InnstantService.CHANNEL_PREFIX + innstantPreBookRS.getContent().getServices().getHotels().get(0).getItems().get(0).getHotel().getId());
			roomResult.setRoomName(item.getName());
			roomResult.setRoomNumber(countRoom++);
			roomResult.setBookingCode(innstantPreBookRS.getContent().getBookingCode());
			roomResult.setBoardCode(item.getBoard());
			roomResult.setBoardDescription(BoardCodes.mapBoardDescription(item.getBoard()));
			roomResult.setBedding(StringUtils.capitalize(item.getBedding()));
			roomResult.setRoomStandard(item.getCategory());
			roomResult.setRoomExtraInformation(item.getRemark() != null ? item.getRemark().getGeneral() : null);
			roomResult.setSupplyRate(new CurrencyValue(hotelServicesDTO.getNetPrice().getCurrency(), BigDecimal.valueOf(hotelServicesDTO.getNetPrice().getAmount())));
			roomResult.setTotalRate(new CurrencyValue(hotelServicesDTO.getPrice().getCurrency(), BigDecimal.valueOf(hotelServicesDTO.getPrice().getAmount())));

			Set<RoomExtraFee> roomExtraFeeHS = new HashSet<>();
			for (HotelServicesDTO.Surcharge surcharge : hotelServicesDTO.getSurcharges())
			{
				RoomExtraFee roomExtraFee = new RoomExtraFee();
				roomExtraFee.setFee(new CurrencyValue(surcharge.getPrice().getCurrency(), BigDecimal.valueOf(surcharge.getPrice().getAmount())));
				roomExtraFee.setFeeType(surcharge.getPayment().equals("direct") ? CheckinFees : null);
				roomExtraFee.setDescription(surcharge.getTitle() + " " + surcharge.getDescription());
				roomExtraFeeHS.add(roomExtraFee);
			}
			roomResult.setExtraFees(roomExtraFeeHS);
			roomResult.setCancellationPolicy(mapCancellationPolicy(hotelServicesDTO.getCancellation()));
			roomResult.setCancellationPolicyText(makeRoomCNXPolicyText(roomResult.getCancellationPolicy(), roomResult.getTotalRate()));

			roomResultsSS.add(roomResult);
		}
		rateCheckRS.setRooms(roomResultsSS);

		return rateCheckRS;
	}

	protected AccommodationProperty loadProperty(AccommodationRC accommodationRC) throws Exception
	{
		AccommodationProperty property = new AccommodationProperty();
		property.setCode(accommodationRC.getCode());
		property.setChannel(InnstantService.CHANNEL);
		property.setChannelCode(accommodationRC.getChannelCode());
		property.setAccommodationName(accommodationRC.getAccommodationName());
		property.setAddress(accommodationRC.getAddress());
		property.setErrata(accommodationRC.getErrata());
		property.setIntroduction(accommodationRC.getIntroduction());
		property.setOleryCompanyCode(accommodationRC.getOleryCompanyCode());
		property.setRatingText(accommodationRC.getRatingText());
		property.setRating(accommodationRC.getRating());
		if ( accommodationRC.getImageThumbnail() != null )
			property.setImageThumbnailUrl(accommodationRC.getImageThumbnail().getImageURL());
		else
			if ( accommodationRC.getImages() != null && accommodationRC.getImages().size() > 0 )
				property.setImageThumbnailUrl(accommodationRC.getImages().first().getImageURL());

		return property;
	}

	protected SortedSet<RoomResult> loadRooms(List<ResultDTO> resultDTOList, String hotelId) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("loadRooms::loading rooms, availabilityRSRooms.size=" + (resultDTOList == null ? 0 : resultDTOList.size()));

		SortedSet<RoomResult> rooms = new TreeSet<>();
		if (resultDTOList == null || resultDTOList.size() == 0)
		{
			return rooms;
		}
		long timer1 = System.currentTimeMillis();
		Integer roomMatchCode = 1;
		for (ResultDTO resultDTO : resultDTOList)
		{
			if (log.isDebugEnabled())
				log.debug("loadRooms::processing room " + resultDTO.getCode());
			if (!resultDTO.getConfirmation().equals("immediate"))
			{
				if (log.isDebugEnabled())
					log.debug("loadRooms::skipping room " + resultDTO.getCode() + ", confirmation is " + resultDTO.getConfirmation());
				continue;
			}
			int roomCount = 1;
			for ( RoomDTO roomDTO : resultDTO.getItems())
			{
				RoomResult roomResult = makeRoomResult(resultDTO.getCode());
				roomResult.setRoomName(roomDTO.getName());
				roomResult.setBedding(StringUtils.capitalize(roomDTO.getBedding()));
				roomResult.setBoardCode(roomDTO.getBoard());
				roomResult.setBoardDescription(BoardCodes.mapBoardDescription(roomDTO.getBoard()));
				roomResult.setBookingCode(resultDTO.getDestinationSearchRQId().toString() + '_' + resultDTO.getCode());
				roomResult.setBundlesOnly(resultDTO.getPackageRate());
				roomResult.setChannelPropertyCode(InnstantService.CHANNEL_PREFIX + hotelId);
				roomResult.setRoomExtraInformation(null);
				roomResult.setRoomStandard(null);
				roomResult.setRoomNumber(roomCount++);
				roomResult.setMultiRoomMatchCode(roomMatchCode.toString());
				roomResult.setSupplyRate(new CurrencyValue(resultDTO.getPrice().getCurrency(), BigDecimal.valueOf(resultDTO.getPrice().getAmount())));
				roomResult.setTotalRate(new CurrencyValue(resultDTO.getPrice().getCurrency(), applyInventoryMarkup(BigDecimal.valueOf(resultDTO.getPrice().getAmount()))));
				roomResult.setRoomStandard(null);
				roomResult.setExtraFees(null);
				roomResult.setCancellationPolicy(mapCancellationPolicy(resultDTO.getCancellation()));
				roomResult.setCancellationPolicyText(makeRoomCNXPolicyText(roomResult.getCancellationPolicy(), roomResult.getTotalRate()));
				if (resultDTO.getSpecialOffers() != null && resultDTO.getSpecialOffers().size() > 0)
				{
					if (log.isDebugEnabled())
						log.warn("loadRooms::found special offers for room " + resultDTO.getCode() + " which are unmapped");
				}
				rooms.add(roomResult);
			}
			roomMatchCode++;
		}
		if (log.isDebugEnabled())
			log.debug("loadRooms::roomcount=" + rooms.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return rooms;
	}

	protected RoomResult makeRoomResult(String code) throws Exception
	{
		RoomResult roomResult = new RoomResult();
		roomResult.setChannel(InnstantService.CHANNEL);
		roomResult.setBookingConditions(null);
		roomResult.setInventory(BigInteger.valueOf(0));
		roomResult.setRequiresRecheck(false);
		roomResult.setRoomCode(code);
		roomResult.setRateCode(code);
		roomResult.setMatchCode(code);
		roomResult.setPromotions(new HashSet<>());
		roomResult.setExtraFees(new HashSet<>());

		return roomResult;
	}

	protected SortedSet<RoomCancellationPolicyLine> mapCancellationPolicy(CancellationDTO cancellationDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("mapCancellationPolicy::enter");
		SortedSet<RoomCancellationPolicyLine> policyLines = new TreeSet<>();

		RoomCancellationPolicyLine cancellationPolicyLine = new RoomCancellationPolicyLine();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		for (CancellationDTO.Frame frame : cancellationDTO.getFrames())
		{
			LocalDateTime from = LocalDateTime.parse(frame.getFrom(), formatter);
			LocalDateTime to = LocalDateTime.parse(frame.getTo(), formatter);

			cancellationPolicyLine.setAsOf(from.toLocalDate());
			cancellationPolicyLine.setBefore(from.isBefore(to));
			cancellationPolicyLine.setPenaltyDescription(cancellationDTO.getType());
			cancellationPolicyLine.setPenaltyPercent(null);
			cancellationPolicyLine.setPenalty(new CurrencyValue(frame.getPenalty().getCurrency(), BigDecimal.valueOf(frame.getPenalty().getAmount())));
			policyLines.add(cancellationPolicyLine);
		}

		return policyLines;
	}

	protected String makeRoomCNXPolicyText(SortedSet<RoomCancellationPolicyLine> roomCancellationPolicyLines, CurrencyValue totalValue)
	{
		if (log.isDebugEnabled())
			log.debug("makeRoomCNXPolicyText::enter");
		StringBuffer fullPolicyText = new StringBuffer();
		if ( roomCancellationPolicyLines.size() > 1)
			log.warn("mapBooking::mutiple cancellation policy lines:" + roomCancellationPolicyLines);
		for (RoomCancellationPolicyLine roomCancellationPolicyLine : roomCancellationPolicyLines)
		{
			if (fullPolicyText.length() > 0)
				fullPolicyText.append(" ");
			if (roomCancellationPolicyLine.getPenaltyDescription().equals("non-refundable"))
				fullPolicyText.append("Non Refundable");
			else if ( roomCancellationPolicyLine.getPenalty().equals(totalValue))
			{
				String cnxPolicyText = new String("If cancelled on or after " + df2ddmmmYY.format(roomCancellationPolicyLine.getAsOf()) + " - no refund applies.");
				fullPolicyText.append(cnxPolicyText);
			}
			else
			{
				String cnxPolicyText = new String("If cancelled on or after " + df2ddmmmYY.format(roomCancellationPolicyLine.getAsOf()) + " - a charge of " + Functions.formatCurrencyDisplay(roomCancellationPolicyLine.getPenalty()) + " applies.");
				fullPolicyText.append(cnxPolicyText);
			}
		}
		return fullPolicyText.toString();
	}

	protected AccommodationBookRS mapBooking(InnstantBookRS bookingRS, AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("mapBooking::enter");
		Map<String, String> mapOfRoomCodeAndInternal = makeMapRoomCodeAndInternalId(bookRQ);
		AccommodationBookRS bookRS = new AccommodationBookRS();
		bookRS.setBookingReference(bookingRS.getContent().getBookingID());
		if (log.isDebugEnabled())
			log.debug("mapBooking::bookingRS has " + bookingRS.getContent().getServices().size() + " rooms");
		List<AccommodationBookRS.ResponseItem> listResponseItem = new ArrayList<>();
		for (BookContentDTO.Service service : bookingRS.getContent().getServices())
		{
			AccommodationBookRS.ResponseItem responseItem = new AccommodationBookRS.ResponseItem();
			responseItem.setChannel(CHANNEL);
			responseItem.setItemRemark(service.getRemarks().getGeneral());
			responseItem.setBookingItemReference(service.getCode());
			responseItem.setItemStatus(Booking.ItemStatus.BOOKED);
			responseItem.setInternalItemReference(mapOfRoomCodeAndInternal.get(service.getCode()));
			listResponseItem.add(responseItem);
		}
		bookRS.setItems(listResponseItem);
		bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
		return bookRS;
	}

	private static final BigDecimal INNSTANT_MARKUP = new BigDecimal("1.1363636");

	public static BigDecimal applyInventoryMarkup(BigDecimal nett) throws Exception
	{
		return nett.multiply(INNSTANT_MARKUP).setScale(0, RoundingMode.UP);
	}

	private Map<String, String> makeMapRoomCodeAndInternalId(AccommodationBookRQ bookRQ)
	{
		Map<String, String> map = new HashMap<>();
		for (AccommodationBookRQ.AccommodationRequestItem item : bookRQ.getItems())
		{
			map.put(item.getBookingCode().substring(item.getBookingCode().indexOf("_") + 1), item.getInternalItemReference());
		}
		return map;
	}
	private static DateTimeFormatter df2ddmmmYY = DateTimeFormatter.ofPattern("dd MMM yy");
}
