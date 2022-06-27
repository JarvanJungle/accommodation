package com.torkirion.eroam.microservice.transport.datadomain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "icaoAircraft")
@Data
public class IcaoAircraft
{
	@Id
	private String aircraftType;

	private String aircraftGroup;

	private String serviceCode;

	private String fullName;

	private String specificGroup;

	private String aircraftBody;
}
