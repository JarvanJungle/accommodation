package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;

public interface IMSAccommodationAllocationRepo extends JpaRepository<IMSAccommodationAllocation, IMSAccommodationAllocation.AllocationKey>
{
	List<IMSAccommodationAllocation> findByHotelId(String hotelId);
	List<IMSAccommodationAllocation> findByHotelIdAndAllocationId(String hotelId, Integer allocationId);
	void deleteByHotelIdAndAllocationId(String hotelId, Integer allocationId);
	//List<IMSAccommodationAllocation> findByHotelIdAllocationIdOrderByAllocationDateAsc(String hotelId, Integer allocationId);
}