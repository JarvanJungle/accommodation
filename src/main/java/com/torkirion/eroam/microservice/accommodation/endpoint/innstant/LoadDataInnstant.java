package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "innstant_loaddata")
@Data
@ToString
public class LoadDataInnstant implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id = 0;

	@Column
	private LocalDate lastHotelUpdate;
}
