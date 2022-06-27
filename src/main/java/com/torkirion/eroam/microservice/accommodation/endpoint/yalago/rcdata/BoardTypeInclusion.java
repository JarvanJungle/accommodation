package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BoardTypeInclusion implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("BoardTypeInclusionId")
	private Integer boardTypeInclusionId;

	@JsonProperty("Title")
	private String title;
}
