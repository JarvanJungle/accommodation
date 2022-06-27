package com.torkirion.eroam.microservice.datadomain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IataAirportV2Repo extends JpaRepository<IataAirportV2, Integer> {
}
