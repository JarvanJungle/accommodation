package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.ActivitySupplier;
import com.torkirion.eroam.ims.datadomain.ActivitySupplierAgeBand;

public interface IMSActivitySupplierAgeBandRepo extends JpaRepository<ActivitySupplierAgeBand, Integer>
{
	List<ActivitySupplierAgeBand> findByActivitySupplier(ActivitySupplier activitySupplier);
	List<ActivitySupplierAgeBand> deleteByActivitySupplier(ActivitySupplier activitySupplier);
}