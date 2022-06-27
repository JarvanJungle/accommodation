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
public class TransportBookRS extends TransportRS implements Serializable
{
	@JsonIgnore
	private List<ResponseExtraInformation> errors = new ArrayList<>();

	@JsonIgnore
	private List<ResponseExtraInformation> warnings = new ArrayList<>();

	@ApiModelProperty(notes = "A reference from the calling system for the booking", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "The booking reference from the called system for this booking", required = true)
	private String bookingReference;

	@ApiModelProperty(notes = "If a PNR was generated for this booking", required = false)
	private String pnr;

	@ApiModelProperty(notes = "Any remarks for this booking returned by the supplier", required = true)
	private List<String> remarks = new ArrayList<>();

	@ApiModelProperty(notes = "Any remarks for this booking returned by the supplier, detail by key value", required = true)
	private List<Map<String, String>> remarksDetail = new ArrayList<>();
}
