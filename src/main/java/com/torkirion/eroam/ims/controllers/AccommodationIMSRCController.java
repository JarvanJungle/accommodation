package com.torkirion.eroam.ims.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
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

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.http.HttpMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.ims.Functions;
import com.torkirion.eroam.ims.apidomain.APIError;
import com.torkirion.eroam.ims.apidomain.AccommodationContent;
import com.torkirion.eroam.ims.apidomain.AccommodationContentWithoutId;
import com.torkirion.eroam.ims.apidomain.AccommodationSale;
import com.torkirion.eroam.ims.apidomain.AccommodationSummary;
import com.torkirion.eroam.ims.apidomain.Activity;
import com.torkirion.eroam.ims.apidomain.ActivitySale;
import com.torkirion.eroam.ims.apidomain.Allocation;
import com.torkirion.eroam.ims.apidomain.Boards;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies;
import com.torkirion.eroam.ims.apidomain.EventType;
import com.torkirion.eroam.ims.apidomain.Rates;
import com.torkirion.eroam.ims.apidomain.ResponseData;
import com.torkirion.eroam.ims.apidomain.Roomtypes;
import com.torkirion.eroam.ims.apidomain.Seasons;
import com.torkirion.eroam.ims.apidomain.Specials;
import com.torkirion.eroam.ims.apidomain.SupplierSummary;
import com.torkirion.eroam.ims.apidomain.AccommodationContentWithoutId.HotelImage;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.CancellationPolicy;
import com.torkirion.eroam.ims.apidomain.Rates.Rate;
import com.torkirion.eroam.ims.apidomain.Roomtypes.Roomtype;
import com.torkirion.eroam.ims.apidomain.Seasons.Season;
import com.torkirion.eroam.ims.apidomain.Specials.Special;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocationSummary;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationBoard;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCategory;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationFacility;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRate;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRoomtype;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSpecial;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocationSummary.AllocationKey;
import com.torkirion.eroam.ims.repository.IMSAccommodationCategoryRepo;
import com.torkirion.eroam.ims.repository.IMSAccommodationRCDataRepo;
import com.torkirion.eroam.ims.services.*;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.apidomain.LookupHotelbynameResult;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.controllers.AccommodationController;
import com.torkirion.eroam.microservice.accommodation.endpoint.ims.IMSService;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationSearchService;
import com.torkirion.eroam.microservice.apidomain.RequestData;

@Slf4j
@AllArgsConstructor
@Service
public class AccommodationIMSRCController
{
	@Autowired
	private DataService dataService;

	@Autowired
	private MapperService mapperService;

	public AccommodationRC getRC(String imsclient, String hotelId)
	{
		log.debug("getRC::enter for " + hotelId);
		try
		{
			Optional<IMSAccommodationRCData> opt = dataService.getAccommodationRCDataRepo().findById(hotelId);
			if (opt.isPresent())
			{
				IMSAccommodationRCData accommodationRCData = opt.get();
				AccommodationRC accommodationRC = mapperService.mapToRC(accommodationRCData);
				return accommodationRC;
			}
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("getRC::error " + e.toString(), e);
			return null;
		}
	}
}
