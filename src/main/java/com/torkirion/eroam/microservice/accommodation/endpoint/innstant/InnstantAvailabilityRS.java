package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.accommodation.endpoint.innstant.dto.ResultDTO;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InnstantAvailabilityRS
{
	@JsonProperty("results")
	private List<ResultDTO> results;

	@JsonProperty("sessionId")
	private String sessionId;

	@JsonProperty("status")
	private String status;

	@JsonProperty("timestamp")
	private Long timestamp;

	@JsonProperty("requestTime")
	private String requestTime;

	@JsonProperty("processTime")
	private Long processTime;

	@JsonProperty("completed")
	private Long completed;

	private Error error;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Error {
		private String code;
		private String message;
	}

}
