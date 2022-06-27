package com.torkirion.eroam.microservice.transport.endpoint;

import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;
import lombok.Getter;

@Getter
public class SaveATrainDataResponse<R> {

    public static <R> SaveATrainDataResponse success(R data) {
        return new SaveATrainDataResponse(data);
    }

    public static SaveATrainDataResponse fail(Code code, String errors) {
        return new SaveATrainDataResponse(code, errors);
    }

    private Code code;
    private String errors;
    private R data;

    private SaveATrainDataResponse(Code code, String errors) {
        this.code = code;
        this.errors = "(" + SaveATrainService.CHANNEL + ") " + errors;
    }

    private SaveATrainDataResponse(R data) {
        code = Code.SUCCESS;
        this.data = data;
    }

    public static enum Code {
        SUCCESS,
        TOKE_EXPIRED,
        CALL_FAIL,
        UNKNOWN
    }
}
