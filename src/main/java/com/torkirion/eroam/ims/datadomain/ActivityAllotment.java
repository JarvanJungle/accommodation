package com.torkirion.eroam.ims.datadomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imsactivityallotment")
@IdClass(ActivityAllotment.AllotmentKey.class)
@Data
public class ActivityAllotment
{
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class AllotmentKey implements Serializable
	{
		private static final long serialVersionUID = -7469436304195200493L;

		@Column
		private Integer activityId;

		@Column
		private Integer optionId;

		@Column
		private Integer departureTimeId;

		@Column
		private LocalDate allotmentDate;
	}

	@Id
	@Column
	private Integer activityId;

	@Id
	@Column
	private Integer optionId;

	@Id
	@Column
	private Integer departureTimeId;

	@Id
	@Column
	private LocalDate allotmentDate;
	
	@Column
	private Integer allotment;
}



