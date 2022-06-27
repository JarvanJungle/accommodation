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

@Entity
@Table(name = "yalago_Location")
@Data
@ToString
public class LocationData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer locationId;

	@Column
	private Integer provinceId;

	@Column(length = 255)
	private String title;
}
