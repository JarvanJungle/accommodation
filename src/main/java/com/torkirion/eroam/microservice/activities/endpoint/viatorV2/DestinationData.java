package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "viatorv2_destination")
@Data
@ToString
public class DestinationData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer destinationId;

	@Column(length = 200)
	private String destinationName;

	@Column(length = 60)
	private String destinationType;

	@Column(length = 50)
	private String timeZone;

	@Column(length = 5)
	private String iataCode;

	@Column
	private Integer parentId;

	@Column(length = 50)
	private String lookupId;

	@Column(scale = 5, precision = 8)
	private BigDecimal latitude;

	@Column(scale = 5, precision = 8)
	private BigDecimal longitude;
}
