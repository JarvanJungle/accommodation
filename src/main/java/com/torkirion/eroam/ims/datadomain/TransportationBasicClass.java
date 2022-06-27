package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "imstransportationbasicclass")
@Data
public class TransportationBasicClass
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
    @JoinColumn(name="transport_id", nullable=false)
	private TransportationBasic transportation;

	@Column(length = 10)
	private String reference;

	@Column(length = 1)
	private String classCode;

	@Column(length = 100)
	private String classDescription;

	@Column
	private Integer baggageMaxWeight;

	@Column
	private Integer baggageMaxPieces;

	@Column
	private Boolean refundable;

	@Column
	private BigDecimal adultNett;

	@Column
	private BigDecimal adultRrp;

	@Column
	private BigDecimal childNett;

	@Column
	private BigDecimal childRrp;

	@Column
	private LocalDateTime lastUpdated;
	
	@Override
	public String toString()
	{
		return "TransportationBasicClass [id=" + id + ", reference=" + reference + ", classCode=" + classCode + ", classDescription=" + classDescription + ", baggageMaxWeight=" + baggageMaxWeight
				+ ", baggageMaxPieces=" + baggageMaxPieces + ", refundable=" + refundable + ", adultNett=" + adultNett + ", adultRrp=" + adultRrp + ", childNett=" + childNett + ", childRrp="
				+ childRrp + "]";
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
		TransportationBasicClass other = (TransportationBasicClass) obj;
		if (adultNett == null)
		{
			if (other.adultNett != null)
				return false;
		}
		else if (!adultNett.equals(other.adultNett))
			return false;
		if (adultRrp == null)
		{
			if (other.adultRrp != null)
				return false;
		}
		else if (!adultRrp.equals(other.adultRrp))
			return false;
		if (baggageMaxPieces == null)
		{
			if (other.baggageMaxPieces != null)
				return false;
		}
		else if (!baggageMaxPieces.equals(other.baggageMaxPieces))
			return false;
		if (baggageMaxWeight == null)
		{
			if (other.baggageMaxWeight != null)
				return false;
		}
		else if (!baggageMaxWeight.equals(other.baggageMaxWeight))
			return false;
		if (childNett == null)
		{
			if (other.childNett != null)
				return false;
		}
		else if (!childNett.equals(other.childNett))
			return false;
		if (childRrp == null)
		{
			if (other.childRrp != null)
				return false;
		}
		else if (!childRrp.equals(other.childRrp))
			return false;
		if (classCode == null)
		{
			if (other.classCode != null)
				return false;
		}
		else if (!classCode.equals(other.classCode))
			return false;
		if (classDescription == null)
		{
			if (other.classDescription != null)
				return false;
		}
		else if (!classDescription.equals(other.classDescription))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (reference == null)
		{
			if (other.reference != null)
				return false;
		}
		else if (!reference.equals(other.reference))
			return false;
		if (refundable == null)
		{
			if (other.refundable != null)
				return false;
		}
		else if (!refundable.equals(other.refundable))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adultNett == null) ? 0 : adultNett.hashCode());
		result = prime * result + ((adultRrp == null) ? 0 : adultRrp.hashCode());
		result = prime * result + ((baggageMaxPieces == null) ? 0 : baggageMaxPieces.hashCode());
		result = prime * result + ((baggageMaxWeight == null) ? 0 : baggageMaxWeight.hashCode());
		result = prime * result + ((childNett == null) ? 0 : childNett.hashCode());
		result = prime * result + ((childRrp == null) ? 0 : childRrp.hashCode());
		result = prime * result + ((classCode == null) ? 0 : classCode.hashCode());
		result = prime * result + ((classDescription == null) ? 0 : classDescription.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		result = prime * result + ((refundable == null) ? 0 : refundable.hashCode());
		return result;
	}
}