package com.torkirion.eroam.microservice.accommodation.endpoint.yalago;

import java.util.*;


import org.springframework.data.jpa.repository.JpaRepository;

import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.BoardTypeInclusionData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.BoardTypeInclusionRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.CountryData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.YalagoCountryRepo;

import lombok.extern.slf4j.Slf4j;

import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.LocationData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.LocationRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.FacilityData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.FacilityRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.ProvinceData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.ProvinceRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.SupplierBoardTypeData;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.SupplierBoardTypeRepo;

@Slf4j
public class YalagoCache
{
	public YalagoCache(FacilityRepo facilityRepo, YalagoCountryRepo countryRepo, ProvinceRepo provinceRepo, LocationRepo locationRepo, BoardTypeInclusionRepo boardTypeInclusionRepo, SupplierBoardTypeRepo supplierBoardTypeRepo)
	{
		log.info("intialisation::");
		countryCache = new Cache<CountryData, Integer, YalagoCountryRepo>(countryRepo);
		provinceCache = new Cache<ProvinceData, Integer, ProvinceRepo>(provinceRepo);
		locationCache = new Cache<LocationData, Integer, LocationRepo>(locationRepo);
		facilityCache = new Cache<FacilityData, Integer, FacilityRepo>(facilityRepo);
		boardTypeInclusionCache = new Cache<BoardTypeInclusionData, Integer, BoardTypeInclusionRepo>(boardTypeInclusionRepo);
		supplierBoardTypeCache = new Cache<SupplierBoardTypeData, Integer, SupplierBoardTypeRepo>(supplierBoardTypeRepo);
	}

	private static class Cache<DATA, KEY, REPO extends JpaRepository<DATA, KEY>>
	{
		private REPO repo;

		private Map<KEY, DATA> cache = new HashMap<>();

		public Cache(REPO repo)
		{
			this.repo = repo;
		}

		public DATA getCachedEntry(KEY id)
		{
			DATA data = cache.get(id);
			if (data != null)
				return data;

			Optional<DATA> opt = repo.findById(id);
			if (opt.isPresent())
			{
				data = opt.get();
				cache.put(id, data);
				return data;
			}
			else
				return null;
		}
	}

	private Cache<CountryData, Integer, YalagoCountryRepo> countryCache = null;

	private Cache<ProvinceData, Integer, ProvinceRepo> provinceCache = null;

	private Cache<LocationData, Integer, LocationRepo> locationCache = null;

	private Cache<FacilityData, Integer, FacilityRepo> facilityCache = null;

	private Cache<BoardTypeInclusionData, Integer, BoardTypeInclusionRepo> boardTypeInclusionCache = null;

	private Cache<SupplierBoardTypeData, Integer, SupplierBoardTypeRepo> supplierBoardTypeCache = null;

	public CountryData getCachedCountry(Integer countryId)
	{
		return countryCache.getCachedEntry(countryId);
	}

	public ProvinceData getCachedProvince(Integer provinceId)
	{
		return provinceCache.getCachedEntry(provinceId);
	}

	public LocationData getCachedLocation(Integer locationId)
	{
		return locationCache.getCachedEntry(locationId);
	}

	public FacilityData getCachedFacility(Integer facilityId)
	{
		return facilityCache.getCachedEntry(facilityId);
	}

	public BoardTypeInclusionData getCachedBoardTypeInclusion(Integer boardTypeInclusionId)
	{
		return boardTypeInclusionCache.getCachedEntry(boardTypeInclusionId);
	}

	public SupplierBoardTypeData getCachedSupplierBoardType(Integer supplierBoardTypeId)
	{
		return supplierBoardTypeCache.getCachedEntry(supplierBoardTypeId);
	}
}
