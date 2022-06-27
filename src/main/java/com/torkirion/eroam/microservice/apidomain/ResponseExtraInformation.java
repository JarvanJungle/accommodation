package com.torkirion.eroam.microservice.apidomain;

import java.io.Serializable;

import lombok.ToString;

@ToString
public class ResponseExtraInformation implements Serializable
{
    public ResponseExtraInformation(String code, String text)
	{
		super();
		this.code = code;
		this.text = text;
	}
	public String getCode()
	{
		return code;
	}
	public void setCode(String code)
	{
		this.code = code;
	}
	public String getText()
	{
		return text;
	}
	public void setText(String text)
	{
		this.text = text;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String text;
}
