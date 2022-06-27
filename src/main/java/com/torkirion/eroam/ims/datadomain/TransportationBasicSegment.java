package com.torkirion.eroam.ims.datadomain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Table(name = "imstransportationbasicsegment")
@Data
public class TransportationBasicSegment
{
	public static class TransportationBasicSegmentComparator implements Comparator<TransportationBasicSegment>
	{

		@Override
		public int compare(TransportationBasicSegment o1, TransportationBasicSegment o2)
		{
			return o1.getSegmentNumber().compareTo(o2.getSegmentNumber());
		}
		
	}
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "transport_id", nullable = false)
	private TransportationBasic transportation;

	@Column
	private Integer segmentNumber;

	@Column(length = 3)
	private String departureAirportLocationCode;

	@Column(length = 100)
	private String departureTerminal;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	@Column
	private LocalTime departureTime;

	@Column(length = 3)
	private String arrivalAirportLocationCode;

	@Column(length = 100)
	private String arrivalTerminal;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	@Column
	private LocalTime arrivalTime;

	@Column
	private Integer arrivalDayExtra;

	@Column
	private Boolean passportRequired;

	@Column
	private Integer flightDurationMinutes;

	@Column(length = 3)
	private String marketingAirlineCode;

	@Column(length = 6)
	private String marketingAirlineFlightNumber;

	@Column(length = 3)
	private String operatingAirlineCode;

	@Column(length = 6)
	private String operatingAirlineFlightNumber;

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
		TransportationBasicSegment other = (TransportationBasicSegment) obj;
		return Objects.equals(arrivalAirportLocationCode, other.arrivalAirportLocationCode) && Objects.equals(arrivalDayExtra, other.arrivalDayExtra)
				&& Objects.equals(arrivalTerminal, other.arrivalTerminal) && Objects.equals(arrivalTime, other.arrivalTime)
				&& Objects.equals(departureAirportLocationCode, other.departureAirportLocationCode) && Objects.equals(departureTerminal, other.departureTerminal)
				&& Objects.equals(departureTime, other.departureTime) && Objects.equals(flightDurationMinutes, other.flightDurationMinutes) && Objects.equals(id, other.id)
				&& Objects.equals(marketingAirlineCode, other.marketingAirlineCode) && Objects.equals(marketingAirlineFlightNumber, other.marketingAirlineFlightNumber)
				&& Objects.equals(operatingAirlineCode, other.operatingAirlineCode) && Objects.equals(operatingAirlineFlightNumber, other.operatingAirlineFlightNumber)
				&& Objects.equals(passportRequired, other.passportRequired) && Objects.equals(segmentNumber, other.segmentNumber);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(arrivalAirportLocationCode, arrivalDayExtra, arrivalTerminal, arrivalTime, departureAirportLocationCode, departureTerminal, departureTime, flightDurationMinutes, id,
				marketingAirlineCode, marketingAirlineFlightNumber, operatingAirlineCode, operatingAirlineFlightNumber, passportRequired, segmentNumber);
	}

	@Override
	public String toString()
	{
		return "TransportationBasicSegment [id=" + id + ", segmentNumber=" + segmentNumber + ", departureAirportLocationCode=" + departureAirportLocationCode + ", departureTerminal="
				+ departureTerminal + ", departureTime=" + departureTime + ", arrivalAirportLocationCode=" + arrivalAirportLocationCode + ", arrivalTerminal=" + arrivalTerminal + ", arrivalTime="
				+ arrivalTime + ", arrivalDayExtra=" + arrivalDayExtra + ", passportRequired=" + passportRequired + ", flightDurationMinutes=" + flightDurationMinutes + ", marketingAirlineCode="
				+ marketingAirlineCode + ", marketingAirlineFlightNumber=" + marketingAirlineFlightNumber + ", operatingAirlineCode=" + operatingAirlineCode + ", operatingAirlineFlightNumber="
				+ operatingAirlineFlightNumber + ", lastUpdated=" + lastUpdated + "]";
	}
}