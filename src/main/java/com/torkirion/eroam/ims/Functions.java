package com.torkirion.eroam.ims;

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

}
