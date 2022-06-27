package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "viatorv2_products")
@Data
@ToString
public class ProductData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 50)
	private String productCode;

	@Column(length = 500)
	private String title;

	@Column
	private LocalDateTime lastUpdatedAt;

	@Column
	private LocalDateTime availLastUpdatedAt;

	@Column(columnDefinition = "TEXT")
	private String productJson;

	@Column(columnDefinition = "TEXT")
	private String availabilityJson;

}
