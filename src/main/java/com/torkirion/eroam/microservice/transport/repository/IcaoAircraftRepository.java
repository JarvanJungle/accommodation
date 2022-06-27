package com.torkirion.eroam.microservice.transport.repository;

import com.torkirion.eroam.microservice.transport.datadomain.IcaoAircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IcaoAircraftRepository extends JpaRepository<IcaoAircraft, String>
{
}
