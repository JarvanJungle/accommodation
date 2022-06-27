package com.torkirion.eroam.microservice.accommodation.endpoint.olery;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import javax.sql.DataSource;

import org.apache.commons.text.WordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.OleryAccommodationData;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.FacilityGroup;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.Image;
import com.torkirion.eroam.microservice.accommodation.apidomain.OleryAccommodationData.CountryScores;
import com.torkirion.eroam.microservice.accommodation.apidomain.OleryCategoryRating;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryReviewResponse.CountryRating;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryReviewResponse.Rating;
import com.torkirion.eroam.microservice.accommodation.endpoint.olery.OleryReviewSummaryResponse.ReviewMap;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.DetailsRS;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoService;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.BoardTypeInclusion;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.datadomain.Country;
import com.torkirion.eroam.microservice.datadomain.CountryRepo;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import io.swagger.annotations.ApiModelProperty;

@Service
@RequiredArgsConstructor
@Slf4j
public class OleryService
{

	@NonNull
	private AccommodationRCRepo accommodationRCRepo;

	@NonNull
	private CountryRepo countryRepo;

	@NonNull
	private OleryPropertyRepo oleryPropertyRepo;

	@NonNull
	private OleryCountryPropertyRepo oleryCountryPropertyRepo;

	@Autowired
	private SystemPropertiesDAO propertiesDAO;

	private OleryHttpService oleryHttpService = null;

	private static final BigInteger MINIMUM_REVIEW_COUNT = BigInteger.valueOf(5);

