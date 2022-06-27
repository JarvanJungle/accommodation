package com.torkirion.eroam.microservice.activities.apidomain;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString

public class ActivityCancellationPolicyLine implements Comparable<ActivityCancellationPolicyLine>
{
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@ApiModelProperty(notes = "The date this policy line takes effect (before=false) or finishes (before=true)")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
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
	public int compareTo(ActivityCancellationPolicyLine o)
	{
		return asOf.compareTo(o.getAsOf());
	}
 }
