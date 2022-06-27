package com.torkirion.eroam.microservice.hirecars.apidomain;

public enum PaymentTiming
{
	PREPAID, POSTPAID, DEPOSIT;
	
	public static PaymentTiming makeFromRateQualifier(String s) 
	{
		if ( s == null )
			return null;
		if ( s.startsWith("POSTPAID") )
			return POSTPAID;
		else if ( s.startsWith("PREPAID") )
			return PREPAID;
		else if ( s.startsWith("PARTPAID") )
			return DEPOSIT;
		else
			return POSTPAID;
	}
}
