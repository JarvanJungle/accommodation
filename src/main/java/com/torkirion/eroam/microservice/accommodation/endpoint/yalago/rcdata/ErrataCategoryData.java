package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "yalago_ErrataCategory")
@Data
public class ErrataCategoryData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer ErrataCategoryId;

	@Column(length = 255)
	private String title;

	@Column(length = 255)
	private String definition;
}
