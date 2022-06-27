package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface DestinationRepo extends JpaRepository<DestinationData, Integer>
{
}