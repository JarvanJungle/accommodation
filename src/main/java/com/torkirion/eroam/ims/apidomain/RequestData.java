package com.torkirion.eroam.ims.apidomain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class RequestData<T> {

    private String sessionID;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public RequestData(T data) {
        this.data = data;
    }

    public RequestData() {
    }
}
