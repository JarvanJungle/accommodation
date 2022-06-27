package com.torkirion.eroam.microservice.datadomain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AirlineRepo extends JpaRepository<Airline, String>
{
}