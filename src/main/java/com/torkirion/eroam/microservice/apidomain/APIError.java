package com.torkirion.eroam.microservice.apidomain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class APIError
{
    public APIError(Integer code, String text)
	{
		super();
		this.code = code;
		this.text = text;
	}
	private Integer code;
    private String text;
}
