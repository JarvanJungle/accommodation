package com.torkirion.eroam.microservice.cruise.apidomain;

import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Ship implements Serializable
{
	private Integer shipId;
	private String description;
	private String shipCode;

	private String shipName;

	@ApiModelProperty(notes = "Images for this ship", required = false)
	private SortedSet<Image> images = new TreeSet<>();

	@ApiModelProperty(notes = "The thumbnail image for this ship", required = false)
	private Image imageThumbnail;

	private List<Deck> decks;

	private List<CabinType> cabinTypes;

	private List<Facility> facilities;
}
