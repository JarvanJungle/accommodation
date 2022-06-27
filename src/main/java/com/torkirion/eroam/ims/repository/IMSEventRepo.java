package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Event;
import com.torkirion.eroam.ims.datadomain.EventSeries;
import com.torkirion.eroam.ims.datadomain.EventVenue;

public interface IMSEventRepo extends JpaRepository<Event, Integer>
{
	List<Event> findByEventVenue(EventVenue eventVenue);
	List<Event> findByEventSeries(EventSeries eventSeries);
}