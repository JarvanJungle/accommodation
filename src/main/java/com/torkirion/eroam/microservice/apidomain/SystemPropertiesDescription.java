package com.torkirion.eroam.microservice.apidomain;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class SystemPropertiesDescription implements Serializable
{
	public enum FieldType
	{
		STRING, INTEGER, BOOLEAN
	}

	@Data
	@AllArgsConstructor
	public static class Field
	{
		private String label;

		private String fieldName;

		private FieldType type;

		private Boolean mandatory;

		private String defaultValue;
	}
	
	@Data
	public static class ChannelType
	{
		private List<Field> fields = new ArrayList<>();
	}

	@Data
	public static class ProductType
	{
		private Map<String, ChannelType> channels = new HashMap<>();
	}

	private Map<com.torkirion.eroam.microservice.datadomain.SystemProperty.ProductType, ProductType> productTypes = new HashMap<>();
}
