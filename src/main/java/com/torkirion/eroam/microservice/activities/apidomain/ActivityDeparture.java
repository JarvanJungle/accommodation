package com.torkirion.eroam.microservice.activities.apidomain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ActivityDeparture implements Comparable<ActivityDeparture>
{
	private String departureId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate date;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	@JsonDeserialize(using = LocalTimeDeserializer.class)
	@JsonSerialize(using = LocalTimeSerializer.class)
	private LocalTime departureTime;

	private String departureName;

	private SortedSet<ActivityOption> options = new TreeSet<>();

	@Override
	public int compareTo(ActivityDeparture o)
	{
		if ( date.equals(o.date))
			if ( departureTime.equals(o.departureTime))
				return departureName.compareTo(o.departureName);
			else
				return departureTime.compareTo(o.departureTime);
		else
			return date.compareTo(o.date);
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
		ActivityDeparture other = (ActivityDeparture) obj;
		if (date == null)
		{
			if (other.date != null)
				return false;
		}
		else if (!date.equals(other.date))
			return false;
		if (departureName == null)
		{
			if (other.departureName != null)
				return false;
		}
		else if (!departureName.equals(other.departureName))
			return false;
		if (departureTime == null)
		{
			if (other.departureTime != null)
				return false;
		}
		else if (!departureTime.equals(other.departureTime))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((departureName == null) ? 0 : departureName.hashCode());
		result = prime * result + ((departureTime == null) ? 0 : departureTime.hashCode());
		return result;
	}
}
