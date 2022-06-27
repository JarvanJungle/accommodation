package com.torkirion.eroam.ims.datadomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = "locations")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "imscountry")
public class IMSCountry implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 2)
	private String countryID;
	
	@Column(length = 200)
	private String countryName;
	
	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<IMSLocation> locations = new ArrayList<>();

	@Override
	public String toString()
	{
		return "Country [countryID=" + countryID + ", countryName=" + countryName + "]";
	}

}