	public OleryAccommodationData getAccommodationOleryData(Long oleryCompanyId, String countryCodeOfOrigin)
	{
		if (log.isDebugEnabled())
			log.debug("getAccommodationOleryData::for " + oleryCompanyId + " and " + countryCodeOfOrigin);
		String key = oleryCompanyId.toString();
		if (countryCodeOfOrigin != null)
			key = oleryCompanyId.toString() + countryCodeOfOrigin;

		OleryAccommodationData data = getCache().get(key);
		if (data != null && !data.getIsNull())
		{
			if (log.isDebugEnabled())
				log.debug("getAccommodationOleryData::cache hit, returning " + data);
			return data;
		}

		Optional<OleryProperty> oleryPropertyOpt = oleryPropertyRepo.findById(oleryCompanyId);
		if (oleryPropertyOpt == null || !oleryPropertyOpt.isPresent())
		{
			if (log.isDebugEnabled())
				log.debug("getAccommodationOleryData::no data at all for " + oleryCompanyId);
			OleryAccommodationData nullOleryAccommodationData = new OleryAccommodationData();
			nullOleryAccommodationData.setIsNull(true);
			getCache().put(key, nullOleryAccommodationData);
			if (log.isDebugEnabled())
				log.debug("getAccommodationOleryData::no data, returning null");
			return null;
		}
		if (log.isDebugEnabled())
			log.debug("getAccommodationOleryData::data for " + key + " is " + oleryPropertyOpt.get());
		try
		{
			OleryProperty oleryProperty = oleryPropertyOpt.get();
			if (oleryProperty.getRating() == null)
			{
				OleryAccommodationData nullOleryAccommodationData = new OleryAccommodationData();
				nullOleryAccommodationData.setIsNull(true);
				getCache().put(key, nullOleryAccommodationData);
				if (log.isDebugEnabled())
					log.debug("getAccommodationOleryData::empty data, returning null");
				return null;
			}
			data = new OleryAccommodationData();
			data.setRating(oleryProperty.getRating());
			data.setReviewCount(oleryProperty.getReviewCount());
			data.setRatingIsCountrySpecific(false);
			if (oleryProperty.getCategoryRating() != null)
			{
				OleryCategoryRatings oleryCategoryRatings = getObjectMapper().readValue(oleryProperty.getCategoryRating(), OleryCategoryRatings.class);
				if (log.isDebugEnabled())
					log.debug("getAccommodationOleryData::turned " + oleryProperty.getCategoryRating() + " into " + oleryCategoryRatings);
				data.setCategoryRatings(new TreeMap<String, OleryCategoryRating>());
				data.getCategoryRatings().putAll(oleryCategoryRatings.getCategoryRatings());
			}
			if (oleryProperty.getReviewSummaries() != null && !oleryProperty.getReviewSummaries().equals("{}"))
			{
				List<String> reviewText = getObjectMapper().readValue(oleryProperty.getReviewSummaries(), new TypeReference<List<String>>()
				{});
				data.setReviewText(reviewText);
			}

			if (countryCodeOfOrigin != null)
			{
				List<OleryCountryProperty> oleryCountryPropertyList = oleryCountryPropertyRepo.findByCompanyIdAndCountryCode(oleryCompanyId, countryCodeOfOrigin);
				if (log.isDebugEnabled())
					log.debug("getAccommodationOleryData::data for country " + countryCodeOfOrigin + " is " + oleryCountryPropertyList);
				if (oleryCountryPropertyList != null && oleryCountryPropertyList.size() > 0)
				{
					OleryCountryProperty oleryCountryProperty = oleryCountryPropertyList.get(0);
					data.setRatingIsCountrySpecific(true);
					data.setOriginCountryRating(oleryCountryProperty.getRating());
					data.setOriginCountryReviewCount(oleryCountryProperty.getReviewCount());
					if (oleryCountryProperty.getCategoryRating() != null)
					{
						OleryCategoryRatings originCountryOleryCategoryRatings = getObjectMapper().readValue(oleryCountryProperty.getCategoryRating(), OleryCategoryRatings.class);
						data.setCategoryRatings(new TreeMap<String, OleryCategoryRating>());
						data.getCategoryRatings().putAll(originCountryOleryCategoryRatings.getCategoryRatings());
					}
				}
			}

			List<OleryCountryProperty> oleryCountryPropertyList = oleryCountryPropertyRepo.findByCompanyId(oleryCompanyId);
			if (log.isDebugEnabled())
				log.debug("getAccommodationOleryData::found reviews from " + oleryCountryPropertyList.size() + " countries");
			if (oleryCountryPropertyList != null && oleryCountryPropertyList.size() > 0)
			{
				SortedSet<CountryScores> countryScores = new TreeSet<>();
				Integer totalReviews = 0;
				for ( OleryCountryProperty oleryCountryProperty : oleryCountryPropertyList)
				{
					totalReviews += oleryCountryProperty.getReviewCount().intValue();
				}
				for ( OleryCountryProperty oleryCountryProperty : oleryCountryPropertyList)
				{
					CountryScores countryScore = new CountryScores();
					countryScore.setCountry(oleryCountryProperty.getCountryCode());
					countryScore.setPercent(BigInteger.valueOf(oleryCountryProperty.getReviewCount().intValue() * 100 / totalReviews));
					countryScores.add(countryScore);
				}
				data.setCountryScores(countryScores);
			}

			getCache().put(key, data);
			if (log.isDebugEnabled())
				log.debug("getAccommodationOleryData::built cache, returning " + data);
			return data;
		}
		catch (Exception e)
		{
			if (log.isDebugEnabled())
				log.debug("getAccommodationOleryData::threw exception " + e.toString(), e);
			OleryAccommodationData nullOleryAccommodationData = new OleryAccommodationData();
			nullOleryAccommodationData.setIsNull(true);
			getCache().put(key, nullOleryAccommodationData);
			if (log.isDebugEnabled())
				log.debug("getAccommodationOleryData::error, returning null");
			return null;
		}
	}

	@Transactional
	public boolean mapAccommodation(int passNumber)
	{
		if (log.isDebugEnabled())
			log.debug("mapAccommodation::enter with passNumber " + passNumber);

		Pageable firstPage = PageRequest.of(0, 50);

		oleryHttpService = new OleryHttpService("https://agora.olery.com");

		// List<AccommodationRCData> accommodationRCDataList = accommodationRCRepo.findByOleryCompanyCodeIsNull(firstPage);

		// List<AccommodationRCData> accommodationRCDataList = accommodationRCRepo.findByGeoboxAndChannel(new BigDecimal("55"),
		// new BigDecimal("26"), new BigDecimal("56"), new BigDecimal("25"),
		// HotelbedsService.CHANNEL);
		List<AccommodationRCData> accommodationRCDataList = accommodationRCRepo.findByGeoboxAndChannel(new BigDecimal("51.6723432"), new BigDecimal("-0.3514683"), new BigDecimal("51.38494009999999"),
				new BigDecimal("0.148271"), HotelbedsService.CHANNEL);
		if (log.isDebugEnabled())
			log.debug("mapAccommodation::accommodationRCDataList has " + accommodationRCDataList.size() + " entries");

		if (accommodationRCDataList.size() == 0)
			return false;

		int loop = 0;
		for (AccommodationRCData accommodationRCData : accommodationRCDataList)
		{
			if (log.isDebugEnabled())
				log.debug("mapAccommodation::pass " + passNumber + ", loop " + loop + " for property " + accommodationRCData.getCode() + " " + accommodationRCData.getAccommodationName());
			loop++;
			try
			{
				Long oleryCompanyCode = getOleryCompany(accommodationRCData);
				if (log.isDebugEnabled())
					log.debug("mapAccommodation::getOleryCompany returned " + oleryCompanyCode);
				accommodationRCData.setOleryCompanyCode(oleryCompanyCode);
				accommodationRCRepo.save(accommodationRCData);
				if (oleryCompanyCode <= 0)
				{
					log.info("mapAccommodation::lookup failed for " + accommodationRCData.getCode());
				}
			}
			catch (Exception e)
			{
				log.warn("mapAccommodation::caught exception " + e.toString(), e);
			}
		}

		Functions.logMemAndYield();
		return true;
	}

