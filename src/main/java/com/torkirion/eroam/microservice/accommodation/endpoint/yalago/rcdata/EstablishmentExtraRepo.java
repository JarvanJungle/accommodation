package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface EstablishmentExtraRepo extends JpaRepository<EstablishmentExtraData, Integer>
{
    List<EstablishmentExtraData> findByEstablishmentId(Integer establishmentId);
}