package com.torkirion.eroam.microservice.cruise.apidomain;

import java.io.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class AvailSearchByDestinationRQ extends AvailSearchRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8869014468164847110L;

	private String locationId;
}
