package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCData;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCRepo;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.ViatorV2Loader.LocationRequest;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.*;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.BookableItem;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.PriceList;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.PricingDetail;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.PricingRecord;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.Season;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.TimedEntry;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.UnavailableDate;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.AdditionalInfo;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.AgeBand;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.CancellationPolicy;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.Destination;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.Image;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.ImageVariant;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.InclusionExclusion;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.Itinerary;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.ItineraryItem;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.LanguageGuide;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.ProductOption;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.Redemption;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.TicketInfo;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.TravelerPickup;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.TravelerPickupLocation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class ViatorV2TransactionLoader
{
	@Autowired
	private ActivityRCRepo activityRCRepo;

	@Autowired
	private ViatorV2ActivityRepo viatorV2ActivityRepo;

	@Autowired
	private ViatorV2ActivityProductOptionRepo viatorV2ActivityProductOptionRepo;

	@Autowired
	private ViatorV2ScheduleDataRepo activityScheduleRepo;

	@Autowired
	private ViatorV2UnavailableDataRepo activityUnavailableRepo;

	@Autowired
	private DestinationRepo destinationRepo;

	@Autowired
	private ViatorV2LoadProgressRepo viatorV2LoadProgressRepo;
	
	@Transactional
	public void updateProgres(String loadtype, String cursor, LocalDateTime theDate) throws Exception
	{
		Optional<ViatorV2LoadProgress> loadProgressOpt = viatorV2LoadProgressRepo.findById(loadtype);
		ViatorV2LoadProgress loadProgress = null;
		if ( loadProgressOpt.isPresent())
		{
			loadProgress = loadProgressOpt.get();
		}
		else
		{
			loadProgress = new ViatorV2LoadProgress();
			loadProgress.setLoadtype(loadtype);
		}
		loadProgress.setCursor(cursor);
		loadProgress.setLastDateTime(theDate);
		viatorV2LoadProgressRepo.save(loadProgress);
	}
	
	@Transactional
	public void processViatorProductJSON(ProductRC.Product product, LocalDateTime lastUpdatedAt) throws Exception
	{
		log.debug("processViatorProductJSON::enter");
		String activityCode = ViatorV2Service.CHANNEL_PREFIX + product.getProductCode();
		ActivityRCData activityRCData = null;
		Optional<ActivityRCData> activityRCDataOpt = activityRCRepo.findById(activityCode);
		if (activityRCDataOpt.isPresent())
			activityRCData = activityRCDataOpt.get();
		if (!product.getStatus().equals("ACTIVE") && activityRCData != null)
		{
			log.debug("processViatorProductJSON::removing " + activityCode + ", status is " + product.getStatus());
			activityRCRepo.deleteById(activityCode);
			return;
		}
		if (!product.getLanguage().equals("en") && activityRCData != null)
		{
			log.debug("processViatorProductJSON::removing " + activityCode + ", language is " + product.getLanguage());
			activityRCRepo.deleteById(activityCode);
			return;
		}
		activityRCData = new ActivityRCData();
		activityRCData.setCode(activityCode);
		activityRCData.setActivityName(product.getTitle());
		activityRCData.setChannelCode(product.getProductCode());
		activityRCData.setChannel(ViatorV2Service.CHANNEL.toString());
		if (product.getSupplier() != null)
			activityRCData.setOperator(product.getSupplier().getName());
		else
		{
			log.debug("processViatorProductJSON::product " + activityCode + " has no supplier info");
			activityRCData.setOperator("");
		}
		if (product.getDestinations() == null)
		{
			log.debug("processViatorProductJSON::product " + activityCode + " has no destinations");
			return;
		}
		for (Destination destination : product.getDestinations())
		{
			log.debug("processViatorProductJSON::destination " + destination);
			DestinationData destinationData = getDestination(Integer.parseInt(destination.getRef()));
			if (destinationData != null)
			{
				if (activityRCData.getGeoCoordinates() == null || destination.getPrimary())
				{
					activityRCData.setGeoCoordinates(
							new com.torkirion.eroam.microservice.activities.datadomain.ActivityRCData.GeoCoordinates(destinationData.getLatitude(), destinationData.getLongitude(), BigDecimal.ZERO));
					log.debug("processViatorProductJSON::product " + activityCode + " set to " + activityRCData.getGeoCoordinates());
				}
			}
			else
			{
				log.warn("processViatorProductJSON::unknown destination " + destination);
			}
		}
		// activityRCData.setCategories(); TODO Tags?

		activityRCData.setProductType("ACTIVITY"); // Unavailable for Viator?
		if ( product.getItinerary() != null && product.getItinerary().getDuration() != null)
		{
			int minutes = 0;
			if ( product.getItinerary().getDuration().getFixedDurationInMinutes() != null )
				minutes = product.getItinerary().getDuration().getFixedDurationInMinutes();
			else
				if ( product.getItinerary().getDuration().getVariableDurationToMinutes() != null )
					minutes = product.getItinerary().getDuration().getVariableDurationToMinutes();
			if ( minutes != 0)
			{
				Duration duration = Duration.ofMinutes(minutes);
				StringBuffer durationText = new StringBuffer();
				if ( duration.toDays() != 0)
					durationText.append(duration.toDays() + " days ");
				duration = duration.plusDays(duration.toDays() * -1);
				if ( duration.toHours() != 0)
					durationText.append(duration.toHours() + " hours ");
				duration = duration.plusHours(duration.toHours() * -1);
				if ( duration.toMinutes() != 0)
					durationText.append(duration.toMinutes() + " minutes ");
				activityRCData.setDurationText(durationText.toString().trim());
				activityRCData.setDuration("P" + minutes + "M");
			}
		}
		activityRCData.setFromPerPerson(null); // TODO Unavailable for RC Load - try to fill it in later?
		if (product.getLogistics() != null && product.getLogistics().getTravelerPickup() != null)
		{
			activityRCData.setDeparturePoint(makeDeparturePointText(product.getLogistics().getTravelerPickup(), activityCode));
		}
		activityRCData.setHotelPickupAvailable(false);
		if (product.getLogistics() != null && product.getLogistics().getTravelerPickup() != null && product.getLogistics().getTravelerPickup().getAllowCustomTravelerPickup() != null)
			activityRCData.setHotelPickupAvailable(product.getLogistics().getTravelerPickup().getAllowCustomTravelerPickup());
		// load hotels for pickup
		if (product.getLogistics() != null && product.getLogistics().getTravelerPickup() != null && product.getLogistics().getTravelerPickup().getLocations() != null)
		{
			Set<String> locationCodes = new HashSet<>();
			for ( TravelerPickupLocation location : product.getLogistics().getTravelerPickup().getLocations())
			{
				locationCodes.add(location.getLocation().getRef());
			}
			LocationRequest locationRequest = new LocationRequest();
			locationRequest.getLocations().addAll(locationCodes);
			//String response = httpService.doCallPost("service/locations/bulk", locationRequest);
		}
		activityRCData.setOverview(product.getDescription());
		if (product.getItinerary() != null)
			activityRCData.setItineraryHighlights(makeItineraryText(product.getItinerary()));
		StringBuffer inclusions = new StringBuffer();
		if (product.getInclusions() != null)
		{
			for (InclusionExclusion inclusion : product.getInclusions())
			{
				if (inclusions.length() > 0)
					inclusions.append("<br/>");
				if ( inclusion.getDescription() == null)
					inclusions.append(inclusion.getOtherDescription());
				else
					inclusions.append(inclusion.getDescription());
			}
		}
		activityRCData.setInclusions(inclusions.toString());
		StringBuffer exclusions = new StringBuffer();
		if (product.getExclusions() != null)
		{
			for (InclusionExclusion exclusion : product.getExclusions())
			{
				if (exclusions.length() > 0)
					exclusions.append("<br/>");
				if ( exclusion.getDescription() == null )
					exclusions.append(exclusion.getOtherDescription());
				else
					exclusions.append(exclusion.getDescription());
			}
		}
		activityRCData.setExclusions(exclusions.toString());
		StringBuffer additionalInformations = new StringBuffer();
		if (product.getAdditionalInfo() != null)
		{
			for (AdditionalInfo additionalInfo : product.getAdditionalInfo())
			{
				if (additionalInformations.length() > 0)
					additionalInformations.append("<br/>");
				additionalInformations.append(additionalInfo.getDescription());
			}
		}
		activityRCData.setAdditionalInformation(additionalInformations.toString());
		activityRCData.setVoucherInformation(makeVoucherInformation(product.getTicketInfo(), product.getLogistics().getRedemption(), activityCode));
		activityRCData.setLocalOperatorInformation(product.getSupplier().getName());
		activityRCData.setCancellationPolicyOverview(makeCancellationPolicy(product.getCancellationPolicy()));
		activityRCData.setCountryCode(""); 

		List<String> imageList = new ArrayList<String>();
		if (product.getImages() != null)
		{
			log.debug("processViatorProductJSON::processing " + product.getImages().size() + " images");
			for (Image productImage : product.getImages())
			{
				String imageUrl = null;
				int maxHeight = 0;
				for (ImageVariant variant : productImage.getVariants())
				{
					if (variant.getHeight().intValue() > maxHeight)
					{
						maxHeight = variant.getHeight();
						imageUrl = variant.getUrl();
					}
				}
				if (imageUrl != null)
				{
					log.debug("processViatorProductJSON::adding image " + imageUrl);
					imageList.add(imageUrl);
				}
			}
			StringWriter writer = new StringWriter();
			getObjectMapper().writeValue(writer, imageList);
			activityRCData.setImagesJson(writer.toString());
		}
		activityRCData.setLastUpdate(lastUpdatedAt);
		activityRCRepo.save(activityRCData);

		List<ViatorV2Activity> activityOptList = viatorV2ActivityRepo.findByProductCode(product.getProductCode());
		log.debug("processViatorProductJSON::product " + activityCode + " has " + activityOptList.size() + " existing records");
		ViatorV2Activity viatorSpecificData = null;
		if (activityOptList.size() == 1)
			viatorSpecificData = activityOptList.get(0);
		else
		{
			viatorSpecificData = new ViatorV2Activity();
			viatorSpecificData.setProductCode(product.getProductCode());
		}
		String bookingQuestions = StringUtils.collectionToCommaDelimitedString(product.getBookingQuestions());
		log.debug("processViatorProductJSON::for code " + viatorSpecificData.getProductCode() + " bookingQuestions(" + bookingQuestions.length() + ")=" + bookingQuestions);
		if (bookingQuestions.length() < 1000)
		{
			viatorSpecificData.setBookingQuestions(bookingQuestions);
		}
		else
		{
			log.warn("processViatorProductJSON::product " + product.getProductCode() + ": bookingQuestions(" + bookingQuestions.length() + ")=" + bookingQuestions);
		}
		if (product.getPricingInfo() != null && !product.getPricingInfo().getType().equals("PER_PERSON"))
		{
			log.warn("processViatorProductJSON::cannot handle pricing model " + product.getPricingInfo().getType() + " for product " + product.getProductCode());
			return;
		}
		for (AgeBand ageband : product.getPricingInfo().getAgeBands())
		{
			switch (ageband.getAgeBand())
			{
				case "INFANT":
					viatorSpecificData.setInfantMinAge(ageband.getStartAge());
					viatorSpecificData.setInfantMaxAge(ageband.getEndAge());
					viatorSpecificData.setInfantMinPax(ageband.getMinTravelersPerBooking());
					viatorSpecificData.setInfantMaxPax(ageband.getMaxTravelersPerBooking());
					break;
				case "CHILD":
					viatorSpecificData.setChildMinAge(ageband.getStartAge());
					viatorSpecificData.setChildMaxAge(ageband.getEndAge());
					viatorSpecificData.setChildMinPax(ageband.getMinTravelersPerBooking());
					viatorSpecificData.setChildMaxPax(ageband.getMaxTravelersPerBooking());
					break;
				case "YOUTH":
					viatorSpecificData.setYouthMinAge(ageband.getStartAge());
					viatorSpecificData.setYouthMaxAge(ageband.getEndAge());
					viatorSpecificData.setYouthMinPax(ageband.getMinTravelersPerBooking());
					viatorSpecificData.setYouthMaxPax(ageband.getMaxTravelersPerBooking());
					break;
				case "ADULT":
					viatorSpecificData.setAdultMinAge(ageband.getStartAge());
					viatorSpecificData.setAdultMaxAge(ageband.getEndAge());
					viatorSpecificData.setAdultMinPax(ageband.getMinTravelersPerBooking());
					viatorSpecificData.setAdultMaxPax(ageband.getMaxTravelersPerBooking());
					break;
				case "SENIOR":
					viatorSpecificData.setSeniorMinAge(ageband.getStartAge());
					viatorSpecificData.setSeniorMaxAge(ageband.getEndAge());
					viatorSpecificData.setSeniorMinPax(ageband.getMinTravelersPerBooking());
					viatorSpecificData.setSeniorMaxPax(ageband.getMaxTravelersPerBooking());
					break;
			}
		}
		viatorV2ActivityRepo.save(viatorSpecificData);
		if (product.getProductOptions() != null)
		{
			log.debug("processViatorProductJSON::product " + activityCode + " has json options:" + product.getProductOptions() + " and data options " + viatorSpecificData.getOptions());
			for (ProductOption productOption : product.getProductOptions())
			{
				boolean existing = false;
				for (ViatorV2ActivityProductOption existingOption : viatorSpecificData.getOptions())
				{
					log.debug("processViatorProductJSON::compare existing " + existingOption.getProductOptionCode() + " with json " + productOption.getProductOptionCode());
					if (existingOption.getProductOptionCode().equals(productOption.getProductOptionCode()))
					{
						existing = true;
						if (!existingOption.getDescription().equals(productOption.getDescription()))
						{
							existingOption.setDescription(productOption.getDescription());
							if ( existingOption.getDescription().length() > 1000)
								existingOption.setDescription(productOption.getDescription().substring(0,  999));
							if ( productOption.getLanguageGuides() != null )
							{
								for ( LanguageGuide languageGuide : productOption.getLanguageGuides())
								{
									if ( languageGuide.getLanguage().equals("en"))
									{
										existingOption.setLanguageType(languageGuide.getLanguage() + "/" + languageGuide.getType());
									}
								}
							}
							viatorV2ActivityProductOptionRepo.save(existingOption);
							log.debug("processViatorProductJSON::updating option " + existingOption.getProductOptionCode() + " for product " + activityCode);
						}
					}
				}
				if (!existing)
				{
					ViatorV2ActivityProductOption newOption = new ViatorV2ActivityProductOption();
					newOption.setActivity(viatorSpecificData);
					newOption.setDescription(productOption.getDescription());
					if ( newOption.getDescription().length() > 1000)
						newOption.setDescription(productOption.getDescription().substring(0,  999));
					newOption.setProductCode(viatorSpecificData.getProductCode());
					newOption.setProductOptionCode(productOption.getProductOptionCode());
					if ( productOption.getLanguageGuides() != null )
					{
						for ( LanguageGuide languageGuide : productOption.getLanguageGuides())
						{
							if ( languageGuide.getLanguage().equals("en"))
							{
								newOption.setLanguageType(languageGuide.getLanguage() + "/" + languageGuide.getType());
							}
						}
					}
					log.debug("processViatorProductJSON::adding option " + newOption.getProductOptionCode() + " for product " + activityCode);
					viatorV2ActivityProductOptionRepo.save(newOption);
				}
			}
		}
		else
		{
			log.debug("processViatorProductJSON::product " + activityCode + " has no options!");
		}
	}

	@Transactional
	public void processAvailability(AvailabilitySchedules.AvailabilitySchedule schedule, String productCode) throws Exception
	{
		log.debug("processAvailability::enter for " + productCode + ", schedule=" + schedule);
		String activityCode = ViatorV2Service.CHANNEL_PREFIX + productCode;
		ActivityRCData activityRCData = new ActivityRCData();
		activityRCData.setCode(activityCode);
		Optional<ActivityRCData> activityRCDataOpt = activityRCRepo.findById(activityCode);
		if (activityRCDataOpt.isPresent())
			activityRCData = activityRCDataOpt.get();

		log.debug("processAvailability::deleting all records for " + schedule.getProductCode());
		activityScheduleRepo.deleteByProductCode(schedule.getProductCode());
		activityUnavailableRepo.deleteByProductCode(schedule.getProductCode());
		ViatorV2Activity activity = new ViatorV2Activity();
		activity.setProductCode(schedule.getProductCode());
		log.debug("processAvailability::product " + productCode + " has " + schedule.getBookableItems().size() + " bookableItems");
		for (BookableItem bookableItem : schedule.getBookableItems())
		{
			ViatorV2ActivityProductOption activityOption = new ViatorV2ActivityProductOption();
			activityOption.setActivity(activity);
			activityOption.setProductCode(schedule.getProductCode());
			activityOption.setProductOptionCode(bookableItem.getProductOptionCode());
			activity.getOptions().add(activityOption);
			for (Season season : bookableItem.getSeasons())
			{
				log.debug("processAvailability::product " + productCode + " optionCode" + bookableItem.getProductOptionCode() + " has " + bookableItem.getSeasons().size() + " seasons");
				LocalDate seasonStart = season.getStartDate();
				LocalDate seasonEnd = season.getEndDate();
				if (seasonEnd == null)
					seasonEnd = seasonStart.plusYears(30);
				log.debug("processAvailability::product " + productCode + " optionCode" + bookableItem.getProductOptionCode() + " season " + seasonStart + " to " + seasonEnd + " has "
						+ season.getPricingRecords().size() + " pricing records");
				for (PricingRecord pricingRecord : season.getPricingRecords())
				{
					if (pricingRecord.getTimedEntries() == null)
					{
						TimedEntry timedEntry = new TimedEntry();
						processTimedEntry(activityRCData, schedule, bookableItem, pricingRecord, timedEntry, seasonStart, seasonEnd);
					}
					else
					{
						for (TimedEntry timedEntry : pricingRecord.getTimedEntries())
						{
							processTimedEntry(activityRCData, schedule, bookableItem, pricingRecord, timedEntry, seasonStart, seasonEnd);
						}
					}
				}
			}
		}
		log.debug("processAvailability::exit");
	}

	private void processTimedEntry(ActivityRCData activityRCData, AvailabilitySchedules.AvailabilitySchedule schedule, BookableItem bookableItem, PricingRecord pricingRecord, TimedEntry timedEntry,
			LocalDate seasonStart, LocalDate seasonEnd)
	{
		log.debug("processTimedEntry::enter");
		ViatorV2UnavailableData unavailableData = new ViatorV2UnavailableData();
		unavailableData.setProductCode(schedule.getProductCode());
		if (bookableItem.getProductOptionCode() == null || bookableItem.getProductOptionCode().length() == 0)
			unavailableData.setProductOptionCode("DEFAULT");
		else
			unavailableData.setProductOptionCode(bookableItem.getProductOptionCode());
		if (timedEntry.getStartTime() != null)
			unavailableData.setTime(LocalTime.parse(timedEntry.getStartTime(), timeFormatterHHMM));
		else
			unavailableData.setTime(ViatorV2Service.NO_TIME);
		StringBuffer unavailableBuf = new StringBuffer();
		if (timedEntry.getUnavailableDates() != null)
		{
			log.debug("processTimedEntry::has " + timedEntry.getUnavailableDates().size() + " unavailable dates");
			for (UnavailableDate unavailable : timedEntry.getUnavailableDates())
			{
				if (unavailableBuf.length() > 0)
					unavailableBuf.append(":");
				unavailableBuf.append(unavailable.getDate().format(yyyymmdd));
			}
		}
		else if (pricingRecord.getUnavailableDates() != null)
		{
			log.debug("processTimedEntry::no timed entry, looking for pricingRecord unavailables");
			for (UnavailableDate unavailable : pricingRecord.getUnavailableDates())
			{
				if (unavailableBuf.length() > 0)
					unavailableBuf.append(":");
				unavailableBuf.append(unavailable.getDate().format(yyyymmdd));
			}
		}
		if ( unavailableBuf.length() > 0 )
		{
			log.debug("processTimedEntry::saving unavailable");
			unavailableData.setUnavailable(unavailableBuf.toString());
			activityUnavailableRepo.save(unavailableData);
		}
		else
		{
			log.debug("processTimedEntry::nothing unavailable!");
		}

		//Need refactor
		for (PricingDetail pricingEntry : pricingRecord.getPricingDetails())
		{
			log.debug("processTimedEntry::processing pricingEntry:" + pricingEntry.toString());
			if (!pricingEntry.getPricingPackageType().equals("PER_PERSON"))
			{
				log.warn("processTimedEntry::unrecognised PricingPackageType:" + pricingEntry.getPricingPackageType());
				continue;
			}
			ViatorV2ScheduleData scheduleData = makeScheduleData(activityRCData, schedule, bookableItem, pricingRecord, timedEntry, seasonStart, seasonEnd);
			scheduleData.setId(null);
			if (pricingEntry.getMinTravelers() != null)
				scheduleData.setMinTravelers(pricingEntry.getMinTravelers());
			if (pricingEntry.getMaxTravelers() != null)
				scheduleData.setMaxTravelers(pricingEntry.getMaxTravelers());
			scheduleData.setAgeBand(pricingEntry.getAgeBand());
			scheduleData.setPriceNet(pricingEntry.getPrice().getOriginal().getPartnerTotalPrice());
			scheduleData.setPriceRrp(pricingEntry.getPrice().getOriginal().getRecommendedRetailPrice());
			scheduleData = activityScheduleRepo.save(scheduleData);
			log.debug("processTimedEntry::saved scheduleData:" + scheduleData);
			if (pricingEntry.getPrice().getSpecial() != null)
			{
				scheduleData = makeScheduleData(activityRCData, schedule, bookableItem, pricingRecord, timedEntry, seasonStart, seasonEnd);
				scheduleData.setStartDate(pricingEntry.getPrice().getSpecial().getOfferStartDate());
				scheduleData.setEndDate(pricingEntry.getPrice().getSpecial().getOfferEndDate());
				scheduleData.setSpecial(true);
				scheduleData.setPriceNet(pricingEntry.getPrice().getSpecial().getPartnerTotalPrice());
				scheduleData.setPriceRrp(pricingEntry.getPrice().getSpecial().getRecommendedRetailPrice());
				scheduleData = activityScheduleRepo.save(scheduleData);
				log.debug("processTimedEntry::saved scheduleData:" + scheduleData);
			}
		}
	}

	private ViatorV2ScheduleData makeScheduleData(ActivityRCData activityRCData, AvailabilitySchedules.AvailabilitySchedule schedule, BookableItem bookableItem, PricingRecord pricingRecord, TimedEntry timedEntry,
			LocalDate seasonStart, LocalDate seasonEnd)
	{
		ViatorV2ScheduleData scheduleData = new ViatorV2ScheduleData();
		scheduleData.setProductCode(schedule.getProductCode());
		if (bookableItem.getProductOptionCode() == null || bookableItem.getProductOptionCode().length() == 0)
			scheduleData.setProductOptionCode("DEFAULT");
		else
			scheduleData.setProductOptionCode(bookableItem.getProductOptionCode());
		scheduleData.setStartDate(seasonStart);
		scheduleData.setEndDate(seasonEnd);
		scheduleData.setCurrencyId(schedule.getCurrency());
		if (activityRCData.getGeoCoordinates() != null)
		{
			scheduleData.setLatitude(activityRCData.getGeoCoordinates().getLatitude());
			scheduleData.setLongitude(activityRCData.getGeoCoordinates().getLongitude());
		}
		else
		{
			log.warn("processTimedEntry::code " + activityRCData.getCode() + " has no latlongs");
		}
		if (pricingRecord.getDaysOfWeek().contains("MONDAY"))
			scheduleData.setMonday(true);
		if (pricingRecord.getDaysOfWeek().contains("TUESDAY"))
			scheduleData.setTuesday(true);
		if (pricingRecord.getDaysOfWeek().contains("WEDNESDAY"))
			scheduleData.setWednesday(true);
		if (pricingRecord.getDaysOfWeek().contains("THURSDAY"))
			scheduleData.setThursday(true);
		if (pricingRecord.getDaysOfWeek().contains("FRIDAY"))
			scheduleData.setFriday(true);
		if (pricingRecord.getDaysOfWeek().contains("SATURDAY"))
			scheduleData.setSaturday(true);
		if (pricingRecord.getDaysOfWeek().contains("SUNDAY"))
			scheduleData.setSunday(true);
		if (timedEntry.getStartTime() != null)
			scheduleData.setTime(LocalTime.parse(timedEntry.getStartTime(), timeFormatterHHMM));
		else
			scheduleData.setTime(ViatorV2Service.NO_TIME);
		return scheduleData;
	}
	
	private static Map<Integer, DestinationData> _destinations = new HashMap<>();;

	private DestinationData getDestination(Integer destinationId)
	{
		if (_destinations.get(destinationId) == null)
		{
			Optional<DestinationData> destinationOpt = destinationRepo.findById(destinationId);
			if (destinationOpt.isPresent())
			{
				_destinations.put(destinationId, destinationOpt.get());
			}
		}
		return _destinations.get(destinationId);
	}

	private String makeItineraryText(Itinerary itinerary)
	{
		StringBuffer text = new StringBuffer();
		// TODO BIG todo!
		switch (itinerary.getItineraryType())
		{
			// TODO parse itinerary differently for eachtype
			case "HOP_ON_HOP_OFF":
				text.append("Hop-n hop-off.");
				break;
			case "STANDARD":
				break;
			case "ACTIVITY":
				break;
			case "MULTI_DAY_TOUR":
				break;
			case "UNSTRUCTURED":
				break;
			default:
				log.debug("Unhandled itineraryType:" + itinerary.getItineraryType());
		}
		if (itinerary.getPrivateTour() != null && itinerary.getPrivateTour())
		{
			if (text.length() > 0)
				text.append(". ");
			text.append("Private Tour.");
		}
		if (itinerary.getSkipTheLine() != null && itinerary.getSkipTheLine())
		{
			if (text.length() > 0)
				text.append(". ");
			text.append("Skip The Line.");
		}
		if (itinerary.getItineraryItems() != null)
		{
			for (ItineraryItem item : itinerary.getItineraryItems())
			{}
		}
		return text.toString();
	}

	private String makeDeparturePointText(TravelerPickup travelerPickup, String productCode)
	{
		StringBuffer text = new StringBuffer();
		switch (travelerPickup.getPickupOptionType())
		{
			case "PICKUP_AND_MEET_AT_START_POINT":
				text.append("Pickup, or meet at the starting point.");
				break;
			case "MEET_EVERYONE_AT_START_POINT":
				text.append("Meet at the starting point.");
				break;
			case "PICKUP_EVERYONE":
				text.append("Tour will pickup each traveller.");
				break;
			default:
				log.debug("Unhandled pickupOptionType:" + travelerPickup.getPickupOptionType());
		}
		if (travelerPickup.getAdditionalInfo() != null && travelerPickup.getAdditionalInfo().length() > 0)
		{
			text.append(" ");
			text.append(travelerPickup.getAdditionalInfo());
		}
		if ( text.toString().length() > 1040)
		{
			log.info("makeDeparturePointText::truncated long departurePointText from " + text.toString().length() + " for product " + productCode);
			return text.toString().substring(0, 1040);
		}
		else
			return text.toString().trim();
	}

	private String makeVoucherInformation(TicketInfo ticketInfo, Redemption redemption, String productCode)
	{
		StringBuffer text = new StringBuffer();
		if (redemption != null)
		{
			log.debug("VOUCHER INFO: redemption=" + redemption.toString());
			switch (redemption.getRedemptionType())
			{
				// TODO describe
				case "DIFFERENT_LOCATION":
					break;
				case "INDIRECT_DELIVERY":
					break;
				case "ATTRACTION_START_POINT":
					break;
				case "NONE":
					break;
				default:
					log.debug("Unhandled RedemptionType:" + redemption.getRedemptionType());
			}
			if (redemption.getSpecialInstructions() != null && redemption.getSpecialInstructions().length() > 0)
			{
				if (text.length() > 0)
					text.append(". ");
				text.append(redemption.getSpecialInstructions());
			}
		}
		if (ticketInfo != null)
		{
			log.debug("VOUCHER INFO: ticketInfo=" + ticketInfo.toString());
			if (ticketInfo.getTicketTypeDescription() != null && ticketInfo.getTicketTypeDescription().length() > 0)
			{
				if (text.length() > 0)
					text.append(". ");
				text.append(ticketInfo.getTicketTypeDescription());
			}
		}
		if ( text.toString().length() > 2020)
		{
			log.info("makeVoucherInformation::truncated long voucherInfo from " + text.toString().length() + " for product " + productCode);
			return text.toString().substring(0, 2020);
		}
		else
			return text.toString();
	}

	private String makeCancellationPolicy(CancellationPolicy cancellationPolicy)
	{
		StringBuffer text = new StringBuffer();
		switch (cancellationPolicy.getType())
		{
			// TODO parse
			case "STANDARD":
				text.append("Refundable.");
				break;
			case "CUSTOM":
				break;
			case "ALL_SALES_FINAL":
				text.append("ALl sales final and non-refundable.");
				break;
			default:
				log.debug("Unhandled CancellationPolicyType:" + cancellationPolicy.getType());
		}
		if (cancellationPolicy.getDescription() != null && cancellationPolicy.getDescription().length() > 0)
		{
			if (text.length() > 0)
				text.append(". ");
			text.append(cancellationPolicy.getDescription());
		}
		if (cancellationPolicy.getCancelIfBadWeather() != null && cancellationPolicy.getCancelIfBadWeather())
		{
			if (text.length() > 0)
				text.append(". ");
			text.append("May be cancelled by the operator for bad weather.");
		}
		if (cancellationPolicy.getCancelIfInsufficientTravelers() != null && cancellationPolicy.getCancelIfInsufficientTravelers())
		{
			if (text.length() > 0)
				text.append(". ");
			text.append("May be cancelled by the operator if there are insufficient travellers.");
		}
		return text.toString();
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

	private static DateTimeFormatter yyyymmdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static ObjectMapper _objectMapper = null;

	private static final DateTimeFormatter timeFormatterHHMM = DateTimeFormatter.ofPattern("HH:mm");
}