	@Transactional
	public boolean mapAccommodationByCountry(String countryCode, int passNumber, String channel)
	{
		if (log.isDebugEnabled())
			log.debug("mapAccommodation::enter with countryCode + " + countryCode + ", passNumber " + passNumber + ", channel " + channel);

		Pageable firstPage = PageRequest.of(0, 50);

		oleryHttpService = new OleryHttpService("https://agora.olery.com");

		List<AccommodationRCData> accommodationRCDataList;
		if ( countryCode != null && countryCode.length() > 0 && !countryCode.equals("ALL"))
			accommodationRCDataList = accommodationRCRepo.findByAddressCountryCodeAndOleryCompanyCodeIsNull(countryCode, firstPage);
		else
			accommodationRCDataList = accommodationRCRepo.findByOleryCompanyCodeIsNull(firstPage);
		if ( channel != null )
		{
			accommodationRCDataList = accommodationRCDataList.stream().filter( a -> a.getChannel().equals(channel)).collect(Collectors.toList());
		}
		if (log.isDebugEnabled())
			log.debug("mapAccommodation::accommodationRCDataList has " + accommodationRCDataList.size() + " entries");

		if (accommodationRCDataList.size() == 0)
			return false;

		int loop = 0;
		for (AccommodationRCData accommodationRCData : accommodationRCDataList)
		{
			if (log.isDebugEnabled())
				log.debug("mapAccommodation::pass " + passNumber + ", loop " + loop + " for property " + accommodationRCData.getCode() + " " + accommodationRCData.getAccommodationName());
			loop++;
			try
			{
				Long oleryCompanyCode = getOleryCompany(accommodationRCData);
				if (log.isDebugEnabled())
					log.debug("mapAccommodation::getOleryCompany returned " + oleryCompanyCode);
				accommodationRCData.setOleryCompanyCode(oleryCompanyCode);
				accommodationRCRepo.save(accommodationRCData);
				if (oleryCompanyCode <= 0)
				{
					log.info("mapAccommodation::lookup failed for " + accommodationRCData.getCode());
				}
			}
			catch (Exception e)
			{
				log.warn("mapAccommodation::caught exception " + e.toString(), e);
			}
		}

		Functions.logMemAndYield();
		return true;
	}

