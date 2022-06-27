package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "viatorv2_bookingquestion")
@Data
@ToString
public class BookingQuestionData implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Integer legacyBookingQuestionId;

	@Column(length = 40)
	private String questionId;
	
	@Column(length = 40)
	private String type;
	
	@Column(length = 30)
	private String bookingGroup;
	
	@Column(length = 300)
	private String label;
	
	@Column(length = 300)
	private String hint;
	
	@Column(length = 50)
	private String units;
	
	@Column(length = 500)
	private String allowedAnswers;
	
	@Column(length = 30)
	private Integer required;
	
	@Column
	private Integer maxLength;

}
