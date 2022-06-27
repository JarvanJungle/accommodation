package com.torkirion.eroam.microservice.hirecars.endpoint;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ApiResponse<R> implements Serializable {

    public static <R> ApiResponse success(R data) {
        return new ApiResponse(data);
    }

    public static ApiResponse fail(Code code, String errors) {
        return new ApiResponse(code, errors);
    }

    private Code code;
    private String errors;
    private R data;

    private ApiResponse(Code code, String errors) {
        this.code = code;
        this.errors = errors;
    }

    private ApiResponse(R data) {
        code = Code.SUCCESS;
        this.data = data;
    }

    public static enum Code {
        SUCCESS,
        TOKE_EXPIRED,
        CALL_FAIL,
        UNKNOWN,
        INCORRECT
    }
}
