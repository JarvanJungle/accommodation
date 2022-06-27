package com.torkirion.eroam.microservice.accommodation.datadomain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.microservice.datadomain.SystemProperty;

public interface SystemPropertyRepo extends JpaRepository<SystemProperty, String>
{
	List<SystemProperty> findBySiteAndChannelAndParameter(String site, String channel, String parameter);
	List<SystemProperty> findBySiteAndProductTypeAndChannelAndParameter(String site, SystemProperty.ProductType productType, String channel, String parameter);
	List<SystemProperty> findBySiteAndProductType(String site, SystemProperty.ProductType productType);
}