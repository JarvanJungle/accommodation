package com.torkirion.eroam.ims.datadomain;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "imsactivitysupplierageband")
@Data
public class ActivitySupplierAgeBand
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 50)
	private String bandName;

	@Column(scale=0)
	private Integer minAge;

	@Column(scale=0)
	private Integer maxAge;

	@ManyToOne
    @JoinColumn(name="activitySupplier_id", nullable=false)
	private ActivitySupplier activitySupplier;

	@Override
	public String toString()
	{
		return "ActivitySupplierAgeBand [id=" + id + ", bandName=" + bandName + ", minAge=" + minAge + ", maxAge=" + maxAge + "]";
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
		ActivitySupplierAgeBand other = (ActivitySupplierAgeBand) obj;
		if (bandName == null)
		{
			if (other.bandName != null)
				return false;
		}
		else if (!bandName.equals(other.bandName))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (maxAge == null)
		{
			if (other.maxAge != null)
				return false;
		}
		else if (!maxAge.equals(other.maxAge))
			return false;
		if (minAge == null)
		{
			if (other.minAge != null)
				return false;
		}
		else if (!minAge.equals(other.minAge))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bandName == null) ? 0 : bandName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((maxAge == null) ? 0 : maxAge.hashCode());
		result = prime * result + ((minAge == null) ? 0 : minAge.hashCode());
		return result;
	}
}