	@Transactional
	public boolean loadReviews(String countryCode, int passNumber)
	{
		log.info("loadReviews::enter with passNumber " + passNumber);

		Pageable firstPage = PageRequest.of(0, 50);

		oleryHttpService = new OleryHttpService("https://agora.olery.com");

		List<OleryProperty> oleryProperties;
		if ( countryCode != null && countryCode.length() > 0 && !countryCode.equals("ALL"))
			oleryProperties = oleryPropertyRepo.findByCountryCodeAndLastUpdatedIsNull(countryCode, firstPage);
		else
			oleryProperties = oleryPropertyRepo.findByLastUpdatedIsNull(firstPage);
		if (log.isDebugEnabled())
			log.debug("loadReviews::oleryProperties has " + oleryProperties.size() + " entries");

		if (oleryProperties.size() == 0)
			return false;

		int loop = 0;
		LocalDate now = LocalDate.now();
		for (OleryProperty oleryProperty : oleryProperties)
		{
			if (log.isDebugEnabled())
				log.debug("loadReviews::pass " + passNumber + ", loop " + loop + " for property " + oleryProperty.getCompanyId() + " " + oleryProperty.getName());
			loop++;
			if (oleryProperty.getCompanyId() == -1)
			{
				if (log.isDebugEnabled())
					log.debug("loadReviews::code -1, bypass");
				continue;
			}
			try
			{
				String requestURL = new String("/v3/companies/" + oleryProperty.getCompanyId() + "/review_content");
				Map<String, String> params = new HashMap<>();
				params.put("auth_token", "b5e63de7e3922a06cad7dc7a71f03162");
				params.put("content", "ratings,country_ratings,opinions");
				String responseString = oleryHttpService.doCallGet(requestURL, params);

				OleryReviewResponse oleryMappingResponse = getObjectMapper().readValue(responseString, OleryReviewResponse.class);

				if (oleryMappingResponse == null || oleryMappingResponse.getData() == null)
				{
					log.info("loadReviews::lookup failed for " + oleryProperty.getCompanyId());
					continue;
				}
				OleryReviewResponse.ReviewData reviewData = oleryMappingResponse.getData();
				if (reviewData.getReviewCount() == 0 || reviewData.getGei() == null)
				{
					// no reviews!
					log.info("loadReviews::no reviews or score for " + oleryProperty.getCompanyId() + ":" + oleryProperty.getName());
					oleryProperty.setLastUpdated(now);
					oleryProperty.setRating(BigDecimal.ZERO);
					oleryProperty.setReviewCount(BigInteger.ZERO);
					oleryProperty.setCategoryRating("{}");
					oleryProperty.setReviewSummaries("{}");
					oleryPropertyRepo.save(oleryProperty);
					continue;
				}
				log.info("loadReviews::found " + oleryMappingResponse);
				oleryProperty.setLastUpdated(now);
				oleryProperty.setRating(reviewData.getGei());
				oleryProperty.setReviewCount(BigInteger.valueOf(reviewData.getReviewCount()));
				OleryCategoryRatings oleryCategoryRatings = new OleryCategoryRatings();
				for (String ratingKey : reviewData.getRatings().keySet())
				{
					log.info("loadReviews::found rating key " + ratingKey);
					OleryReviewResponse.Rating rating = reviewData.getRatings().get(ratingKey);
					log.info("loadReviews::title = " + rating.getTitle());
					log.info("loadReviews::value = " + rating.getValue());
					log.info("loadReviews::reviewCount = " + rating.getReviewCount());
					OleryCategoryRating oleryCategoryRating = new OleryCategoryRating();
					oleryCategoryRating.setRating(rating.getValue());
					oleryCategoryRating.setReviewCount(BigInteger.valueOf(rating.getReviewCount()));
					oleryCategoryRatings.getCategoryRatings().put(WordUtils.capitalizeFully(rating.getTitle()), oleryCategoryRating);
				}
				if ( reviewData.getOpinions() != null )
				{
					for (String opinionKey : reviewData.getOpinions().keySet())
					{
						log.info("loadReviews::found opinion key " + opinionKey);
						OleryReviewResponse.Opinion opinion = reviewData.getOpinions().get(opinionKey);
						if ( opinion.getTopic().equals("sanitary_safety") || opinion.getTopic().equals("health_precautions") || opinion.getTopic().equals("covid"))
						{
							log.info("loadReviews::title = " + opinion.getTitle());
							log.info("loadReviews::value = " + opinion.getSentimentScore());
							log.info("loadReviews::reviewCount = " + opinion.getReviewCount());
							OleryCategoryRating oleryCategoryRating = new OleryCategoryRating();
							oleryCategoryRating.setRating(opinion.getSentimentScore());
							oleryCategoryRating.setReviewCount(BigInteger.valueOf(opinion.getReviewCount()));
							oleryCategoryRatings.getCategoryRatings().put(WordUtils.capitalizeFully(opinion.getTitle()), oleryCategoryRating);
						}
					}
				}
				String oleryCategoryRatingsJSon = getObjectMapper().writeValueAsString(oleryCategoryRatings);
				oleryProperty.setCategoryRating(oleryCategoryRatingsJSon);

				// load review snippets
				String reviewSummariesJson = makeReviewSummaries(oleryProperty.getCompanyId());
				oleryProperty.setReviewSummaries(reviewSummariesJson);
				oleryPropertyRepo.save(oleryProperty);

				// do country level
				saveCountryRating(reviewData, oleryProperty.getCompanyId());
			}
			catch (Exception e)
			{
				log.warn("loadReviews::caught exception " + e.toString(), e);
			}
		}

		Functions.logMemAndYield();
		return true;
	}

	protected Long getOleryCompany(AccommodationRCData accommodationRCData) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("getOleryCompany::enter for " + accommodationRCData.getCode());

