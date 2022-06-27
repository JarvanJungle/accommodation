package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.datadomain.SystemProperty;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainAPIProperties;
import org.apache.http.HttpMessage;

public class SaveATrainBookApiHttpService extends HttpService {

    private SaveATrainAPIProperties saveATrainProperties;

    public SaveATrainBookApiHttpService(SaveATrainAPIProperties saveATrainProperties) {
        this.saveATrainProperties = saveATrainProperties;
    }

    @Override
    protected void addHeaders(HttpMessage httpMessage) {
        httpMessage.setHeader("http_accept", "application/json");
        httpMessage.setHeader("Content-Type", "application/json");
        httpMessage.setHeader("x-agent-email", saveATrainProperties.bookApiEmail);
        httpMessage.setHeader("x-agent-token", saveATrainProperties.bookApiToken);
    }

    @Override
    protected String getUrl() {
        return saveATrainProperties.bookApiUrl;
    }
}
