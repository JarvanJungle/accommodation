package com.torkirion.eroam.ims.apidomain;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SupplierSummary
{
	private Long id;

	private String externalSupplierId;
	
	private String supplierName;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
