package com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface LoadRepo extends JpaRepository<LoadData, Integer>
{
}