package com.torkirion.eroam.ims.datadomain;


import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
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
@Table(name = "imseventseries")
@Data
public class EventSeries
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 20)
	private String externalSeriesId;

	@Column(length = 200)
	private String name;

	@Column(length = 3)
	private String defaultCurrency;

	@ManyToOne
    @JoinColumn(name="eventtype_id", nullable=false)
	private EventType eventType;

	@OneToMany(fetch = FetchType.LAZY, mappedBy="eventSeries")
	private Set<Event> events;

	@Column(length = 100)
	private String countries;

	@Column(length = 100)
	private String marketingCountries;

	@Column(length = 100)
	private String excludedMarketingCountries;

	@Column(columnDefinition = "TEXT")
	private String overview;

	@Column
	private Boolean active = true;

	@Column(length = 255)
	private String imageUrl;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="eventSeries", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EventMerchandiseLink> eventMerchandiseLinks;

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
		EventSeries other = (EventSeries) obj;
		if (countries == null)
		{
			if (other.countries != null)
				return false;
		}
		else if (!countries.equals(other.countries))
			return false;
		if (defaultCurrency == null)
		{
			if (other.defaultCurrency != null)
				return false;
		}
		else if (!defaultCurrency.equals(other.defaultCurrency))
			return false;
		if (eventType == null)
		{
			if (other.eventType != null)
				return false;
		}
		else if (!eventType.equals(other.eventType))
			return false;
		if (externalSeriesId == null)
		{
			if (other.externalSeriesId != null)
				return false;
		}
		else if (!externalSeriesId.equals(other.externalSeriesId))
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
		if (marketingCountries == null)
		{
			if (other.marketingCountries != null)
				return false;
		}
		else if (!marketingCountries.equals(other.marketingCountries))
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
		result = prime * result + ((countries == null) ? 0 : countries.hashCode());
		result = prime * result + ((defaultCurrency == null) ? 0 : defaultCurrency.hashCode());
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + ((externalSeriesId == null) ? 0 : externalSeriesId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((marketingCountries == null) ? 0 : marketingCountries.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((overview == null) ? 0 : overview.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "EventSeries [id=" + id + ", externalSeriesId=" + externalSeriesId + ", name=" + name + ", defaultCurrency=" + defaultCurrency + ", eventType=" + eventType + ", countries=" + countries
				+ ", marketingCountries=" + marketingCountries + ", overview=" + overview + ", active=" + active + ", imageUrl=" + imageUrl + ", eventMerchandiseLinks=" + eventMerchandiseLinks
				+ ", lastUpdated=" + lastUpdated + "]";
	}
}
