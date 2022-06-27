package com.torkirion.eroam.ims.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.ims.apidomain.AccommodationContent;
import com.torkirion.eroam.ims.apidomain.AccommodationSummary;
import com.torkirion.eroam.ims.apidomain.ActivityAgeBand;
import com.torkirion.eroam.ims.apidomain.ActivityAllotment;
import com.torkirion.eroam.ims.apidomain.Address;
import com.torkirion.eroam.ims.apidomain.Allocation;
import com.torkirion.eroam.ims.apidomain.Boards;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies;
import com.torkirion.eroam.ims.apidomain.CurrencyValue;
import com.torkirion.eroam.ims.apidomain.DaysOfTheWeek;
import com.torkirion.eroam.ims.apidomain.EventMerchandiseAPILink;
import com.torkirion.eroam.ims.apidomain.EventType;
import com.torkirion.eroam.ims.apidomain.Rates;
import com.torkirion.eroam.ims.apidomain.Roomtypes;
import com.torkirion.eroam.ims.apidomain.Seasons;
import com.torkirion.eroam.ims.apidomain.Specials;
import com.torkirion.eroam.ims.apidomain.Supplier;
import com.torkirion.eroam.ims.apidomain.SupplierSummary;
import com.torkirion.eroam.ims.apidomain.Transportation;
import com.torkirion.eroam.ims.apidomain.AccommodationContentWithoutId.HotelImage;
import com.torkirion.eroam.ims.apidomain.AccommodationSale.ItemStatus;
import com.torkirion.eroam.ims.apidomain.Activity.HotelPickup;
import com.torkirion.eroam.ims.apidomain.ActivityOption.ActivityOptionBlock;
import com.torkirion.eroam.ims.apidomain.ActivityOption.ActivityOptionPriceBand;
import com.torkirion.eroam.ims.apidomain.Boards.Board;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.BeforeCheckinAfterBooking;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.CancellationPolicy;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.CancellationPolicyLine;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.PenaltyType;
import com.torkirion.eroam.ims.apidomain.Rates.PaxmixRate;
import com.torkirion.eroam.ims.apidomain.Rates.Rate;
import com.torkirion.eroam.ims.apidomain.Roomtypes.Roomtype;
import com.torkirion.eroam.ims.apidomain.Seasons.Season;
import com.torkirion.eroam.ims.apidomain.Specials.Special;
import com.torkirion.eroam.ims.datadomain.AccommodationTypeTag;
import com.torkirion.eroam.ims.datadomain.ActivitySupplierAgeBand;
import com.torkirion.eroam.ims.datadomain.GeoCoordinates;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocationSummary;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationBoard;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRate;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRoomtype;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSpecial;
import com.torkirion.eroam.ims.datadomain.IMSLocation;
import com.torkirion.eroam.ims.datadomain.MerchandiseCategory;
import com.torkirion.eroam.ims.datadomain.TransportationBasicClass;
import com.torkirion.eroam.ims.datadomain.TransportationBasicSegment;
import com.torkirion.eroam.ims.repository.IMSAccommodationRCDataRepo;
import com.torkirion.eroam.ims.repository.IMSLocationRepo;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.FacilityGroup;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityRC;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import io.swagger.annotations.ApiModelProperty;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class MapperService
{
	public AccommodationContent map(IMSAccommodationRCData accommodationRC) throws Exception
	{
		AccommodationContent accommodationContent = new AccommodationContent();
		accommodationContent.setHotelId(accommodationRC.getHotelId());
		accommodationContent.setHotelName(accommodationRC.getAccommodationName());
		accommodationContent.setHotelOverview(accommodationRC.getDescription());
		accommodationContent.setAddress(new Address());
		BeanUtils.copyProperties(accommodationRC.getAddress(), accommodationContent.getAddress());
		if (accommodationRC.getAddress() != null && accommodationRC.getAddress().getGeoCoordinates() != null)
		{
			accommodationContent.getAddress().setGeoCoordinates(new com.torkirion.eroam.ims.apidomain.GeoCoordinates());
			accommodationContent.getAddress().getGeoCoordinates().setLatitude(accommodationRC.getAddress().getGeoCoordinates().getLatitude());
			accommodationContent.getAddress().getGeoCoordinates().setLongitude(accommodationRC.getAddress().getGeoCoordinates().getLongitude());
		}
		accommodationContent.setHotelRating(accommodationRC.getRating());
		accommodationContent.setChildAge(accommodationRC.getChildAge());
		accommodationContent.setInfantAge(accommodationRC.getInfantAge());
		accommodationContent.setHotelRating(accommodationRC.getRating());
		accommodationContent.setCurrency(accommodationRC.getCurrency());
		accommodationContent.setRrpCurrency(accommodationRC.getRrpCurrency());
		accommodationContent.setSupplier(accommodationRC.getSupplier());
		accommodationContent.setHotel_category(accommodationRC.getCategory());
		accommodationContent.setHotelRating(accommodationRC.getRating());
		accommodationContent.setOleryCompanyCode(accommodationRC.getOleryCompanyCode());
		if (accommodationRC.getFacilityGroupsJson() != null && accommodationRC.getFacilityGroupsJson().length() > 0)
			accommodationContent.setFacilities(getObjectMapper().readValue(accommodationRC.getFacilityGroupsJson(), new TypeReference<List<String>>()
			{}));
		else
			accommodationContent.setFacilities(new ArrayList<>());
		accommodationContent.setPhone(accommodationRC.getPhone());
		if (accommodationRC.getImagesJson() != null && accommodationRC.getImagesJson().length() > 0)
			accommodationContent.setHotel_images(getObjectMapper().readValue(accommodationRC.getImagesJson(), new TypeReference<List<HotelImage>>()
			{}));
		else
			accommodationContent.setHotel_images(new ArrayList<>());
		accommodationContent.setLastUpdated(accommodationRC.getLastUpdated());
		return accommodationContent;
	}

	public AccommodationContent mapContent(AccommodationRC accommodationRC)
	{
		AccommodationContent accommodationContent = new AccommodationContent();
		accommodationContent.setHotelId(accommodationRC.getCode());
		accommodationContent.setHotelName(accommodationRC.getAccommodationName());
		accommodationContent.setHotelOverview(accommodationRC.getIntroduction());
		accommodationContent.setAddress(new Address());
		BeanUtils.copyProperties(accommodationRC.getAddress(), accommodationContent.getAddress());
		accommodationContent.getAddress().setGeoCoordinates(new com.torkirion.eroam.ims.apidomain.GeoCoordinates());
		accommodationContent.getAddress().getGeoCoordinates().setLatitude(accommodationRC.getAddress().getGeoCoordinates().getLatitude());
		accommodationContent.getAddress().getGeoCoordinates().setLongitude(accommodationRC.getAddress().getGeoCoordinates().getLongitude());
		accommodationContent.setHotelRating(accommodationRC.getRating());
		accommodationContent.setOleryCompanyCode(accommodationRC.getOleryCompanyCode());
		accommodationContent.setFacilities(new ArrayList<>());
		for (FacilityGroup group : accommodationRC.getFacilityGroups())
		{
			for (String facility : group.getFacilities())
			{
				accommodationContent.getFacilities().add(facility);
			}
		}
		accommodationContent.setPhone(accommodationRC.getPhone());
		for (Image image : accommodationRC.getImages())
		{
			HotelImage hotelImage = new HotelImage();
			hotelImage.setUrl(image.getImageURL());
			hotelImage.setImageDescription(image.getImageDescription());
			accommodationContent.getHotel_images().add(hotelImage);
		}
		return accommodationContent;
	}

	public AccommodationRC mapToRC(IMSAccommodationRCData accommodationRCData) throws Exception
	{
		AccommodationRC accommodationRC = new AccommodationRC();
		accommodationRC.setAddress(new AccommodationRC.Address());
		BeanUtils.copyProperties(accommodationRCData, accommodationRC);
		BeanUtils.copyProperties(accommodationRCData.getAddress(), accommodationRC.getAddress());
		accommodationRC.getAddress().setGeoCoordinates(new com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.GeoCoordinates());
		if (accommodationRCData.getAddress() != null && accommodationRCData.getAddress().getGeoCoordinates() != null)
			BeanUtils.copyProperties(accommodationRCData.getAddress().getGeoCoordinates(), accommodationRC.getAddress().getGeoCoordinates());
		accommodationRC.setCode(IMSService.CHANNEL_PREFIX + accommodationRCData.getHotelId());
		accommodationRC.setChannel(IMSService.CHANNEL);
		accommodationRC.setChannelCode(accommodationRCData.getHotelId());
		if (accommodationRCData.getProductType() != null)
			accommodationRC.setProductType(AccommodationRC.AccommodationTypeTag.valueOf(accommodationRCData.getProductType().name()));
		List<HotelImage> images = new ArrayList<>();
		if (accommodationRCData.getImagesJson() != null && accommodationRCData.getImagesJson().length() > 0)
			images = getObjectMapper().readValue(accommodationRCData.getImagesJson(), new TypeReference<List<HotelImage>>()
			{});
		int imageOrder = 0;
		for (HotelImage i : images)
		{
			AccommodationRC.Image image = new AccommodationRC.Image();
			if (i.getUrl() != null)
			{
				image.setImageURL(i.getUrl());
				image.setImageDescription(i.getImageDescription());
				image.setImageOrder(imageOrder++);
				accommodationRC.getImages().add(image);
				if (accommodationRC.getImageThumbnail() == null)
				{
					accommodationRC.setImageThumbnail(image);
				}
			}
		}
		List<String> facilities = new ArrayList<>();
		if (accommodationRCData.getFacilityGroupsJson() != null && accommodationRCData.getFacilityGroupsJson().length() > 0)
			facilities = getObjectMapper().readValue(accommodationRCData.getFacilityGroupsJson(), new TypeReference<List<String>>()
			{});
		FacilityGroup facilityGroup = new FacilityGroup();
		facilityGroup.setFacilities(facilities);
		facilityGroup.setGroupName("Property facilities");
		accommodationRC.setFacilityGroups(new ArrayList<>());
		accommodationRC.getFacilityGroups().add(facilityGroup);

		return accommodationRC;
	}

	public IMSAccommodationRCData mapContent(AccommodationContent accommodationContent, IMSAccommodationRCData accommodationRC) throws Exception
	{
		if (accommodationRC == null)
			accommodationRC = new IMSAccommodationRCData();
		accommodationRC.setHotelId(accommodationContent.getHotelId());
		accommodationRC.setAccommodationName(accommodationContent.getHotelName());
		accommodationRC.setIntroduction(accommodationContent.getHotelOverview());
		accommodationRC.setDescription(accommodationContent.getHotelOverview());
		accommodationRC.setCurrency(accommodationContent.getCurrency());
		accommodationRC.setRrpCurrency(accommodationContent.getRrpCurrency());
		accommodationRC.setSupplier(accommodationContent.getSupplier());
		accommodationRC.setCategory(accommodationContent.getHotel_category());
		accommodationRC.setAddress(new com.torkirion.eroam.ims.datadomain.Address());
		BeanUtils.copyProperties(accommodationContent.getAddress(), accommodationRC.getAddress());
		accommodationRC.getAddress().setGeoCoordinates(new com.torkirion.eroam.ims.datadomain.GeoCoordinates());
		if (accommodationContent.getAddress() != null)
			BeanUtils.copyProperties(accommodationContent.getAddress().getGeoCoordinates(), accommodationRC.getAddress().getGeoCoordinates());
		accommodationRC.setRating(accommodationContent.getHotelRating());
		accommodationRC.setChildAge(accommodationContent.getChildAge());
		accommodationRC.setInfantAge(accommodationContent.getInfantAge());
		accommodationRC.setOleryCompanyCode(accommodationContent.getOleryCompanyCode());
		accommodationRC.setPhone(accommodationContent.getPhone());
		String facilitiesJson = getObjectMapper().writeValueAsString(accommodationContent.getFacilities());
		accommodationRC.setFacilityGroupsJson(facilitiesJson);
		String imagesJson = getObjectMapper().writeValueAsString(accommodationContent.getHotel_images());
		accommodationRC.setImagesJson(imagesJson);

		return accommodationRC;
	}

	public CancellationPolicies mapPolicies(List<IMSAccommodationCancellationPolicy> policyData)
	{
		CancellationPolicies cancellationPolicies = new CancellationPolicies();
		Map<Integer, CancellationPolicies.CancellationPolicy> ps = new HashMap<>();
		for (IMSAccommodationCancellationPolicy policyDataItem : policyData)
		{
			cancellationPolicies.setHotelId(policyDataItem.getHotelId());
			CancellationPolicies.CancellationPolicy p = ps.get(policyDataItem.getPolicyId());
			if (p == null)
			{
				p = new CancellationPolicies.CancellationPolicy();
				p.setPolicyId(policyDataItem.getPolicyId());
				p.setPolicyName(policyDataItem.getPolicyName());
				p.setBookingConditions(policyDataItem.getBookingConditions());
				ps.put(policyDataItem.getPolicyId(), p);
			}
			CancellationPolicies.CancellationPolicyLine line = new CancellationPolicies.CancellationPolicyLine();
			p.getLines().add(line);
			line.setLineId(policyDataItem.getLineId());
			line.setNumberOfDays(policyDataItem.getNumberOfDays());
			line.setBeforeCheckinAfterBooking(policyDataItem.getBeforeCheckinAfterBooking());
			line.setPenaltyType(policyDataItem.getPenaltyType());
			line.setPenalty(policyDataItem.getPenalty());
		}
		cancellationPolicies.getPolicies().addAll(ps.values());
		return cancellationPolicies;
	}

	public List<IMSAccommodationCancellationPolicy> mapPolicies(CancellationPolicies cancellationPolicies)
	{
		List<IMSAccommodationCancellationPolicy> policyDatas = new ArrayList<>();
		for (CancellationPolicy policy : cancellationPolicies.getPolicies())
		{
			for (CancellationPolicyLine line : policy.getLines())
			{
				IMSAccommodationCancellationPolicy policyData = new IMSAccommodationCancellationPolicy();
				policyData.setHotelId(cancellationPolicies.getHotelId());
				policyData.setPolicyId(policy.getPolicyId());
				policyData.setPolicyName(policy.getPolicyName());
				policyData.setBookingConditions(policy.getBookingConditions());
				policyData.setLineId(line.getLineId());
				policyData.setNumberOfDays(line.getNumberOfDays());
				policyData.setBeforeCheckinAfterBooking(line.getBeforeCheckinAfterBooking());
				policyData.setPenaltyType(line.getPenaltyType());
				policyData.setPenalty(line.getPenalty());
				policyDatas.add(policyData);
			}
		}
		return policyDatas;
	}

	/**
	 * Make sure don't delete any CNX policies that are being used!
	 * 
	 * @param oldPolicies
	 * @param newPolicies
	 * @return an optional error message
	 */
	public Optional<String> validatePolicies(List<IMSAccommodationCancellationPolicy> oldPolicies, List<IMSAccommodationCancellationPolicy> newPolicies, DataService dataService, String hotelId,
			String currencyId, String rrpCurrencyId)
	{
		log.debug("validatePolicies::enter");
		for (IMSAccommodationCancellationPolicy policy : newPolicies)
		{
			if (policy.getLineId() == null)
				return Optional.of("Line Id must not be empty");
			if (policy.getPolicyId() == null)
				return Optional.of("Policy Id must not be empty");
		}
		// find any policies that are to be deleted
		Set<Integer> policiesBeingDeleted = new HashSet<>();
		for (IMSAccommodationCancellationPolicy pOld : oldPolicies)
		{
			boolean found = false;
			for (IMSAccommodationCancellationPolicy pNew : newPolicies)
			{
				if (pOld.getPolicyId().intValue() == pNew.getPolicyId().intValue())
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				policiesBeingDeleted.add(pOld.getPolicyId());
			}
		}
		log.debug("validatePolicies::policiesBeingDeleted=" + policiesBeingDeleted);
		if (policiesBeingDeleted.size() > 0)
		{
			Rates rates = mapFullRates(dataService, hotelId, currencyId, rrpCurrencyId);
			for (Integer policyId : policiesBeingDeleted)
			{
				for (Rate rate : rates.getRates())
				{
					if (rate.getPolicyId() != null && policyId != null && rate.getPolicyId().equals(policyId))
					{
						return Optional.of("Policy with id " + policyId + " is in use");
					}
				}
			}
		}
		return Optional.empty();
	}

	public List<IMSAccommodationSeason> mapSeasons(Seasons seasons)
	{
		List<IMSAccommodationSeason> seasonDatas = new ArrayList<>();
		for (Season season : seasons.getSeasons())
		{
			IMSAccommodationSeason seasonData = new IMSAccommodationSeason();
			seasonData.setHotelId(seasons.getHotelId());
			seasonData.setSeasonId(season.getSeasonId());
			seasonData.setSeasonName(season.getSeasonName());
			seasonData.setDateFrom(season.getSeasonStartDate());
			seasonData.setDateTo(season.getSeasonEndDate());
			seasonDatas.add(seasonData);
		}
		return seasonDatas;
	}

	public Seasons mapSeasons(List<IMSAccommodationSeason> seasonDatas)
	{
		Seasons seasons = new Seasons();
		for (IMSAccommodationSeason seasonData : seasonDatas)
		{
			seasons.setHotelId(seasonData.getHotelId());
			seasons.getSeasons().add(mapSeason(seasonData));
		}
		return seasons;
	}

	public Seasons.Season mapSeason(IMSAccommodationSeason seasonData)
	{
		Seasons.Season s = new Seasons.Season();
		s.setSeasonId(seasonData.getSeasonId());
		s.setSeasonName(seasonData.getSeasonName());
		s.setSeasonStartDate(seasonData.getDateFrom());
		s.setSeasonEndDate(seasonData.getDateTo());
		return s;
	}

	/**
	 * Make sure don't delete any Seasonsthat are being used!
	 * 
	 * @param oldPolicies
	 * @param newPolicies
	 * @return an optional error message
	 */
	public Optional<String> validateSeasons(List<IMSAccommodationSeason> oldSeasons, List<IMSAccommodationSeason> newSeasons, DataService dataService, String hotelId, String currencyId, String rrpCurrencyId)
	{
		for (IMSAccommodationSeason season : newSeasons)
		{
			if (season.getSeasonId() == null)
				return Optional.of("Season Id must not be empty");
			if (season.getDateFrom() == null || season.getDateTo() == null)
				return Optional.of("Season dates must not be empty");
			if (!season.getDateFrom().isBefore(season.getDateTo()))
				return Optional.of("Season date from must be before season date to");
		}
		// find any seasons that are to be deleted
		Set<Integer> seasonsBeingDeleted = new HashSet<>();
		for (IMSAccommodationSeason sOld : oldSeasons)
		{
			boolean found = false;
			for (IMSAccommodationSeason sNew : newSeasons)
			{
				if (sOld.getSeasonId() == sNew.getSeasonId())
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				seasonsBeingDeleted.add(sOld.getSeasonId());
			}
		}
		if (seasonsBeingDeleted.size() > 0)
		{
			Rates rates = mapFullRates(dataService, hotelId, currencyId, rrpCurrencyId);
			for (Integer seasonId : seasonsBeingDeleted)
			{
				for (Rate rate : rates.getRates())
				{
					if (rate.getSeasonId().equals(seasonId))
					{
						return Optional.of("Season with id " + seasonId + " is in use");
					}
				}
			}
		}
		return Optional.empty();
	}

	public List<IMSAccommodationBoard> mapBoards(Boards boards)
	{
		List<IMSAccommodationBoard> boardDatas = new ArrayList<>();
		for (Board board : boards.getBoards())
		{
			IMSAccommodationBoard boardData = new IMSAccommodationBoard();
			boardData.setHotelId(boards.getHotelId());
			if (board.getBoardCode().length() > 10)
				boardData.setBoardCode(board.getBoardCode().substring(0, 10));
			else
				boardData.setBoardCode(board.getBoardCode());
			if (board.getBoardDescription().length() > 200)
				boardData.setBoardDescription(board.getBoardDescription().substring(0, 200));
			else
				boardData.setBoardDescription(board.getBoardDescription());
			boardDatas.add(boardData);
		}
		return boardDatas;
	}

	public Boards mapBoards(List<IMSAccommodationBoard> boardDatas)
	{
		Boards boards = new Boards();
		for (IMSAccommodationBoard boardData : boardDatas)
		{
			boards.setHotelId(boardData.getHotelId());
			Boards.Board b = new Boards.Board();
			b.setBoardCode(boardData.getBoardCode());
			b.setBoardDescription(boardData.getBoardDescription());
			boards.getBoards().add(b);
		}
		return boards;
	}

	/**
	 * Make sure don't delete any Boards that are being used!
	 * 
	 * @param oldPolicies
	 * @param newPolicies
	 * @return an optional error message
	 */
	public Optional<String> validateBoards(List<IMSAccommodationBoard> oldBoards, List<IMSAccommodationBoard> newBoards, DataService dataService, String hotelId, String currencyId, String rrpCurrencyId)
	{
		// find any boards that are to be deleted
		Set<String> newBoardCodes = new HashSet<>();
		for (IMSAccommodationBoard bNew : newBoards)
		{
			if (newBoardCodes.contains(bNew.getBoardCode()))
			{
				return Optional.of("Board with id " + bNew.getBoardCode() + " is duplicated");
			}
			newBoardCodes.add(bNew.getBoardCode());
		}

		Set<String> boardsBeingDeleted = new HashSet<>();
		for (IMSAccommodationBoard bOld : oldBoards)
		{
			boolean found = false;
			for (IMSAccommodationBoard bNew : newBoards)
			{
				if (bOld.getBoardCode().equals(bNew.getBoardCode()))
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				boardsBeingDeleted.add(bOld.getBoardCode());
			}
		}
		if (boardsBeingDeleted.size() > 0)
		{
			Rates rates = mapFullRates(dataService, hotelId, currencyId, rrpCurrencyId);
			for (String boardCode : boardsBeingDeleted)
			{
				for (Rate rate : rates.getRates())
				{
					if (rate.getBoardCode().equals(boardCode))
					{
						return Optional.of("Board with id " + boardCode + " is in use");
					}
				}
			}
		}
		return Optional.empty();
	}

	public List<IMSAccommodationRoomtype> mapRoomtypes(Roomtypes roomtypes, String currency)
	{
		log.debug("mapRoomtypes::enter");
		List<IMSAccommodationRoomtype> roomtypeDatas = new ArrayList<>();
		for (Roomtype roomtype : roomtypes.getRoomtypes())
		{
			IMSAccommodationRoomtype roomtypeData = new IMSAccommodationRoomtype();
			roomtypeData.setHotelId(roomtypes.getHotelId());
			roomtypeData.setRoomtypeId(roomtype.getRoomtypeId());
			roomtypeData.setDescription(roomtype.getDescription());
			roomtypeData.setRoomSize(roomtype.getRoomSize());
			roomtypeData.setBeddingDescription(roomtype.getBeddingDescription());
			roomtypeData.setMaximumAdults(roomtype.getMaximumAdults());
			roomtypeData.setMaximumPeople(roomtype.getMaximumPeople());
			roomtypeData.setSimpleAllocation(roomtype.getSimpleAllocation());
			if (roomtype.getSimpleAllocation())
			{
				roomtypeData.setRates(mapRates(roomtype.getRates(), currency));
				for (IMSAccommodationRate imsRate : roomtypeData.getRates())
				{
					imsRate.setAllocationId(roomtype.getRoomtypeId());
					imsRate.setRateId(roomtype.getRoomtypeId());
					imsRate.setHotelId(roomtypeData.getHotelId());
				}
			}
			roomtypeDatas.add(roomtypeData);
		}
		return roomtypeDatas;
	}

	public Roomtypes mapRoomtypes(List<IMSAccommodationRoomtype> roomtypeDatas)
	{
		Roomtypes roomtypes = new Roomtypes();
		for (IMSAccommodationRoomtype roomtypeData : roomtypeDatas)
		{
			roomtypes.setHotelId(roomtypeData.getHotelId());
			Roomtypes.Roomtype r = mapRoomtype(roomtypeData);
			roomtypes.getRoomtypes().add(r);
		}
		return roomtypes;
	}

	public Roomtypes.Roomtype mapRoomtype(IMSAccommodationRoomtype roomtypeData)
	{
		Roomtypes.Roomtype r = new Roomtypes.Roomtype();
		r.setRoomtypeId(roomtypeData.getRoomtypeId());
		r.setDescription(roomtypeData.getDescription());
		r.setRoomSize(roomtypeData.getRoomSize());
		r.setBeddingDescription(roomtypeData.getBeddingDescription());
		r.setMaximumAdults(roomtypeData.getMaximumAdults() == null ? 0 : roomtypeData.getMaximumAdults());
		r.setMaximumPeople(roomtypeData.getMaximumPeople() == null ? 0 : roomtypeData.getMaximumPeople());
		r.setSimpleAllocation(roomtypeData.getSimpleAllocation());
		return r;
	}

	/**
	 * Make sure don't delete any Rooms that are being used!
	 * 
	 * @param oldPolicies
	 * @param newPolicies
	 * @return an optional error message
	 */
	public Optional<String> validateRoomtypes(List<IMSAccommodationRoomtype> oldRoomtypes, List<IMSAccommodationRoomtype> newRoomtypes, List<IMSAccommodationRate> oldRates, DataService dataService,
			String hotelId, String currencyId, String rrpCurrencyId)
	{
		log.debug("validateRoomtypes::enter");
		for (IMSAccommodationRoomtype roomType : newRoomtypes)
		{
			if (roomType.getBeddingDescription() == null || roomType.getBeddingDescription().length() == 0)
				return Optional.of("Bedding Description must not be empty");
			if (roomType.getDescription() == null || roomType.getDescription().length() == 0)
				return Optional.of("Description must not be empty");
			// if (roomType.getRoomSize() == null || roomType.getRoomSize().length() == 0)
			// return Optional.of("Room size must not be empty");
			if (roomType.getRoomtypeId() == null)
				return Optional.of("Room type id must be supplied");
			if (roomType.getMaximumAdults() == null || roomType.getMaximumAdults() == 0)
				return Optional.of("Maximum adults must be supplied");
			if (roomType.getMaximumPeople() == null || roomType.getMaximumPeople() == 0)
				return Optional.of("Maximum people must be supplied");
		}
		// find any roomtypes that are to be deleted
		Set<Integer> roomtypesBeingDeleted = new HashSet<>();
		Set<Integer> ratesBeingDeleted = new HashSet<>();
		for (IMSAccommodationRoomtype rOld : oldRoomtypes)
		{
			boolean found = false;
			for (IMSAccommodationRoomtype rNew : newRoomtypes)
			{
				if (rOld.getRoomtypeId() != null && rOld.getRoomtypeId().equals(rNew.getRoomtypeId()))
				{
					found = true;
					break;
				}
			}
			if (!found && rOld.getRoomtypeId() != null)
			{
				roomtypesBeingDeleted.add(rOld.getRoomtypeId());
			}
		}
		log.debug("validateRoomtypes::roomtypesBeingDeleted=" + roomtypesBeingDeleted);
		if (roomtypesBeingDeleted.size() > 0)
		{
			Rates rates = mapFullRates(dataService, hotelId, currencyId, rrpCurrencyId);
			for (Integer roomtypeId : roomtypesBeingDeleted)
			{
				for (Rate rate : rates.getRates())
				{
					if (log.isDebugEnabled())
						log.debug("validateRoomtypes::checking " + rate + " against " + roomtypeId);
					if (rate.getRoomtypeId() != null && rate.getRoomtypeId().equals(roomtypeId) && !roomtypesBeingDeleted.contains(roomtypeId))
					{
						return Optional.of("Roomtype with id " + roomtypeId + " is in use");
					}
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Make sure don't delete any Rates that are being used!
	 * 
	 * @param oldPolicies
	 * @param newPolicies
	 * @return an optional error message
	 */
	public Optional<String> validateRates(List<IMSAccommodationRate> oldRates, List<IMSAccommodationRate> newRates, DataService dataService, String hotelId, String currencyId)
	{
		for (IMSAccommodationRate rate : newRates)
		{
			if (rate.getSeasonId() == null || rate.getBoardCode() == null || rate.getRoomtypeId() == null || rate.getPolicyId() == null)
				return Optional.of("Season, Board, Roomtype and Policy must be specified");
			if (rate.getDescription() == null)
				return Optional.of("Description must be specified");
		}
		return Optional.empty();
	}

	public List<IMSAccommodationRate> mapRates(Rates rates, String currencyId)
	{
		List<IMSAccommodationRate> rateDatas = new ArrayList<>();

		for (Rates.Rate rate : rates.getRates())
		{
			for (Rates.DOTWRate dotwRates : rate.getDotwRates())
			{
				if (dotwRates.getPaxmixPricing())
				{
					for (PaxmixRate paxmixPrice : dotwRates.getPaxmixRates())
					{
						IMSAccommodationRate rateData = new IMSAccommodationRate();
						rateData.setHotelId(rates.getHotelId());
						rateData.setRateId(rate.getRateId());
						rateData.setDescription(rate.getDescription());
						rateData.setMinimumNights(rate.getMinimumNights());
						rateData.setRoomtypeId(rate.getRoomtypeId());
						rateData.setRateGroup(rate.getRateGroup());
						rateData.setSeasonId(rate.getSeasonId());
						rateData.setPolicyId(rate.getPolicyId());
						rateData.setBoardCode(rate.getBoardCode());
						rateData.setAllocationId(rate.getAllocationId());
						rateData.setBundlesOnly(rate.getBundlesOnly());
						rateData.setPerInfantSurcharge(rate.getPerInfantSurcharge());
						rateData.setPaxmixPricing(true);
						rateData.setDaysOfTheWeek(new com.torkirion.eroam.ims.datadomain.DaysOfTheWeek());
						BeanUtils.copyProperties(dotwRates.getDaysOfTheWeek(), rateData.getDaysOfTheWeek());
						rateData.setNumberOfAdults(paxmixPrice.getNumberOfAdults());
						rateData.setNumberOfChildren(paxmixPrice.getNumberOfChildren());
						rateData.setNett(paxmixPrice.getNett().getAmount());
						if (paxmixPrice.getRrp() != null)
						{
							rateData.setRrp(paxmixPrice.getRrp().getAmount());
						}
						rateDatas.add(rateData);
					}
				}
				else
				{
					IMSAccommodationRate rateData = new IMSAccommodationRate();
					rateData.setHotelId(rates.getHotelId());
					rateData.setRateId(rate.getRateId());
					rateData.setDescription(rate.getDescription());
					rateData.setMinimumNights(rate.getMinimumNights());
					rateData.setRoomtypeId(rate.getRoomtypeId());
					rateData.setRateGroup(rate.getRateGroup());
					rateData.setSeasonId(rate.getSeasonId());
					rateData.setPolicyId(rate.getPolicyId());
					rateData.setBoardCode(rate.getBoardCode());
					rateData.setAllocationId(rate.getAllocationId());
					rateData.setBundlesOnly(rate.getBundlesOnly());
					rateData.setPerInfantSurcharge(rate.getPerInfantSurcharge());
					rateData.setPaxmixPricing(false);
					rateData.setDaysOfTheWeek(new com.torkirion.eroam.ims.datadomain.DaysOfTheWeek());
					BeanUtils.copyProperties(dotwRates.getDaysOfTheWeek(), rateData.getDaysOfTheWeek());
					rateData.setNett(dotwRates.getNett().getAmount());
					if (dotwRates.getRrp() != null)
					{
						rateData.setRrp(dotwRates.getRrp().getAmount());
					}
					rateDatas.add(rateData);
				}
			}
		}
		return rateDatas;
	}

	// map all rates for one hotel
	public Rates mapRates(List<IMSAccommodationRate> rateDatas, String nettCurrencyId, String rrpCurrencyId)
	{
		if (log.isDebugEnabled())
			log.debug("mapRates::enter with nettCurrencyId " + nettCurrencyId + ", rrpCurrencyId " + rrpCurrencyId);
		Rates rates = new Rates();
		Map<Integer, Rates.Rate> rateMap = new HashMap<>();
		HashMap<com.torkirion.eroam.ims.apidomain.DaysOfTheWeek, Rates.DOTWRate> dotwMap = new HashMap<>();
		for (IMSAccommodationRate rateData : rateDatas)
		{
			if (rateData == null || rateData.getNett() == null || rateData.getRrp() == null)
			{
				if (log.isDebugEnabled())
					log.debug("mapRates::null rateData, bypassing:" + rateData);
				continue;
			}
			if (log.isDebugEnabled())
				log.debug("mapRates::rateData=" + rateData);

			rates.setHotelId(rateData.getHotelId());

			Rates.Rate rate = rateMap.get(rateData.getRateId());
			if (log.isDebugEnabled())
				log.debug("mapRates::rate from map=" + rate);
			if (rate == null)
			{
				if (log.isDebugEnabled())
					log.debug("mapRates::create rate into map");
				rate = new Rates.Rate();
				rate.setHotelId(rateData.getHotelId());
				rate.setRateId(rateData.getRateId());
				rate.setDescription(rateData.getDescription());
				rate.setBundlesOnly(rateData.getBundlesOnly());
				rate.setPerInfantSurcharge(rateData.getPerInfantSurcharge());
				rate.setMinimumNights(rateData.getMinimumNights());
				rate.setRateGroup(rateData.getRateGroup());
				rate.setAllocationId(rateData.getAllocationId());
				rateMap.put(rateData.getRateId(), rate);
			}

			if (!rateData.getPaxmixPricing())
			{
				BeanUtils.copyProperties(rateData, rate);
				Rates.DOTWRate dotwRate = new Rates.DOTWRate();
				dotwRate.setPaxmixPricing(false);
				dotwRate.setDaysOfTheWeek(new com.torkirion.eroam.ims.apidomain.DaysOfTheWeek());
				BeanUtils.copyProperties(rateData.getDaysOfTheWeek(), dotwRate.getDaysOfTheWeek());
				dotwRate.setNett(new CurrencyValue(nettCurrencyId, rateData.getNett()));
				dotwRate.setRrp(new CurrencyValue(rrpCurrencyId, rateData.getRrp()));
				rate.getDotwRates().add(dotwRate);
			}
			else
			{
				// we will have multiple records for rateID, roomtypeId, seasonId, policyId, boardCode
				if (log.isDebugEnabled())
					log.debug("mapRates::getPaxmixPricing=true");

				DaysOfTheWeek dotw = new com.torkirion.eroam.ims.apidomain.DaysOfTheWeek();
				BeanUtils.copyProperties(rateData.getDaysOfTheWeek(), dotw);
				Rates.DOTWRate dotwRate = dotwMap.get(dotw);
				if (dotwRate == null)
				{
					dotwRate = new Rates.DOTWRate();
					dotwRate.setPaxmixPricing(true);
					dotwRate.setDaysOfTheWeek(dotw);
					dotwRate.setPaxmixRates(new ArrayList<>());
					rate.getDotwRates().add(dotwRate);
				}

				BeanUtils.copyProperties(rateData, rate);
				// see if it exists..
				if (log.isDebugEnabled())
					log.debug("mapRates::rateData=" + rateData + ", rate=" + rate + ", dotw=" + dotwRate.getDaysOfTheWeek());
				dotwRate.setPaxmixPricing(true);
				dotwRate.setDaysOfTheWeek(new com.torkirion.eroam.ims.apidomain.DaysOfTheWeek());
				BeanUtils.copyProperties(rateData.getDaysOfTheWeek(), dotwRate.getDaysOfTheWeek());
				dotwRate.setNett(null);
				dotwRate.setRrp(null);

				Rates.PaxmixRate paxmixRate = new Rates.PaxmixRate();
				paxmixRate.setNumberOfAdults(rateData.getNumberOfAdults());
				paxmixRate.setNumberOfChildren(rateData.getNumberOfChildren());
				paxmixRate.setNett(new CurrencyValue(nettCurrencyId, rateData.getNett()));
				if (rateData.getRrp() != null)
				{
					paxmixRate.setRrp(new CurrencyValue(rrpCurrencyId, rateData.getRrp()));
				}
				dotwRate.getPaxmixRates().add(paxmixRate);
			}
		}
		SortedSet<Rates.Rate> allRates = new TreeSet<>(new Rates.RateComparator());
		allRates.addAll(rateMap.values());
		rates.getRates().addAll(allRates);
		return rates;
	}

	public Allocation mapAllocation(List<IMSAccommodationAllocation> allocationData, IMSAccommodationAllocationSummary s)
	{
		Allocation allocation = new Allocation();
		for (IMSAccommodationAllocation a : allocationData)
		{
			if (allocation.getAllocationSummary() == null)
			{
				Allocation.AllocationSummary summary = new Allocation.AllocationSummary();
				summary.setHotelId(s.getHotelId());
				summary.setAllocationId(s.getAllocationId());
				summary.setAllocationDescription(s.getAllocationDescription());
				summary.setHandbackDays(s.getHandbackDays());
				allocation.setAllocationSummary(summary);
			}
			allocation.getAllocationDates().add(new Allocation.AccommodationAllocationDateData(a.getAllocationDate(), a.getAllocation()));
			allocation.getAllocationMap().put(a.getAllocationDate(), a.getAllocation());
		}
		return allocation;
	}

	public Allocation mapAllocation(List<IMSAccommodationAllocation> allocationData, Allocation.AllocationSummary s)
	{
		Allocation allocation = new Allocation();
		for (IMSAccommodationAllocation a : allocationData)
		{
			if (allocation.getAllocationSummary() == null)
			{
				Allocation.AllocationSummary summary = new Allocation.AllocationSummary();
				summary.setHotelId(s.getHotelId());
				summary.setAllocationId(s.getAllocationId());
				summary.setAllocationDescription(s.getAllocationDescription());
				summary.setHandbackDays(s.getHandbackDays());
				allocation.setAllocationSummary(summary);
			}
			allocation.getAllocationDates().add(new Allocation.AccommodationAllocationDateData(a.getAllocationDate(), a.getAllocation()));
			allocation.getAllocationMap().put(a.getAllocationDate(), a.getAllocation());
		}
		return allocation;
	}

	public List<Allocation.AllocationSummary> mapAllocationSummaries(List<IMSAccommodationAllocationSummary> dbSummaries)
	{
		List<Allocation.AllocationSummary> allocationSummaries = new ArrayList<>();
		for (IMSAccommodationAllocationSummary a : dbSummaries)
		{
			Allocation.AllocationSummary summary = new Allocation.AllocationSummary();
			summary.setHotelId(a.getHotelId());
			summary.setAllocationId(a.getAllocationId());
			summary.setAllocationDescription(a.getAllocationDescription());
			summary.setHandbackDays(a.getHandbackDays());
			allocationSummaries.add(summary);
		}
		return allocationSummaries;
	}

	/**
	 * Returns the rate structure fully linked with their objects, not just IDs and references
	 * 
	 * @param dataService
	 * @param hotelId
	 * @param currencyId
	 * @return
	 */
	public Rates mapFullRates(DataService dataService, String hotelId, String currencyId, String rrpCurrencyId)
	{
		log.debug("mapFullRates::for hotelId " + hotelId);

		Map<Integer, Roomtype> roomtypeMap = new HashMap<>();
		Map<String, Board> boardMap = new HashMap<>();
		Map<Integer, CancellationPolicy> policyMap = new HashMap<>();
		Map<Integer, Season> seasonMap = new HashMap<>();
		Map<Integer, Allocation> allocationMap = new HashMap<>();
		for (Roomtype roomtype : mapRoomtypes(dataService.getAccommodationRoomtypeRepo().findByHotelIdOrderByRoomtypeIdAsc(hotelId)).getRoomtypes())
		{
			roomtypeMap.put(roomtype.getRoomtypeId(), roomtype);
		}
		for (Board board : mapBoards(dataService.getAccommodationBoardRepo().findByHotelIdOrderByBoardCodeAsc(hotelId)).getBoards())
		{
			boardMap.put(board.getBoardCode(), board);
		}
		for (CancellationPolicy cancellationPolicy : mapPolicies(dataService.getAccommodationCancellationPolicyRepo().findByHotelIdOrderByPolicyIdAscLineIdAsc(hotelId)).getPolicies())
		{
			policyMap.put(cancellationPolicy.getPolicyId(), cancellationPolicy);
		}
		for (Season season : mapSeasons(dataService.getAccommodationSeasonRepo().findByHotelIdOrderByDateFromAsc(hotelId)).getSeasons())
		{
			seasonMap.put(season.getSeasonId(), season);
		}
		for (Allocation.AllocationSummary allocationSummary : mapAllocationSummaries(dataService.getAccommodationAllocationSummaryRepo().findByHotelId(hotelId)))
		{
			Allocation allocation = mapAllocation(dataService.getAccommodationAllocationRepo().findByHotelIdAndAllocationId(hotelId, allocationSummary.getAllocationId()), allocationSummary);
			allocationMap.put(allocationSummary.getAllocationId(), allocation);
		}

		Rates rates = mapRates(dataService.getAccommodationRateRepo().findByHotelIdOrderByDescriptionAsc(hotelId), currencyId, rrpCurrencyId);

		// hook them all up!
		for (Rates.Rate rate : rates.getRates())
		{
			if (log.isDebugEnabled())
				log.debug("mapRates::hooking up rate " + rate);

			rate.setRoomType(roomtypeMap.get(rate.getRoomtypeId()));
			rate.setBoard(boardMap.get(rate.getBoardCode()));
			rate.setPolicy(policyMap.get(rate.getPolicyId()));
			rate.setSeason(seasonMap.get(rate.getSeasonId()));
			rate.setAllocation(allocationMap.get(rate.getAllocationId()));
		}
		return rates;
	}

	public Specials mapSpecials(List<IMSAccommodationSpecial> specialDatas)
	{
		Specials specials = new Specials();
		for (IMSAccommodationSpecial specialData : specialDatas)
		{
			specials.setHotelId(specialData.getHotelId());
			Specials.Special special = new Specials.Special();
			BeanUtils.copyProperties(specialData, special);
			if ( specialData.getRateIds() != null && specialData.getRateIds().length() > 0)
			{
				if (log.isDebugEnabled())
					log.debug("mapSpecials::splitting '" + specialData.getRateIds() + "'");
				String[] a = specialData.getRateIds().split(",");
				List<Integer> rateIds = new ArrayList<>();
				for ( int i = 0; i < a.length; i++ )
				{
					if ( a[i] != null && a[i].length() > 0)
						rateIds.add(Integer.parseInt(a[i]));
				}
				special.setRateIds(rateIds);   
			}
			specials.getSpecials().add(special);
		}
		return specials;
	}

	public List<IMSAccommodationSpecial> mapSpecials(Specials specials)
	{
		List<IMSAccommodationSpecial> specialDatas = new ArrayList<>();
		for (Special special : specials.getSpecials())
		{
			IMSAccommodationSpecial specialData = new IMSAccommodationSpecial();
			specialData.setHotelId(specials.getHotelId());
			BeanUtils.copyProperties(special, specialData);
			if ( special.getRateIds() != null)
			{
				specialData.setRateIds("," + special.getRateIds().stream().map(i -> i.toString()).collect(Collectors.joining(",")) + "," );
			}
			specialDatas.add(specialData);
		}
		return specialDatas;
	}

	/**
	 * Make sure don't delete any Boards that are being used!
	 * 
	 * @param oldPolicies
	 * @param newPolicies
	 * @return an optional error message
	 */
	public Optional<String> validateSpecials(List<IMSAccommodationSpecial> oldSpecials, List<IMSAccommodationSpecial> newSpecials, DataService dataService, String hotelId, String currencyId)
	{
		for (IMSAccommodationSpecial special : newSpecials)
		{
			int adjustmentFields = 0;
			if (special.getAdjustPercentage() != null && special.getAdjustPercentage().compareTo(BigDecimal.ZERO) != 0)
				adjustmentFields++;
			if (special.getAdjustValue() != null && special.getAdjustValue().compareTo(BigDecimal.ZERO) != 0)
				adjustmentFields++;
			if (special.getFreeNights() != null && special.getFreeNights() != 0)
				adjustmentFields++;
			if (adjustmentFields == 0)
				return Optional.of("One adjustment must be specified");
			if (adjustmentFields > 1)
				return Optional.of("Only one adjustment must be specified");
		}
		return Optional.empty();
	}

	public void fixRateCurrency()
	{

	}

	public List<com.torkirion.eroam.ims.apidomain.AccommodationSale> mapAccommodationSales(List<com.torkirion.eroam.ims.datadomain.IMSAccommodationSale> accommodationSales)
	{
		List<com.torkirion.eroam.ims.apidomain.AccommodationSale> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.IMSAccommodationSale data : accommodationSales)
		{
			com.torkirion.eroam.ims.apidomain.AccommodationSale s = mapAccommodationSale(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.AccommodationSale mapAccommodationSale(com.torkirion.eroam.ims.datadomain.IMSAccommodationSale data)
	{
		com.torkirion.eroam.ims.apidomain.AccommodationSale s = new com.torkirion.eroam.ims.apidomain.AccommodationSale();
		BeanUtils.copyProperties(data, s);
		s.setItemStatus(ItemStatus.valueOf(data.getItemStatus().toString()));
		return s;
	}

	public List<com.torkirion.eroam.ims.apidomain.EventType> mapEventTypes(List<com.torkirion.eroam.ims.datadomain.EventType> eventTypes)
	{
		List<com.torkirion.eroam.ims.apidomain.EventType> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.EventType eventType : eventTypes)
		{
			com.torkirion.eroam.ims.apidomain.EventType e = new com.torkirion.eroam.ims.apidomain.EventType();
			e.setName(eventType.getName());
			list.add(e);
		}
		return list;
	}

	public List<com.torkirion.eroam.ims.apidomain.EventSeries> mapEventSeries(List<com.torkirion.eroam.ims.datadomain.EventSeries> eventSeriess)
	{
		List<com.torkirion.eroam.ims.apidomain.EventSeries> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.EventSeries data : eventSeriess)
		{
			com.torkirion.eroam.ims.apidomain.EventSeries s = mapEventSeries(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.EventSeries mapEventSeries(com.torkirion.eroam.ims.datadomain.EventSeries data)
	{
		com.torkirion.eroam.ims.apidomain.EventSeries s = new com.torkirion.eroam.ims.apidomain.EventSeries();
		BeanUtils.copyProperties(data, s);
		s.setType(data.getEventType().getName());
		if (data.getCountries() != null)
		{
			String[] split = data.getCountries().split(",");
			s.setCountries(Arrays.asList(split));
		}
		if (data.getMarketingCountries() != null)
		{
			String[] split = data.getMarketingCountries().split(",");
			s.setMarketingCountries(Arrays.asList(split));
		}
		if (data.getExcludedMarketingCountries() != null)
		{
			String[] split = data.getExcludedMarketingCountries().split(",");
			s.setExcludedMarketingCountries(Arrays.asList(split));
		}
		if (data.getEventMerchandiseLinks() != null && data.getEventMerchandiseLinks().size() > 0)
		{
			s.setEventMerchandiseLinks(new ArrayList<>());
			for (com.torkirion.eroam.ims.datadomain.EventMerchandiseLink link : data.getEventMerchandiseLinks())
			{
				com.torkirion.eroam.ims.apidomain.EventMerchandiseAPILink apiLink = new com.torkirion.eroam.ims.apidomain.EventMerchandiseAPILink();
				apiLink.setMerchandiseId(link.getMerchandise().getId());
				apiLink.setMerchandiseName(link.getMerchandise().getName());
				apiLink.setMandatoryInclusion(link.getMandatoryInclusion());
				apiLink.setEventSeriesId(link.getEventSeries().getId());
				apiLink.setEventSeriesName(link.getEventSeries().getName());
				apiLink.setId(link.getId());
				s.getEventMerchandiseLinks().add(apiLink);
			}
		}
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.EventSeries mapEventSeries(com.torkirion.eroam.ims.apidomain.EventSeries api,
			com.torkirion.eroam.ims.datadomain.EventType type)
	{
		com.torkirion.eroam.ims.datadomain.EventSeries data = new com.torkirion.eroam.ims.datadomain.EventSeries();
		BeanUtils.copyProperties(api, data);
		data.setCountries(org.apache.commons.lang3.StringUtils.join(api.getCountries(), ","));
		data.setMarketingCountries(org.apache.commons.lang3.StringUtils.join(api.getMarketingCountries(), ","));
		data.setExcludedMarketingCountries(org.apache.commons.lang3.StringUtils.join(api.getExcludedMarketingCountries(), ","));
		data.setEventType(type);
		data.setEventMerchandiseLinks(new HashSet<>());
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.EventSupplier> mapEventSuppliers(List<com.torkirion.eroam.ims.datadomain.EventSupplier> eventSuppliers)
	{
		List<com.torkirion.eroam.ims.apidomain.EventSupplier> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.EventSupplier data : eventSuppliers)
		{
			com.torkirion.eroam.ims.apidomain.EventSupplier s = mapEventSupplier(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.EventSupplier mapEventSupplier(com.torkirion.eroam.ims.datadomain.EventSupplier data)
	{
		com.torkirion.eroam.ims.apidomain.EventSupplier s = new com.torkirion.eroam.ims.apidomain.EventSupplier();
		BeanUtils.copyProperties(data, s);
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.EventSupplier mapEventSupplier(com.torkirion.eroam.ims.apidomain.EventSupplier api)
	{
		com.torkirion.eroam.ims.datadomain.EventSupplier data = new com.torkirion.eroam.ims.datadomain.EventSupplier();
		BeanUtils.copyProperties(api, data);
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.EventVenue> mapEventVenues(List<com.torkirion.eroam.ims.datadomain.EventVenue> eventVenues)
	{
		List<com.torkirion.eroam.ims.apidomain.EventVenue> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.EventVenue data : eventVenues)
		{
			com.torkirion.eroam.ims.apidomain.EventVenue s = mapEventVenue(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.EventVenue mapEventVenue(com.torkirion.eroam.ims.datadomain.EventVenue data)
	{
		com.torkirion.eroam.ims.apidomain.EventVenue s = new com.torkirion.eroam.ims.apidomain.EventVenue();
		BeanUtils.copyProperties(data, s);
		s.setAddress(new Address());
		BeanUtils.copyProperties(data.getAddress(), s.getAddress());
		if (data.getAddress() != null)
		{
			s.getAddress().setGeoCoordinates(new com.torkirion.eroam.ims.apidomain.GeoCoordinates());
			if (data.getAddress().getGeoCoordinates() != null)
				BeanUtils.copyProperties(data.getAddress().getGeoCoordinates(), s.getAddress().getGeoCoordinates());
		}
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.EventVenue mapEventVenue(com.torkirion.eroam.ims.apidomain.EventVenue api)
	{
		com.torkirion.eroam.ims.datadomain.EventVenue data = new com.torkirion.eroam.ims.datadomain.EventVenue();
		BeanUtils.copyProperties(api, data);
		data.setAddress(new com.torkirion.eroam.ims.datadomain.Address());
		BeanUtils.copyProperties(api.getAddress(), data.getAddress());
		if (api.getAddress() != null)
		{
			data.getAddress().setGeoCoordinates(new GeoCoordinates());
			if (api.getAddress().getGeoCoordinates() != null)
				BeanUtils.copyProperties(api.getAddress().getGeoCoordinates(), data.getAddress().getGeoCoordinates());
		}
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.Event> mapEvents(List<com.torkirion.eroam.ims.datadomain.Event> events)
	{
		List<com.torkirion.eroam.ims.apidomain.Event> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.Event data : events)
		{
			com.torkirion.eroam.ims.apidomain.Event s = mapEvent(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.Event mapEvent(com.torkirion.eroam.ims.datadomain.Event data)
	{
		com.torkirion.eroam.ims.apidomain.Event s = new com.torkirion.eroam.ims.apidomain.Event();
		BeanUtils.copyProperties(data, s);
		s.setSeriesId(data.getEventSeries().getId());
		s.setSeries(mapEventSeries(data.getEventSeries()));
		s.setSupplierId(data.getEventSupplier().getId());
		s.setSupplier(mapEventSupplier(data.getEventSupplier()));
		s.setVenueId(data.getEventVenue().getId());
		s.setVenue(mapEventVenue(data.getEventVenue()));
		if (data.getStartTime() != null)
			s.setStartTime(data.getStartTime().format(timeFormatter));
		s.setStartDate(data.getStartDate().format(dateFormatter));
		if (data.getEndDate() != null)
			s.setEndDate(data.getEndDate().format(dateFormatter));
		return s;
	}

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");;

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");;

	public com.torkirion.eroam.ims.datadomain.Event mapEvent(com.torkirion.eroam.ims.apidomain.Event api,
			com.torkirion.eroam.ims.datadomain.EventSeries eventSeries, com.torkirion.eroam.ims.datadomain.EventSupplier eventSupplier,
			com.torkirion.eroam.ims.datadomain.EventVenue eventVenue)
	{
		com.torkirion.eroam.ims.datadomain.Event data = new com.torkirion.eroam.ims.datadomain.Event();
		BeanUtils.copyProperties(api, data);
		if (data.getExternalEventId() != null && data.getExternalEventId().length() > 20)
			data.setExternalEventId(data.getExternalEventId().substring(0, 20));
		data.setEventSeries(eventSeries);
		data.setEventSupplier(eventSupplier);
		data.setEventVenue(eventVenue);
		if (api.getStartTime() != null && api.getStartTime().length() > 0)
			data.setStartTime(java.time.LocalTime.parse(api.getStartTime(), timeFormatter));
		else
			data.setStartTime(null);
		data.setStartDate(java.time.LocalDate.parse(api.getStartDate(), dateFormatter));
		if (api.getEndDate() != null)
			data.setEndDate(java.time.LocalDate.parse(api.getEndDate(), dateFormatter));
		else
			data.setEndDate(java.time.LocalDate.parse(api.getStartDate(), dateFormatter));
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.EventAllotment> mapEventAllotments(List<com.torkirion.eroam.ims.datadomain.EventAllotment> eventAllotments)
	{
		List<com.torkirion.eroam.ims.apidomain.EventAllotment> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.EventAllotment data : eventAllotments)
		{
			com.torkirion.eroam.ims.apidomain.EventAllotment s = mapEventAllotment(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.EventAllotment mapEventAllotment(com.torkirion.eroam.ims.datadomain.EventAllotment data)
	{
		com.torkirion.eroam.ims.apidomain.EventAllotment s = new com.torkirion.eroam.ims.apidomain.EventAllotment();
		BeanUtils.copyProperties(data, s);
		s.setEventId(data.getEvent().getId());
		if (data.getMultiplePattern() != null && data.getMultiplePattern().length() > 0)
		{
			String[] split = data.getMultiplePattern().split(",");
			s.setMultiplePattern(new ArrayList<>());
			for (int i = 0; i < split.length; i++)
			{
				s.getMultiplePattern().add(Integer.parseInt(split[i]));
			}
		}
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.EventAllotment mapEventAllotment(com.torkirion.eroam.ims.apidomain.EventAllotment api,
			com.torkirion.eroam.ims.datadomain.Event event)
	{
		com.torkirion.eroam.ims.datadomain.EventAllotment data = new com.torkirion.eroam.ims.datadomain.EventAllotment();
		BeanUtils.copyProperties(api, data);
		data.setEvent(event);
		data.setMultiplePattern(org.apache.commons.lang3.StringUtils.join(api.getMultiplePattern(), ","));
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.EventClassification> mapEventClassifications(
			List<com.torkirion.eroam.ims.datadomain.EventClassification> eventClassifications, List<com.torkirion.eroam.ims.apidomain.EventAllotment> eventAllotments)
	{
		Map<Integer, com.torkirion.eroam.ims.apidomain.EventAllotment> allotmentMap = new HashMap<>();
		for (com.torkirion.eroam.ims.apidomain.EventAllotment a : eventAllotments)
		{
			allotmentMap.put(a.getId(), a);
		}
		List<com.torkirion.eroam.ims.apidomain.EventClassification> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.EventClassification data : eventClassifications)
		{
			com.torkirion.eroam.ims.apidomain.EventClassification s = mapEventClassification(data, allotmentMap.get(data.getAllotmentId()));
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.EventClassification mapEventClassification(com.torkirion.eroam.ims.datadomain.EventClassification data,
			com.torkirion.eroam.ims.apidomain.EventAllotment allotment)
	{
		com.torkirion.eroam.ims.apidomain.EventClassification s = new com.torkirion.eroam.ims.apidomain.EventClassification();
		BeanUtils.copyProperties(data, s);
		s.setEventId(data.getEvent().getId());
		if (allotment != null)
		{
			s.setAllotmentId(allotment.getId());
			s.setAllotment(allotment);
		}
		if (data.getDays() != null)
		{
			String[] split = data.getDays().split(",");
			s.setDays(new ArrayList<>());
			for (int i = 0; i < split.length; i++)
			{
				s.getDays().add(Integer.parseInt(split[i]));
			}
		}
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.EventClassification mapEventClassification(com.torkirion.eroam.ims.apidomain.EventClassification api,
			com.torkirion.eroam.ims.datadomain.Event event)
	{
		com.torkirion.eroam.ims.datadomain.EventClassification data = new com.torkirion.eroam.ims.datadomain.EventClassification();
		BeanUtils.copyProperties(api, data);
		data.setDays(org.apache.commons.lang3.StringUtils.join(api.getDays(), ","));
		data.setEvent(event);
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.EventSale> mapEventSales(List<com.torkirion.eroam.ims.datadomain.EventSale> eventSales)
	{
		List<com.torkirion.eroam.ims.apidomain.EventSale> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.EventSale data : eventSales)
		{
			com.torkirion.eroam.ims.apidomain.EventSale s = mapEventSale(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.EventSale mapEventSale(com.torkirion.eroam.ims.datadomain.EventSale data)
	{
		com.torkirion.eroam.ims.apidomain.EventSale s = new com.torkirion.eroam.ims.apidomain.EventSale();
		BeanUtils.copyProperties(data, s);
		return s;
	}

	public List<com.torkirion.eroam.ims.apidomain.MerchandiseCategory> mapMerchandiseCategories(
			List<com.torkirion.eroam.ims.datadomain.MerchandiseCategory> merchandiseCategories)
	{
		List<com.torkirion.eroam.ims.apidomain.MerchandiseCategory> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.MerchandiseCategory merchandiseCategory : merchandiseCategories)
		{
			com.torkirion.eroam.ims.apidomain.MerchandiseCategory e = new com.torkirion.eroam.ims.apidomain.MerchandiseCategory();
			e.setName(merchandiseCategory.getName());
			list.add(e);
		}
		return list;
	}

	public List<com.torkirion.eroam.ims.apidomain.MerchandiseSupplier> mapMerchandiseSuppliers(
			List<com.torkirion.eroam.ims.datadomain.MerchandiseSupplier> merchandiseSuppliers)
	{
		List<com.torkirion.eroam.ims.apidomain.MerchandiseSupplier> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.MerchandiseSupplier data : merchandiseSuppliers)
		{
			com.torkirion.eroam.ims.apidomain.MerchandiseSupplier s = mapMerchandiseSupplier(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.MerchandiseSupplier mapMerchandiseSupplier(com.torkirion.eroam.ims.datadomain.MerchandiseSupplier data)
	{
		com.torkirion.eroam.ims.apidomain.MerchandiseSupplier s = new com.torkirion.eroam.ims.apidomain.MerchandiseSupplier();
		BeanUtils.copyProperties(data, s);
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.MerchandiseSupplier mapMerchandiseSupplier(com.torkirion.eroam.ims.apidomain.MerchandiseSupplier api)
	{
		com.torkirion.eroam.ims.datadomain.MerchandiseSupplier data = new com.torkirion.eroam.ims.datadomain.MerchandiseSupplier();
		BeanUtils.copyProperties(api, data);
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.Merchandise> mapMerchandise(List<com.torkirion.eroam.ims.datadomain.Merchandise> m) throws Exception
	{
		List<com.torkirion.eroam.ims.apidomain.Merchandise> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.Merchandise data : m)
		{
			com.torkirion.eroam.ims.apidomain.Merchandise s = mapMerchandise(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.Merchandise mapMerchandise(com.torkirion.eroam.ims.datadomain.Merchandise data) throws Exception
	{
		com.torkirion.eroam.ims.apidomain.Merchandise s = new com.torkirion.eroam.ims.apidomain.Merchandise();
		BeanUtils.copyProperties(data, s);
		s.setSupplierId(data.getMerchandiseSupplier().getId());
		s.setSupplier(mapMerchandiseSupplier(data.getMerchandiseSupplier()));
		if (data.getImagesJson() != null)
		{
			s.setImages(getObjectMapper().readValue(data.getImagesJson(), new TypeReference<List<String>>()
			{}));
		}
		else
		{
			s.setImages(new ArrayList<>());
		}
		/*
		 * if (data.getBrandsJson() != null && data.getBrandsJson().length() > 0) {
		 * s.setBrands(getObjectMapper().readValue(data.getBrandsJson(), new TypeReference<List<String>>() {})); } else {
		 * s.setBrands(new ArrayList<>()); }
		 */
		/*
		 * if ( data.getEventMerchandiseLinks() != null && data.getEventMerchandiseLinks().size() > 0 ) {
		 * s.setEventMerchandiseLinks(new HashSet<>()); for ( com.torkirion.eroam.microservice.ims.datadomain.EventMerchandiseLink
		 * link : data.getEventMerchandiseLinks() ) { com.torkirion.eroam.microservice.ims.apidomain.EventMerchandiseAPILink
		 * apiLink = new com.torkirion.eroam.microservice.ims.apidomain.EventMerchandiseAPILink();
		 * apiLink.setEventSeriesId(link.getEventSeries().getId()); apiLink.setEventSeriesName(link.getEventSeries().getName());
		 * apiLink.setMandatoryInclusion(link.getMandatoryInclusion()); apiLink.setMerchandiseId(data.getId());
		 * apiLink.setMerchandiseName(data.getName()); s.getEventMerchandiseLinks().add(apiLink); } }
		 */

		s.setMerchandiseCategory(data.getMerchandiseCategory().getName());
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.Merchandise mapMerchandise(com.torkirion.eroam.ims.apidomain.Merchandise api,
			com.torkirion.eroam.ims.datadomain.MerchandiseSupplier merchandiseSupplier, MerchandiseCategory merchandiseCategory) throws Exception
	{
		com.torkirion.eroam.ims.datadomain.Merchandise data = new com.torkirion.eroam.ims.datadomain.Merchandise();
		BeanUtils.copyProperties(api, data);
		data.setMerchandiseSupplier(merchandiseSupplier);
		data.setMerchandiseCategory(merchandiseCategory);
		data.setImagesJson(getObjectMapper().writeValueAsString(api.getImages()));
		// data.setBrandsJson(getObjectMapper().writeValueAsString(api.getBrands()));
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.MerchandiseOption> mapMerchandiseOptions(List<com.torkirion.eroam.ims.datadomain.MerchandiseOption> merchandiseOptions)
	{
		List<com.torkirion.eroam.ims.apidomain.MerchandiseOption> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.MerchandiseOption data : merchandiseOptions)
		{
			com.torkirion.eroam.ims.apidomain.MerchandiseOption s = mapMerchandiseOption(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.MerchandiseOption mapMerchandiseOption(com.torkirion.eroam.ims.datadomain.MerchandiseOption data)
	{
		com.torkirion.eroam.ims.apidomain.MerchandiseOption s = new com.torkirion.eroam.ims.apidomain.MerchandiseOption();
		BeanUtils.copyProperties(data, s);
		s.setMerchandiseId(data.getMerchandise().getId());
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.MerchandiseOption mapMerchandiseOption(com.torkirion.eroam.ims.apidomain.MerchandiseOption api,
			com.torkirion.eroam.ims.datadomain.Merchandise merchandise)
	{
		com.torkirion.eroam.ims.datadomain.MerchandiseOption data = new com.torkirion.eroam.ims.datadomain.MerchandiseOption();
		BeanUtils.copyProperties(api, data);
		data.setMerchandise(merchandise);
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.MerchandiseSale> mapMerchandiseSales(List<com.torkirion.eroam.ims.datadomain.MerchandiseSale> merchandiseSales)
	{
		List<com.torkirion.eroam.ims.apidomain.MerchandiseSale> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.MerchandiseSale data : merchandiseSales)
		{
			com.torkirion.eroam.ims.apidomain.MerchandiseSale s = mapMerchandiseSale(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.MerchandiseSale mapMerchandiseSale(com.torkirion.eroam.ims.datadomain.MerchandiseSale data)
	{
		com.torkirion.eroam.ims.apidomain.MerchandiseSale s = new com.torkirion.eroam.ims.apidomain.MerchandiseSale();
		BeanUtils.copyProperties(data, s);
		return s;
	}

	public List<com.torkirion.eroam.ims.apidomain.ActivitySupplier> mapActivitySuppliers(List<com.torkirion.eroam.ims.datadomain.ActivitySupplier> activitySuppliers)
			throws Exception
	{
		List<com.torkirion.eroam.ims.apidomain.ActivitySupplier> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.ActivitySupplier data : activitySuppliers)
		{
			com.torkirion.eroam.ims.apidomain.ActivitySupplier s = mapActivitySupplier(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.datadomain.ActivitySupplier mapActivitySupplier(com.torkirion.eroam.ims.apidomain.ActivitySupplier api) throws Exception
	{
		com.torkirion.eroam.ims.datadomain.ActivitySupplier data = new com.torkirion.eroam.ims.datadomain.ActivitySupplier();
		BeanUtils.copyProperties(api, data);
		// TODO map ageBands
		return data;
	}

	public com.torkirion.eroam.ims.apidomain.ActivitySupplier mapActivitySupplier(com.torkirion.eroam.ims.datadomain.ActivitySupplier data) throws Exception
	{
		com.torkirion.eroam.ims.apidomain.ActivitySupplier api = new com.torkirion.eroam.ims.apidomain.ActivitySupplier();
		BeanUtils.copyProperties(data, api);
		if (data.getAgebands() != null)
		{
			for (ActivitySupplierAgeBand ageBandData : data.getAgebands())
			{
				com.torkirion.eroam.ims.apidomain.ActivityAgeBand agebandApi = new ActivityAgeBand();
				BeanUtils.copyProperties(ageBandData, agebandApi);
				api.getAgeBands().add(agebandApi);
			}
		}
		return api;
	}

	public List<com.torkirion.eroam.ims.apidomain.Activity> mapActivities(List<com.torkirion.eroam.ims.datadomain.Activity> activities) throws Exception
	{
		List<com.torkirion.eroam.ims.apidomain.Activity> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.Activity data : activities)
		{
			com.torkirion.eroam.ims.apidomain.Activity s = mapActivity(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.microservice.activities.apidomain.ActivityRC mapActivityRC(com.torkirion.eroam.microservice.activities.datadomain.ActivityRCData data) throws Exception
	{
		ActivityRC activityRC = new ActivityRC();
		BeanUtils.copyProperties(data, activityRC);
		activityRC.setSupplierName("Viator");
		activityRC.setGeoCoordinates(new LatitudeLongitude());
		BeanUtils.copyProperties(data.getGeoCoordinates(), activityRC.getGeoCoordinates());
		if (data.getImagesJson() != null && data.getImagesJson().length() > 0)
		{
			activityRC.setImages(new ArrayList<>());
			List<String> imageURLs = getObjectMapper().readValue(data.getImagesJson(), new TypeReference<List<String>>(){});
			for ( String imageURL : imageURLs)
			{
				ActivityRC.Image image = new ActivityRC.Image();
				image.setImageURL(imageURL);
				activityRC.getImages().add(image);
			}
		}
		return activityRC;
	}

	public com.torkirion.eroam.ims.apidomain.Activity mapActivity(com.torkirion.eroam.ims.datadomain.Activity data) throws Exception
	{
		com.torkirion.eroam.ims.apidomain.Activity s = new com.torkirion.eroam.ims.apidomain.Activity();
		BeanUtils.copyProperties(data, s);
		s.setSupplierId(data.getActivitySupplier().getId());
		s.setSupplier(mapActivitySupplier(data.getActivitySupplier()));
		s.setGeoCoordinates(new com.torkirion.eroam.ims.apidomain.GeoCoordinates());
		if (data.getGeoCoordinates() != null)
			BeanUtils.copyProperties(data.getGeoCoordinates(), s.getGeoCoordinates());
		if (data.getCategoriesJson() != null && data.getCategoriesJson().length() > 0)
		{
			s.setCategories(getObjectMapper().readValue(data.getCategoriesJson(), new TypeReference<List<String>>()
			{}));
		}
		else
		{
			s.setCategories(new ArrayList<>());
		}
		if (data.getImagesJson() != null && data.getImagesJson().length() > 0)
		{
			s.setImages(getObjectMapper().readValue(data.getImagesJson(), new TypeReference<List<String>>()
			{}));
		}
		else
		{
			s.setImages(new ArrayList<>());
		}
		if (data.getInclusionsJson() != null && data.getInclusionsJson().length() > 0)
		{
			s.setInclusions(getObjectMapper().readValue(data.getInclusionsJson(), new TypeReference<List<String>>()
			{}));
		}
		else
		{
			s.setInclusions(new ArrayList<>());
		}
		if (data.getExclusionsJson() != null && data.getExclusionsJson().length() > 0)
		{
			s.setExclusions(getObjectMapper().readValue(data.getExclusionsJson(), new TypeReference<List<String>>()
			{}));
		}
		else
		{
			s.setExclusions(new ArrayList<>());
		}
		if (data.getAdditionalInformationJson() != null && data.getAdditionalInformationJson().length() > 0)
		{
			s.setAdditionalInformation(getObjectMapper().readValue(data.getAdditionalInformationJson(), new TypeReference<List<String>>()
			{}));
		}
		else
		{
			s.setAdditionalInformation(new ArrayList<>());
		}
		if (data.getHotelPickupsJson() != null && data.getHotelPickupsJson().length() > 0)
		{
			s.setHotelPickups(getObjectMapper().readValue(data.getHotelPickupsJson(), new TypeReference<List<HotelPickup>>()
			{}));
		}
		else
		{
			s.setAdditionalInformation(new ArrayList<>());
		}
		return s;
	}

	public com.torkirion.eroam.ims.datadomain.Activity mapActivity(com.torkirion.eroam.ims.apidomain.Activity api,
			com.torkirion.eroam.ims.datadomain.ActivitySupplier activitySupplier) throws Exception
	{
		com.torkirion.eroam.ims.datadomain.Activity data = new com.torkirion.eroam.ims.datadomain.Activity();
		BeanUtils.copyProperties(api, data);
		if (data.getExternalActivityId() != null && data.getExternalActivityId().length() > 20)
			data.setExternalActivityId(data.getExternalActivityId().substring(0, 20));
		if (data.getOperator() != null && data.getOperator().length() > 20)
			data.setOperator(data.getOperator().substring(0, 100));
		data.setActivitySupplier(activitySupplier);
		data.setGeoCoordinates(new GeoCoordinates());
		if (api.getGeoCoordinates() != null)
			BeanUtils.copyProperties(api.getGeoCoordinates(), data.getGeoCoordinates());
		data.setCategoriesJson(getObjectMapper().writeValueAsString(api.getCategories()));
		data.setImagesJson(getObjectMapper().writeValueAsString(api.getImages()));
		data.setInclusionsJson(getObjectMapper().writeValueAsString(api.getInclusions()));
		data.setExclusionsJson(getObjectMapper().writeValueAsString(api.getExclusions()));
		data.setAdditionalInformationJson(getObjectMapper().writeValueAsString(api.getAdditionalInformation()));
		data.setHotelPickupsJson(getObjectMapper().writeValueAsString(api.getHotelPickups()));
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.ActivityOption> mapActivityOptions(List<com.torkirion.eroam.ims.datadomain.ActivityOption> activityOptions) throws Exception
	{
		List<com.torkirion.eroam.ims.apidomain.ActivityOption> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.ActivityOption data : activityOptions)
		{
			com.torkirion.eroam.ims.apidomain.ActivityOption s = mapActivityOption(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.ActivityOption mapActivityOption(com.torkirion.eroam.ims.datadomain.ActivityOption data) throws Exception
	{
		com.torkirion.eroam.ims.apidomain.ActivityOption api = new com.torkirion.eroam.ims.apidomain.ActivityOption();
		BeanUtils.copyProperties(data, api);
		if (data.getPriceBlocksJson() != null && data.getPriceBlocksJson().length() > 0)
			api.setPriceBlocks(getObjectMapper().readValue(data.getPriceBlocksJson(), new TypeReference<List<com.torkirion.eroam.ims.apidomain.ActivityOption.ActivityOptionBlock>>()
			{}));
		else
			api.setPriceBlocks(new ArrayList<>());
		return api;
	}

	public com.torkirion.eroam.ims.datadomain.ActivityOption mapActivityOption(com.torkirion.eroam.ims.apidomain.ActivityOption api,
			com.torkirion.eroam.ims.datadomain.Activity activity) throws Exception
	{
		com.torkirion.eroam.ims.datadomain.ActivityOption data = new com.torkirion.eroam.ims.datadomain.ActivityOption();
		data.setActivity(activity);
		BeanUtils.copyProperties(api, data);
		log.debug("mapActivityOption::setting up band names");
		for (ActivityOptionBlock priceBlock : api.getPriceBlocks())
		{
			for (Entry<Integer, ActivityOptionPriceBand> ageBand : priceBlock.getPriceBands().entrySet())
			{
				// set the name properly
				for (ActivitySupplierAgeBand dataAgeBand : activity.getActivitySupplier().getAgebands())
				{
					if (dataAgeBand.getId().intValue() == ageBand.getKey().intValue())
					{
						ageBand.getValue().setAgeBandName(dataAgeBand.getBandName());
					}
				}
			}
		}
		data.setPriceBlocksJson(getObjectMapper().writeValueAsString(api.getPriceBlocks()));
		return data;
	}

	public List<com.torkirion.eroam.ims.apidomain.ActivityDepartureTime> mapActivityDepartureTimes(
			List<com.torkirion.eroam.ims.datadomain.ActivityDepartureTime> activityDepartureTimes) throws Exception
	{
		List<com.torkirion.eroam.ims.apidomain.ActivityDepartureTime> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.ActivityDepartureTime data : activityDepartureTimes)
		{
			com.torkirion.eroam.ims.apidomain.ActivityDepartureTime s = mapActivityDepartureTime(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.ActivityDepartureTime mapActivityDepartureTime(com.torkirion.eroam.ims.datadomain.ActivityDepartureTime data) throws Exception
	{
		com.torkirion.eroam.ims.apidomain.ActivityDepartureTime api = new com.torkirion.eroam.ims.apidomain.ActivityDepartureTime();
		BeanUtils.copyProperties(data, api);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		api.setDepartureTime(data.getDepartureTime().format(dtf));
		return api;
	}

	public com.torkirion.eroam.ims.datadomain.ActivityDepartureTime mapActivityDepartureTime(com.torkirion.eroam.ims.apidomain.ActivityDepartureTime api,
			com.torkirion.eroam.ims.datadomain.Activity activity) throws Exception
	{
		com.torkirion.eroam.ims.datadomain.ActivityDepartureTime data = new com.torkirion.eroam.ims.datadomain.ActivityDepartureTime();
		BeanUtils.copyProperties(api, data);
		data.setActivity(activity);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		data.setDepartureTime(LocalTime.parse(api.getDepartureTime(), dtf));
		return data;
	}

	public ActivityAllotment mapActivityAllotment(List<com.torkirion.eroam.ims.datadomain.ActivityAllotment> dataList)
	{
		ActivityAllotment activityAllotment = new ActivityAllotment();
		for (com.torkirion.eroam.ims.datadomain.ActivityAllotment data : dataList)
		{
			if (activityAllotment.getAllotmentSummary() == null)
			{
				activityAllotment.setAllotmentSummary(new ActivityAllotment.AllotmentSummary());
				activityAllotment.getAllotmentSummary().setActivityId(data.getActivityId());
				activityAllotment.getAllotmentSummary().setOptionId(data.getOptionId());
				activityAllotment.getAllotmentSummary().setDepartureTimeId(data.getDepartureTimeId());
			}
			if (data.getAllotment() != null && data.getAllotment().intValue() > 0)
			{
				ActivityAllotment.DateData dateData = new ActivityAllotment.DateData();
				dateData.setDate(data.getAllotmentDate());
				dateData.setAllotment(data.getAllotment());
				activityAllotment.getAllotmentDates().add(dateData);
			}
		}
		return activityAllotment;
	}

	public List<com.torkirion.eroam.ims.datadomain.ActivityAllotment> mapActivityAllotment(ActivityAllotment activityAllotment)
	{
		List<com.torkirion.eroam.ims.datadomain.ActivityAllotment> dataList = new ArrayList<>();
		for (ActivityAllotment.DateData dateData : activityAllotment.getAllotmentDates())
		{
			com.torkirion.eroam.ims.datadomain.ActivityAllotment data = new com.torkirion.eroam.ims.datadomain.ActivityAllotment();
			data.setActivityId(activityAllotment.getAllotmentSummary().getActivityId());
			data.setOptionId(activityAllotment.getAllotmentSummary().getOptionId());
			data.setDepartureTimeId(activityAllotment.getAllotmentSummary().getDepartureTimeId());
			data.setAllotmentDate(dateData.getDate());
			data.setAllotment(dateData.getAllotment());
			dataList.add(data);
		}
		return dataList;
	}

	public List<com.torkirion.eroam.ims.apidomain.ActivitySale> mapActivitySales(List<com.torkirion.eroam.ims.datadomain.ActivitySale> activitySales)
	{
		List<com.torkirion.eroam.ims.apidomain.ActivitySale> list = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.ActivitySale data : activitySales)
		{
			com.torkirion.eroam.ims.apidomain.ActivitySale s = mapActivitySale(data);
			list.add(s);
		}
		return list;
	}

	public com.torkirion.eroam.ims.apidomain.ActivitySale mapActivitySale(com.torkirion.eroam.ims.datadomain.ActivitySale data)
	{
		com.torkirion.eroam.ims.apidomain.ActivitySale s = new com.torkirion.eroam.ims.apidomain.ActivitySale();
		BeanUtils.copyProperties(data, s);
		return s;
	}

	public Transportation mapTransportation(com.torkirion.eroam.ims.datadomain.TransportationBasic dataTransportation)
	{
		Transportation apiTransportation = new Transportation();
		if (dataTransportation.getSegments() != null)
		{
			for (TransportationBasicSegment dataSegment : dataTransportation.getSegments())
			{
				Transportation.Segment apiSegment = new Transportation.Segment();
				apiSegment.setId(dataSegment.getId());
				apiSegment.setSegmentNumber(dataSegment.getSegmentNumber());
				apiSegment.setDepartureAirportLocationCode(dataSegment.getDepartureAirportLocationCode());
				apiSegment.setDepartureTerminal(dataSegment.getDepartureTerminal());
				apiSegment.setDepartureTime(dataSegment.getDepartureTime());
				apiSegment.setArrivalAirportLocationCode(dataSegment.getArrivalAirportLocationCode());
				apiSegment.setArrivalTerminal(dataSegment.getArrivalTerminal());
				apiSegment.setArrivalTime(dataSegment.getArrivalTime());
				apiSegment.setArrivalDayExtra(dataSegment.getArrivalDayExtra());
				apiSegment.setFlightDurationMinutes(dataSegment.getFlightDurationMinutes());
				apiSegment.setMarketingAirlineCode(dataSegment.getMarketingAirlineCode());
				apiSegment.setMarketingAirlineFlightNumber(dataSegment.getMarketingAirlineFlightNumber());
				apiSegment.setOperatingAirlineCode(dataSegment.getOperatingAirlineCode());
				apiSegment.setOperatingAirlineFlightNumber(dataSegment.getOperatingAirlineFlightNumber());
				apiSegment.setLastUpdated(dataSegment.getLastUpdated());
				apiTransportation.getSegments().add(apiSegment);
			}
		}
		if (dataTransportation.getClasses() != null)
		{
			for (TransportationBasicClass flightClass : dataTransportation.getClasses())
			{
				Transportation.TransportationClass transportationClass = new Transportation.TransportationClass();
				BeanUtils.copyProperties(flightClass, transportationClass, "transportation");
				apiTransportation.getClasses().add(transportationClass);
			}
		}
		apiTransportation.setId(dataTransportation.getId());
		apiTransportation.setCurrency(dataTransportation.getCurrency());
		apiTransportation.setRrpCurrency(dataTransportation.getRrpCurrency());
		apiTransportation.setFromIata(dataTransportation.getFromIata());
		apiTransportation.setToIata(dataTransportation.getToIata());
		apiTransportation.setFlight(dataTransportation.getFlight());
		apiTransportation.setScheduleFrom(dataTransportation.getScheduleFrom());
		apiTransportation.setScheduleTo(dataTransportation.getScheduleTo());
		apiTransportation.setDaysOfTheWeek(new DaysOfTheWeek());
		apiTransportation.setSearchIataFrom(dataTransportation.getSearchIataFrom());
		apiTransportation.setSearchIataTo(dataTransportation.getSearchIataTo());
		apiTransportation.setLastUpdated(dataTransportation.getLastUpdated());
		apiTransportation.setRequiresPassport(dataTransportation.getRequiresPassport() == null ? false : dataTransportation.getRequiresPassport());
		apiTransportation.setOnRequest(dataTransportation.getOnRequest() == null ? false : dataTransportation.getOnRequest());
		apiTransportation.setSupplier(dataTransportation.getSupplier());
		apiTransportation.setBookingConditions(dataTransportation.getBookingConditions());
		BeanUtils.copyProperties(dataTransportation.getDaysOfTheWeek(), apiTransportation.getDaysOfTheWeek());
		return apiTransportation;
	}

	public SupplierSummary mapSupplierSummary(com.torkirion.eroam.ims.datadomain.Supplier data)
	{
		SupplierSummary supplierSummary = new SupplierSummary();
		supplierSummary.setId(data.getId());
		supplierSummary.setExternalSupplierId(data.getExternalSupplierId());
		supplierSummary.setSupplierName(data.getSupplierName());
		supplierSummary.setLastUpdated(data.getLastUpdated());
		return supplierSummary;
	}
	
	public Supplier mapSupplier(com.torkirion.eroam.ims.datadomain.Supplier data)
	{
		Supplier apiSupplier = new Supplier();
		BeanUtils.copyProperties(data, apiSupplier);
		if ( validString(data.getReservationsEmail()) || validString(data.getReservationsName()) || validString(data.getReservationsPhone()))
		{
			Supplier.SupplierContact contact = new Supplier.SupplierContact();
			contact.setContactType(Supplier.SupplierContactType.RESERVATIONS);
			contact.setEmail(data.getReservationsEmail());
			contact.setName(data.getReservationsName());
			contact.setPhone(data.getReservationsPhone());
			apiSupplier.getContacts().add(contact);
		}
		if ( validString(data.getContractingEmail()) || validString(data.getContractingName()) || validString(data.getContractingPhone()))
		{
			Supplier.SupplierContact contact = new Supplier.SupplierContact();
			contact.setContactType(Supplier.SupplierContactType.CONTRACTING);
			contact.setEmail(data.getContractingEmail());
			contact.setName(data.getContractingName());
			contact.setPhone(data.getContractingPhone());
			apiSupplier.getContacts().add(contact);
		}
		if ( validString(data.getCustomerserviceEmail()) || validString(data.getCustomerserviceName()) || validString(data.getCustomerservicePhone()))
		{
			Supplier.SupplierContact contact = new Supplier.SupplierContact();
			contact.setContactType(Supplier.SupplierContactType.CUSTOMER_SERVICE);
			contact.setEmail(data.getCustomerserviceEmail());
			contact.setName(data.getCustomerserviceName());
			contact.setPhone(data.getCustomerservicePhone());
			apiSupplier.getContacts().add(contact);
		}
		if ( validString(data.getGmEmail()) || validString(data.getGmName()) || validString(data.getGmPhone()))
		{
			Supplier.SupplierContact contact = new Supplier.SupplierContact();
			contact.setContactType(Supplier.SupplierContactType.GM);
			contact.setEmail(data.getGmEmail());
			contact.setName(data.getGmName());
			contact.setPhone(data.getGmPhone());
			apiSupplier.getContacts().add(contact);
		}
		if ( validString(data.getAccountsEmail()) || validString(data.getAccountsName()) || validString(data.getAccountsPhone()))
		{
			Supplier.SupplierContact contact = new Supplier.SupplierContact();
			contact.setContactType(Supplier.SupplierContactType.ACCOUNTS);
			contact.setEmail(data.getAccountsEmail());
			contact.setName(data.getAccountsName());
			contact.setPhone(data.getAccountsPhone());
			apiSupplier.getContacts().add(contact);
		}
		return apiSupplier;
	}

	private final ObjectMapper getObjectMapper()
	{
		if (_objectMapper == null)
		{
			_objectMapper = new ObjectMapper();
			_objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return _objectMapper;
	}

	private boolean validString(String s)
	{
		if ( StringUtils.isEmpty(s))
			return false;
		else
			return true;
	}

	private ObjectMapper _objectMapper;
}
