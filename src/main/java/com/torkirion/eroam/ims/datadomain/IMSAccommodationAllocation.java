package com.torkirion.eroam.ims.datadomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.BeforeCheckinAfterBooking;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.PenaltyType;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "imsaccommodationallocation", indexes = { @Index(name = "imsaccommodationallocation_hotelid", columnList = "hotelId", unique = false) })
@IdClass(IMSAccommodationAllocation.AllocationKey.class)
@Data
public class IMSAccommodationAllocation
{
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class AllocationKey implements Serializable
	{
		private static final long serialVersionUID = -7469436304195200493L;

		@Column(length = 20)
		private String hotelId;

		@Column
		private Integer allocationId;

		@Column
		private LocalDate allocationDate;
	}

	@Id
	@Column(length = 20)
	private String hotelId;

	@Id
	@Column
	private Integer allocationId;

	@Id
	@Column
	private LocalDate allocationDate;

	private Integer allocation;
}
