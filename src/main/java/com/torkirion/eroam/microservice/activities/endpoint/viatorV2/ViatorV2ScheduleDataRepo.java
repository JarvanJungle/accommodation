package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ViatorV2ScheduleDataRepo extends JpaRepository<ViatorV2ScheduleData, Long>
{
	List<ViatorV2ScheduleData> findByProductCode(String productCode);
	void deleteByProductCode(String productCode);
	List<ViatorV2ScheduleData> findAllByProductCodeIn(List<String> productCodes);

	@Query("FROM ViatorV2ScheduleData " +
			"where ageBand is not null " +
			"  and priceNet is not null " +
			"  and priceRrp is not null " +
			"  and startDate <= ?5 " +
			"  and endDate >= ?6 " +
			"  and longitude > ?2 " +
			"  and longitude < ?4 " +
			"  and latitude < ?1 " +
			"  and latitude > ?3 ")
	List<ViatorV2ScheduleData> findByGeoboxAndDate(BigDecimal latitudeNW, BigDecimal longitudeNW, BigDecimal latitudeSE, BigDecimal longitudeSE,
												   LocalDate startDate, LocalDate endDate);
}