package com.torkirion.eroam.microservice.accommodation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedSet;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.torkirion.eroam.microservice.accommodation.apidomain.RoomCancellationPolicyLine;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRC.FacilityGroup;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancellationPolicyLine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Functions
{
	public static final BigDecimal BD_1_NEG = new BigDecimal("-1");

	public static final BigDecimal BD_100 = new BigDecimal("100");

	public static void logMemAndYield()
	{
		System.gc();
		Thread.yield();
		log.debug("logMem::freeMemory:" + (Runtime.getRuntime().freeMemory() / 1024) + ", usedMemory:" + (Runtime.getRuntime().totalMemory() / 1024));
	}

	public static BigDecimal distance(LatitudeLongitude point1, LatitudeLongitude point2)
	{
		double theDistance = (Math.sin(Math.toRadians(point1.getLatitude().doubleValue())) * Math.sin(Math.toRadians(point2.getLatitude().doubleValue())) + Math.cos(Math.toRadians(point1.getLatitude().doubleValue()))
				* Math.cos(Math.toRadians(point2.getLatitude().doubleValue())) * Math.cos(Math.toRadians(point2.getLongitude().doubleValue() - point2.getLongitude().doubleValue())));

		double dVal = (Math.toDegrees(Math.acos(theDistance))) * (double)69.09 * (double)1.6093;
		return BigDecimal.valueOf(dVal);
	}

	public static BigDecimal distance2(LatitudeLongitude point1, LatitudeLongitude point2)
	{
		double lat1 = point1.getLatitude().doubleValue();
		double lat2 = point2.getLatitude().doubleValue();
		double lon1 = point1.getLongitude().doubleValue();
        double lon2 = point2.getLongitude().doubleValue();
        double el1 = 0;
        double el2 = 0;
        
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

		double theDistance = Math.sqrt(distance) / (double)1000.0;
		return BigDecimal.valueOf(theDistance);
	}

	public static boolean isAccommodationCNXNonRefundable(SortedSet<RoomCancellationPolicyLine> cnxPolicyLines)
	{
		if (log.isDebugEnabled())
			log.debug("isAccommodationCNXNonRefundable::enter for " + cnxPolicyLines);
		LocalDate tomorrow = LocalDate.now().plusDays(2);
		int index = 0;
		for ( RoomCancellationPolicyLine roomCancellationPolicyLine : cnxPolicyLines )
		{
			// if the first entry is later than today, assume today is refundable
			if ( index == 0 && roomCancellationPolicyLine.getAsOf().isAfter(tomorrow) && !roomCancellationPolicyLine.getBefore())
			{
				if (log.isDebugEnabled())
					log.debug("isAccommodationCNXNonRefundable::no entry for today, assume refundable today");
				return false;
			}
			if ( roomCancellationPolicyLine.getPenalty() != null && roomCancellationPolicyLine.getPenalty().getAmount().compareTo(BigDecimal.ZERO) == 0 )
			{
				if (log.isDebugEnabled())
					log.debug("isAccommodationCNXNonRefundable::found zero penalty amount!  NOT nonrefundable");
				return false;
			}
			if ( roomCancellationPolicyLine.getPenaltyPercent() != null && roomCancellationPolicyLine.getPenaltyPercent().compareTo(BigDecimal.ZERO) == 0 )
			{
				if (log.isDebugEnabled())
					log.debug("isAccommodationCNXNonRefundable::found zero penalty percent!  NOT nonrefundable");
				return false;
			}
			index++;
		}
		return true;
	}
	
	public static boolean isTransferCNXNonRefundable(SortedSet<TransferCancellationPolicyLine> cnxPolicyLines)
	{
		for ( TransferCancellationPolicyLine cancellationPolicyLine : cnxPolicyLines )
		{
			if ( cancellationPolicyLine.getPenalty() != null && cancellationPolicyLine.getPenalty().getAmount().compareTo(BigDecimal.ZERO) == 0 )
			{
				return false;
			}
			if ( cancellationPolicyLine.getPenaltyPercent() != null && cancellationPolicyLine.getPenaltyPercent().compareTo(BigDecimal.ZERO) == 0 )
			{
				return false;
			}
		}
		return true;
	}
	
	public static List<String> flattenFacilityGroups(List<FacilityGroup> facilityGroups)
	{
		List<String> faciities = new ArrayList<>();
		for ( FacilityGroup facilityGroup : facilityGroups)
		{
			faciities.addAll(facilityGroup.getFacilities());
		}
		return faciities;
	}
	
	private static BigDecimal BD_2 = new BigDecimal("2.0");
	public static LatitudeLongitude center(LatitudeLongitude point1, LatitudeLongitude point2)
	{
		return new LatitudeLongitude(point1.getLatitude().add(point2.getLatitude()).divide(BD_2), point1.getLongitude().add(point2.getLongitude()).divide(BD_2));
	}

	public static Integer getNightsFromPrice(BigDecimal penalty, BigDecimal price1, BigDecimal price2, int nights, int rooms)
	{
		if (penalty == null || penalty.compareTo(BigDecimal.ZERO) == 0)
		{
			if (log.isDebugEnabled())
				log.debug("getNightsFromPrice::broken penalty, returning null");
			return null;
		}
		if (price1 != null && penalty.compareTo(price1) == 0)
		{
			if (log.isDebugEnabled())
				log.debug("getNightsFromPrice::penalty==price1, returning " + nights);
			return nights;
		}
		if (price2 != null && penalty.compareTo(price2) == 0)
		{
			if (log.isDebugEnabled())
				log.debug("getNightsFromPrice::penalty==price2, returning " + nights);
			return nights;
		}
		if (price1 != null && nights > 1)
		{
			BigDecimal nightsBD = BigDecimal.valueOf(nights);
			BigDecimal pricePerNight = price1.divide(nightsBD, 2, RoundingMode.HALF_EVEN);
			BigDecimal pricePerNightDown = price1.divide(nightsBD, 0, RoundingMode.DOWN);
			BigDecimal pricePerNightUp = price1.divide(nightsBD, 0, RoundingMode.UP);
			if (log.isDebugEnabled())
				log.debug("getNightsFromPrice::comparing penalty=" + penalty + " with nightly price1 " + pricePerNight + "/" + pricePerNightDown + "/" + pricePerNightUp);
			if (penalty.remainder(pricePerNight).compareTo(BigDecimal.ZERO) == 0)
			{
				int nightsPenalty = penalty.divide(pricePerNight).intValue();
				if (log.isDebugEnabled())
					log.debug("getNightsFromPrice::remainder1 = 0, returning " + nightsPenalty);
				return nightsPenalty;
			}
			if (penalty.remainder(pricePerNightDown).compareTo(BigDecimal.ZERO) == 0)
			{
				int nightsPenalty = penalty.divide(pricePerNightDown).intValue();
				if (log.isDebugEnabled())
					log.debug("getNightsFromPrice::remainder1 = 0, returning " + nightsPenalty);
				return nightsPenalty;
			}
			if (penalty.remainder(pricePerNightUp).compareTo(BigDecimal.ZERO) == 0)
			{
				int nightsPenalty = penalty.divide(pricePerNightUp).intValue();
				if (log.isDebugEnabled())
					log.debug("getNightsFromPrice::remainder1 = 0, returning " + nightsPenalty);
				return nightsPenalty;
			}
		}
		if (price2 != null && nights > 1)
		{
			BigDecimal nightsBD = BigDecimal.valueOf(nights);
			BigDecimal pricePerNight = price2.divide(nightsBD, 2, RoundingMode.HALF_EVEN);
			BigDecimal pricePerNightDown = price1.divide(nightsBD, 0, RoundingMode.DOWN);
			BigDecimal pricePerNightUp = price1.divide(nightsBD, 0, RoundingMode.UP);
			if (log.isDebugEnabled())
				log.debug("getNightsFromPrice::comparing penalty=" + penalty + " with nightly price2 " + pricePerNight + "/" + pricePerNightDown + "/" + pricePerNightUp);
			if (penalty.remainder(pricePerNight).compareTo(BigDecimal.ZERO) == 0)
			{
				int nightsPenalty = penalty.divide(pricePerNight).intValue();
				if (log.isDebugEnabled())
					log.debug("getNightsFromPrice::remainder2 = 0, returning " + nightsPenalty);
				return nightsPenalty;
			}
			if (penalty.remainder(pricePerNightDown).compareTo(BigDecimal.ZERO) == 0)
			{
				int nightsPenalty = penalty.divide(pricePerNightDown).intValue();
				if (log.isDebugEnabled())
					log.debug("getNightsFromPrice::remainder2 = 0, returning " + nightsPenalty);
				return nightsPenalty;
			}
			if (penalty.remainder(pricePerNightUp).compareTo(BigDecimal.ZERO) == 0)
			{
				int nightsPenalty = penalty.divide(pricePerNightUp).intValue();
				if (log.isDebugEnabled())
					log.debug("getNightsFromPrice::remainder2 = 0, returning " + nightsPenalty);
				return nightsPenalty;
			}
		}
		if (log.isDebugEnabled())
			log.debug("getNightsFromPrice::no match, returning null");
		return null;
	}

	public static String formatCurrencyDisplay(CurrencyValue currencyValue)
	{
		NumberFormat nF = NumberFormat.getCurrencyInstance();
		nF.setCurrency(Currency.getInstance(currencyValue.getCurrencyId()));
		return nF.format(currencyValue.getAmount());
	}

	public static java.util.Date convertGeoToTimestamp(XMLGregorianCalendar xml)
	{
		try
		{
			return xml.toGregorianCalendar().getTime();
		}
		catch (Exception e)
		{
			throw new java.lang.Error(e);
		}
	}

	public static String cleanHTML(String s)
	{
		if (s == null)
			return "";
		if (s.contains("<") || s.contains("&"))
		{
			s = s.replaceAll("<br>", " ");
			s = s.replaceAll("<br/>", " ");
			s = s.replaceAll("</br>", "");
			s = s.replaceAll("<br />", " ");
			s = s.replaceAll("<p>", "");
			s = s.replaceAll("<p/>", "");
			s = s.replaceAll("</p>", "");
			s = s.replaceAll("<b>", "");
			s = s.replaceAll("<b/>", "");
			s = s.replaceAll("</b>", "");
			s = s.replaceAll("<strong>", "");
			s = s.replaceAll("</strong>", "");
			s = s.replaceAll("<", "&lt;");
			s = s.replaceAll(">", "&gt;");
			s = s.replaceAll("&amp;", "&");
			s = s.replaceAll("&quot;", "'");
			s = s.replaceAll("&lt;b&gt;", "");
			s = s.replaceAll("&lt;/b&gt;", "");
			s = s.replaceAll("&lt;br&gt;", " ");
			s = s.replaceAll("&nbsp;", " ");
		}
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\r", "");
		return s.trim();
	}

	public static Date normaliseDate(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static LocalDate normaliseLocalDate(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Instant instant = c.getTime().toInstant();
		LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
		return localDate;
	}

	public static XMLGregorianCalendar convertTimeStampToGeo(java.util.Date date)
	{
		try
		{
			if (date == null)
				return null;
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			XMLGregorianCalendar xml = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			xml.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			return xml;
		}
		catch (DatatypeConfigurationException e)
		{
			throw new java.lang.Error(e);
		}
	}

	public static XMLGregorianCalendar convertLocalDateToGeo(LocalDate localDate)
	{
		if (localDate == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth(), 0, 0, 0);
		return convertTimeStampToGeo(calendar.getTime());
	}

	public static LocalDate convertGeoToLocalDate(XMLGregorianCalendar xmlGregorianCalendar)
	{
		if (xmlGregorianCalendar == null)
			return null;
		LocalDate localDate = LocalDate.of(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(), xmlGregorianCalendar.getDay());
		return localDate;
	}

	public static LocalDateTime convertGeoToLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar)
	{
		if (xmlGregorianCalendar == null)
			return null;
		LocalDateTime localDateTime = LocalDateTime.of(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(), xmlGregorianCalendar.getDay(), xmlGregorianCalendar.getHour(),
				xmlGregorianCalendar.getMinute(), xmlGregorianCalendar.getSecond());
		return localDateTime;
	}

	public static LocalDate fromYYYYMDD(String yyyymmdd)
	{
		return LocalDate.parse(yyyymmdd, dfYYYYMMDD);
	}

	private static final DateTimeFormatter dfYYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
