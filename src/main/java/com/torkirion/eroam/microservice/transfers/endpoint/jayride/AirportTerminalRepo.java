package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;

public interface AirportTerminalRepo extends JpaRepository<AirportTerminalData, String>
{
	List<AirportTerminalData> findByIataAndTerminalNameContaining(String iata, String terminalName);
	List<AirportTerminalData> findByIata(String iata);
}