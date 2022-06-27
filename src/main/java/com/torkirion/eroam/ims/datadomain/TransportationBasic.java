package com.torkirion.eroam.ims.datadomain;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "imstransportationbasic")
@Data
public class TransportationBasic
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 3)
	private String currency;

	@Column(length = 3)
	private String rrpCurrency;

	@Column(length = 3)
	private String fromIata;

	@Column(length = 3)
	private String toIata;

	@Column(length = 10)
	private String flight;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column
	private LocalDate scheduleFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column
	private LocalDate scheduleTo;

	@Embedded
	private DaysOfTheWeek daysOfTheWeek;

	@Column(length = 3)
	private String searchIataFrom;

	@Column(length = 3)
	private String searchIataTo;

	@Column
	private Boolean requiresPassport;
	
	@Column
	private Boolean onRequest = false;
	
	@Column(length = 100)
	private String supplier;
	
	@Column(length = 1000)
	private String bookingConditions;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="transportation", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TransportationBasicClass> classes;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="transportation", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TransportationBasicSegment> segments;

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
		TransportationBasic other = (TransportationBasic) obj;
		if (currency == null)
		{
			if (other.currency != null)
				return false;
		}
		else if (!currency.equals(other.currency))
			return false;
		if (daysOfTheWeek == null)
		{
			if (other.daysOfTheWeek != null)
				return false;
		}
		else if (!daysOfTheWeek.equals(other.daysOfTheWeek))
			return false;
		if (flight == null)
		{
			if (other.flight != null)
				return false;
		}
		else if (!flight.equals(other.flight))
			return false;
		if (fromIata == null)
		{
			if (other.fromIata != null)
				return false;
		}
		else if (!fromIata.equals(other.fromIata))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (scheduleFrom == null)
		{
			if (other.scheduleFrom != null)
				return false;
		}
		else if (!scheduleFrom.equals(other.scheduleFrom))
			return false;
		if (scheduleTo == null)
		{
			if (other.scheduleTo != null)
				return false;
		}
		else if (!scheduleTo.equals(other.scheduleTo))
			return false;
		if (searchIataFrom == null)
		{
			if (other.searchIataFrom != null)
				return false;
		}
		else if (!searchIataFrom.equals(other.searchIataFrom))
			return false;
		if (searchIataTo == null)
		{
			if (other.searchIataTo != null)
				return false;
		}
		else if (!searchIataTo.equals(other.searchIataTo))
			return false;
		if (toIata == null)
		{
			if (other.toIata != null)
				return false;
		}
		else if (!toIata.equals(other.toIata))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((daysOfTheWeek == null) ? 0 : daysOfTheWeek.hashCode());
		result = prime * result + ((flight == null) ? 0 : flight.hashCode());
		result = prime * result + ((fromIata == null) ? 0 : fromIata.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((scheduleFrom == null) ? 0 : scheduleFrom.hashCode());
		result = prime * result + ((scheduleTo == null) ? 0 : scheduleTo.hashCode());
		result = prime * result + ((searchIataFrom == null) ? 0 : searchIataFrom.hashCode());
		result = prime * result + ((searchIataTo == null) ? 0 : searchIataTo.hashCode());
		result = prime * result + ((toIata == null) ? 0 : toIata.hashCode());
		return result;
	}
}
