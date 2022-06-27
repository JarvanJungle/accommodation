package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Event;
import com.torkirion.eroam.ims.datadomain.Merchandise;
import com.torkirion.eroam.ims.datadomain.MerchandiseOption;

public interface IMSMerchandiseOptionRepo extends JpaRepository<MerchandiseOption, Integer>
{
	List<MerchandiseOption> findByMerchandise(Merchandise merchandise);
	void deleteByMerchandise(Merchandise merchandise);
}