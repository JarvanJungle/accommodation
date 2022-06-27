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
@Table(name = "country")
public class Country implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 2)
	private String countryID;
	
	@Column(length = 200)
	private String countryName;
}
