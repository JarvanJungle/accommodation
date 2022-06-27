package com.torkirion.eroam.microservice.activities.endpoint.hotelbeds;

import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

public class ActivityHBAPIProperties {
    public ActivityHBAPIProperties(SystemPropertiesDAO properties, String site)
    {
        url = properties.getProperty(site, com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelbedsService.CHANNEL, "url");
        apikey = properties.getProperty(site, com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelbedsService.CHANNEL, "apikey");
        secret = properties.getProperty(site, com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelbedsService.CHANNEL, "secret");
        bypassBooking = properties.getProperty(site, com.torkirion.eroam.microservice.activities.endpoint.hotelbeds.HotelbedsService.CHANNEL, "bypassBooking", false);
    }

    String url;

    String apikey;

    String secret;

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

    String accept = "application/json";

    String acceptEncoding = "gzip";

    String cancellationFlag = "True";

    String basePathActivityBooking = "/activity-api/3.0";

    String basePathActivityContent = "/activity-content-api/3.0";
}
