package com.torkirion.eroam.microservice.activities.endpoint.viatorV2;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsInterface;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCData;
import com.torkirion.eroam.microservice.activities.datadomain.ActivityRCRepo;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.*;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.AvailabilitySchedule;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.BookableItem;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.PriceList;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.PricingDetail;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.PricingRecord;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.Season;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.AvailabilitySchedules.TimedEntry;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.BookingQuestions.BookingQuestion;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.Destinations.Destination;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.AdditionalInfo;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.Image;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.ImageVariant;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.InclusionExclusion;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.Product;
import com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data.ProductRC.ProductOption;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "viatorv2_load_progress")
@Data
@ToString
public class ViatorV2LoadProgress implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 50)
	private String loadtype;

	@Column(length = 50)
	private String cursor;

	@Column
	private LocalDateTime lastDateTime;
}