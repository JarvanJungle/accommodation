package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "jayride_AirportTerminal")
@Data
public class AirportTerminalData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 100)
	private String id;

	@Column(length = 4)
	private String iata;

	@Column(length = 255)
	private String terminalName;

	@Column(length = 255)
	private String fullAirportTerminalName;

	@Column(length = 2)
	private String countryCode;

	@Column
	private BigDecimal latitude;

	@Column
	private BigDecimal longitude;
}
