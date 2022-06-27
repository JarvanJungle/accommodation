package com.torkirion.eroam.microservice.cruise.endpoint.traveltek;

import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.Data;

@Data
public class TravelTekProperties
{
	public TravelTekProperties(SystemPropertiesDAO properties, String site, String channel)
	{
		apiUrl = properties.getProperty(site, channel, "apiurl");
		ftpHostname = properties.getProperty(site, channel, "ftphostname");
		ftpUsername = properties.getProperty(site, channel, "ftpusername");
		ftpPassword = properties.getProperty(site, channel, "ftppassword");
	}
	String apiUrl;
	String ftpHostname;
	String ftpUsername;
	String ftpPassword;
}
