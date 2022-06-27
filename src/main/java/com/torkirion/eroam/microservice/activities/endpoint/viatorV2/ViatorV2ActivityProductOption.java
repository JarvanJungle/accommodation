package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.Serializable;
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
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.torkirion.eroam.ims.datadomain.Activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "viatorv2_option", indexes = { @Index(name = "viatorv2option_productCodeOptionCode", columnList = "productCode, productOptionCode", unique = true) })
@Data
public class ViatorV2ActivityProductOption
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
    @JoinColumn(name="activity_id", nullable=false)
	private ViatorV2Activity activity;

	@Column(length = 20)
	private String productCode;

	@Column(length = 20)
	private String productOptionCode;

	@Column(length = 1000)
	private String description;

	@Column(length = 100)
	private String languageType;

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViatorV2ActivityProductOption other = (ViatorV2ActivityProductOption) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (productCode == null)
		{
			if (other.productCode != null)
				return false;
		}
		else if (!productCode.equals(other.productCode))
			return false;
		if (productOptionCode == null)
		{
			if (other.productOptionCode != null)
				return false;
		}
		else if (!productOptionCode.equals(other.productOptionCode))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = prime * result + ((productOptionCode == null) ? 0 : productOptionCode.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "ViatorV2ActivityProductOption [id=" + id + ", productCode=" + productCode + ", productOptionCode=" + productOptionCode + ", description=" + description + "]";
	}
}
