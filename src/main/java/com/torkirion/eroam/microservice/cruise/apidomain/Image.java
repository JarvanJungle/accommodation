package com.torkirion.eroam.microservice.cruise.apidomain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Image implements Serializable, Comparable<Image>
{
	private String imageURL;

	private String imageDescription;

	@Override
	public int compareTo(Image image) {
		return this.imageURL.compareTo(image.getImageURL());
	}
}
