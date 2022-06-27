package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Embeddable
public class DaysOfTheWeek
{
	@Column
	private Boolean sunday;

	@Column
	private Boolean monday;

	@Column
	private Boolean tuesday;

	@Column
	private Boolean wednesday;

	@Column
	private Boolean thursday;

	@Column
	private Boolean friday;

	@Column
	private Boolean saturday;
}