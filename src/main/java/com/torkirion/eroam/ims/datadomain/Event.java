package com.torkirion.eroam.ims.datadomain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
@Table(name = "imsevent")
@Data
public class Event
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 20)
	private String externalEventId;

	@Column(length = 200)
	private String name;

	@Column(length = 200)
	private String teamOrPerformer;

	@ManyToOne
    @JoinColumn(name="eventseries_id", nullable=false)
	private EventSeries eventSeries;

	@ManyToOne
    @JoinColumn(name="eventvenue_id", nullable=false)
	private EventVenue eventVenue;

	@ManyToOne
    @JoinColumn(name="eventsupplier_id", nullable=false)
	private EventSupplier eventSupplier;

	//@Column(length = 20)
	//private String associatedExternalMerchandiseId;

	@Column
	private LocalDate startDate;

	@Column
	private LocalTime startTime;

	@Column
	private LocalDate endDate;

	@Column(columnDefinition = "TEXT")
	private String overview;

	@Column(length = 255)
	private String imageUrl;

	@Column(columnDefinition = "TEXT")
	private String termsAndConditions;

	@Column(length = 255)
	private String defaultSeatmapImageUrl;

	@Column
	private Boolean seatMapNotAvailable = false;
	
	@Column(length = 100)
	private String operator;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="event", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EventAllotment> allotments;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="event", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EventClassification> classifications;

	@OneToMany(fetch = FetchType.LAZY, mappedBy="event", cascade = CascadeType.DETACH, orphanRemoval = false)
	private Set<EventSale> sales;

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
		Event other = (Event) obj;
		if (defaultSeatmapImageUrl == null)
		{
			if (other.defaultSeatmapImageUrl != null)
				return false;
		}
		else if (!defaultSeatmapImageUrl.equals(other.defaultSeatmapImageUrl))
			return false;
		if (endDate == null)
		{
			if (other.endDate != null)
				return false;
		}
		else if (!endDate.equals(other.endDate))
			return false;
		if (externalEventId == null)
		{
			if (other.externalEventId != null)
				return false;
		}
		else if (!externalEventId.equals(other.externalEventId))
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
		if (seatMapNotAvailable == null)
		{
			if (other.seatMapNotAvailable != null)
				return false;
		}
		else if (!seatMapNotAvailable.equals(other.seatMapNotAvailable))
			return false;
		if (startDate == null)
		{
			if (other.startDate != null)
				return false;
		}
		else if (!startDate.equals(other.startDate))
			return false;
		if (startTime == null)
		{
			if (other.startTime != null)
				return false;
		}
		else if (!startTime.equals(other.startTime))
			return false;
		if (teamOrPerformer == null)
		{
			if (other.teamOrPerformer != null)
				return false;
		}
		else if (!teamOrPerformer.equals(other.teamOrPerformer))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultSeatmapImageUrl == null) ? 0 : defaultSeatmapImageUrl.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((externalEventId == null) ? 0 : externalEventId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((overview == null) ? 0 : overview.hashCode());
		result = prime * result + ((seatMapNotAvailable == null) ? 0 : seatMapNotAvailable.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((teamOrPerformer == null) ? 0 : teamOrPerformer.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "Event [id=" + id + ", externalEventId=" + externalEventId + ", name=" + name + ", teamOrPerformer=" + teamOrPerformer + ", eventSeries=" + eventSeries + ", eventVenue=" + eventVenue
				+ ", eventSupplier=" + eventSupplier + ", startDate=" + startDate + ", startTime=" + startTime + ", endDate="
				+ endDate + ", overview=" + overview + ", imageUrl=" + imageUrl + ", termsAndConditions=" + termsAndConditions + ", defaultSeatmapImageUrl=" + defaultSeatmapImageUrl
				+ ", seatMapNotAvailable=" + seatMapNotAvailable + "]";
	}
}
