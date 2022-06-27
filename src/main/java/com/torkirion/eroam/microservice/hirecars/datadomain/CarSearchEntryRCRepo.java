package com.torkirion.eroam.microservice.hirecars.datadomain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarSearchEntryRCRepo extends JpaRepository<CarSearchEntryRCData, String> {
}
