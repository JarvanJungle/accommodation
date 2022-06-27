package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Activity;
import com.torkirion.eroam.ims.datadomain.ActivityOption;

public interface IMSActivityOptionRepo extends JpaRepository<ActivityOption, Integer>
{
	List<ActivityOption> findByActivity(Activity activity);
	void deleteByActivity(Activity activity);
}