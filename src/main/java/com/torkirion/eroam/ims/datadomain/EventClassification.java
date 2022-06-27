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
@Table(name = "imseventclassification")
@Data
public class EventClassification
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

	@Column(length = 3)
	private String currency;

	@Column(length = 3)
	private String rrpCurrency;

	@Column
	private BigDecimal nettPrice;

	@Column
	private BigDecimal rrpPrice;

	@Column(length = 1000)
	private String ticketingDescription;

	@Column
	private Integer allotmentId;

	@Column
	private String days;

	@Column
	private Boolean bundlesOnly = false;

	@Column
	private Integer allowInfantIfUnder;

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
		EventClassification other = (EventClassification) obj;
		if (allotmentId == null)
		{
			if (other.allotmentId != null)
				return false;
		}
		else if (!allotmentId.equals(other.allotmentId))
			return false;
		if (bundlesOnly == null)
		{
			if (other.bundlesOnly != null)
				return false;
		}
		else if (!bundlesOnly.equals(other.bundlesOnly))
			return false;
		if (currency == null)
		{
			if (other.currency != null)
				return false;
		}
		else if (!currency.equals(other.currency))
			return false;
		if (days == null)
		{
			if (other.days != null)
				return false;
		}
		else if (!days.equals(other.days))
			return false;
		if (event == null)
		{
			if (other.event != null)
				return false;
		}
		else if (!event.equals(other.event))
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
		if (ticketingDescription == null)
		{
			if (other.ticketingDescription != null)
				return false;
		}
		else if (!ticketingDescription.equals(other.ticketingDescription))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allotmentId == null) ? 0 : allotmentId.hashCode());
		result = prime * result + ((bundlesOnly == null) ? 0 : bundlesOnly.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((days == null) ? 0 : days.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nettPrice == null) ? 0 : nettPrice.hashCode());
		result = prime * result + ((rrpPrice == null) ? 0 : rrpPrice.hashCode());
		result = prime * result + ((ticketingDescription == null) ? 0 : ticketingDescription.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "EventClassification [id=" + id + ", name=" + name + ", currency=" + currency + ", nettPrice=" + nettPrice + ", rrpPrice=" + rrpPrice + ", ticketingDescription=" + ticketingDescription
				+ ", allotmentId=" + allotmentId + ", days=" + days + ", bundlesOnly=" + bundlesOnly + ", allowInfantIfUnder=" + allowInfantIfUnder + "]";
	}
}
