package com.torkirion.eroam.microservice.hirecars.apidomain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HireCarDetailResult
{
	private CarSearchEntryDetailed carSearchEntryDetailed = new CarSearchEntryDetailed();

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
}
