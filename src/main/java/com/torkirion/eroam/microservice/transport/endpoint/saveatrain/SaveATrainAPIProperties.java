package com.torkirion.eroam.microservice.transport.endpoint.saveatrain;

import com.torkirion.eroam.microservice.datadomain.SystemProperty;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

public class SaveATrainAPIProperties {

    public SaveATrainAPIProperties(SystemPropertiesDAO properties, String site)
    {
        bookApiUrl = properties.getProperty(site, SaveATrainService.CHANNEL, "book_api_url");
        bookApiEmail = properties.getProperty(site, SaveATrainService.CHANNEL, "book_api_email");
        bookApiPassword = properties.getProperty(site, SaveATrainService.CHANNEL, "book_api_password");
        bookApiToken = properties.getProperty(site, SaveATrainService.CHANNEL, "book_api_token");

        searchApiUrl = properties.getProperty(site, SaveATrainService.CHANNEL, "search_api_url");
        searchApiEmail = properties.getProperty(site, SaveATrainService.CHANNEL, "search_api_email");
        searchApiPassword = properties.getProperty(site, SaveATrainService.CHANNEL, "search_api_password");
        searchApiToken = properties.getProperty(site, SaveATrainService.CHANNEL, "search_api_token");
    }

    public void saveBookApiToken(SystemPropertiesDAO properties, String site, String bookApiToken) {
        this.bookApiToken = bookApiToken;
        properties.saveSiteChannelProperty(site, SystemProperty.ProductType.TRANSPORT, SaveATrainService.CHANNEL, "book_api_token", bookApiToken);
    }

    public void saveSearchApiToken(SystemPropertiesDAO properties, String site, String searchApiToken) {
        this.searchApiToken = searchApiToken;
        properties.saveSiteChannelProperty(site, SystemProperty.ProductType.TRANSPORT, SaveATrainService.CHANNEL, "search_api_token", searchApiToken);
    }

    public String bookApiUrl;

    public String bookApiEmail;

    public String bookApiPassword;

    public String bookApiToken;

    public String searchApiUrl;

    public String searchApiEmail;

    public String searchApiPassword;

    public String searchApiToken;

    public String sourceMarket;

    public boolean useGZip;

    public String proxyHost;

    public Integer proxyPort;

    public boolean bypassProxy = false;

    public  boolean bypassBooking = false;

    public boolean testBooking = false;

    public boolean markupCNXValues = false;

    public int connectionTimeout = 60; // seconds

    public boolean allowZeroCommissionProduct = true;
}
