package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
@Table(name = "imsmerchandiseoption")
@Data
public class MerchandiseOption
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
    @JoinColumn(name="merchandise_id", nullable=false)
	private Merchandise merchandise;

	@Column(length = 200)
	private String name;

	@Column(length = 3)
	private String currency;

	@Column(length = 3)
	private String rrpCurrency;

	@Column
	private BigDecimal nettPrice;

	@Column
	private BigDecimal rrpPrice;

	// Allotment of -1 means FREESALE
	@Column
	private Integer allotment;

	@Column
	private LocalDateTime lastUpdated;

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MerchandiseOption other = (MerchandiseOption) obj;
		if (allotment == null)
		{
			if (other.allotment != null)
				return false;
		}
		else if (!allotment.equals(other.allotment))
			return false;
		if (currency == null)
		{
			if (other.currency != null)
				return false;
		}
		else if (!currency.equals(other.currency))
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
		if (nettPrice == null)
		{
			if (other.nettPrice != null)
				return false;
		}
		else if (!nettPrice.equals(other.nettPrice))
			return false;
		if (rrpPrice == null)
		{
			if (other.rrpPrice != null)
				return false;
		}
		else if (!rrpPrice.equals(other.rrpPrice))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allotment == null) ? 0 : allotment.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nettPrice == null) ? 0 : nettPrice.hashCode());
		result = prime * result + ((rrpPrice == null) ? 0 : rrpPrice.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "MerchandiseOption [id=" + id + ", name=" + name + ", currency=" + currency + ", nettPrice=" + nettPrice + ", rrpPrice=" + rrpPrice + ", allotment=" + allotment + "]";
	}
}
