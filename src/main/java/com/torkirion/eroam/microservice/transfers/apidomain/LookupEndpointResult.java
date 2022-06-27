package com.torkirion.eroam.microservice.transfers.apidomain;

import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LookupEndpointResult implements Comparable<LookupEndpointResult>
{
	@ApiModelProperty(notes = "The type of endpoint.  May be used to make a pretty icon!")
	private EndpointType endpointType;

	@ApiModelProperty(notes = "The full code of the endpoint, to pass back into searchByCode")
	private String typeAndCode;

	@ApiModelProperty(notes = "The description of this endpoint")
	private String description;

	@JsonIgnore
	private Integer order;
	
	@Override
	public int compareTo(LookupEndpointResult other)
	{
		if ( this.endpointType.equals(other.endpointType))
			return this.order.compareTo(other.order);
		else
			return this.endpointType.toString().compareTo(other.endpointType.toString());
	}
}
