package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCRepo;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.AvailabilitySchedule;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.BookingQuestions;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.BookingQuestions.BookingQuestion;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.Destinations;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.Destinations.Destination;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.Product;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Slf4j
@Service
public class ViatorV2Loader
{
	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	@Autowired
	private DestinationRepo destinationRepo;

	@Autowired
	private BookingQuestionRepo bookingQuestionRepo;

	@Autowired
	private ProductDataRepo productDataRepo;

	@Autowired
	private ActivityRCRepo activityRCRepo;

	@Autowired
	private ViatorV2LoadProgressRepo viatorV2LoadProgressRepo;

	@Autowired
	private ViatorV2ActivityRepo viatorV2ActivityRepo;

	@Autowired
	private ViatorV2TransactionLoader viatorV2TransactionLoader;

	@Autowired
	private ViatorV2ActivityProductOptionRepo viatorV2ActivityProductOptionRepo;

	@Autowired
	private ViatorV2ScheduleDataRepo activityScheduleRepo;

	@Transactional
	public void loadDestinations(HttpService httpService) throws Exception
	{
		log.debug("loadDestinations::enter");

		String response = httpService.doCallGet("service/taxonomy/destinations", null);
		Destinations destinations = getObjectMapper().readValue(response, Destinations.class);
		destinationRepo.deleteAll();
		int count = 0;
		for (Destination destination : destinations.getData())
		{
			DestinationData destinationData = new DestinationData();
			destinationData.setDestinationId(destination.getDestinationId());
			destinationData.setDestinationName(destination.getDestinationName());
			BeanUtils.copyProperties(destination, destinationData);
			destinationRepo.save(destinationData);
			count++;
		}
		log.debug("loadDestinations::loaded " + count + " destinations");
	}

	@Data
	public static class LocationRequest
	{
		private List<String> locations = new ArrayList<>();
	}
	@Transactional
	public void loadLocations(HttpService httpService, Set<String> locations) throws Exception
	{
		log.debug("loadLocations::enter");

		LocationRequest locationRequest = new LocationRequest();
		locationRequest.getLocations().addAll(locations);
		String response = httpService.doCallPost("partner/service/locations/bulk", locationRequest);
		/*
		Destinations destinations = getObjectMapper().readValue(response, Destinations.class);
		destinationRepo.deleteAll();
		int count = 0;
		for (Destination destination : destinations.getData())
		{
			DestinationData destinationData = new DestinationData();
			destinationData.setDestinationId(destination.getDestinationId());
			destinationData.setDestinationName(destination.getDestinationName());
			BeanUtils.copyProperties(destination, destinationData);
			destinationRepo.save(destinationData);
			count++;
		}
		log.debug("loadDestinations::loaded " + count + " destinations");
		*/
	}

	@Transactional
	public void loadBookingQuestions(HttpService httpService) throws Exception
	{
		log.debug("loadBookingQuestions::enter");

		String response = httpService.doCallGet("partner/products/booking-questions", null);
		log.debug("loadBookingQuestions::response=" + response);
		BookingQuestions bookingQuestions = getObjectMapper().readValue(response, BookingQuestions.class);
		bookingQuestionRepo.deleteAll();
		int count = 0;
		for (BookingQuestion bookingQuestion : bookingQuestions.getBookingQuestions())
		{
			BookingQuestionData bookingQuestionData = new BookingQuestionData();
			BeanUtils.copyProperties(bookingQuestion, bookingQuestionData);
			bookingQuestionData.setQuestionId(bookingQuestion.getId());
			bookingQuestionData.setBookingGroup(bookingQuestion.getGroup());
			if (bookingQuestion.getUnits() != null)
			{
				bookingQuestionData.setUnits(StringUtils.collectionToCommaDelimitedString(bookingQuestion.getUnits()));
			}
			if (bookingQuestion.getAllowedAnswers() != null)
			{
				bookingQuestionData.setAllowedAnswers(StringUtils.collectionToCommaDelimitedString(bookingQuestion.getAllowedAnswers()));
			}
			log.debug("loadBookingQuestions::saving " + bookingQuestionData);
			bookingQuestionRepo.save(bookingQuestionData);
			count++;
		}
		ViatorV2Service.resetBookingQuestionsCache();
		log.debug("loadBookingQuestions::loaded " + count + " booking questions");
	}

