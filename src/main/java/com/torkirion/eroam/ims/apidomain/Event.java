package com.torkirion.eroam.ims.apidomain;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Event
{
	public static class EventSorterByDate implements Comparator<Event>
	{
		@Override
		public int compare(Event o1, Event o2)
		{
			if ( o1.startDate == null || o2.startDate == null || o1.startDate.equals(o2.startDate))
			{
				return o1.id - o2.id;
			}
			return o1.startDate.compareTo(o2.startDate);
		}
	}
	private Integer id;

	private String externalEventId;

	private String name;

	private String teamOrPerformer;

	private Integer seriesId;

	@ApiModelProperty(notes = "Only for when reading", required = false)
	private EventSeries series;

	private Integer venueId;

	@ApiModelProperty(notes = "Only for when reading", required = false)
	private EventVenue venue;

	private Integer supplierId;

	private String associatedExternalMerchandiseId;

	@ApiModelProperty(notes = "Only for when reading", required = false)
	private EventSupplier supplier;

	@JsonFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(required = true, example = "2021-08-10")
	private String startDate;

	@ApiModelProperty(required = false, example = "13:00")
	private String startTime;

	@ApiModelProperty(required = false, example = "2021-08-11")
	@JsonFormat(pattern="yyyy-MM-dd")
	private String endDate;

	private String overview;

	private String imageUrl;

	private String termsAndConditions;

	@ApiModelProperty(required = false)
	private String defaultSeatmapImageUrl;

	@ApiModelProperty(required = false)
	private Boolean seatMapNotAvailable = false;
	
	private String operator;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
}
