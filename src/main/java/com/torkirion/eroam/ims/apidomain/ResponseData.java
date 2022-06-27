package com.torkirion.eroam.ims.apidomain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class ResponseData<T>
{
	private Integer status = 200;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	private Integer errorCode;

	private String errorMessage;

	public ResponseData(T data)
	{
		this.data = data;
	}

	public ResponseData(Integer errorCode, APIError error)
	{
		this.status = errorCode;
		this.errorMessage = error.getText();
		this.errorCode = error.getCode();
	}

}
