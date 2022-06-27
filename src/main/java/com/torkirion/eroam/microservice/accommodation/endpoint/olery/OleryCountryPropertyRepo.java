package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.StaticData;

public interface OleryCountryPropertyRepo extends JpaRepository<OleryCountryProperty, Integer>
{
	List<OleryCountryProperty> findByCompanyIdAndCountryCode(Long companyId, String countryCode);
	
	List<OleryCountryProperty> findByCompanyId(Long companyId);
	
	Long deleteByCompanyId(Long companyId);
}