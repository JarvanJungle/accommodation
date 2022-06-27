package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.io.*;
import java.math.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.sql.*;
import java.time.LocalDate;

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
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.AccommodationTypeTag;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.FacilityGroup;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.ImageTag;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;
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

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class YalagoRCLoader
{
	@Autowired
	private BoardTypeInclusionRepo boardTypeInclusionsRepo;

	@Autowired
	private SupplierBoardTypeRepo supplierbBoardTypeRepo;

	@Autowired
	private YalagoCountryRepo countryRepo;

	@Autowired
	private ProvinceRepo provinceRepo;

	@Autowired
	private LocationRepo locationRepo;

	@Autowired
	private ErrataCategoryRepo errataCategoryRepo;

	@Autowired
	private EstablishmentExtraOptionRepo establishmentExtraOptionRepo;

	@Autowired
	private EstablishmentExtraRepo establishmentExtraRepo;

	@Autowired
	private SupplierBoardTypeRepo supplierBoardTypeRepo;

	@Autowired
	private FacilityRepo facilityRepo;

	@Autowired
	private EstablishmentRoomTypeRepo establishmentRoomTypeRepo;

	@Autowired
	private EstablishmentImageRepo establishmentImageRepo;

	@Autowired
	private EstablishmentFacilityRepo establishmentFacilityRepo;

	@Autowired
	private EstablishmentRepo establishmentRepo;

	@Autowired
	private EstablishmentTextRepo establishmentTextRepo;

	@Autowired
	private AccommodationRCService accommodationRCService;

	@Autowired
	private YalagoRCUpdater yalagoRCUpdater;
	private static final String STATIC_URL = "http://yalago.s3-eu-west-1.amazonaws.com/InventoryV4json.zip";
	// private static final String STATIC_URL = "http://yalago.s3-eu-west-1.amazonaws.com/TestInventoryV4json.zip";

	@Transactional
	public void fetchLookupData()
	{
		log.debug("fetchLookupData::entering");
		final boolean downloadData = false;

		try
		{
			// String dirPrefix;
			String dirPrefix = "/Users/jadigby/temp/eroam";
			//String dirPrefix = "/tmp/yalago";
			if (downloadData)
			{
				Path tempDir = Files.createTempDirectory("yalago");
				log.debug("fetchLookupData::created temp dir " + tempDir);
				Path tempFile = Files.createTempFile(tempDir, "yalago", ".zip");
				log.debug("fetchLookupData::created temp file " + tempFile + ". Copying...");
				FileUtils.copyURLToFile(new java.net.URL(STATIC_URL), tempFile.toFile(), 30 * 1000, 30 * 1000);
				log.debug("fetchLookupData::copied URL");
				unzip(tempFile.toFile().getAbsolutePath(), tempDir.toFile());
				dirPrefix = tempDir.toFile().getAbsolutePath();
				log.debug("fetchLookupData::entering");
			}

			//loadBoardTypeInclusion(dirPrefix + "/BoardTypeInclusions_enV4.json");
			//Functions.logMemAndYield();
			//loadCountry(dirPrefix + "/CountriesV4.json");
			//Functions.logMemAndYield();
			//loadErrataCategory(dirPrefix + "/ErrataCategories_enV4.json");
			//Functions.logMemAndYield();
			//loadEstablishmentExtra(dirPrefix + "/EstablishmentExtras_enV4.json");
			//Functions.logMemAndYield();
			//loadEstablishmentExtraOption(dirPrefix + "/eroam/EstablishmentExtraOptions_enV4.json");
			//Functions.logMemAndYield();
			//loadSupplierBoardType(dirPrefix + "/SupplierBoardTypes_enV4.json");
			//Functions.logMemAndYield();
			//loadFacility(dirPrefix + "/Facilities_enV4.json");
			//Functions.logMemAndYield();
			//loadEstablishmentRoomType(dirPrefix + "/EstablishmentRoomTypes_enV4.json");
			//Functions.logMemAndYield();
			//loadEstablishmentImage(dirPrefix + "/EstablishmentImagesV4.json");
			//Functions.logMemAndYield();
			//loadEstablishmentFacility(dirPrefix + "/EstablishmentFacilities_enV4.json");
			//Functions.logMemAndYield();
			//loadEstablishment(dirPrefix + "/EstablishmentsV4.json");
			//Functions.logMemAndYield();
			//loadEstablishmentText(dirPrefix + "/EstablishmentText_enV4.json");
			//Functions.logMemAndYield();
		}
		catch (Exception e)
		{
			log.warn("fetchLookupData::exception " + e.toString(), e);
		}

	}

	private final static int PAGESIZE = 100;
	public int convertToRC(String code) throws Exception
	{
		log.debug("convertToRC::enter");

		int processCount = 0;
		try
		{
			LocalDate now = LocalDate.now();
			Pageable pageable = PageRequest.of(0, PAGESIZE);
			Slice<EstablishmentData> slice = null;
			YalagoCache yalagoRCLoaderCache = new YalagoCache(facilityRepo, countryRepo, provinceRepo, locationRepo, boardTypeInclusionsRepo, supplierBoardTypeRepo);
			do
			{
				log.debug("convertToRC::finding " + pageable);
				if ( code == null )
					slice = establishmentRepo.findByLastUpdateLessThan(now, pageable);
				else
					slice = establishmentRepo.findByEstablishmentId(Integer.parseInt(code), pageable);
				log.debug("convertToRC::slice.number" + slice.getNumber() + ", size=" + slice.getSize() + ", hasNext=" + slice.hasNext());
				List<EstablishmentData> establishmentDataList = slice.getContent();
				log.debug("convertToRC::processing " + establishmentDataList.size() + " records, processCount=" + processCount);
				
				yalagoRCUpdater.convertListToRC(now, establishmentDataList, yalagoRCLoaderCache, establishmentRepo);
				processCount += establishmentDataList.size();
				Functions.logMemAndYield();
				pageable = slice.nextPageable();
			}
			while (slice.hasNext() && slice.getSize() == PAGESIZE);
		}
		catch (Exception e)
		{
			log.warn("convertToRC::caught " + e.toString(), e);
		}
		log.debug("convertToRC::processed " + processCount + " establishments");
		return processCount;
	}

	private String makeRatingText(Integer rating, Integer ratingTypeId)
	{
		// TODO should use ratingTypeId to make the name
		return rating + " stars";
	}

	private void loadBoardTypeInclusion(String filename)
	{
		log.debug("loadBoardTypeInclusion::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		try
		{
			List<BoardTypeInclusion> boardTypeInclusions = getObjectMapper().readValue(new File(filename), new TypeReference<List<BoardTypeInclusion>>()
			{});
			for (BoardTypeInclusion boardTypeInclusion : boardTypeInclusions)
			{
				readCount++;
				log.debug("loadBoardTypeInclusion::processing " + boardTypeInclusion.toString());
				Optional<BoardTypeInclusionData> boardTypeInclusionsDataOpt = boardTypeInclusionsRepo.findById(boardTypeInclusion.getBoardTypeInclusionId());
				BoardTypeInclusionData boardTypeInclusionData = null;
				if (!boardTypeInclusionsDataOpt.isPresent())
				{
					log.debug("loadBoardTypeInclusion::creating " + boardTypeInclusion.getBoardTypeInclusionId());
					boardTypeInclusionData = new BoardTypeInclusionData();
					boardTypeInclusionData.setBoardTypeInclusionId(boardTypeInclusion.getBoardTypeInclusionId());
				}
				else
				{
					boardTypeInclusionData = boardTypeInclusionsDataOpt.get();
				}
				if (!boardTypeInclusion.getTitle().equals(boardTypeInclusionData.getTitle()))
				{
					log.debug("loadBoardTypeInclusion::updating");
					boardTypeInclusionData.setTitle(boardTypeInclusion.getTitle());
					boardTypeInclusionsRepo.save(boardTypeInclusionData);
					updateCount++;
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadBoardTypeInclusion::caught " + e.toString(), e);
		}
		log.debug("loadBoardTypeInclusion::read " + readCount + ", updated " + updateCount);
	}

	private void loadCountry(String filename)
	{
		log.debug("loadCountry::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		try
		{
			List<Country> countries = getObjectMapper().readValue(new File(filename), new TypeReference<List<Country>>()
			{});
			for (Country country : countries)
			{
				readCount++;
				log.debug("loadCountry::processing " + country.toString());
				Optional<CountryData> countryDataOpt = countryRepo.findById(country.getCountryId());
				CountryData countryData = null;
				if (!countryDataOpt.isPresent())
				{
					log.debug("loadCountry::creating " + country.getCountryCode());
					countryData = new CountryData();
					countryData.setCountryId(country.getCountryId());
					countryData.setCountryCode(country.getCountryCode());
				}
				else
				{
					countryData = countryDataOpt.get();
				}
				if (!country.getTitle().equals(countryData.getTitle()))
				{
					log.debug("loadCountry::updating country");
					countryData.setTitle(country.getTitle());
					countryRepo.save(countryData);
					updateCount++;
				}
				for (Province province : country.getProvinces())
				{
					log.debug("loadCountry::processing province " + province.toString());
					Optional<ProvinceData> provinceDataOpt = provinceRepo.findById(province.getProvinceId());
					ProvinceData provinceData = null;
					if (!provinceDataOpt.isPresent())
					{
						log.debug("loadCountry::creating province " + province.getProvinceId());
						provinceData = new ProvinceData();
						provinceData.setProvinceId(province.getProvinceId());
					}
					else
					{
						provinceData = provinceDataOpt.get();
					}
					if (!province.getTitle().equals(provinceData.getTitle()))
					{
						log.debug("loadCountry::updating province");
						provinceData.setTitle(province.getTitle());
						provinceData.setCountryId(country.getCountryId());
						provinceRepo.save(provinceData);
					}
					for (Location location : province.getLocations())
					{
						log.debug("loadCountry::processing location " + location.toString());
						Optional<LocationData> locationDataOpt = locationRepo.findById(location.getLocationId());
						LocationData locationData = null;
						if (!locationDataOpt.isPresent())
						{
							log.debug("loadCountry::creating location " + location.getLocationId());
							locationData = new LocationData();
							locationData.setLocationId(location.getLocationId());
						}
						else
						{
							locationData = locationDataOpt.get();
						}
						if (!location.getTitle().equals(locationData.getTitle()))
						{
							log.debug("loadCountry::updating location");
							locationData.setTitle(location.getTitle());
							locationData.setProvinceId(province.getProvinceId());
							locationRepo.save(locationData);
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadCountry::caught " + e.toString(), e);
		}
		log.debug("loadCountry::read " + readCount + ", updated " + updateCount);
	}

	private void loadErrataCategory(String filename)
	{
		log.debug("loadErrataCategory::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		try
		{
			List<ErrataCategory> errataCategories = getObjectMapper().readValue(new File(filename), new TypeReference<List<ErrataCategory>>()
			{});
			for (ErrataCategory errataCategory : errataCategories)
			{
				readCount++;
				log.debug("loadErrataCategory::processing " + errataCategory.toString());
				Optional<ErrataCategoryData> errataCategoryDataOpt = errataCategoryRepo.findById(errataCategory.getErrataCategoryId());
				ErrataCategoryData errataCategoryData = null;
				if (!errataCategoryDataOpt.isPresent())
				{
					log.debug("loadErrataCategory::creating " + errataCategory.getErrataCategoryId());
					errataCategoryData = new ErrataCategoryData();
					errataCategoryData.setErrataCategoryId(errataCategory.getErrataCategoryId());
				}
				else
				{
					errataCategoryData = errataCategoryDataOpt.get();
				}
				if (!errataCategory.getTitle().equals(errataCategoryData.getTitle()) || !errataCategory.getDefinition().equals(errataCategoryData.getDefinition()))
				{
					log.debug("loadErrataCategory::updating");
					errataCategoryData.setTitle(errataCategory.getTitle());
					errataCategoryData.setDefinition(errataCategory.getDefinition());
					errataCategoryRepo.save(errataCategoryData);
					updateCount++;
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadErrataCategory::caught " + e.toString(), e);
		}
		log.debug("loadErrataCategory::read " + readCount + ", updated " + updateCount);
	}

	private void loadEstablishmentExtraOption(String filename)
	{
		log.debug("loadEstablishmentExtraOption::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		try
		{
			List<EstablishmentExtraOption> establishmentExtraOptions = getObjectMapper().readValue(new File(filename), new TypeReference<List<EstablishmentExtraOption>>()
			{});
			for (EstablishmentExtraOption establishmentExtraOption : establishmentExtraOptions)
			{
				readCount++;
				log.debug("loadEstablishmentExtraOption::processing " + establishmentExtraOption.toString());
				Optional<EstablishmentExtraOptionData> establishmentExtraOptionDataOpt = establishmentExtraOptionRepo.findById(establishmentExtraOption.getMasterExtraOptionId());
				EstablishmentExtraOptionData establishmentExtraOptionData = null;
				if (!establishmentExtraOptionDataOpt.isPresent())
				{
					log.debug("loadEstablishmentExtraOption::creating " + establishmentExtraOption.getMasterExtraOptionId());
					establishmentExtraOptionData = new EstablishmentExtraOptionData();
					establishmentExtraOptionData.setMasterExtraOptionId(establishmentExtraOption.getMasterExtraOptionId());
				}
				else
				{
					establishmentExtraOptionData = establishmentExtraOptionDataOpt.get();
				}
				if (!establishmentExtraOption.getTitle().equals(establishmentExtraOptionData.getTitle()))
				{
					log.debug("loadEstablishmentExtraOption::updating " + establishmentExtraOptionData + " to " + establishmentExtraOption);
					establishmentExtraOptionData.setMasterExtraId(establishmentExtraOption.getMasterExtraId());
					establishmentExtraOptionData.setTitle(establishmentExtraOption.getTitle());
					establishmentExtraOptionData.setSortOrder(establishmentExtraOption.getSortOrder());
					establishmentExtraOptionRepo.save(establishmentExtraOptionData);
					updateCount++;
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadEstablishmentExtraOption::caught " + e.toString(), e);
		}
		log.debug("loadEstablishmentExtraOption::read " + readCount + ", updated " + updateCount);
	}

	private void loadEstablishmentExtra(String filename)
	{
		log.debug("loadEstablishmentExtra::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		try
		{
			List<EstablishmentExtra> establishmentExtras = getObjectMapper().readValue(new File(filename), new TypeReference<List<EstablishmentExtra>>()
			{});
			for (EstablishmentExtra establishmentExtra : establishmentExtras)
			{
				readCount++;
				log.debug("loadEstablishmentExtra::processing " + establishmentExtra.toString());
				Optional<EstablishmentExtraData> establishmentExtraDataOpt = establishmentExtraRepo.findById(establishmentExtra.getMasterExtraId());
				EstablishmentExtraData establishmentExtraData = null;
				if (!establishmentExtraDataOpt.isPresent())
				{
					log.debug("loadEstablishmentExtra::creating " + establishmentExtra.getMasterExtraId());
					establishmentExtraData = new EstablishmentExtraData();
					establishmentExtraData.setMasterExtraId(establishmentExtra.getMasterExtraId());
				}
				else
				{
					establishmentExtraData = establishmentExtraDataOpt.get();
				}
				if (!establishmentExtra.getTitle().equals(establishmentExtraData.getTitle()))
				{
					log.debug("loadEstablishmentExtra::updating");
					establishmentExtraData.setTitle(establishmentExtra.getTitle());
					establishmentExtraData.setType(establishmentExtra.getType());
					establishmentExtraData.setEstablishmentId(establishmentExtra.getEstablishmentId());
					establishmentExtraRepo.save(establishmentExtraData);
					updateCount++;
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadEstablishmentExtra::caught " + e.toString(), e);
		}
		log.debug("loadEstablishmentExtra::read " + readCount + ", updated " + updateCount);
	}

	private void loadFacility(String filename)
	{
		log.debug("loadFacility::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		try
		{
			List<Facility> facilities = getObjectMapper().readValue(new File(filename), new TypeReference<List<Facility>>()
			{});
			for (Facility facility : facilities)
			{
				readCount++;
				log.debug("loadFacility::processing " + facility.toString());
				Optional<FacilityData> facilityDataOpt = facilityRepo.findById(facility.getFacilityId());
				FacilityData facilityData = null;
				if (!facilityDataOpt.isPresent())
				{
					log.debug("loadFacility::creating " + facility.getFacilityId());
					facilityData = new FacilityData();
					facilityData.setFacilityId(facility.getFacilityId());
				}
				else
				{
					facilityData = facilityDataOpt.get();
				}
				if (!facility.getTitle().equals(facilityData.getTitle()))
				{
					log.debug("loadFacility::updating");
					facilityData.setTitle(facility.getTitle());
					facilityData.setFacilityGroup(facility.getFacilityGroup());
					facilityData.setFacilityType(facility.getFacilityType());
					facilityRepo.save(facilityData);
					updateCount++;
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadFacility::caught " + e.toString(), e);
		}
		log.debug("loadFacility::read " + readCount + ", updated " + updateCount);
	}

	private void loadSupplierBoardType(String filename)
	{
		log.debug("loadSupplierBoardType::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		try
		{
			List<SupplierBoardType> supplierBoardTypes = getObjectMapper().readValue(new File(filename), new TypeReference<List<SupplierBoardType>>()
			{});
			for (SupplierBoardType supplierBoardType : supplierBoardTypes)
			{
				readCount++;
				log.debug("loadSupplierBoardType::processing " + supplierBoardType.toString());
				Optional<SupplierBoardTypeData> supplierBoardTypeDataOpt = supplierBoardTypeRepo.findById(supplierBoardType.getSupplierBoardTypeId());
				SupplierBoardTypeData supplierBoardTypeData = null;
				if (!supplierBoardTypeDataOpt.isPresent())
				{
					log.debug("loadSupplierBoardType::creating " + supplierBoardType.getSupplierBoardTypeId());
					supplierBoardTypeData = new SupplierBoardTypeData();
					supplierBoardTypeData.setSupplierBoardTypeId(supplierBoardType.getSupplierBoardTypeId());
				}
				else
				{
					supplierBoardTypeData = supplierBoardTypeDataOpt.get();
				}
				if (!supplierBoardType.getTitle().equals(supplierBoardTypeData.getTitle()))
				{
					log.debug("loadSupplierBoardType::updating");
					supplierBoardTypeData.setTitle(supplierBoardType.getTitle());
					supplierBoardTypeRepo.save(supplierBoardTypeData);
					updateCount++;
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadSupplierBoardType::caught " + e.toString(), e);
		}
		log.debug("loadSupplierBoardType::read " + readCount + ", updated " + updateCount);
	}

	private void loadEstablishmentRoomType(String filename)
	{
		log.debug("loadEstablishmentRoomType::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		Set<String> roomCodes = new HashSet<>();
		try
		{
			List<EstablishmentRoomType> establishmentRoomTypes = getObjectMapper().readValue(new File(filename), new TypeReference<List<EstablishmentRoomType>>()
			{});
			for (EstablishmentRoomType establishmentRoomType : establishmentRoomTypes)
			{
				readCount++;
				log.debug("loadEstablishmentRoomType::processing " + establishmentRoomType.toString());
				if (roomCodes.contains(establishmentRoomType.getRoomCode()))
				{
					// Yalago roomType data seems to contain exact duplicates! Just flagging...
					log.debug("loadEstablishmentRoomType::processing duplicate roomCode " + establishmentRoomType.toString());
				}
				roomCodes.add(establishmentRoomType.getRoomCode());

				Optional<EstablishmentRoomTypeData> establishmentRoomTypeDataOpt = establishmentRoomTypeRepo.findById(establishmentRoomType.getRoomCode());
				EstablishmentRoomTypeData establishmentRoomTypeData = null;
				if (!establishmentRoomTypeDataOpt.isPresent())
				{
					log.debug("loadEstablishmentRoomType::creating " + establishmentRoomType.getRoomCode());
					establishmentRoomTypeData = new EstablishmentRoomTypeData();
					establishmentRoomTypeData.setRoomCode(establishmentRoomType.getRoomCode());
				}
				else
				{
					establishmentRoomTypeData = establishmentRoomTypeDataOpt.get();
				}
				if (!establishmentRoomType.getTitle().equals(establishmentRoomTypeData.getTitle()) || !establishmentRoomType.getEstablishmentId().equals(establishmentRoomTypeData.getEstablishmentId())
						|| !establishmentRoomType.getDescription().equals(establishmentRoomTypeData.getDescription())
						|| !establishmentRoomType.getImageId().equals(establishmentRoomTypeData.getImageId()) || !establishmentRoomType.getImageUrl().equals(establishmentRoomTypeData.getImageUrl()))
				{
					log.debug("loadEstablishmentRoomType::updating " + establishmentRoomTypeData + " to " + establishmentRoomType);
					establishmentRoomTypeData.setTitle(establishmentRoomType.getTitle());
					establishmentRoomTypeData.setEstablishmentId(establishmentRoomType.getEstablishmentId());
					establishmentRoomTypeData.setDescription(establishmentRoomType.getDescription());
					establishmentRoomTypeData.setImageId(establishmentRoomType.getImageId());
					establishmentRoomTypeData.setImageUrl(establishmentRoomType.getImageUrl());
					establishmentRoomTypeRepo.save(establishmentRoomTypeData);
					updateCount++;
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadEstablishmentRoomType::caught " + e.toString(), e);
		}
		log.debug("loadEstablishmentRoomType::read " + readCount + ", updated " + updateCount);
	}

	private void loadEstablishmentImage(String filename)
	{
		log.debug("loadEstablishmentImage::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		Set<String> imageIDs = new HashSet<>();
		try
		{
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filename));
			try (JsonParser jsonParser = getObjectMapper().getFactory().createParser(bufferedInputStream))
			{
				// Check the first token
				if (jsonParser.nextToken() != JsonToken.START_ARRAY)
				{
					throw new IllegalStateException("Expected content to be an array");
				}

				// Iterate over the tokens until the end of the array
				while (jsonParser.nextToken() != JsonToken.END_ARRAY)
				{
					readCount++;
					// Read a contact instance using ObjectMapper and do something with it
					EstablishmentImage establishmentImage = getObjectMapper().readValue(jsonParser, EstablishmentImage.class);
					if (processEstablishmentImage(establishmentImage, imageIDs))
						updateCount++;
					if (readCount % 10000 == 0)
						log.debug("loadEstablishmentImage::processing item " + readCount);
					if (readCount % 100000 == 0)
						Functions.logMemAndYield();
				}
			}
			// List<EstablishmentImage> establishmentImages = getObjectMapper().readValue(new File(filename), new
			// TypeReference<List<EstablishmentImage>>()
			// {});
			// log.debug("loadEstablishmentImage::about to process " + establishmentImages.size() + " entries");
			// for (EstablishmentImage establishmentImage : establishmentImages)
			// {
			// readCount++;
			// processEstablishmentImage(establishmentImage, imageIDs);
			// if (readCount % 10000 == 0)
			// log.debug("loadEstablishmentImage::processing item " + readCount);
			// if (readCount % 100000 == 0)
			// Functions.logMemAndYield();
			// }
		}
		catch (IOException e)
		{
			log.warn("loadEstablishmentImage::caught " + e.toString(), e);
		}
		log.debug("loadEstablishmentImage::read " + readCount + ", updated " + updateCount);
	}

	private boolean processEstablishmentImage(EstablishmentImage establishmentImage, Set<String> imageIDs)
	{
		if (imageIDs.contains(establishmentImage.getImageId()))
		{
			// Yalago roomType data seems to contain exact duplicates! Just flagging...
			// log.debug("loadEstablishmentImage::processing duplicate imageId " + establishmentImage.getImageId());
		}
		imageIDs.add(establishmentImage.getImageId());

		Optional<EstablishmentImageData> establishmentImageDataOpt = establishmentImageRepo.findById(establishmentImage.getImageId());
		EstablishmentImageData establishmentImageData = null;
		if (!establishmentImageDataOpt.isPresent())
		{
			// log.debug("loadEstablishmentImage::creating " + establishmentImage.getImageId());
			establishmentImageData = new EstablishmentImageData();
			establishmentImageData.setImageId(establishmentImage.getImageId());
		}
		else
		{
			establishmentImageData = establishmentImageDataOpt.get();
		}
		if (!establishmentImage.getEstablishmentId().equals(establishmentImageData.getEstablishmentId()) || !establishmentImage.getUrl().equals(establishmentImageData.getUrl()))
		{
			// log.debug("loadEstablishmentImage::updating " + establishmentImageData + " to " + establishmentImage);
			establishmentImageData.setEstablishmentId(establishmentImage.getEstablishmentId());
			establishmentImageData.setUrl(establishmentImage.getUrl());
			establishmentImageRepo.save(establishmentImageData);
			return true;
		}
		return false;
	}

	private void loadEstablishmentFacility(String filename)
	{
		log.debug("loadEstablishmentFacility::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		Set<String> keys = new HashSet<>();
		try
		{
			List<EstablishmentFacility> establishmentFacilities = getObjectMapper().readValue(new File(filename), new TypeReference<List<EstablishmentFacility>>()
			{});
			log.debug("loadEstablishmentFacility::about to process " + establishmentFacilities.size() + " entries");
			for (EstablishmentFacility establishmentFacility : establishmentFacilities)
			{
				String key = establishmentFacility.getEstablishmentId() + "-" + establishmentFacility.getFacilityId();
				readCount++;
				log.debug("loadEstablishmentFacility::processing item " + readCount + " : " + establishmentFacility.toString());
				if (keys.contains(key))
				{
					// Yalago facilities data seems to contain exact duplicates! Just flagging...
					log.debug("loadEstablishmentFacility::processing duplicate key " + key);
				}
				keys.add(key);

				Optional<EstablishmentFacilityData> establishmentFacilityDataOpt = establishmentFacilityRepo.findById(key);
				EstablishmentFacilityData establishmentFacilityData = null;
				if (!establishmentFacilityDataOpt.isPresent())
				{
					log.debug("loadEstablishmentFacility::creating " + key);
					establishmentFacilityData = new EstablishmentFacilityData();
					establishmentFacilityData.setEstablishmentId_facilityId(key);
				}
				else
				{
					establishmentFacilityData = establishmentFacilityDataOpt.get();
				}
				log.debug("loadEstablishmentFacility::updating " + establishmentFacilityData + " to " + establishmentFacility);
				establishmentFacilityData.setEstablishmentId(establishmentFacility.getEstablishmentId());
				establishmentFacilityData.setFacilityId(establishmentFacility.getFacilityId());
				establishmentFacilityData.setDescription(establishmentFacility.getDescription());
				establishmentFacilityRepo.save(establishmentFacilityData);
				updateCount++;
			}
		}
		catch (IOException e)
		{
			log.warn("loadEstablishmentFacility::caught " + e.toString(), e);
		}
		log.debug("loadEstablishmentFacility::read " + readCount + ", updated " + updateCount);
	}

	private void loadEstablishment(String filename)
	{
		log.debug("loadEstablishment::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;

		try
		{
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filename));
			try (JsonParser jsonParser = getObjectMapper().getFactory().createParser(bufferedInputStream))
			{
				// Check the first token
				if (jsonParser.nextToken() != JsonToken.START_ARRAY)
				{
					throw new IllegalStateException("Expected content to be an array");
				}

				// Iterate over the tokens until the end of the array
				while (jsonParser.nextToken() != JsonToken.END_ARRAY)
				{
					readCount++;
					Establishment establishment = getObjectMapper().readValue(jsonParser, Establishment.class);
					if (processEstablishment(establishment))
						updateCount++;
					if (readCount % 10000 == 0)
						log.debug("loadEstablishment::processing item " + readCount);
					if (readCount % 100000 == 0)
						Functions.logMemAndYield();
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadEstablishment::caught " + e.toString(), e);
		}
		log.debug("loadEstablishment::read " + readCount + ", updated " + updateCount);
	}

	private boolean processEstablishment(Establishment establishment)
	{
		Optional<EstablishmentData> establishmentDataOpt = establishmentRepo.findById(establishment.getEstablishmentId());
		EstablishmentData establishmentData = null;
		if (!establishmentDataOpt.isPresent())
		{
			log.debug("loadEstablishment::creating " + establishment.getEstablishmentId());
			establishmentData = new EstablishmentData();
			establishmentData.setEstablishmentId(establishment.getEstablishmentId());
		}
		else
		{
			establishmentData = establishmentDataOpt.get();
		}
		if (!establishment.getEstablishmentTitle().equals(establishmentData.getEstablishmentTitle()) || !establishment.getAcommodationTypeId().equals(establishmentData.getAcommodationTypeId())
				|| !establishment.getAddress().equals(establishmentData.getAddress()) || !establishment.getPostalCode().equals(establishmentData.getPostalCode())
				|| !establishment.getEmail().equals(establishmentData.getEmail()) || !establishment.getFaxNumber().equals(establishmentData.getFaxNumber())
				|| !establishment.getGeocodeAccuracy().equals(establishmentData.getGeocodeAccuracy()) || !establishment.getLatitude().equals(establishmentData.getLatitude())
				|| !establishment.getLongitude().equals(establishmentData.getLongitude()))
		{
			log.debug("loadEstablishment::updating? " + establishmentData + " to " + establishment);
			establishmentData.setEstablishmentId(establishment.getEstablishmentId());
			establishmentData.setEstablishmentTitle(establishment.getEstablishmentTitle());
			establishmentData.setAcommodationTypeId(establishment.getAcommodationTypeId());
			establishmentData.setAddress(establishment.getAddress());
			establishmentData.setPostalCode(establishment.getPostalCode());
			establishmentData.setEmail(establishment.getEmail());
			establishmentData.setFaxNumber(establishment.getFaxNumber());
			establishmentData.setGeocodeAccuracy(establishment.getGeocodeAccuracy());
			establishmentData.setLatitude(establishment.getLatitude());
			establishmentData.setLongitude(establishment.getLongitude());
			establishmentData.setLocationId(establishment.getLocationId());
			establishmentData.setPhoneNumber(establishment.getPhoneNumber());
			establishmentData.setRating(establishment.getRating());
			establishmentData.setRatingTypeId(establishment.getRatingTypeId());
			establishmentRepo.save(establishmentData);
			return true;
		}
		return false;
	}

	private void loadEstablishmentText(String filename)
	{
		log.debug("loadEstablishmentText::entering on " + filename);
		int updateCount = 0;
		int readCount = 0;
		try
		{
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filename));
			try (JsonParser jsonParser = getObjectMapper().getFactory().createParser(bufferedInputStream))
			{
				// Check the first token
				if (jsonParser.nextToken() != JsonToken.START_ARRAY)
				{
					throw new IllegalStateException("Expected content to be an array");
				}

				// Iterate over the tokens until the end of the array
				while (jsonParser.nextToken() != JsonToken.END_ARRAY)
				{
					readCount++;
					EstablishmentText establishmentText = getObjectMapper().readValue(jsonParser, EstablishmentText.class);
					if (processEstablishmentText(establishmentText))
					{
						updateCount++;
					}
					if (readCount % 10000 == 0)
						log.debug("loadEstablishmentText::processing item " + readCount);
					if (readCount % 100000 == 0)
						Functions.logMemAndYield();
				}
			}
		}
		catch (IOException e)
		{
			log.warn("loadEstablishmentText::caught " + e.toString(), e);
		}
		log.debug("loadEstablishmentText::read " + readCount + ", updated " + updateCount);
	}

	private boolean processEstablishmentText(EstablishmentText establishmentText)
	{
		Optional<EstablishmentTextData> establishmentTextDataOpt = establishmentTextRepo.findById(establishmentText.getEstablishmentId());
		EstablishmentTextData establishmentTextData = null;
		if (!establishmentTextDataOpt.isPresent())
		{
			log.debug("loadEstablishmentText::creating " + establishmentText.getEstablishmentId());
			establishmentTextData = new EstablishmentTextData();
			establishmentTextData.setEstablishmentId(establishmentText.getEstablishmentId());
		}
		else
		{
			establishmentTextData = establishmentTextDataOpt.get();
		}
		if (!establishmentText.getSummary().equals(establishmentTextData.getSummary()) || !establishmentText.getDescription().equals(establishmentTextData.getDescription()))
		{
			log.debug("loadEstablishmentText::updating " + establishmentTextData + " to " + establishmentText);
			establishmentTextData.setSummary(establishmentText.getSummary());
			establishmentTextData.setDescription(establishmentText.getDescription());
			establishmentTextRepo.save(establishmentTextData);
			return true;
		}
		return false;
	}

	private void unzip(String fileZip, File destDir) throws Exception
	{
		log.debug("unzip::unzipping " + fileZip + " to " + destDir.toString());
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null)
		{
			File newFile = newFile(destDir, zipEntry);
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0)
			{
				fos.write(buffer, 0, len);
			}
			fos.close();
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}

	private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException
	{
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator))
		{
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
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
