package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class Roomtypes
{
	@Data
	public static class Roomtype
	{
		private Integer roomtypeId;

		private String description;

		private String roomSize;

		private String beddingDescription;

		private Integer maximumAdults = 0;

		private Integer maximumPeople = 0;

		private Boolean simpleAllocation = true;
		
		@ApiModelProperty(notes = "if simpleAllocation is true, rates must be provided")
		private Rates rates;
	}

	@ApiModelProperty(notes = "The unique code of this property within the service", example = "YL123456")
	private String hotelId;

	private List<Roomtype> roomtypes = new ArrayList<>();
}
