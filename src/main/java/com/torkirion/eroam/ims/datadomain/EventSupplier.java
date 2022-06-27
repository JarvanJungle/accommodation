package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "imseventsupplier")
@Data
public class EventSupplier
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 20)
	private String externalSupplierId;

	@Column(length = 200)
	private String name;

	@Column
	private BigDecimal defaultMargin;

	@Column
	private Boolean showSupplierName;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="eventSupplier")
	private Set<Event> events;

	@Column
	private LocalDateTime lastUpdated;

	@Override
	public String toString()
	{
		return "EventSupplier [id=" + id + ", externalSupplierId=" + externalSupplierId + ", name=" + name + ", defaultMargin=" + defaultMargin + ", showSupplierName=" + showSupplierName + "]";
	}
}
