package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface EstablishmentFacilityRepo extends JpaRepository<EstablishmentFacilityData, String>
{
    List<EstablishmentFacilityData> findByEstablishmentId(Integer establishmentId);
}