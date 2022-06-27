package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocationSummary;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRate;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRoomtype;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;

public interface IMSAccommodationAllocationSummaryRepo extends JpaRepository<IMSAccommodationAllocationSummary, IMSAccommodationAllocationSummary.AllocationKey>
{
	List<IMSAccommodationAllocationSummary> findByHotelId(String hotelId);
	void deleteByHotelIdAndAllocationId(String hotelId, Integer allocationId);
	void deleteByHotelId(String hotelId);
}