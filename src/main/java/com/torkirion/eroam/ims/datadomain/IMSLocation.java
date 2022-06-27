package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "imslocation")
public class IMSLocation
{
	@Id
	@Column
	private Integer id;

	@Column(length = 100)
	private String locationName;

	@Embedded
	private GeoCoordinates geoCoordinates;

	private BigDecimal radius;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private IMSCountry country;

}
