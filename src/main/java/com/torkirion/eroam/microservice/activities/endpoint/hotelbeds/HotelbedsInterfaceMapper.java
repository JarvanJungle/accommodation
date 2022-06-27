package com.torkirion.eroam.microservice.activities.endpoint.hotelbeds;

import com.hotelbeds.activities.model.*;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.activities.apidomain.*;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ.ActivityRequestItem;
import com.torkirion.eroam.microservice.activities.apidomain.BookingQuestion;
import com.torkirion.eroam.microservice.activities.apidomain.BookingQuestion.QuestionType;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByActivityIdRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.activities.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.activities.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.apidomain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class HotelbedsInterfaceMapper
{
	public static AvailabilitybyhotelcodeRequest makeAvailabilityRQ(AvailSearchByGeocordBoxRQDTO availSearchRQ)
	{
		log.debug("makeAvailabilityRQ::entering for availSearchRQ=" + availSearchRQ);
		AvailabilitybyhotelcodeRequest request = new AvailabilitybyhotelRQBuilder().builder().dateRange(availSearchRQ.getActivityDateFrom(), availSearchRQ.getActivityDateTo()).page(100, 1)
				.boundingBox(availSearchRQ.getNorthwest(), availSearchRQ.getSoutheast()).travellers(availSearchRQ.getTravellers()).build();
		return request;
	}

	public static AvailabilitybyhotelcodeRequest makeAvailabilityRQ(AvailSearchByActivityIdRQDTO availSearchRQ)
	{
		log.debug("makeAvailabilityRQ::entering for availSearchRQ=" + availSearchRQ);
		AvailabilitybyhotelcodeRequest request = new AvailabilitybyhotelRQBuilder().builder().dateRange(availSearchRQ.getActivityDateFrom(), availSearchRQ.getActivityDateTo()).page(100, 1)
				.activityIdsSelected(availSearchRQ.getActivityIds()).travellers(availSearchRQ.getTravellers()).build();
		return request;
	}

	public static AvailabilitybyhotelcodeRequest makeAvailabilityRQ(RateCheckRQDTO rateCheckRQ)
	{
		log.debug("makeAvailabilityRQ::entering for availSearchRQ= {}", rateCheckRQ);
		AvailabilitybyhotelcodeRequest request = new AvailabilitybyhotelRQBuilder().builder().dateRange(rateCheckRQ.getActivityDate(), rateCheckRQ.getActivityDate()).page(100, 1)
				.activityIdsSelected(Set.of(rateCheckRQ.getActivityId())).travellers(rateCheckRQ.getTravellers()).build();
		return request;
	}

	public static DetailSimpleRequest makeDetailRQ(RateCheckRQDTO rateCheckRQ)
	{
		log.debug("makeDetailRQ::entering for rateCheckRQ=" + rateCheckRQ);
		DetailSimpleRequest request = new DetailSimpleRQBuilder().builder().dateRange(rateCheckRQ.getActivityDate(), rateCheckRQ.getActivityDate()).activityIdSelected(rateCheckRQ.getActivityId())
				.travellers(rateCheckRQ.getTravellers()).build();
		return request;
	}

	public static List<DetailSimpleRequest> makeDetailRQ(ActivityBookRQ bookRQ)
	{
		log.debug("makeDetailRQ::entering for bookRQ=" + bookRQ);
		List<DetailSimpleRequest> requests = new ArrayList<>();
		for ( ActivityRequestItem item : bookRQ.getItems())
		{
			DetailSimpleRequest request = new DetailSimpleRQBuilder().builder().dateRange(item.getDate(), item.getDate()).activityIdSelected(item.getActivityId())
					.travellers(item, bookRQ.getTravellers()).build();
			requests.add(request);
		}
		return requests;
	}

	public static class AvailabilitybyhotelRQBuilder
	{

		private AvailabilitybyhotelcodeRequest request;

		public AvailabilitybyhotelRQBuilder builder()
		{
			request = new AvailabilitybyhotelcodeRequest();
			request.setLanguage("en");
			request.setFilters(new ArrayList<>());
			request.setPaxes(new ArrayList<>());
			return this;
		}

		public AvailabilitybyhotelRQBuilder dateRange(LocalDate from, LocalDate to)
		{
			request.setFrom(df2YYYYMMDD.format(from));
			//if ( from.equals(to))
			//	request.setTo(df2YYYYMMDD.format(to.plusDays(5)));
			//else
				request.setTo(df2YYYYMMDD.format(to));
			return this;
		}

		public AvailabilitybyhotelRQBuilder page(int itemsPerPage, int page)
		{
			request.setPagination(new Pagination());
			request.getPagination().setItemsPerPage(itemsPerPage);
			request.getPagination().setPage(page);
			return this;
		}

		public AvailabilitybyhotelRQBuilder boundingBox(LatitudeLongitude northwest, LatitudeLongitude southeast)
		{
			BigDecimal latitude = northwest.getLatitude().add(southeast.getLatitude()).divide(NUMBER_2);
			BigDecimal longitude = northwest.getLongitude().add(southeast.getLongitude()).divide(NUMBER_2);
			latitude = latitude.multiply(NUMBER_2).setScale(0, RoundingMode.HALF_UP).divide(NUMBER_2, 1, RoundingMode.HALF_UP);
			longitude = longitude.multiply(NUMBER_2).setScale(0, RoundingMode.HALF_UP).divide(NUMBER_2, 1, RoundingMode.HALF_UP);
			SearchFilterItem searchFilterItem = new SearchFilterItem();
			searchFilterItem.setType("gps");
			searchFilterItem.setLatitude(latitude.toString());
			searchFilterItem.setLongitude(longitude.toString());
			List<SearchFilterItem> searchFilterItems = List.of(searchFilterItem);
			Filter filter = new Filter();
			filter.setSearchFilterItems(searchFilterItems);
			request.getFilters().add(filter);
			return this;
		}

		public AvailabilitybyhotelRQBuilder travellers(TravellerMix travellers)
		{
			Integer adultCount = travellers.getAdultCount();
			List<Integer> childAges = travellers.getChildAges();
			if (adultCount > 0)
			{
				List<Pax> adultPaxes = Collections.nCopies(adultCount, PAX_30_YEAR_OLD);
				request.getPaxes().addAll(adultPaxes);
			}
			if (!CollectionUtils.isEmpty(childAges))
			{
				List<Pax> childPaxes = childAges.stream().filter(a -> a != 0).map(a -> new Pax().age(a)).collect(Collectors.toList());
				request.getPaxes().addAll(childPaxes);
			}
			return this;
		}

		public AvailabilitybyhotelRQBuilder activityIdsSelected(Set<String> activityIds)
		{
			if (activityIds == null || activityIds.isEmpty())
			{
				return this;
			}
			List<SearchFilterItem> searchFilterItems = activityIds.stream().map(aId -> {
				SearchFilterItem item = new SearchFilterItem();
				item.setType("service");
				item.setValue(getActivityCodeByActivityId(aId));
				return item;
			}).collect(Collectors.toList());
			Filter filter = new Filter();
			filter.setSearchFilterItems(searchFilterItems);
			request.getFilters().add(filter);
			return this;
		}

		public AvailabilitybyhotelcodeRequest build()
		{
			return request;
		}
	}

	public static class DetailSimpleRQBuilder
	{

		private DetailSimpleRequest request;

		public DetailSimpleRQBuilder builder()
		{
			request = new DetailSimpleRequest();
			request.setLanguage("en");
			request.setPaxes(new ArrayList<>());
			return this;
		}

		public DetailSimpleRQBuilder dateRange(LocalDate from, LocalDate to)
		{
			request.setFrom(df2YYYYMMDD.format(from));
			request.setTo(df2YYYYMMDD.format(to));
			return this;
		}

		public DetailSimpleRQBuilder travellers(TravellerMix travellers)
		{
			Integer adultCount = travellers.getAdultCount();
			List<Integer> childAges = travellers.getChildAges();
			if (adultCount > 0)
			{
				List<Pax> adultPaxes = Collections.nCopies(adultCount, PAX_30_YEAR_OLD);
				request.getPaxes().addAll(adultPaxes);
			}
			if (!CollectionUtils.isEmpty(childAges))
			{
				List<Pax> childPaxes = childAges.stream().filter(a -> a != 0).map(a -> new Pax().age(a)).collect(Collectors.toList());
				request.getPaxes().addAll(childPaxes);
			}
			return this;
		}

		public DetailSimpleRQBuilder travellers(ActivityRequestItem item, List<Traveller> travellers)
		{
			for ( Integer travellerIdx : item.getTravellerIndex())
			{
				Pax pax = new Pax().age(travellers.get(travellerIdx).getAge(LocalDate.now()));
				request.getPaxes().add(pax);
			}
			return this;
		}

		public DetailSimpleRQBuilder activityIdSelected(String activityId)
		{
			if (activityId == null || activityId.length() == 0)
			{
				return this;
			}
			request.setCode(getActivityCodeByActivityId(activityId));
			return this;
		}

		public DetailSimpleRequest build()
		{
			return request;
		}
	}

	/*---------------------------------------------------------------------------------------------------*/

	public static Set<ActivityResult> makeActivityResults(List<Activity> activities, AvailSearchRQDTO availSearchRQ) throws Exception
	{
		if (CollectionUtils.isEmpty(activities))
		{
			return Collections.EMPTY_SET;
		}
		boolean sameDayFilter = false;
		if ( availSearchRQ.getActivityDateFrom() != null && availSearchRQ.getActivityDateTo() != null && availSearchRQ.getActivityDateFrom().equals(availSearchRQ.getActivityDateTo()))
			sameDayFilter = true;
		Set<ActivityResult> results = new HashSet<>();
		for (Activity activity : activities)
		{
			ActivityResult result = new ActivityResult();
			if (activity.getContent() == null)
			{
				log.debug("makeActivityResults::content is null for " + activity.getCode() + " " + activity.getName());
				continue;
			}
			result.setActivityRC(makeActivityRC(activity, availSearchRQ));
			result.setDepartures(makeActivityDepartures(activity.getModalities(), availSearchRQ));
			results.add(result);
		}
		return results;
	}

	public static ActivityResult makeActivityResult(Activity activity, AvailSearchRQDTO availSearchRQ) throws Exception
	{
		if (activity == null)
		{
			log.debug("makeActivityResult::activity is null");
			return null;
		}
		ActivityResult result = new ActivityResult();
		if (activity.getContent() == null)
		{
			log.debug("makeActivityResult::content is null for " + activity.getCode() + " " + activity.getName());
			return null;
		}
		result.setActivityRC(makeActivityRC(activity, availSearchRQ));
		result.setDepartures(makeActivityDepartures(activity.getModalities(), availSearchRQ));
		return result;
	}

	private static ActivityRC makeActivityRC(Activity activity, AvailSearchRQDTO availSearchRQ) throws Exception
	{
		/*----------------------------------------*/
		Content hbContent = activity.getContent();
		ActivityRC rc = new ActivityRC();
		rc.setProductType(activity.getType());
		rc.setCountryCode(activity.getCountry().getCode());
		// rc.setOperator(activity.getSource());
		rc.setActivityName(activity.getName());
		rc.setSupplierName("Hotelbeds");

		rc.setCode(HotelbedsService.CHANNEL_PREFIX + activity.getCode());
		rc.setChannelCode(activity.getCode());
		/*----------------------------------------*/
		rc.setChannel(com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelbedsService.CHANNEL);
		rc.setCategories(makeCategories(hbContent));

		/*--set for starting point-*/
		List<ContentLocationStartingPoints> startingPoints = getStartingPointsFromContent(hbContent);
		rc.setGeoCoordinates(makeGeoCoordinates(startingPoints));
		rc.setDeparturePoint(makeDeparturePoint(startingPoints));
		rc.setHotelPickupAvailable(makeHotelPickupAvailable(startingPoints));
		/*-----------------------*/

		rc.setAdditionalInformation(stripOutHtml(makeAdditionalInformation(hbContent)));
		rc.setVoucherInformation(stripOutHtml(makeVoucherInformation(hbContent)));
		rc.setOverview(stripOutHtml(hbContent.getDescription()));
		rc.setItineraryHighlights(stripOutHtml(getHighLights(hbContent)));
		// TODO ADD ROUTE HERE??

		try
		{
			rc.setLastUpdate(LocalDate.parse(hbContent.getLastUpdate(), df2YYYYMMDD));
		}
		catch (DateTimeParseException e)
		{
			log.error("parse datetime: {}", e.getMessage());
		}
		rc.setImages(makeActivityRCImages(hbContent));
		/*-------------------------------------------------------------------*/
		rc.setFromPerPerson(getFromPerPerson(activity));
		String durationText = makeDurationTextFromModalities(activity);
		log.debug("makeActivityRC::get durationText from Modalities value: {}", durationText);
		if (durationText == null)
		{
			durationText = makeDurationTextFromContentRoute(activity);
			log.debug("makeActivityRC::get durationText from Content Route value: {}", durationText);
		}
		rc.setDurationText(durationText);
		rc.setDuration(convertTextDurationToIso(durationText));
		rc.setInclusions(stripOutHtml(makeInclusions(hbContent)));
		rc.setExclusions(stripOutHtml(makeExclusions(hbContent)));

		rc.setOperator(makeOperator(hbContent));
		return rc;
	}

	private static List<String> makeCategories(Content hbContent)
	{
		List<String> categories = new ArrayList<>();
		if (hbContent == null || hbContent.getSegmentationGroups() == null)
		{
			return categories;
		}
		Optional<ContentSegmentationGroups> categoriesCgOptional = hbContent.getSegmentationGroups().stream().filter(sg -> "Categories".equals(sg.getName())).findFirst();
		if (categoriesCgOptional.isEmpty())
		{
			return categories;
		}
		ContentSegmentationGroups categoriesCg = categoriesCgOptional.get();
		if (categoriesCg.getSegments() == null || categoriesCg.getSegments().isEmpty())
		{
			return categories;
		}
		return categoriesCg.getSegments().stream().map(s -> s.getName()).collect(Collectors.toList());
	}

	private static String makeOperator(Content hbContent)
	{
		if (hbContent == null || hbContent.getSegmentationGroups() == null)
		{
			return "";
		}
		Optional<ContentSegmentationGroups> supplierCgOptional = hbContent.getSegmentationGroups().stream().filter(sg -> "Supplier".equals(sg.getName())).findFirst();
		if (supplierCgOptional.isEmpty())
		{
			return "";
		}
		ContentSegmentationGroups supplierCg = supplierCgOptional.get();
		if (supplierCg.getSegments() == null || supplierCg.getSegments().isEmpty())
		{
			return "";
		}
		return supplierCg.getSegments().stream().map(s -> s.getName()).collect(Collectors.joining(", "));
	}

	private static String makeVoucherInformation(Content content)
	{
		if (content.getRedeemInfo() == null || content.getRedeemInfo().getComments() == null)
		{
			return "";
		}
		return content.getRedeemInfo().getComments().stream().filter(c -> !"".equals((c.getDescription()))).map(c -> c.getDescription()).collect(Collectors.joining(STRING_JOIN_CHARACTER));
	}

	private static String makeAdditionalInformation(Content content)
	{
		List<ContentNotes> hbNotes = content.getNotes();
		String notesStr = "";
		if (!CollectionUtils.isEmpty(hbNotes))
		{
			String notesDescStr = hbNotes.stream().filter(note -> !CollectionUtils.isEmpty(note.getDescriptions())).flatMap(note -> note.getDescriptions().stream())
					.map(noteDesc -> noteDesc.getDescription()).collect(Collectors.joining(STRING_JOIN_CHARACTER));
			if (!"".equals(notesDescStr))
			{
				notesStr = "Notes: " + notesDescStr;
			}
		}
		String schedulingStr = serializeScheduling(content);
		if ("".equals(schedulingStr))
		{
			return notesStr;
		}
		if ("".equals(notesStr))
		{
			return schedulingStr;
		}
		// TODO separate notesStr,schedulingStr
		return notesStr + schedulingStr;
	}

	private static String serializeScheduling(Content content)
	{
		ContentScheduling Scheduling = content.getScheduling();
		if (Scheduling == null)
		{
			return "";
		}
		List<ContentSchedulingOpened> openeds = Scheduling.getOpened();
		if (CollectionUtils.isEmpty(openeds))
		{
			return "";
		}
		String schedulingStr = "Scheduling: ";
		String opnedsStr = openeds.stream().map(op -> {
			List<String> openClose = new ArrayList<>();
			if (!"".equals(op.getOpeningTime()))
			{
				openClose.add("Opening time: " + op.getOpeningTime());
			}
			if (!"".equals(op.getCloseTime()))
			{
				openClose.add("Close time: " + op.getCloseTime());
			}
			return openClose.stream().collect(Collectors.joining(" - "));
		}).collect(Collectors.joining(", "));
		if ("".equals(opnedsStr))
		{
			return "";
		}
		return schedulingStr + opnedsStr;
	}

	private static List<ActivityRC.Image> makeActivityRCImages(Content hbContent)
	{
		List<ActivityRC.Image> imagesOutput = new ArrayList<>();
		if (hbContent.getMedia() == null)
		{
			return imagesOutput;
		}
		List<ContentMediaImages> imagesInput = hbContent.getMedia().getImages();
		if (imagesInput != null && !imagesInput.isEmpty())
		{
			for (ContentMediaImages hbImage : hbContent.getMedia().getImages())
			{
				ActivityRC.Image image = new ActivityRC.Image();
				Optional<ContentMediaUrls> imgOptional = hbImage.getUrls().stream().filter(url -> "LARGE".equals(url.getSizeType())).findFirst();
				if (!imgOptional.isEmpty())
				{
					image.setImageURL(imgOptional.get().getResource());
					imagesOutput.add(image);
				}
			}
		}
		return imagesOutput;
	}

	private static String makeInclusions(Content content)
	{
		List<ContentFeatureGroups> featureGroups = content.getFeatureGroups();
		if (CollectionUtils.isEmpty(featureGroups))
		{
			return "";
		}
		String inclusions = featureGroups.stream().filter(fg -> !CollectionUtils.isEmpty(fg.getIncluded())).flatMap(fg -> fg.getIncluded().stream()).filter(f -> !"".equals(f.getDescription()))
				.map(f -> f.getDescription()).collect(Collectors.joining(STRING_JOIN_CHARACTER));
		log.debug("makeInclusions::inclusions:");
		log.debug("\n" + inclusions);
		return inclusions;
	}

	private static String makeExclusions(Content content)
	{
		List<ContentFeatureGroups> featureGroups = content.getFeatureGroups();
		if (CollectionUtils.isEmpty(featureGroups))
		{
			return "";
		}
		String exclusions = featureGroups.stream().filter(fg -> !CollectionUtils.isEmpty(fg.getExcluded())).flatMap(fg -> fg.getExcluded().stream()).filter(f -> !"".equals(f.getDescription()))
				.map(f -> f.getDescription()).collect(Collectors.joining(STRING_JOIN_CHARACTER));
		log.debug("makeExclusions::exclusions:");
		log.debug("\n" + exclusions);
		return exclusions;
	}

	private static String convertTextDurationToIso(String textDuration)
	{
		if (textDuration == null)
		{
			return null;
		}
		String[] s = textDuration.split(" "); // the first is the duration values, the second is metric
		if (s.length < 2)
		{
			return s[0];
		}
		String metric = s[1].toUpperCase();
		/* check that is hour, minute, second */
		if (metric.startsWith("H") || metric.startsWith("MI") || metric.startsWith("SE"))
		{
			return "PT" + s[0] + metric.charAt(0);
		}
		return "P" + s[0] + metric.charAt(0);
	}

	private static String makeDurationTextFromModalities(Activity activities)
	{
		if (CollectionUtils.isEmpty(activities.getModalities()) || activities.getModalities().get(0) == null)
		{
			return null;
		}
		ActivityModalities firstModality = activities.getModalities().get(0);
		String metric = firstModality.getDuration().getMetric();
		if (metric == null || metric.isEmpty())
		{
			return "" + firstModality.getDuration().getValue();
		}
		metric = metric.toLowerCase();
		if (firstModality.getDuration().getValue() < 2)
		{
			StringBuffer sb = new StringBuffer(metric);
			return "1 " + sb.deleteCharAt(sb.length() - 1);
		}
		return firstModality.getDuration().getValue() + " " + metric;
	}

	private static String makeDurationTextFromContentRoute(Activity activities)
	{
		if (activities.getContent() == null || CollectionUtils.isEmpty(activities.getContent().getRoutes()))
			return null;
		RouteDuration routeDuration = activities.getContent().getRoutes().get(0).getDuration();
		String metric = routeDuration.getMetric();
		if ("".equals(metric))
		{
			return "" + routeDuration.getValue();
		}
		if (routeDuration.getValue() < 2)
		{
			StringBuffer sb = new StringBuffer(metric);
			return "1 " + sb.deleteCharAt(sb.length() - 1);
		}
		return routeDuration.getValue() + " " + metric;
	}

	private static CurrencyValue getFromPerPerson(Activity activities)
	{
		CurrencyValue currencyValue = new CurrencyValue();
		currencyValue.setCurrencyId(activities.getCurrency());
		if (CollectionUtils.isEmpty(activities.getAmountsFrom()))
		{
			return currencyValue;
		}
		Optional<ActivityAmountsFrom> optional = activities.getAmountsFrom().stream().filter(amount -> amount.getAgeTo() >= AGE_30_YEARS_OLD).min(Comparator.comparing(ActivityAmountsFrom::getAmount));
		if (!optional.isEmpty())
		{
			currencyValue.setAmount(optional.get().getAmount());
		}
		return currencyValue;
	}

	private static String getHighLights(Content content)
	{
		if (CollectionUtils.isEmpty(content.getHighligths()))
		{
			return "";
		}
		return content.getHighligths().stream().collect(Collectors.joining(STRING_JOIN_CHARACTER));
	}

	/*-- start mapping for departure --*/
	private static SortedSet<ActivityDeparture> makeActivityDepartures(List<ActivityModalities> modalities, AvailSearchRQDTO availSearchRQ)
	{
		SortedSet<ActivityDeparture> departures = new TreeSet<>();
		if (CollectionUtils.isEmpty(modalities))
		{
			return departures;
		}
		// get date from first modality
		List<LocalDate> departureDates = getListOfDepartureDateInModality(modalities.get(0));
		if (log.isDebugEnabled())
			log.debug("makeActivityDepartures: (departureDates: {})", departureDates);
		Map<LocalDate, SortedSet<ActivityOption>> departureDateAndActivityOptionMap = getMapOfDepartureDateAndActivityOptionSet(modalities);
		for (LocalDate departureDate : departureDates)
		{
			ActivityDeparture departure = new ActivityDeparture();
			departure.setDepartureId(DEPARTURE_IT_DEFAULT);
			departure.setDepartureTime(DEPARTURE_TIME_DEFAULT);
			departure.setDate(departureDate);
			departure.setDepartureName(DEPARTURE_NAME_DEFAULT);
			departure.setOptions(departureDateAndActivityOptionMap.get(departureDate));
			departures.add(departure);
		}
		return departures;
	}

	private static List<LocalDate> getListOfDepartureDateInModality(ActivityModalities modality)
	{
		List<ActivityRates> rates = modality.getRates();
		if (CollectionUtils.isEmpty(rates))
		{
			return Collections.EMPTY_LIST;
		}
		List<ActivityRateDetails> firstRateDetails = rates.get(0).getRateDetails();
		if (CollectionUtils.isEmpty(firstRateDetails))
		{
			return Collections.EMPTY_LIST;
		}
		List<ActivityOperationDates> operationDates = firstRateDetails.get(0).getOperationDates();
		if (CollectionUtils.isEmpty(operationDates))
		{
			return Collections.EMPTY_LIST;
		}
		return operationDates.stream().map(opD -> LocalDate.parse(opD.getFrom(), df2YYYYMMDD)).collect(Collectors.toList());
	}

	private static Map<LocalDate, SortedSet<ActivityOption>> getMapOfDepartureDateAndActivityOptionSet(List<ActivityModalities> modalities)
	{
		Map<LocalDate, SortedSet<ActivityOption>> departureDateAndOptionsMap = new HashMap<>();
		for (ActivityModalities modality : modalities)
		{
			mapModalityToOption(modality, departureDateAndOptionsMap);
		}
		/*--update bundlesOnly--*/
		departureDateAndOptionsMap.values().forEach(optionSet -> {
			if (optionSet.size() > 1)
				optionSet.forEach(option -> option.setBundlesOnly(false));
		});
		/*---------------------*/
		return departureDateAndOptionsMap;
	}

	private static void mapModalityToOption(ActivityModalities modality, Map<LocalDate, SortedSet<ActivityOption>> departureDateAndOptionsMap)
	{
		List<ActivityRates> rates = modality.getRates();
		for (ActivityRates rate : rates)
		{
			mapModalityAndRateToOption(modality, rate, departureDateAndOptionsMap);
		}
	}

	private static void mapModalityAndRateToOption(ActivityModalities modality, ActivityRates rate, Map<LocalDate, SortedSet<ActivityOption>> departureDateAndOptionsMap)
	{

		List<ActivityRateDetails> rateDetails = rate.getRateDetails();
		for (ActivityRateDetails rateDetail : rateDetails)
		{
			mapModalityAndRateAndRateDetailToOption(modality, rate, rateDetail, departureDateAndOptionsMap);
		}
	}

	private static void mapModalityAndRateAndRateDetailToOption(ActivityModalities modality, ActivityRates rate, ActivityRateDetails rateDetail,
			Map<LocalDate, SortedSet<ActivityOption>> departureDateAndOptionsMap)
	{
		List<ActivityOperationDates> operationDates = rateDetail.getOperationDates();
		for (ActivityOperationDates operationDate : operationDates)
		{
			mapModalityAndRateAndRateDetailAndOperationDateToOption(modality, rate, rateDetail, operationDate, departureDateAndOptionsMap);
		}
	}

	private static void mapModalityAndRateAndRateDetailAndOperationDateToOption(ActivityModalities modality, ActivityRates rate, ActivityRateDetails rateDetail, ActivityOperationDates operationDate,
			Map<LocalDate, SortedSet<ActivityOption>> departureDateAndOptionsMap)
	{
		if (log.isDebugEnabled())
			log.debug("mapModalityAndRateAndRateDetailAndOperationDateToOption::enter");
		LocalDate departureDate = LocalDate.parse(operationDate.getFrom(), df2YYYYMMDD);
		ActivityOption option = new ActivityOption();
		StringBuffer modalName = new StringBuffer(modality.getName());
		// HB seems to detail 'the' session time of the rateDetail.  But they provide an array?  What do multiple sessions indicate, if you cant select between them?  For now, log a WARN and bypass this activity if it contaisn multiple sessions (or languages)
		StringBuffer sessionSelector = new StringBuffer();
		if (rateDetail.getSessions() != null && rateDetail.getSessions().size() > 0)
		{
			for (Session session : rateDetail.getSessions())
			{
				modalName.append(", session " + session.getName());
				if ( sessionSelector.length() > 0 )
					sessionSelector.append(",");
				sessionSelector.append(session.getCode());
			}
		}
		StringBuffer languageSelector = new StringBuffer();
		if (rateDetail.getLanguages() != null && rateDetail.getLanguages().size() > 0)
		{
			for (Language language : rateDetail.getLanguages())
			{
				modalName.append(", language " + language.getDescription());
				if ( languageSelector.length() > 0 )
					languageSelector.append(",");
				languageSelector.append(language.getCode());
			}
		}
		option.setOptionId(modality.getCode() + "_" + sessionSelector.toString() + "_" + languageSelector.toString());
		option.setOptionName(modalName.toString());
		option.setBookingQuestions(new ArrayList<>());
		if ( modality.getQuestions() != null )
		{
			for ( ActivityQuestions modalityQuestion : modality.getQuestions())
			{
				BookingQuestion q = new BookingQuestion();
				q.setQuestionId(modalityQuestion.getCode());
				q.setQuestionText(modalityQuestion.getText());
				q.setQuestionType(QuestionType.STRING);
				q.setPerTraveller(false);
				option.getBookingQuestions().add(q);
				if (log.isDebugEnabled())
					log.debug("mapModalityAndRateAndRateDetailAndOperationDateToOption::create question " + q);
			}
		}
		//BookingQuestion q = new BookingQuestion();
		//q.setQuestionId("1");
		//q.setQuestionText("Enter weight in kilograms");
		//q.setQuestionType(QuestionType.NUMBER);
		//q.setPerTraveller(true);
		//option.getBookingQuestions().add(q);
		//q = new BookingQuestion();
		//q.setQuestionId("2");
		//q.setQuestionText("Enter pickup choice");
		//q.setQuestionType(QuestionType.LIST);
		//q.setList(new ArrayList<>());
		//q.getList().add("Hotel");
		//q.getList().add("Airport");
		//q.setPerTraveller(false);
		//option.getBookingQuestions().add(q);
		//q = new BookingQuestion();
		//q.setQuestionId("3");
		//q.setQuestionText("Enter preferred pickup time");
		//q.setQuestionType(QuestionType.TIME);
		//q.setPerTraveller(false);
		//option.getBookingQuestions().add(q);
		
		List<ActivityOption.ActivityOptionPriceBand> priceBands = makeActivityOptionPriceBands(modality.getAmountsFrom());
		option.setPricePer(priceBands);
		// TODO quick test
		BigDecimal nettPrice = rateDetail.getTotalAmount().getAmount();
		option.setNettPrice(new CurrencyValue(EUR_CURRENCY, nettPrice));
		try
		{
			option.setRrpPrice(new CurrencyValue(EUR_CURRENCY, HotelbedsInterface.applyInventoryMarkup(nettPrice, null)));
		}
		catch (Exception e)
		{
			log.debug("makeActivityOption::setRrpPrice::error: " + e.getMessage());
		}

		/*--cancellation policy--*/
		SortedSet<ActivityCancellationPolicyLine> cancellationPolicies = makeCancellationPolicy(rate.getFreeCancellation(), operationDate.getCancellationPolicies(), nettPrice);
		option.setCancellationPolicy(cancellationPolicies);
		option.setCancellationPolicyText(makeCancellationPolicyText(cancellationPolicies));
		/*---------------------*/
		/*--Activity Rate key--*/
		ActivityRate activityRate = new ActivityRate();
		activityRate.setRateCode(rate.getRateCode());
		activityRate.setRateClass(rate.getRateClass());
		activityRate.setFreeCancellation(rate.getFreeCancellation());
		activityRate.setRateKey(rateDetail.getRateKey());
		SortedSet<ActivityOption> activityOptions = departureDateAndOptionsMap.get(departureDate);
		if (activityOptions == null)
		{
			activityOptions = new TreeSet<>();
		}
		activityOptions.add(option);
		// Put override
		departureDateAndOptionsMap.put(departureDate, activityOptions);
	}

	private static BigDecimal calculateOptionNettPrice(List<ActivityAmountsFrom> amountsFroms, TravellerMix traveller)
	{
		BigDecimal nettPrice = new BigDecimal("0");
		int numberOfAdult = traveller.getAdultCount();
		List<Integer> childAges = traveller.getChildAges();
		if (numberOfAdult > 0)
		{
			Optional<ActivityAmountsFrom> optional = amountsFroms.stream().filter(af -> af.getAgeFrom() <= AGE_30_YEARS_OLD && AGE_30_YEARS_OLD <= af.getAgeTo())
					.max(Comparator.comparing(ActivityAmountsFrom::getAmount));
			if (!optional.isEmpty())
			{
				BigDecimal adultPrice = optional.get().getAmount();
				nettPrice = new BigDecimal(numberOfAdult).multiply(adultPrice);
			}
		}
		if (!childAges.isEmpty())
		{
			nettPrice = nettPrice.add(calculateOptionNettPriceOfChildAges(amountsFroms, childAges));
		}
		return nettPrice;
	}

	private static BigDecimal calculateOptionNettPriceOfChildAges(List<ActivityAmountsFrom> amountsFroms, List<Integer> childAges)
	{
		BigDecimal childNettPrice = new BigDecimal(0);
		for (int childAge : childAges)
		{
			BigDecimal childPrice = calculateOptionNettPriceByAge(childAge, amountsFroms);
			childNettPrice = childNettPrice.add(childPrice);
		}
		return childNettPrice;
	}

	private static BigDecimal calculateOptionNettPriceByAge(int age, List<ActivityAmountsFrom> amountsFroms)
	{
		for (ActivityAmountsFrom amountsFrom : amountsFroms)
		{
			if (age >= amountsFrom.getAgeFrom() && age <= amountsFrom.getAgeTo())
			{
				return amountsFrom.getAmount();
			}
		}
		return new BigDecimal(0);
	}

	private static List<ActivityOption.ActivityOptionPriceBand> makeActivityOptionPriceBands(List<ActivityAmountsFrom> amountsFroms)
	{
		List<ActivityOption.ActivityOptionPriceBand> priceBands = new ArrayList<>();
		if (amountsFroms == null || amountsFroms.isEmpty())
		{
			return priceBands;
		}
		for (ActivityAmountsFrom amountsFrom : amountsFroms)
		{
			ActivityOption.ActivityOptionPriceBand priceBand = makeActivityOptionPriceBand(amountsFrom);
			if (priceBand != null)
			{
				priceBands.add(priceBand);
			}
		}

		List<ActivityOption.ActivityOptionPriceBand> priceBandsResult = new ArrayList<>();
		/*--get max of child--*/
		List<ActivityOption.ActivityOptionPriceBand> childPriceBands = priceBands.stream().filter(pB -> pB.getAgeBandName().startsWith(AgeBrandName.CHILD.name())).collect(Collectors.toList());
		priceBandsResult.addAll(childPriceBands);

		/*------------------*/

		/*--if there are more than one Adult priceBand get best expensive--*/
		List<ActivityOption.ActivityOptionPriceBand> adultPriceBands = priceBands.stream().filter(pB -> AgeBrandName.ADULT.name().equals(pB.getAgeBandName())).collect(Collectors.toList());
		if (adultPriceBands == null || adultPriceBands.size() <= 1)
		{
			priceBandsResult.addAll(adultPriceBands);
		}
		else
		{
			ActivityOption.ActivityOptionPriceBand adultPriceBand = adultPriceBands.get(0);
			for (ActivityOption.ActivityOptionPriceBand priceBand : adultPriceBands)
			{
				if (adultPriceBand.getNettPrice().getAmount().compareTo(priceBand.getNettPrice().getAmount()) == 0)
				{
					adultPriceBand = priceBand;
				}
			}
			priceBandsResult.add(adultPriceBand);
		}
		/*--Add Child Band if not exist--*/
		if (priceBandsResult.size() == 1)
		{
			ActivityOption.ActivityOptionPriceBand adultPriceBand = priceBandsResult.get(0);
			ActivityOption.ActivityOptionPriceBand additionalChildPriceBand = new ActivityOption.ActivityOptionPriceBand();
			additionalChildPriceBand.setAgeBandName(AgeBrandName.CHILD.name());
			additionalChildPriceBand.setNettPrice(adultPriceBand.getNettPrice());
			additionalChildPriceBand.setRrpPrice(adultPriceBand.getRrpPrice());
			priceBandsResult.add(additionalChildPriceBand);
		}
		return priceBandsResult;
	}

	private static ActivityOption.ActivityOptionPriceBand makeActivityOptionPriceBand(ActivityAmountsFrom amountsFrom)
	{
		if (amountsFrom == null)
		{
			return null;
		}
		ActivityOption.ActivityOptionPriceBand priceBand = new ActivityOption.ActivityOptionPriceBand();
		/*--as child--*/
		if (amountsFrom.getAgeTo() == AGE_30_YEARS_OLD - 1)
		{
			priceBand.setAgeBandName(AgeBrandName.CHILD.name());
		}
		else if (amountsFrom.getAgeTo() < AGE_30_YEARS_OLD - 1)
		{
			priceBand.setAgeBandName(AgeBrandName.CHILD.name() + "_FROM_" + amountsFrom.getAgeFrom() + "_TO_" + amountsFrom.getAgeTo());
		}
		else
		{
			priceBand.setAgeBandName(AgeBrandName.ADULT.name());
		}
		priceBand.setNettPrice(new CurrencyValue(EUR_CURRENCY, amountsFrom.getAmount()));
		try
		{
			BigDecimal rrp = HotelbedsInterface.applyInventoryMarkup(amountsFrom.getAmount(), null);
			priceBand.setRrpPrice(new CurrencyValue(EUR_CURRENCY, rrp));
		}
		catch (Exception e)
		{
			priceBand.setRrpPrice(new CurrencyValue());
			log.error("applyInventoryMarkup - error: {}", e.getMessage());
		}
		return priceBand;
	}

	private static SortedSet<ActivityCancellationPolicyLine> makeCancellationPolicy(Boolean isFreeCancellation, List<ModalityCancellationPolicy> hbPolicies, BigDecimal nettPrice)
	{
		if (log.isDebugEnabled())
			log.debug("makeCancellationPolicy::enter");
		SortedSet<ActivityCancellationPolicyLine> cancelPolicyLines = new TreeSet<>();
		if (CollectionUtils.isEmpty(hbPolicies))
		{
			return cancelPolicyLines;
		}
		for (ModalityCancellationPolicy hbPolicy : hbPolicies)
		{
			if ("".equals(hbPolicy.getDateFrom()))
			{
				continue;
			}
			ActivityCancellationPolicyLine policyLine = new ActivityCancellationPolicyLine();
			try
			{
				ZonedDateTime dateFromGmt = ZonedDateTime.parse(hbPolicy.getDateFrom());
				policyLine.setAsOf(dateFromGmt.toLocalDate());
			}
			catch (Exception e)
			{
				if (log.isDebugEnabled())
				{
					log.debug("makeCancellationPolicy error: {}", e.getMessage());
				}
				continue;
			}
			policyLine.setPenalty(new CurrencyValue(EUR_CURRENCY, hbPolicy.getAmount()));
			policyLine.setPenaltyDescription(makePenaltyDescription(policyLine));
			if (log.isDebugEnabled())
				log.debug("makeCancellationPolicy::nettPrice=" + nettPrice + ", hbPolicy.amount=" + hbPolicy.getAmount());
			if (nettPrice != null && !ZERO_BIGDECIMAL.equals(nettPrice))
			{
				BigDecimal percent = hbPolicy.getAmount().divide(nettPrice, 2, RoundingMode.HALF_UP).multiply(ONE_HUNDRED).setScale(2);
				policyLine.setPenaltyPercent(percent);
			}
			cancelPolicyLines.add(policyLine);
		}
		return cancelPolicyLines;
	}

	private static String makePenaltyDescription(ActivityCancellationPolicyLine policyLine)
	{
		LocalDate asOf = policyLine.getAsOf();
		return new String("If cancelled on or after " + df2YYYYMMDD.format(asOf) + " - a charge of " + Functions.formatCurrencyDisplay(policyLine.getPenalty()) + " applies. ");
	}

	private static String makeCancellationPolicyText(SortedSet<ActivityCancellationPolicyLine> cancellationPolicies)
	{
		if (cancellationPolicies.isEmpty())
		{
			return "";
		}
		return cancellationPolicies.first().getPenaltyDescription();
	}

	/*-end departure -*/

	/*--Starting point--*/
	private static List<ContentLocationStartingPoints> getStartingPointsFromContent(Content hbContent)
	{
		ContentLocation location = hbContent.getLocation();
		if (location == null)
		{
			return Collections.EMPTY_LIST;
		}
		List<ContentLocationStartingPoints> startingPoints = location.getStartingPoints();
		if (startingPoints == null)
		{
			return Collections.EMPTY_LIST;
		}
		return startingPoints;
	}

	private static boolean makeHotelPickupAvailable(List<ContentLocationStartingPoints> startingPoints)
	{
		if (startingPoints.isEmpty())
		{
			return false;
		}
		for (ContentLocationStartingPoints point : startingPoints)
		{
			if ("HOTEL_PICKUP".equals(point.getType()))
			{
				return true;
			}
		}
		return false;

	}

	private static LatitudeLongitude makeGeoCoordinates(List<ContentLocationStartingPoints> startingPoints)
	{
		if (startingPoints != null && !startingPoints.isEmpty())
		{
			ContentLocationMeetingPoint firstMeetingPoint = startingPoints.get(0).getMeetingPoint();
			if (firstMeetingPoint != null && firstMeetingPoint.getGeolocation() != null)
			{
				return new LatitudeLongitude(firstMeetingPoint.getGeolocation().getLatitude(), firstMeetingPoint.getGeolocation().getLongitude());
			}
		}
		return new LatitudeLongitude();
	}

	private static String makeDeparturePoint(List<ContentLocationStartingPoints> startingPoints)
	{
		if (startingPoints.isEmpty())
		{
			return "";
		}
		String departurePoints = startingPoints.stream().filter(s -> s.getMeetingPoint().getDescription() != null).map(s -> s.getMeetingPoint().getDescription()).collect(Collectors.joining("\n"));
		return departurePoints;
	}
	/*--End starting point--*/
	/*-- end mapping for departure --*/

	/*--Booking mapper--*/
	public static BookingConfirmRequest makeBookingConfirmRequest(String client, ActivityBookRQ bookRQ, List<Activity> activitiesRateKeys) throws Exception
	{
		return new BookingConfirmRequest().language(LANGUAGE_EN).clientReference(bookRQ.getInternalBookingReference()).holder(makeBookingHolder(client, bookRQ))
				.activities(makeBookingActivitiesRequest(client, bookRQ, activitiesRateKeys));
	}

	private static BookingHolder makeBookingHolder(String client, ActivityBookRQ bookRQ)
	{
		ActivityBookRQ.ActivityBooker booker = bookRQ.getBooker();
		return new BookingHolder().name(booker.getGivenName()).title(booker.getTitle()).surname(booker.getSurname()).mailing(MAILING_DEFAULT).telephones(List.of(booker.getTelephone()))
				.country(bookRQ.getCountryCodeOfOrigin());
	}

	private static List<BookingActivity> makeBookingActivitiesRequest(String client, ActivityBookRQ bookRQ, List<Activity> activitiesIncludingRateKeys) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("makeBookingActivitiesRequest::enter with " + activitiesIncludingRateKeys.size() + " rateKeys");
		Set<ActivityBookRQ.ActivityRequestItem> bookingActivities = bookRQ.getItems();
		if (CollectionUtils.isEmpty(bookingActivities))
		{
			return Collections.EMPTY_LIST;
		}
		List<Traveller> travellers = bookRQ.getTravellers();
		if (CollectionUtils.isEmpty(travellers))
		{
			return Collections.EMPTY_LIST;
		}
		List<BookingActivity> activitiesRS = new ArrayList<>();
		Map<ActivityKey, String> mapActivityKeyRateKey = makeMapActivityKeyAnRateKey(activitiesIncludingRateKeys);
		if (log.isDebugEnabled())
			log.debug("makeBookingActivitiesRequest::mapActivityKeyRateKey=" + mapActivityKeyRateKey);
		for (ActivityBookRQ.ActivityRequestItem activityRQ : bookingActivities)
		{
			ActivityKey key = ActivityKey.builder().activityId(getActivityCodeByActivityId(activityRQ.getActivityId())).optionId(activityRQ.getOptionId()).date(DATE_DEFAULT).build();
			if ( activityRQ.getOptionId().contains("#"))
				key = ActivityKey.builder().activityId(getActivityCodeByActivityId(activityRQ.getActivityId())).optionId(activityRQ.getOptionId().substring(activityRQ.getOptionId().indexOf("#"))).date(DATE_DEFAULT).build();
			if (log.isDebugEnabled())
				log.debug("makeBookingActivitiesRequest::looking for built key " + key + " rateKey=" + mapActivityKeyRateKey.get(key));
			activitiesRS.add(new BookingActivity().preferedLanguage(LANGUAGE_EN).serviceLanguage(LANGUAGE_EN).rateKey(mapActivityKeyRateKey.get(key)).from(activityRQ.getDate().format(df2YYYYMMDD))
					.to(activityRQ.getDate().format(df2YYYYMMDD)).paxes(makePaxes(activityRQ.getDate().format(df2YYYYMMDD), bookRQ.getTravellers())).answers(makeAnswers(activityRQ.getBookingQuestionAnswers())));
		}
		return activitiesRS;
	}

	private static Map<ActivityKey, String> makeMapActivityKeyAnRateKey(List<Activity> activitiesIncludingRateKeys)
	{
		if (log.isDebugEnabled())
			log.debug("makeMapActivityKeyAnRateKey::enter");
		Map<ActivityKey, String> map = new HashMap<>();
		for (Activity activity : activitiesIncludingRateKeys)
		{
			List<ActivityModalities> modalities = activity.getModalities();
			for (ActivityModalities modality : modalities)
			{
				if ( modality.getRates().size() > 1)
				{
					if (log.isDebugEnabled())
						log.warn("makeMapActivityKeyAnRateKey::multiple rates found for  " + activity.getActivityCode());
				}
				for ( ActivityRates rate : modality.getRates())
				{
					for ( ActivityRateDetails rateDetail : rate.getRateDetails())
					{
						StringBuffer sessionSelector = new StringBuffer();
						if (rateDetail.getSessions() != null && rateDetail.getSessions().size() > 0)
						{
							for (Session session : rateDetail.getSessions())
							{
								if ( sessionSelector.length() > 0 )
									sessionSelector.append(",");
								sessionSelector.append(session.getCode());
							}
						}
						StringBuffer languageSelector = new StringBuffer();
						if (rateDetail.getLanguages() != null && rateDetail.getLanguages().size() > 0)
						{
							for (Language language : rateDetail.getLanguages())
							{
								if ( languageSelector.length() > 0 )
									languageSelector.append(",");
								languageSelector.append(language.getCode());
							}
						}
						String optionId = modality.getCode() + "_" + sessionSelector.toString() + "_" + languageSelector.toString();
						if ( modality.getCode().contains("#"))
							optionId = modality.getCode().substring(modality.getCode().indexOf("#")) + "_" + sessionSelector.toString() + "_" + languageSelector.toString();
						map.put(ActivityKey.builder().activityId(activity.getCode()).optionId(optionId).date(DATE_DEFAULT).build(), rateDetail.getRateKey());
					}
				}
			}
		}
		if (log.isDebugEnabled())
			log.debug("makeMapActivityKeyAnRateKey::returning " + map);
		return map;
	}

	private static List<BookingPax> makePaxes(String bookingDateStr, List<Traveller> travellers) throws Exception
	{
		LocalDate bookingDate = LocalDate.parse(bookingDateStr, df2YYYYMMDD);
		List<BookingPax> paxes = new ArrayList<>(travellers.size());
		for (Traveller traveller : travellers)
		{
			int age = traveller.getAge(bookingDate);
			paxes.add(new BookingPax().name(traveller.getGivenName()).surname(traveller.getSurname()).age(age).customerId(null).type(null).passport(null));
		}
		return paxes;
	}

	private static List<com.hotelbeds.activities.model.BookingAnswer> makeAnswers(List<com.torkirion.eroam.microservice.activities.apidomain.BookingAnswers> bookingAnswers) throws Exception
	{
		if ( bookingAnswers == null || bookingAnswers.size() == 0)
			return null;
		log.debug("makeAnswers::making " + bookingAnswers.size() + " answers");
		List<com.hotelbeds.activities.model.BookingAnswer> answers = new ArrayList<>();
		for ( BookingAnswers suppliedAnswer : bookingAnswers)
		{
			com.hotelbeds.activities.model.BookingAnswer answer = new com.hotelbeds.activities.model.BookingAnswer();
			answer.setAnswer(suppliedAnswer.getAnswer());
			answer.setQuestion(new com.hotelbeds.activities.model.BookingQuestionHB());
			answer.getQuestion().setCode(suppliedAnswer.getQuestionId());
			answers.add(answer);
		}
		return answers;
	}

	public static ActivityBookRS makeActivityBookRS(BookingResponse bookingResponse, ActivityBookRQ bookRQ)
	{
		ActivityBookRS result = new ActivityBookRS();
		result.setErrors(makeResponseErrors(bookingResponse.getErrors()));

		Booking booking = bookingResponse.getBooking();
		if (booking != null)
		{
			result.setBookingReference(booking.getReference());
			result.setInternalBookingReference(booking.getClientReference());
			List<ActivityBookRS.ActivityResponseItem> activityResponses = makeActivityResponses(booking.getActivities(), bookRQ);
			if (!CollectionUtils.isEmpty(activityResponses))
			{
				result.setItems(activityResponses);
				result.setRemarks(activityResponses.stream().map(response -> response.getItemRemark()).collect(Collectors.toList()));
			}
		}
		return result;
	}

	private static List<ActivityBookRS.ActivityResponseItem> makeActivityResponses(List<Items6> hbActivities, ActivityBookRQ bookRQ)
	{
		if (CollectionUtils.isEmpty(hbActivities))
		{
			return Collections.EMPTY_LIST;
		}
		Map<ActivityKey, String> mapKeyAndInternalItemReference = makeActivityKeyAndInternalItemReference(bookRQ.getItems());
		List<ActivityBookRS.ActivityResponseItem> activityResponseItems = new ArrayList<>();
		for (Items6 hbActivity : hbActivities)
		{
			ActivityBookRS.ActivityResponseItem item = new ActivityBookRS.ActivityResponseItem();
			item.setBookingItemReference(hbActivity.getActivityReference().toLowerCase());
			item.setChannel(HotelbedsService.CHANNEL);
			item.setItemStatus(convertHbStatusToBookingStatus(hbActivity.getStatus()));
			item.setBookingQuestionAnswers(convertHbQuestionToBookingAnswer(hbActivity.getQuestions()));
			item.setItemRemark(makeRemark(hbActivity.getComments()));
			ActivityKey activityKey = ActivityKey.builder().activityId(HotelbedsService.CHANNEL_PREFIX + hbActivity.getContent().getActivityCode()).optionId(hbActivity.getModality().getCode())
					.date(hbActivity.getDateFrom()).build();
			item.setInternalItemReference(mapKeyAndInternalItemReference.get(activityKey));
			String hbVoucherInfo = MessageFormat.format(HB_VOUCHER_INFO, hbActivity.getSupplier().getName(), hbActivity.getSupplier().getVatNumber(), item.getBookingItemReference());
			if (item.getItemRemark().length() == 0)
				item.setItemRemark(hbVoucherInfo);
			else
				item.setItemRemark(item.getItemRemark() + " " + hbVoucherInfo);
			if ( hbActivity.getVouchers() != null )
			{
				for ( Items1 voucher : hbActivity.getVouchers())
				{
					if ( "ENG".equals(voucher.getLanguage()) && "application/pdf".equals(voucher.getMimeType()))
					{
						item.setItemVoucherURL(voucher.getUrl());
					}
				}
			}
			int adultCount = 0;
			List<Integer> childAges = new ArrayList<>();
			for (Traveller traveller : bookRQ.getTravellers())
			{
				try
				{
					int age = traveller.getAge(LocalDate.now());
					if (age >= 18)
						adultCount++;
					else
						childAges.add(age);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			StringBuffer agesOutline = new StringBuffer("Passenger mix: " + adultCount + " adults");
			for (Integer age : childAges)
			{
				agesOutline.append(", one " + age + " year old");
			}
			if (item.getItemRemark().length() == 0)
				item.setItemRemark(agesOutline.toString());
			else
				item.setItemRemark(item.getItemRemark() + ". " + agesOutline.toString());
			activityResponseItems.add(item);
		}
		return activityResponseItems;
	}

	private static Map<ActivityKey, String> makeActivityKeyAndInternalItemReference(Set<ActivityBookRQ.ActivityRequestItem> activityRequestItems)
	{
		Map<ActivityKey, String> map = new HashMap<>();
		if (CollectionUtils.isEmpty(activityRequestItems))
		{
			return map;
		}
		for (ActivityBookRQ.ActivityRequestItem item : activityRequestItems)
		{
			/*-remove unique identifier in modality code-*/
			String optionId = item.getOptionId();
			int indexSharp = optionId.indexOf("#");
			optionId = optionId.substring(indexSharp + 1) + "@" + RATE_KEY_CODE + "||";
			/*-------------------------------------------*/
			ActivityKey key = ActivityKey.builder().activityId(item.getActivityId()).optionId(optionId).date(item.getDate().format(df2YYYYMMDD)).build();
			map.put(key, item.getInternalItemReference());
		}
		return map;
	}

	private static String getDateFromByActivityRequestItems(Set<ActivityBookRQ.ActivityRequestItem> items)
	{
		Optional<LocalDate> optional = items.stream().filter(i -> !"".equals(i.getDate())).map(i -> i.getDate()).min(Comparator.comparing(LocalDate::toEpochDay));
		if (optional.isPresent())
		{
			return df2YYYYMMDD.format(optional.get());
		}
		return items.stream().findFirst().get().getDate().format(df2YYYYMMDD);
	}

	private static String getDateToByActivityRequestItems(Set<ActivityBookRQ.ActivityRequestItem> items)
	{
		Optional<LocalDate> optional = items.stream().filter(i -> !"".equals(i.getDate())).map(i -> i.getDate()).max(Comparator.comparing(LocalDate::toEpochDay));
		if (optional.isPresent())
		{
			return df2YYYYMMDD.format(optional.get());
		}
		return items.stream().findFirst().get().getDate().format(df2YYYYMMDD);
	}

	public static AvailabilitybyhotelcodeRequest makeActivitiesRequestByActivityItems(Set<ActivityBookRQ.ActivityRequestItem> items, List<Traveller> travellers) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("makeAvailabilityActivitiesRequestByActivityBookingRQ::entering for activityRequestItems={}", items);
		AvailabilitybyhotelcodeRequest request = new AvailabilitybyhotelcodeRequest();
		String bookingDateStr = getDateFromByActivityRequestItems(items);
		request.setFrom(bookingDateStr);
		request.setTo(bookingDateStr);
		request.setLanguage(LANGUAGE_EN);
		// TODO: pagination
		request.setPagination(new Pagination());
		request.getPagination().setItemsPerPage(100);
		request.getPagination().setPage(1);

		/*-Start setting the activities filter-*/
		List<SearchFilterItem> searchFilterItems = items.stream()
				//.map(i -> new SearchFilterItem().type("service_modality").value(getActivityCodeByActivityId(i.getActivityId()) + "@@#@@" + i.getOptionId().substring(0, i.getOptionId().indexOf("_")))).collect(Collectors.toList());
				.map(i -> new SearchFilterItem().type("service").value(getActivityCodeByActivityId(i.getActivityId()))).collect(Collectors.toList());
		if (log.isDebugEnabled())
			log.debug("makeAvailabilityActivitiesRequestByActivityBookingRQ::searchFilterItems=" + searchFilterItems);
		Filter filter = new Filter();
		filter.setSearchFilterItems(searchFilterItems);
		request.getFilters().add(filter);

		/*-End setting the activities filter-*/
		/*-paxes-*/
		LocalDate bookingDate = LocalDate.parse(bookingDateStr, df2YYYYMMDD);
		for (Traveller traveller : travellers)
		{
			if (traveller.getAge(bookingDate) >= 30)
			{
				request.getPaxes().add(PAX_30_YEAR_OLD);
			}
			request.getPaxes().add(new Pax().age(traveller.getAge(bookingDate)));
		}
		/*------*/
		return request;
	}

	/*--end booking--*/

	private static String getActivityCodeByActivityId(String activityId)
	{
		if (activityId.startsWith(HotelbedsService.CHANNEL_PREFIX))
		{
			return activityId.substring(HotelbedsService.CHANNEL_PREFIX.length());
		}
		return activityId;
	}

	private static String makeRemark(List<Items> comments)
	{
		// CONTRACT_REMARKS
		if (CollectionUtils.isEmpty(comments))
		{
			return "";
		}
		Optional<Items> optional = comments.stream().filter(c -> "CONTRACT_REMARKS".equals(c.getType())).findFirst();
		if (optional.isPresent())
		{
			return optional.get().getText();
		}
		return "";
	}

	// CONFIRMED
	// CANCELLED
	private static ActivityBooking.ItemStatus convertHbStatusToBookingStatus(String hbStatus)
	{
		if ("".equals(hbStatus))
		{
			return ActivityBooking.ItemStatus.FAILED;
		}
		switch (hbStatus)
		{
			case "CONFIRMED":
				return ActivityBooking.ItemStatus.BOOKED;
			case "CANCELLED":
				return ActivityBooking.ItemStatus.CANCELLED;
		}
		return ActivityBooking.ItemStatus.FAILED;
	}

	private static List<BookingAnswers> convertHbQuestionToBookingAnswer(List<Items4> hbQuestions)
	{
		if (CollectionUtils.isEmpty(hbQuestions))
		{
			return Collections.EMPTY_LIST;
		}
		List<BookingAnswers> bookingAnswers = new ArrayList<>();
		for (Items4 question : hbQuestions)
		{
			BookingAnswers ba = new BookingAnswers();
			if (question.getQuestion() == null)
			{
				continue;
			}
			ba.setAnswer(question.getAnswer());
			ba.setQuestionId(question.getQuestion().getCode());
			bookingAnswers.add(ba);
		}
		return bookingAnswers;
	}

	private static List<ResponseExtraInformation> makeResponseErrors(List<ResponseError> hbErrors)
	{
		if (CollectionUtils.isEmpty(hbErrors))
		{
			return Collections.EMPTY_LIST;
		}
		return hbErrors.stream().map(e -> new ResponseExtraInformation(e.getCode(), e.getText())).collect(Collectors.toList());
	}

	/*-end booking-*/

	/*--start mapping canceling--*/
	public static ActivityCancelRS makeActivityCancelRS(BookingResponse cancelResponse)
	{
		ActivityCancelRS result = new ActivityCancelRS(null, new CurrencyValue(EUR_CURRENCY, ZERO_BIGDECIMAL));
		Booking booking = cancelResponse.getBooking();
		if (booking != null)
		{
			result = new ActivityCancelRS(booking.getReference(), new CurrencyValue(EUR_CURRENCY, booking.getCancelValuationAmount()));
		}
		result.setErrors(makeResponseErrors(cancelResponse.getErrors()));
		return result;
	}

	private static String stripOutHtml(String textContent)
	{
		if (textContent == null)
		{
			return textContent;
		}
		String text = textContent.replaceAll("\\<.*?\\>", "");
		return text;
	}

	private static final String TEXT_CONTENT = "{TEXT_CONTENT}";

	private static final String STRIP_HMTL_TEMPLATE = "<div class=\"WordSection1\"><p style=\"margin-bottom:.0001pt; margin:0in 0in 8pt\">" + TEXT_CONTENT + "</p></div>";

	/*--end mapping canceling--*/

	public static final DateTimeFormatter df2YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

	public static final String EUR_CURRENCY = "EUR";

	public static final String DEPARTURE_IT_DEFAULT = "00:01";

	public static final LocalTime DEPARTURE_TIME_DEFAULT = LocalTime.parse("00:01");

	public static final String DEPARTURE_NAME_DEFAULT = "DEFAULT";

	public static final Integer AGE_30_YEARS_OLD = 30;

	public static final String LANGUAGE_EN = "en";

	public static final Boolean MAILING_DEFAULT = true;

	private enum AgeBrandName
	{
		ADULT, CHILD
	}

	public static final Pax PAX_30_YEAR_OLD = new Pax().age(30);

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	public static final BigDecimal ZERO_BIGDECIMAL = new BigDecimal(0);

	public static final List<Pax> PAXES_DEFAULT = List.of(new Pax().age(30));

	public static final String DATE_DEFAULT = "2021-06-22";

	public static final String RATE_KEY_CODE = "STANDARD";

	public static final String RATE_KEY_CLASS = "NOR";

	private static final BigDecimal NUMBER_2 = new BigDecimal("2");

	private static final String STRING_JOIN_CHARACTER = ". ";

	private static final String HB_VOUCHER_INFO = "Payable through {0}, acting as agent for the service operating company, details of which can be provided upon request. VAT: {1} Reference: {2}";
}
