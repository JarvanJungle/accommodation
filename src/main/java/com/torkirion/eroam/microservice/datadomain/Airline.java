package com.torkirion.eroam.microservice.datadomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCData.Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "airline")
public class Airline implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 3)
	private String iataCode;
	
	@Column(length = 5)
	private String icaoCode;
	
	@Column(length = 100)
	private String airline;
	
	@Column(length = 100)
	private String callsign;
	
	@Column(length = 50)
	private String country;
	
	@Column(length = 255)
	private String comments;
}
