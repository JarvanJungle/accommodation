package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryStaticDataRepo extends JpaRepository<CountryStaticData, Integer> {
}
