package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
public class EstablishmentRoomType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonProperty("RoomCode")
	private String roomCode;

	@JsonProperty("EstablishmentId")
	private Integer establishmentId;

	@JsonProperty("Title")
	private String title;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("ImageUrl")
	private String imageUrl;

	@JsonProperty("ImageId")
	private Integer imageId;
}

