package com.torkirion.eroam.ims.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.torkirion.eroam.ims.apidomain.AccommodationSummary;
import com.torkirion.eroam.ims.datadomain.*;
import com.torkirion.eroam.ims.repository.*;
import com.torkirion.eroam.microservice.datadomain.AirlineRepo;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class DataService
{
	@Autowired
	private IMSAccommodationRCDataRepo accommodationRCDataRepo;

	@Autowired
	private IMSAccommodationCancellationPolicyRepo accommodationCancellationPolicyRepo;

	@Autowired
	private IMSAccommodationSeasonRepo accommodationSeasonRepo;

	@Autowired
	private IMSAccommodationBoardRepo accommodationBoardRepo;

	@Autowired
	private IMSAccommodationRoomtypeRepo accommodationRoomtypeRepo;

	@Autowired
	private IMSAccommodationRateRepo accommodationRateRepo;

	@Autowired
	private IMSAccommodationAllocationRepo accommodationAllocationRepo;

	@Autowired
	private IMSAccommodationSpecialRepo accommodationSpecialRepo;

	@Autowired
	private IMSAccommodationAllocationSummaryRepo accommodationAllocationSummaryRepo;

	@Autowired
	private IMSAccommodationCategoryRepo accommodationCategoryRepo;

	@Autowired
	private IMSAccommodationFacilityRepo accommodationFacilityRepo;

	@Autowired
	private IMSAccommodationSaleRepo accommodationSaleRepo;

	@Autowired
	private IMSEventRepo eventRepo;

	@Autowired
	private IMSEventAllotmentRepo eventAllotmentRepo;

	@Autowired
	private IMSEventClassificationRepo eventClassificationRepo;

	@Autowired
	private IMSEventSeriesRepo eventSeriesRepo;

	@Autowired
	private IMSEventSupplierRepo eventSupplierRepo;

	@Autowired
	private IMSEventTypeRepo eventTypeRepo;

	@Autowired
	private IMSEventVenueRepo eventVenueRepo;

	@Autowired
	private IMSEventSaleRepo eventSaleRepo;

	@Autowired
	private IMSEventMerchandiseLinkRepo eventMerchandiseLinkRepo;

	@Autowired
	private IMSMerchandiseCategoryRepo merchandiseCategoryRepo;

	@Autowired
	private IMSMerchandiseSupplierRepo merchandiseSupplierRepo;

	@Autowired
	private IMSMerchandiseRepo merchandiseRepo;

	@Autowired
	private IMSMerchandiseOptionRepo merchandiseOptionRepo;

	@Autowired
	private IMSMerchandiseSaleRepo merchandiseSaleRepo;

	@Autowired
	private IMSActivityRepo activityRepo;

	@Autowired
	private IMSActivitySupplierRepo activitySupplierRepo;

	@Autowired
	private IMSActivitySupplierAgeBandRepo activitySupplierAgeBandRepo;

	@Autowired
	private IMSActivityOptionRepo activityOptionRepo;

	@Autowired
	private IMSActivityDepartureTimeRepo activityDepartureTimeRepo;

	@Autowired
	private IMSActivityAllotmentRepo activityAllotmentRepo;

	@Autowired
	private IMSActivitySaleRepo activitySaleRepo;

	@Autowired
	private IMSTransportationBasicRepo transportationBasicRepo;

	@Autowired
	private IMSTransportationBasicClassRepo transportationBasicClassRepo;

	@Autowired
	private IMSTransportationBasicSegmentRepo transportationBasicSegmentRepo;

	@Autowired
	private IMSTransportationSaleRepo transportationSaleRepo;

	@Autowired
	private IMSSupplierRepo supplierRepo;

	@Autowired
	private IataAirportRepo iataAirportRepo;

	@Autowired
	private AirlineRepo airlineRepo;

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void createIMS(String client) throws Exception
	{
		log.debug("createIMS::enter for " + client);

		if ( !client.matches("[A-Za-z0-9]+"))
		{
			log.warn("createIMS::invalid client name " + client);
			return;
		}
		Query checkQuery = entityManager.createNativeQuery("SELECT schema_name FROM information_schema.schemata WHERE schema_name = '" + client + "'");
		List results = checkQuery.getResultList();
		if ( results.size() != 0)
		{
			log.warn("createIMS::schema " + client + " already exists");
			return;
		}
		
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources("classpath*:sql/create_ims.sql");
		for (Resource r : resources)
		{
			InputStream inputStream = r.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, Charset.defaultCharset());
			String sql = writer.toString();
			sql = sql.replaceAll("\\{schema\\}", client);
			log.debug("createIMS::execte:" + sql);
			int updateCount = entityManager.createNativeQuery(sql).executeUpdate();
			log.debug("createIMS::updateCount=" + updateCount);
		}
	}

	public List<AccommodationSummary> returnSummaryOfAll()
	{
		log.debug("returnSummaryOfAll::enter");

		List<AccommodationSummary> all = new ArrayList<>();
		for (IMSAccommodationRCData rc : accommodationRCDataRepo.findAll())
		{
			AccommodationSummary accommodationSummary = new AccommodationSummary();
			accommodationSummary.setHotelId(rc.getHotelId());
			accommodationSummary.setCity(rc.getAddress().getCity());
			accommodationSummary.setAccommodationName(rc.getAccommodationName());
			accommodationSummary.setState(rc.getAddress().getState());
			accommodationSummary.setCountry(rc.getAddress().getCountryCode());
			accommodationSummary.setLastUpdated(rc.getLastUpdated());
			all.add(accommodationSummary);
		}
		return all;
	}

}
