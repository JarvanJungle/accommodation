package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.EventSeries;

public interface IMSEventSeriesRepo extends JpaRepository<EventSeries, Integer>
{
}