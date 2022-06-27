package com.torkirion.eroam.microservice.datadomain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Never use this outside of rhe AccommodationRCService.  Since the service will look after cache refreshing etc 
 * @author jadigby
 *
 */
public interface CountryRepo extends JpaRepository<Country, String>
{
}