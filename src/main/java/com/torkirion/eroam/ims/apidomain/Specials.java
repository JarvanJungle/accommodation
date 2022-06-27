package com.torkirion.eroam.ims.apidomain;

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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.BeforeCheckinAfterBooking;
import com.torkirion.eroam.ims.apidomain.CancellationPolicies.PenaltyType;
import com.torkirion.eroam.ims.apidomain.Seasons.Season;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class Specials
{
	@Data
	public static class Special
	{
		private Long id;

		private Integer specialId;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate checkinFrom;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate checkinTo;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate bookFrom;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private LocalDate bookTo;

		private Integer daysInAdvanceMore;

		private Integer daysInAdvanceLess;

		private Integer minimumStay;

		private Integer rateIdx;

		private List<Integer> rateIds;

		private String description;

		// only one of the following should be non-zero

		private BigDecimal adjustPercentage;

		private BigDecimal adjustValue;

		private Integer freeNights;

	}

	@ApiModelProperty(notes = "The unique code of this property within the service", example = "YL123456")
	private String hotelId;

	private List<Special> specials = new ArrayList<>();
}
