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
public class DaysOfTheWeek
{
	private Boolean sunday = false;
	private Boolean monday = false;
	private Boolean tuesday = false;
	private Boolean wednesday = false;
	private Boolean thursday = false;
	private Boolean friday = false;
	private Boolean saturday = false;
}
