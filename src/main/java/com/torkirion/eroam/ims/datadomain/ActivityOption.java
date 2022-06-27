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
@Table(name = "imsactivityoption")
@Data
public class ActivityOption
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
    @JoinColumn(name="activity_id", nullable=false)
	private Activity activity;

	@Column(length = 200)
	private String externalCode;

	@Column(length = 200)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String priceBlocksJson;

	@Column
	private Boolean bundlesOnly = false;

	@Column
	private LocalDateTime lastUpdated;

	@Override
	public String toString()
	{
		return "ActivityOption [id=" + id + ", externalCode=" + externalCode + ", name=" + name + ", priceBlocksJson=" + priceBlocksJson + "]";
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
		ActivityOption other = (ActivityOption) obj;
		if (externalCode == null)
		{
			if (other.externalCode != null)
				return false;
		}
		else if (!externalCode.equals(other.externalCode))
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
		if (priceBlocksJson == null)
		{
			if (other.priceBlocksJson != null)
				return false;
		}
		else if (!priceBlocksJson.equals(other.priceBlocksJson))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externalCode == null) ? 0 : externalCode.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((priceBlocksJson == null) ? 0 : priceBlocksJson.hashCode());
		return result;
	}
}
