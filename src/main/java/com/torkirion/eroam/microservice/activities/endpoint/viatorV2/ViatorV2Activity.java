package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "viatorv2_activity", indexes = { @Index(name = "viatorv2activity_productCode", columnList = "productCode", unique = true) })
@Data
public class ViatorV2Activity
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 20)
	private String productCode;

	@Column(length = 1000)
	private String bookingQuestions;

	@Column
	private Integer infantMinAge = -1;

	@Column
	private Integer infantMaxAge = -1;

	@Column
	private Integer infantMinPax = -1;

	@Column
	private Integer infantMaxPax = -1;

	@Column
	private Integer childMinAge = -1;

	@Column
	private Integer childMaxAge = -1;

	@Column
	private Integer childMinPax = -1;

	@Column
	private Integer childMaxPax = -1;

	@Column
	private Integer youthMinAge = -1;

	@Column
	private Integer youthMaxAge = -1;

	@Column
	private Integer youthMinPax = -1;

	@Column
	private Integer youthMaxPax = -1;

	@Column
	private Integer adultMinAge = -1;

	@Column
	private Integer adultMaxAge = -1;

	@Column
	private Integer adultMinPax = -1;

	@Column
	private Integer adultMaxPax = -1;

	@Column
	private Integer seniorMinAge = -1;

	@Column
	private Integer seniorMaxAge = -1;

	@Column
	private Integer seniorMinPax = -1;

	@Column
	private Integer seniorMaxPax = -1;

	@Embedded
	private com.torkirion.eroam.ims.datadomain.GeoCoordinates geoCoordinates;

	// @OneToMany(fetch = FetchType.EAGER, mappedBy="activity", cascade = CascadeType.ALL)
	// private Set<ViatorV2ActivityProductOption> allotments = new HashSet<>();

	// @OneToMany(fetch = FetchType.EAGER, mappedBy="activity", cascade = CascadeType.ALL)
	// private Set<ViatorV2ActivityDepartureTime> departureTimes = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy="activity", cascade = CascadeType.ALL)
	private Set<ViatorV2ActivityProductOption> options = new HashSet<>();

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViatorV2Activity other = (ViatorV2Activity) obj;
		if (geoCoordinates == null)
		{
			if (other.geoCoordinates != null)
				return false;
		}
		else if (!geoCoordinates.equals(other.geoCoordinates))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (productCode == null)
		{
			if (other.productCode != null)
				return false;
		}
		else if (!productCode.equals(other.productCode))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geoCoordinates == null) ? 0 : geoCoordinates.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "ViatorV2Activity [id=" + id + ", productCode=" + productCode + ", infantMinAge=" + infantMinAge + ", infantMaxAge=" + infantMaxAge + ", infantMinPax=" + infantMinPax
				+ ", infantMaxPax=" + infantMaxPax + ", childMinAge=" + childMinAge + ", childMaxAge=" + childMaxAge + ", childMinPax=" + childMinPax + ", childMaxPax=" + childMaxPax
				+ ", youthMinAge=" + youthMinAge + ", youthMaxAge=" + youthMaxAge + ", youthMinPax=" + youthMinPax + ", youthMaxPax=" + youthMaxPax + ", adultMinAge=" + adultMinAge + ", adultMaxAge="
				+ adultMaxAge + ", adultMinPax=" + adultMinPax + ", adultMaxPax=" + adultMaxPax + ", seniorMinAge=" + seniorMinAge + ", seniorMaxAge=" + seniorMaxAge + ", seniorMinPax=" + seniorMinPax
				+ ", seniorMaxPax=" + seniorMaxPax + ", geoCoordinates=" + geoCoordinates + "]";
	}
}
