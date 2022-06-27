package com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "yalago_SupplierBoardType")
@Data
public class SupplierBoardTypeData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private Integer supplierBoardTypeId;
	
	@Column(length = 255)
	private String title;
}
