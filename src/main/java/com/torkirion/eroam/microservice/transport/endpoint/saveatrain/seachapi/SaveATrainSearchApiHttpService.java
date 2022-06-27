package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainAPIProperties;
import org.apache.http.HttpMessage;

public class SaveATrainSearchApiHttpService extends HttpService  {

    private SaveATrainAPIProperties saveATrainProperties;

    public SaveATrainSearchApiHttpService(SaveATrainAPIProperties saveATrainProperties) {
        this.saveATrainProperties = saveATrainProperties;
    }

    @Override
    protected void addHeaders(HttpMessage httpMessage) {
        httpMessage.setHeader("Content-Type", "application/json");
    }

    @Override
    protected String getUrl() {
        return saveATrainProperties.searchApiUrl;
    }
}
