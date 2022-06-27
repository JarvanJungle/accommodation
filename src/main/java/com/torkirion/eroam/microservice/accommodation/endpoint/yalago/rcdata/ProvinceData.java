package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "yalago_Province")
@Data
public class ProvinceData implements Serializable
{
	@Id
	private Integer provinceId;

	@Column
	private Integer countryId;
	
	@Column(length = 255)
	private String title;
}
