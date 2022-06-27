package com.torkirion.eroam.microservice.accommodation.endpoint.youtravel;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "hotelbeds_static", indexes = { @Index(name = "hotelbeds_static_typeCode", columnList = "staticType, code", unique = false) })
@Data
@ToString
public class YoutravelStaticData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 20)
	private String staticType;

	@Column(length = 100)
	private String code;

	@Column(length = 1000)
	private String name;

	@Column(length = 5000)
	private String description;

	@Column(length = 20)
	private String simpleCode;

	@Column(length = 100)
	private String type;

	@Column(length = 100)
	private String cgroup;

	@Column(length = 100)
	private String facilityFlags;

	@Column
	private Boolean alternative;

	@Column
	private Integer distance;

	@Column(length = 100)
	private String corder;

	@Column
	private LocalDate dateFrom;

	@Column
	private LocalDate dateTo;
}
