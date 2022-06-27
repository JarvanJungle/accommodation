package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "imseventallotment")
@Data
public class EventAllotment
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
    @JoinColumn(name="event_id", nullable=false)
	private Event event;

	@Column(length = 200)
	private String name;

	@Column
	private Integer allotment;

	@Column
	private Integer minimumSale;

	@Column
	private Integer maximumSale;

	@Column(length = 50)
	private String multiplePattern;

	@Column
	private Boolean onRequest;

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
		EventAllotment other = (EventAllotment) obj;
		if (allotment == null)
		{
			if (other.allotment != null)
				return false;
		}
		else if (!allotment.equals(other.allotment))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (maximumSale == null)
		{
			if (other.maximumSale != null)
				return false;
		}
		else if (!maximumSale.equals(other.maximumSale))
			return false;
		if (minimumSale == null)
		{
			if (other.minimumSale != null)
				return false;
		}
		else if (!minimumSale.equals(other.minimumSale))
			return false;
		if (multiplePattern == null)
		{
			if (other.multiplePattern != null)
				return false;
		}
		else if (!multiplePattern.equals(other.multiplePattern))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (onRequest == null)
		{
			if (other.onRequest != null)
				return false;
		}
		else if (!onRequest.equals(other.onRequest))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allotment == null) ? 0 : allotment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((maximumSale == null) ? 0 : maximumSale.hashCode());
		result = prime * result + ((minimumSale == null) ? 0 : minimumSale.hashCode());
		result = prime * result + ((multiplePattern == null) ? 0 : multiplePattern.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((onRequest == null) ? 0 : onRequest.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "EventAllotment [id=" + id + ", name=" + name + ", allotment=" + allotment + ", minimumSale=" + minimumSale + ", maximumSale=" + maximumSale + ", multiplePattern=" + multiplePattern
				+ ", onRequest=" + onRequest + "]";
	}
}
