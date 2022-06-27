package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;

public interface ViatorV2ActivityRepo extends JpaRepository<ViatorV2Activity, Long>
{
	List<ViatorV2Activity> findByProductCode(String productCode);
	List<ViatorV2Activity> findAllByProductCodeIn(List<String> productCodes);
	void deleteByProductCode(String productCode);
}