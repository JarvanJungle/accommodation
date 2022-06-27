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
@Table(name = "imsactivitydeparturetime")
@Data
public class ActivityDepartureTime
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
    @JoinColumn(name="activity_id", nullable=false)
	private Activity activity;

	@Column
	private LocalTime departureTime;

	@Column(length = 200)
	private String name;

	@Column
	private LocalDateTime lastUpdated;

	@Override
	public String toString()
	{
		return "ActivityDepartureTime [id=" + id + ", departureTime=" + departureTime + ", name=" + name + "]";
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
		ActivityDepartureTime other = (ActivityDepartureTime) obj;
		if (departureTime == null)
		{
			if (other.departureTime != null)
				return false;
		}
		else if (!departureTime.equals(other.departureTime))
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
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((departureTime == null) ? 0 : departureTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
}
