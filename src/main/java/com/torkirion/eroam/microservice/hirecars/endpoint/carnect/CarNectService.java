package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import com.torkirion.eroam.microservice.activities.apidomain.ActivityCancelRS;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.hirecars.apidomain.*;
import com.torkirion.eroam.microservice.hirecars.datadomain.CarSearchEntryRCData;
import com.torkirion.eroam.microservice.hirecars.datadomain.CarSearchEntryRCRepo;
import com.torkirion.eroam.microservice.hirecars.dto.DetailRQDTO;
import com.torkirion.eroam.microservice.hirecars.dto.HireCarSearchRQDTO;
import com.torkirion.eroam.microservice.hirecars.endpoint.HireCarServiceIF;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AllArgsConstructor
@Slf4j
public class CarNectService implements HireCarServiceIF
{
	private SystemPropertiesDAO propertiesDAO;
	private CarSearchEntryRCRepo carSearchEntryRCRepo;


	public static final String CHANNEL = "CARNECT";

	public static final String CHANNEL_PREFIX = "CN";

	public static final BigInteger MAX_RESPONSE_OF_SEARCH = new BigInteger("20");
	public static final BigDecimal AMOUNT_0 = new BigDecimal("0.00");
	public static final String CURRENCY_DEFAULT = "EUR";

	@Override
	public Collection<HireCarResult> search(HireCarSearchRQDTO availSearchRQ)
	{
		log.debug("search::availSearchRQ: \n{}", JsonUtil.convertToPrettyJson(availSearchRQ));
		CarnectInterface carnectInterface = new CarnectInterface(propertiesDAO, availSearchRQ.getClient(), carSearchEntryRCRepo);
		try {
			List<HireCarResult> hireCarResults = carnectInterface.startSearch(availSearchRQ);
			log.debug("hireCarResults:: \n{}", JsonUtil.convertToPrettyJson(hireCarResults));
			ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
			log.debug("carSearchEntryRC::search::before::cacheCarSearchEntry");
			threadPoolExecutor.submit(() -> cacheCarSearchEntry(hireCarResults));
			log.debug("carSearchEntryRC::search::after::cacheCarSearchEntry");
			return hireCarResults;
		} catch (Exception e) {
			log.warn("search::e: {}", e.toString());
			return null;
		}
	}

	private void cacheCarSearchEntry(List<HireCarResult> hireCarResults) {
		log.debug("carSearchEntryRC::cacheCarSearchEntry::start");
		if(hireCarResults == null || hireCarResults.isEmpty()) {
			return;
		}
		List<SIPPBlock> sippBlocks = hireCarResults.get(0).getSippBlocks();
		List<CarSearchEntryRCData> carSearchEntryRCDataList = new ArrayList<>();
		for(SIPPBlock sippBlock : sippBlocks) {
			for(CarSearchEntry carSearchEntry : sippBlock.getCarSearchEntries()) {
				CarSearchEntryRCData carSearchEntryRCData = new CarSearchEntryRCData();
				carSearchEntryRCData.setVehicleId(carSearchEntry.getId());
				carSearchEntryRCData.setCarSearchEntryJson(JsonUtil.convertToJson(carSearchEntry));
				carSearchEntryRCData.setDateCreated(Calendar.getInstance().getTime());
				carSearchEntryRCDataList.add(carSearchEntryRCData);
			}
		}
		carSearchEntryRCRepo.saveAll(carSearchEntryRCDataList);
		log.debug("carSearchEntryRC::cacheCarSearchEntry::end");
	}

	@Override
	public HireCarDetailResult getDetail(DetailRQDTO detailRQDTO) throws Exception
	{
		log.debug("getDetail::detailRQDTO: \n{}", JsonUtil.convertToPrettyJson(detailRQDTO));
		CarnectInterface carnectInterface = new CarnectInterface(propertiesDAO, detailRQDTO.getClient(), carSearchEntryRCRepo);
		StartDetailRQDTO startDetailRQ = StartDetailRQDTO.makeStartDetailRQDtoFromDetailRQDto(detailRQDTO);
		return carnectInterface.startDetail(startDetailRQ);
	}

	@Override
	public HireCarBookRS book(String client, HireCarBookRQ bookRQ) throws Exception
	{
		log.debug("book::bookRQ: \n{}", JsonUtil.convertToPrettyJson(bookRQ));
		CarnectInterface carnectInterface = new CarnectInterface(propertiesDAO, client, carSearchEntryRCRepo);
		return carnectInterface.startBook(bookRQ);
	}

	@Override
	public HireCarCancelRS cancel(String client, HireCarCancelRQ cancelRQ) throws Exception {
		log.debug("cancel::cancelRQ: \n{}", JsonUtil.convertToPrettyJson(cancelRQ));
		CarnectInterface carnectInterface = new CarnectInterface(propertiesDAO, client, carSearchEntryRCRepo);
		return carnectInterface.startCancel(cancelRQ);
	}

	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("URL endpoint of CarNect API", "url", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The userid of the APi service", "username", FieldType.STRING, true, null));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The password for the service", "password", FieldType.STRING, true, null));
        
		return channelType;
	}

	public static final DateTimeFormatter df2YYYYMMDDTHHMMSS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	public static final DateTimeFormatter df2YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
