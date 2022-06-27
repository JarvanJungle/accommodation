package com.torkirion.eroam.microservice.merchandise.endpoint.ims;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.ims.apidomain.Address;
import com.torkirion.eroam.ims.datadomain.EventMerchandiseLink;
import com.torkirion.eroam.ims.datadomain.Merchandise;
import com.torkirion.eroam.ims.datadomain.MerchandiseSale;
import com.torkirion.eroam.ims.services.DataService;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRS;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.config.ThreadLocalAwareThreadPool;
import com.torkirion.eroam.microservice.merchandise.apidomain.*;
import com.torkirion.eroam.microservice.merchandise.apidomain.Booking.ItemStatus;
import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseBookRQ.MerchandiseRequestItem;
import com.torkirion.eroam.microservice.merchandise.dto.*;
import com.torkirion.eroam.microservice.merchandise.endpoint.MerchandiseServiceIF;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class IMSService implements MerchandiseServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private DataService imsDataService;

	public static final String CHANNEL = "LOCALIMS";

	public static final String CHANNEL_PREFIX = "IM";

	public Set<MerchandiseResult> search(AvailSearchRQDTO availSearchRQ)
	{
		log.info("search::search(AvailSearchRQDTO)=" + availSearchRQ);

		long timer1 = System.currentTimeMillis();

		Set<MerchandiseResult> results = new HashSet<>();
		try
		{
			Integer merchandiseID = null;
			if (availSearchRQ.getMerchandiseId() != null && availSearchRQ.getMerchandiseId().startsWith(CHANNEL_PREFIX))
			{
				merchandiseID = Integer.parseInt(availSearchRQ.getMerchandiseId().substring(CHANNEL_PREFIX.length()));
			}
			List<Merchandise> allMerchandise = new ArrayList<>();
			if (merchandiseID == null)
				allMerchandise = imsDataService.getMerchandiseRepo().findAll();
			else
			{
				Optional<Merchandise> m = imsDataService.getMerchandiseRepo().findById(merchandiseID);
				if (m.isPresent())
				{
					allMerchandise.add(m.get());
				}
			}
			log.debug("search::testing " + allMerchandise.size() + " merch");
			for (Merchandise m : allMerchandise)
			{
				if (availSearchRQ.getBrand() != null)
				{
					if (availSearchRQ.getBrand().equals("") && (m.getEventMerchandiseLinks() == null || m.getEventMerchandiseLinks().size() == 0))
					{
						log.debug("search::no brand asked for, no brand found");
						MerchandiseResult merchandiseResult = makeMerchandiseResult(m, availSearchRQ, false);
						if (merchandiseResult != null)
						{
							results.add(merchandiseResult);
						}
					}
					else
					{
						if (m.getEventMerchandiseLinks() != null)
						{
							for (EventMerchandiseLink ml : m.getEventMerchandiseLinks())
							{
								log.debug("search::brand asked for, brand found");
								if (ml.getEventSeries().getName().equals(availSearchRQ.getBrand()))
								{
									MerchandiseResult merchandiseResult = makeMerchandiseResult(m, availSearchRQ, false);
									if (merchandiseResult != null)
									{
										results.add(merchandiseResult);
									}
									break;
								}
							}
						}
					}
				}
				else
				{
					MerchandiseResult merchandiseResult = makeMerchandiseResult(m, availSearchRQ, false);
					if (merchandiseResult != null)
					{
						results.add(merchandiseResult);
					}
				}
			}
			return results;
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		log.info("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	@Override
	public MerchandiseResult rateCheck(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter for " + rateCheckRQDTO);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(rateCheckRQDTO.getClient(), 1);
		Callable<MerchandiseResult> callableTask = () -> {
		    return rateCheckThreaded(rateCheckRQDTO);
		};
		Future<MerchandiseResult> future = threadPoolExecutor.submit(callableTask);
		MerchandiseResult result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	protected MerchandiseResult rateCheckThreaded(RateCheckRQDTO rateCheckRQDTO) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("rateCheck::enter for " + rateCheckRQDTO);

		long timer1 = System.currentTimeMillis();

		try
		{
			Integer merchandiseId = (rateCheckRQDTO.getMerchandiseId().startsWith(IMSService.CHANNEL_PREFIX)
					? Integer.parseInt(rateCheckRQDTO.getMerchandiseId().substring(IMSService.CHANNEL_PREFIX.length()))
					: Integer.parseInt(rateCheckRQDTO.getMerchandiseId()));
			Optional<com.torkirion.eroam.ims.datadomain.Merchandise> merchandiseDataOpt = imsDataService.getMerchandiseRepo().findById(merchandiseId);
			if (!merchandiseDataOpt.isPresent())
			{
				throw new Exception("Merchandise not found");
			}
			com.torkirion.eroam.ims.datadomain.Merchandise merchandiseData = merchandiseDataOpt.get();
			com.torkirion.eroam.ims.datadomain.MerchandiseOption option = null;
			for (com.torkirion.eroam.ims.datadomain.MerchandiseOption o : merchandiseData.getOptions())
			{
				if (log.isDebugEnabled())
					log.debug("book::compare " + o.getId().toString() + " and " + rateCheckRQDTO.getOptionId());
				if (o.getId().toString().equals(rateCheckRQDTO.getOptionId()))
				{
					option = o;
					break;
				}
			}
			if (option == null)
			{
				throw new Exception("Option not found");
			}
			if (rateCheckRQDTO.getCount() != null && rateCheckRQDTO.getCount().intValue() != 0)
				validateItem(rateCheckRQDTO.getCount(), null, merchandiseData, option);

			AvailSearchRQDTO availSearchRQDTO = new AvailSearchRQDTO();
			MerchandiseResult merchandiseResult = makeMerchandiseResult(merchandiseData, availSearchRQDTO, true);
			boolean optionFound = false;
			for (com.torkirion.eroam.ims.datadomain.MerchandiseOption o : merchandiseData.getOptions())
			{
				if (o.getId().toString().equals(rateCheckRQDTO.getOptionId()) && o.getAllotment().intValue() > 0)
				{
					optionFound = true;
				}
			}

			if (!optionFound)
			{
				throw new Exception("Option not found");
			}

			log.info("rateCheck:: time taken = " + (System.currentTimeMillis() - timer1));
			return merchandiseResult;
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		return null;
	}

	@Override
	@Transactional
	public MerchandiseBookRS book(String client, MerchandiseBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book()::recevied " + bookRQ);
		ExecutorService threadPoolExecutor = new ThreadLocalAwareThreadPool(client, 1);
		Callable<MerchandiseBookRS> callableTask = () -> {
		    return bookThreaded(client, bookRQ);
		};
		Future<MerchandiseBookRS> future = threadPoolExecutor.submit(callableTask);
		MerchandiseBookRS result = future.get(30, TimeUnit.SECONDS);
		return result;
	}
	@Transactional
	protected MerchandiseBookRS bookThreaded(String client, MerchandiseBookRQ bookRQ)
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied " + bookRQ);

		long timer1 = System.currentTimeMillis();
		MerchandiseBookRS bookRS = new MerchandiseBookRS();
		bookRS.setInternalBookingReference(bookRQ.getInternalBookingReference());
		try
		{

			for (MerchandiseRequestItem bookingItem : bookRQ.getItems())
			{
				Integer merchandiseId = (bookingItem.getMerchandiseId().startsWith(IMSService.CHANNEL_PREFIX)
						? Integer.parseInt(bookingItem.getMerchandiseId().substring(IMSService.CHANNEL_PREFIX.length()))
						: Integer.parseInt(bookingItem.getMerchandiseId()));
				Optional<com.torkirion.eroam.ims.datadomain.Merchandise> merchandiseDataOpt = imsDataService.getMerchandiseRepo().findById(merchandiseId);
				if (!merchandiseDataOpt.isPresent())
				{
					throw new Exception("Merchandise not found");
				}
				com.torkirion.eroam.ims.datadomain.Merchandise merchandiseData = merchandiseDataOpt.get();
				com.torkirion.eroam.ims.datadomain.MerchandiseOption option = null;
				for (com.torkirion.eroam.ims.datadomain.MerchandiseOption o : merchandiseData.getOptions())
				{
					if (log.isDebugEnabled())
						log.debug("book::compare " + o.getId().toString() + " and " + bookingItem.getOptionId());
					if (o.getId().toString().equals(bookingItem.getOptionId()))
					{
						option = o;
						break;
					}
				}
				if (option == null)
				{
					throw new Exception("Option not found");
				}
				validateItem(bookingItem.getCount(), bookingItem.getSupplyRate(), merchandiseData, option);

				int remaining = option.getAllotment().intValue() - bookingItem.getCount().intValue();
				if ( remaining < 0)
				{
					throw new Exception("Allotment no longer available, requested " + bookingItem.getCount() + " less than available " + option.getAllotment());
				}
				option.setAllotment(remaining);
				option = imsDataService.getMerchandiseOptionRepo().save(option);
				if (log.isDebugEnabled())
					log.debug("book::updated option : " + option);
				MerchandiseSale merchandiseSale = new MerchandiseSale();
				merchandiseSale.setMerchandise(merchandiseData);
				merchandiseSale.setBookingDateTime(LocalDateTime.now());
				merchandiseSale.setName(merchandiseData.getName());
				merchandiseSale.setCurrency(option.getCurrency());
				merchandiseSale.setNettPrice(option.getNettPrice());
				merchandiseSale.setRrpPrice(option.getRrpPrice());
				merchandiseSale.setRrpCurrency(option.getRrpCurrency());
				merchandiseSale.setItemStatus(MerchandiseSale.ItemStatus.BOOKED);
				merchandiseSale.setOptionId(option.getId());
				merchandiseSale.setOptionName(option.getName());
				merchandiseSale.setCount(bookingItem.getCount());
				merchandiseSale.setCountryCodeOfOrigin(bookRQ.getCountryCodeOfOrigin());
				merchandiseSale.setTitle(bookRQ.getBooker().getTitle());
				merchandiseSale.setGivenName(bookRQ.getBooker().getGivenName());
				merchandiseSale.setSurname(bookRQ.getBooker().getSurname());
				merchandiseSale.setTelephone(bookRQ.getBooker().getTelephone());
				merchandiseSale.setInternalBookingReference(bookRQ.getInternalBookingReference());
				merchandiseSale.setInternalItemReference(bookingItem.getInternalItemReference());
				merchandiseSale = imsDataService.getMerchandiseSaleRepo().save(merchandiseSale);
				merchandiseData.getSales().add(merchandiseSale);
				imsDataService.getMerchandiseRepo().save(merchandiseData);
				if (log.isDebugEnabled())
					log.debug("book::saved sale : " + merchandiseData);

				MerchandiseBookRS.ResponseItem responseItem = new MerchandiseBookRS.ResponseItem();
				responseItem.setChannel(CHANNEL);
				responseItem.setBookingItemReference(merchandiseSale.getId().toString());
				responseItem.setInternalItemReference(bookingItem.getInternalItemReference());
				responseItem.setItemStatus(ItemStatus.BOOKED);
				bookRS.getItems().add(responseItem);
				if (bookRS.getBookingReference() == null)
					bookRS.setBookingReference(responseItem.getBookingItemReference());
				if (log.isDebugEnabled())
					log.debug("book::responseItem=" + responseItem);
			}
			log.info("book:: time taken = " + (System.currentTimeMillis() - timer1));
		}
		catch (Exception e)
		{
			log.error("book::threw exception " + e.toString(), e);
			ResponseExtraInformation error = new ResponseExtraInformation("500", e.getMessage());
			bookRS.getErrors().add(error);
		}
		return bookRS;
	}

	protected void validateItem(Integer count, CurrencyValue supplyRate, com.torkirion.eroam.ims.datadomain.Merchandise merchandiseData,
			com.torkirion.eroam.ims.datadomain.MerchandiseOption option) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("validateItem::enter for count " + count + " and allotment " + option.getAllotment());
		if (option.getAllotment().intValue() >= 0 && count > option.getAllotment().intValue())
		{
			throw new Exception("Allotment no longer available, requested " + 0 + " less than available " + option.getAllotment());
		}
		if (supplyRate != null)
		{
			if (log.isDebugEnabled())
				log.debug("validateItem::enter for getSupplyRate " + supplyRate.getAmount() + " and nett price " + option.getNettPrice());
			if (supplyRate.getAmount().compareTo(option.getNettPrice()) != 0)
			{
				throw new Exception("Price has changed");
			}
		}
		if (log.isDebugEnabled())
			log.debug("validateItem::ok");
	}

	/*
	 * public CancelRS cancel(String site, CancelRQ cancelRQ) { if (log.isDebugEnabled()) log.debug("cancel::received " +
	 * cancelRQ); long timer1 = System.currentTimeMillis(); try { HotelbedsInterface hotelBedsInterface = new
	 * HotelbedsInterface(propertiesDAO, site, CHANNEL); CancelRS cancelRS = hotelBedsInterface.cancel(cancelRQ);
	 * log.info("cancel:: time taken = " + (System.currentTimeMillis() - timer1)); return cancelRS; } catch (Exception e) {
	 * log.error("cancel::threw exception " + e.toString(), e); } return null; }
	 */
	/*
	 * public RetrieveRS retrieve(String site, RetrieveRQ retrieveRQ) { if (log.isDebugEnabled()) log.debug("retrieve::received "
	 * + retrieveRQ); long timer1 = System.currentTimeMillis(); try { HotelbedsInterface hotelBedsInterface = new
	 * HotelbedsInterface(propertiesDAO, site, CHANNEL); RetrieveRS retrieveRS = hotelBedsInterface.retrieve(retrieveRQ);
	 * log.info("retrieve:: time taken = " + (System.currentTimeMillis() - timer1)); return retrieveRS; } catch (Exception e) {
	 * log.error("retrieve::threw exception " + e.toString(), e); } return null; }
	 */
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	protected MerchandiseResult makeMerchandiseResult(com.torkirion.eroam.ims.datadomain.Merchandise merchandise, AvailSearchRQDTO availSearchRQDTO, boolean checkAllotments)
			throws Exception
	{
		log.debug("makeMerchandiseResult::enter for " + merchandise.getName());

		List<MerchandiseOption> options = makeMerchandiseOption(merchandise);
		if (options.size() > 0)
		{
			MerchandiseResult merchandiseResult = new MerchandiseResult();
			merchandiseResult.setOptions(options);
			BeanUtils.copyProperties(merchandise, merchandiseResult);
			merchandiseResult.setId(IMSService.CHANNEL_PREFIX + merchandise.getId());
			merchandiseResult.setChannelId(merchandise.getId().toString());
			merchandiseResult.setChannel(IMSService.CHANNEL);
			if (merchandise.getMerchandiseSupplier() != null && merchandise.getMerchandiseSupplier().getShowSupplierName() != null && merchandise.getMerchandiseSupplier().getShowSupplierName())
				merchandiseResult.setSupplierName(merchandise.getMerchandiseSupplier().getName());
			merchandiseResult.setMerchandiseCategory(merchandise.getMerchandiseCategory().getName());
			if (merchandise.getImagesJson() != null)
			{
				merchandiseResult.setImages(getObjectMapper().readValue(merchandise.getImagesJson(), new TypeReference<List<String>>()
				{}));
			}
			else
			{
				merchandiseResult.setImages(new ArrayList<>());
			}
			merchandiseResult.setBrands(new ArrayList<>());
			if (merchandise.getEventMerchandiseLinks() != null)
			{
				for (EventMerchandiseLink link : merchandise.getEventMerchandiseLinks())
				{
					String brandName = link.getEventSeries().getName();
					if (!merchandiseResult.getBrands().contains(brandName))
						merchandiseResult.getBrands().add(brandName);
				}
			}
			return merchandiseResult;
		}
		log.debug("makeMerchandiseResult::no options have availability");
		return null;
	}

	protected List<MerchandiseOption> makeMerchandiseOption(com.torkirion.eroam.ims.datadomain.Merchandise merchandise)
	{
		log.debug("makeMerchandiseOption::enter");

		List<MerchandiseOption> options = new ArrayList<>();
		for (com.torkirion.eroam.ims.datadomain.MerchandiseOption o : merchandise.getOptions())
		{
			if (o.getAllotment() > 0 || o.getAllotment() == -1)
			{
				MerchandiseOption merchandiseOption = new MerchandiseOption();
				BeanUtils.copyProperties(o, merchandiseOption);
				merchandiseOption.setTotalNetPrice(new CurrencyValue(o.getCurrency(), o.getNettPrice()));
				merchandiseOption.setTotalRetailPrice(new CurrencyValue(o.getRrpCurrency(), o.getRrpPrice()));
				options.add(merchandiseOption);
			}
		}
		return options;
	}

	private static final ObjectMapper getObjectMapper()
	{
		if (_objectMapper == null)
		{
			_objectMapper = new ObjectMapper();
			_objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return _objectMapper;
	}

	private static ObjectMapper _objectMapper;

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}
}
