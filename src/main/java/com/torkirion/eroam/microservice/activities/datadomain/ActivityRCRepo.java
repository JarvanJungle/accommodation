package com.torkirion.eroam.microservice.activities.datadomain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Never use this outside of the ActivityRCService. Since the service will look after cache refreshing etc
 * 
 * @author jadigby
 *
 */
public interface ActivityRCRepo extends JpaRepository<ActivityRCData, String>
{
	@Query("FROM ActivityRCData a WHERE longitude > ?2 and longitude < ?4 and latitude < ?1 and latitude > ?3 and channel = ?5")
	List<ActivityRCData> findByGeoboxAndChannel(BigDecimal latitudeNW, BigDecimal longitudeNW, BigDecimal latitudeSE, BigDecimal longitudeSE, String channel);

	List<ActivityRCData> findByChannelAndChannelCode(String channel, String channelCode);

	List<ActivityRCData> findAllByCodeIn(List<String> codes);

}