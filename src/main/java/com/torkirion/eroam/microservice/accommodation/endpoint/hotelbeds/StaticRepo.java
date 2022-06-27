package com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface StaticRepo extends JpaRepository<StaticData, Integer>
{
	Long deleteByStaticTypeAndCode(String staticType, String code);

	List<StaticData> findByStaticTypeAndCode(String staticType, String code);
}