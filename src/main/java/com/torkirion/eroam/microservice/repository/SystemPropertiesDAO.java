package com.torkirion.eroam.microservice.repository;

import java.util.*;
import java.math.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.datadomain.SystemPropertyRepo;
import com.torkirion.eroam.microservice.datadomain.SystemProperty;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class SystemPropertiesDAO
{
	@Autowired
	private SystemPropertyRepo systemPropertyRepo;
	
	public SystemPropertiesDAO()
	{
	}

	public Set<String> getSiteChannelList()
	{
		//log.debug("getSiteChannelList::entered");
		checkSetup();
		Set<String> set = new TreeSet<>();
		for ( String key : propertiesMap.keySet())
		{
			String[] vars = key.split("-");
			set.add(vars[0] + "-" + vars[1]);
		}
		return set;
	}

	public Set<String> getSiteChannelList(String site)
	{
		//log.debug("getSiteChannelList::entered for site " + site);
		checkSetup();
		Set<String> set = new TreeSet<>();
		for ( String key : propertiesMap.keySet())
		{
			String[] vars = key.split("-");
			if ( site.equals(vars[0]))
				set.add(vars[1]);
		}
		return set;
	}

	public Set<String> getSiteChannelList(String site, SystemProperty.ProductType productType)
	{
		//log.debug("getSiteChannelList::entered for site " + site + " and productType" + productType.toString());
		checkSetup();
		Set<String> set = new TreeSet<>();
		for ( String key : propertiesMapWithProductType.keySet())
		{
			String[] vars = key.split("-");
			if ( site.equals(vars[0]) && productType.toString().equals(vars[1]))
				set.add(vars[2]);
		}
		return set;
	}

	public Set<String> getSiteList()
	{
		//log.debug("getSiteList::entered");
		checkSetup();
		Set<String> set = new TreeSet<>();
		for ( String key : propertiesMap.keySet())
		{
			String[] vars = key.split("-");
			set.add(vars[0]);
		}
		return set;
	}

	public Map<String, String> getSiteChannelPropertyList(String site, String channel)
	{
		//log.debug("getSiteChannelPropertyList::entered for " + site + "-" + channel);
		checkSetup();
		Map<String, String> list = new TreeMap<String, String>();
		for ( String key : propertiesMap.keySet())
		{
			String[] vars = key.split("-");
			if ( vars.length > 2 )
			{
				if ( vars[0].equals(site) && vars[1].equals(channel) )
				{
					list.put(vars[2], propertiesMap.get(key));
				}
			}
		}
		return list;
	}

	public Map<String, String> getSiteChannelPropertyList(String site, String channel, SystemProperty.ProductType productType)
	{
		//log.debug("getSiteChannelPropertyList::entered for " + site + "-" + channel + " and productType" + productType.toString());
		checkSetup();
		Map<String, String> list = new TreeMap<String, String>();
		for ( String key : propertiesMapWithProductType.keySet())
		{
			String[] vars = key.split("-");
			if ( vars.length > 2 )
			{
				if ( vars[0].equals(site) && vars[1].equals(productType.toString()) && vars[2].equals(channel) )
				{
					list.put(vars[3], propertiesMapWithProductType.get(key));
				}
			}
		}
		return list;
	}

	public void saveSiteChannelProperty(String site, com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType productType, String channel, String parameter, String value)
	{
		log.debug("saveSiteChannelProperties::entered for " + site + "-" + channel);
		
		SystemProperty systemProperty = new SystemProperty();
		List<SystemProperty> props = systemPropertyRepo.findBySiteAndProductTypeAndChannelAndParameter(site, productType, channel, parameter);
		if ( props.size() > 0)
			systemProperty = props.get(0);
		else
			systemProperty.setId(++maxId);
		systemProperty.setSite(org.apache.commons.lang3.StringUtils.trimToEmpty(site));
		systemProperty.setChannel(org.apache.commons.lang3.StringUtils.trimToEmpty(channel));
		systemProperty.setProductType(productType);
		systemProperty.setParameter(org.apache.commons.lang3.StringUtils.trimToEmpty(parameter));
		systemProperty.setValue(org.apache.commons.lang3.StringUtils.trimToEmpty(value));
		systemPropertyRepo.save(systemProperty);
		setup();
	}
	
	public String getProperty(String site, String channel, String parameter)
	{
		String property = propertiesMap.get(site + "-" + channel + "-" + parameter);
		checkSetup();
		if ( property == null || property.length() == 0 )
		{
			log.warn("getProperty::did not find " + site + "-" + channel + "-" + parameter + " and no default provided");
		}
		return propertiesMap.get(site + "-" + channel + "-" + parameter);
	}

	public String getProperty(String site, String channel, String parameter, String defaultValue)
	{
		String property = propertiesMap.get(site + "-" + channel + "-" + parameter);
		checkSetup();
		if ( property == null || property.length() == 0 )
		{
			return defaultValue;
		}
		return propertiesMap.get(site + "-" + channel + "-" + parameter);
	}

	public Integer getProperty(String site, String channel, String parameter, Integer defaultValue)
	{
		checkSetup();
		String i = propertiesMap.get(site + "-" + channel + "-" + parameter);
		if ( i != null )
		{
			try
			{
				Integer ii = Integer.parseInt(i);
				return ii;
			} 
			catch (NumberFormatException nfe)
			{
				log.warn("setup::" + site +"-" + channel + "-" + parameter + " is invalid integer");
				return defaultValue;
			}
		}
		else
		{
			return defaultValue;
		}
	}

	public BigDecimal getProperty(String site, String channel, String parameter, BigDecimal defaultValue)
	{
		checkSetup();
		String b = propertiesMap.get(site + "-" + channel + "-" + parameter);
		if ( b != null )
		{
			try
			{
				BigDecimal bd = new BigDecimal(b);
				return bd;
			} 
			catch (NumberFormatException nfe)
			{
				log.warn("setup::" + site +"-" + channel + "-" + parameter + " is invalid decimal");
				return defaultValue;
			}
		}
		else
		{
			return defaultValue;
		}
	}

	public Boolean getProperty(String site, String channel, String parameter, Boolean defaultValue)
	{
		checkSetup();
		String s = propertiesMap.get(site + "-" + channel + "-" + parameter);
		if ( s != null )
		{
			Boolean b = Boolean.parseBoolean(s);
			return b;
		}
		else
		{
			return defaultValue;
		}
	}

	private void checkSetup()
	{
		if (propertiesMap == null || propertiesMap.size() == 0)
		{
			//log.debug("checkSetup::systemPropertyRepo=" + systemPropertyRepo);
			setup();
		}
	}
	
	private synchronized void setup()
	{
		log.debug("setup::entered");
		Map<String, String> newPropertiesMap = new HashMap<String, String>();
		Map<String, String> newPropertiesMapWithProductType = new HashMap<String, String>();

		List<SystemProperty> systemPropertiesList = systemPropertyRepo.findAll();
		
		for ( SystemProperty systemProperty : systemPropertiesList )
		{
			newPropertiesMap.put(systemProperty.getSite() + "-" + systemProperty.getChannel() + "-" + systemProperty.getParameter(), systemProperty.getValue());
			newPropertiesMapWithProductType.put(systemProperty.getSite() + "-" + systemProperty.getProductType().toString() + "-" + systemProperty.getChannel() + "-" + systemProperty.getParameter(), systemProperty.getValue());
			if ( systemProperty.getId().intValue() > maxId)
				maxId = systemProperty.getId().intValue();
		}
		propertiesMap = newPropertiesMap;
		propertiesMapWithProductType = newPropertiesMapWithProductType;
		log.debug("setup::loaded " + propertiesMap.size() + " properties as " + propertiesMap);
	}

	private static Map<String, String> propertiesMap = new HashMap<>();
	private static Map<String, String> propertiesMapWithProductType = new HashMap<>();
	private static int maxId = 0;
}
