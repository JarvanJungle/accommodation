package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Event;
import com.torkirion.eroam.ims.datadomain.EventClassification;

public interface IMSEventClassificationRepo extends JpaRepository<EventClassification, Integer>
{
	List<EventClassification> findByEvent(Event event);
	void deleteByEvent(Event event);
}