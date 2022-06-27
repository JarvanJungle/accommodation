package com.torkirion.eroam.microservice.events.dto;

import java.io.Serializable;
import java.util.Objects;

import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AvailSearchByGeocordBoxRQDTO extends AvailSearchRQDTO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private LatitudeLongitude northwest;

	private LatitudeLongitude southeast;

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AvailSearchByGeocordBoxRQDTO other = (AvailSearchByGeocordBoxRQDTO) obj;
		return Objects.equals(northwest, other.northwest) && Objects.equals(southeast, other.southeast);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(northwest, southeast);
		return result;
	}

}
