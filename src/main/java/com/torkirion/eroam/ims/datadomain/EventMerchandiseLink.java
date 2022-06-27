package com.torkirion.eroam.ims.datadomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "imseventmerchandiselink")
@Data
public class EventMerchandiseLink
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
    @JoinColumn(name="eventseries_id", nullable=false)
	private EventSeries eventSeries;

	@ManyToOne
    @JoinColumn(name="merchandise_id", nullable=false)
	private Merchandise merchandise;

	@Column
	private Boolean mandatoryInclusion;

	@Override
	public String toString()
	{
		return "EventMerchandiseLink [id=" + id + ", eventSeries=" + eventSeries.getId() + ", merchandise=" + merchandise.getId() + ", mandatoryInclusion=" + mandatoryInclusion + "]";
	}
}
