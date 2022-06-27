package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CruiseDataRepo extends JpaRepository<CruiseData, CruiseId> {
    CruiseData findFirstByCodeToCruiseIdAndAndCountry(Integer cruiseId, String country);
}
