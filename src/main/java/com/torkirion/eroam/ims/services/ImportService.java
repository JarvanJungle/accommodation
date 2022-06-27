package com.torkirion.eroam.ims.services;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.transaction.Transactional;

import org.apache.commons.text.WordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.torkirion.eroam.ims.apidomain.AccommodationSummary;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.BeforeCheckinAfterBooking;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.PenaltyType;
import com.torkirion.eroam.ims.datadomain.*;
import com.torkirion.eroam.ims.repository.*;
import com.torkirion.eroam.ims.services.ImportService.AccommodationImportBean;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.controllers.AccommodationController;
import com.torkirion.eroam.microservice.apidomain.RequestData;
import com.torkirion.eroam.microservice.datadomain.AirlineRepo;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class ImportService
{
	@Autowired
	private AccommodationController accommodationController;

	@Autowired
	private MapperService mapperService;

	@Autowired
	private IMSAccommodationRCDataRepo accommodationRCDataRepo;

	@Autowired
	private IMSAccommodationCancellationPolicyRepo accommodationCancellationPolicyRepo;

	@Autowired
	private IMSAccommodationSeasonRepo accommodationSeasonRepo;

	@Autowired
	private IMSAccommodationBoardRepo accommodationBoardRepo;

	@Autowired
	private IMSAccommodationRoomtypeRepo accommodationRoomtypeRepo;

	@Autowired
	private IMSAccommodationRateRepo accommodationRateRepo;

	@Autowired
	private IMSAccommodationAllocationRepo accommodationAllocationRepo;

	@Autowired
	private IMSAccommodationSpecialRepo accommodationSpecialRepo;

	@Autowired
	private IMSAccommodationAllocationSummaryRepo accommodationAllocationSummaryRepo;

	@Autowired
	private IMSAccommodationSaleRepo accommodationSaleRepo;

	@Autowired
	private IMSEventRepo eventRepo;

	@Autowired
	private IMSEventAllotmentRepo eventAllotmentRepo;

	@Autowired
	private IMSEventClassificationRepo eventClassificationRepo;

	@Autowired
	private IMSEventSeriesRepo eventSeriesRepo;

	@Autowired
	private IMSEventSupplierRepo eventSupplierRepo;

	@Autowired
	private IMSEventTypeRepo eventTypeRepo;

	@Autowired
	private IMSEventVenueRepo eventVenueRepo;

	@Autowired
	private IMSEventSaleRepo eventSaleRepo;

	@Autowired
	private IMSEventMerchandiseLinkRepo eventMerchandiseLinkRepo;

	@Autowired
	private IMSMerchandiseCategoryRepo merchandiseCategoryRepo;

	@Autowired
	private IMSMerchandiseSupplierRepo merchandiseSupplierRepo;

	@Autowired
	private IMSMerchandiseRepo merchandiseRepo;

	@Autowired
	private IMSMerchandiseOptionRepo merchandiseOptionRepo;

	@Autowired
	private IMSMerchandiseSaleRepo merchandiseSaleRepo;

	@Autowired
	private IMSActivityRepo activityRepo;

	@Autowired
	private IMSActivitySupplierRepo activitySupplierRepo;

	@Autowired
	private IMSActivitySupplierAgeBandRepo activitySupplierAgeBandRepo;

	@Autowired
	private IMSActivityOptionRepo activityOptionRepo;

	@Autowired
	private IMSActivityDepartureTimeRepo activityDepartureTimeRepo;

	@Autowired
	private IMSActivityAllotmentRepo activityAllotmentRepo;

	@Autowired
	private IMSActivitySaleRepo activitySaleRepo;

	@Autowired
	private IMSTransportationBasicRepo transportationBasicRepo;

	@Autowired
	private IMSTransportationBasicClassRepo transportationBasicClassRepo;

	@Autowired
	private IMSTransportationBasicSegmentRepo transportationBasicSegmentRepo;

	@Autowired
	private IMSTransportationSaleRepo transportationSaleRepo;

	@Autowired
	private IataAirportRepo iataAirportRepo;

	@Autowired
	private AirlineRepo airlineRepo;

	@Data
	public static class AccommodationImportBean
	{
		@CsvBindByPosition(position = 0)
		private String hotelName;

		@CsvBindByPosition(position = 1)
		private String address;

		@CsvBindByPosition(position = 2)
		private String displayName;

		@CsvBindByPosition(position = 3)
		private String roomType;

		@CsvBindByPosition(position = 4)
		private String bedding;

		@CsvBindByPosition(position = 5)
		private String checkinTime;

		@CsvBindByPosition(position = 6)
		private String checkoutTime;

		@CsvBindByPosition(position = 7)
		private String inclusions;

		@CsvBindByPosition(position = 8)
		private String productCapacity;

		@CsvBindByPosition(position = 9)
		private String costCurrency;

		@CsvBindByPosition(position = 10)
		private String fxRate;

		@CsvBindByPosition(position = 11)
		private String sellCurrency;

		@CsvBindByPosition(position = 12)
		private String fromDate;

		@CsvBindByPosition(position = 13)
		private String toDate;

		@CsvBindByPosition(position = 14)
		private String nettPerNightPrice;

		@CsvBindByPosition(position = 15)
		private String markup;

		@CsvBindByPosition(position = 16)
		private String sellPrice;

		@CsvBindByPosition(position = 17)
		private String allocation;

		@CsvBindByPosition(position = 18)
		private String informationAlerts;

		@CsvBindByPosition(position = 19)
		private String fixedDuration;

		@CsvBindByPosition(position = 20)
		private String restrictToPackage;

		@CsvBindByPosition(position = 21)
		private String area;

		@CsvBindByPosition(position = 22)
		private String agentCommission;

		@CsvBindByPosition(position = 23)
		private String notes;
	}

	protected class ImportRunData
	{
		IMSAccommodationRCData hotelRc;

		IMSAccommodationCancellationPolicy policy;

		IMSAccommodationBoard board;

		Map<String, IMSAccommodationRoomtype> roomTypes;

		Map<Integer, IMSAccommodationRate> roomRates;

		Map<String, IMSAccommodationSeason> seasons;

		Map<String, IMSAccommodationAllocationSummary> roomAllocationsSummaries;
	}

	@Data
	public static class EventImportBean
	{
		@CsvBindByPosition(position = 0)
		private String ignore1;

		@CsvBindByPosition(position = 1)
		private String ignore2;

		@CsvBindByPosition(position = 2)
		private String ignore3;

		@CsvBindByPosition(position = 3)
		private String venue;

		@CsvBindByPosition(position = 4)
		private String venueAddress;

		@CsvBindByPosition(position = 5)
		private String ticketType;

		@CsvBindByPosition(position = 6)
		private String ticketDescription;

		@CsvBindByPosition(position = 7)
		private String eventDate;

		@CsvBindByPosition(position = 8)
		private String ignore9;

		@CsvBindByPosition(position = 9)
		private String ignore10;

		@CsvBindByPosition(position = 10)
		private String costCurrency;

		@CsvBindByPosition(position = 11)
		private String ignore12;

		@CsvBindByPosition(position = 12)
		private String sellCurrency;

		@CsvBindByPosition(position = 13)
		private String nett;

		@CsvBindByPosition(position = 14)
		private String markup;

		@CsvBindByPosition(position = 15)
		private String sellPrice;

		@CsvBindByPosition(position = 16)
		private String allocation;

		@CsvBindByPosition(position = 17)
		private String ignore18;

		@CsvBindByPosition(position = 18)
		private String ignore19;

		@CsvBindByPosition(position = 19)
		private String ignore20;

		@CsvBindByPosition(position = 20)
		private String ignore21;

		@CsvBindByPosition(position = 21)
		private String ignore22;

		@CsvBindByPosition(position = 22)
		private String ignore23;

		@CsvBindByPosition(position = 23)
		private String ignore24;
	}

	@Data
	public static class TransportImportBean
	{
		@CsvBindByPosition(position = 0)
		private String fromCityName;

		@CsvBindByPosition(position = 1)
		private String toCityName;

		@CsvBindByPosition(position = 2)
		private String fromCityIATA;

		@CsvBindByPosition(position = 3)
		private String toCityIATA;

		@CsvBindByPosition(position = 4)
		private String flightNumber;

		@CsvBindByPosition(position = 5)
		private String currency;

		@CsvBindByPosition(position = 6)
		private String baseNett;

		@CsvBindByPosition(position = 7)
		private String baseRrp;

		@CsvBindByPosition(position = 8)
		private String taxNett;

		@CsvBindByPosition(position = 9)
		private String taxRrp;

		@CsvBindByPosition(position = 10)
		private String fromDate;

		@CsvBindByPosition(position = 11)
		private String toDate;

		@CsvBindByPosition(position = 12)
		private String frequency;

		@CsvBindByPosition(position = 13)
		private String quantity;

		@CsvBindByPosition(position = 14)
		private String operator;

		@CsvBindByPosition(position = 15)
		private String supplier;

		@CsvBindByPosition(position = 16)
		private String notes;

		@CsvBindByPosition(position = 17)
		private String leg1Flight;

		@CsvBindByPosition(position = 18)
		private String leg1FromIATA;

		@CsvBindByPosition(position = 19)
		private String leg1ToIATA;

		@CsvBindByPosition(position = 20)
		private String leg1FlightClass;

		@CsvBindByPosition(position = 21)
		private String leg1Etd;

		@CsvBindByPosition(position = 22)
		private String leg1Eta;

		@CsvBindByPosition(position = 23)
		private String leg2Flight;

		@CsvBindByPosition(position = 24)
		private String leg2FromIATA;

		@CsvBindByPosition(position = 25)
		private String leg2ToIATA;

		@CsvBindByPosition(position = 26)
		private String leg2FlightClass;

		@CsvBindByPosition(position = 27)
		private String leg2Etd;

		@CsvBindByPosition(position = 28)
		private String leg2Eta;
	}

	public int importAccommodation(String filename, String site, String prefix) throws Exception
	{
		log.debug("importAccommodation::enter for '" + filename + "'");

		Reader fileReader = new FileReader(filename);

		ImportRunData importRunData = new ImportRunData();

		List<AccommodationImportBean> beans = new CsvToBeanBuilder<AccommodationImportBean>(fileReader).withType(AccommodationImportBean.class).build().parse();

		int propertiesImported = 0;
		int recCount = 0;
		for (AccommodationImportBean accommodationLine : beans)
		{
			log.debug("importAccommodation::read line hoteName '" + accommodationLine.getHotelName() + "' roomName '" + accommodationLine.getRoomType() + "'");
			if (recCount == 0)
			{
				log.debug("importAccommodation::bypass header");
				recCount++;
				continue;
			}
			if (accommodationLine.getHotelName() != null && accommodationLine.getHotelName().length() > 0)
			{
				log.debug("importAccommodation::create accommodation with " + accommodationLine);
				createAccommodation(importRunData, accommodationLine, prefix);
				propertiesImported++;
			}
			if (accommodationLine.getRoomType() != null && accommodationLine.getRoomType().length() > 0)
			{
				if (accommodationLine.getFromDate() != null && accommodationLine.getFromDate().length() > 0)
				{
					log.debug("importAccommodation::create room with " + accommodationLine);
					createRoom(importRunData, accommodationLine);
				}
			}
			recCount++;
		}
		log.debug("importAccommodation::Imported " + propertiesImported + " properties");
		return propertiesImported;
	}

	protected void createAccommodation(ImportRunData importRunData, AccommodationImportBean accommodationLine, String prefix)
	{
		log.debug("createAccommodation::enter for " + accommodationLine.getHotelName());

		// check if hotel already exists... only by using name ...
		List<IMSAccommodationRCData> all = accommodationRCDataRepo.findAll();
		importRunData.hotelRc = new IMSAccommodationRCData();
		importRunData.roomTypes = new HashMap<>();
		importRunData.roomRates = new HashMap<>();
		importRunData.roomAllocationsSummaries = new HashMap<>();
		importRunData.seasons = new HashMap<>();
		String hotelName = accommodationLine.getHotelName().replace("\uFEFF", "");
		importRunData.hotelRc.setHotelId(prefix + WordUtils.capitalize(hotelName.replaceAll("\\s", "")));
		if (importRunData.hotelRc.getHotelId().length() > 20)
			importRunData.hotelRc.setHotelId(importRunData.hotelRc.getHotelId().substring(0, 20));
		boolean newhotel = true;
		for (IMSAccommodationRCData rc : all)
		{
			if (rc.getAccommodationName().equals(hotelName))
			{
				importRunData.hotelRc = rc;
				newhotel = false;

			}
		}
		log.debug("createAccommodation::for " + importRunData.hotelRc.getHotelId() + ", newHotel=" + newhotel);
		importRunData.hotelRc.setAccommodationName(hotelName);
		importRunData.hotelRc.setProductType(AccommodationTypeTag.HOTEL);
		importRunData.hotelRc.setCurrency(accommodationLine.getCostCurrency());
		importRunData.hotelRc.setLastUpdate(LocalDate.now());
		importRunData.hotelRc.setLastUpdated(LocalDateTime.now());
		importRunData.hotelRc.setAddress(new Address());
		importRunData.hotelRc.getAddress().setCountryCode("AU");
		;
		if (accommodationLine.getAddress().contains("P:"))
		{
			importRunData.hotelRc.getAddress().setFullFormAddress(accommodationLine.getAddress().substring(0, accommodationLine.getAddress().indexOf("P:")));
			importRunData.hotelRc.setPhone(accommodationLine.getAddress().substring(accommodationLine.getAddress().indexOf("P:") + 2).trim());
		}
		else
		{
			importRunData.hotelRc.getAddress().setFullFormAddress(accommodationLine.getAddress());
		}
		if (accommodationLine.getCheckinTime() != null && accommodationLine.getCheckinTime().length() <= 20)
			importRunData.hotelRc.setCheckinTime(accommodationLine.getCheckinTime());
		else
			log.debug("createAccommodation::not saving checkin time : '" + accommodationLine.getCheckinTime() + "'");
		if (accommodationLine.getCheckoutTime() != null && accommodationLine.getCheckoutTime().length() <= 20)
			importRunData.hotelRc.setCheckoutTime(accommodationLine.getCheckoutTime());
		else
			log.debug("createAccommodation::not saving checkout time : '" + accommodationLine.getCheckoutTime() + "'");
		log.debug("createAccommodation::saving " + importRunData.hotelRc);

		importRunData.hotelRc = accommodationRCDataRepo.save(importRunData.hotelRc);
		if (newhotel)
		{
			log.debug("createAccommodation::creating CNX for newhotel " + importRunData.hotelRc.getHotelId());
			IMSAccommodationCancellationPolicy accommodationCancellationPolicy = new IMSAccommodationCancellationPolicy();
			accommodationCancellationPolicy.setHotelId(importRunData.hotelRc.getHotelId());
			accommodationCancellationPolicy.setPolicyId(1);
			accommodationCancellationPolicy.setPolicyName("Default");
			accommodationCancellationPolicy.setLineId(0);
			accommodationCancellationPolicy.setNumberOfDays(0);
			accommodationCancellationPolicy.setBeforeCheckinAfterBooking(BeforeCheckinAfterBooking.BEFORE_CHECKIN);
			accommodationCancellationPolicy.setPenaltyType(PenaltyType.PERCENTAGE);
			accommodationCancellationPolicy.setPenalty(BigDecimal.ZERO);
			importRunData.policy = accommodationCancellationPolicyRepo.save(accommodationCancellationPolicy);

			log.debug("createAccommodation::creating board for newhotel " + importRunData.hotelRc.getHotelId());
			IMSAccommodationBoard board = new IMSAccommodationBoard();
			board.setHotelId(importRunData.hotelRc.getHotelId());
			board.setBoardCode("NA");
			board.setBoardDescription("N/A");
			importRunData.board = accommodationBoardRepo.save(board);
		}
		else
		{
			log.debug("createAccommodation::loading existing CNX for " + importRunData.hotelRc.getHotelId());
			List<IMSAccommodationCancellationPolicy> policies = accommodationCancellationPolicyRepo.findByHotelIdOrderByPolicyIdAscLineIdAsc(importRunData.hotelRc.getHotelId());
			importRunData.policy = policies.get(0);
			log.debug("createAccommodation::loading existing board for " + importRunData.hotelRc.getHotelId());
			List<IMSAccommodationBoard> boards = accommodationBoardRepo.findByHotelIdOrderByBoardCodeAsc(importRunData.hotelRc.getHotelId());
			importRunData.board = boards.get(0);
		}

		try
		{
			AccommodationRC accommodationRC = mapperService.mapToRC(importRunData.hotelRc);
			accommodationController.saveRichContentForProperty(accommodationRC.getCode(), new RequestData<>(accommodationRC));
		}
		catch (Exception e)
		{
			log.debug("createAccommodation::caught " + e.toString(), e);
		}
	}

	protected void createRoom(ImportRunData importRunData, AccommodationImportBean accommodationLine)
	{
		// WE'RE GOING TO ASSUME FOR NOW THIS IS ONLY RUN ONCE!!
		log.debug("createRoom::enter for " + accommodationLine.getRoomType());
		String roomName = accommodationLine.getRoomType();
		boolean newRoom = true;
		IMSAccommodationRoomtype roomtype = importRunData.roomTypes.get(roomName);
		IMSAccommodationAllocationSummary allocationSummary = importRunData.roomAllocationsSummaries.get(roomName);
		if (roomtype == null)
		{
			newRoom = true;
			IMSAccommodationRoomtype accommodationRoomtype = new IMSAccommodationRoomtype();
			accommodationRoomtype.setHotelId(importRunData.hotelRc.getHotelId());
			accommodationRoomtype.setRoomtypeId(importRunData.roomTypes.size() + 1);
			accommodationRoomtype.setDescription(roomName);
			accommodationRoomtype.setBeddingDescription(accommodationLine.getBedding());
			accommodationRoomtype.setMaximumAdults(Integer.parseInt(accommodationLine.getProductCapacity()));
			accommodationRoomtype.setMaximumPeople(Integer.parseInt(accommodationLine.getProductCapacity()));
			accommodationRoomtype.setSimpleAllocation(true);
			accommodationRoomtype = accommodationRoomtypeRepo.save(accommodationRoomtype);
			importRunData.roomTypes.put(roomName, accommodationRoomtype);
			roomtype = accommodationRoomtype;

			allocationSummary = new IMSAccommodationAllocationSummary();
			allocationSummary.setHotelId(importRunData.hotelRc.getHotelId());
			allocationSummary.setAllocationId(importRunData.roomAllocationsSummaries.size() + 1);
			allocationSummary.setHandbackDays(0);
			allocationSummary.setAllocationDescription(roomName);
			allocationSummary = accommodationAllocationSummaryRepo.save(allocationSummary);
			importRunData.roomAllocationsSummaries.put(roomName, allocationSummary);
		}

		LocalDate fromDate = null;
		LocalDate toDate = null;
		try
		{
			fromDate = LocalDate.parse(accommodationLine.getFromDate(), df1);
		}
		catch (DateTimeParseException e)
		{
			try
			{
				fromDate = LocalDate.parse(accommodationLine.getFromDate(), df2);
			}
			catch (DateTimeParseException e2)
			{
				fromDate = LocalDate.parse(accommodationLine.getFromDate(), df3);
			}
		}
		try
		{
			toDate = LocalDate.parse(accommodationLine.getToDate(), df1);
		}
		catch (DateTimeParseException e)
		{
			try
			{
				toDate = LocalDate.parse(accommodationLine.getToDate(), df2);
			}
			catch (DateTimeParseException e2)
			{
				toDate = LocalDate.parse(accommodationLine.getToDate(), df3);
			}
		}
		String fromtoString = fromDate.format(df1) + toDate.format(df1);
		IMSAccommodationSeason season = importRunData.seasons.get(fromtoString);
		if (season == null)
		{
			season = new IMSAccommodationSeason();
			season.setSeasonId(importRunData.seasons.size() + 1);
			season.setHotelId(importRunData.hotelRc.getHotelId());
			season.setSeasonName(fromDate.format(df1) + " to " + toDate.format(df1));
			season.setDateFrom(fromDate);
			season.setDateTo(toDate);
			season = accommodationSeasonRepo.save(season);
			importRunData.seasons.put(fromtoString, season);
		}

		IMSAccommodationRate accommodationrate = new IMSAccommodationRate();
		accommodationrate.setHotelId(importRunData.hotelRc.getHotelId());
		accommodationrate.setRateId(importRunData.roomRates.size() + 1);
		accommodationrate.setDescription(roomName);
		accommodationrate.setRoomtypeId(roomtype.getRoomtypeId().intValue());
		accommodationrate.setSeasonId(season.getSeasonId());
		accommodationrate.setPolicyId(importRunData.policy.getPolicyId());
		accommodationrate.setBoardCode(importRunData.board.getBoardCode());
		accommodationrate.setAllocationId(allocationSummary.getAllocationId());
		accommodationrate.setDaysOfTheWeek(new DaysOfTheWeek());
		accommodationrate.getDaysOfTheWeek().setSunday(true);
		accommodationrate.getDaysOfTheWeek().setMonday(true);
		accommodationrate.getDaysOfTheWeek().setTuesday(true);
		accommodationrate.getDaysOfTheWeek().setWednesday(true);
		accommodationrate.getDaysOfTheWeek().setThursday(true);
		accommodationrate.getDaysOfTheWeek().setFriday(true);
		accommodationrate.getDaysOfTheWeek().setSaturday(true);
		accommodationrate.setPaxmixPricing(false);
		log.debug("createRoom::nett=" + accommodationLine.getNettPerNightPrice().substring(1) + "'");
		accommodationrate.setNett(new BigDecimal(accommodationLine.getNettPerNightPrice().substring(1).replaceAll("[^0-9\\.]", "")));
		accommodationrate.setRrp(new BigDecimal(accommodationLine.getSellPrice().substring(1).replaceAll("[^0-9\\.]", "")));
		accommodationrate = accommodationRateRepo.save(accommodationrate);
		importRunData.roomRates.put(accommodationrate.getRateId(), accommodationrate);

		Integer alloc = Integer.parseInt(accommodationLine.getAllocation());
		LocalDate d = fromDate;
		while (!d.isAfter(toDate))
		{
			//
			IMSAccommodationAllocation allocation = new IMSAccommodationAllocation();
			allocation.setHotelId(importRunData.hotelRc.getHotelId());
			allocation.setAllocationId(allocationSummary.getAllocationId());
			allocation.setAllocationDate(d);
			allocation.setAllocation(alloc);
			accommodationAllocationRepo.save(allocation);
			d = d.plusDays(1);
		}
	}

	@Data
	private static class MiniVenue
	{
		private String venueName;

		private String venueAddress;
	}

	@Data
	private static class MiniEvent
	{
		private String eventName;

		private LocalDate eventDate;

		private MiniVenue venue;

		private List<MiniEventClassAlloc> classifications = new ArrayList<>();;
	}

	@Data
	private static class MiniEventClassAlloc
	{
		private String className;

		private String description;

		private BigDecimal nettEUR;

		private BigDecimal markup;

		private BigDecimal rrpAUD;

		private Integer allocation = 0;
	}

	public int importEvent(String filename, String site, String prefix) throws Exception
	{
		log.debug("importEvent::enter for '" + filename + "'");

		Reader fileReader = new FileReader(filename);

		List<EventImportBean> beans = new CsvToBeanBuilder<EventImportBean>(fileReader).withType(EventImportBean.class).build().parse();

		Map<String, MiniVenue> miniVenues = new HashMap<>();
		Map<String, MiniEvent> miniEvents = new HashMap<>();
		int eventsImported = 0;
		int recCount = 0;
		for (EventImportBean eventLine : beans)
		{
			log.debug("importEvent::read line ticketType '" + eventLine.getTicketType());
			if (recCount == 0)
			{
				log.debug("importEvent::bypass header");
				recCount++;
				continue;
			}
			if (eventLine.getTicketType() != null && eventLine.getTicketType().length() > 0)
			{
				MiniVenue miniVenue = miniVenues.get(eventLine.getVenue());
				if (miniVenue == null)
				{
					miniVenue = new MiniVenue();
					miniVenue.setVenueName(eventLine.getVenue());
					miniVenue.setVenueAddress(eventLine.getVenueAddress());
					miniVenues.put(miniVenue.getVenueName(), miniVenue);
				}
				String[] eventNameParts = eventLine.getTicketType().split("-");
				String match = eventNameParts[0].trim();
				String pool = eventNameParts[1].trim();
				String category = eventNameParts[2].trim();
				String who = eventNameParts[3].trim();
				String eventName = match + " " + pool + " " + who;
				MiniEvent miniEvent = miniEvents.get(eventName);
				if (miniEvent == null)
				{
					miniEvent = new MiniEvent();
					miniEvent.setEventName(eventName);
					LocalDate d = LocalDate.parse(eventLine.getEventDate(), df4);
					miniEvent.setEventDate(d);
					miniEvent.setVenue(miniVenue);
					miniEvents.put(eventName, miniEvent);
				}
				MiniEventClassAlloc miniEventClassAlloc = new MiniEventClassAlloc();
				miniEventClassAlloc.setClassName(category);
				miniEventClassAlloc.setDescription(eventLine.getTicketDescription());
				String nett = eventLine.getNett().substring(0, eventLine.getNett().length() - 2).replace(",", "").trim();
				log.debug("importEvent::nett=" + nett);
				miniEventClassAlloc.setNettEUR(new BigDecimal(nett));
				miniEventClassAlloc.setMarkup(new BigDecimal(eventLine.getMarkup()));
				String rrp = eventLine.getSellPrice().substring(1).replace(",", "").trim();
				log.debug("importEvent::rrp=" + rrp);
				miniEventClassAlloc.setRrpAUD(new BigDecimal(rrp));
				try
				{
					miniEventClassAlloc.setAllocation(Integer.parseInt(eventLine.getAllocation().trim()));
					log.debug("importEvent::allotment=" + miniEventClassAlloc.getAllocation());
				}
				catch (Exception e)
				{
					log.debug("importEvent::failed parsing allotment " + eventLine.getAllocation());
				}
				miniEvent.getClassifications().add(miniEventClassAlloc);
			}
			recCount++;
		}
		int eventSaved = saveEvents(miniVenues, miniEvents);
		log.debug("importEvent::Imported " + eventsImported + " events, saved " + eventSaved);
		return eventsImported;
	}

	protected int saveEvents(Map<String, MiniVenue> miniVenues, Map<String, MiniEvent> miniEvents) throws Exception
	{
		log.debug("saveEvents::enter");
		int eventsSaved = 0;
		Map<String, EventVenue> eventVenues = new HashMap<>();

		Optional<EventSeries> eventSeriesOpt = eventSeriesRepo.findById(1171155);
		EventSeries eventSeries = eventSeriesOpt.get();
		Optional<EventSupplier> supplierOpt = eventSupplierRepo.findById(1171198);
		EventSupplier eventSupplier = supplierOpt.get();

		for (MiniVenue miniVenue : miniVenues.values())
		{
			com.torkirion.eroam.ims.datadomain.EventVenue data = new com.torkirion.eroam.ims.datadomain.EventVenue();
			data.setName(miniVenue.getVenueName());
			data.setAddress(new com.torkirion.eroam.ims.datadomain.Address());
			data.getAddress().setFullFormAddress(miniVenue.getVenueAddress());
			data.setLastUpdated(LocalDateTime.now());
			com.torkirion.eroam.ims.datadomain.EventVenue savedData = eventVenueRepo.save(data);
			eventVenues.put(miniVenue.getVenueName(), savedData);
			log.debug("saveEvents::saved venue " + miniVenue.getVenueName());
		}
		EventSaver eventSaver = new EventSaver();
		for (MiniEvent miniEvent : miniEvents.values())
		{
			com.torkirion.eroam.ims.datadomain.EventVenue venue = eventVenues.get(miniEvent.getVenue().getVenueName());
			log.debug("saveEvents::lookup venue " + miniEvent.getVenue().getVenueName() + "=" + venue);
			com.torkirion.eroam.ims.datadomain.Event event = new com.torkirion.eroam.ims.datadomain.Event();
			event.setName(miniEvent.getEventName());
			event.setStartDate(miniEvent.getEventDate());
			event.setEndDate(miniEvent.getEventDate());
			event.setEventSeries(eventSeries);
			event.setEventSupplier(eventSupplier);
			event.setEventVenue(venue);
			com.torkirion.eroam.ims.datadomain.Event savedEvent = eventSaver.saveEvent(eventRepo, event);
			log.debug("saveEvents::saved event " + savedEvent.getId());
			eventsSaved++;

			for (MiniEventClassAlloc classification : miniEvent.getClassifications())
			{
				com.torkirion.eroam.ims.datadomain.EventAllotment eventAllotment = new com.torkirion.eroam.ims.datadomain.EventAllotment();
				eventAllotment.setEvent(savedEvent);
				eventAllotment.setMaximumSale(100);
				eventAllotment.setMinimumSale(1);
				eventAllotment.setName(classification.getClassName());
				eventAllotment.setAllotment(classification.getAllocation());
				eventAllotment.setLastUpdated(LocalDateTime.now());
				com.torkirion.eroam.ims.datadomain.EventAllotment savedEventAllotment = eventSaver.saveEventAllotment(eventAllotmentRepo, eventAllotment);

				com.torkirion.eroam.ims.datadomain.EventClassification eventClassification = new com.torkirion.eroam.ims.datadomain.EventClassification();
				eventClassification.setAllotmentId(savedEventAllotment.getId());
				eventClassification.setEvent(savedEvent);
				eventClassification.setBundlesOnly(true);
				eventClassification.setCurrency("AUD");
				eventClassification.setName(classification.getClassName());
				eventClassification.setTicketingDescription(classification.getDescription());
				eventClassification.setLastUpdated(LocalDateTime.now());
				eventClassification.setNettPrice(classification.getNettEUR());
				eventClassification.setRrpPrice(classification.getRrpAUD());
				com.torkirion.eroam.ims.datadomain.EventClassification savedEventClassification = eventSaver.saveEventClassification(eventClassificationRepo, eventClassification);
			}
		}
		return eventsSaved;
	}

	public static class EventSaver
	{
		@Transactional
		public com.torkirion.eroam.ims.datadomain.Event saveEvent(IMSEventRepo eventRepo, com.torkirion.eroam.ims.datadomain.Event event)
		{
			return eventRepo.save(event);
		}

		@Transactional
		public com.torkirion.eroam.ims.datadomain.EventAllotment saveEventAllotment(IMSEventAllotmentRepo eventAllotmentRepo,
				com.torkirion.eroam.ims.datadomain.EventAllotment eventAllotment)
		{
			return eventAllotmentRepo.save(eventAllotment);
		}

		@Transactional
		public com.torkirion.eroam.ims.datadomain.EventClassification saveEventClassification(IMSEventClassificationRepo eventClassificationRepo,
				com.torkirion.eroam.ims.datadomain.EventClassification eventClassification)
		{
			return eventClassificationRepo.save(eventClassification);
		}
	}

	public int importTransportation(String filename, String site, String prefix) throws Exception
	{
		log.debug("importTransportation::enter for '" + filename + "'");

		Reader fileReader = new FileReader(filename);

		List<TransportImportBean> beans = new CsvToBeanBuilder<TransportImportBean>(fileReader).withType(TransportImportBean.class).build().parse();
		TransportSaver transportSaver = new TransportSaver();
		int transportImported = 0;
		int recCount = 0;
		// Pattern timeRegex = Pattern.compile("([0-9][0-9])([0-9][0-9])([\\+0-9]*)");
		Pattern timeRegex = Pattern.compile("([0-9][0-9])([0-9][0-9])");
		for (TransportImportBean transportLine : beans)
		{
			log.debug("importEvent::read line '" + transportLine.getFromCityName() + " to " + transportLine.getToCityName());
			if (recCount <= 1)
			{
				log.debug("importTransportation::bypass header");
				recCount++;
				continue;
			}
			log.debug("importTransportation::line " + recCount);
			TransportationBasic transportationBasic = new TransportationBasic();
			transportationBasic.setCurrency(transportLine.getCurrency());
			transportationBasic.setFromIata(transportLine.getFromCityIATA());
			transportationBasic.setToIata(transportLine.getToCityIATA());
			transportationBasic.setFlight(transportLine.getFlightNumber());
			LocalDate fromDate = null;
			log.debug("importTransportation::fromDate=" + transportLine.getFromDate());
			try
			{
				fromDate = LocalDate.parse(transportLine.getFromDate(), df1);
			}
			catch (Exception e)
			{
				try
				{
					fromDate = LocalDate.parse(transportLine.getFromDate(), df1a);
				}
				catch (Exception e1)
				{
					try
					{
						fromDate = LocalDate.parse(transportLine.getFromDate(), df1b);
					}
					catch (Exception e2)
					{
						fromDate = LocalDate.parse(transportLine.getFromDate(), df1c);
					}
				}
			}
			LocalDate toDate = null;
			log.debug("importTransportation::toDate=" + transportLine.getToDate());
			try
			{
				toDate = LocalDate.parse(transportLine.getToDate(), df1);
			}
			catch (Exception e)
			{
				try
				{
					toDate = LocalDate.parse(transportLine.getToDate(), df1a);
				}
				catch (Exception e1)
				{
					try
					{
						toDate = LocalDate.parse(transportLine.getToDate(), df1b);
					}
					catch (Exception e2)
					{
						toDate = LocalDate.parse(transportLine.getToDate(), df1c);
					}
				}
			}
			transportationBasic.setScheduleFrom(fromDate);
			transportationBasic.setScheduleTo(toDate);
			transportationBasic.setDaysOfTheWeek(new DaysOfTheWeek());
			transportationBasic.getDaysOfTheWeek().setSunday(true);
			transportationBasic.getDaysOfTheWeek().setMonday(true);
			transportationBasic.getDaysOfTheWeek().setTuesday(true);
			transportationBasic.getDaysOfTheWeek().setWednesday(true);
			transportationBasic.getDaysOfTheWeek().setThursday(true);
			transportationBasic.getDaysOfTheWeek().setFriday(true);
			transportationBasic.getDaysOfTheWeek().setSaturday(true);
			transportationBasic.setSearchIataFrom(transportLine.getFromCityIATA());
			transportationBasic.setSearchIataTo(transportLine.getToCityIATA());
			transportationBasic.setRequiresPassport(true);
			transportationBasic.setOnRequest(false);
			transportationBasic.setClasses(new HashSet<>());
			transportationBasic.setSegments(new HashSet<>());
			transportationBasic.setLastUpdated(LocalDateTime.now());
			transportationBasic = transportSaver.saveTransportBasic(transportationBasicRepo, transportationBasic);

			TransportationBasicClass transportationBasicClass = new TransportationBasicClass();
			transportationBasicClass.setTransportation(transportationBasic);
			transportationBasicClass.setReference("ECONOMY");
			transportationBasicClass.setClassCode("Y");
			transportationBasicClass.setClassDescription("Economy");
			transportationBasicClass.setBaggageMaxPieces(2);
			transportationBasicClass.setBaggageMaxWeight(20);
			transportationBasicClass.setRefundable(true);
			String baseNett = transportLine.getBaseNett().trim().substring(1).replace(",", "");
			String baseRrp = transportLine.getBaseRrp().trim().substring(1).replace(",", "");
			String taxNett = transportLine.getTaxNett().trim().substring(1).replace(",", "");
			String taxRrp = transportLine.getTaxRrp().trim().substring(1).replace(",", "");
			transportationBasicClass.setAdultNett((new BigDecimal(baseNett)).add(new BigDecimal(taxNett)));
			transportationBasicClass.setAdultRrp((new BigDecimal(baseRrp)).add(new BigDecimal(taxRrp)));
			transportationBasicClass.setChildNett((new BigDecimal(baseNett)).add(new BigDecimal(taxNett)));
			transportationBasicClass.setChildRrp((new BigDecimal(baseRrp)).add(new BigDecimal(taxRrp)));
			transportationBasicClass.setLastUpdated(LocalDateTime.now());
			transportationBasicClass = transportSaver.saveTransportBasicClass(transportationBasicClassRepo, transportationBasicClass);

			TransportationBasicSegment transportationBasicSegment = new TransportationBasicSegment();
			transportationBasicSegment.setTransportation(transportationBasic);
			transportationBasicSegment.setSegmentNumber(0);
			transportationBasicSegment.setDepartureAirportLocationCode(transportLine.getLeg1FromIATA());
			transportationBasicSegment.setDepartureTerminal("");
			Integer hourPortion = null;
			Integer minutePortion = null;
			log.debug("importTransportation::leg1 etd='" + transportLine.getLeg1Etd() + "'");
			{
				String[] parts = transportLine.getLeg1Etd().split("\\+");
				if (parts[0].length() == 4)
				{
					hourPortion = Integer.parseInt(parts[0].substring(0, 2));
					minutePortion = Integer.parseInt(parts[0].substring(2, 4));
				}
				else
				{
					hourPortion = Integer.parseInt(parts[0].substring(0, 1));
					minutePortion = Integer.parseInt(parts[0].substring(1, 3));
				}
				LocalTime localTime = LocalTime.of(hourPortion, minutePortion, 0);
				transportationBasicSegment.setDepartureTime(localTime);
			}
			transportationBasicSegment.setArrivalAirportLocationCode(transportLine.getLeg1ToIATA());
			transportationBasicSegment.setArrivalTerminal("");
			log.debug("importTransportation::leg1 eta='" + transportLine.getLeg1Eta() + "'");
			{
				String[] parts = transportLine.getLeg1Eta().split("\\+");
				if (parts[0].length() == 4)
				{
					hourPortion = Integer.parseInt(parts[0].substring(0, 2));
					minutePortion = Integer.parseInt(parts[0].substring(2, 4));
				}
				else
				{
					hourPortion = Integer.parseInt(parts[0].substring(0, 1));
					minutePortion = Integer.parseInt(parts[0].substring(1, 3));
				}
				LocalTime localTime = LocalTime.of(hourPortion, minutePortion, 0);
				transportationBasicSegment.setArrivalTime(localTime);
				Integer arrivalDayExtra = 0;
				log.debug("importTransportation::parts.length=" + parts.length + ":" + (parts.length > 1 ? parts[1] : ""));
				if (parts.length > 1 && parts[1].length() > 0)
				{
					arrivalDayExtra = Integer.parseInt(parts[1].substring(0, 1));
				}
				log.debug("importTransportation::arrivalDayExtra=" + arrivalDayExtra);
				transportationBasicSegment.setArrivalDayExtra(arrivalDayExtra);
			}
			transportationBasicSegment.setPassportRequired(true);
			transportationBasicSegment.setFlightDurationMinutes(0);
			transportationBasicSegment.setMarketingAirlineCode("EK");
			transportationBasicSegment.setMarketingAirlineFlightNumber(transportLine.getLeg1Flight());
			transportationBasicSegment.setOperatingAirlineCode("EK");
			transportationBasicSegment.setOperatingAirlineFlightNumber(transportLine.getLeg1Flight());
			transportationBasicSegment.setLastUpdated(LocalDateTime.now());
			transportationBasicSegment = transportSaver.saveTransportBasicSegment(transportationBasicSegmentRepo, transportationBasicSegment);
			transportationBasicSegment = new TransportationBasicSegment();
			transportationBasicSegment.setTransportation(transportationBasic);
			transportationBasicSegment.setSegmentNumber(1);
			transportationBasicSegment.setDepartureAirportLocationCode(transportLine.getLeg2FromIATA());
			transportationBasicSegment.setDepartureTerminal("");
			log.debug("importTransportation::leg2 etd='" + transportLine.getLeg2Etd() + "'");
			{
				String[] parts = transportLine.getLeg2Etd().split("\\+");
				if (parts[0].length() == 4)
				{
					hourPortion = Integer.parseInt(parts[0].substring(0, 2));
					minutePortion = Integer.parseInt(parts[0].substring(2, 4));
				}
				else
				{
					hourPortion = Integer.parseInt(parts[0].substring(0, 1));
					minutePortion = Integer.parseInt(parts[0].substring(1, 3));
				}
				LocalTime localTime = LocalTime.of(hourPortion, minutePortion, 0);
				transportationBasicSegment.setDepartureTime(localTime);
			}
			transportationBasicSegment.setArrivalAirportLocationCode(transportLine.getLeg2ToIATA());
			transportationBasicSegment.setArrivalTerminal("");
			log.debug("importTransportation::leg2 eta='" + transportLine.getLeg2Eta() + "'");
			{
				String[] parts = transportLine.getLeg2Eta().split("\\+");
				if (parts[0].length() == 4)
				{
					hourPortion = Integer.parseInt(parts[0].substring(0, 2));
					minutePortion = Integer.parseInt(parts[0].substring(2, 4));
				}
				else
				{
					hourPortion = Integer.parseInt(parts[0].substring(0, 1));
					minutePortion = Integer.parseInt(parts[0].substring(1, 3));
				}
				LocalTime localTime = LocalTime.of(hourPortion, minutePortion, 0);
				transportationBasicSegment.setArrivalTime(localTime);
				Integer arrivalDayExtra = 0;
				log.debug("importTransportation::parts.length=" + parts.length + ":" + (parts.length > 1 ? parts[1] : ""));
				if (parts.length > 1 && parts[1].length() > 0)
				{
					arrivalDayExtra = Integer.parseInt(parts[1].substring(0, 1));
				}
				log.debug("importTransportation::arrivalDayExtra=" + arrivalDayExtra);
				transportationBasicSegment.setArrivalDayExtra(arrivalDayExtra);
			}
			transportationBasicSegment.setPassportRequired(true);
			transportationBasicSegment.setFlightDurationMinutes(0);
			transportationBasicSegment.setMarketingAirlineCode("EK");
			transportationBasicSegment.setMarketingAirlineFlightNumber(transportLine.getLeg2Flight());
			transportationBasicSegment.setOperatingAirlineCode("EK");
			transportationBasicSegment.setOperatingAirlineFlightNumber(transportLine.getLeg2Flight());
			transportationBasicSegment.setLastUpdated(LocalDateTime.now());
			transportationBasicSegment = transportSaver.saveTransportBasicSegment(transportationBasicSegmentRepo, transportationBasicSegment);
			transportImported++;
			recCount++;
		}
		return transportImported;
	}

	public static class TransportSaver
	{
		@Transactional
		public com.torkirion.eroam.ims.datadomain.TransportationBasic saveTransportBasic(IMSTransportationBasicRepo transportationBasicRepo,
				com.torkirion.eroam.ims.datadomain.TransportationBasic transportationBasic)
		{
			return transportationBasicRepo.save(transportationBasic);
		}

		@Transactional
		public com.torkirion.eroam.ims.datadomain.TransportationBasicClass saveTransportBasicClass(IMSTransportationBasicClassRepo transportationBasicClassRepo,
				com.torkirion.eroam.ims.datadomain.TransportationBasicClass transportationBasicClass)
		{
			return transportationBasicClassRepo.save(transportationBasicClass);
		}

		@Transactional
		public com.torkirion.eroam.ims.datadomain.TransportationBasicSegment saveTransportBasicSegment(IMSTransportationBasicSegmentRepo transportationBasicSegmentRepo,
				com.torkirion.eroam.ims.datadomain.TransportationBasicSegment transportationBasicSegment)
		{
			return transportationBasicSegmentRepo.save(transportationBasicSegment);
		}
	}

	private static final DateTimeFormatter df1 = DateTimeFormatter.ofPattern("dd/MM/yy");

	private static final DateTimeFormatter df1a = DateTimeFormatter.ofPattern("d/MM/yy");

	private static final DateTimeFormatter df1b = DateTimeFormatter.ofPattern("dd/M/yy");

	private static final DateTimeFormatter df1c = DateTimeFormatter.ofPattern("d/M/yy");

	private static final DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static final DateTimeFormatter df3 = DateTimeFormatter.ofPattern("dd/M/yy");

	private static final DateTimeFormatter df4 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
}
