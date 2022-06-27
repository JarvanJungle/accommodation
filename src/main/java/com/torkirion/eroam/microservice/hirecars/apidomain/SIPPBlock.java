package com.torkirion.eroam.microservice.hirecars.apidomain;

import java.util.*;

public class SIPPBlock implements Comparable<SIPPBlock>
{
	private String sippCode;
	
	private String category;

	private String carType;

	private String transmission;

	private String drive;

	private String fuel;

	private Boolean aircon;

	private List<CarSearchEntry> carSearchEntries;

	public String getCarType()
	{
		return carType;
	}

	public void setCarType(String carType)
	{
		this.carType = carType;
	}

	public String getTransmission()
	{
		return transmission;
	}

	public void setTransmission(String transmission)
	{
		this.transmission = transmission;
	}

	public Boolean getAircon()
	{
		return aircon;
	}

	public void setAircon(Boolean aircon)
	{
		this.aircon = aircon;
	}

	public String getFuel()
	{
		return fuel;
	}

	public void setFuel(String fuel)
	{
		this.fuel = fuel;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public List<CarSearchEntry> getCarSearchEntries()
	{
		if ( carSearchEntries == null )
			carSearchEntries = new ArrayList<CarSearchEntry>();
		return carSearchEntries;
	}

	public void setCarSearchEntries(List<CarSearchEntry> carSearchEntries)
	{
		this.carSearchEntries = carSearchEntries;
	}

	@Override
	public int compareTo(SIPPBlock s)
	{
		String s1 = getCarType().concat(getAircon().toString()).concat(getCategory()).concat(getFuel()).concat(getTransmission());
		String s2 = s.getCarType().concat(s.getAircon().toString()).concat(s.getCategory()).concat(s.getFuel()).concat(s.getTransmission());
		return s1.compareTo(s2);
	}

	public String getSippCode()
	{
		return sippCode;
	}

	public void setSippCode(String sippCode)
	{
		this.sippCode = sippCode;
	}

	public String getDrive()
	{
		return drive;
	}

	public void setDrive(String drive)
	{
		this.drive = drive;
	}
}
