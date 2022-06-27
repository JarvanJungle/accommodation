package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "imseventvenue")
@Data
public class EventVenue
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 20)
	private String externalVenueId;

	@Column(length = 200)
	private String name;

	@Embedded
	private Address address;

	@Column(columnDefinition = "TEXT")
	private String overview;

	@Column(length = 255)
	private String imageUrl;

	@Column(length = 255)
	private String defaultSeatmapImageUrl;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="eventVenue")
	private Set<Event> events;

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
		EventVenue other = (EventVenue) obj;
		if (address == null)
		{
			if (other.address != null)
				return false;
		}
		else if (!address.equals(other.address))
			return false;
		if (defaultSeatmapImageUrl == null)
		{
			if (other.defaultSeatmapImageUrl != null)
				return false;
		}
		else if (!defaultSeatmapImageUrl.equals(other.defaultSeatmapImageUrl))
			return false;
		if (externalVenueId == null)
		{
			if (other.externalVenueId != null)
				return false;
		}
		else if (!externalVenueId.equals(other.externalVenueId))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (imageUrl == null)
		{
			if (other.imageUrl != null)
				return false;
		}
		else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (overview == null)
		{
			if (other.overview != null)
				return false;
		}
		else if (!overview.equals(other.overview))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((defaultSeatmapImageUrl == null) ? 0 : defaultSeatmapImageUrl.hashCode());
		result = prime * result + ((externalVenueId == null) ? 0 : externalVenueId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((overview == null) ? 0 : overview.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "EventVenue [id=" + id + ", externalVenueId=" + externalVenueId + ", name=" + name + ", address=" + address + ", overview=" + overview + ", imageUrl=" + imageUrl
				+ ", defaultSeatmapImageUrl=" + defaultSeatmapImageUrl + "]";
	}
}
