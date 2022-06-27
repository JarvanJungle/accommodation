package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

public class CarnectApiProperties {
    public CarnectApiProperties(SystemPropertiesDAO properties, String site)
    {
        carnectURL = properties.getProperty(site, "CARNECT", "url");
        username = properties.getProperty(site, "CARNECT", "username");
        password = properties.getProperty(site, "CARNECT", "password");
    }

    String carnectURL;

    String username;

    String password;

    String sourceMarket;

    boolean useGZip;

    String proxyHost;

    Integer proxyPort;

    boolean bypassProxy = false;

    boolean bypassBooking = false;

    boolean testBooking = false;

    boolean markupCNXValues = false;

    int connectionTimeout = 60; // seconds

    boolean allowZeroCommissionProduct = true;
}
