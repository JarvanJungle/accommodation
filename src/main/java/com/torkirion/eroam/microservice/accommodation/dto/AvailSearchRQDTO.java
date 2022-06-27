package com.torkirion.eroam.microservice.accommodation.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class AvailSearchRQDTO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String countryCodeOfOrigin;
	
	private LocalDate checkin;

	private LocalDate checkout;

	private List<TravellerMix> travellers;

	private String nameFilter;

	private List<BigDecimal> starsFilter;

	private String client;

	private String channel;

	private List<String> channelExceptions;

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AvailSearchRQDTO other = (AvailSearchRQDTO) obj;
		if (channel == null)
		{
			if (other.channel != null)
				return false;
		}
		else if (!channel.equals(other.channel))
			return false;
		if (checkin == null)
		{
			if (other.checkin != null)
				return false;
		}
		else if (!checkin.equals(other.checkin))
			return false;
		if (checkout == null)
		{
			if (other.checkout != null)
				return false;
		}
		else if (!checkout.equals(other.checkout))
			return false;
		if (client == null)
		{
			if (other.client != null)
				return false;
		}
		else if (!client.equals(other.client))
			return false;
		if (countryCodeOfOrigin == null)
		{
			if (other.countryCodeOfOrigin != null)
				return false;
		}
		else if (!countryCodeOfOrigin.equals(other.countryCodeOfOrigin))
			return false;
		if (nameFilter == null)
		{
			if (other.nameFilter != null)
				return false;
		}
		else if (!nameFilter.equals(other.nameFilter))
			return false;
		if (starsFilter == null)
		{
			if (other.starsFilter != null)
				return false;
		}
		else if (!starsFilter.equals(other.starsFilter))
			return false;
		if (travellers == null)
		{
			if (other.travellers != null)
				return false;
		}
		else if (!travellers.equals(other.travellers))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + ((checkin == null) ? 0 : checkin.hashCode());
		result = prime * result + ((checkout == null) ? 0 : checkout.hashCode());
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((countryCodeOfOrigin == null) ? 0 : countryCodeOfOrigin.hashCode());
		result = prime * result + ((nameFilter == null) ? 0 : nameFilter.hashCode());
		result = prime * result + ((starsFilter == null) ? 0 : starsFilter.hashCode());
		result = prime * result + ((travellers == null) ? 0 : travellers.hashCode());
		return result;
	}
}
