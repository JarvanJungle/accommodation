package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AirportRepo extends JpaRepository<AirportData, String>
{
}