package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MerchandiseSupplier
{
	private Integer id;

	private String externalSupplierId;

	private String name;

	private BigDecimal defaultMargin;

	private Boolean showSupplierName;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