	public LocalDateTime loadActivityRC(HttpService httpService) throws Exception
	{
		log.debug("loadActivityRC::enter");

		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

		String cursor = null;
		LocalDateTime lastModifiedDate = null;
		Optional<ViatorV2LoadProgress> loadProgressOpt = viatorV2LoadProgressRepo.findById("rc");
		if (loadProgressOpt.isPresent())
		{
			ViatorV2LoadProgress loadProgress = loadProgressOpt.get();
			if (loadProgress.getCursor() != null && loadProgress.getCursor().length() > 0)
				cursor = loadProgress.getCursor();
			if (loadProgress.getLastDateTime() != null)
				lastModifiedDate = loadProgress.getLastDateTime();
		}
		final int readCount = 100;
		boolean loop = true;
		int loopCount = 0;
		int totalCount = 0;
		LocalDateTime oldestUpdate = null;
		while (loop)
		{
			try
			{
				StringBuffer url = new StringBuffer("partner/products/modified-since?");
				if (lastModifiedDate != null)
				{
					if (!url.toString().endsWith("?"))
						url.append("&");
					url.append("modified-since=" + lastModifiedDate.format(formatter2));
				}
				if (cursor != null)
				{
					if (!url.toString().endsWith("?"))
						url.append("&");
					url.append("cursor=" + cursor);
				}
				{
					if (!url.toString().endsWith("?"))
						url.append("&");
					url.append("count=" + readCount);
				}
				log.debug("loadActivityRC::calling loop " + loopCount + ", totalCount=" + totalCount);
				String response = httpService.doCallGet(url.toString(), null);
				log.debug("loadActivityRC::response=" + response);
				ProductRC allProductRC = getObjectMapper().readValue(response, ProductRC.class);
				cursor = allProductRC.getNextCursor();
				if (cursor == null)
					loop = false;
				for (Product productRC : allProductRC.getProducts())
				{
					LocalDateTime lastUpdatedAt = null;
					log.debug("loadActivityRC::procesing productCode '" + productRC.getProductCode() + "' status " + productRC.getStatus());
					if ("INACTIVE".equals(productRC.getStatus()))
					{
						log.debug("loadActivityRC::bypass inactive");
						continue;
					}
					try
					{
						lastUpdatedAt = LocalDateTime.parse(productRC.getLastUpdatedAt(), formatter2);
					}
					catch (java.time.format.DateTimeParseException e)
					{
						log.debug("saveActivityRC::format2: bad field:" + productRC.getLastUpdatedAt());
						try
						{
							lastUpdatedAt = LocalDateTime.parse(productRC.getLastUpdatedAt(), formatter1);
						}
						catch (java.time.format.DateTimeParseException e2)
						{
							log.debug("saveActivityRC::format1: bad field:" + productRC.getLastUpdatedAt());
							lastUpdatedAt = LocalDateTime.now();
						}
					}

					try
					{
						viatorV2TransactionLoader.processViatorProductJSON(productRC, lastUpdatedAt);
						oldestUpdate = saveActivityRC(productRC, formatter1, formatter2, oldestUpdate);
						totalCount++;
					}
					catch (Exception e)
					{
						log.warn("loadActivityRC::caught exception for " + productRC.getProductCode() + ":" + e.toString(), e);
					}
				}
				viatorV2TransactionLoader.updateProgres("rc", cursor, null);

				loopCount++;
				log.info("loadActivityRC::freeMemory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + "M, totalMemory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024))
						+ "M, usedMemory:" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "M");
			}
			catch (Exception e)
			{
				log.warn("loadActivityRC::caught exception, pausing and retrying:" + e.toString(), e);
				Thread.sleep(5000);
			}
		}
		viatorV2TransactionLoader.updateProgres("rc", null, LocalDateTime.now());
		log.debug("loadActivityRC::loaded " + totalCount + " products, oldest update is " + oldestUpdate);
		return oldestUpdate;
	}

	public void loadSingleActivityRC(HttpService httpService, String productCode) throws Exception
	{
		log.debug("loadSingleActivityRC::enter for " + productCode);

		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

		try
		{
			StringBuffer url = new StringBuffer("partner/products/" + productCode);
			String response = httpService.doCallGet(url.toString(), null);
			log.debug("loadSingleActivityRC::response=" + response);
			Product productRC = getObjectMapper().readValue(response, Product.class);
			log.debug("loadSingleActivityRC::processing productCode '" + productRC.getProductCode() + "' status " + productRC.getStatus());
			if ("INACTIVE".equals(productRC.getStatus()))
			{
				log.debug("loadSingleActivityRC::bypass inactive");
				return;
			}
			LocalDateTime lastUpdatedAt = LocalDateTime.now();
			if ( productRC.getLastUpdatedAt() != null )
			{
				try
				{
					lastUpdatedAt = LocalDateTime.parse(productRC.getLastUpdatedAt(), formatter2);
				}
				catch (java.time.format.DateTimeParseException e)
				{
					log.debug("loadSingleActivityRC::format2: bad field:" + productRC.getLastUpdatedAt());
					try
					{
						lastUpdatedAt = LocalDateTime.parse(productRC.getLastUpdatedAt(), formatter1);
					}
					catch (java.time.format.DateTimeParseException e2)
					{
						log.debug("loadSingleActivityRC::format1: bad field:" + productRC.getLastUpdatedAt());
						lastUpdatedAt = LocalDateTime.now();
					}
				}
			}

			try
			{
				viatorV2TransactionLoader.processViatorProductJSON(productRC, lastUpdatedAt);
				saveActivityRC(productRC, formatter1, formatter2, null);
			}
			catch (Exception e)
			{
				log.warn("loadSingleActivityRC::caught exception for " + productRC.getProductCode() + ":" + e.toString(), e);
			}
		}
		catch (Exception e)
		{
			log.warn("loadSingleActivityRC::caught exception, pausing and retrying:" + e.toString(), e);
			Thread.sleep(5000);
		}

		log.debug("loadSingleActivityRC::loaded " + productCode);
	}

	@Transactional
	public LocalDateTime saveActivityRC(Product productRC, DateTimeFormatter formatter1, DateTimeFormatter formatter2, LocalDateTime oldestUpdate) throws Exception
	{
		Optional<ProductData> productDataOpt = productDataRepo.findById(productRC.getProductCode());
		ProductData productData = new ProductData();
		if (productDataOpt.isPresent())
		{
			productData = productDataOpt.get();
			if (productData.getProductJson() == null)
				log.debug("saveActivityRC::updating existing productCode with new data");
			else
				log.debug("saveActivityRC::updating existing productCode with existing data");
		}
		else
		{
			log.debug("saveActivityRC::creating new productCode");
			productData = new ProductData();
			productData.setProductCode(productRC.getProductCode());
		}
		productData.setTitle(productRC.getTitle());
		if ( productRC.getLastUpdatedAt() != null)
		{
			try
			{
				productData.setLastUpdatedAt(LocalDateTime.parse(productRC.getLastUpdatedAt(), formatter2));
			}
			catch (java.time.format.DateTimeParseException e)
			{
				log.debug("saveActivityRC::format2: bad field:" + productRC.getLastUpdatedAt());
				try
				{
					productData.setLastUpdatedAt(LocalDateTime.parse(productRC.getLastUpdatedAt(), formatter1));
				}
				catch (java.time.format.DateTimeParseException e2)
				{
					log.debug("saveActivityRC::format1: bad field:" + productRC.getLastUpdatedAt());
					productData.setLastUpdatedAt(LocalDateTime.now());
				}
			}
		}
		else
		{
			productData.setLastUpdatedAt(LocalDateTime.now());
		}
		if (oldestUpdate == null || productData.getLastUpdatedAt().isBefore(oldestUpdate))
		{
			oldestUpdate = productData.getLastUpdatedAt();
		}
		// StringWriter writer = new StringWriter();
		// getObjectMapper().writeValue(writer, productRC);
		// String productRCString = writer.toString();
		// productData.setProductJson(productRCString);
		productDataRepo.save(productData);
		return oldestUpdate;
	}

	@Transactional
	public void saveActivityAvailability(AvailabilitySchedule productAvailSchedule, LocalDateTime now) throws Exception
	{
		Optional<ProductData> productDataOpt = productDataRepo.findById(productAvailSchedule.getProductCode());
		ProductData productData = null;
		if (productDataOpt.isPresent())
		{
			productData = productDataOpt.get();
			if (productData.getAvailabilityJson() == null)
				log.debug("saveActivityRC::updating existing productCode with new data");
			else
				log.debug("saveActivityRC::updating existing productCode with existing data");
		}
		else
		{
			productData = new ProductData();
			productData.setProductCode(productAvailSchedule.getProductCode());
			log.debug("saveActivityAvailability::creating new productCode");
		}
		// StringWriter writer = new StringWriter();
		// getObjectMapper().writeValue(writer, productAvailSchedule);
		// String availabilityJson = writer.toString();
		// productData.setAvailabilityJson(availabilityJson);
		productData.setAvailLastUpdatedAt(now);
		log.debug("saveActivityAvailability::saving code " + productData.getProductCode());
		productDataRepo.save(productData);
	}

	@Transactional
	public void processAllActivityRCFromDB(LocalDateTime oldestUpdate) throws Exception
	{
		log.debug("processAllActivityRCFromDB::enter");
		List<ProductData> dataToprocess = null;
		if (oldestUpdate == null)
			dataToprocess = productDataRepo.findAll();
		else
			dataToprocess = productDataRepo.findByLastUpdatedAtAfter(oldestUpdate);
		int updateCount = 0;
		for (ProductData productData : dataToprocess)
		{
			if (productData.getProductJson() != null)
			{
				ProductRC.Product product = getObjectMapper().readValue(productData.getProductJson(), ProductRC.Product.class);
				log.debug("processAllActivityRCFromDB::processing product " + product.getProductCode());

				viatorV2TransactionLoader.processViatorProductJSON(product, productData.getLastUpdatedAt());
				updateCount++;
			}
		}
		ViatorV2Service.resetActivitySummaryCache();
		log.debug("processAllActivityRCFromDB::processed " + updateCount + " products");
	}

	public LocalDateTime loadAvailability(HttpService httpService) throws Exception
	{
		log.debug("loadAvailability::enter");

		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

		String cursor = "";
		LocalDateTime lastModifiedDate = null;
		Optional<ViatorV2LoadProgress> loadProgressOpt = viatorV2LoadProgressRepo.findById("avail");
		if (loadProgressOpt.isPresent())
		{
			ViatorV2LoadProgress loadProgress = loadProgressOpt.get();
			if (loadProgress.getCursor() != null && loadProgress.getCursor().length() > 0)
				cursor = loadProgress.getCursor();
			if (loadProgress.getLastDateTime() != null)
				lastModifiedDate = loadProgress.getLastDateTime();
		}

		final Integer readCount = 500;
		boolean loop = true;
		int loopCount = 0;
		int totalCount = 0;
		LocalDateTime now = LocalDateTime.now();
		while (loop)
		{
			StringBuffer url = new StringBuffer("partner/availability/schedules/modified-since?");
			if (lastModifiedDate != null)
			{
				if (!url.toString().endsWith("?"))
					url.append("&");
				url.append("modified-since=" + lastModifiedDate.format(formatter1));
			}
			if (cursor != null)
			{
				if (!url.toString().endsWith("?"))
					url.append("&");
				url.append("cursor=" + cursor);
			}
			{
				if (!url.toString().endsWith("?"))
					url.append("&");
				url.append("count=" + readCount);
			}
			log.debug("loadAvailability::calling loop " + loopCount + ", totalCount=" + totalCount);
			String response = httpService.doCallGet(url.toString(), null);
			log.debug("loadAvailability::response=" + response);
			AvailabilitySchedules availabilitySchedules = getObjectMapper().readValue(response, AvailabilitySchedules.class);
			cursor = availabilitySchedules.getNextCursor();
			if (cursor == null)
				loop = false;
			for (AvailabilitySchedule productAvailSchedule : availabilitySchedules.getAvailabilitySchedules())
			{
				viatorV2TransactionLoader.processAvailability(productAvailSchedule, productAvailSchedule.getProductCode());
				saveActivityAvailability(productAvailSchedule, now);
				totalCount++;
			}
			viatorV2TransactionLoader.updateProgres("avail", cursor, null);

			loopCount++;
			log.info("loadAvailability::freeMemory:" + (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + "M, totalMemory:" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + "M, usedMemory:"
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "M");
		}
		viatorV2TransactionLoader.updateProgres("avail", null, LocalDateTime.now());
		log.debug("loadAvailAbility::loop " + loopCount + ", loaded " + totalCount + " products");
		return now;
	}

	public void loadSingleAvailability(HttpService httpService, String productCode) throws Exception
	{
		log.debug("loadSingleAvailability::enter");

		StringBuffer url = new StringBuffer("partner/availability/schedules/" + productCode);
		String response = httpService.doCallGet(url.toString(), null);
		log.debug("loadSingleAvailability::response=" + response);
		AvailabilitySchedule productAvailSchedule = getObjectMapper().readValue(response, AvailabilitySchedule.class);
		viatorV2TransactionLoader.processAvailability(productAvailSchedule, productAvailSchedule.getProductCode());
		saveActivityAvailability(productAvailSchedule, LocalDateTime.now());
		
		log.debug("loadSingleAvailability::loaded " + productCode);
	}
	
	@Transactional
	public void processAllAvailabilityFromDB(LocalDateTime oldestUpdate) throws Exception
	{
		log.debug("processAllAvailabilityFromDB::enter with oldestUpdate " + oldestUpdate);
		List<ProductData> dataToprocess = null;
		if (oldestUpdate == null)
			dataToprocess = productDataRepo.findAll();
		else
			dataToprocess = productDataRepo.findByAvailLastUpdatedAtAfter(oldestUpdate);
		int updateCount = 0;

		for (ProductData productData : dataToprocess)
		{
			if (productData.getAvailabilityJson() != null)
			{
				AvailabilitySchedules.AvailabilitySchedule schedule = getObjectMapper().readValue(productData.getAvailabilityJson(), AvailabilitySchedules.AvailabilitySchedule.class);
				viatorV2TransactionLoader.processAvailability(schedule, productData.getProductCode());
				updateCount++;
			}
		}
		log.debug("processAllAvailabilityFromDB::processed " + updateCount + " products");
	}

	private final ObjectMapper getObjectMapper()
	{
		if (_objectMapper == null)
		{
			_objectMapper = new ObjectMapper();
			_objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return _objectMapper;
	}

	private ObjectMapper _objectMapper = null;

	private static final DateTimeFormatter timeFormatterHHMM = DateTimeFormatter.ofPattern("HH:mm");
}
