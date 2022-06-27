package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "yalago_EstablishmentText")
@Data
public class EstablishmentTextData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer establishmentId;

	@Column(length = 3000)
	private String summary;

	@Column(columnDefinition="TEXT")
	private String description;
}
