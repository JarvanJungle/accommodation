package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.EventType;

public interface IMSEventTypeRepo extends JpaRepository<EventType, Integer>
{
	List<EventType> findByName(String name);
}