package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Activity;
import com.torkirion.eroam.ims.datadomain.ActivitySale;

public interface IMSActivitySaleRepo extends JpaRepository<ActivitySale, Integer>
{
	List<ActivitySale> findByActivity(Activity activity);
}