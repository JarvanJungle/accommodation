package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OleryPropertyRepo extends JpaRepository<OleryProperty, Long>
{
	List<OleryProperty> findByLastUpdatedIsNull(Pageable page);

	List<OleryProperty> findByCountryCodeAndLastUpdatedIsNull(String countryCode, Pageable page);

}