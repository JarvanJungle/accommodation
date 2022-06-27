package com.torkirion.eroam.ims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSCountry;

/**
 * Never use this outside of rhe AccommodationRCService.  Since the service will look after cache refreshing etc 
 * @author jadigby
 *
 */
public interface IMSCountryRepo extends JpaRepository<IMSCountry, String>
{
}