		String requestURL = new String("/v3/mapping");
		Map<String, String> params = new HashMap<>();
		params.put("auth_token", "b5e63de7e3922a06cad7dc7a71f03162");
		params.put("name", accommodationRCData.getAccommodationName());
		if (accommodationRCData.getAddress().getFullFormAddress() != null)
		{
			params.put("address", accommodationRCData.getAddress().getFullFormAddress());
		}
		else if (accommodationRCData.getAddress().getStreet() != null)
		{
			params.put("address", accommodationRCData.getAddress().getStreet());
		}
		if (accommodationRCData.getAddress().getCity() != null)
		{
			params.put("city", accommodationRCData.getAddress().getCity());
		}
		if (accommodationRCData.getAddress().getGeoCoordinates() != null)
		{
			params.put("lat", accommodationRCData.getAddress().getGeoCoordinates().getLatitude().toPlainString());
			params.put("lng", accommodationRCData.getAddress().getGeoCoordinates().getLongitude().toPlainString());
		}
		params.put("country", accommodationRCData.getAddress().getCountryCode());

		String responseString = oleryHttpService.doCallGet(requestURL, params);

		OleryMappingResponse oleryMappingResponse = getObjectMapper().readValue(responseString, OleryMappingResponse.class);

		if (oleryMappingResponse == null || oleryMappingResponse.getData() == null || oleryMappingResponse.getData().getBestMatch() == null)
		{
			log.info("getOleryCompany::lookup failed for " + accommodationRCData.getCode() + ":" + accommodationRCData.getAccommodationName());
			return -1L;
		}
		OleryMappingResponse.Match oleryBestMatch = oleryMappingResponse.getData().getBestMatch();
		if (log.isDebugEnabled())
			log.debug("getOleryCompany::found olery record " + oleryBestMatch.getName() + " for " + accommodationRCData.getAccommodationName());

