package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.PaxDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InnstantAvailabilityRQ
{
	@Data
	public static class Destination
	{
		private Integer id;
		private String type;
	}

	@Data
	public static class Client
	{
		private String ip;
		private String userAgent;
	}

	@Data
	public static class Dates
	{
		private String from;
		private String to;
	}

	private Client client;

	private List<String> currencies;

	private String customerCountry;

	private List<String> customFields = new ArrayList<>();

	private Dates dates;

	private List<Destination> destinations;

	private List<String> filters = new ArrayList<>();

	private List<PaxDTO> pax;

	private Integer timeout = 12;

	private String service = "hotels";

}
