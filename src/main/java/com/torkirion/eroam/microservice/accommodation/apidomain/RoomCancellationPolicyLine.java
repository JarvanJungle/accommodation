package com.torkirion.eroam.microservice.accommodation.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString

public class RoomCancellationPolicyLine implements Comparable<RoomCancellationPolicyLine>, Serializable
{
	@ApiModelProperty(notes = "The date this policy line takes effect (before=false) or finishes (before=true)")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate asOf;
    
	@ApiModelProperty(notes = "Description of this penalty line")
    private String penaltyDescription;
    
	@ApiModelProperty(notes = "Value of the penalty as a dollar figure.  A full value = no refund. EITHER penalty OR penaltyPercent will be given.")
    private CurrencyValue penalty;
    
	@ApiModelProperty(notes = "Value of the penalty as a percntage.  A full value = no refund. EITHER penalty OR penaltyPercent will be given.")
    private BigDecimal penaltyPercent;
    
	@ApiModelProperty(notes = "Whether this line applies to BEFORE 'asOf' or after (including) 'asOf'")
    private Boolean before = Boolean.FALSE;

	@Override
	public int compareTo(RoomCancellationPolicyLine o)
	{
		return asOf.compareTo(o.getAsOf());
	}
 }
