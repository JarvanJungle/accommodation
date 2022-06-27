package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "yalago_EstablishmentExtraOption", indexes = {@Index(name = "yalago_EstablishmentExtraOption_idx_masterExtraId",  columnList="masterExtraId", unique = false)})
@Data
public class EstablishmentExtraOptionData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer masterExtraOptionId;

	@Column
	private Integer masterExtraId;

	@Column
	private Integer sortOrder;

	@Column(length = 255)
	private String title;
}
