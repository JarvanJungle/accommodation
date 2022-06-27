package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.io.*;
import java.math.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.sql.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.AccommodationTypeTag;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.FacilityGroup;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.ImageTag;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsAPIProperties;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsHttpService;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsInterface;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.BoardTypeInclusion;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.BoardTypeInclusionData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.BoardTypeInclusionRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.Country;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.CountryData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.YalagoCountryRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.ErrataCategory;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.ErrataCategoryData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.ErrataCategoryRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.Establishment;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentExtra;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentExtraData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentExtraOption;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentExtraOptionData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentExtraOptionRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentExtraRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentFacility;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentFacilityData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentFacilityRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentImage;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentImageData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentImageRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentRoomType;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentRoomTypeData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentRoomTypeRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentText;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentTextData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentTextRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.Facility;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.FacilityData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.FacilityRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.Location;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.LocationData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.LocationRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.Province;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.ProvinceData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.ProvinceRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.SupplierBoardType;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.SupplierBoardTypeData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.SupplierBoardTypeRepo;
import com.torkirion.eroam.microservice.accommodation.services.AccommodationRCService;
import com.torkirion.eroam.microservice.repository.CountryDAO;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.Airports.Terminal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class JayrideStaticLoader
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private AirportRepo airportRepo;

	@Autowired
	private AirportTerminalRepo airportTerminalRepo;
	
	private static final String SITE_DEFAULT = "eroam";

	@Async
	@Transactional
	public void fetchAirports() throws Exception
	{
		log.debug("fetchAirports::entering");

		JayrideAPIProperties jayrideProperties = new JayrideAPIProperties(propertiesDAO, SITE_DEFAULT);
		HttpService httpService = new JayrideHttpService(jayrideProperties);
		
		Map<String, String> params = new HashMap<>();
		params.put("key", jayrideProperties.apikey);
		String response = httpService.doCallGet("v2/countries", params);

		Countries countries = getObjectMapper().readValue(response, Countries.class);
		for ( Countries.Country country : countries.getCountries() )
		{
			fetchAirportForCountry(httpService, country, jayrideProperties.apikey);	
		}
	}
	
	protected void fetchAirportForCountry(HttpService httpService, Countries.Country country, String apiKey) throws Exception
	{
		log.debug("fetchAirportForCountry::entering for " + country.getCode());
		Map<String, String> params = new HashMap<>();
		params.put("key", apiKey);
		params.put("country_code", country.getCode());

		String response = httpService.doCallGet("v2/airport-terminals", params);

		int airportCount = 0;
		int terminalCount = 0;
		Airports airports = getObjectMapper().readValue(response, Airports.class);
		for ( Airports.Airport airport : airports.getAirports() )
		{
			AirportData airportData = new AirportData();
			airportData.setIata(airport.getIata());
			airportData.setAirportName(airport.getName());
			airportData.setCountryCode(country.getCode());
			airportRepo.save(airportData);
			airportCount++;
			for ( Terminal terminal : airport.getTerminals() )
			{
				AirportTerminalData airportTerminalData = new AirportTerminalData();
				airportTerminalData.setIata(airport.getIata());
				airportTerminalData.setId(terminal.getId());
				airportTerminalData.setTerminalName(terminal.getName());
				String fullAirportTerminalName = airport.getName();
				if ( terminal.getName() != null && !terminal.getName().equals("Terminal"))
				{
					fullAirportTerminalName = airport.getName() + " (" + airport.getIata() + "), "  + terminal.getName();
				}
				airportTerminalData.setFullAirportTerminalName(fullAirportTerminalName);
				airportTerminalData.setCountryCode(country.getCode());
				airportTerminalData.setLatitude(terminal.getLatitude());
				airportTerminalData.setLongitude(terminal.getLongitude());
				airportTerminalRepo.save(airportTerminalData);
				terminalCount++;
			}
		}
		log.debug("fetchAirportForCountry::saved " + airportCount + " airports, " + terminalCount + " terminals");
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

	private ObjectMapper _objectMapper = null;

}
