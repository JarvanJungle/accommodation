package com.torkirion.eroam.microservice.datadomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "systemproperties")
public class SystemProperty implements Serializable
{
	public static enum ProductType
	{
		ACCOMMODATION, ACTIVITIES, EVENTS, MERCHANDISE, TRANSFERS, TRANSPORT, CRUISE, HIRECAR
	}
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	private Integer id;
	
	@Column(length = 50)
	private String site;
	
	@Enumerated(EnumType.STRING)
    private ProductType productType;
	
	@Column(length = 50)
	private String channel;
	
	@Column(length = 50)
	private String parameter;
	
	@Column(length = 255)
	private String value;
}
