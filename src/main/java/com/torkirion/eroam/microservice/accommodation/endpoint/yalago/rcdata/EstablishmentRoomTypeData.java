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
@Table(name = "yalago_EstablishmentRoomType", indexes = {@Index(name = "yalago_EstablishmentRoomType_idx_establishmentId",  columnList="establishmentId", unique = false)})
@Data
@ToString
public class EstablishmentRoomTypeData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 255)
	private String roomCode;

	@Column
	private Integer establishmentId;

	@Column(length = 255)
	private String title;

	@Column(length = 2000)
	private String description;

	@Column(length = 255)
	private String imageUrl;

	@Column
	private Integer imageId;
}

