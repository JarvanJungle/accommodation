package com.torkirion.eroam.ims.datadomain;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "imssupplier")
@Data
public class Supplier
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Boolean forAccommodation;
	
	@Column
	private Boolean forActivities;
	
	@Column
	private Boolean forEvents;
	
	@Column
	private Boolean forMerchandise;
	
	@Column
	private Boolean forTransportation;

	@Column
	private BigDecimal defaultMargin;

	@Column
	private Boolean showSupplierName;

	@Column(length = 40)
	private String externalSupplierId;
	
	@Column(length = 100)
	private String supplierName;
	
	@Column(length = 100)
	private String reservationsName;
	
	@Column(length = 100)
	private String reservationsEmail;
	
	@Column(length = 20)
	private String reservationsPhone;
	
	@Column(length = 100)
	private String contractingName;
	
	@Column(length = 100)
	private String contractingEmail;
	
	@Column(length = 20)
	private String contractingPhone;
	
	@Column(length = 100)
	private String customerserviceName;
	
	@Column(length = 100)
	private String customerserviceEmail;
	
	@Column(length = 20)
	private String customerservicePhone;
	
	@Column(length = 100)
	private String gmName;
	
	@Column(length = 100)
	private String gmEmail;
	
	@Column(length = 20)
	private String gmPhone;
	
	@Column(length = 100)
	private String accountsName;
	
	@Column(length = 100)
	private String accountsEmail;
	
	@Column(length = 20)
	private String accountsPhone;
	
	@Column
	private LocalDateTime lastUpdated;

}
