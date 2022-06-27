package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "jayride_Airport")
@Data
public class AirportData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private String iata;

	@Column(length = 255)
	private String airportName;

	@Column(length = 2)
	private String countryCode;
}
