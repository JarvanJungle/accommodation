package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Activity;
import com.torkirion.eroam.ims.datadomain.ActivityAllotment;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocationSummary;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRate;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRoomtype;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;

public interface IMSActivityAllotmentRepo extends JpaRepository<ActivityAllotment, ActivityAllotment.AllotmentKey>
{
	List<ActivityAllotment> findByActivityId(Integer activityId);
	List<ActivityAllotment> findByActivityIdAndOptionIdAndDepartureTimeIdOrderByAllotmentDate(Integer activityId, Integer optionId, Integer departureTimeId);
	List<ActivityAllotment> findByActivityIdAndOptionId(Integer activityId, Integer optionId);
	Long deleteByActivityIdAndOptionIdAndDepartureTimeId(Integer activityId, Integer optionId, Integer departureTimeId);
	void deleteByActivityId(Integer activityId);
}