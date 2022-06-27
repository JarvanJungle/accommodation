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
public class Boards
{
	@Data
	public static class Board
	{
		private String boardCode;

		private String boardDescription;
	}

	@ApiModelProperty(notes = "The unique code of this property within the service", example = "YL123456")
	private String hotelId;

	private List<Board> boards = new ArrayList<>();
}
