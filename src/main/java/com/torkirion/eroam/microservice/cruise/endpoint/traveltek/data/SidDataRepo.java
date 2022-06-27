package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SidDataRepo extends JpaRepository<SidData, Integer> {

    SidData findSidDataByCountryCode(String code);
}
