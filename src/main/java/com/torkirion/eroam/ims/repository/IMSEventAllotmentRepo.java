package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Event;
import com.torkirion.eroam.ims.datadomain.EventAllotment;

public interface IMSEventAllotmentRepo extends JpaRepository<EventAllotment, Integer>
{
	List<EventAllotment> findByEvent(Event event);
	void deleteByEvent(Event event);
}