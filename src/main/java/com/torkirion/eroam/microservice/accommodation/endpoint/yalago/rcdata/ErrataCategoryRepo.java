package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ErrataCategoryRepo extends JpaRepository<ErrataCategoryData, Integer>
{
}