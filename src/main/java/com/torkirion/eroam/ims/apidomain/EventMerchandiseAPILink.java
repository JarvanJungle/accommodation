package com.torkirion.eroam.ims.apidomain;

import com.torkirion.eroam.ims.datadomain.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class EventMerchandiseAPILink
{
	public EventMerchandiseAPILink()
	{
		
	}
	
	public EventMerchandiseAPILink(EventMerchandiseLink link)
	{
		setId(link.getId());
		setEventSeriesId(link.getEventSeries().getId());
		setEventSeriesName(link.getEventSeries().getName());
		setMerchandiseId(link.getMerchandise().getId());
		setMerchandiseName(link.getMerchandise().getName());
		setMandatoryInclusion(link.getMandatoryInclusion());
	}
	
	@ApiModelProperty(notes = "The id of this link", required = false)
	private Integer id;

	@ApiModelProperty(notes = "The series Id", required = true)
	private Integer eventSeriesId;

	@ApiModelProperty(notes = "The name of the series", required = false)
	private String eventSeriesName;

	@ApiModelProperty(notes = "The merchandise Id", required = true)
	private Integer merchandiseId;

	@ApiModelProperty(notes = "The name of the merchandise", required = false)
	private String merchandiseName;

	@ApiModelProperty(notes = "If inclusion of this merchandise is mandatory with a ticket purchase", required = true)
	private Boolean mandatoryInclusion;

	@Override
	public String toString()
	{
		return "EventMerchandiseAPILink [eventSeriesId=" + eventSeriesId + ", eventSeriesName=" + eventSeriesName + ", merchandiseId=" + merchandiseId + ", merchandiseName=" + merchandiseName
				+ ", mandatoryInclusion=" + mandatoryInclusion + "]";
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
		EventMerchandiseAPILink other = (EventMerchandiseAPILink) obj;
		if (eventSeriesId == null)
		{
			if (other.eventSeriesId != null)
				return false;
		}
		else if (!eventSeriesId.equals(other.eventSeriesId))
			return false;
		if (merchandiseId == null)
		{
			if (other.merchandiseId != null)
				return false;
		}
		else if (!merchandiseId.equals(other.merchandiseId))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventSeriesId == null) ? 0 : eventSeriesId.hashCode());
		result = prime * result + ((merchandiseId == null) ? 0 : merchandiseId.hashCode());
		return result;
	}

}
