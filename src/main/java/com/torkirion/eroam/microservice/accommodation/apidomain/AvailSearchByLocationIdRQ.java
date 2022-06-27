package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class AvailSearchByLocationIdRQ extends AvailSearchSetRQ implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8869014468164847110L;

	private String locationId;
}
