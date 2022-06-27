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
@Table(name = "yalago_EstablishmentExtra", indexes = {@Index(name = "yalago_EstablishmentExtra_idx_establishmentId",  columnList="establishmentId", unique = false)})
@Data
public class EstablishmentExtraData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer masterExtraId;

	@Column
	private Integer establishmentId;

	@Column(length = 255)
	private String title;

	@Column(length = 255)
	private String type;
}
