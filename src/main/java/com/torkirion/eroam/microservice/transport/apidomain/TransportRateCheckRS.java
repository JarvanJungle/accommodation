package com.torkirion.eroam.microservice.transport.apidomain;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseBookRS;
import com.torkirion.eroam.microservice.merchandise.apidomain.Booking.ItemStatus;
import com.torkirion.eroam.microservice.merchandise.apidomain.MerchandiseBookRS.ResponseItem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class TransportRateCheckRS extends TransportRS implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6084660253533258857L;

	private String packedData;

	@JsonIgnore
	private List<ResponseExtraInformation> errors = new ArrayList<>();

	@JsonIgnore
	private List<ResponseExtraInformation> warnings = new ArrayList<>();

	public TransportRateCheckRS() {

	}

	public TransportRateCheckRS(AvailTransportSearchRS searchRS) {
		this.setId(searchRS.getId());
		this.setType(searchRS.getType());
		this.setProvider(searchRS.getProvider());
		this.setItineraryPricingInfo(searchRS.getItineraryPricingInfo());
		this.setSegments(searchRS.getSegments());
		this.setCommonDatas(searchRS.getCommonDatas());
		this.setDuration(searchRS.getDuration());
		this.setDuration_time(searchRS.getDuration_time());
	}
}
