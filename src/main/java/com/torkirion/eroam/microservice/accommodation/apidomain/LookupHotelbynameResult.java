package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LookupHotelbynameResult implements Comparable<LookupHotelbynameResult>
{
	@ApiModelProperty(notes = "The full code of the hotel")
	private String code;

	@ApiModelProperty(notes = "The name of this hotel")
	private String description;

	@JsonIgnore
	private Integer order;

	@Override
	public int compareTo(LookupHotelbynameResult other)
	{
		return this.order.compareTo(other.order);
	}
}
