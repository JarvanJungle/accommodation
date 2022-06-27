package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRate;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRoomtype;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;

public interface IMSAccommodationRateRepo extends JpaRepository<IMSAccommodationRate, Long>
{
	List<IMSAccommodationRate> findByHotelIdOrderByDescriptionAsc(String hotelId);
	Long deleteByHotelId(String hotelId);
	Long deleteByHotelIdAndRateId(String hotelId, Integer rateId);
}