package com.torkirion.eroam.ims.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;

public interface IMSAccommodationRCDataRepo extends JpaRepository<IMSAccommodationRCData, String>
{
	@Query("SELECT DISTINCT category FROM IMSAccommodationRCData")
	List<String> findDistinctCategories();
	
	@Query("FROM IMSAccommodationRCData a WHERE longitude > ?2 and longitude < ?4 and latitude < ?1 and latitude > ?3")
	List<IMSAccommodationRCData> findByGeobox(BigDecimal latitudeNW, BigDecimal longitudeNW, BigDecimal latitudeSE, BigDecimal longitudeSE);

}