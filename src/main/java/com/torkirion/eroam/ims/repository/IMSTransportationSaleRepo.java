package com.torkirion.eroam.ims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.torkirion.eroam.ims.datadomain.Event;
import com.torkirion.eroam.ims.datadomain.EventSale;
import com.torkirion.eroam.ims.datadomain.Merchandise;
import com.torkirion.eroam.ims.datadomain.MerchandiseSale;
import com.torkirion.eroam.ims.datadomain.TransportSale;

public interface IMSTransportationSaleRepo extends JpaRepository<TransportSale, Integer>
{
}