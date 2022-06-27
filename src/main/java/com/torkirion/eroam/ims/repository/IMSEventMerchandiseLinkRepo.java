package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.EventMerchandiseLink;
import com.torkirion.eroam.ims.datadomain.EventSeries;
import com.torkirion.eroam.ims.datadomain.EventType;
import com.torkirion.eroam.ims.datadomain.Merchandise;

public interface IMSEventMerchandiseLinkRepo extends JpaRepository<EventMerchandiseLink, Integer>
{
	Integer deleteByEventSeriesAndMerchandise(EventSeries eventSeries, Merchandise merchandise);
	Integer deleteByEventSeries(EventSeries eventSeries);
}