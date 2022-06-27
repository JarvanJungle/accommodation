package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSpecial;

public interface IMSAccommodationSpecialRepo extends JpaRepository<IMSAccommodationSpecial, Long>
{
	List<IMSAccommodationSpecial> findByHotelId(String hotelId);
	Long deleteByHotelId(String hotelId);
}