package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Activity;
import com.torkirion.eroam.ims.datadomain.ActivitySupplier;
import com.torkirion.eroam.ims.datadomain.ActivitySupplierAgeBand;
import com.torkirion.eroam.ims.datadomain.Event;
import com.torkirion.eroam.ims.datadomain.EventSeries;
import com.torkirion.eroam.ims.datadomain.EventVenue;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationBoard;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;

public interface IMSActivityRepo extends JpaRepository<Activity, Integer>
{
	public List<Activity> findByActivitySupplier(ActivitySupplier activitySupplier);
	public List<Activity> findByName(String name);
	public List<Activity> findByExternalActivityId(String externalActivityId);
}