package com.torkirion.eroam.microservice.accommodation.endpoint.youtravel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YoutravelStaticRepo extends JpaRepository<YoutravelStaticData, Integer>
{
	Long deleteByStaticTypeAndCode(String staticType, String code);

	List<YoutravelStaticData> findByStaticTypeAndCode(String staticType, String code);
}