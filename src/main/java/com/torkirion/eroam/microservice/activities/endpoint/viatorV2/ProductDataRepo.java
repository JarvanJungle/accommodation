package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;

public interface ProductDataRepo extends JpaRepository<ProductData, String>
{
	List<ProductData> findByLastUpdatedAtAfter(LocalDateTime oldestUpdate);
	List<ProductData> findByAvailLastUpdatedAtAfter(LocalDateTime oldestUpdate);
}