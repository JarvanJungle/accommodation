package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import com.torkirion.eroam.HttpService;
import org.apache.http.HttpMessage;

public class CarnectHttpService extends HttpService  {

    private CarnectApiProperties carnectApiProperties;

    private String soapAction;

    public CarnectHttpService(CarnectApiProperties carnectApiProperties, String soapAction) {
        this.carnectApiProperties = carnectApiProperties;
        this.soapAction = soapAction;
    }

    @Override
    protected void addHeaders(HttpMessage httpMessage) {
        httpMessage.addHeader("Content-Type", "text/xml; charset=utf-8");
        httpMessage.addHeader("SOAPAction", this.soapAction);
    }

    @Override
    protected String getUrl() {
        //return "https://ota2007a.micronnexus-staging.com";
        return carnectApiProperties.carnectURL;
    }
}
