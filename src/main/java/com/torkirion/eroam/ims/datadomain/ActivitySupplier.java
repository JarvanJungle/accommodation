package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
@Table(name = "imsactivitysupplier")
@Data
public class ActivitySupplier
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
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="activitySupplier", cascade = CascadeType.ALL)
	private Set<ActivitySupplierAgeBand> agebands;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="activitySupplier")
	private Set<Activity> activities;

	@Column
	private LocalDateTime lastUpdated;

	@Override
	public String toString()
	{
		return "ActivitySupplier [id=" + id + ", externalSupplierId=" + externalSupplierId + ", name=" + name + ", defaultMargin=" + defaultMargin + ", showSupplierName=" + showSupplierName
				+ ", agebands=" + agebands + "]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivitySupplier other = (ActivitySupplier) obj;
		if (defaultMargin == null)
		{
			if (other.defaultMargin != null)
				return false;
		}
		else if (!defaultMargin.equals(other.defaultMargin))
			return false;
		if (externalSupplierId == null)
		{
			if (other.externalSupplierId != null)
				return false;
		}
		else if (!externalSupplierId.equals(other.externalSupplierId))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (showSupplierName == null)
		{
			if (other.showSupplierName != null)
				return false;
		}
		else if (!showSupplierName.equals(other.showSupplierName))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultMargin == null) ? 0 : defaultMargin.hashCode());
		result = prime * result + ((externalSupplierId == null) ? 0 : externalSupplierId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((showSupplierName == null) ? 0 : showSupplierName.hashCode());
		return result;
	}
}
