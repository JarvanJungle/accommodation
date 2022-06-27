package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Activity;
import com.torkirion.eroam.ims.datadomain.ActivitySale;
import com.torkirion.eroam.ims.datadomain.ActivitySupplier;
import com.torkirion.eroam.ims.datadomain.IataAirport;

public interface IataAirportRepo extends JpaRepository<IataAirport, Integer>
{
	List<IataAirport> findByIataCode(String iataCode);
}