package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "iataAirport", indexes = { @Index(name = "iataAirport_iataCode", columnList = "iataCode", unique = false) })
@Data
public class IataAirport
{
	@Id
	@Column
	private Integer openflightsIndex;

	@Column(length = 100)
	private String airportName;

	@Column(length = 100)
	private String cityname;

	@Column(length = 100)
	private String country;

	@Column(length = 3)
	private String iataCode;

	@Column(length = 4)
	private String icao;

	@Column
	private BigDecimal latitude;

	@Column(length = 100)
	private BigDecimal longitude;

	@Column(length = 100)
	private BigDecimal altitude;

	@Column(length = 100)
	private BigDecimal timezone;

	@Column(length = 10)
	private String dst;
}
