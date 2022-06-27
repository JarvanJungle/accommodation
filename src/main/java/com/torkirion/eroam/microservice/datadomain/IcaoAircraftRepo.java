package com.torkirion.eroam.microservice.datadomain;

import com.torkirion.eroam.microservice.transport.datadomain.IcaoAircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IcaoAircraftRepo extends JpaRepository<IcaoAircraft, Integer> {
}
