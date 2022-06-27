package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "viatorv2_unavailable", indexes = { @Index(name = "viatorv2_unavailable_productCode", columnList = "productCode", unique = false) })
@Data
@ToString
public class ViatorV2UnavailableData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 50)
	private String productCode;

	@Column(length = 100)
	private String productOptionCode;

	@Column
	private LocalTime time;

	@Column(columnDefinition = "TEXT")
	private String unavailable;
}
