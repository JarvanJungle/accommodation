package com.torkirion.eroam.microservice.hirecars.apidomain;


import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HireCarResult
{
	private List<SIPPBlock> sippBlocks;

	@JsonIgnore
	private LocationResults enteredLocations;

	@JsonIgnore
	private LocationResults dropoffLocations;

	@JsonIgnore
	private LocationResults includedLocations;

	@JsonIgnore
	private LocationResults suggestedLocations;

	@JsonIgnore
	private LocationResults includedDropoffLocations;

	@JsonIgnore
	private LocationResults suggestedDropoffLocations;

	private List<Error> errors;
}
