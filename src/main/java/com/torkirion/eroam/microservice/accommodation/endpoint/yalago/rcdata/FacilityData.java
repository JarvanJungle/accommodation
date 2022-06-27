package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "yalago_Facility")
@Data
public class FacilityData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer facilityId;

	@Column(length = 255)
	private String facilityGroup;

	@Column(length = 255)
	private String facilityType;

	@Column(length = 255)
	private String title;
}
