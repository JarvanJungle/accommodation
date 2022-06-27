package com.torkirion.eroam.microservice.events.apidomain;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EventResult implements Comparable<EventResult>
{
	private Venue venue;
	
	@ApiModelProperty(notes = "The unique code of this property within the channel from which it originated", example = "123456")
	private String channelId;

	@ApiModelProperty(notes = "The channel from which this property record originated", example = "IMS")
	private String channel;

	private String id;

	private String externalEventId;

	private String name;

	private String teamOrPerformer;

	private Integer seriesId;

	private String seriesName;

	private String type;

	private String supplierName;

	@JsonFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(example = "2021-08-10")
	private String startDate;

	@ApiModelProperty(example = "13:00")
	private String startTime;

	@ApiModelProperty(example = "2021-08-11")
	@JsonFormat(pattern="yyyy-MM-dd")
	private String endDate;

	private String overview;

	private String imageUrl;

	private String termsAndConditions;

	private String defaultSeatmapImageUrl;

	private Boolean seatMapNotAvailable = false;
	
	@Deprecated
	private String currency;

	@Deprecated
	private BigDecimal fromNettPrice;

	@Deprecated
	private BigDecimal fromRrpPrice;

	private CurrencyValue totalRetailPrice;

	private CurrencyValue totalNetPrice;

	private List<EventTicketAllotment> eventTicketAllotments;

	private List<EventMerchandiseSalesAPILink> eventMerchandiseAPILink = new ArrayList<>();

	@Override
	public int compareTo(EventResult other)
	{
		if ( this.getStartDate().equals(other.getStartDate()))
		{
			if ( this.getStartTime().equals(other.getStartTime()))
			{
				if ( this.getName().equals(other.getName()))
				{
					return this.getId().compareTo(other.getId());
				}
				else
					return this.getName().compareTo(other.getName());
			}
			else
				return this.getStartTime().compareTo(other.getStartTime());
		}
		else
			return this.getStartDate().compareTo(other.getStartDate());
	}
}
