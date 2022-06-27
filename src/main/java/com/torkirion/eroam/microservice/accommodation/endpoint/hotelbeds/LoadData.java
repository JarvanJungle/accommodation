package com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "hotelbeds_loaddata")
@Data
@ToString
public class LoadData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id = 0;

	@Column
	private LocalDate lastHotelUpdate;
}
