package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ActivitySupplier
{
	private Integer id;

	private String externalSupplierId;

	private String name;

	private BigDecimal defaultMargin;

	private Boolean showSupplierName;
	
	private SortedSet<ActivityAgeBand> ageBands = new TreeSet<>();
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
