package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;

public interface IMSAccommodationCancellationPolicyRepo extends JpaRepository<IMSAccommodationCancellationPolicy, Long>
{
	List<IMSAccommodationCancellationPolicy> findByHotelIdOrderByPolicyIdAscLineIdAsc(String hotelId);
	Long deleteByHotelId(String hotelId);
}