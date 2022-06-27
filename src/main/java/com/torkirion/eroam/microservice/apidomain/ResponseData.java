package com.torkirion.eroam.microservice.apidomain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class ResponseData<T>
{
	private Integer status = 0;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	private List<ResponseExtraInformation> errors = new ArrayList<>();

	private List<ResponseExtraInformation> warnings = new ArrayList<>();

	public ResponseData(T data)
	{
		this.data = data;
	}

	public ResponseData(Integer errorCode, APIError apiError)
	{
		ResponseExtraInformation e = new ResponseExtraInformation(apiError.getCode().toString(), apiError.getText());
		errors.add(e);
		this.status = errorCode;
	}

	public ResponseData(Integer errorCode, List<ResponseExtraInformation> e)
	{
		errors = e;
		this.status = errorCode;
	}
}
