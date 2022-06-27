package com.torkirion.eroam.ims.datadomain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "imsmerchandise")
@Data
public class Merchandise
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 20, unique = true)
	private String externalMerchandiseId;

	@Column(length = 200)
	private String name;

	@ManyToOne
    @JoinColumn(name="merchandisesupplier_id", nullable=false)
	private MerchandiseSupplier merchandiseSupplier;

	@ManyToOne
    @JoinColumn(name="merchandisecategory_id", nullable=false)
	private MerchandiseCategory merchandiseCategory;

	@Column(columnDefinition = "TEXT")
	private String overview;

	@Column
	private Boolean active;

	@Column(columnDefinition = "TEXT")
	private String imagesJson;

	@Column(columnDefinition = "TEXT")
	private String brandsJson;

	@Column(columnDefinition = "TEXT")
	private String termsAndConditions;

	@Column
	private Boolean bundlesOnly = false;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="merchandise", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<MerchandiseOption> options;

	@OneToMany(fetch = FetchType.LAZY, mappedBy="merchandise", cascade = CascadeType.DETACH, orphanRemoval = false)
	private Set<MerchandiseSale> sales;

	@OneToMany(fetch = FetchType.EAGER, mappedBy="merchandise", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EventMerchandiseLink> eventMerchandiseLinks;

	@Column
	private LocalDateTime lastUpdated;

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Merchandise other = (Merchandise) obj;
		if (externalMerchandiseId == null)
		{
			if (other.externalMerchandiseId != null)
				return false;
		}
		else if (!externalMerchandiseId.equals(other.externalMerchandiseId))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (imagesJson == null)
		{
			if (other.imagesJson != null)
				return false;
		}
		else if (!imagesJson.equals(other.imagesJson))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (overview == null)
		{
			if (other.overview != null)
				return false;
		}
		else if (!overview.equals(other.overview))
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externalMerchandiseId == null) ? 0 : externalMerchandiseId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imagesJson == null) ? 0 : imagesJson.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((overview == null) ? 0 : overview.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "Merchandise [id=" + id + ", externalMerchandiseId=" + externalMerchandiseId + ", name=" + name + ", overview=" + overview + ", imagesJson=" + imagesJson + ", brandsJson=" + brandsJson
				+ ", termsAndConditions=" + termsAndConditions + "]";
	}
}
