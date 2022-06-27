package com.torkirion.eroam.microservice.transport.endpoint;

public interface SaveATrainHttpExecution<T> {
    SaveATrainDataResponse<T> execute();
}
