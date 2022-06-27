package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinationSearchRQDataRepo extends JpaRepository<DestinationSearchRQData, Long> {
	void deleteByCreationDateTimeBefore(LocalDateTime creationDateTime);
}
