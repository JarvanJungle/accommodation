package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "yalago_Country")
@Data
@ToString
public class CountryData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer countryId;

	@Column(length = 2)
	private String countryCode;

	@Column(length = 255)
	private String title;
}
