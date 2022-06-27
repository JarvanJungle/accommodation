package com.torkirion.eroam.microservice.repository;

import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Collator;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.datadomain.Country;

import lombok.extern.slf4j.Slf4j;

//@Repository
@Service

@Slf4j
public class CountryDAO
{
	public CountryDAO(DataSource dataSource)
	{
		if (countryMap == null)
		{
			//setup(dataSource);
		}
	}
	
	public Country getCountry(String countryID)
	{
		return countryMap.get(countryID);
	}

	private synchronized void setup(DataSource dataSource)
	{
		List<Country> countries = new ArrayList<Country>();

		Connection conn = null;
		try
		{
			conn = dataSource.getConnection();

			String sql = "select * from country";
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery(sql);
			while (rs.next())
			{
				String countrycode = rs.getString("COUNTRYID");
				String countryname = rs.getString("COUNTRYNAME");
				countries.add(new Country(countrycode, countryname));
			}
			log.debug("read::setup loaded " + countries.size() + " countries");
		}
		catch (Exception e)
		{
			getLogger().error("setup::threw exception " + e.toString(), e);
		}
		finally
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (Exception e)
				{
					getLogger().error("setup::threw exception " + e.toString(), e);
				}
			}
		}

		Collections.sort(countries, new CountryComparator());
		countryMap = new HashMap<String, Country>();
		for (Country country : countries)
		{
			countryMap.put(country.getCountryID(), country);
		}
	}

	private static Map<String, Country> countryMap = null;

	private static class CountryComparator implements Comparator<Country>
	{
		private Comparator<Object> comparator;

		CountryComparator()
		{
			comparator = Collator.getInstance();
		}

		public int compare(Country o1, Country o2)
		{
			return comparator.compare(o1.getCountryID(), o2.getCountryID());
		}
	}

	/**
	 * @return the Log4J logger object
	 */
	protected synchronized org.apache.logging.log4j.Logger getLogger()
	{
		if (l4jLogger == null)
		{
			l4jLogger = org.apache.logging.log4j.LogManager.getLogger(this.getClass().getName());
		}
		return l4jLogger;
	}

	/*
	 * The logging interface
	 */
	private transient static org.apache.logging.log4j.Logger l4jLogger = null;
}
