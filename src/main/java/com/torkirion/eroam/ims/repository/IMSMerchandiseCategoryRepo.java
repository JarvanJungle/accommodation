package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.MerchandiseCategory;

public interface IMSMerchandiseCategoryRepo extends JpaRepository<MerchandiseCategory, Integer>
{
	List<MerchandiseCategory> findByName(String name);
}