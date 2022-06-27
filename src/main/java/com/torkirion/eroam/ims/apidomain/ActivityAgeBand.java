package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import lombok.Data;

@Data
public class ActivityAgeBand implements Comparable<ActivityAgeBand>
{
	private Integer id;

	private String bandName;

	private Integer minAge;
	
	private Integer maxAge;

	@Override
	public int compareTo(ActivityAgeBand o)
	{
		// maximum to minimum
		return maxAge.compareTo(o.maxAge) * -1;
	}
}
