package com.torkirion.eroam.microservice.accommodation.datadomain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Never use this outside of the AccommodationRCService. Since the service will look after cache refreshing etc
 * 
 * @author jadigby
 *
 */
public interface AccommodationRCRepo extends JpaRepository<AccommodationRCData, String>
{
	public interface CodeOnly
	{
		String getCode();
	}
	
	public interface CodeAndName
	{
		String getAccommodationName();
		String getCode();
	}
	
	List<AccommodationRCData> findByAddressCountryCode(String countryCode);

	@Query("FROM AccommodationRCData a WHERE longitude > ?2 and longitude < ?4 and latitude < ?1 and latitude > ?3")
	List<AccommodationRCData> findByGeobox(BigDecimal latitudeNW, BigDecimal longitudeNW, BigDecimal latitudeSE, BigDecimal longitudeSE);

	@Query("FROM AccommodationRCData a WHERE longitude > ?2 and longitude < ?4 and latitude < ?1 and latitude > ?3 and channel = ?5")
	List<AccommodationRCData> findByGeoboxAndChannel(BigDecimal latitudeNW, BigDecimal longitudeNW, BigDecimal latitudeSE, BigDecimal longitudeSE, String channel);

	List<AccommodationRCData> findByOleryCompanyCodeIsNull(Pageable page);

	@Query("FROM AccommodationRCData a WHERE a.address.countryCode = ?1 and oleryCompanyCode is null")
	List<AccommodationRCData> findByAddressCountryCodeAndOleryCompanyCodeIsNull(String countryCode, Pageable page);

	List<AccommodationRCData> findByOleryCompanyCode(Long oleryCompanyCode);

	@Query(value = " select rc2.code as code from accommodationrc rc1,  accommodationrc rc2 where rc1.olery_company_code = rc2.olery_company_code and rc1.olery_company_code is not null and rc1.olery_company_code > 0 and rc1.code = ?1", nativeQuery = true)
	List<CodeOnly> findDedupedCodesFromCode(String code);

	@Query(value = " select rc.code as code, rc.accommodation_name as accommodationName from accommodationrc rc", nativeQuery = true)
	Slice<CodeAndName> findCodeAndName(Pageable page);

	@Query(value = "SELECT u.channel_code FROM accommodationrc u WHERE u.latitude <= :latNorthwest AND u.latitude >= :latSoutheast AND u.longitude >= :lonNorthwest AND u.longitude <= :lonSoutheast AND u.channel = :channel", nativeQuery = true)
	List<String> findHotelCodeByGeoboxAndChannel(BigDecimal latNorthwest, BigDecimal latSoutheast, BigDecimal lonNorthwest, BigDecimal lonSoutheast, String channel);

}