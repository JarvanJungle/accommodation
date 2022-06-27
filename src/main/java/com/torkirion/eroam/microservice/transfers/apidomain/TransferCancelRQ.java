package com.torkirion.eroam.microservice.transfers.apidomain;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.AbstractRQ;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.Traveller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class TransferCancelRQ extends AbstractRQ implements Serializable
{
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "The source channel of this item", example = "JAYRIDE", required = true)
	private String channel;
	
	@ApiModelProperty(notes = "A reference from the calling system for this item", required = true)
	private String internalBookingReference;

	@ApiModelProperty(notes = "The booking reference from the called system for this booking", required = true)
	private String bookingReference;
}
