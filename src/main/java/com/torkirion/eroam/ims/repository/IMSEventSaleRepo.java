package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Event;
import com.torkirion.eroam.ims.datadomain.EventSale;

public interface IMSEventSaleRepo extends JpaRepository<EventSale, Integer>
{
	List<EventSale> findByEvent(Event event);
}