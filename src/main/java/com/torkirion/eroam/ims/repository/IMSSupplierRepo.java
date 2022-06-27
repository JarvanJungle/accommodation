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
import com.torkirion.eroam.ims.datadomain.IMSAccommodationAllocation;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationBoard;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationCancellationPolicy;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationRCData;
import com.torkirion.eroam.ims.datadomain.IMSAccommodationSeason;
import com.torkirion.eroam.ims.datadomain.Merchandise;
import com.torkirion.eroam.ims.datadomain.Supplier;
import com.torkirion.eroam.ims.datadomain.TransportationBasic;

public interface IMSSupplierRepo extends JpaRepository<Supplier, Long>
{
	List<Supplier> findByForAccommodation(Boolean forAccommodation);
	List<Supplier> findByForActivities(Boolean forActivities);
	List<Supplier> findByForEvents(Boolean forEvents);
	List<Supplier> findByForMerchandise(Boolean forMerchandise);
	List<Supplier> findByForTransportation(Boolean forTransportation);
}