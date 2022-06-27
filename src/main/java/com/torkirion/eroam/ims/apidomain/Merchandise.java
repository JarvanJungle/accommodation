package com.torkirion.eroam.ims.apidomain;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Merchandise
{
	public static class MerchandiseSorterByExternalMerchandiseId implements Comparator<Merchandise>
	{
		@Override
		public int compare(Merchandise o1, Merchandise o2)
		{
			if ( o1.externalMerchandiseId == null || o2.externalMerchandiseId == null || o1.externalMerchandiseId.equals(o2.externalMerchandiseId))
			{
				return o1.id - o2.id;
			}
			return o1.externalMerchandiseId.compareTo(o2.externalMerchandiseId);
		}
	}
	private Integer id;

	private String externalMerchandiseId;

	private String name;

	private Integer supplierId;

	private MerchandiseSupplier supplier;

	private String merchandiseCategory;

	private String overview;

	private Boolean active;

	private List<String> images;

	//private List<String> brands;

	private String termsAndConditions;
	
	private Boolean bundlesOnly;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime lastUpdated;
	
	//private Set<EventMerchandiseAPILink> eventMerchandiseLinks;
}