		Optional<OleryProperty> oleryPropertyOpt = oleryPropertyRepo.findById(oleryMappingResponse.getData().getBestMatch().getId());
		if (!oleryPropertyOpt.isPresent())
		{
			if (log.isDebugEnabled())
				log.debug("getOleryCompany::oleryProperty record not found, creating...");
			// save to DB
			OleryProperty oleryProperty = new OleryProperty();
			oleryProperty.setCompanyId(oleryBestMatch.getId());
			oleryProperty.setName(oleryBestMatch.getName());
			oleryProperty.setGeoCoordinates(new OleryProperty.GeoCoordinates());
			oleryProperty.getGeoCoordinates().setLatitude(oleryBestMatch.getLatitude());
			oleryProperty.getGeoCoordinates().setLongitude(oleryBestMatch.getLongitude());
			oleryProperty.setCity(oleryBestMatch.getCity());
			oleryProperty.setCountryCode(oleryBestMatch.getCountryCode());
			oleryProperty.setTripAdvisorId(oleryBestMatch.getTripAdvisorId());
			oleryProperty.setGooglePlaceId(oleryBestMatch.getGooglePlaceId());
			oleryProperty.setExpediaId(oleryBestMatch.getExpediaId());
			oleryProperty.setEanId(oleryBestMatch.getEanId());
			if (log.isDebugEnabled())
				log.debug("getOleryCompany::saving " + oleryProperty);
			oleryPropertyRepo.save(oleryProperty);
		}
		return oleryBestMatch.getId();
	}

	protected String makeReviewSummaries(Long oleryCompanyId) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("makeReviewSummaries::enter for " + oleryCompanyId);

		String requestURL = new String("/v2/review-summaries/templates/1/generate/" + oleryCompanyId);
		Map<String, String> params = new HashMap<>();
		params.put("auth_token", "b5e63de7e3922a06cad7dc7a71f03162");
		String responseString = oleryHttpService.doCallGet(requestURL, params);

		List<String> reviewSummaries = new ArrayList<>();

		OleryReviewSummaryResponse oleryReviewSummaryResponse = getObjectMapper().readValue(responseString, OleryReviewSummaryResponse.class);
		if (log.isDebugEnabled())
			log.debug("makeReviewSummaries::oleryReviewSummaryResponse=" + oleryReviewSummaryResponse);

		if (oleryReviewSummaryResponse == null || oleryReviewSummaryResponse.getData() == null)
		{
			log.info("makeReviewSummaries::lookup failed for " + oleryCompanyId);
			return "{}";
		}
		for (Map<String, Object> d : oleryReviewSummaryResponse.getData())
		{
			for (Entry<String, Object> dd : d.entrySet())
			{
				// log.debug("makeReviewSummaries::dd.key=" + dd.getKey() + " ("+ dd.getKey().getClass().toString() + "),
				// valueType=" + dd.getValue() + " (" + dd.getValue().getClass().toString() + ")");
				if (dd.getValue() instanceof Map)
				{
					@SuppressWarnings("unchecked")
					Map<String, Object> valueMap = (Map<String, Object>) dd.getValue();
					Object reviewTextObj = valueMap.get("text");
					if (reviewTextObj instanceof String)
					{
						if (log.isDebugEnabled())
							log.debug("makeReviewSummaries::companyID " + oleryCompanyId + " found text: " + reviewTextObj);
						reviewSummaries.add((String) reviewTextObj);
					}
				}
			}
		}
		String reviewSummariesJson = getObjectMapper().writeValueAsString(reviewSummaries);

		return reviewSummariesJson;
	}

	protected void saveCountryRating(OleryReviewResponse.ReviewData reviewData, Long companyId) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("saveCountryRating::enter");

		oleryCountryPropertyRepo.deleteByCompanyId(companyId);
		int countriesSaved = 0;
		for (Entry<String, CountryRating> countryRatingBlock : reviewData.getCountryRatings().entrySet())
		{
			String countryCode = countryRatingBlock.getKey();

			if (log.isDebugEnabled())
				log.debug("saveCountryRating::processing country " + countryCode);

			CountryRating countryRating = countryRatingBlock.getValue();
			BigDecimal rating = null;
			BigInteger reviewCount = null;
			OleryCategoryRatings oleryCategoryRatings = new OleryCategoryRatings();
			if (countryRating.getOverall() != null)
			{
				rating = countryRating.getOverall().getValue();
				reviewCount = BigInteger.valueOf(countryRating.getOverall().getReviewCount());
			}
			makeCountryRating(countryRating.getAmbiance(), oleryCategoryRatings);
			makeCountryRating(countryRating.getCleanliness(), oleryCategoryRatings);
			makeCountryRating(countryRating.getFacilities(), oleryCategoryRatings);
			makeCountryRating(countryRating.getFood(), oleryCategoryRatings);
			makeCountryRating(countryRating.getFriendliness(), oleryCategoryRatings);
			makeCountryRating(countryRating.getLocation(), oleryCategoryRatings);
			makeCountryRating(countryRating.getOverall(), oleryCategoryRatings);
			makeCountryRating(countryRating.getRoom(), oleryCategoryRatings);
			makeCountryRating(countryRating.getService(), oleryCategoryRatings);
			makeCountryRating(countryRating.getValue(), oleryCategoryRatings);

			String oleryCategoryRatingsJSon = getObjectMapper().writeValueAsString(oleryCategoryRatings);

			OleryCountryProperty oleryCountryProperty = new OleryCountryProperty();
			oleryCountryProperty.setCompanyId(companyId);
			oleryCountryProperty.setCountryCode(countryCode);
			oleryCountryProperty.setRating(rating);
			oleryCountryProperty.setReviewCount(reviewCount);
			oleryCountryProperty.setCategoryRating(oleryCategoryRatingsJSon);
			oleryCountryPropertyRepo.save(oleryCountryProperty);
			countriesSaved++;
		}
		if (log.isDebugEnabled())
			log.debug("saveCountryRating::saved " + countriesSaved + " countries for " + companyId);
	}

	protected void makeCountryRating(Rating rating, OleryCategoryRatings oleryCategoryRatings)
	{
		if (rating == null)
			return;
		OleryCategoryRating oleryCategoryRating = new OleryCategoryRating();
		oleryCategoryRating.setRating(rating.getValue());
		oleryCategoryRating.setReviewCount(BigInteger.valueOf(rating.getReviewCount()));
		oleryCategoryRatings.getCategoryRatings().put(WordUtils.capitalizeFully(rating.getTitle()), oleryCategoryRating);
	}

	private final Cache<String, OleryAccommodationData> getCache()
	{
		if (_cache == null)
		{
			CachingProvider provider = Caching.getCachingProvider();
			CacheManager cacheManager = provider.getCacheManager();
			MutableConfiguration<String, OleryAccommodationData> configuration = new MutableConfiguration<String, OleryAccommodationData>().setTypes(String.class, OleryAccommodationData.class);
			_cache = cacheManager.createCache("accommodationOleryData", configuration);
		}
		return _cache;
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

	private Cache<String, OleryAccommodationData> _cache = null;

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		return channelType;
	}
}
