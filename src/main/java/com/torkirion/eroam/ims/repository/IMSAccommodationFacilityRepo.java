package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.EventType;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationFacility;

public interface IMSAccommodationFacilityRepo extends JpaRepository<IMSAccommodationFacility, String>
{
}