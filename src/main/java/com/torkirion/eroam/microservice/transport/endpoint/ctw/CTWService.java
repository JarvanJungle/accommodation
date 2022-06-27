package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.cache.AirlineCacheUtil;
import com.torkirion.eroam.microservice.datadomain.Airline;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.apidomain.*;
import com.torkirion.eroam.microservice.transport.datadomain.IataAirport;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO;
import com.torkirion.eroam.microservice.transport.dto.RouteResult;
import com.torkirion.eroam.microservice.transport.dto.RouteResult.TransportationClass;
import com.torkirion.eroam.microservice.transport.endpoint.AbstractTransportService;
import com.torkirion.eroam.microservice.transport.endpoint.TransportServiceIF;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class CTWService extends AbstractTransportService implements TransportServiceIF
{
	private SystemPropertiesDAO propertiesDAO;

	private AirlineCacheUtil airlineCacheUtil;

	public static final String CHANNEL = "CTW";

	public static final String CHANNEL_PREFIX = "CT";

	@Transactional
	public Collection<AvailTransportSearchRS> search(AvailTransportSearchRQDTO availTransportSearchRQDTO)
	{
		log.debug("search::search(availTransportSearchRQDTO)=" + availTransportSearchRQDTO);

		long timer1 = System.currentTimeMillis();
		Collection<AvailTransportSearchRS> results = new HashSet<>();
		try
		{
			CTWInterface ctwInterface = new CTWInterface(propertiesDAO, availTransportSearchRQDTO.getClient(), airlineCacheUtil);
			results = ctwInterface.search(availTransportSearchRQDTO);
		}
      		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		log.debug("search::resultcount=" + results.size() + ", time taken = " + (System.currentTimeMillis() - timer1));
		return results;
	}

	@Transactional
	public TransportRateCheckRS rateCheck(String client, TransportRateCheckRQ rateCheckRQ) throws Exception
	{
		log.debug("rateCheck::search(rateCheckRQ): " + rateCheckRQ);

		long timer1 = System.currentTimeMillis();
		TransportRateCheckRS results = new TransportRateCheckRS();
		try
		{
			CTWInterface ctwInterface = new CTWInterface(propertiesDAO, client, airlineCacheUtil);
			results = ctwInterface.rateCheck(client, rateCheckRQ);
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		return results;
	}

	@Transactional
	public TransportBookRS book(String client, TransportBookRQ bookRQ) throws Exception
	{
		log.debug("book::enter(bookRQ): " + bookRQ);

		long timer1 = System.currentTimeMillis();
		TransportBookRS results = new TransportBookRS();
		try
		{
			CTWInterface ctwInterface = new CTWInterface(propertiesDAO, client, airlineCacheUtil);
			results = ctwInterface.book(client, bookRQ);
		}
		catch (Exception e)
		{
			log.error("search::threw exception " + e.toString(), e);
		}
		return results;
	}

	public TransportCancelRS cancel(String site, TransportCancelRQ cancelRQ) throws Exception
	{
		return null;
	}

	public static ChannelType getSystemPropertiesDescription() {
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The email for login access", "	", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The password for login access", "password", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("The root URL endpoint", "url", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("'pos' for CTW requests", "pos", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("'channel' for CTW requests", "channel", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("'country' for CTW requests", "country", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("'currency' for CTW requests", "currency", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("'travelAgencyCode' for CTW requests", "travelAgencyCode", FieldType.STRING, true, ""));
		channelType.getFields().add(new SystemPropertiesDescription.Field("'iataNumber' for CTW requests", "iataNumber", FieldType.STRING, true, ""));
		return channelType;
	}

	@Override
	protected Airline getAirline(String iataCode)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TransportationClass findCheapestClass(RouteResult routeResult)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getProvider()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String makeTransportSearchRSId(List<RouteResult> routeResults)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IataAirport getAirport(String iataCode)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
