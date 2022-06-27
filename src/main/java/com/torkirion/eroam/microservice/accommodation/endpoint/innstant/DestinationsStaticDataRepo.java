package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DestinationsStaticDataRepo extends JpaRepository<DestinationsStaticData, String> {
    @Query("SELECT u.id FROM DestinationsStaticData u WHERE u.lat < :latNorthwest AND u.lat > :latSoutheast AND u.lon > :lonNorthwest AND u.lon < :lonSoutheast")
    List<String> findDestinationIdByLatLon(BigDecimal latNorthwest, BigDecimal lonNorthwest, BigDecimal latSoutheast, BigDecimal lonSoutheast);
}
